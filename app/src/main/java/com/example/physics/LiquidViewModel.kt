package com.example.physics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * ViewModel managing liquid physics simulation, hardware sensor tracking,
 * Exponential Moving Average (EMA) sensor data filtering, and decoupled fixed-timestep
 * spring-damper fluid integration.
 */
class LiquidViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorTracker = LiquidSensorTracker(application.applicationContext)
    private val physicsSimulator = LiquidPhysicsSimulator()

    // Exponential Moving Average (EMA) filter on incoming sensor data
    // Alpha = 0.15 provides smooth input transitions without sluggishness
    private var emaRoll = 0f
    private var emaPitch = 0f
    private var emaImpulse = 0f
    private val emaAlpha = 0.15f
    private val emaImpulseAlpha = 0.25f

    private val _physicsState = MutableStateFlow(LiquidPhysicsState())
    val physicsState: StateFlow<LiquidPhysicsState> = _physicsState.asStateFlow()

    private var simulationJob: Job? = null
    private var useSensor = true
    private var manualTilt = 0f
    private var manualFill = 0.65f

    init {
        startSimulation()
    }

    fun setSensorPhysicsEnabled(enabled: Boolean) {
        useSensor = enabled
        if (!enabled) {
            sensorTracker.stop()
        } else {
            sensorTracker.start()
        }
    }

    fun setManualTilt(angleDeg: Float) {
        manualTilt = angleDeg
    }

    fun setManualFill(level: Float) {
        manualFill = level.coerceIn(0.08f, 0.95f)
    }

    fun injectSloshImpulse(magnitude: Float) {
        emaImpulse = (emaImpulse + magnitude).coerceAtMost(25f)
    }

    private fun startSimulation() {
        sensorTracker.start()
        simulationJob?.cancel()

        // High-frequency decoupled physics loop running at fixed intervals
        simulationJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val nowNanos = System.nanoTime()

                val rawTargetTilt: Float
                val targetPitch: Float
                val isUpsideDown: Boolean
                val rawImpulse: Float

                if (useSensor) {
                    // Exponential Moving Average (EMA) filter on incoming sensor stream
                    emaRoll += (sensorTracker.rollDeg - emaRoll) * emaAlpha
                    emaPitch += (sensorTracker.pitchDeg - emaPitch) * emaAlpha
                    emaImpulse += (sensorTracker.impulse - emaImpulse) * emaImpulseAlpha

                    rawTargetTilt = emaRoll
                    targetPitch = emaPitch
                    isUpsideDown = sensorTracker.isUpsideDown
                    rawImpulse = emaImpulse
                } else {
                    rawTargetTilt = manualTilt.coerceIn(-40f, 40f)
                    targetPitch = 0f
                    isUpsideDown = manualTilt < -70f || manualTilt > 70f
                    rawImpulse = 0f
                }

                // Decoupled fixed-timestep physics simulation with spring-damper equations
                val smoothedTilt = physicsSimulator.step(
                    targetTilt = rawTargetTilt,
                    externalImpulse = rawImpulse,
                    currentNanos = nowNanos
                )
                val effectiveImpulse = physicsSimulator.getEffectiveSloshImpulse()

                val currentState = _physicsState.value
                val tiltDelta = abs(smoothedTilt - currentState.tiltAngleDeg)
                val pitchDelta = abs(targetPitch - currentState.pitchAngleDeg)
                val impulseDelta = abs(effectiveImpulse - currentState.sloshImpulse)
                val upsideDownChanged = isUpsideDown != currentState.isUpsideDown

                // Only emit when noticeable motion occurs (or to keep alive at steady cadence), preventing Compose frame saturation
                if (tiltDelta > 0.18f || pitchDelta > 0.22f || impulseDelta > 0.08f || upsideDownChanged) {
                    _physicsState.value = LiquidPhysicsState(
                        tiltAngleDeg = smoothedTilt,
                        pitchAngleDeg = targetPitch,
                        isUpsideDown = isUpsideDown,
                        waveSloshAmplitude = (8.5f + effectiveImpulse * 0.95f).coerceIn(6f, 28f),
                        sloshImpulse = effectiveImpulse,
                        gravityMagnitude = sensorTracker.gravityMagnitude
                    )
                }

                // Smooth 40-50Hz cadence during active motion; eases load on Compose UI thread
                delay(22L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
        sensorTracker.stop()
    }
}
