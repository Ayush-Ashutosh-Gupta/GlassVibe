package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.R

enum class WidgetCategory(
    val title: String,
    val iconName: String,
    val description: String
) {
    LIQUID("Liquid Physics", "Science", "Sensory fluid physics driven by real hardware accelerometer"),
    CLOCKS("Clocks & Time", "Schedule", "Minimalist, Bauhaus, and Tokyo neon frosted dials"),
    WEATHER("Weather & Radar", "WbSunny", "Hyperlocal forecast glass with live atmospheric visuals"),
    SCREEN_TIME("Screen Time", "HourglassEmpty", "Visual wellbeing beakers, focus gauges, and app quotas"),
    BATTERY("Battery & Power", "BatteryChargingFull", "Cyber fluid cells, radial energy cores, and wireless arcs"),
    MUSIC("Music & Media", "MusicNote", "Frosted vinyl decks, glowing waveforms, and album capsules"),
    CALENDAR("Calendar & Agenda", "CalendarMonth", "Modular day schedules, month grids, and countdown capsules"),
    LAUNCHERS("Quick Launchers", "Apps", "Floating docks, glass app orbs, and quick action pads"),
    FITNESS("Fitness & Steps", "DirectionsRun", "Hydration waves, activity rings, and step milestone bars"),
    NOTES("Notes & Memos", "StickyNote2", "Translucent sticky pads, checklists, and voice capsules"),
    SYSTEM("System Monitors", "Memory", "Real-time CPU telemetry, RAM gauges, and storage glass"),
    QUOTES("Quotes & Mantra", "FormatQuote", "Stoic reflections, neon typography, and daily inspiration"),
    AMBIENT("Ambient Visualizers", "AutoAwesome", "Particle auroras, lava glows, and geometric prisms"),
    CRYPTO("Crypto & Finance", "CurrencyBitcoin", "Live tickers, portfolio sparklines, and currency matrices"),
    ASTRONOMY("Moon & Astronomy", "NightsStay", "Lunar phase orbits, starry constellations, and solar arcs"),
    POMODORO("Pomodoro & Focus", "Timer", "Deep work flasks, flow state gauges, and interval sandglass"),
    HABITS("Habits & Mood", "Favorite", "Daily streak heatmaps, emotion rings, and water trackers"),
    DYNAMIC_ISLAND("Dynamic Island", "Widgets", "Live status capsules, flight pills, and alert badges"),
    NETWORK("Network & Wi-Fi", "Wifi", "Signal glass, data tanks, and latency telemetry graphs"),
    MINI_GAMES("Mini Games & Toys", "SportsEsports", "Fidget bubble pops, tilt maze balls, and dice rollers"),
    PHOTO_FRAME("Photo & Memories", "PhotoLibrary", "Polaroid frosted frames and ambient photo spotlights"),
    SENSORS("Sensors & Compass", "Explore", "3D Gyro compass, lux light meter, and tilt inclinometer")
}

enum class WidgetSizeType(val label: String, val gridSpan: Int) {
    SMALL_2x2("Small (2×2)", 1),
    MEDIUM_4x2("Medium (4×2)", 2),
    LARGE_4x4("Large (4×4)", 2),
    WIDE_4x1("Wide (4×1)", 2),
    PILL_2x1("Pill (2×1)", 1)
}

data class WallpaperItem(
    val id: String,
    val name: String,
    val resId: Int?,
    val gradientColors: List<Color> = emptyList()
)

object WallpaperPresets {
    val wallpapers = listOf(
        WallpaperItem("cosmic_aurora", "Cosmic Aurora", R.drawable.bg_cosmic_aurora, listOf(Color(0xFF030712), Color(0xFF0F172A), Color(0xFF1E1B4B))),
        WallpaperItem("cyber_sunset", "Cyber Sunset", R.drawable.bg_cyber_sunset, listOf(Color(0xFF180324), Color(0xFF3B0764), Color(0xFF4C0519))),
        WallpaperItem("liquid_prism", "Liquid Prism", R.drawable.bg_liquid_prism, listOf(Color(0xFF042F2E), Color(0xFF0F172A), Color(0xFF1E1B4B))),
        WallpaperItem("obsidian_deep", "Obsidian Deep", null, listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF000000))),
        WallpaperItem("emerald_abyss", "Emerald Abyss", null, listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF020617))),
        WallpaperItem("royal_iris", "Royal Iris", null, listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF09090B)))
    )
}

data class WidgetCustomization(
    val blurIntensity: Float = 0.85f,
    val borderOpacity: Float = 0.55f,
    val borderWidthDp: Float = 1.5f,
    val glassAlpha: Float = 0.22f,
    val tintIndex: Int = 0,
    val cornerRadiusDp: Int = 24,
    val glowIntensity: Float = 0.7f,
    val manualLiquidLevel: Float = 0.65f,
    val manualTiltAngleDeg: Float = 0f,
    val useSensorPhysics: Boolean = true,
    val wallpaperIndex: Int = 0
)

data class GlassTint(
    val name: String,
    val primaryColor: Color,
    val glowColor: Color,
    val liquidColor: Color,
    val liquidSecondaryColor: Color
)

object GlassTintPalettes {
    val tints = listOf(
        GlassTint("Frosted Ice", Color(0xFFE2E8F0), Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF38BDF8)),
        GlassTint("Emerald Neon", Color(0xFF10B981), Color(0xFF34D399), Color(0xFF059669), Color(0xFF10B981)),
        GlassTint("Cyber Amethyst", Color(0xFF8B5CF6), Color(0xFFA78BFA), Color(0xFF7C3AED), Color(0xFFC084FC)),
        GlassTint("Amber Honey", Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFFFDE68A)),
        GlassTint("Cyber Cyan", Color(0xFF06B6D4), Color(0xFF22D3EE), Color(0xFF0891B2), Color(0xFF67E8F9)),
        GlassTint("Rose Gold", Color(0xFFF43F5E), Color(0xFFFB7185), Color(0xFFE11D48), Color(0xFFFDA4AF)),
        GlassTint("Sunset Coral", Color(0xFFFF5722), Color(0xFFFF8A65), Color(0xFFE64A19), Color(0xFFFFAB91)),
        GlassTint("Obsidian Deep", Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFF334155), Color(0xFF64748B)),
        GlassTint("Royal Indigo", Color(0xFF6366F1), Color(0xFF818CF8), Color(0xFF4F46E5), Color(0xFFA5B4FC))
    )
}

data class WidgetItem(
    val id: String,
    val title: String,
    val category: WidgetCategory,
    val description: String,
    val sizeType: WidgetSizeType,
    val isLiquid: Boolean = false,
    val defaultCustomization: WidgetCustomization = WidgetCustomization(),
    val tags: List<String> = emptyList(),
    val isPopular: Boolean = false
)
