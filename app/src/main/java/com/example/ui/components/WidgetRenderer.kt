package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GlassTintPalettes
import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.model.WidgetSizeType
import com.example.physics.LiquidPhysicsState
import com.example.utils.HapticHelper
import com.example.utils.RealSystemDataState
import com.example.utils.rememberLiveSystemData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WidgetRenderer(
    widget: WidgetItem,
    customization: WidgetCustomization,
    physicsState: LiquidPhysicsState,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    realSystemData: RealSystemDataState? = null
) {
    val liveData = realSystemData ?: rememberLiveSystemData().value
    val tint = GlassTintPalettes.tints.getOrElse(customization.tintIndex) { GlassTintPalettes.tints[0] }

    val effectiveLiquidLevel = when (widget.id) {
        "liquid_battery_cell" -> liveData.battery.levelPercent / 100f
        "liquid_screen_time_beaker" -> (liveData.screenTime.totalMinutesToday / 360f).coerceIn(0.1f, 0.95f)
        else -> customization.manualLiquidLevel
    }

    GlassCard(
        modifier = modifier.testTag("widget_card_${widget.id}"),
        customization = customization
    ) {
        if (widget.isLiquid || widget.category == WidgetCategory.LIQUID) {
            // Liquid Canvas background layer with physical sensor sloshing
            LiquidPhysicsCanvas(
                customization = customization,
                physicsState = physicsState,
                fillLevel = effectiveLiquidLevel
            )
        }

        // Widget foreground content based on category and ID
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            when (widget.category) {
                WidgetCategory.LIQUID -> LiquidWidgetContent(widget, customization, physicsState, liveData)
                WidgetCategory.CLOCKS -> ClockWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.WEATHER -> WeatherWidgetContent(widget, tint.glowColor)
                WidgetCategory.SCREEN_TIME -> ScreenTimeWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.BATTERY -> BatteryWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.MUSIC -> MusicWidgetContent(widget, tint.glowColor, isInteractive)
                WidgetCategory.CALENDAR -> CalendarWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.LAUNCHERS -> LauncherWidgetContent(widget, tint.glowColor)
                WidgetCategory.FITNESS -> FitnessWidgetContent(widget, tint.glowColor)
                WidgetCategory.NOTES -> NotesWidgetContent(widget, tint.glowColor, isInteractive)
                WidgetCategory.SYSTEM -> SystemWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.QUOTES -> QuotesWidgetContent(widget, tint.glowColor)
                WidgetCategory.AMBIENT -> AmbientWidgetContent(widget, tint.glowColor)
                WidgetCategory.CRYPTO -> CryptoWidgetContent(widget, tint.glowColor)
                WidgetCategory.ASTRONOMY -> AstronomyWidgetContent(widget, tint.glowColor)
                WidgetCategory.POMODORO -> PomodoroWidgetContent(widget, tint.glowColor, isInteractive)
                WidgetCategory.HABITS -> HabitsWidgetContent(widget, tint.glowColor, isInteractive)
                WidgetCategory.DYNAMIC_ISLAND -> DynamicIslandWidgetContent(widget, tint.glowColor)
                WidgetCategory.NETWORK -> NetworkWidgetContent(widget, tint.glowColor, liveData)
                WidgetCategory.MINI_GAMES -> MiniGameWidgetContent(widget, tint.glowColor, isInteractive)
                WidgetCategory.PHOTO_FRAME -> PhotoFrameWidgetContent(widget, tint.glowColor)
                WidgetCategory.SENSORS -> SensorWidgetContent(widget, tint.glowColor, physicsState)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. LIQUID WIDGET CONTENT
// -------------------------------------------------------------
@Composable
fun LiquidWidgetContent(
    widget: WidgetItem,
    customization: WidgetCustomization,
    physicsState: LiquidPhysicsState,
    systemData: RealSystemDataState
) {
    val levelPercent = when (widget.id) {
        "liquid_battery_cell" -> systemData.battery.levelPercent
        "liquid_screen_time_beaker" -> ((systemData.screenTime.totalMinutesToday / 360f) * 100).toInt().coerceIn(5, 100)
        else -> (customization.manualLiquidLevel * 100).toInt()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Liquid Sensor",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = widget.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tilt angle badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (physicsState.isUpsideDown) "FLIPPED 180°" else "${physicsState.tiltAngleDeg.toInt()}° TILT",
                    color = if (physicsState.isUpsideDown) Color(0xFFFF5252) else Color(0xFF67E8F9),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Center telemetry
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = when (widget.id) {
                        "liquid_screen_time_beaker" -> "${systemData.screenTime.formattedTime} today"
                        "liquid_battery_cell" -> if (systemData.battery.isCharging) "${systemData.battery.levelPercent}% • Charging" else "${systemData.battery.levelPercent}% Level"
                        "liquid_weather_tide" -> "2.4m High Tide"
                        "liquid_mana_potion" -> "180/240 MP"
                        "liquid_lava_lamp" -> "${systemData.battery.temperatureC}°C Core"
                        "liquid_hydration_flask" -> "1,750 / 2,500 ml"
                        else -> "$levelPercent% Capacity"
                    },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (customization.useSensorPhysics) "Sensor Physics: Active" else "Manual Simulation",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$levelPercent%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CLOCKS & TIME
// -------------------------------------------------------------
@Composable
fun ClockWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val clock = systemData.clock

    when (widget.id) {
        "clock_tokyo_neon" -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOKYO // LIVE", color = glowColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("24°C LIVE", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = clock.tokyoTime,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ":${clock.localSeconds}",
                        color = glowColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text("UTC+9 ASIA/TOKYO", color = glowColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        "clock_world_dual_zone" -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("LOCAL", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(clock.localTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(clock.localDate, color = glowColor, fontSize = 10.sp, maxLines = 1)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.7f)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("LONDON", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(clock.londonTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("GMT Zone", color = glowColor, fontSize = 10.sp)
                }
            }
        }
        else -> {
            // Bauhaus / Neumorphic Analog Dial
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(100.dp)) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Dial ticks
                    for (i in 0 until 12) {
                        val angle = (i * 30.0) * PI / 180.0
                        val start = Offset(
                            (center.x + (radius - 12) * cos(angle)).toFloat(),
                            (center.y + (radius - 12) * sin(angle)).toFloat()
                        )
                        val end = Offset(
                            (center.x + radius * cos(angle)).toFloat(),
                            (center.y + radius * sin(angle)).toFloat()
                        )
                        drawLine(
                            color = if (i % 3 == 0) Color.White else Color.White.copy(alpha = 0.4f),
                            start = start,
                            end = end,
                            strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                        )
                    }

                    // Dynamic Hour hand
                    val hourAngle = Math.toRadians(clock.hourAngle.toDouble())
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(
                            (center.x + radius * 0.5 * sin(hourAngle)).toFloat(),
                            (center.y - radius * 0.5 * cos(hourAngle)).toFloat()
                        ),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Dynamic Minute hand
                    val minAngle = Math.toRadians(clock.minuteAngle.toDouble())
                    drawLine(
                        color = glowColor,
                        start = center,
                        end = Offset(
                            (center.x + radius * 0.75 * sin(minAngle)).toFloat(),
                            (center.y - radius * 0.75 * cos(minAngle)).toFloat()
                        ),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Second hand
                    val secAngle = Math.toRadians(clock.secondAngle.toDouble())
                    drawLine(
                        color = Color(0xFFFF5252),
                        start = center,
                        end = Offset(
                            (center.x + radius * 0.82 * sin(secAngle)).toFloat(),
                            (center.y - radius * 0.82 * cos(secAngle)).toFloat()
                        ),
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Center pivot
                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = center)
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(clock.localDate, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. WEATHER & ATMOSPHERE
// -------------------------------------------------------------
@Composable
fun WeatherWidgetContent(widget: WidgetItem, glowColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("San Francisco", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Partly Cloudy • Live", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(28.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "72°",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Column(horizontalAlignment = Alignment.End) {
                Text("H: 76°  L: 58°", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = glowColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("12% Rain", color = glowColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SCREEN TIME
// -------------------------------------------------------------
@Composable
fun ScreenTimeWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val st = systemData.screenTime

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SCREEN TIME", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Top: ${st.topAppName}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(st.formattedTime, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Text("today", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        // Usage category bars
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(modifier = Modifier.weight(st.socialPercent).fillMaxHeight().background(glowColor))
                Box(modifier = Modifier.weight(st.workPercent).fillMaxHeight().background(Color(0xFFF43F5E)))
                Box(modifier = Modifier.weight(st.mediaPercent).fillMaxHeight().background(Color(0xFFF59E0B)))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("• Social ${(st.socialPercent * 100).toInt()}%", color = glowColor, fontSize = 9.sp)
                Text("• Work ${(st.workPercent * 100).toInt()}%", color = Color(0xFFF43F5E), fontSize = 9.sp)
                Text("• Media ${(st.mediaPercent * 100).toInt()}%", color = Color(0xFFF59E0B), fontSize = 9.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 5. BATTERY & POWER
// -------------------------------------------------------------
@Composable
fun BatteryWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val b = systemData.battery

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (b.isCharging) "Charging" else "Healthy", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Icon(
                if (b.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.Speed,
                contentDescription = null,
                tint = if (b.isCharging) glowColor else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${b.levelPercent}%", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)

            Column(horizontalAlignment = Alignment.End) {
                Text(b.chargeStatusText, color = if (b.isCharging) glowColor else Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("${b.temperatureC}°C • ${b.voltageMv} mV", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            }
        }

        // Progress bar
        val progress = (b.levelPercent / 100f).coerceIn(0.05f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(Brush.horizontalGradient(listOf(glowColor, Color(0xFF34D399))))
            )
        }
    }
}

// -------------------------------------------------------------
// 6. MUSIC & MEDIA
// -------------------------------------------------------------
@Composable
fun MusicWidgetContent(widget: WidgetItem, glowColor: Color, isInteractive: Boolean) {
    var isPlaying by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Midnight Mirage", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                Text("Cyberwave Orchestra", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1)
            }
        }

        // Waveform / progress
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(modifier = Modifier.weight(0.55f).fillMaxHeight().background(glowColor))
            Box(modifier = Modifier.weight(0.45f).fillMaxHeight().background(Color.Transparent))
        }

        // Media controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.FastRewind,
                contentDescription = "Previous",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp).clickable(enabled = isInteractive) { HapticHelper.tick(context) }
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(glowColor)
                    .clickable(enabled = isInteractive) {
                        isPlaying = !isPlaying
                        HapticHelper.click(context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
            Icon(
                Icons.Default.FastForward,
                contentDescription = "Next",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp).clickable(enabled = isInteractive) { HapticHelper.tick(context) }
            )
        }
    }
}

// -------------------------------------------------------------
// 7. CALENDAR & AGENDA
// -------------------------------------------------------------
@Composable
fun CalendarWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val cal = systemData.calendar

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(cal.dayOfWeek.uppercase(), color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("in ${cal.startsInMinutes} mins", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }

        Column {
            Text(
                text = cal.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${cal.timeRange} • ${cal.locationOrMeet}",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(cal.fullDateStr, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// -------------------------------------------------------------
// 8. QUICK LAUNCHERS
// -------------------------------------------------------------
@Composable
fun LauncherWidgetContent(widget: WidgetItem, glowColor: Color) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val launcherIcons = listOf("📷", "💬", "🎵", "🧭", "⚙️")
        launcherIcons.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable { HapticHelper.click(context) },
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 18.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 9. FITNESS & STEPS
// -------------------------------------------------------------
@Composable
fun FitnessWidgetContent(widget: WidgetItem, glowColor: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = glowColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ACTIVITY", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("8,420", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("of 10,000 steps", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
                Text(" 480 kcal", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("6.2 km walked", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
    }
}

// -------------------------------------------------------------
// 10. NOTES & MEMOS
// -------------------------------------------------------------
@Composable
fun NotesWidgetContent(widget: WidgetItem, glowColor: Color, isInteractive: Boolean) {
    val items = remember {
        mutableStateListOf(
            "Ship GlassVibe Widgets" to true,
            "Calibrate gyro physics" to true,
            "Design custom frost styles" to false
        )
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("QUICK MEMO", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEachIndexed { index, (text, done) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isInteractive) {
                            items[index] = text to !done
                            HapticHelper.tick(context)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (done) glowColor else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = if (done) Color.White.copy(alpha = 0.5f) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (done) FontWeight.Normal else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 11. SYSTEM MONITORS
// -------------------------------------------------------------
@Composable
fun SystemWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val sys = systemData.systemHardware

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("${sys.cpuCores}C", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("CPU CORES", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("${sys.ramUsedGb}G", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("RAM / ${sys.ramTotalGb}G", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// 12. QUOTES & MANTRA
// -------------------------------------------------------------
@Composable
fun QuotesWidgetContent(widget: WidgetItem, glowColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = glowColor, modifier = Modifier.size(20.dp))
        Text(
            text = "“The soul becomes dyed with the color of its thoughts.”",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )
        Text("— Marcus Aurelius", color = glowColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// -------------------------------------------------------------
// 13. AMBIENT VISUALIZERS
// -------------------------------------------------------------
@Composable
fun AmbientWidgetContent(widget: WidgetItem, glowColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_rot")
    val rotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(12000, easing = LinearEasing)),
        label = "rot"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(70.dp).rotate(rotAngle)) {
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFEC4899),
                        Color(0xFF8B5CF6),
                        Color(0xFF06B6D4),
                        Color(0xFF10B981),
                        Color(0xFFEC4899)
                    )
                ),
                style = Stroke(width = 8.dp.toPx())
            )
        }
        Text("AURORA", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
    }
}

// -------------------------------------------------------------
// 14. CRYPTO & FINANCE
// -------------------------------------------------------------
@Composable
fun CryptoWidgetContent(widget: WidgetItem, glowColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("BTC / USD", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("+4.85%", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Text("$89,420", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("24h Vol: $32.4B", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}

// -------------------------------------------------------------
// 15. MOON & ASTRONOMY
// -------------------------------------------------------------
@Composable
fun AstronomyWidgetContent(widget: WidgetItem, glowColor: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("WAXING GIBBOUS", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("82% Illuminated", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Full Moon in 3 days", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
        Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color(0xFFE2E8F0), modifier = Modifier.size(36.dp))
    }
}

// -------------------------------------------------------------
// 16. POMODORO & FOCUS
// -------------------------------------------------------------
@Composable
fun PomodoroWidgetContent(widget: WidgetItem, glowColor: Color, isInteractive: Boolean) {
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("FLOW FOCUS", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("18:42", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("Session 3 of 4", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(glowColor)
                .clickable(enabled = isInteractive) {
                    isRunning = !isRunning
                    HapticHelper.click(context)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 17. HABITS & MOOD
// -------------------------------------------------------------
@Composable
fun HabitsWidgetContent(widget: WidgetItem, glowColor: Color, isInteractive: Boolean) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("14-DAY STREAK", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("🔥 Active", color = Color(0xFFFF9800), fontSize = 11.sp)
        }

        // Mini heatmap grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 7) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i < 5) glowColor else Color.White.copy(alpha = 0.2f))
                )
            }
        }

        Text("Hydration & Meditation Completed", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
    }
}

// -------------------------------------------------------------
// 18. DYNAMIC ISLAND & STATUS
// -------------------------------------------------------------
@Composable
fun DynamicIslandWidgetContent(widget: WidgetItem, glowColor: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Flight, contentDescription = null, tint = glowColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("UA 842 • SFO > HND", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Gate B24 • Boarding in 20m", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(glowColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("ON TIME", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
    }
}

// -------------------------------------------------------------
// 19. NETWORK
// -------------------------------------------------------------
@Composable
fun NetworkWidgetContent(widget: WidgetItem, glowColor: Color, systemData: RealSystemDataState) {
    val net = systemData.systemHardware
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Wifi, contentDescription = null, tint = if (net.isOnline) glowColor else Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(net.networkType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(if (net.isOnline) "Status: Online • Fast Link" else "Status: Disconnected", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(if (net.isOnline) "ONLINE" else "OFFLINE", color = if (net.isOnline) glowColor else Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// 20. MINI GAMES
// -------------------------------------------------------------
@Composable
fun MiniGameWidgetContent(widget: WidgetItem, glowColor: Color, isInteractive: Boolean) {
    val context = LocalContext.current
    val popped = remember { mutableStateListOf(false, false, false, false, false, false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("FIDGET POP", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (i in 0 until 6) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (popped[i]) Color.White.copy(alpha = 0.15f) else glowColor)
                        .clickable(enabled = isInteractive) {
                            popped[i] = !popped[i]
                            HapticHelper.splash(context)
                        }
                )
            }
        }

        Text("Tap bubbles to pop", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}

// -------------------------------------------------------------
// 21. PHOTO FRAME
// -------------------------------------------------------------
@Composable
fun PhotoFrameWidgetContent(widget: WidgetItem, glowColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF0F766E),
                        Color(0xFFF43F5E)
                    )
                )
            )
            .padding(10.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text("Golden Memories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Kyoto, Japan • 1 Year Ago", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
        }
    }
}

// -------------------------------------------------------------
// 22. COMPASS & SENSORS
// -------------------------------------------------------------
@Composable
fun SensorWidgetContent(widget: WidgetItem, glowColor: Color, physicsState: LiquidPhysicsState) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("3D COMPASS", color = glowColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            val heading = ((physicsState.tiltAngleDeg + 180) % 360).toInt()
            Text("$heading° NNW", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Elevation: 142m", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Speed,
                contentDescription = null,
                tint = glowColor,
                modifier = Modifier.size(28.dp).rotate(physicsState.tiltAngleDeg)
            )
        }
    }
}
