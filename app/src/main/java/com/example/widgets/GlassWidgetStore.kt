package com.example.widgets

import android.content.Context
import android.content.SharedPreferences
import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.repository.WidgetCatalogRepository

object GlassWidgetStore {

    private const val PREFS_NAME = "glass_widgets_prefs"
    private const val KEY_LAST_WIDGET_ID = "last_pinned_widget_id"
    private const val KEY_PREFIX_PROVIDER = "prov_widget_"
    private const val KEY_PREFIX_CATEGORY = "cat_widget_id_"
    private const val KEY_PREFIX_TINT = "tint_"
    private const val KEY_PREFIX_ALPHA = "alpha_"
    private const val KEY_PREFIX_BORDER = "border_"
    private const val KEY_PREFIX_CORNER = "corner_"
    private const val KEY_PREFIX_GLOW = "glow_"
    private const val KEY_PREFIX_LIQUID = "liquid_"
    private const val KEY_PREFIX_WALLPAPER = "wallpaper_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePinnedWidget(
        context: Context,
        widget: WidgetItem,
        customization: WidgetCustomization?
    ) {
        val prefs = getPrefs(context)
        val custom = customization ?: widget.defaultCustomization
        val providerName = AppWidgetPinHelper.getProviderClassForCategory(widget.category).simpleName

        prefs.edit().apply {
            putString(KEY_LAST_WIDGET_ID, widget.id)
            putString("$KEY_PREFIX_PROVIDER$providerName", widget.id)
            putString("$KEY_PREFIX_CATEGORY${widget.category.name}", widget.id)
            putInt("$KEY_PREFIX_TINT${widget.id}", custom.tintIndex)
            putFloat("$KEY_PREFIX_ALPHA${widget.id}", custom.glassAlpha)
            putFloat("$KEY_PREFIX_BORDER${widget.id}", custom.borderOpacity)
            putInt("$KEY_PREFIX_CORNER${widget.id}", custom.cornerRadiusDp)
            putFloat("$KEY_PREFIX_GLOW${widget.id}", custom.glowIntensity)
            putFloat("$KEY_PREFIX_LIQUID${widget.id}", custom.manualLiquidLevel)
            putInt("$KEY_PREFIX_WALLPAPER${widget.id}", custom.wallpaperIndex)
            apply()
        }
    }

    fun getWidgetForProvider(
        context: Context,
        providerSimpleName: String,
        fallbackCategory: WidgetCategory
    ): Pair<WidgetItem, WidgetCustomization> {
        val prefs = getPrefs(context)
        val savedId = prefs.getString("$KEY_PREFIX_PROVIDER$providerSimpleName", null)
            ?: prefs.getString("$KEY_PREFIX_CATEGORY${fallbackCategory.name}", null)
            ?: prefs.getString(KEY_LAST_WIDGET_ID, null)

        val widget = if (savedId != null) {
            WidgetCatalogRepository.allWidgets.find { it.id == savedId }
                ?: WidgetCatalogRepository.allWidgets.find { it.category == fallbackCategory }
                ?: WidgetCatalogRepository.allWidgets.first()
        } else {
            WidgetCatalogRepository.allWidgets.find { it.category == fallbackCategory }
                ?: WidgetCatalogRepository.allWidgets.first()
        }

        val custom = getCustomizationForWidget(context, widget)
        return widget to custom
    }

    fun getWidgetForCategory(
        context: Context,
        category: WidgetCategory
    ): Pair<WidgetItem, WidgetCustomization> {
        val providerName = AppWidgetPinHelper.getProviderClassForCategory(category).simpleName
        return getWidgetForProvider(context, providerName, category)
    }

    fun getCustomizationForWidget(
        context: Context,
        widget: WidgetItem
    ): WidgetCustomization {
        val prefs = getPrefs(context)
        val default = widget.defaultCustomization
        val tint = prefs.getInt("$KEY_PREFIX_TINT${widget.id}", default.tintIndex)
        val alpha = prefs.getFloat("$KEY_PREFIX_ALPHA${widget.id}", default.glassAlpha)
        val border = prefs.getFloat("$KEY_PREFIX_BORDER${widget.id}", default.borderOpacity)
        val corner = prefs.getInt("$KEY_PREFIX_CORNER${widget.id}", default.cornerRadiusDp)
        val glow = prefs.getFloat("$KEY_PREFIX_GLOW${widget.id}", default.glowIntensity)
        val liquid = prefs.getFloat("$KEY_PREFIX_LIQUID${widget.id}", default.manualLiquidLevel)
        val wallpaper = prefs.getInt("$KEY_PREFIX_WALLPAPER${widget.id}", default.wallpaperIndex)

        return default.copy(
            tintIndex = tint,
            glassAlpha = alpha,
            borderOpacity = border,
            cornerRadiusDp = corner,
            glowIntensity = glow,
            manualLiquidLevel = liquid,
            wallpaperIndex = wallpaper
        )
    }
}
