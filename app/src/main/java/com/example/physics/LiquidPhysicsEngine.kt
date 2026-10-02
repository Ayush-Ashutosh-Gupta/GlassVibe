package com.example.physics

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.utils.HapticHelper
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.sqrt

data class LiquidPhysicsState(
    val tiltAngleDeg: Float = 0f,
    val pitchAngleDeg: Float = 0f,
    val isUpsideDown: Boolean = false,
    val waveSloshAmplitude: Float = 7.5f,
    val sloshImpulse: Float = 0f,
    val gravityMagnitude: Float = 9.8f
)

/**
 * High-frequency hardware low-pass filter with noise-gating deadband.
 * Filters raw sensor readings before they reach the simulation engine,
 * eliminating micro-hand tremors and accelerometer noise spikes.
 */
class HardwareSensorLowPassFilter(
    private val alpha: Float = 0.16f,
    private val deadbandThreshold: Float = 0.035f
) {
    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 9.8f
    private var initialized = false

    fun filter(rawX: Float, rawY: Float, rawZ: Float): Triple<Float, Float, Float> {
        if (!initialized) {
            filteredX = rawX
            filteredY = rawY
            filteredZ = rawZ
            initialized = true
            return Triple(filteredX, filteredY, filteredZ)
        }

        val deltaX = rawX - filteredX
        val deltaY = rawY - filteredY
        val deltaZ = rawZ - filteredZ

        // Deadband filter: ignore sensor noise micro-oscillations below threshold
        val effectiveDeltaX = if (abs(deltaX) > deadbandThreshold) deltaX else 0f
        val effectiveDeltaY = if (abs(deltaY) > deadbandThreshold) deltaY else 0f
        val effectiveDeltaZ = if (abs(deltaZ) > deadbandThreshold) deltaZ else 0f

        filteredX += effectiveDeltaX * alpha
        filteredY += effectiveDeltaY * alpha
        filteredZ += effectiveDeltaZ * alpha

        return Triple(filteredX, filteredY, filteredZ)
    }

    fun reset() {
        initialized = false
    }
}

/**
 * Motion-sensing physics engine tracker using the device's accelerometer AND gyroscope.
 * Employs complementary filter sensor fusion: combines accelerometer gravitational projection
 * with gyroscope angular velocity integration for instantaneous, buttery-smooth, jitter-free
 * liquid motion.
 * Automatically triggers haptic feedback via VibrationManager upon significant physical motion.
 */
class LiquidSensorTracker(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val lowPassFilter = HardwareSensorLowPassFilter()

    @Volatile
    var rollDeg = 0f
        private set

    @Volatile
    var pitchDeg = 0f
        private set

    @Volatile
    var isUpsideDown = false
        private set

    @Volatile
    var impulse = 0f
        private set

    @Volatile
    var gravityMagnitude = 9.8f
        private set

    private var prevAx = 0f
    private var prevAy = 0f
    private var lastGyroTimestampNanos = 0L
    private var fusedRoll = 0f
    private var fusedPitch = 0f
    private var wasUpsideDown = false
    private var isTracking = false

    fun start() {
        if (isTracking) return
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        isTracking = true
    }

    fun stop() {
        if (!isTracking) return
        sensorManager?.unregisterListener(this)
        lowPassFilter.reset()
        lastGyroTimestampNanos = 0L
        isTracking = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // 1. Hardware low-pass filter with deadband
                val (ax, ay, az) = lowPassFilter.filter(event.values[0], event.values[1], event.values[2])
                gravityMagnitude = sqrt(ax * ax + ay * ay + az * az)

                // 2. Linear jerk detection
                val jerk = sqrt((ax - prevAx) * (ax - prevAx) + (ay - prevAy) * (ay - prevAy))
                prevAx = ax
                prevAy = ay

                if (jerk > 0.42f) {
                    impulse = (impulse * 0.80f + jerk * 1.9f).coerceIn(0f, 25f)
                    // Significant linear acceleration shake -> Trigger VibrationManager haptics
                    if (jerk > 2.8f) {
                        HapticHelper.onSignificantMotion(context, intensity = (jerk / 6f).coerceIn(0.4f, 1f))
                    }
                } else {
                    impulse *= 0.92f
                }

                // 3. Euler angles from gravity
                val accelRollRad = atan2(-ax.toDouble(), sqrt((ay * ay + az * az).toDouble()).coerceAtLeast(0.1))
                val accelRollDeg = Math.toDegrees(accelRollRad).toFloat().coerceIn(-40f, 40f)

                val accelPitchRad = atan2(ay.toDouble(), az.toDouble().coerceAtLeast(0.1))
                val accelPitchDeg = Math.toDegrees(accelPitchRad).toFloat().coerceIn(-40f, 40f)

                // If gyroscope is missing, fallback directly to low-pass accelerometer angles
                if (gyroscope == null) {
                    rollDeg = accelRollDeg
                    pitchDeg = accelPitchDeg
                } else {
                    // Complementary filter fusion correction with low drift
                    fusedRoll = 0.94f * fusedRoll + 0.06f * accelRollDeg
                    fusedPitch = 0.94f * fusedPitch + 0.06f * accelPitchDeg
                    rollDeg = fusedRoll.coerceIn(-40f, 40f)
                    pitchDeg = fusedPitch.coerceIn(-40f, 40f)
                }

                val nowUpsideDown = ay < -3.2f
                if (nowUpsideDown != wasUpsideDown) {
                    wasUpsideDown = nowUpsideDown
                    // 180° Inversion flip -> VibrationManager tactile notch
                    HapticHelper.onSignificantMotion(context, intensity = 0.9f)
                }
                isUpsideDown = nowUpsideDown
            }

            Sensor.TYPE_GYROSCOPE -> {
                // Angular velocity in rad/s: [0]=x, [1]=y, [2]=z
                val gyroY = event.values[1] // Roll rotational velocity
                val gyroX = event.values[0] // Pitch rotational velocity
                val gyroZ = event.values[2]

                if (lastGyroTimestampNanos != 0L) {
                    val dt = ((event.timestamp - lastGyroTimestampNanos) * 1.0e-9f).coerceIn(0.001f, 0.040f)
                    val gyroDeltaRoll = Math.toDegrees((gyroY * dt).toDouble()).toFloat()
                    val gyroDeltaPitch = Math.toDegrees((gyroX * dt).toDouble()).toFloat()

                    // Complementary filter: fast gyro integration with accelerometer anchor
                    fusedRoll = (fusedRoll + gyroDeltaRoll).coerceIn(-40f, 40f)
                    fusedPitch = (fusedPitch + gyroDeltaPitch).coerceIn(-40f, 40f)
                    rollDeg = fusedRoll
                    pitchDeg = fusedPitch

                    // Rotational kinetic energy
                    val rotSpeed = sqrt(gyroX * gyroX + gyroY * gyroY + gyroZ * gyroZ)
                    if (rotSpeed > 2.2f) {
                        impulse = (impulse * 0.85f + rotSpeed * 3.5f).coerceIn(0f, 25f)
                        // Fast tilt rotation -> Trigger VibrationManager haptics
                        HapticHelper.onSignificantMotion(context, intensity = (rotSpeed / 5f).coerceIn(0.3f, 1f))
                    }
                }
                lastGyroTimestampNanos = event.timestamp
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/**
 * Decoupled fixed-timestep physics simulator using spring-damper equations.
 * Decouples liquid flow calculation from the frame rate to ensure smooth movement
 * regardless of device refresh rate (60Hz/90Hz/120Hz) or composition load.
 * Uses viscous fluid damping ratio zeta = 0.85 for naturally decelerating fluid motion.
 */
class LiquidPhysicsSimulator {
    private var simulatedTilt = 0f
    private var tiltVelocity = 0f
    private var activeImpulse = 0f
    private var lastUpdateNanos = 0L

    // Decoupled fixed-timestep constants (60Hz fixed simulation physics step)
    private val fixedDeltaTime = 0.016667f
    private var accumulator = 0f

    // Fluid spring-damper parameters
    private val omega0 = 7.2f // Natural angular frequency (rad/s)
    private val zeta = 0.85f  // Viscous damping ratio (critically damped feel)
    private val impulseDecayRate = 2.4f // Exponential fluid dissipation

    fun step(targetTilt: Float, externalImpulse: Float, currentNanos: Long): Float {
        if (lastUpdateNanos == 0L) {
            lastUpdateNanos = currentNanos
            simulatedTilt = targetTilt
            return simulatedTilt
        }

        val elapsedSeconds = ((currentNanos - lastUpdateNanos) / 1_000_000_000f).coerceIn(0.001f, 0.060f)
        lastUpdateNanos = currentNanos
        accumulator += elapsedSeconds

        if (externalImpulse > 0.3f) {
            activeImpulse = (activeImpulse + externalImpulse * 0.5f).coerceAtMost(22f)
        }

        // Step physics in fixed time slices
        while (accumulator >= fixedDeltaTime) {
            val displacement = targetTilt - simulatedTilt
            // Spring acceleration: F_spring = -k * x, F_damper = -c * v
            val springAcceleration = displacement * (omega0 * omega0) - 2f * zeta * omega0 * tiltVelocity
            tiltVelocity += springAcceleration * fixedDeltaTime
            simulatedTilt += tiltVelocity * fixedDeltaTime

            // Exponential fluid dissipation
            activeImpulse = (activeImpulse * exp(-impulseDecayRate * fixedDeltaTime)).coerceAtLeast(0f)
            accumulator -= fixedDeltaTime
        }

        // Sub-step interpolation alpha
        val alpha = (accumulator / fixedDeltaTime).coerceIn(0f, 1f)
        return simulatedTilt + tiltVelocity * fixedDeltaTime * alpha
    }

    fun getEffectiveSloshImpulse(): Float = activeImpulse
}

/**
 * Composable helper supplying smoothed, spring-damped liquid physics state
 * with double-stage filtering and frame-rate decoupled integration.
 */
@Composable
fun rememberLiquidPhysics(
    useSensor: Boolean = true,
    manualTiltDeg: Float = 0f,
    manualFillLevel: Float = 0.65f
): State<LiquidPhysicsState> {
    val context = LocalContext.current
    val physicsState = remember { mutableStateOf(LiquidPhysicsState()) }

    DisposableEffect(useSensor, manualTiltDeg) {
        if (useSensor) {
            val tracker = LiquidSensorTracker(context)
            val simulator = LiquidPhysicsSimulator()
            tracker.start()

            // Choreographer VSYNC ticker with spring-damper interpolation
            val choreographer = android.view.Choreographer.getInstance()
            val frameCallback = object : android.view.Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    val smoothedTilt = simulator.step(
                        targetTilt = tracker.rollDeg,
                        externalImpulse = tracker.impulse,
                        currentNanos = frameTimeNanos
                    )
                    val effectiveImpulse = simulator.getEffectiveSloshImpulse()

                    physicsState.value = LiquidPhysicsState(
                        tiltAngleDeg = smoothedTilt,
                        pitchAngleDeg = tracker.pitchDeg,
                        isUpsideDown = tracker.isUpsideDown,
                        waveSloshAmplitude = (6.5f + effectiveImpulse * 0.75f).coerceIn(4f, 22f),
                        sloshImpulse = effectiveImpulse,
                        gravityMagnitude = tracker.gravityMagnitude
                    )
                    choreographer.postFrameCallback(this)
                }
            }
            choreographer.postFrameCallback(frameCallback)

            onDispose {
                choreographer.removeFrameCallback(frameCallback)
                tracker.stop()
            }
        } else {
            val isUpsideDown = manualTiltDeg < -70f || manualTiltDeg > 70f
            physicsState.value = LiquidPhysicsState(
                tiltAngleDeg = manualTiltDeg.coerceIn(-40f, 40f),
                pitchAngleDeg = 0f,
                isUpsideDown = isUpsideDown,
                waveSloshAmplitude = 7.5f,
                sloshImpulse = 0f
            )

            onDispose {}
        }
    }

    return physicsState
}
