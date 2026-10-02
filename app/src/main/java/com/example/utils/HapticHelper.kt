package com.example.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * HapticHelper providing precision haptic feedback responses using the Android VibrationManager
 * (on Android S / API 31+) and Vibrator, with rate-limiting to prevent stuttering.
 */
object HapticHelper {
    private var vibrator: Vibrator? = null
    private var lastHapticTimestampMs = 0L

    private fun getVibrator(context: Context): Vibrator? {
        if (vibrator == null) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        }
        return vibrator
    }

    /**
     * Triggered whenever the liquid widget reacts to significant accelerometer or gyroscope input changes
     * (e.g. sharp phone tilt, shock impulse, or 180° inversion flip).
     */
    fun onSignificantMotion(context: Context, intensity: Float = 1.0f) {
        val now = System.currentTimeMillis()
        // Rate limit haptic feedback so it doesn't overwhelm the system or buzz continuously
        if (now - lastHapticTimestampMs < 180L) return
        lastHapticTimestampMs = now

        try {
            val v = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android VibrationManager rich primitive feedback
                val composition = VibrationEffect.startComposition()
                val scale = intensity.coerceIn(0.2f, 1.0f)
                composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, scale)
                v.vibrate(composition.compose())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(25, (160 * intensity.coerceIn(0.2f, 1f)).toInt()))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(25)
            }
        } catch (_: Exception) {
            // Ignore if vibration fails on unsupported devices/emulators
        }
    }

    fun tick(context: Context) {
        try {
            val v = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(10)
            }
        } catch (_: Exception) {
            // Ignore
        }
    }

    fun click(context: Context) {
        try {
            val v = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(20)
            }
        } catch (_: Exception) {
            // Ignore
        }
    }

    fun splash(context: Context) {
        try {
            val v = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val composition = VibrationEffect.startComposition()
                composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.8f)
                composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.5f, 30)
                v.vibrate(composition.compose())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 15, 20, 30), intArrayOf(0, 80, 0, 160), -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(40)
            }
        } catch (_: Exception) {
            // Ignore
        }
    }
}
