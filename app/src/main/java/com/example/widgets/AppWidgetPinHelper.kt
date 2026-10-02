package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.MainActivity
import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem

object AppWidgetPinHelper {

    fun getProviderClassForCategory(category: WidgetCategory): Class<*> {
        return when (category) {
            WidgetCategory.BATTERY,
            WidgetCategory.LIQUID -> GlassLiquidBatteryWidgetProvider::class.java

            WidgetCategory.CLOCKS,
            WidgetCategory.ASTRONOMY -> GlassClockWidgetProvider::class.java

            WidgetCategory.WEATHER,
            WidgetCategory.SENSORS -> GlassWeatherWidgetProvider::class.java

            WidgetCategory.SCREEN_TIME,
            WidgetCategory.POMODORO,
            WidgetCategory.HABITS -> GlassScreenTimeWidgetProvider::class.java

            WidgetCategory.SYSTEM,
            WidgetCategory.NETWORK -> GlassSystemStatsWidgetProvider::class.java

            WidgetCategory.CALENDAR,
            WidgetCategory.NOTES,
            WidgetCategory.QUOTES,
            WidgetCategory.FITNESS,
            WidgetCategory.CRYPTO -> GlassCalendarWidgetProvider::class.java

            WidgetCategory.MUSIC,
            WidgetCategory.AMBIENT -> GlassMusicWidgetProvider::class.java

            WidgetCategory.LAUNCHERS,
            WidgetCategory.DYNAMIC_ISLAND,
            WidgetCategory.MINI_GAMES,
            WidgetCategory.PHOTO_FRAME -> GlassQuickLauncherWidgetProvider::class.java
        }
    }

    /**
     * Attempts to directly pin the widget to the home screen.
     * Returns true if requestPinAppWidget was accepted by the OS launcher, false otherwise.
     */
    fun pinWidgetToHomeScreen(
        context: Context,
        widget: WidgetItem,
        customization: WidgetCustomization? = null
    ): Boolean {
        return try {
            GlassWidgetStore.savePinnedWidget(context, widget, customization)

            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return false
            val providerClass = getProviderClassForCategory(widget.category)
            val componentName = ComponentName(context, providerClass)

            // Trigger immediate update on existing widgets of this provider
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            if (ids != null && ids.isNotEmpty()) {
                val updateIntent = Intent(context, providerClass).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(updateIntent)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    val successIntent = Intent(context, MainActivity::class.java).apply {
                        putExtra("pinned_widget_id", widget.id)
                        putExtra("pinned_widget_title", widget.title)
                    }
                    val successCallback = PendingIntent.getActivity(
                        context,
                        widget.id.hashCode(),
                        successIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    appWidgetManager.requestPinAppWidget(componentName, null, successCallback)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
