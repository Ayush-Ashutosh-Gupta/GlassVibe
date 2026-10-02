package com.example.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.exp

/**
 * High-efficiency, jitter-free background ticker for home screen widgets.
 * Decoupled from the main UI thread to eliminate lag and frame drops.
 * Employs active-widget presence detection: only performs bitmap rendering
 * for widgets that are actually pinned to the user's home screen.
 */
object GlassWidgetFlowTicker {

    private var tickerJob: Job? = null
    // Dispatchers.Default ensures bitmap rendering NEVER blocks the main thread
    private val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
    private var isSloshing = false

    /**
     * Starts periodic flow updates for active home screen liquid widgets with continuous wave progression.
     */
    fun startFlowTicker(context: Context) {
        if (tickerJob?.isActive == true) return

        val appContext = context.applicationContext
        tickerJob = backgroundScope.launch {
            while (isActive) {
                try {
                    val appWidgetManager = AppWidgetManager.getInstance(appContext)
                    if (appWidgetManager != null) {
                        // Check if any liquid widgets are actually pinned before rendering
                        val batteryComponent = ComponentName(appContext, GlassLiquidBatteryWidgetProvider::class.java)
                        val screenTimeComponent = ComponentName(appContext, GlassScreenTimeWidgetProvider::class.java)

                        val batteryIds = appWidgetManager.getAppWidgetIds(batteryComponent)
                        val screenTimeIds = appWidgetManager.getAppWidgetIds(screenTimeComponent)

                        val hasLiquidWidgets = (batteryIds != null && batteryIds.isNotEmpty()) ||
                                               (screenTimeIds != null && screenTimeIds.isNotEmpty())

                        if (hasLiquidWidgets) {
                            val nowMs = System.currentTimeMillis()
                            // Smooth continuous wave phase calculation
                            val continuousWavePhase = ((nowMs % 60000L) / 1000f * 2.2f)

                            // 1. Liquid Battery Widget
                            if (batteryIds != null && batteryIds.isNotEmpty()) {
                                for (id in batteryIds) {
                                    GlassLiquidBatteryWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, continuousWavePhase)
                                }
                            }

                            // 2. Liquid Screen Time Beaker Widget
                            if (screenTimeIds != null && screenTimeIds.isNotEmpty()) {
                                for (id in screenTimeIds) {
                                    GlassScreenTimeWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, continuousWavePhase)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Smooth 1.0-second tick cadence keeps wave progression fluid without binder congestion
                delay(1000L)
            }
        }
    }

    fun stopFlowTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    /**
     * Triggers a naturally decelerating 8-frame slosh burst animation on active home screen widgets
     * using an exponential damping factor on a background thread.
     */
    fun triggerSloshBurst(context: Context) {
        if (isSloshing) return
        isSloshing = true
        val appContext = context.applicationContext

        backgroundScope.launch {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(appContext) ?: return@launch
                val batteryComponent = ComponentName(appContext, GlassLiquidBatteryWidgetProvider::class.java)
                val screenTimeComponent = ComponentName(appContext, GlassScreenTimeWidgetProvider::class.java)

                val batteryIds = appWidgetManager.getAppWidgetIds(batteryComponent) ?: IntArray(0)
                val screenTimeIds = appWidgetManager.getAppWidgetIds(screenTimeComponent) ?: IntArray(0)

                if (batteryIds.isEmpty() && screenTimeIds.isEmpty()) return@launch

                val startTime = System.currentTimeMillis()
                for (frame in 0..7) {
                    // Exponentially decaying slosh amplitude
                    val damping = exp(-frame * 0.32f).toFloat()
                    val burstPhase = ((startTime + frame * 90) % 60000L) / 1000f * (3.4f + damping * 2.2f)

                    for (id in batteryIds) {
                        GlassLiquidBatteryWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, burstPhase)
                    }
                    for (id in screenTimeIds) {
                        GlassScreenTimeWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, burstPhase)
                    }
                    delay(90L)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSloshing = false
            }
        }
    }

    /**
     * Updates all installed widget providers off the main thread.
     */
    fun updateAllWidgets(context: Context, wavePhase: Float? = null) {
        val appContext = context.applicationContext
        backgroundScope.launch {
            val appWidgetManager = AppWidgetManager.getInstance(appContext) ?: return@launch

            // 1. Liquid Battery Widget
            updateProvider(appContext, appWidgetManager, GlassLiquidBatteryWidgetProvider::class.java) { id ->
                GlassLiquidBatteryWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, wavePhase)
            }

            // 2. Liquid Screen Time Beaker Widget
            updateProvider(appContext, appWidgetManager, GlassScreenTimeWidgetProvider::class.java) { id ->
                GlassScreenTimeWidgetProvider.updateAppWidget(appContext, appWidgetManager, id, wavePhase)
            }

            // 3. Clock Widget
            updateProvider(appContext, appWidgetManager, GlassClockWidgetProvider::class.java) { id ->
                GlassClockWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }

            // 4. System Hardware Widget
            updateProvider(appContext, appWidgetManager, GlassSystemStatsWidgetProvider::class.java) { id ->
                GlassSystemStatsWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }

            // 5. Calendar Widget
            updateProvider(appContext, appWidgetManager, GlassCalendarWidgetProvider::class.java) { id ->
                GlassCalendarWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }

            // 6. Quick Launcher Widget
            updateProvider(appContext, appWidgetManager, GlassQuickLauncherWidgetProvider::class.java) { id ->
                GlassQuickLauncherWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }

            // 7. Weather Widget
            updateProvider(appContext, appWidgetManager, GlassWeatherWidgetProvider::class.java) { id ->
                GlassWeatherWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }

            // 8. Music Widget
            updateProvider(appContext, appWidgetManager, GlassMusicWidgetProvider::class.java) { id ->
                GlassMusicWidgetProvider.updateAppWidget(appContext, appWidgetManager, id)
            }
        }
    }

    private fun updateProvider(
        context: Context,
        appWidgetManager: AppWidgetManager,
        providerClass: Class<*>,
        updateAction: (Int) -> Unit
    ) {
        try {
            val component = ComponentName(context, providerClass)
            val ids = appWidgetManager.getAppWidgetIds(component)
            if (ids != null && ids.isNotEmpty()) {
                for (id in ids) {
                    updateAction(id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
