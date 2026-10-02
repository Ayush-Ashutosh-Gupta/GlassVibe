package com.example.repository

import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.model.WidgetSizeType

object WidgetCatalogRepository {

    val allWidgets: List<WidgetItem> = listOf(
        // ==================== 1. LIQUID PHYSICS (8 Widgets) ====================
        WidgetItem(
            id = "liquid_screen_time_beaker",
            title = "Screen Time Liquid Beaker",
            category = WidgetCategory.LIQUID,
            description = "Glass chemistry beaker filled with reactive fluid showing remaining daily screen time quota. Sloshes with phone tilt and inverts upside down.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 4, manualLiquidLevel = 0.62f),
            tags = listOf("Screen Time", "Sensor", "Physics", "Chemistry"),
            isPopular = true
        ),
        WidgetItem(
            id = "liquid_battery_cell",
            title = "Battery Fluid Core",
            category = WidgetCategory.LIQUID,
            description = "High-tech translucent cylinder showing battery charge level as luminous cyber fluid with tilt-responsive wave dynamics.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 1, manualLiquidLevel = 0.84f),
            tags = listOf("Battery", "Fluid", "Sensor"),
            isPopular = true
        ),
        WidgetItem(
            id = "liquid_weather_tide",
            title = "Ocean Tide & Rain Tank",
            category = WidgetCategory.LIQUID,
            description = "Precipitation and oceanic tide tracker with sensor sloshing water, rising bubbles, and current temperature display.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 0, manualLiquidLevel = 0.45f),
            tags = listOf("Weather", "Ocean", "Tide"),
            isPopular = true
        ),
        WidgetItem(
            id = "liquid_mana_potion",
            title = "Mystic Mana Potion",
            category = WidgetCategory.LIQUID,
            description = "Fantasy potion flask filled with glowing amethyst elixir that splashes dynamically and inverts with phone gravity.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 2, manualLiquidLevel = 0.75f),
            tags = listOf("Potion", "Fantasy", "Elixir"),
            isPopular = true
        ),
        WidgetItem(
            id = "liquid_lava_lamp",
            title = "Retro Lava Glass",
            category = WidgetCategory.LIQUID,
            description = "Warm glowing liquid chamber with morphing wax blobs responding to accelerometer tilt and orientation inversion.",
            sizeType = WidgetSizeType.LARGE_4x4,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 6, manualLiquidLevel = 0.70f),
            tags = listOf("Lava", "Ambient", "Retro"),
            isPopular = false
        ),
        WidgetItem(
            id = "liquid_mercury_tube",
            title = "Liquid Mercury Barometer",
            category = WidgetCategory.LIQUID,
            description = "Sleek metallic liquid barometer with reflective chrome sheen that glides along the glass capillary.",
            sizeType = WidgetSizeType.WIDE_4x1,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 7, manualLiquidLevel = 0.58f),
            tags = listOf("Mercury", "Barometer", "Minimal"),
            isPopular = false
        ),
        WidgetItem(
            id = "liquid_zen_ripple",
            title = "Zen Water Basin",
            category = WidgetCategory.LIQUID,
            description = "Tranquil circular water vessel with smooth sine wave ripples and calming ambient particle reflections.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 0, manualLiquidLevel = 0.50f),
            tags = listOf("Zen", "Water", "Mindful"),
            isPopular = false
        ),
        WidgetItem(
            id = "liquid_hydration_flask",
            title = "Daily Hydro Flask",
            category = WidgetCategory.LIQUID,
            description = "Interactive water intake tracker showing milliliters consumed with live sloshing water physics.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 4, manualLiquidLevel = 0.68f),
            tags = listOf("Hydration", "Water", "Health"),
            isPopular = true
        ),

        // ==================== 2. CLOCKS & TIME (7 Widgets) ====================
        WidgetItem(
            id = "clock_bauhaus_minimal",
            title = "Bauhaus Frost Clock",
            category = WidgetCategory.CLOCKS,
            description = "Clean geometric analog clock face with frosted glass background and smooth sweeping second hand.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Clock", "Analog", "Bauhaus"),
            isPopular = true
        ),
        WidgetItem(
            id = "clock_tokyo_neon",
            title = "Tokyo Cyber Digital",
            category = WidgetCategory.CLOCKS,
            description = "High-contrast digital timepiece with glowing neon segments, date capsule, and weather pill.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Clock", "Digital", "Cyberpunk"),
            isPopular = true
        ),
        WidgetItem(
            id = "clock_neumorphic_dial",
            title = "Neumorphic Depth Clock",
            category = WidgetCategory.CLOCKS,
            description = "Subtle soft shadows and frosted glass reflections framing a luxury Swiss-inspired dial.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Clock", "Neumorphic", "Luxury"),
            isPopular = false
        ),
        WidgetItem(
            id = "clock_world_dual_zone",
            title = "Dual World Time Capsule",
            category = WidgetCategory.CLOCKS,
            description = "Side-by-side frosted cards tracking your local time and a secondary global time zone (Tokyo/London/NYC).",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Clock", "World Time", "Travel"),
            isPopular = false
        ),
        WidgetItem(
            id = "clock_orbital_celestial",
            title = "Orbital Celestial Clock",
            category = WidgetCategory.CLOCKS,
            description = "Planetary orbits representing hours, minutes, and seconds traversing translucent cosmic rings.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Clock", "Celestial", "Orbit"),
            isPopular = true
        ),
        WidgetItem(
            id = "clock_typo_horizon",
            title = "Typographic Horizon",
            category = WidgetCategory.CLOCKS,
            description = "Bold oversized typography paired with a delicate gradient horizon bar and battery status.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Clock", "Typography", "Wide"),
            isPopular = false
        ),
        WidgetItem(
            id = "clock_chrono_speed",
            title = "Chrono Speed Dial",
            category = WidgetCategory.CLOCKS,
            description = "Automotive-inspired speedometer dial showcasing milliseconds, active timezone, and system stats.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Clock", "Chrono", "Sport"),
            isPopular = false
        ),

        // ==================== 3. WEATHER & ATMOSPHERE (7 Widgets) ====================
        WidgetItem(
            id = "weather_hyperlocal_radar",
            title = "Hyperlocal Forecast Glass",
            category = WidgetCategory.WEATHER,
            description = "Dynamic meteorological card with animated precipitation radar, high/low curves, and air quality index.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Weather", "Radar", "Forecast"),
            isPopular = true
        ),
        WidgetItem(
            id = "weather_aurora_horizon",
            title = "Aurora Horizon Forecast",
            category = WidgetCategory.WEATHER,
            description = "Curved translucent temperature arc showing the 24-hour sun cycle and weather shifts.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Weather", "Aurora", "24Hour"),
            isPopular = true
        ),
        WidgetItem(
            id = "weather_solar_golden_hour",
            title = "Solar Arc & Golden Hour",
            category = WidgetCategory.WEATHER,
            description = "Tracks remaining daylight, golden hour photography window, and blue hour sunset progression.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Weather", "Sun", "Golden Hour"),
            isPopular = false
        ),
        WidgetItem(
            id = "weather_rain_droplet_gauge",
            title = "Rain Droplet Pod",
            category = WidgetCategory.WEATHER,
            description = "Compact pill with real-time precipitation chance and atmospheric humidity percentage.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Weather", "Rain", "Humidity"),
            isPopular = false
        ),
        WidgetItem(
            id = "weather_frost_temp_bar",
            title = "Frost Temp Horizon",
            category = WidgetCategory.WEATHER,
            description = "Ultra-slim wide frosted bar displaying weekly temperature spreads and current condition icon.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Weather", "Temp", "Minimal"),
            isPopular = false
        ),
        WidgetItem(
            id = "weather_barometer_pod",
            title = "Atmospheric Pressure Glass",
            category = WidgetCategory.WEATHER,
            description = "Precision barometric telemetry tracking storm fronts and altitude pressure variances in hPa.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Weather", "Barometer", "Pressure"),
            isPopular = false
        ),
        WidgetItem(
            id = "weather_wind_uv_prism",
            title = "Wind & UV Index Prism",
            category = WidgetCategory.WEATHER,
            description = "Compass-styled wind vector with UV exposure safety recommendations in frosted glass.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Weather", "Wind", "UV"),
            isPopular = false
        ),

        // ==================== 4. SCREEN TIME & WELLBEING (6 Widgets) ====================
        WidgetItem(
            id = "screen_time_quota_beaker",
            title = "Digital Wellbeing Flask",
            category = WidgetCategory.SCREEN_TIME,
            description = "Liquid flask draining throughout the day as phone usage ticks up. Supports physical tilting!",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 5, manualLiquidLevel = 0.55f),
            tags = listOf("Screen Time", "Wellbeing", "Liquid"),
            isPopular = true
        ),
        WidgetItem(
            id = "screen_time_mindful_rings",
            title = "Mindful Focus Rings",
            category = WidgetCategory.SCREEN_TIME,
            description = "Three concentric translucent rings tracking Social, Productivity, and Entertainment app ratios.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Screen Time", "Rings", "Focus"),
            isPopular = true
        ),
        WidgetItem(
            id = "screen_time_weekly_bar",
            title = "Weekly Usage Sparkline",
            category = WidgetCategory.SCREEN_TIME,
            description = "7-day frosted glass column chart comparing your daily screen time against your weekly average.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Screen Time", "Weekly", "Chart"),
            isPopular = false
        ),
        WidgetItem(
            id = "screen_time_unplug_timer",
            title = "Bedtime Unplug Capsule",
            category = WidgetCategory.SCREEN_TIME,
            description = "Warm amber glass countdown prompting you to disconnect 45 minutes before sleep.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Screen Time", "Sleep", "Unplug"),
            isPopular = false
        ),
        WidgetItem(
            id = "screen_time_app_cap_pill",
            title = "App Quota Limit Pill",
            category = WidgetCategory.SCREEN_TIME,
            description = "Tracks specific high-usage apps like Instagram or YouTube with remaining time badges.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Screen Time", "Quota", "Pill"),
            isPopular = false
        ),
        WidgetItem(
            id = "screen_time_dopamine_detox",
            title = "Dopamine Detox Gauge",
            category = WidgetCategory.SCREEN_TIME,
            description = "Visual streak counter celebrating consecutive days under your 2-hour screen goal.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Screen Time", "Detox", "Habit"),
            isPopular = false
        ),

        // ==================== 5. BATTERY & POWER (6 Widgets) ====================
        WidgetItem(
            id = "battery_cyber_cell",
            title = "Cyber Cell Fluid Battery",
            category = WidgetCategory.BATTERY,
            description = "Translucent futuristic fuel cell with liquid electrolytes that slosh dynamically when moving.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 1, manualLiquidLevel = 0.88f),
            tags = listOf("Battery", "Cyber", "Fluid"),
            isPopular = true
        ),
        WidgetItem(
            id = "battery_radial_energy_core",
            title = "Radial Energy Core",
            category = WidgetCategory.BATTERY,
            description = "Circular arc displaying device battery percentage, charging wattage, and estimated time remaining.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Battery", "Radial", "Core"),
            isPopular = true
        ),
        WidgetItem(
            id = "battery_dual_wireless_arc",
            title = "Multi-Device Power Hub",
            category = WidgetCategory.BATTERY,
            description = "Combined status monitor for Phone, Watch, and Earbuds battery levels in frosted glass tiles.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Battery", "Multi-Device", "Wireless"),
            isPopular = false
        ),
        WidgetItem(
            id = "battery_obsidian_saver",
            title = "Obsidian Low-Power Glass",
            category = WidgetCategory.BATTERY,
            description = "Ultra-minimal dark glass strip with adaptive color shifting (Green > Yellow > Crimson).",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Battery", "Obsidian", "Minimal"),
            isPopular = false
        ),
        WidgetItem(
            id = "battery_thermal_core_gauge",
            title = "Thermal & Health Core",
            category = WidgetCategory.BATTERY,
            description = "Monitors battery temperature in °C, charge cycle health score, and fast charge speed.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Battery", "Thermal", "Health"),
            isPopular = false
        ),
        WidgetItem(
            id = "battery_fast_charge_bolt",
            title = "Lightning Fast Charge Pill",
            category = WidgetCategory.BATTERY,
            description = "Glowing lightning bolt pill with pulsing charge animations and wattage readout.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Battery", "Charge", "Pill"),
            isPopular = false
        ),

        // ==================== 6. MUSIC & MEDIA (6 Widgets) ====================
        WidgetItem(
            id = "music_vinyl_turntable",
            title = "Vinyl Frosted Turntable",
            category = WidgetCategory.MUSIC,
            description = "Rotating translucent vinyl record with ambient album glow, playback scrubber, and glass controls.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Music", "Vinyl", "Player"),
            isPopular = true
        ),
        WidgetItem(
            id = "music_glow_waveform_deck",
            title = "Glow Waveform Media Deck",
            category = WidgetCategory.MUSIC,
            description = "Live pulsating audio waveform with blurred album artwork backdrop and haptic play/pause.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Music", "Waveform", "Audio"),
            isPopular = true
        ),
        WidgetItem(
            id = "music_spotify_glass_pill",
            title = "Frosted Spotify Capsule",
            category = WidgetCategory.MUSIC,
            description = "Compact glass pill showing current track title, artist name, and quick skip controls.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Music", "Spotify", "Capsule"),
            isPopular = false
        ),
        WidgetItem(
            id = "music_ambient_equalizer",
            title = "Prism Spectrum Visualizer",
            category = WidgetCategory.MUSIC,
            description = "10-band frosted equalizer bars that bounce rhythmically with the music beat.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Music", "Equalizer", "Visualizer"),
            isPopular = false
        ),
        WidgetItem(
            id = "music_retro_cassette_frost",
            title = "Retro Cassette Glass",
            category = WidgetCategory.MUSIC,
            description = "Nostalgic 90s cassette tape rendered in frosted clear polycarbonate with spinning reels.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Music", "Cassette", "Retro"),
            isPopular = false
        ),
        WidgetItem(
            id = "music_spatial_audio_capsule",
            title = "Spatial Audio Status",
            category = WidgetCategory.MUSIC,
            description = "High-res lossless badge, Dolby Atmos indicator, and volume slider in frosted glass.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Music", "Spatial", "Audio"),
            isPopular = false
        ),

        // ==================== 7. CALENDAR & AGENDA (6 Widgets) ====================
        WidgetItem(
            id = "calendar_modular_month_grid",
            title = "Modular Month Glass",
            category = WidgetCategory.CALENDAR,
            description = "Elegant frosted monthly grid with glowing indicator dots for scheduled meetings and events.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Calendar", "Month", "Grid"),
            isPopular = true
        ),
        WidgetItem(
            id = "calendar_next_agenda_capsule",
            title = "Next Up Agenda Glass",
            category = WidgetCategory.CALENDAR,
            description = "Chronological timeline cards for your upcoming meetings with Google Meet launch pills.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Calendar", "Agenda", "Schedule"),
            isPopular = true
        ),
        WidgetItem(
            id = "calendar_event_countdown_glass",
            title = "Event Countdown Capsule",
            category = WidgetCategory.CALENDAR,
            description = "Frosted glass timer counting down days, hours, and minutes to birthdays, holidays, or trips.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Calendar", "Countdown", "Event"),
            isPopular = false
        ),
        WidgetItem(
            id = "calendar_horizon_day_bar",
            title = "Horizon Day Timeline",
            category = WidgetCategory.CALENDAR,
            description = "Sleek horizontal timeline highlighting free slots, current meeting, and evening plans.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Calendar", "Timeline", "Day"),
            isPopular = false
        ),
        WidgetItem(
            id = "calendar_minimal_date_pill",
            title = "Minimalist Date & Day",
            category = WidgetCategory.CALENDAR,
            description = "Clean aesthetic day of the week, date number, and week number in frosted capsule.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Calendar", "Date", "Pill"),
            isPopular = false
        ),
        WidgetItem(
            id = "calendar_schedule_matrix",
            title = "Work-Life Split Matrix",
            category = WidgetCategory.CALENDAR,
            description = "Visual balance card displaying percentage of time spent in meetings vs deep focus.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Calendar", "Matrix", "Productivity"),
            isPopular = false
        ),

        // ==================== 8. QUICK LAUNCHERS (6 Widgets) ====================
        WidgetItem(
            id = "launcher_floating_dock",
            title = "Floating Glass Dock",
            category = WidgetCategory.LAUNCHERS,
            description = "Customizable glass dock with 4 or 6 shortcuts featuring subtle hover sheen and haptic clicks.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Launcher", "Dock", "Shortcuts"),
            isPopular = true
        ),
        WidgetItem(
            id = "launcher_speed_dial_orb",
            title = "Contact Speed Dial Glass",
            category = WidgetCategory.LAUNCHERS,
            description = "Frosted circular avatar orbs for 1-tap WhatsApp, Call, or Message to favorite contacts.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Launcher", "Contacts", "SpeedDial"),
            isPopular = true
        ),
        WidgetItem(
            id = "launcher_action_matrix",
            title = "Quick Settings Matrix",
            category = WidgetCategory.LAUNCHERS,
            description = "Frosted toggles for Flashlight, Bluetooth, Wi-Fi Hotspot, DND, and Calculator.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Launcher", "Toggles", "Settings"),
            isPopular = false
        ),
        WidgetItem(
            id = "launcher_workspace_toggle",
            title = "Workspace Profile Switcher",
            category = WidgetCategory.LAUNCHERS,
            description = "Instantly switch between Work, Personal, Gaming, and Reading widget setups.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Launcher", "Workspace", "Profiles"),
            isPopular = false
        ),
        WidgetItem(
            id = "launcher_smart_suggestions",
            title = "Contextual App Suggestions",
            category = WidgetCategory.LAUNCHERS,
            description = "AI-suggested apps based on morning routine, commute, workout, or nighttime.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Launcher", "AI", "Suggestions"),
            isPopular = false
        ),
        WidgetItem(
            id = "launcher_web_search_capsule",
            title = "Frosted Search Capsule",
            category = WidgetCategory.LAUNCHERS,
            description = "Translucent Google search bar with Google Lens and Voice Search glass buttons.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Launcher", "Search", "Google"),
            isPopular = false
        ),

        // ==================== 9. FITNESS & STEPS (6 Widgets) ====================
        WidgetItem(
            id = "fitness_hydration_wave",
            title = "Hydration Wave Tracker",
            category = WidgetCategory.FITNESS,
            description = "Liquid filled glass vessel tracking your daily 2.5L water intake with dynamic sloshing physics.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 4, manualLiquidLevel = 0.72f),
            tags = listOf("Fitness", "Hydration", "Liquid"),
            isPopular = true
        ),
        WidgetItem(
            id = "fitness_tri_rings_glass",
            title = "Activity Tri-Rings Glass",
            category = WidgetCategory.FITNESS,
            description = "Move, Exercise, and Stand progress rings with glowing neon halos on frosted glass.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Fitness", "Rings", "Activity"),
            isPopular = true
        ),
        WidgetItem(
            id = "fitness_calorie_flame_gauge",
            title = "Calorie Burn Flame Gauge",
            category = WidgetCategory.FITNESS,
            description = "Dynamic fire energy meter tracking active kcal burned against your daily goal.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Fitness", "Calories", "Flame"),
            isPopular = false
        ),
        WidgetItem(
            id = "fitness_step_milestone_arc",
            title = "Step Milestone Arc",
            category = WidgetCategory.FITNESS,
            description = "10,000 steps progress arc with distance in km, active walking minutes, and pace.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Fitness", "Steps", "Walking"),
            isPopular = true
        ),
        WidgetItem(
            id = "fitness_heart_rate_ecg",
            title = "Heart Rate ECG Glass",
            category = WidgetCategory.FITNESS,
            description = "Pulsing cardio waveform showing resting heart rate, current BPM, and HRV score.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Fitness", "HeartRate", "Cardio"),
            isPopular = false
        ),
        WidgetItem(
            id = "fitness_runner_splits",
            title = "Runner Pace & Route Pod",
            category = WidgetCategory.FITNESS,
            description = "Tracks your last jog with 1km split times, elevation climbed, and cadence.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Fitness", "Running", "Pace"),
            isPopular = false
        ),

        // ==================== 10. NOTES & MEMOS (6 Widgets) ====================
        WidgetItem(
            id = "notes_translucent_sticky",
            title = "Frosted Sticky Note",
            category = WidgetCategory.NOTES,
            description = "Translucent colorful notepad with custom markdown text and pin icon.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Notes", "Sticky", "Memo"),
            isPopular = true
        ),
        WidgetItem(
            id = "notes_checklist_pill",
            title = "Task Checklist Glass",
            category = WidgetCategory.NOTES,
            description = "Interactive to-do card where you can tap items to check them off directly on the widget.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Notes", "Tasks", "Todo"),
            isPopular = true
        ),
        WidgetItem(
            id = "notes_idea_scratchpad",
            title = "Idea Scratchpad Glass",
            category = WidgetCategory.NOTES,
            description = "Quick jotting area for brainstorming, links, phone numbers, and grocery lists.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Notes", "Scratchpad", "Brainstorm"),
            isPopular = false
        ),
        WidgetItem(
            id = "notes_voice_memo_capsule",
            title = "Voice Memo Audio Capsule",
            category = WidgetCategory.NOTES,
            description = "One-tap voice recorder with recorded audio wave preview and playback pill.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Notes", "Voice", "Audio"),
            isPopular = false
        ),
        WidgetItem(
            id = "notes_quick_code_snippet",
            title = "Code Snippet Frost",
            category = WidgetCategory.NOTES,
            description = "Monospace syntax-highlighted code card for developers to pin commands or API tokens.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Notes", "Code", "Dev"),
            isPopular = false
        ),
        WidgetItem(
            id = "notes_pinned_quote_card",
            title = "Pinned Affirmation Note",
            category = WidgetCategory.NOTES,
            description = "Custom personal intention of the day framed in delicate frosted glass.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Notes", "Affirmation", "Pill"),
            isPopular = false
        ),

        // ==================== 11. SYSTEM MONITORS (6 Widgets) ====================
        WidgetItem(
            id = "system_cpu_ram_gauge",
            title = "CPU & RAM Telemetry",
            category = WidgetCategory.SYSTEM,
            description = "Real-time twin circular gauges showing CPU frequency load and RAM utilization.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("System", "CPU", "RAM"),
            isPopular = true
        ),
        WidgetItem(
            id = "system_storage_glass",
            title = "Storage Breakdown Glass",
            category = WidgetCategory.SYSTEM,
            description = "Multi-segment translucent bar displaying System, Apps, Media, and Free GB storage.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("System", "Storage", "Memory"),
            isPopular = true
        ),
        WidgetItem(
            id = "system_sensor_telemetry_pod",
            title = "Hardware Sensor Pod",
            category = WidgetCategory.SYSTEM,
            description = "Live Accelerometer, Gyro, Magnetometer, and Ambient Light sensor raw data readouts.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("System", "Sensors", "Hardware"),
            isPopular = false
        ),
        WidgetItem(
            id = "system_bandwidth_speedo",
            title = "Bandwidth Speedometer",
            category = WidgetCategory.SYSTEM,
            description = "Real-time network download/upload speed meter in MB/s with ping graph.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("System", "Speed", "Network"),
            isPopular = false
        ),
        WidgetItem(
            id = "system_device_uptime_pill",
            title = "Device Uptime & Kernel",
            category = WidgetCategory.SYSTEM,
            description = "Displays continuous system uptime (days/hours), Android version, and kernel patch level.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("System", "Uptime", "Android"),
            isPopular = false
        ),
        WidgetItem(
            id = "system_thermal_pod",
            title = "Battery & SOC Thermals",
            category = WidgetCategory.SYSTEM,
            description = "Monitors processor core temperature and throttling states in frosted glass.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("System", "Thermal", "SOC"),
            isPopular = false
        ),

        // ==================== 12. QUOTES & MANTRA (6 Widgets) ====================
        WidgetItem(
            id = "quotes_stoic_mind_frost",
            title = "Stoic Reflections Glass",
            category = WidgetCategory.QUOTES,
            description = "Daily philosophical insights from Marcus Aurelius, Seneca, and Epictetus on frosted obsidian.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Quotes", "Stoic", "Philosophy"),
            isPopular = true
        ),
        WidgetItem(
            id = "quotes_neon_typo_mantra",
            title = "Neon Typography Mantra",
            category = WidgetCategory.QUOTES,
            description = "High-energy motivational typography with luminous ambient neon drop shadow.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Quotes", "Neon", "Motivation"),
            isPopular = true
        ),
        WidgetItem(
            id = "quotes_zen_reflection",
            title = "Zen Mindfulness Spark",
            category = WidgetCategory.QUOTES,
            description = "Gentle breathing reminders and mindful zen quotes to anchor your present awareness.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Quotes", "Zen", "Mindfulness"),
            isPopular = false
        ),
        WidgetItem(
            id = "quotes_cyber_philosophy",
            title = "Cyber Horizon Monologue",
            category = WidgetCategory.QUOTES,
            description = "Futuristic reflections on technology, cosmos, and human potential in sleek cyber cyan.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Quotes", "Cyber", "Futuristic"),
            isPopular = false
        ),
        WidgetItem(
            id = "quotes_daily_affirmation",
            title = "Positive Energy Affirmation",
            category = WidgetCategory.QUOTES,
            description = "Daily confidence and gratitude affirmation card with soft pastel glow.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Quotes", "Affirmation", "Positive"),
            isPopular = false
        ),
        WidgetItem(
            id = "quotes_literary_classic",
            title = "Literary Classics Frost",
            category = WidgetCategory.QUOTES,
            description = "Timeless prose quotes from Shakespeare, Oscar Wilde, and Virginia Woolf.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Quotes", "Literature", "Classics"),
            isPopular = false
        ),

        // ==================== 13. AMBIENT VISUALIZERS (6 Widgets) ====================
        WidgetItem(
            id = "ambient_particle_aurora",
            title = "Particle Aurora Glow",
            category = WidgetCategory.AMBIENT,
            description = "Hypnotic swirling particle mesh responding to phone movements and touch ripples.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Ambient", "Aurora", "Particles"),
            isPopular = true
        ),
        WidgetItem(
            id = "ambient_geometric_prism",
            title = "Geometric Prism Refraction",
            category = WidgetCategory.AMBIENT,
            description = "3D rotating frosted crystal prism casting chromatic rainbow dispersion flares.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Ambient", "Prism", "3D"),
            isPopular = true
        ),
        WidgetItem(
            id = "ambient_matrix_digital_rain",
            title = "Cyber Matrix Stream",
            category = WidgetCategory.AMBIENT,
            description = "Glowing emerald rain of digital glyphs cascading behind a frosted glass overlay.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Ambient", "Matrix", "Cyber"),
            isPopular = false
        ),
        WidgetItem(
            id = "ambient_hologram_orb",
            title = "Holographic Plasma Orb",
            category = WidgetCategory.AMBIENT,
            description = "Translucent glowing sphere containing turbulent plasma arcs that follow device gravity.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 2, manualLiquidLevel = 0.50f),
            tags = listOf("Ambient", "Hologram", "Plasma"),
            isPopular = false
        ),
        WidgetItem(
            id = "ambient_color_waveform",
            title = "Calm Chromatic Waveform",
            category = WidgetCategory.AMBIENT,
            description = "Continuous gentle sine gradient wave that transitions through sunset color hues.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Ambient", "Wave", "Color"),
            isPopular = false
        ),
        WidgetItem(
            id = "ambient_lava_glow",
            title = "Bioluminescent Deep Sea",
            category = WidgetCategory.AMBIENT,
            description = "Floating glowing bioluminescent jellyfish spores on deep midnight glass.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Ambient", "Bioluminescent", "DeepSea"),
            isPopular = false
        ),

        // ==================== 14. CRYPTO & FINANCE (6 Widgets) ====================
        WidgetItem(
            id = "crypto_bitcoin_glass",
            title = "Bitcoin Glass Ticker",
            category = WidgetCategory.CRYPTO,
            description = "Real-time BTC price with 24h gain percentage, market dominance, and candle indicator.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Crypto", "Bitcoin", "Price"),
            isPopular = true
        ),
        WidgetItem(
            id = "crypto_ethereum_pulse",
            title = "Ethereum Pulse & Gas",
            category = WidgetCategory.CRYPTO,
            description = "ETH price tracker with live Gwei gas fee gauge and staking yield badge.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Crypto", "Ethereum", "Gas"),
            isPopular = true
        ),
        WidgetItem(
            id = "crypto_portfolio_sparkline",
            title = "Portfolio Sparkline Glass",
            category = WidgetCategory.CRYPTO,
            description = "Total net worth chart across Crypto, Stocks, and ETFs with daily profit/loss pill.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Crypto", "Portfolio", "Stocks"),
            isPopular = false
        ),
        WidgetItem(
            id = "crypto_currency_matrix",
            title = "Forex FX Exchange Matrix",
            category = WidgetCategory.CRYPTO,
            description = "Side-by-side exchange rate cards for USD, EUR, GBP, and JPY in frosted glass.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Crypto", "Forex", "Currency"),
            isPopular = false
        ),
        WidgetItem(
            id = "crypto_gold_reserve_vault",
            title = "Gold & Precious Metals",
            category = WidgetCategory.CRYPTO,
            description = "Tracks Gold, Silver, and Platinum spot prices per ounce with glowing amber cards.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Crypto", "Gold", "Metals"),
            isPopular = false
        ),
        WidgetItem(
            id = "crypto_fear_greed_index",
            title = "Crypto Fear & Greed Meter",
            category = WidgetCategory.CRYPTO,
            description = "Market sentiment speedometer ranging from Extreme Fear (0) to Extreme Greed (100).",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Crypto", "Sentiment", "Market"),
            isPopular = false
        ),

        // ==================== 15. MOON & ASTRONOMY (6 Widgets) ====================
        WidgetItem(
            id = "astronomy_lunar_phase_orbit",
            title = "Lunar Phase Orbit Glass",
            category = WidgetCategory.ASTRONOMY,
            description = "Accurate 3D moon rendering with illumination percentage, next full moon date, and tides.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Astronomy", "Moon", "Lunar"),
            isPopular = true
        ),
        WidgetItem(
            id = "astronomy_constellation_glass",
            title = "Constellation Stargaze Glass",
            category = WidgetCategory.ASTRONOMY,
            description = "Live celestial map highlighting visible constellations (Orion, Ursa Major) in tonight's sky.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Astronomy", "Stars", "Constellations"),
            isPopular = true
        ),
        WidgetItem(
            id = "astronomy_sunset_horizon_arc",
            title = "Twilight & Solar Horizon",
            category = WidgetCategory.ASTRONOMY,
            description = "Tracks Civil, Nautical, and Astronomical twilight hours along a curved glass spectrum.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Astronomy", "Sunset", "Twilight"),
            isPopular = false
        ),
        WidgetItem(
            id = "astronomy_iss_tracker_pod",
            title = "ISS Overhead Pass Pod",
            category = WidgetCategory.ASTRONOMY,
            description = "Countdown timer and trajectory map for when the International Space Station flies above.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Astronomy", "ISS", "Space"),
            isPopular = false
        ),
        WidgetItem(
            id = "astronomy_cosmic_zodiac",
            title = "Cosmic Zodiac Transit",
            category = WidgetCategory.ASTRONOMY,
            description = "Current astrological sun & rising sign transit positions on frosted starry glass.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Astronomy", "Zodiac", "Cosmic"),
            isPopular = false
        ),
        WidgetItem(
            id = "astronomy_planet_visibility",
            title = "Planetary Alignment Glass",
            category = WidgetCategory.ASTRONOMY,
            description = "Visual guide showing which planets (Jupiter, Mars, Saturn) are visible tonight.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Astronomy", "Planets", "Stargazing"),
            isPopular = false
        ),

        // ==================== 16. POMODORO & FOCUS (6 Widgets) ====================
        WidgetItem(
            id = "pomodoro_deep_work_sandglass",
            title = "Deep Work Sandglass",
            category = WidgetCategory.POMODORO,
            description = "Frosted hourglass filled with granular particles trickling down during 25min focus sprint.",
            sizeType = WidgetSizeType.SMALL_2x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 3, manualLiquidLevel = 0.40f),
            tags = listOf("Pomodoro", "Sandglass", "Focus"),
            isPopular = true
        ),
        WidgetItem(
            id = "pomodoro_flow_state_gauge",
            title = "Flow State Arc Timer",
            category = WidgetCategory.POMODORO,
            description = "Glowing circular focus gauge with Start/Pause touch controls and interval count.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Pomodoro", "Flow", "Timer"),
            isPopular = true
        ),
        WidgetItem(
            id = "pomodoro_interval_flask",
            title = "Interval Focus Flask",
            category = WidgetCategory.POMODORO,
            description = "Chemistry flask showing remaining work session time as draining luminescent elixir.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 1, manualLiquidLevel = 0.65f),
            tags = listOf("Pomodoro", "Flask", "Liquid"),
            isPopular = false
        ),
        WidgetItem(
            id = "pomodoro_rest_break_capsule",
            title = "5-Min Rest Break Capsule",
            category = WidgetCategory.POMODORO,
            description = "Restorative break prompt with breathing pacing ring and stretch reminder.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Pomodoro", "Break", "Rest"),
            isPopular = false
        ),
        WidgetItem(
            id = "pomodoro_daily_focus_tally",
            title = "Daily Focus Tally Glass",
            category = WidgetCategory.POMODORO,
            description = "Tracks total completed 25-minute Pomodoro blocks (e.g. 6/8 sessions completed).",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Pomodoro", "Tally", "Productivity"),
            isPopular = false
        ),
        WidgetItem(
            id = "pomodoro_audio_focus_pill",
            title = "Binaural Focus Beat Pill",
            category = WidgetCategory.POMODORO,
            description = "One-tap trigger for 40Hz Gamma waves or Brown noise during deep study.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Pomodoro", "Audio", "Binaural"),
            isPopular = false
        ),

        // ==================== 17. HABITS & MOOD (6 Widgets) ====================
        WidgetItem(
            id = "habits_streak_heatmap",
            title = "Daily Streak Heatmap",
            category = WidgetCategory.HABITS,
            description = "GitHub-styled frosted contribution squares tracking daily workout, reading, and meditation.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Habits", "Streak", "Heatmap"),
            isPopular = true
        ),
        WidgetItem(
            id = "habits_mood_emotion_ring",
            title = "Mood & Emotion Ring",
            category = WidgetCategory.HABITS,
            description = "Quick daily emotional check-in (Calm, Joy, Energized, Reflective) with chromatic aura.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Habits", "Mood", "Emotion"),
            isPopular = true
        ),
        WidgetItem(
            id = "habits_water_intake_log",
            title = "Water Intake Glass Log",
            category = WidgetCategory.HABITS,
            description = "Tap +250ml glass buttons to quickly log water glasses drunk throughout the day.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Habits", "Water", "Logging"),
            isPopular = false
        ),
        WidgetItem(
            id = "habits_sleep_quality_wave",
            title = "Sleep Quality Wave Glass",
            category = WidgetCategory.HABITS,
            description = "Deep sleep vs REM sleep stages curve with sleep score and wake-up target.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Habits", "Sleep", "Health"),
            isPopular = false
        ),
        WidgetItem(
            id = "habits_gratitude_jar",
            title = "Gratitude Jar Capsule",
            category = WidgetCategory.HABITS,
            description = "Tap to view a random positive memory or recorded gratitude moment from this month.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Habits", "Gratitude", "Mindful"),
            isPopular = false
        ),
        WidgetItem(
            id = "habits_daily_checklist_matrix",
            title = "3-Core Habits Matrix",
            category = WidgetCategory.HABITS,
            description = "Check off Read 20m, Drink 2L, and Walk 8k with tactile haptic check toggles.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Habits", "Checklist", "Matrix"),
            isPopular = false
        ),

        // ==================== 18. DYNAMIC ISLAND & STATUS PILLS (6 Widgets) ====================
        WidgetItem(
            id = "island_live_capsule",
            title = "Live Island Glass Capsule",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Interactive status pill showing ongoing timer, playback audio visualizer, and navigation arrow.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("DynamicIsland", "Capsule", "Status"),
            isPopular = true
        ),
        WidgetItem(
            id = "island_flight_alert_bar",
            title = "Flight Live Tracker Pill",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Real-time boarding countdown, gate number (B24), and terminal info in frosted pill.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("DynamicIsland", "Flight", "Travel"),
            isPopular = true
        ),
        WidgetItem(
            id = "island_delivery_tracker_pill",
            title = "Food Delivery Live Pill",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Courier ETA progress bar with animated scooter icon and order summary.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("DynamicIsland", "Delivery", "Food"),
            isPopular = false
        ),
        WidgetItem(
            id = "island_sports_live_score",
            title = "Sports Match Live Score",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Live scores for Champions League, NBA, or Premier League games in frosted glass.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("DynamicIsland", "Sports", "Scores"),
            isPopular = false
        ),
        WidgetItem(
            id = "island_airpod_battery_bar",
            title = "AirPod Battery Glass Pill",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Left Bud (85%), Right Bud (90%), and Case (65%) status in frosted capsule.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("DynamicIsland", "AirPods", "Bluetooth"),
            isPopular = false
        ),
        WidgetItem(
            id = "island_uber_ride_status",
            title = "Ride Share Arrival Pill",
            category = WidgetCategory.DYNAMIC_ISLAND,
            description = "Driver distance (3 mins away), car model, and license plate on dark glass pill.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("DynamicIsland", "Uber", "Ride"),
            isPopular = false
        ),

        // ==================== 19. NETWORK & CONNECTIVITY (6 Widgets) ====================
        WidgetItem(
            id = "network_wifi_signal_glass",
            title = "Wi-Fi 6 Signal Glass",
            category = WidgetCategory.NETWORK,
            description = "SSID name, frequency band (5GHz/6GHz), signal RSSI in dBm, and connection security.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Network", "WiFi", "Signal"),
            isPopular = true
        ),
        WidgetItem(
            id = "network_data_usage_tank",
            title = "Cellular Data Fluid Tank",
            category = WidgetCategory.NETWORK,
            description = "Monthly mobile data allowance (e.g. 18.4GB / 30GB) shown as a sloshing fluid container.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 2, manualLiquidLevel = 0.61f),
            tags = listOf("Network", "Data", "Fluid"),
            isPopular = true
        ),
        WidgetItem(
            id = "network_latency_ping_radar",
            title = "Ping & Latency Radar",
            category = WidgetCategory.NETWORK,
            description = "Live ping to Cloudflare 1.1.1.1 and Google DNS with packet jitter curve in ms.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Network", "Ping", "Latency"),
            isPopular = false
        ),
        WidgetItem(
            id = "network_vpn_shield_status",
            title = "VPN Shield Status Glass",
            category = WidgetCategory.NETWORK,
            description = "WireGuard / OpenVPN encryption status, virtual IP location, and kill switch indicator.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Network", "VPN", "Security"),
            isPopular = false
        ),
        WidgetItem(
            id = "network_bluetooth_mesh_pod",
            title = "Bluetooth Connected Mesh",
            category = WidgetCategory.NETWORK,
            description = "Shows all currently paired smart home devices, speakers, and peripherals.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Network", "Bluetooth", "Devices"),
            isPopular = false
        ),
        WidgetItem(
            id = "network_hotspot_speedo",
            title = "Hotspot Tethering Glass",
            category = WidgetCategory.NETWORK,
            description = "Monitors connected client devices and data shared through mobile hotspot.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Network", "Hotspot", "Tether"),
            isPopular = false
        ),

        // ==================== 20. MINI GAMES & TOYS (6 Widgets) ====================
        WidgetItem(
            id = "game_fidget_bubble_pop",
            title = "Fidget Bubble Pop Glass",
            category = WidgetCategory.MINI_GAMES,
            description = "Interactive silicone bubble sheet that pops with tactile haptic clicks when tapped.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Games", "Fidget", "Pop"),
            isPopular = true
        ),
        WidgetItem(
            id = "game_tilt_maze_ball",
            title = "Tilt Maze Sensor Ball",
            category = WidgetCategory.MINI_GAMES,
            description = "Real accelerometer-driven metal ball that rolls through a glass maze as you tilt your phone!",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 0, manualLiquidLevel = 0.50f),
            tags = listOf("Games", "Maze", "Sensor"),
            isPopular = true
        ),
        WidgetItem(
            id = "game_fortune_coin_flip",
            title = "Fortune Coin Flip Frost",
            category = WidgetCategory.MINI_GAMES,
            description = "Tap to spin a gold or silver coin with realistic 3D flipping physics and decision result.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Games", "Coin", "Decision"),
            isPopular = false
        ),
        WidgetItem(
            id = "game_polyhedral_dice_roller",
            title = "D20 RPG Dice Roller Glass",
            category = WidgetCategory.MINI_GAMES,
            description = "Shake phone or tap to roll a glowing D20, D6, or D100 with random critical hits.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Games", "Dice", "D20"),
            isPopular = false
        ),
        WidgetItem(
            id = "game_reaction_tap_speed",
            title = "Reaction Tap Reflex Test",
            category = WidgetCategory.MINI_GAMES,
            description = "Test your reflex speed in milliseconds when the glass flash turns green.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 1),
            tags = listOf("Games", "Reflex", "Speed"),
            isPopular = false
        ),
        WidgetItem(
            id = "game_magic_8_ball",
            title = "Magic 8-Ball Liquid Oracle",
            category = WidgetCategory.MINI_GAMES,
            description = "Ask a question and tilt or shake to reveal answers floating up from the dark blue liquid.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 8, manualLiquidLevel = 0.80f),
            tags = listOf("Games", "8Ball", "Oracle"),
            isPopular = false
        ),

        // ==================== 21. PHOTO & MEMORIES (6 Widgets) ====================
        WidgetItem(
            id = "photo_polaroid_frosted_frame",
            title = "Polaroid Frosted Frame",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Vintage Polaroid photo frame with frosted glass border and subtle warm vignette.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Photo", "Polaroid", "Memories"),
            isPopular = true
        ),
        WidgetItem(
            id = "photo_memory_stack_glass",
            title = "Memory Stack Carousel",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Interactive 3D photo stack showing highlights from 'On This Day 1 Year Ago'.",
            sizeType = WidgetSizeType.LARGE_4x4,
            defaultCustomization = WidgetCustomization(tintIndex = 2),
            tags = listOf("Photo", "Memories", "Carousel"),
            isPopular = true
        ),
        WidgetItem(
            id = "photo_ambient_spotlight",
            title = "Ambient Live Photo Spotlight",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Shuffles through selected favorite portraits with gentle parallax motion effect.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 5),
            tags = listOf("Photo", "Spotlight", "Parallax"),
            isPopular = false
        ),
        WidgetItem(
            id = "photo_duo_moodboard",
            title = "Duo Aesthetic Moodboard",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Split frosted glass displaying paired aesthetic wallpapers or couple photos.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 7),
            tags = listOf("Photo", "Moodboard", "Aesthetic"),
            isPopular = false
        ),
        WidgetItem(
            id = "photo_minimal_glass_frame",
            title = "Frameless Floating Glass Photo",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Edge-to-edge photo with translucent bottom title overlay and location stamp.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Photo", "Frameless", "Minimal"),
            isPopular = false
        ),
        WidgetItem(
            id = "photo_golden_album_pill",
            title = "Favorites Album Quick Pill",
            category = WidgetCategory.PHOTO_FRAME,
            description = "Tap to quickly launch and preview recently taken camera snapshots.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Photo", "Favorites", "Pill"),
            isPopular = false
        ),

        // ==================== 22. COMPASS & SENSORS (6 Widgets) ====================
        WidgetItem(
            id = "sensor_3d_gyro_compass",
            title = "3D Gyro Marine Compass",
            category = WidgetCategory.SENSORS,
            description = "Precision magnetic compass dial with degrees (0-360°), cardinal directions, and level bead.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 4),
            tags = listOf("Sensors", "Compass", "Gyro"),
            isPopular = true
        ),
        WidgetItem(
            id = "sensor_lux_light_meter",
            title = "Ambient Lux Light Meter",
            category = WidgetCategory.SENSORS,
            description = "Measures ambient room illuminance in Lux with recommended reading and eye-strain alerts.",
            sizeType = WidgetSizeType.SMALL_2x2,
            defaultCustomization = WidgetCustomization(tintIndex = 3),
            tags = listOf("Sensors", "Light", "Lux"),
            isPopular = false
        ),
        WidgetItem(
            id = "sensor_decibel_sound_wave",
            title = "Decibel Sound Meter Glass",
            category = WidgetCategory.SENSORS,
            description = "Real-time environmental noise meter measuring ambient decibels (dB) with hearing safety color coding.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            defaultCustomization = WidgetCustomization(tintIndex = 6),
            tags = listOf("Sensors", "Sound", "Decibel"),
            isPopular = true
        ),
        WidgetItem(
            id = "sensor_tilt_inclinometer",
            title = "Precision Tilt Inclinometer",
            category = WidgetCategory.SENSORS,
            description = "Dual-axis spirit level and surface slope bubble level for carpentry and alignment.",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true,
            defaultCustomization = WidgetCustomization(tintIndex = 1, manualLiquidLevel = 0.50f),
            tags = listOf("Sensors", "Level", "Tilt"),
            isPopular = true
        ),
        WidgetItem(
            id = "sensor_barometer_altimeter",
            title = "Barometric Altimeter Glass",
            category = WidgetCategory.SENSORS,
            description = "Calculates exact elevation above sea level in meters based on real-time atmospheric sensor data.",
            sizeType = WidgetSizeType.WIDE_4x1,
            defaultCustomization = WidgetCustomization(tintIndex = 0),
            tags = listOf("Sensors", "Altimeter", "Altitude"),
            isPopular = false
        ),
        WidgetItem(
            id = "sensor_magnetic_field_pod",
            title = "Electromagnetic Gauss Pod",
            category = WidgetCategory.SENSORS,
            description = "Measures surrounding magnetic field strength in microteslas (μT) with metal detector mode.",
            sizeType = WidgetSizeType.PILL_2x1,
            defaultCustomization = WidgetCustomization(tintIndex = 8),
            tags = listOf("Sensors", "Magnetic", "Gauss"),
            isPopular = false
        )
    )

    fun getWidgetsByCategory(category: WidgetCategory): List<WidgetItem> {
        return allWidgets.filter { it.category == category }
    }

    fun getPopularWidgets(): List<WidgetItem> {
        return allWidgets.filter { it.isPopular }
    }

    fun getLiquidWidgets(): List<WidgetItem> {
        return allWidgets.filter { it.isLiquid }
    }

    fun searchWidgets(query: String): List<WidgetItem> {
        if (query.isBlank()) return allWidgets
        val q = query.trim().lowercase()
        return allWidgets.filter {
            it.title.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.category.title.lowercase().contains(q) ||
            it.tags.any { tag -> tag.lowercase().contains(q) }
        }
    }
}
