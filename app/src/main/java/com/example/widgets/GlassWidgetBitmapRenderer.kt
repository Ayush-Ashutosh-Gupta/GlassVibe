package com.example.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.model.GlassTintPalettes
import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.model.WidgetSizeType
import com.example.utils.RealSystemDataProvider
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object GlassWidgetBitmapRenderer {

    private val smoothedLevelMap = java.util.concurrent.ConcurrentHashMap<String, Float>()

    fun renderWidget(
        context: Context,
        widget: WidgetItem,
        customization: WidgetCustomization,
        wavePhase: Float? = null
    ): Bitmap {
        val (width, height) = when (widget.sizeType) {
            WidgetSizeType.SMALL_2x2 -> 440 to 440
            WidgetSizeType.PILL_2x1 -> 440 to 220
            WidgetSizeType.WIDE_4x1 -> 720 to 220
            WidgetSizeType.LARGE_4x4 -> 720 to 720
            WidgetSizeType.MEDIUM_4x2 -> 720 to 360
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val tint = GlassTintPalettes.tints.getOrElse(customization.tintIndex) { GlassTintPalettes.tints[0] }
        val primaryTintInt = tint.primaryColor.toArgbInt()
        val glowTintInt = tint.glowColor.toArgbInt()
        val liquidTintInt = tint.liquidColor.toArgbInt()
        val liquidSecTintInt = tint.liquidSecondaryColor.toArgbInt()

        val cornerRadius = (customization.cornerRadiusDp * (width / 240f)).coerceIn(28f, 76f)
        val cardRect = RectF(6f, 6f, width - 6f, height - 6f)

        // Clip canvas inside rounded corner for container layers
        canvas.save()
        val clipPath = Path().apply {
            addRoundRect(cardRect, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        canvas.clipPath(clipPath)

        // 1. Ultra-Refined Frosted Smoked Glass Background
        val baseAlpha = customization.glassAlpha.coerceIn(0.12f, 0.85f)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val c1 = Color.argb(((baseAlpha + 0.18f) * 255).toInt().coerceIn(40, 240), Color.red(primaryTintInt), Color.green(primaryTintInt), Color.blue(primaryTintInt))
            val c2 = Color.argb((baseAlpha * 0.75f * 255).toInt().coerceIn(30, 210), 255, 255, 255)
            val c3 = Color.argb((baseAlpha * 0.45f * 255).toInt().coerceIn(20, 190), Color.red(glowTintInt), Color.green(glowTintInt), Color.blue(glowTintInt))
            val c4 = Color.argb(160, 11, 15, 25) // Deep Slate/Midnight backdrop
            shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(c1, c2, c3, c4), floatArrayOf(0f, 0.30f, 0.65f, 1f), Shader.TileMode.CLAMP)
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, bgPaint)

        // 2. Ambient Radial Glow Spotlight
        val glowAlpha = (customization.glowIntensity * 95).toInt().coerceIn(25, 180)
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val glowCenterColor = Color.argb(glowAlpha, Color.red(glowTintInt), Color.green(glowTintInt), Color.blue(glowTintInt))
            val glowEndColor = Color.argb(0, 0, 0, 0)
            shader = RadialGradient(
                width * 0.32f, height * 0.28f,
                width * 0.90f,
                glowCenterColor, glowEndColor,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, glowPaint)

        // 3. Dynamic Liquid Wave Layer (for liquid widgets, battery, or screen time)
        if (widget.isLiquid || widget.category == WidgetCategory.LIQUID) {
            val liveBattery = RealSystemDataProvider.getBatteryInfo(context)
            val liveScreenTime = RealSystemDataProvider.getScreenTimeInfo(context)

            val rawLiquidLevel = when (widget.id) {
                "liquid_battery_cell" -> (liveBattery.levelPercent / 100f).coerceIn(0.12f, 0.92f)
                "liquid_screen_time_beaker" -> (liveScreenTime.totalMinutesToday / 360f).coerceIn(0.15f, 0.92f)
                else -> customization.manualLiquidLevel.coerceIn(0.12f, 0.92f)
            }

            // Spring / decay-based interpolation logic to smooth out jitter during state transitions
            val prevLevel = smoothedLevelMap[widget.id] ?: rawLiquidLevel
            val smoothedLiquidLevel = prevLevel + (rawLiquidLevel - prevLevel) * 0.40f
            smoothedLevelMap[widget.id] = smoothedLiquidLevel

            // Calculate dynamic wave phase from time if not provided
            val nowMs = System.currentTimeMillis()
            val effectivePhase = wavePhase ?: (((nowMs % 100000L) / 1000f * 2.8f))
            val bubblePhase = ((nowMs % 8000L) / 8000f)

            drawDynamicLiquidWave(
                canvas = canvas,
                width = width,
                height = height,
                level = smoothedLiquidLevel,
                primaryLiquid = liquidTintInt,
                secondaryLiquid = liquidSecTintInt,
                wavePhase = effectivePhase,
                bubblePhase = bubblePhase,
                bounds = cardRect
            )
        }

        // 4. Content Specific Drawing (matching in-app WidgetRenderer components)
        drawWidgetContent(
            context = context,
            canvas = canvas,
            width = width,
            height = height,
            widget = widget,
            tintPrimary = primaryTintInt,
            tintGlow = glowTintInt,
            customization = customization
        )

        // 5. Specular Glass Refraction Sheen (Diagonal Highlight at 135 deg)
        val sheenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                0f, 0f,
                width * 0.80f, height * 0.60f,
                intArrayOf(
                    Color.argb(65, 255, 255, 255),
                    Color.argb(18, 255, 255, 255),
                    Color.argb(0, 255, 255, 255)
                ),
                floatArrayOf(0f, 0.40f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, sheenPaint)

        canvas.restore()

        // 6. Dual-Tone Specular Glass Border (High gloss top-left corner, ambient glow bottom-right)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (customization.borderWidthDp * (width / 300f)).coerceIn(2.5f, 5.5f)
            val borderAlpha = (customization.borderOpacity * 255).toInt().coerceIn(60, 250)
            val b1 = Color.argb(borderAlpha, 255, 255, 255)
            val b2 = Color.argb((borderAlpha * 0.90f).toInt(), Color.red(glowTintInt), Color.green(glowTintInt), Color.blue(glowTintInt))
            val b3 = Color.argb((borderAlpha * 0.30f).toInt(), 255, 255, 255)
            val b4 = Color.argb((borderAlpha * 0.50f).toInt(), Color.red(primaryTintInt), Color.green(primaryTintInt), Color.blue(primaryTintInt))
            shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(b1, b2, b3, b4), floatArrayOf(0f, 0.30f, 0.70f, 1f), Shader.TileMode.CLAMP)
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, borderPaint)

        return bitmap
    }

    private fun drawDynamicLiquidWave(
        canvas: Canvas,
        width: Int,
        height: Int,
        level: Float,
        primaryLiquid: Int,
        secondaryLiquid: Int,
        wavePhase: Float,
        bubblePhase: Float,
        bounds: RectF
    ) {
        val waterY = height * (1f - level.coerceIn(0.08f, 0.92f))
        val scaleFactor = width / 360f

        // Bottom Caustic Light Beams
        val causticPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val causticColor = Color.argb(45, Color.red(secondaryLiquid), Color.green(secondaryLiquid), Color.blue(secondaryLiquid))
            shader = RadialGradient(
                width * 0.5f, height.toFloat(),
                width * 0.65f,
                causticColor, Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, waterY, width.toFloat(), height.toFloat(), causticPaint)

        // Helper to evaluate harmonic wave height at normX with fluid damping factor
        fun evalWave(normX: Float, phase: Float, amp: Float): Float {
            // Viscous fluid spatial damping factor across vessel container (eliminates high-frequency jitter)
            val dampingFactor = kotlin.math.exp(-kotlin.math.abs(normX - 0.5f) * 0.50f)
            val w1 = sin((normX * 2.0 * PI + phase)).toFloat() * amp * dampingFactor
            val w2 = sin((normX * 4.0 * PI - phase * 0.6)).toFloat() * (amp * 0.20f) * (dampingFactor * dampingFactor)
            val meniscus = ((1f - normX).pow(4) + normX.pow(4)) * (3.8f * scaleFactor)
            return (waterY + w1 + w2 - meniscus).coerceIn(height * 0.05f, height * 0.95f)
        }

        // Helper to construct smooth cubic Bezier spline
        fun buildBezierWave(segments: Int, phase: Float, amp: Float): Pair<Path, Path> {
            val xs = FloatArray(segments + 1)
            val ys = FloatArray(segments + 1)
            for (i in 0..segments) {
                val normX = i.toFloat() / segments
                xs[i] = normX * width
                ys[i] = evalWave(normX, phase, amp)
            }

            val waveBody = Path()
            val crestLine = Path()

            waveBody.moveTo(0f, ys[0])
            crestLine.moveTo(xs[0], ys[0])

            for (i in 0 until segments) {
                val p0X = if (i > 0) xs[i - 1] else xs[i]
                val p0Y = if (i > 0) ys[i - 1] else ys[i]
                val p1X = xs[i]
                val p1Y = ys[i]
                val p2X = xs[i + 1]
                val p2Y = ys[i + 1]
                val p3X = if (i + 2 <= segments) xs[i + 2] else xs[i + 1]
                val p3Y = if (i + 2 <= segments) ys[i + 2] else ys[i + 1]

                val cp1X = p1X + (p2X - p0X) / 6f
                val cp1Y = p1Y + (p2Y - p0Y) / 6f
                val cp2X = p2X - (p3X - p1X) / 6f
                val cp2Y = p2Y - (p3Y - p1Y) / 6f

                waveBody.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2X, p2Y)
                crestLine.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2X, p2Y)
            }

            waveBody.lineTo(width.toFloat(), height.toFloat())
            waveBody.lineTo(0f, height.toFloat())
            waveBody.close()

            return waveBody to crestLine
        }

        // 1. Background Wave Layer (semi-translucent shifted phase +1.25 rad)
        val (bgWavePath, _) = buildBezierWave(8, wavePhase + 1.25f, 5.2f * scaleFactor)
        val bgLiquidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val alphaLiquid = 140
            val topColor = Color.argb(alphaLiquid, Color.red(secondaryLiquid), Color.green(secondaryLiquid), Color.blue(secondaryLiquid))
            val botColor = Color.argb(alphaLiquid + 45, Color.red(primaryLiquid), Color.green(primaryLiquid), Color.blue(primaryLiquid))
            shader = LinearGradient(0f, waterY, 0f, height.toFloat(), topColor, botColor, Shader.TileMode.CLAMP)
        }
        canvas.drawPath(bgWavePath, bgLiquidPaint)

        // 2. Primary Foreground Filled Wave Body (Smooth Cubic Spline with damping)
        val (fgWavePath, crestPath) = buildBezierWave(8, wavePhase, 7.2f * scaleFactor)
        val liquidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val topColor = Color.argb(225, Color.red(secondaryLiquid), Color.green(secondaryLiquid), Color.blue(secondaryLiquid))
            val botColor = Color.argb(255, Color.red(primaryLiquid), Color.green(primaryLiquid), Color.blue(primaryLiquid))
            shader = LinearGradient(0f, waterY, width.toFloat(), height.toFloat(), topColor, botColor, Shader.TileMode.CLAMP)
        }
        canvas.drawPath(fgWavePath, liquidPaint)

        // 3. Open Wave Crest Highlight Line & Bloom (Surface tension glow)
        // Crest soft bloom
        val crestBloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 7.0f * scaleFactor
            color = Color.argb(90, Color.red(secondaryLiquid), Color.green(secondaryLiquid), Color.blue(secondaryLiquid))
        }
        canvas.drawPath(crestPath, crestBloomPaint)

        // Crest sharp specular white line
        val crestPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3.2f * scaleFactor
            shader = LinearGradient(0f, 0f, width.toFloat(), 0f, intArrayOf(
                Color.argb(140, 255, 255, 255),
                Color.argb(255, 255, 255, 255),
                Color.argb(140, 255, 255, 255)
            ), null, Shader.TileMode.CLAMP)
        }
        canvas.drawPath(crestPath, crestPaint)

        // 4. Dynamic Animated Rising Bubbles with Specular Glints
        val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.argb(165, 255, 255, 255)
        }
        val bubbleHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.argb(245, 255, 255, 255)
        }

        val bubbleConfigs = listOf(
            // relX, baseRelY, radius, speed, horizontalSway
            floatArrayOf(0.22f, 0.85f, 5.5f, 0.45f, 0.08f),
            floatArrayOf(0.42f, 0.92f, 7.5f, 0.55f, -0.06f),
            floatArrayOf(0.62f, 0.78f, 4.5f, 0.65f, 0.10f),
            floatArrayOf(0.80f, 0.88f, 6.5f, 0.50f, -0.07f),
            floatArrayOf(0.32f, 0.65f, 4.0f, 0.60f, 0.05f),
            floatArrayOf(0.72f, 0.95f, 8.0f, 0.40f, 0.09f),
            floatArrayOf(0.15f, 0.70f, 3.5f, 0.70f, -0.05f)
        )

        val fluidDepth = (height - waterY).coerceAtLeast(10f)

        for (cfg in bubbleConfigs) {
            val relX = cfg[0]
            val baseRelY = cfg[1]
            val r = cfg[2] * scaleFactor
            val speed = cfg[3]
            val sway = cfg[4]

            val progress = (baseRelY - bubblePhase * speed + 2f) % 1f
            val bx = (relX + sin(progress * 2 * PI + relX * 10) * sway) * width
            val by = waterY + progress * fluidDepth

            if (by in (waterY + r)..(height - r)) {
                canvas.drawCircle(bx.toFloat(), by.toFloat(), r, bubblePaint)
                canvas.drawCircle((bx - r * 0.35f).toFloat(), (by - r * 0.35f).toFloat(), r * 0.32f, bubbleHighlightPaint)
            }
        }
    }

    private fun drawWidgetContent(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        widget: WidgetItem,
        tintPrimary: Int,
        tintGlow: Int,
        customization: WidgetCustomization
    ) {
        val pad = width * 0.075f

        when (widget.category) {
            WidgetCategory.LIQUID -> {
                drawLiquidWidget(context, canvas, width, height, widget, pad, tintGlow, customization)
            }

            WidgetCategory.CLOCKS, WidgetCategory.ASTRONOMY -> {
                drawClockWidget(context, canvas, width, height, widget, pad, tintGlow)
            }

            WidgetCategory.BATTERY -> {
                drawBatteryWidget(context, canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.WEATHER, WidgetCategory.SENSORS -> {
                drawWeatherWidget(canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.SCREEN_TIME, WidgetCategory.POMODORO, WidgetCategory.HABITS -> {
                drawScreenTimeWidget(context, canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.MUSIC, WidgetCategory.AMBIENT -> {
                drawMusicWidget(canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.CALENDAR, WidgetCategory.NOTES, WidgetCategory.QUOTES -> {
                drawCalendarWidget(context, canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.LAUNCHERS, WidgetCategory.DYNAMIC_ISLAND, WidgetCategory.MINI_GAMES, WidgetCategory.PHOTO_FRAME -> {
                drawLauncherWidget(canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.SYSTEM, WidgetCategory.NETWORK -> {
                drawSystemWidget(context, canvas, width, height, pad, tintGlow)
            }

            WidgetCategory.FITNESS, WidgetCategory.CRYPTO -> {
                drawFitnessWidget(canvas, width, height, pad, tintGlow)
            }
        }
    }

    private fun drawLiquidWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        widget: WidgetItem,
        pad: Float,
        tintGlow: Int,
        customization: WidgetCustomization
    ) {
        val liveBattery = RealSystemDataProvider.getBatteryInfo(context)
        val liveScreenTime = RealSystemDataProvider.getScreenTimeInfo(context)

        val levelPercent = when (widget.id) {
            "liquid_battery_cell" -> liveBattery.levelPercent
            "liquid_screen_time_beaker" -> ((liveScreenTime.totalMinutesToday / 360f) * 100).toInt().coerceIn(5, 100)
            else -> (customization.manualLiquidLevel * 100).toInt()
        }

        // Top-Left Icon capsule + Title (matches Compose LiquidWidgetContent)
        val iconRadius = width * 0.048f
        val iconBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(75, 255, 255, 255)
            style = Paint.Style.FILL
        }
        val iconCx = pad + iconRadius
        val iconCy = pad * 1.5f
        canvas.drawCircle(iconCx, iconCy, iconRadius, iconBgPaint)

        val flaskEmoji = when (widget.id) {
            "liquid_battery_cell" -> "⚡"
            "liquid_screen_time_beaker" -> "🧪"
            "liquid_mana_potion" -> "🔮"
            "liquid_lava_lamp" -> "🫧"
            "liquid_hydration_flask" -> "💧"
            "liquid_weather_tide" -> "🌊"
            else -> "🧪"
        }

        val flaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = iconRadius * 1.25f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(flaskEmoji, iconCx, iconCy + iconRadius * 0.40f, flaskPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.048f).coerceIn(18f, 34f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(widget.title, iconCx + iconRadius + 14f, iconCy + iconRadius * 0.35f, titlePaint)

        // Top-Right Live Flow Indicator Pill
        val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(80, 0, 0, 0)
            style = Paint.Style.FILL
        }
        val badgeRect = RectF(width - pad - 110f, pad * 0.9f, width - pad, pad * 2.1f)
        canvas.drawRoundRect(badgeRect, 14f, 14f, badgeBgPaint)

        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(103, 232, 249)
            textSize = (width * 0.032f).coerceIn(13f, 20f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("● FLOW", badgeRect.centerX(), badgeRect.centerY() + 5f, badgePaint)

        // Main Center Metric
        val mainText = when (widget.id) {
            "liquid_screen_time_beaker" -> "${liveScreenTime.formattedTime} today"
            "liquid_battery_cell" -> if (liveBattery.isCharging) "${liveBattery.levelPercent}% • Fast Charge" else "${liveBattery.levelPercent}% Capacity"
            "liquid_weather_tide" -> "2.4m High Tide"
            "liquid_mana_potion" -> "180/240 Mana"
            "liquid_lava_lamp" -> "${liveBattery.temperatureC}°C Core Temp"
            "liquid_hydration_flask" -> "1,750 / 2,500 ml"
            else -> "$levelPercent% Level"
        }

        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.075f).coerceIn(30f, 54f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(mainText, pad, height * 0.64f, mainPaint)

        // Bottom Subtitle
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(220, 226, 232, 240)
            textSize = (width * 0.038f).coerceIn(16f, 26f)
        }
        val subText = if (customization.useSensorPhysics) "Fluid Dynamics • Sensor Live" else "Liquid Physics • Tap to Slosh"
        canvas.drawText(subText, pad, height * 0.82f, subPaint)

        // Bottom Right Percentage Pill (Matches Compose circular badge)
        val pillR = (width * 0.078f).coerceIn(28f, 46f)
        val pillCx = width - pad - pillR
        val pillCy = height * 0.72f
        val pillBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(85, 255, 255, 255)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(pillCx, pillCy, pillR, pillBg)

        val pillBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawCircle(pillCx, pillCy, pillR, pillBorder)

        val pillTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (pillR * 0.75f).coerceIn(17f, 30f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("$levelPercent%", pillCx, pillCy + pillR * 0.32f, pillTextPaint)
    }

    private fun drawClockWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        widget: WidgetItem,
        pad: Float,
        tintGlow: Int
    ) {
        val clock = RealSystemDataProvider.getLiveClockInfo()

        when (widget.id) {
            "clock_tokyo_neon" -> {
                // Top-Left: TOKYO // LIVE
                val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    textSize = (width * 0.040f).coerceIn(16f, 28f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("TOKYO // LIVE", pad, pad * 1.5f, headerPaint)

                val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(200, 255, 255, 255)
                    textSize = (width * 0.038f).coerceIn(15f, 24f)
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText("24°C LIVE", width - pad, pad * 1.5f, rightPaint)

                // Large Monospace Time
                val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = (width * 0.16f).coerceIn(52f, 100f)
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                canvas.drawText(clock.tokyoTime, pad, height * 0.62f, timePaint)

                // Seconds in Glow Color
                val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    textSize = (width * 0.08f).coerceIn(28f, 50f)
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                val tokyoTimeWidth = timePaint.measureText(clock.tokyoTime)
                canvas.drawText(":${clock.localSeconds}", pad + tokyoTimeWidth + 10f, height * 0.62f, secPaint)

                // Subtitle: Timezone
                val zonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    textSize = (width * 0.038f).coerceIn(15f, 26f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("UTC+9 ASIA/TOKYO", pad, height * 0.82f, zonePaint)
            }

            "clock_world_dual_zone" -> {
                val colWidth = (width - 2 * pad) / 2f

                // Left: LOCAL
                val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(180, 255, 255, 255)
                    textSize = (width * 0.038f).coerceIn(15f, 24f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("LOCAL", pad, pad * 1.5f, labelPaint)

                val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = (width * 0.11f).coerceIn(36f, 70f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(clock.localTime, pad, height * 0.58f, timePaint)

                val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    textSize = (width * 0.036f).coerceIn(14f, 24f)
                }
                canvas.drawText(clock.localDate, pad, height * 0.78f, datePaint)

                // Divider Line
                val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(50, 255, 255, 255)
                    strokeWidth = 2f
                }
                canvas.drawLine(pad + colWidth, height * 0.20f, pad + colWidth, height * 0.80f, linePaint)

                // Right: LONDON
                val rightStart = pad + colWidth + 24f
                canvas.drawText("LONDON", rightStart, pad * 1.5f, labelPaint)
                canvas.drawText(clock.londonTime, rightStart, height * 0.58f, timePaint)
                canvas.drawText("GMT Zone", rightStart, height * 0.78f, datePaint)
            }

            "clock_typo_horizon" -> {
                // Large Typographic Horizon Time
                val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = (width * 0.18f).coerceIn(54f, 108f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(clock.localTime, pad, height * 0.52f, timePaint)

                // Glowing Accent Horizon Bar
                val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    strokeWidth = 4f
                }
                canvas.drawLine(pad, height * 0.62f, width - pad, height * 0.62f, barPaint)

                // Subtitle
                val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = (width * 0.040f).coerceIn(16f, 26f)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(clock.localDate, pad, height * 0.82f, datePaint)
            }

            else -> {
                // Analog Watch Dial (Bauhaus, Neumorphic, Chrono, Minimal)
                val cx = width / 2f
                val cy = height / 2f
                val radius = (Math.min(width, height) * 0.36f).coerceIn(60f, 220f)

                // 12 Dial Ticks
                for (i in 0 until 12) {
                    val angle = (i * 30.0) * PI / 180.0
                    val isMain = (i % 3 == 0)
                    val tickLen = if (isMain) radius * 0.20f else radius * 0.10f
                    val startX = (cx + (radius - tickLen) * sin(angle)).toFloat()
                    val startY = (cy - (radius - tickLen) * cos(angle)).toFloat()
                    val endX = (cx + radius * sin(angle)).toFloat()
                    val endY = (cy - radius * cos(angle)).toFloat()

                    val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = if (isMain) Color.WHITE else Color.argb(120, 255, 255, 255)
                        strokeWidth = if (isMain) 4f else 2f
                        strokeCap = Paint.Cap.ROUND
                    }
                    canvas.drawLine(startX, startY, endX, endY, tickPaint)
                }

                // Hour Hand
                val hourAngle = Math.toRadians(clock.hourAngle.toDouble())
                val hourLen = radius * 0.52f
                val hourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    strokeWidth = 7f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(cx, cy, (cx + hourLen * sin(hourAngle)).toFloat(), (cy - hourLen * cos(hourAngle)).toFloat(), hourPaint)

                // Minute Hand
                val minAngle = Math.toRadians(clock.minuteAngle.toDouble())
                val minLen = radius * 0.76f
                val minPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintGlow
                    strokeWidth = 4.5f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(cx, cy, (cx + minLen * sin(minAngle)).toFloat(), (cy - minLen * cos(minAngle)).toFloat(), minPaint)

                // Second Hand (Coral Red)
                val secAngle = Math.toRadians(clock.secondAngle.toDouble())
                val secLen = radius * 0.84f
                val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(255, 82, 82)
                    strokeWidth = 2.5f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(cx, cy, (cx + secLen * sin(secAngle)).toFloat(), (cy - secLen * cos(secAngle)).toFloat(), secPaint)

                // Center Pin Circle
                val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(cx, cy, 6f, pinPaint)

                // Date below center
                val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = (width * 0.034f).coerceIn(14f, 22f)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(clock.localDate, cx, height - pad * 0.8f, datePaint)
            }
        }
    }

    private fun drawBatteryWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val battery = RealSystemDataProvider.getBatteryInfo(context)

        // Top status
        val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val topStatus = if (battery.isCharging) "⚡ Fast Charging" else "Battery Health"
        canvas.drawText(topStatus, pad, pad * 1.5f, statusPaint)

        // Large Percentage
        val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.17f).coerceIn(52f, 98f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("${battery.levelPercent}%", pad, height * 0.58f, percentPaint)

        // Subtitle
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(16f, 26f)
        }
        canvas.drawText(if (battery.isCharging) "Charging (AC) • ${battery.temperatureC}°C" else "${battery.temperatureC}°C • ${battery.voltageMv} mV", pad, height * 0.74f, subPaint)

        // Horizontal Progress Bar
        val barY = height * 0.84f
        val barHeight = 9f
        val bgBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(40, 255, 255, 255)
            style = Paint.Style.FILL
        }
        val barWidth = width - 2 * pad
        canvas.drawRoundRect(RectF(pad, barY, pad + barWidth, barY + barHeight), 4f, 4f, bgBarPaint)

        val fillProgress = (battery.levelPercent / 100f).coerceIn(0.05f, 1f)
        val fillBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(pad, barY, pad + barWidth * fillProgress, barY, tintGlow, Color.rgb(52, 211, 153), Shader.TileMode.CLAMP)
        }
        canvas.drawRoundRect(RectF(pad, barY, pad + barWidth * fillProgress, barY + barHeight), 4f, 4f, fillBarPaint)
    }

    private fun drawWeatherWidget(
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val cityPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.046f).coerceIn(18f, 30f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("San Francisco", pad, pad * 1.5f, cityPaint)

        val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(251, 191, 36)
            textSize = (width * 0.06f).coerceIn(24f, 40f)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("☀️", width - pad, pad * 1.7f, rightPaint)

        // Big Temp
        val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.17f).coerceIn(52f, 98f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("72°", pad, height * 0.60f, tempPaint)

        // Subtitle
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Partly Sunny • H:76° L:58° • 💧 12%", pad, height * 0.80f, subPaint)
    }

    private fun drawScreenTimeWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val st = RealSystemDataProvider.getScreenTimeInfo(context)

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("SCREEN TIME", pad, pad * 1.5f, headerPaint)

        val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Top: ${st.topAppName}", width - pad, pad * 1.5f, rightPaint)

        // Main Time
        val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.13f).coerceIn(42f, 80f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(st.formattedTime, pad, height * 0.58f, timePaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(160, 255, 255, 255)
            textSize = (width * 0.036f).coerceIn(14f, 24f)
        }
        val timeW = timePaint.measureText(st.formattedTime)
        canvas.drawText("today", pad + timeW + 12f, height * 0.58f, subPaint)

        // 3-Segment Usage Bar
        val barY = height * 0.72f
        val barH = 8f
        val totalBarW = width - 2 * pad
        val socW = totalBarW * st.socialPercent
        val wrkW = totalBarW * st.workPercent
        val medW = totalBarW * st.mediaPercent

        val p1 = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = tintGlow }
        val p2 = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(244, 63, 94) }
        val p3 = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(245, 158, 11) }

        canvas.drawRoundRect(RectF(pad, barY, pad + socW, barY + barH), 3f, 3f, p1)
        canvas.drawRoundRect(RectF(pad + socW, barY, pad + socW + wrkW, barY + barH), 3f, 3f, p2)
        canvas.drawRoundRect(RectF(pad + socW + wrkW, barY, pad + totalBarW, barY + barH), 3f, 3f, p3)

        // Legend row
        val legPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 255, 255)
            textSize = (width * 0.032f).coerceIn(13f, 22f)
        }
        canvas.drawText("• Social ${(st.socialPercent * 100).toInt()}%  • Work ${(st.workPercent * 100).toInt()}%  • Media ${(st.mediaPercent * 100).toInt()}%", pad, height * 0.88f, legPaint)
    }

    private fun drawMusicWidget(
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        // Album art square
        val artSize = (height * 0.40f).coerceIn(40f, 90f)
        val artRect = RectF(pad, pad * 1.2f, pad + artSize, pad * 1.2f + artSize)
        val artPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(pad, pad, pad + artSize, pad + artSize, Color.rgb(99, 102, 241), Color.rgb(236, 72, 153), Shader.TileMode.CLAMP)
        }
        canvas.drawRoundRect(artRect, 12f, 12f, artPaint)

        // Music icon on album art
        val notePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = artSize * 0.5f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🎵", artRect.centerX(), artRect.centerY() + artSize * 0.18f, notePaint)

        // Title and Artist
        val textStart = pad + artSize + 16f
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.048f).coerceIn(18f, 32f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Midnight Mirage", textStart, pad * 1.8f, titlePaint)

        val artistPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
        }
        canvas.drawText("Cyberwave Orchestra", textStart, pad * 2.6f, artistPaint)

        // Progress bar
        val barY = height * 0.65f
        val barW = width - 2 * pad
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(40, 255, 255, 255) }
        canvas.drawRoundRect(RectF(pad, barY, pad + barW, barY + 6f), 3f, 3f, bgPaint)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = tintGlow }
        canvas.drawRoundRect(RectF(pad, barY, pad + barW * 0.55f, barY + 6f), 3f, 3f, fillPaint)

        // Media Controls: Rewind, Play, Next
        val ctrlY = height * 0.84f
        val ctrlPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.055f).coerceIn(20f, 36f)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("⏮", width * 0.35f, ctrlY, ctrlPaint)
        canvas.drawText("▶", width * 0.50f, ctrlY, ctrlPaint)
        canvas.drawText("⏭", width * 0.65f, ctrlY, ctrlPaint)
    }

    private fun drawCalendarWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val cal = RealSystemDataProvider.getCalendarInfo(context)

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("${cal.dayOfWeek.uppercase(Locale.getDefault())} • TODAY", pad, pad * 1.5f, headerPaint)

        val eventTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.055f).coerceIn(20f, 36f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(cal.title, pad, height * 0.55f, eventTitlePaint)

        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
        }
        canvas.drawText("${cal.timeRange} • ${cal.locationOrMeet}", pad, height * 0.74f, detailPaint)
    }

    private fun drawLauncherWidget(
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val icons = listOf("📷", "💬", "🎵", "🧭", "⚙️")
        val count = icons.size
        val itemW = (width - 2 * pad) / count
        val cy = height * 0.50f

        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = (width * 0.075f).coerceIn(26f, 48f)
            textAlign = Paint.Align.CENTER
        }
        val pillBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(50, 255, 255, 255)
            style = Paint.Style.FILL
        }

        icons.forEachIndexed { i, icon ->
            val cx = pad + itemW * (i + 0.5f)
            val pillRadius = (itemW * 0.38f).coerceIn(24f, 50f)
            canvas.drawCircle(cx, cy, pillRadius, pillBgPaint)
            canvas.drawText(icon, cx, cy + pillRadius * 0.35f, iconPaint)
        }
    }

    private fun drawSystemWidget(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val sys = RealSystemDataProvider.getSystemHardwareInfo(context)

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("SYSTEM HARDWARE", pad, pad * 1.5f, headerPaint)

        val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (sys.isOnline) Color.rgb(52, 211, 153) else Color.rgb(248, 113, 113)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(if (sys.isOnline) "● ONLINE" else "○ OFFLINE", width - pad, pad * 1.5f, rightPaint)

        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.12f).coerceIn(38f, 76f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("${sys.ramUsedGb}G / ${sys.ramTotalGb}G", pad, height * 0.60f, mainPaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
        }
        canvas.drawText("RAM Usage • ${sys.cpuCores} Cores Active", pad, height * 0.78f, subPaint)
    }

    private fun drawFitnessWidget(
        canvas: Canvas,
        width: Int,
        height: Int,
        pad: Float,
        tintGlow: Int
    ) {
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = tintGlow
            textSize = (width * 0.040f).coerceIn(16f, 26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("DAILY ACTIVITY", pad, pad * 1.5f, headerPaint)

        val stepsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (width * 0.12f).coerceIn(38f, 76f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("7,842 Steps", pad, height * 0.60f, stepsPaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = (width * 0.038f).coerceIn(15f, 24f)
        }
        canvas.drawText("🔥 540 kcal  •  📍 5.2 km  •  78% Goal", pad, height * 0.78f, subPaint)
    }

    private fun androidx.compose.ui.graphics.Color.toArgbInt(): Int {
        val a = (alpha * 255.0f + 0.5f).toInt()
        val r = (red * 255.0f + 0.5f).toInt()
        val g = (green * 255.0f + 0.5f).toInt()
        val b = (blue * 255.0f + 0.5f).toInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
