package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial6_41Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .systemBarsPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TutorialHeader("Custom DropShadow\nCustom Animated Shadows")
        StyleableTutorialText(
            text = "Use custom shadow modifier to draw drop shadows.",
            bullets = false
        )
        CustomDropShadowSample()
        Spacer(modifier = Modifier.height(32.dp))
        StyleableTutorialText(
            text = "Use custom shadow modifier to draw animated shadows.",
            bullets = false
        )
        CustomAnimatedShadowSample()
    }
}

@Preview
@Composable
private fun CustomDropShadowSample() {
    Column(
        modifier = Modifier.systemBarsPadding()
    ) {
        var radius by remember {
            mutableFloatStateOf(5f)
        }

        var spread by remember {
            mutableFloatStateOf(5f)
        }

        var offsetX by remember {
            mutableFloatStateOf(5f)
        }

        var offsetY by remember {
            mutableFloatStateOf(5f)
        }

        var alpha by remember {
            mutableFloatStateOf(1f)
        }

        Text(text = "radius: ${radius.roundToInt()}")
        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 0f..30f,
        )

        Text(text = "spread: ${spread.roundToInt()}")
        Slider(
            value = spread,
            onValueChange = { spread = it },
            valueRange = 0f..30f,
        )

        Text(text = "offsetX: ${offsetX.roundToInt()}")
        Slider(
            value = offsetX,
            onValueChange = { offsetX = it },
            valueRange = -20f..30f,
        )

        Text(text = "offsetY: ${offsetY.roundToInt()}")
        Slider(
            value = offsetY,
            onValueChange = { offsetY = it },
            valueRange = -20f..30f,
        )

        Text(text = "alpha: $alpha")
        Slider(
            value = alpha,
            onValueChange = { alpha = it },
        )

        Spacer(modifier = Modifier.height(32.dp))

        val shape = RoundedCornerShape(16.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                Modifier
                    .size(120.dp)
                    .dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            brush = Brush.sweepGradient(colors),
                            radius = radius.dp,
                            spread = spread.dp,
                            alpha = alpha,
                            offset = DpOffset(x = offsetX.dp, offsetY.dp)
                        )
                    )
                    .background(
                        color = Color.White,
                        shape = shape
                    )
            ) {
                Text(
                    "Drop Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }

            Box(
                Modifier
                    .size(120.dp)
                    .dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            brush = Brush.sweepGradient(colors),
                            radius = radius.dp,
                            spread = spread.dp,
                            alpha = alpha,
                            offset = DpOffset(x = offsetX.dp, offsetY.dp)
                        )
                    )
                    .background(
                        color = Color.White.copy(alpha = .7f),
                        shape = shape
                    )
            ) {
                Text(
                    "Drop Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                Modifier
                    .size(120.dp)
                    .drawShadow(
                        shape = shape,
                        Shadow(
                            brush = Brush.sweepGradient(colors),
                            alpha = alpha,
                            radius = radius.dp,
                            spread = spread.dp,
                            offset = DpOffset(x = offsetX.dp, offsetY.dp)
                        )
                    )
                    .background(
                        color = Color.White,
                        shape = shape
                    )
            ) {
                Text(
                    "Custom\nDrop Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }

            Box(
                Modifier
                    .size(120.dp)
                    .drawShadow(
                        shape = shape,
                        Shadow(
                            brush = Brush.sweepGradient(colors),
                            alpha = alpha,
                            radius = radius.dp,
                            spread = spread.dp,
                            offset = DpOffset(x = offsetX.dp, offsetY.dp)
                        )
                    )
                    .background(
                        color = Color.White.copy(alpha = .7f),
                        shape = shape
                    )
            ) {
                Text(
                    "Custom\nDrop Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun CustomAnimatedShadowSample() {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var radius by remember {
            mutableFloatStateOf(25f)
        }

        var spread by remember {
            mutableFloatStateOf(0f)
        }

        var alpha by remember {
            mutableFloatStateOf(1f)
        }

        Text(text = "radius: ${radius.roundToInt()}")
        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 0f..50f,
        )

        Text(text = "spread: ${spread.roundToInt()}")
        Slider(
            value = spread,
            onValueChange = { spread = it },
            valueRange = 0f..30f,
        )

        var animateAlpha by remember {
            mutableStateOf(true)
        }

        var animateRotation by remember {
            mutableStateOf(true)
        }

        CheckBoxWithTextRippleFullRow(label = "animateAlpha", animateAlpha) {
            animateAlpha = it
        }

        CheckBoxWithTextRippleFullRow(label = "animateRotation", animateRotation) {
            animateRotation = it
        }

        Spacer(modifier = Modifier.height(32.dp))

        val shape = RoundedCornerShape(24.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                Modifier
                    .size(120.dp)
                    .drawAnimatedShadow(
                        shape = shape,
                        Shadow(
                            brush = Brush.sweepGradient(colors),
                            alpha = alpha,
                            radius = radius.dp,
                            spread = spread.dp
                        ),
                        animateAlpha = animateAlpha,
                        animateRotation = animateRotation
                    )
                    .background(
                        color = Color.White,
                        shape = shape
                    )
            ) {
                Text(
                    "Animated\n Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }

            Box(
                Modifier
                    .size(120.dp)
                    .drawAnimatedShadow(
                        shape = shape,
                        Shadow(
                            brush = Brush.sweepGradient(colors),
                            alpha = alpha,
                            radius = radius.dp,
                            spread = spread.dp
                        ),
                        animateAlpha = animateAlpha,
                        animateRotation = animateRotation
                    )
                    .background(
                        color = Color.White.copy(alpha = .7f),
                        shape = shape
                    )
            ) {
                Text(
                    "Animated\n Shadow",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            Modifier
                .padding(16.dp)
                .height(56.dp)
                .fillMaxWidth()
                .drawAnimatedShadow(
                    shape = shape,
                    Shadow(
                        brush = Brush.sweepGradient(colors),
                        alpha = alpha,
                        radius = radius.dp,
                        spread = spread.dp
                    ),
                    animateAlpha = animateAlpha,
                    animateRotation = animateRotation
                )
                .background(
                    color = Color.White,
                    shape = shape
                )
        ) {
            Text(
                "Animated Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        }
    }
}

fun Modifier.drawShadow(
    shape: Shape,
    shadow: Shadow
) = composed {

    val paint = remember { Paint().apply { style = PaintingStyle.Fill } }

    drawWithCache {
        val radiusPx = shadow.radius.toPx()
        val spreadPx = shadow.spread.toPx()
        val offsetXPx = shadow.offset.x.toPx()
        val offsetYPx = shadow.offset.y.toPx()

        val outset = spreadPx * 2f
        val shadowWidth = size.width + outset
        val shadowHeight = size.height + outset

        val outline = shape.createOutline(
            size = Size(shadowWidth, shadowHeight),
            layoutDirection = layoutDirection,
            density = this
        )

        // Update paint fields without LaunchedEffect
        paint.color = shadow.color
        paint.alpha = shadow.alpha

        paint.shader = SweepGradientShader(
            center = size.center,
            colors = colors
        )

        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.maskFilter = if (radiusPx > 0f) {
            BlurMaskFilter(radiusPx, BlurMaskFilter.Blur.NORMAL)
        } else {
            null
        }

        val translateX = offsetXPx - spreadPx
        val translateY = offsetYPx - spreadPx

        onDrawBehind {
            translate(left = translateX, top = translateY) {
                drawIntoCanvas { canvas ->
                    canvas.drawOutline(outline, paint)
                }
            }
        }
    }
}

fun Modifier.drawAnimatedShadow(
    shape: Shape,
    shadow: Shadow,
    animateAlpha: Boolean = true,
    animateRotation: Boolean = true,
    durationMillis: Int = 2000,
    border: Border? = Border(width = 2.dp, brush = shadow.brush ?: Brush.sweepGradient(colors))
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    val angle = if (animateRotation) {
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ), label = "rotation"
        )
        angle
    } else {
        0f
    }

    val phase = if (animateAlpha) {
        val phase by infiniteTransition.animateFloat(
            initialValue = .3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        phase
    } else {
        1f
    }

    val radius = shadow.radius
    val spread = shadow.spread
    val brush = shadow.brush ?: SolidColor(shadow.color)

    drawAnimatedShadow(
        shape = shape,
        radius = radius * phase,
        spread = spread,
        alpha = phase,
        angle = angle,
        brush = brush,
        border = border
    )
}

fun Modifier.drawAnimatedShadow(
    shape: Shape,
    radius: Dp,
    spread: Dp,
    alpha: Float,
    angle: Float,
    brush: Brush,
    border: Border? = null
) = composed {

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Fill
        }
    }

    drawWithCache {
        val radiusPx = radius.toPx()
        val spreadPx = spread.toPx()

        val outset = spreadPx * 2f
        val shadowWidth = size.width + outset
        val shadowHeight = size.height + outset

        val outline: Outline = shape.createOutline(
            size = Size(shadowWidth, shadowHeight),
            layoutDirection = layoutDirection,
            density = this
        )

        val borderOutline: Outline? = border?.let {
            shape.createOutline(
                size = Size(size.width, size.height),
                layoutDirection = layoutDirection,
                density = this
            )
        }
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.maskFilter = if (radiusPx > 0f) {
            BlurMaskFilter(radiusPx, BlurMaskFilter.Blur.NORMAL)
        } else {
            null
        }

        onDrawWithContent {
            paint.alpha = alpha

            val context = this
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                drawIntoCanvas { canvas ->
                    // Destination
                    translate((size.width - shadowWidth) / 2, (size.height - shadowHeight) / 2) {
                        canvas.drawOutline(
                            outline = outline,
                            paint = paint
                        )
                    }

                    // Source
                    context.rotate(angle) {
                        context.drawCircle(
                            center = center,
                            radius = size.width,
                            brush = brush,
                            blendMode = BlendMode.SrcIn
                        )
                    }
                }

                restoreToCount(checkPoint)
            }

            drawContent()

            if (border != null && borderOutline != null) {
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    // Destination
                    val strokeWidthPx = border.width.toPx()
                    drawOutline(
                        outline = borderOutline,
                        color = Color.White.copy(alpha = alpha),
                        style = Stroke(strokeWidthPx)
                    )

                    // Source
                    rotate(angle) {
                        drawCircle(
                            brush = border.brush,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                            alpha = alpha
                        )
                    }
                    restoreToCount(checkPoint)
                }
            }
        }
    }
}

@Stable
fun Border(width: Dp, color: Color) = Border(width, SolidColor(color))
data class Border(val width: Dp, val brush: Brush)

private val colors = listOf(
    Color(0xFF4cc9f0),
    Color(0xFFf72585),
    Color(0xFFb5179e),
    Color(0xFF7209b7),
    Color(0xFF560bad),
    Color(0xFF480ca8),
    Color(0xFF3a0ca3),
    Color(0xFF3f37c9),
    Color(0xFF4361ee),
    Color(0xFF4895ef),
    Color(0xFF4cc9f0)
)
