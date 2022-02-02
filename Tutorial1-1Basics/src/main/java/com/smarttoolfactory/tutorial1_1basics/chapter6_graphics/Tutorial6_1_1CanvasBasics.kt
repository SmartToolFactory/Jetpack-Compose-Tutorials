package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
            text = "Draw Line",
            bullets = false
        )
        DrawLineSample()
        StyleableTutorialText(
            text = "Draw Circle",
            bullets = false
        )
        DrawCircleSamples()
    }
}

@Composable
private fun DrawLineSample() {

    Spacer(modifier = Modifier.height(10.dp))
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
    Canvas(modifier = canvasModifier2) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2
        val space = (canvasWidth - 6 * radius) / 4

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(space + radius, canvasHeight / 2),
        )

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(2 * space + 3 * radius, canvasHeight / 2),
            style = Stroke(width = 4.dp.toPx())
        )

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(canvasWidth - space - radius, canvasHeight / 2),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f))
            )
        )

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


