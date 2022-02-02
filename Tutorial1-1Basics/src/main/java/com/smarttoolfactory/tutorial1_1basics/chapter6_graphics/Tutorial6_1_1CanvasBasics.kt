package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial6_1Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            modifier = Modifier.padding(top = 10.dp),
            text = "Draw Line",
            bullets = false
        )
        DrawLineSample()
        StyleableTutorialText(
            modifier = Modifier.padding(top = 10.dp),
            text = "Draw Oval&Circle",
            bullets = false
        )
        DrawCircleSamples()
    }
}

@Composable
private fun DrawLineSample() {

    TutorialText2(text = "strokeWidth")
    Canvas(modifier = canvasModifier) {
        drawLine(
            start = Offset(x = 100f, y = 30f),
            end = Offset(x = size.width - 100f, y = 30f),
            color = Color.Red,
        )

        drawLine(
            start = Offset(x = 100f, y = 70f),
            end = Offset(x = size.width - 100f, y = 70f),
            color = Color.Red,
            strokeWidth = 5f
        )

        drawLine(
            start = Offset(x = 100f, y = 110f),
            end = Offset(x = size.width - 100f, y = 110f),
            color = Color.Red,
            strokeWidth = 10f
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "cap")
    Canvas(modifier = canvasModifier) {

        drawLine(
            cap = StrokeCap.Round,
            start = Offset(x = 100f, y = 30f),
            end = Offset(x = size.width - 100f, y = 30f),
            color = Color.Red,
            strokeWidth = 20f
        )

        drawLine(
            cap = StrokeCap.Butt,
            start = Offset(x = 100f, y = 70f),
            end = Offset(x = size.width - 100f, y = 70f),
            color = Color.Red,
            strokeWidth = 20f
        )

        drawLine(
            cap = StrokeCap.Square,
            start = Offset(x = 100f, y = 110f),
            end = Offset(x = size.width - 100f, y = 110f),
            color = Color.Red,
            strokeWidth = 20f
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "brush")
    Canvas(modifier = canvasModifier) {

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Green)
            ),
            start = Offset(x = 100f, y = 30f),
            end = Offset(x = size.width - 100f, y = 30f),
            strokeWidth = 20f,
        )

        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Green, Color.Blue)
            ),
            start = Offset(x = 100f, y = 70f),
            end = Offset(x = size.width - 100f, y = 70f),
            strokeWidth = 20f,
        )

        drawLine(
            brush = Brush.sweepGradient(
                colors = listOf(Color.Red, Color.Green, Color.Blue)
            ),
            start = Offset(x = 100f, y = 110f),
            end = Offset(x = size.width - 100f, y = 110f),
            strokeWidth = 20f,
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "pathEffect")
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(120.dp)
    ) {

        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
            start = Offset(x = 100f, y = 30f),
            end = Offset(x = size.width - 100f, y = 30f),
            color = Color.Red,
            strokeWidth = 10f
        )


        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 10f)),
            start = Offset(x = 100f, y = 70f),
            end = Offset(x = size.width - 100f, y = 70f),
            color = Color.Red,
            strokeWidth = 10f
        )


        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(70f, 40f)),
            start = Offset(x = 100f, y = 110f),
            end = Offset(x = size.width - 100f, y = 110f),
            cap = StrokeCap.Round,
            color = Color.Red,
            strokeWidth = 15f
        )

        val path = Path().apply {
            moveTo(10f, 0f)
            lineTo(20f, 10f)
            lineTo(10f, 20f)
            lineTo(0f, 10f)
        }

        drawLine(
            pathEffect = PathEffect.stampedPathEffect(
                shape = path,
                advance = 30f,
                phase = 30f,
                style = StampedPathEffectStyle.Rotate
            ),
            start = Offset(x = 100f, y = 150f),
            end = Offset(x = size.width - 100f, y = 150f),
            color = Color.Green,
            strokeWidth = 10f
        )

        drawLine(
            pathEffect = PathEffect.stampedPathEffect(
                shape = path,
                advance = 60f,
                phase = 10f,
                style = StampedPathEffectStyle.Morph
            ),
            start = Offset(x = 100f, y = 190f),
            end = Offset(x = size.width - 100f, y = 190f),
            color = Color.Green,
            strokeWidth = 10f
        )
    }
}

@Composable
private fun DrawCircleSamples() {

    TutorialText2(text = "Oval and Circle")

    Canvas(modifier = canvasModifier2) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2

        drawOval(
            color = Color.Blue,
            topLeft = Offset.Zero,
            size = Size(1.2f * canvasHeight, canvasHeight)
        )
        drawOval(
            color = Color.Green,
            topLeft = Offset(1.5f * canvasHeight, 0f),
            size = Size(canvasHeight / 1.5f, canvasHeight)
        )
        drawCircle(
            Color.Red,
            center = Offset(canvasWidth - 2 * radius, canvasHeight / 2),
            radius = radius * 0.8f,
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "pathEffect")

    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2
        val space = (canvasWidth - 6 * radius) / 4

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(space + radius, canvasHeight / 2),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Bevel,
                cap = StrokeCap.Square,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 30f))
            )
        )

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(2 * space + 3 * radius, canvasHeight / 2),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 40f))
            )
        )

        val path = Path().apply {
            moveTo(10f, 0f)
            lineTo(20f, 10f)
            lineTo(10f, 20f)
            lineTo(0f, 10f)
        }

        val pathEffect = PathEffect.stampedPathEffect(
            shape = path,
            advance = 20f,
            phase = 20f,
            style = StampedPathEffectStyle.Morph
        )

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(canvasWidth - space - radius, canvasHeight / 2),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = pathEffect
            )
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "brush")
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2
        val space = (canvasWidth - 6 * radius) / 4

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Green),
                start = Offset(radius * .3f, radius * .1f),
                end = Offset(radius * 2f, radius * 2f)
            ),
            radius = radius,
            center = Offset(space + radius, canvasHeight / 2),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Green)
            ),
            radius = radius,
            center = Offset(2 * space + 3 * radius, canvasHeight / 2),
        )

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Red,
                    Color.Green,
                    Color.Yellow,
                    Color.Blue,
                    Color.Cyan,
                    Color.Magenta
                ),
            ),
            radius = radius,
            center = Offset(canvasWidth - space - radius, canvasHeight / 2)
        )
    }


    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "blendMode")
    Canvas(modifier = canvasModifier2) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2
        val space = (canvasWidth - 4 * radius) / 2

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            drawCircle(
                color = Color.Red,
                radius = radius,
                center = Offset(space  + radius + 50f, canvasHeight / 2),
            )


            drawCircle(
                blendMode = BlendMode.DstOut,
                color = Color.Blue,
                radius = radius,
                center = Offset(space + 3 * radius - 50f, canvasHeight / 2),
            )

            restoreToCount(checkPoint)
        }
    }
}

private val canvasModifier = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(60.dp)

private val canvasModifier2 = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(100.dp)


