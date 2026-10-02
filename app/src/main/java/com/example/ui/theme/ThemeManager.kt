package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GlassmorphicPresetId(val displayName: String) {
    FROSTED_MIDNIGHT("Frosted Midnight"),
    OCEAN_DEPTH("Ocean Depth"),
    ELECTRIC_NEON("Electric Neon"),
    CYBER_SUNSET("Cyber Sunset"),
    EMERALD_AURORA("Emerald Aurora")
}

data class GlassmorphicThemePreset(
    val id: GlassmorphicPresetId,
    val name: String,
    val description: String,
    val tintIndex: Int,
    val glassAlpha: Float,
    val borderOpacity: Float,
    val glowIntensity: Float,
    val backgroundGradient: List<Color>,
    val accentGlow: Color,
    val specularRimColor: Color,
    val liquidPrimaryColor: Color,
    val liquidSecondaryColor: Color
)

object ThemeManager {

    val presets = listOf(
        GlassmorphicThemePreset(
            id = GlassmorphicPresetId.FROSTED_MIDNIGHT,
            name = "Frosted Midnight",
            description = "Smoked slate glass with icy diamond specular highlights and deep sapphire liquid",
            tintIndex = 0, // Frosted Ice / Sapphire
            glassAlpha = 0.20f,
            borderOpacity = 0.70f,
            glowIntensity = 0.75f,
            backgroundGradient = listOf(
                Color(0xFF030712),
                Color(0xFF0F172A),
                Color(0xFF1E293B)
            ),
            accentGlow = Color(0xFF38BDF8),
            specularRimColor = Color(0xFFF8FAFC),
            liquidPrimaryColor = Color(0xFF0284C7),
            liquidSecondaryColor = Color(0xFF38BDF8)
        ),
        GlassmorphicThemePreset(
            id = GlassmorphicPresetId.OCEAN_DEPTH,
            name = "Ocean Depth",
            description = "Abyssal marine frosted glass with bioluminescent cyan caustics and aquamarine flow",
            tintIndex = 4, // Cyber Cyan
            glassAlpha = 0.22f,
            borderOpacity = 0.65f,
            glowIntensity = 0.85f,
            backgroundGradient = listOf(
                Color(0xFF021B2B),
                Color(0xFF042F2E),
                Color(0xFF0B192C)
            ),
            accentGlow = Color(0xFF22D3EE),
            specularRimColor = Color(0xFF67E8F9),
            liquidPrimaryColor = Color(0xFF0891B2),
            liquidSecondaryColor = Color(0xFF67E8F9)
        ),
        GlassmorphicThemePreset(
            id = GlassmorphicPresetId.ELECTRIC_NEON,
            name = "Electric Neon",
            description = "High-voltage ultraviolet and cyber magenta glass with radiant electric bloom",
            tintIndex = 2, // Cyber Amethyst
            glassAlpha = 0.24f,
            borderOpacity = 0.75f,
            glowIntensity = 0.90f,
            backgroundGradient = listOf(
                Color(0xFF18032B),
                Color(0xFF3B0764),
                Color(0xFF0F172A)
            ),
            accentGlow = Color(0xFFA78BFA),
            specularRimColor = Color(0xFFF472B6),
            liquidPrimaryColor = Color(0xFF7C3AED),
            liquidSecondaryColor = Color(0xFFC084FC)
        ),
        GlassmorphicThemePreset(
            id = GlassmorphicPresetId.CYBER_SUNSET,
            name = "Cyber Sunset",
            description = "Warm amber and twilight coral frosted glass with vibrant horizon glow",
            tintIndex = 6, // Sunset Coral
            glassAlpha = 0.22f,
            borderOpacity = 0.65f,
            glowIntensity = 0.80f,
            backgroundGradient = listOf(
                Color(0xFF1C0A00),
                Color(0xFF3A1010),
                Color(0xFF0F172A)
            ),
            accentGlow = Color(0xFFFF8A65),
            specularRimColor = Color(0xFFFFCCBC),
            liquidPrimaryColor = Color(0xFFE64A19),
            liquidSecondaryColor = Color(0xFFFFAB91)
        ),
        GlassmorphicThemePreset(
            id = GlassmorphicPresetId.EMERALD_AURORA,
            name = "Emerald Aurora",
            description = "Luminous jade and neon mint glass with cosmic polar particle highlights",
            tintIndex = 1, // Emerald Neon
            glassAlpha = 0.20f,
            borderOpacity = 0.70f,
            glowIntensity = 0.85f,
            backgroundGradient = listOf(
                Color(0xFF022C22),
                Color(0xFF064E3B),
                Color(0xFF030712)
            ),
            accentGlow = Color(0xFF34D399),
            specularRimColor = Color(0xFFA7F3D0),
            liquidPrimaryColor = Color(0xFF059669),
            liquidSecondaryColor = Color(0xFF10B981)
        )
    )

    private val _currentPreset = MutableStateFlow(presets[0])
    val currentPreset: StateFlow<GlassmorphicThemePreset> = _currentPreset.asStateFlow()

    fun setPreset(id: GlassmorphicPresetId) {
        val found = presets.find { it.id == id } ?: presets[0]
        _currentPreset.value = found
    }

    fun setPresetByName(name: String) {
        val found = presets.find { it.name.equals(name, ignoreCase = true) } ?: presets[0]
        _currentPreset.value = found
    }
}
