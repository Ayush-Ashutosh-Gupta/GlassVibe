package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GlassTintPalettes
import com.example.model.WidgetCustomization
import com.example.physics.LiquidPhysicsState
import com.example.physics.rememberLiquidPhysics
import com.example.ui.components.GlassCard
import com.example.ui.components.LiquidPhysicsCanvas
import com.example.utils.HapticHelper
import java.util.Locale

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.GlassmorphicPresetId
import com.example.ui.theme.ThemeManager

@Composable
fun LiquidLabScreen(
    physicsState: LiquidPhysicsState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentPreset by ThemeManager.currentPreset.collectAsStateWithLifecycle()
    var selectedVesselIndex by remember { mutableIntStateOf(0) }
    var selectedTintIndex by remember { mutableIntStateOf(currentPreset.tintIndex) }
    var fillLevel by remember { mutableFloatStateOf(0.65f) }
    var useSensor by remember { mutableStateOf(true) }
    var manualTilt by remember { mutableFloatStateOf(0f) }

    val activeLabPhysics by rememberLiquidPhysics(
        useSensor = useSensor,
        manualTiltDeg = manualTilt,
        manualFillLevel = fillLevel
    )
    val effectivePhysics = if (useSensor) physicsState else activeLabPhysics

    val vessels = listOf(
        "Screen Time Beaker" to "Tapered laboratory glass vessel tracking digital screen time",
        "Battery Fluid Core" to "High-tech cylindrical cell storing luminous power fluid",
        "Hydration Hydro Flask" to "Insulated water intake cylinder with volumetric lines",
        "Mystic Mana Potion" to "Spherical elixir orb with glowing magical particles"
    )

    val currentCustomization = WidgetCustomization(
        tintIndex = selectedTintIndex,
        glassAlpha = currentPreset.glassAlpha,
        borderOpacity = currentPreset.borderOpacity,
        borderWidthDp = 2.0f,
        cornerRadiusDp = if (selectedVesselIndex == 3) 40 else 24,
        manualLiquidLevel = fillLevel,
        manualTiltAngleDeg = manualTilt,
        useSensorPhysics = useSensor,
        glowIntensity = currentPreset.glowIntensity
    )

    val currentTint = GlassTintPalettes.tints.getOrElse(selectedTintIndex) { GlassTintPalettes.tints[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentPreset.backgroundGradient))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("liquid_lab_screen")
    ) {
        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("lab_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Liquid Physics Lab", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Hardware Sensor Slosh Engine", color = Color(0xFF34D399), fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (effectivePhysics.isUpsideDown) "FLIPPED 180°" else "${effectivePhysics.tiltAngleDeg.toInt()}° TILT",
                    color = if (effectivePhysics.isUpsideDown) Color(0xFFFF5252) else Color(0xFF34D399),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Scrollable Lab Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // ================= THE LIQUID PHYSICS SIMULATOR VESSEL =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.22f)
                    .heightIn(min = 220.dp, max = 330.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Glass Card containing the live sloshing liquid canvas
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight(0.86f),
                    customization = currentCustomization
                ) {
                    LiquidPhysicsCanvas(
                        customization = currentCustomization,
                        physicsState = effectivePhysics,
                        fillLevel = fillLevel
                    )

                    // Foreground Telemetry Overlay inside vessel
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = vessels[selectedVesselIndex].first,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = currentTint.glowColor, modifier = Modifier.size(18.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${(fillLevel * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (useSensor) "PHYSICAL GYROSCOPE ACTIVE" else "MANUAL TEST MODE",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Wave: ±${effectivePhysics.waveSloshAmplitude.toInt()}px", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text(
                                if (effectivePhysics.isUpsideDown) "Gravity: INVERTED" else "Gravity: NORMAL",
                                color = if (effectivePhysics.isUpsideDown) Color(0xFFFF5252) else currentTint.glowColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Fluid Action Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Splash Button
                Button(
                    onClick = {
                        HapticHelper.splash(context)
                        manualTilt = if (manualTilt == 0f) 18f else -manualTilt
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🌊 Splash & Ripple", fontSize = 12.sp, color = Color.White)
                }

                // Invert Gravity
                Button(
                    onClick = {
                        HapticHelper.splash(context)
                        manualTilt = if (manualTilt > 70f || manualTilt < -70f) 0f else 90f
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔄 Invert Gravity 180°", fontSize = 12.sp, color = Color(0xFF38BDF8))
                }

                // Tilt Left
                Button(
                    onClick = {
                        HapticHelper.click(context)
                        manualTilt = -30f
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("⬅️ Tilt Left -30°", fontSize = 12.sp, color = Color.White)
                }

                // Tilt Right
                Button(
                    onClick = {
                        HapticHelper.click(context)
                        manualTilt = 30f
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("➡️ Tilt Right +30°", fontSize = 12.sp, color = Color.White)
                }

                // Reset Level
                Button(
                    onClick = {
                        HapticHelper.click(context)
                        manualTilt = 0f
                        fillLevel = 0.65f
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🧪 Reset Equilibrium", fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glassmorphism Preset Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("GLASSMORPHISM PRESET", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                Text(currentPreset.name, color = currentPreset.accentGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeManager.presets.forEach { preset ->
                    val isSelected = currentPreset.id == preset.id
                    val iconEmoji = when (preset.id) {
                        GlassmorphicPresetId.FROSTED_MIDNIGHT -> "🌙"
                        GlassmorphicPresetId.OCEAN_DEPTH -> "🌊"
                        GlassmorphicPresetId.ELECTRIC_NEON -> "⚡"
                        GlassmorphicPresetId.CYBER_SUNSET -> "🌅"
                        GlassmorphicPresetId.EMERALD_AURORA -> "✨"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) preset.liquidPrimaryColor.copy(alpha = 0.40f) else Color(0xFF1E293B))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) preset.specularRimColor else Color(0xFF334155),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                HapticHelper.click(context)
                                ThemeManager.setPreset(preset.id)
                                selectedTintIndex = preset.tintIndex
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(iconEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = preset.name,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vessel Type Selector
            Text("CHOOSE LIQUID VESSEL", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vessels.forEachIndexed { index, (title, _) ->
                    val isSelected = selectedVesselIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) currentTint.primaryColor.copy(alpha = 0.3f) else Color(0xFF1E293B))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) currentTint.glowColor else Color(0xFF334155),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedVesselIndex = index
                                HapticHelper.click(context)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Tint Selector
            Text("FLUID PALETTE", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassTintPalettes.tints.forEachIndexed { index, tint ->
                    val isSelected = selectedTintIndex == index
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(tint.liquidColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedTintIndex = index
                                HapticHelper.click(context)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= SENSOR VS MANUAL SWITCH =================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Real Accelerometer Physics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Tilt phone physically to slosh & invert", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = useSensor,
                            onCheckedChange = {
                                useSensor = it
                                HapticHelper.click(context)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF38BDF8),
                                checkedTrackColor = Color(0xFF0284C7)
                            )
                        )
                    }

                    // Fill Level Slider
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fluid Fill Level", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        Text("${(fillLevel * 100).toInt()}%", color = currentTint.glowColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = fillLevel,
                        onValueChange = {
                            fillLevel = it
                            HapticHelper.tick(context)
                        },
                        valueRange = 0.05f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentTint.glowColor,
                            activeTrackColor = currentTint.primaryColor
                        )
                    )

                    // Manual tilt angle slider when manual mode is active
                    AnimatedVisibility(visible = !useSensor) {
                        Column {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Manual Tilt Angle (-90° to +90°)", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                Text("${manualTilt.toInt()}°", color = currentTint.glowColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = manualTilt,
                                onValueChange = {
                                    manualTilt = it
                                    HapticHelper.tick(context)
                                },
                                valueRange = -90f..90f,
                                colors = SliderDefaults.colors(
                                    thumbColor = currentTint.glowColor,
                                    activeTrackColor = currentTint.primaryColor
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
