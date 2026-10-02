package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.model.GlassTintPalettes
import com.example.model.WidgetCustomization
import com.example.physics.LiquidPhysicsState
import com.example.utils.HapticHelper
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

data class BubbleParticle(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val speed: Float,
    val phase: Float,
    val swayAmp: Float
)

@Composable
fun LiquidPhysicsCanvas(
    modifier: Modifier = Modifier,
    customization: WidgetCustomization = WidgetCustomization(),
    physicsState: LiquidPhysicsState = LiquidPhysicsState(),
    fillLevel: Float = customization.manualLiquidLevel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tint = GlassTintPalettes.tints.getOrElse(customization.tintIndex) { GlassTintPalettes.tints[0] }

    val bubbles = remember {
        listOf(
            BubbleParticle(relX = 0.22f, relY = 0.85f, radius = 4.2f, speed = 0.38f, phase = 0.12f, swayAmp = 6f),
            BubbleParticle(relX = 0.42f, relY = 0.92f, radius = 5.8f, speed = 0.45f, phase = 0.38f, swayAmp = 8f),
            BubbleParticle(relX = 0.68f, relY = 0.78f, radius = 3.6f, speed = 0.52f, phase = 0.72f, swayAmp = 5f),
            BubbleParticle(relX = 0.32f, relY = 0.65f, radius = 4.5f, speed = 0.36f, phase = 0.54f, swayAmp = 7f),
            BubbleParticle(relX = 0.80f, relY = 0.88f, radius = 5.0f, speed = 0.42f, phase = 0.90f, swayAmp = 6f),
            BubbleParticle(relX = 0.55f, relY = 0.95f, radius = 6.2f, speed = 0.32f, phase = 0.25f, swayAmp = 9f),
            BubbleParticle(relX = 0.15f, relY = 0.72f, radius = 3.0f, speed = 0.58f, phase = 0.42f, swayAmp = 4f)
        )
    }

    // Interactive drag tilt with Spring return
    val touchTilt = remember { Animatable(0f) }

    val targetTilt = if (customization.useSensorPhysics) {
        physicsState.tiltAngleDeg
    } else {
        customization.manualTiltAngleDeg
    }

    val isUpsideDown = if (customization.useSensorPhysics) {
        physicsState.isUpsideDown
    } else {
        customization.manualTiltAngleDeg < -70f || customization.manualTiltAngleDeg > 70f
    }

    // 1. Fluid tilt angle transition with responsive damping
    val totalTargetTilt = (targetTilt + touchTilt.value).coerceIn(-42f, 42f)
    val animatedTilt by animateFloatAsState(
        targetValue = totalTargetTilt,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "FluidTiltSpring"
    )

    // 2. Fluid level state transition
    val animatedFillLevel by animateFloatAsState(
        targetValue = fillLevel.coerceIn(0.10f, 0.90f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FluidFillLevelSpring"
    )

    // 3. Smooth decaying slosh impulse managed via Animatable for zero GC and no recomposition storm
    val sloshAnim = remember { Animatable(0f) }

    LaunchedEffect(physicsState.sloshImpulse) {
        if (physicsState.sloshImpulse > 0.35f) {
            val boost = (physicsState.sloshImpulse * 0.75f).coerceIn(4f, 24f)
            sloshAnim.snapTo(boost)
            sloshAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1100, easing = LinearEasing)
            )
        }
    }

    // 4. Fast, highly visible, and buttery-smooth infinite wave transitions (60 FPS fluid dynamic flow)
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidInfiniteTransition")

    // Fast and lively primary wave progression
    val primaryWavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1550, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FluidPrimaryWavePhase"
    )

    // Smooth secondary harmonic wave
    val secondaryWavePhase by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = (1.2f + 2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FluidSecondaryWavePhase"
    )

    // Micro-wave ripple for realistic fluid surface shimmer
    val ripplePhase by infiniteTransition.animateFloat(
        initialValue = 2.4f,
        targetValue = (2.4f + 2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FluidRipplePhase"
    )

    val bubbleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FluidBubbleProgress"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        HapticHelper.splash(context)
                        coroutineScope.launch {
                            val kick = if (animatedTilt >= 0) -16f else 16f
                            touchTilt.snapTo(kick)
                            sloshAnim.snapTo(18f)
                            launch {
                                sloshAnim.animateTo(0f, tween(durationMillis = 1000, easing = LinearEasing))
                            }
                            touchTilt.animateTo(0f, spring(0.65f, Spring.StiffnessMediumLow))
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        HapticHelper.splash(context)
                        coroutineScope.launch {
                            sloshAnim.snapTo(14f)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val tiltDelta = (dragAmount.x * 0.32f).coerceIn(-30f, 30f)
                        coroutineScope.launch {
                            touchTilt.snapTo((touchTilt.value + tiltDelta).coerceIn(-38f, 38f))
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            launch {
                                sloshAnim.animateTo(0f, tween(durationMillis = 900, easing = LinearEasing))
                            }
                            touchTilt.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = 0.68f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            touchTilt.animateTo(0f, spring(0.7f, Spring.StiffnessMediumLow))
                        }
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val clampedFill = animatedFillLevel.coerceIn(0.10f, 0.90f)

        // Baseline fluid level with spring interpolation
        val baseWaterY = if (isUpsideDown) {
            h * clampedFill
        } else {
            h * (1f - clampedFill)
        }

        // Tilt height difference across container width
        val tiltRad = Math.toRadians(animatedTilt.toDouble().coerceIn(-40.0, 40.0)).toFloat()
        val tiltOffset = (w * 0.34f * sin(tiltRad)).coerceIn(-h * 0.35f, h * 0.35f)

        // Active impulse slosh height
        val impulseWave = sloshAnim.value

        // Dynamically scale wave amplitude to container height so it looks vivid and fluid on all aspect ratios
        val baseAmp = ((h * 0.045f).coerceIn(11f, 26f) + impulseWave * 0.85f).coerceIn(9f, 36f)

        // 1. Bottom Radial Caustic Glow
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    tint.liquidSecondaryColor.copy(alpha = 0.28f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f - tiltOffset * 0.8f, if (isUpsideDown) 0f else h),
                radius = w * 0.75f
            )
        )

        // 2. Background Translucent Wave Layer (shifted phase & depth)
        val bgWavePath = Path()
        buildCubicBezierFluidWave(
            path = bgWavePath,
            width = w,
            height = h,
            baseY = baseWaterY,
            tiltOffset = tiltOffset,
            amplitude = baseAmp * 0.75f,
            phase = secondaryWavePhase,
            impulseWave = impulseWave * 0.6f,
            isUpsideDown = isUpsideDown
        )

        val bgFluidBrush = Brush.verticalGradient(
            colors = listOf(
                tint.liquidSecondaryColor.copy(alpha = 0.52f),
                tint.liquidColor.copy(alpha = 0.72f)
            ),
            startY = if (isUpsideDown) 0f else (baseWaterY - 30f).coerceAtLeast(0f),
            endY = if (isUpsideDown) (baseWaterY + 30f).coerceAtMost(h) else h
        )
        drawPath(path = bgWavePath, brush = bgFluidBrush)

        // 3. Foreground Main Fluid Body Layer (Smooth continuous cubic splines)
        val fgWavePath = Path()
        buildCubicBezierFluidWave(
            path = fgWavePath,
            width = w,
            height = h,
            baseY = baseWaterY,
            tiltOffset = tiltOffset,
            amplitude = baseAmp,
            phase = primaryWavePhase,
            impulseWave = impulseWave,
            isUpsideDown = isUpsideDown
        )

        val fgFluidBrush = Brush.linearGradient(
            colors = listOf(
                tint.liquidSecondaryColor.copy(alpha = 0.92f),
                tint.liquidColor.copy(alpha = 0.96f),
                tint.primaryColor.copy(alpha = 0.88f)
            ),
            start = Offset(0f, if (isUpsideDown) 0f else baseWaterY),
            end = Offset(w, if (isUpsideDown) baseWaterY else h)
        )
        drawPath(path = fgWavePath, brush = fgFluidBrush)

        // 4. Surface Tension Crest Line & Specular Bloom
        val crestPath = Path()
        buildCubicBezierFluidCrest(
            path = crestPath,
            width = w,
            height = h,
            baseY = baseWaterY,
            tiltOffset = tiltOffset,
            amplitude = baseAmp,
            phase = primaryWavePhase,
            impulseWave = impulseWave,
            isUpsideDown = isUpsideDown
        )

        // Soft crest glow bloom
        drawPath(
            path = crestPath,
            color = tint.liquidSecondaryColor.copy(alpha = 0.45f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.5f)
        )

        // Specular white meniscus line
        drawPath(
            path = crestPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.92f),
                    Color.White.copy(alpha = 0.35f)
                )
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.4f)
        )

        // 5. Buoyant Micro-Bubbles with Smooth Brownian Sway
        clipRect(0f, 0f, w, h) {
            bubbles.forEach { b ->
                val bubblePeriod = 3.6f / b.speed
                val prog = (bubbleProgress + b.phase) % 1.0f
                val bubbleY = if (isUpsideDown) {
                    prog * baseWaterY
                } else {
                    baseWaterY + (1f - prog) * (h - baseWaterY)
                }

                val sway = sin((primaryWavePhase * 1.5f + b.phase * 2 * PI).toDouble()).toFloat() * b.swayAmp
                val bubbleX = (b.relX * w + sway + (animatedTilt * 0.4f)).coerceIn(10f, w - 10f)

                if (bubbleY in (baseWaterY - 8f)..h) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.42f),
                        radius = b.radius,
                        center = Offset(bubbleX, bubbleY)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.88f),
                        radius = b.radius * 0.32f,
                        center = Offset(bubbleX - b.radius * 0.28f, bubbleY - b.radius * 0.28f)
                    )
                }
            }
        }

        // 6. Glass Refraction Diagonal Glint
        val sheenBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.24f),
                Color.White.copy(alpha = 0.04f),
                Color.Transparent
            ),
            start = Offset(0f, 0f),
            end = Offset(w * 0.75f, h * 0.55f)
        )
        drawRect(brush = sheenBrush)
    }
}

/**
 * Evaluates the exact height of the fluid surface at normalized coordinate x (0..1)
 * with spatial damping factor and meniscus surface adhesion to eliminate jitter.
 */
private fun evalFluidY(
    normX: Float,
    leftBase: Float,
    rightBase: Float,
    amplitude: Float,
    phase: Float,
    impulseWave: Float,
    height: Float
): Float {
    val linearWater = leftBase + (rightBase - leftBase) * normX
    // Spatial damping factor: viscous boundary layer suppresses high-frequency edge tremors
    val spatialDamping = exp(-abs(normX - 0.5f) * 0.45f)

    val wave1 = sin((normX * 2f * PI.toFloat() + phase).toDouble()).toFloat() * amplitude * spatialDamping
    val wave2 = sin((normX * 4f * PI.toFloat() - phase * 0.6f).toDouble()).toFloat() * (amplitude * 0.20f) * (spatialDamping * spatialDamping)
    val slosh = sin((normX * PI.toFloat() + phase * 1.4f).toDouble()).toFloat() * impulseWave
    // Meniscus curve near container walls
    val meniscus = ((1f - normX).pow(4) + normX.pow(4)) * 3.5f

    val finalY = linearWater + wave1 + wave2 + slosh - meniscus
    return finalY.coerceIn(height * 0.04f, height * 0.96f)
}

/**
 * Builds a smooth Cubic Bézier curve surface through evaluation nodes,
 * producing continuous derivatives and zero polygonal visual stepping.
 */
private fun buildCubicBezierFluidWave(
    path: Path,
    width: Float,
    height: Float,
    baseY: Float,
    tiltOffset: Float,
    amplitude: Float,
    phase: Float,
    impulseWave: Float,
    isUpsideDown: Boolean
) {
    path.reset()

    val leftBase = (if (isUpsideDown) baseY - tiltOffset else baseY + tiltOffset).coerceIn(height * 0.06f, height * 0.94f)
    val rightBase = (if (isUpsideDown) baseY + tiltOffset else baseY - tiltOffset).coerceIn(height * 0.06f, height * 0.94f)

    // Evaluate 8 key control anchors across the width
    val segments = 8
    val pointsX = FloatArray(segments + 1)
    val pointsY = FloatArray(segments + 1)

    for (i in 0..segments) {
        val normX = i.toFloat() / segments
        pointsX[i] = normX * width
        pointsY[i] = evalFluidY(normX, leftBase, rightBase, amplitude, phase, impulseWave, height)
    }

    if (isUpsideDown) {
        path.moveTo(0f, 0f)
        path.lineTo(pointsX[0], pointsY[0])

        for (i in 0 until segments) {
            val p0X = if (i > 0) pointsX[i - 1] else pointsX[i]
            val p0Y = if (i > 0) pointsY[i - 1] else pointsY[i]
            val p1X = pointsX[i]
            val p1Y = pointsY[i]
            val p2X = pointsX[i + 1]
            val p2Y = pointsY[i + 1]
            val p3X = if (i + 2 <= segments) pointsX[i + 2] else pointsX[i + 1]
            val p3Y = if (i + 2 <= segments) pointsY[i + 2] else pointsY[i + 1]

            val cp1X = p1X + (p2X - p0X) / 6f
            val cp1Y = p1Y + (p2Y - p0Y) / 6f
            val cp2X = p2X - (p3X - p1X) / 6f
            val cp2Y = p2Y - (p3Y - p1Y) / 6f

            path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2X, p2Y)
        }

        path.lineTo(width, 0f)
        path.close()
    } else {
        path.moveTo(0f, pointsY[0])

        for (i in 0 until segments) {
            val p0X = if (i > 0) pointsX[i - 1] else pointsX[i]
            val p0Y = if (i > 0) pointsY[i - 1] else pointsY[i]
            val p1X = pointsX[i]
            val p1Y = pointsY[i]
            val p2X = pointsX[i + 1]
            val p2Y = pointsY[i + 1]
            val p3X = if (i + 2 <= segments) pointsX[i + 2] else pointsX[i + 1]
            val p3Y = if (i + 2 <= segments) pointsY[i + 2] else pointsY[i + 1]

            val cp1X = p1X + (p2X - p0X) / 6f
            val cp1Y = p1Y + (p2Y - p0Y) / 6f
            val cp2X = p2X - (p3X - p1X) / 6f
            val cp2Y = p2Y - (p3Y - p1Y) / 6f

            path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2X, p2Y)
        }

        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()
    }
}

/**
 * Builds the open top surface line for specular crest highlights and bloom glow.
 */
private fun buildCubicBezierFluidCrest(
    path: Path,
    width: Float,
    height: Float,
    baseY: Float,
    tiltOffset: Float,
    amplitude: Float,
    phase: Float,
    impulseWave: Float,
    isUpsideDown: Boolean
) {
    path.reset()

    val leftBase = (if (isUpsideDown) baseY - tiltOffset else baseY + tiltOffset).coerceIn(height * 0.06f, height * 0.94f)
    val rightBase = (if (isUpsideDown) baseY + tiltOffset else baseY - tiltOffset).coerceIn(height * 0.06f, height * 0.94f)

    val segments = 8
    val pointsX = FloatArray(segments + 1)
    val pointsY = FloatArray(segments + 1)

    for (i in 0..segments) {
        val normX = i.toFloat() / segments
        pointsX[i] = normX * width
        pointsY[i] = evalFluidY(normX, leftBase, rightBase, amplitude, phase, impulseWave, height)
    }

    path.moveTo(pointsX[0], pointsY[0])
    for (i in 0 until segments) {
        val p0X = if (i > 0) pointsX[i - 1] else pointsX[i]
        val p0Y = if (i > 0) pointsY[i - 1] else pointsY[i]
        val p1X = pointsX[i]
        val p1Y = pointsY[i]
        val p2X = pointsX[i + 1]
        val p2Y = pointsY[i + 1]
        val p3X = if (i + 2 <= segments) pointsX[i + 2] else pointsX[i + 1]
        val p3Y = if (i + 2 <= segments) pointsY[i + 2] else pointsY[i + 1]

        val cp1X = p1X + (p2X - p0X) / 6f
        val cp1Y = p1Y + (p2Y - p0Y) / 6f
        val cp2X = p2X - (p3X - p1X) / 6f
        val cp2Y = p2Y - (p3Y - p1Y) / 6f

        path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2X, p2Y)
    }
}
