package com.example.ui.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.GlassTintPalettes
import com.example.model.WallpaperPresets
import com.example.model.WidgetCustomization

/**
 * Custom Modifier applying an ultra-refined glassmorphic effect:
 * - Blurred backgrounds with optical diffusion
 * - Semi-transparent frosted gradient overlays
 * - Ambient radial spotlight back-glow
 * - Specular diagonal refraction sheen
 * - Refined dual-tone edge highlight border
 */
fun Modifier.glassmorphic(
    cornerRadius: Dp = 24.dp,
    tintPrimary: Color = Color(0xFF0284C7),
    tintGlow: Color = Color(0xFF38BDF8),
    glassAlpha: Float = 0.22f,
    borderAlpha: Float = 0.60f,
    borderWidth: Dp = 1.5.dp,
    glowIntensity: Float = 0.65f,
    elevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(cornerRadius)
): Modifier {
    val baseAlpha = glassAlpha.coerceIn(0.08f, 0.85f)
    val bAlpha = borderAlpha.coerceIn(0.12f, 0.95f)
    val glowAlpha = (glowIntensity * 0.35f).coerceIn(0f, 0.65f)

    // Frosted glass background brush
    val glassBgBrush = Brush.linearGradient(
        colors = listOf(
            tintPrimary.copy(alpha = (baseAlpha + 0.14f).coerceAtMost(0.92f)),
            Color.White.copy(alpha = baseAlpha * 0.55f),
            tintGlow.copy(alpha = baseAlpha * 0.35f),
            Color(0xFF030712).copy(alpha = 0.40f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Refined edge highlight border brush (specular gloss top-left corner to ambient rim)
    val edgeHighlightBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = bAlpha),
            tintGlow.copy(alpha = bAlpha * 0.85f),
            Color.White.copy(alpha = bAlpha * 0.25f),
            tintPrimary.copy(alpha = bAlpha * 0.50f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = tintGlow.copy(alpha = glowAlpha),
            spotColor = tintPrimary.copy(alpha = glowAlpha)
        )
        .clip(shape)
        .drawBehind {
            // Ambient back-glow ring
            if (glowAlpha > 0.05f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tintGlow.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        radius = size.maxDimension * 0.70f
                    )
                )
            }

            // Semi-transparent frosted glass gradient overlay
            drawRect(brush = glassBgBrush)

            // Specular diagonal refraction sheen (Apple Vision / iOS glass highlight)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.24f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.85f, size.height * 0.65f)
                )
            )
        }
        .border(
            width = borderWidth,
            brush = edgeHighlightBrush,
            shape = shape
        )
}

/**
 * Reusable Composable that encapsulates glassmorphic aesthetics.
 */
@Composable
fun GlassmorphicBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    tintPrimary: Color = Color(0xFF0284C7),
    tintGlow: Color = Color(0xFF38BDF8),
    glassAlpha: Float = 0.22f,
    borderAlpha: Float = 0.60f,
    borderWidth: Dp = 1.5.dp,
    glowIntensity: Float = 0.65f,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "glassmorphic_press_scale"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .glassmorphic(
                cornerRadius = cornerRadius,
                tintPrimary = tintPrimary,
                tintGlow = tintGlow,
                glassAlpha = glassAlpha,
                borderAlpha = borderAlpha,
                borderWidth = borderWidth,
                glowIntensity = glowIntensity,
                elevation = elevation
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = tintGlow),
                        onClick = onClick
                    )
                } else Modifier
            ),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    customization: WidgetCustomization = WidgetCustomization(),
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = customization.cornerRadiusDp.dp,
    showWallpaperInside: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val tint = GlassTintPalettes.tints.getOrElse(customization.tintIndex) { GlassTintPalettes.tints[0] }
    val shape = RoundedCornerShape(cornerRadius)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "glass_press_scale"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .glassmorphic(
                cornerRadius = cornerRadius,
                tintPrimary = tint.primaryColor,
                tintGlow = tint.glowColor,
                glassAlpha = customization.glassAlpha,
                borderAlpha = customization.borderOpacity,
                borderWidth = customization.borderWidthDp.dp,
                glowIntensity = customization.glowIntensity,
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = tint.glowColor),
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        if (showWallpaperInside) {
            val wallpaper = WallpaperPresets.wallpapers.getOrElse(customization.wallpaperIndex) { WallpaperPresets.wallpapers[0] }
            if (wallpaper.resId != null) {
                Image(
                    painter = painterResource(id = wallpaper.resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (wallpaper.gradientColors.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(wallpaper.gradientColors))
                )
            }
        }

        content()
    }
}
