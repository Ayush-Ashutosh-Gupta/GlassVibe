package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.model.WidgetCategory
import com.example.utils.HapticHelper

class GlassLiquidBatteryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        GlassWidgetFlowTicker.startFlowTicker(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_SLOSH_WIDGET) {
            HapticHelper.splash(context)
            GlassWidgetFlowTicker.triggerSloshBurst(context)
        }
    }

    companion object {
        const val ACTION_SLOSH_WIDGET = "com.example.widgets.ACTION_SLOSH_WIDGET"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            wavePhase: Float? = null
        ) {
            try {
                val (widget, custom) = GlassWidgetStore.getWidgetForProvider(
                    context,
                    "GlassLiquidBatteryWidgetProvider",
                    WidgetCategory.BATTERY
                )
                val bitmap = GlassWidgetBitmapRenderer.renderWidget(
                    context = context,
                    widget = widget,
                    customization = custom,
                    wavePhase = wavePhase
                )

                val views = RemoteViews(context.packageName, R.layout.widget_liquid_battery)
                views.setImageViewBitmap(R.id.widget_glass_image, bitmap)

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("open_widget_id", widget.id)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    widget.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
