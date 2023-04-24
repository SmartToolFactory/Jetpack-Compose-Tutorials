package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.ArrowDirection
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.createBubbleShape
import com.smarttoolfactory.tutorial1_1basics.ui.gradientColors


@Preview
@Composable
fun Tutorial6_17Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .drawRainbowBorder(
                    strokeWidth = 4.dp,
                    durationMillis = 3000
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .drawRainbowBorder(
                    strokeWidth = 4.dp,
                    durationMillis = 2000,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(60.dp)
                .drawRainbowBorder(
                    strokeWidth = 4.dp,
                    durationMillis = 2000,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .drawRainbowBorder(
                    strokeWidth = 4.dp,
                    durationMillis = 2000,
                    shape = CutCornerShape(8.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .drawRainbowBorder(
                    strokeWidth = 4.dp,
                    durationMillis = 2000,
                    shape = createBubbleShape(
                        arrowWidth = 20f,
                        arrowHeight = 20f,
                        arrowOffset = 10f,
                        arrowDirection = ArrowDirection.Left
                    )
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World")
        }
    }

}

fun Modifier.drawRainbowBorder(
    strokeWidth: Dp,
    durationMillis: Int
) = composed {

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Modifier.drawWithContent {

        val strokeWidthPx = strokeWidth.toPx()
        val width = size.width
        val height = size.height

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(
                color = Color.Gray,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(width - strokeWidthPx, height - strokeWidthPx),
                style = Stroke(strokeWidthPx)
            )

            // Source
            withRotation(
                angle,
                pivotX = center.x,
                pivotY = center.y
            ) {

                drawCircle(
                    brush = Brush.sweepGradient(gradientColors),
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }

            restoreToCount(checkPoint)
        }

        drawContent()
    }
}

fun Modifier.drawRainbowBorder(
    strokeWidth: Dp,
    shape: Shape,
    durationMillis: Int
) = composed {

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    Modifier
        .drawWithContent {


            val strokeWidthPx = strokeWidth.toPx()
            val width = size.width
            val height = size.height

            val outline = shape.createOutline(
                size = Size(
                    size.width - strokeWidthPx,
                    size.height - strokeWidthPx
                ),
                layoutDirection = layoutDirection,
                density = density
            )

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // Destination
                translate(
                    left = strokeWidthPx / 2,
                    top = strokeWidthPx / 2
                ) {
                    drawOutline(
                        outline = outline,
                        color = Color.Gray,
                        style = Stroke(strokeWidthPx)
                    )
                }

                // Source
                withRotation(
                    angle,
                    pivotX = center.x,
                    pivotY = center.y
                ) {

                    drawCircle(
                        brush = Brush.sweepGradient(gradientColors),
                        radius = size.width,
                        blendMode = BlendMode.SrcIn,
                    )
                }

                restoreToCount(checkPoint)
            }
            drawContent()
        }
}