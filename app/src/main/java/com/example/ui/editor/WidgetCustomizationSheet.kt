package com.example.ui.editor

import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GlassTintPalettes
import com.example.model.WallpaperPresets
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.model.WidgetSizeType
import com.example.physics.LiquidPhysicsState
import com.example.ui.components.WidgetRenderer
import com.example.utils.HapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetCustomizationSheet(
    widget: WidgetItem,
    customization: WidgetCustomization,
    physicsState: LiquidPhysicsState,
    onCustomizationChange: (WidgetCustomization) -> Unit,
    onDismiss: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onPinWidget: (WidgetItem, WidgetCustomization) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copiedFeedback by remember { mutableStateOf(false) }

    val wallpapers = WallpaperPresets.wallpapers

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0B0F19),
        dragHandle = null,
        modifier = Modifier.testTag("customization_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(widget.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(widget.category.title + " • " + widget.sizeType.label, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            onToggleFavorite()
                            HapticHelper.click(context)
                        },
                        modifier = Modifier.testTag("favorite_button")
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_sheet_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // ================= LIVE STAGE PREVIEW =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Selected Wallpaper Background
                    val selectedWp = wallpapers.getOrElse(customization.wallpaperIndex) { wallpapers[0] }
                    if (selectedWp.resId != null) {
                        Image(
                            painter = painterResource(id = selectedWp.resId),
                            contentDescription = "Wallpaper Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (selectedWp.gradientColors.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(selectedWp.gradientColors))
                        )
                    }

                    // Ambient dimming overlay
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))

                    // Live Rendered Glassmorphic Widget
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(
                                when (widget.sizeType) {
                                    WidgetSizeType.SMALL_2x2 -> 130.dp
                                    WidgetSizeType.MEDIUM_4x2 -> 140.dp
                                    WidgetSizeType.LARGE_4x4 -> 180.dp
                                    WidgetSizeType.WIDE_4x1 -> 90.dp
                                    WidgetSizeType.PILL_2x1 -> 75.dp
                                }
                            )
                    ) {
                        WidgetRenderer(
                            widget = widget,
                            customization = customization,
                            physicsState = physicsState,
                            isInteractive = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallpaper switcher row
                Text("BACKDROP WALLPAPER", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    wallpapers.forEachIndexed { index, wpItem ->
                        val isSelected = customization.wallpaperIndex == index
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onCustomizationChange(customization.copy(wallpaperIndex = index))
                                    HapticHelper.tick(context)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (wpItem.resId != null) {
                                Image(
                                    painter = painterResource(id = wpItem.resId),
                                    contentDescription = wpItem.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (wpItem.gradientColors.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(wpItem.gradientColors))
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = if (isSelected) 0.25f else 0.55f))
                            )
                            Text(
                                wpItem.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ================= COLOR TINT PALETTE =================
                Text("GLASS COLOR TINT", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassTintPalettes.tints.forEachIndexed { index, tint ->
                        val isSelected = customization.tintIndex == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onCustomizationChange(customization.copy(tintIndex = index))
                                HapticHelper.click(context)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(tint.primaryColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tint.name,
                                color = if (isSelected) Color.White else Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ================= SLIDERS & PARAMETERS =================
                Text("GLASSMORPHISM CONTROLS", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Glass Translucency / Alpha
                CustomSliderRow(
                    label = "Translucency",
                    value = customization.glassAlpha,
                    valueRange = 0.08f..0.60f,
                    displayValue = "${(customization.glassAlpha * 100).toInt()}%",
                    onValueChange = {
                        onCustomizationChange(customization.copy(glassAlpha = it))
                        HapticHelper.tick(context)
                    }
                )

                // 2. Border Specular Opacity
                CustomSliderRow(
                    label = "Border Reflection",
                    value = customization.borderOpacity,
                    valueRange = 0.10f..0.95f,
                    displayValue = "${(customization.borderOpacity * 100).toInt()}%",
                    onValueChange = {
                        onCustomizationChange(customization.copy(borderOpacity = it))
                        HapticHelper.tick(context)
                    }
                )

                // 3. Border Stroke Width
                CustomSliderRow(
                    label = "Border Width",
                    value = customization.borderWidthDp,
                    valueRange = 0.5f..4.0f,
                    displayValue = "${String.format(Locale.US, "%.1f", customization.borderWidthDp)} dp",
                    onValueChange = {
                        onCustomizationChange(customization.copy(borderWidthDp = it))
                        HapticHelper.tick(context)
                    }
                )

                // 4. Corner Radius
                CustomSliderRow(
                    label = "Corner Radius",
                    value = customization.cornerRadiusDp.toFloat(),
                    valueRange = 12f..36f,
                    displayValue = "${customization.cornerRadiusDp} dp",
                    onValueChange = {
                        onCustomizationChange(customization.copy(cornerRadiusDp = it.toInt()))
                        HapticHelper.tick(context)
                    }
                )

                // 5. Glow Halo Intensity
                CustomSliderRow(
                    label = "Ambient Glow Halo",
                    value = customization.glowIntensity,
                    valueRange = 0f..1f,
                    displayValue = "${(customization.glowIntensity * 100).toInt()}%",
                    onValueChange = {
                        onCustomizationChange(customization.copy(glowIntensity = it))
                        HapticHelper.tick(context)
                    }
                )

                // ================= LIQUID PHYSICS SPECIFIC SETTINGS =================
                if (widget.isLiquid) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF131C31))
                            .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF38BDF8))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Sensor Gyro Physics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Slosh with physical phone movement", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                }
                                Switch(
                                    checked = customization.useSensorPhysics,
                                    onCheckedChange = {
                                        onCustomizationChange(customization.copy(useSensorPhysics = it))
                                        HapticHelper.click(context)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF38BDF8),
                                        checkedTrackColor = Color(0xFF0284C7)
                                    )
                                )
                            }

                            // Manual test sliders when sensor is off or for testing
                            AnimatedVisibility(visible = !customization.useSensorPhysics) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    CustomSliderRow(
                                        label = "Manual Liquid Level",
                                        value = customization.manualLiquidLevel,
                                        valueRange = 0.05f..0.95f,
                                        displayValue = "${(customization.manualLiquidLevel * 100).toInt()}%",
                                        onValueChange = {
                                            onCustomizationChange(customization.copy(manualLiquidLevel = it))
                                            HapticHelper.tick(context)
                                        }
                                    )

                                    CustomSliderRow(
                                        label = "Manual Tilt Angle",
                                        value = customization.manualTiltAngleDeg,
                                        valueRange = -90f..90f,
                                        displayValue = "${customization.manualTiltAngleDeg.toInt()}°",
                                        onValueChange = {
                                            onCustomizationChange(customization.copy(manualTiltAngleDeg = it))
                                            HapticHelper.tick(context)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Action Bar
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 20.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Full Width Ultra-Prominent "Add to Home Screen" Button (Zero Cut-Off)
                    Button(
                        onClick = {
                            onPinWidget(widget, customization)
                            HapticHelper.splash(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("pin_widget_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.AddHome,
                                contentDescription = "Add to Home Screen",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Add to Home Screen",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Secondary Action Row: Copy Code and Reset Preset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val codeSnippet = """
                                    // GlassVibe Compose Widget: ${widget.title}
                                    GlassCard(
                                        customization = WidgetCustomization(
                                            glassAlpha = ${customization.glassAlpha}f,
                                            borderOpacity = ${customization.borderOpacity}f,
                                            borderWidthDp = ${customization.borderWidthDp}f,
                                            tintIndex = ${customization.tintIndex},
                                            cornerRadiusDp = ${customization.cornerRadiusDp}
                                        )
                                    ) {
                                        // Widget Content
                                    }
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(codeSnippet))
                                copiedFeedback = true
                                HapticHelper.click(context)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF94A3B8)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("copy_code_button")
                        ) {
                            Icon(
                                if (copiedFeedback) Icons.Default.Check else Icons.Default.Code,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (copiedFeedback) Color(0xFF10B981) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (copiedFeedback) "Copied!" else "Copy Code",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onCustomizationChange(WidgetCustomization())
                                HapticHelper.click(context)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF94A3B8)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(displayValue, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF38BDF8),
                activeTrackColor = Color(0xFF0284C7),
                inactiveTrackColor = Color(0xFF334155)
            )
        )
    }
}
