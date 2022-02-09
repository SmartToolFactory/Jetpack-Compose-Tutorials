package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import java.lang.Math.PI
import kotlin.math.sin

@OptIn(ExperimentalMaterialApi::class)
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

        Text(
            text = "Draw Line",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawLineExample()

        Text(
            text = "Draw Oval&Circle",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawCircleExample()
        Text(
            text = "Draw Rectangle",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawRectangleExample()
        Text(
            text = "Draw Points",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawPointsExample()
    }
}

@Composable
private fun DrawLineExample() {

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
    TutorialText2(text = "StrokeCap")
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
    TutorialText2(text = "Brush")
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
    TutorialText2(text = "PathEffect")
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
private fun DrawCircleExample() {

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
    TutorialText2(text = "DrawStyle")

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
    TutorialText2(text = "Brush")
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
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2
        val space = (canvasWidth - 6 * radius) / 4

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Green,
                    Color.Red,
                    Color.Blue
                ),
                center = Offset(space + radius, canvasHeight / 2),
            ),
            radius = radius,
            center = Offset(space + radius, canvasHeight / 2),
        )

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Green,
                    Color.Cyan,
                    Color.Red,
                    Color.Blue,
                    Color.Yellow,
                    Color.Magenta,
                ),
                // Offset for this gradient is not at center, a little bit left of center
                center = Offset(2 * space + 2.7f * radius, canvasHeight / 2),
            ),
            radius = radius,
            center = Offset(2 * space + 3 * radius, canvasHeight / 2),
        )

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Green,
                    Color.Cyan,
                    Color.Red,
                    Color.Blue,
                    Color.Yellow,
                    Color.Magenta
                ),
                center = Offset(canvasWidth - space - radius, canvasHeight / 2),
            ),
            radius = radius,
            center = Offset(canvasWidth - space - radius, canvasHeight / 2)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "BlendMode")
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
                center = Offset(space + radius + 50f, canvasHeight / 2),
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

@Composable
private fun DrawRectangleExample() {
    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Rectangle")
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val space = 60f
        val rectHeight = canvasHeight / 2
        val rectWidth = (canvasWidth - 4 * space) / 3

        drawRect(
            color = Color.Blue,
            topLeft = Offset(space, rectHeight / 2),
            size = Size(rectWidth, rectHeight)
        )

        drawRect(
            color = Color.Green,
            topLeft = Offset(2 * space + rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            style = Stroke(width = 12.dp.toPx())
        )

        drawRect(
            color = Color.Red,
            topLeft = Offset(3 * space + 2 * rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            style = Stroke(width = 2.dp.toPx())
        )
    }


    TutorialText2(text = "RoundedRect")
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val space = 60f
        val rectHeight = canvasHeight / 2
        val rectWidth = (canvasWidth - 4 * space) / 3

        drawRoundRect(
            color = Color.Blue,
            topLeft = Offset(space, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )

        drawRoundRect(
            color = Color.Green,
            topLeft = Offset(2 * space + rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(70f, 70f)

        )

        drawRoundRect(
            color = Color.Red,
            topLeft = Offset(3 * space + 2 * rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(50f, 25f)

        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "DrawStyle")
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val space = 30f
        val rectHeight = canvasHeight / 2
        val rectWidth = (canvasWidth - 4 * space) / 3

        drawRect(
            color = Color.Blue,
            topLeft = Offset(space, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            style = Stroke(
                width = 2.dp.toPx(),
                join = StrokeJoin.Miter,
                cap = StrokeCap.Butt,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
            )
        )

        drawRect(
            color = Color.Green,
            topLeft = Offset(2 * space + rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            style = Stroke(
                width = 2.dp.toPx(),
                join = StrokeJoin.Bevel,
                cap = StrokeCap.Square,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
            )
        )

        drawRect(
            color = Color.Red,
            topLeft = Offset(3 * space + 2 * rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight),
            style = Stroke(
                width = 2.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
            )
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Brush")
    Canvas(modifier = canvasModifier2) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val space = 30f
        val rectHeight = canvasHeight / 2
        val rectWidth = (canvasWidth - 4 * space) / 3

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Green,
                    Color.Red,
                    Color.Blue,
                    Color.Yellow,
                    Color.Magenta
                ),
                center = Offset(space + .5f * rectWidth, rectHeight),
                tileMode = TileMode.Mirror,
                radius = 20f
            ),
            topLeft = Offset(space, rectHeight / 2),
            size = Size(rectWidth, rectHeight)
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Green,
                    Color.Red,
                    Color.Blue,
                    Color.Yellow,
                    Color.Magenta
                ),
                center = Offset(2 * space + 1.5f * rectWidth, rectHeight),
                tileMode = TileMode.Repeated,
                radius = 20f
            ),
            topLeft = Offset(2 * space + rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight)
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Green,
                    Color.Red,
                    Color.Blue,
                    Color.Yellow,
                    Color.Magenta
                ),
                center = Offset(3 * space + 2.5f * rectWidth, rectHeight),
                tileMode = TileMode.Decal,
                radius = rectHeight / 2
            ),
            topLeft = Offset(3 * space + 2 * rectWidth, rectHeight / 2),
            size = Size(rectWidth, rectHeight)
        )
    }
}

@Composable
private fun DrawPointsExample() {
    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "PointMode")
    Canvas(modifier = canvasModifier2) {


        val middleW = size.width / 2
        val middleH = size.height / 2
        drawLine(Color.Gray, Offset(0f, middleH), Offset(size.width - 1, middleH))
        drawLine(Color.Gray, Offset(middleW, 0f), Offset(middleW, size.height - 1))

        val points1 = getSinusoidalPoints(size)

        drawPoints(
            color = Color.Blue,
            points = points1,
            cap = StrokeCap.Round,
            pointMode = PointMode.Points,
            strokeWidth = 10f
        )

        val points2 = getSinusoidalPoints(size, 100f)
        drawPoints(
            color = Color.Green,
            points = points2,
            cap = StrokeCap.Round,
            pointMode = PointMode.Lines,
            strokeWidth = 10f
        )

        val points3 = getSinusoidalPoints(size, 200f)
        drawPoints(
            color = Color.Red,
            points = points3,
            cap = StrokeCap.Round,
            pointMode = PointMode.Polygon,
            strokeWidth = 10f
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Brush")
    Canvas(modifier = canvasModifier2) {

        val middleW = size.width / 2
        val middleH = size.height / 2
        drawLine(Color.Gray, Offset(0f, middleH), Offset(size.width - 1, middleH))
        drawLine(Color.Gray, Offset(middleW, 0f), Offset(middleW, size.height - 1))


        val points1 = getSinusoidalPoints(size)

        drawPoints(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Green)
            ),
            points = points1,
            cap = StrokeCap.Round,
            pointMode = PointMode.Points,
            strokeWidth = 10f
        )

        val points2 = getSinusoidalPoints(size, 100f)
        drawPoints(
            brush = Brush.linearGradient(
                colors = listOf(Color.Green, Color.Magenta)
            ),
            points = points2,
            cap = StrokeCap.Round,
            pointMode = PointMode.Lines,
            strokeWidth = 10f
        )

        val points3 = getSinusoidalPoints(size, 200f)
        drawPoints(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow)
            ),
            points = points3,
            cap = StrokeCap.Round,
            pointMode = PointMode.Polygon,
            strokeWidth = 10f
        )
    }
}



fun getSinusoidalPoints(size: Size, horizontalOffset: Float = 0f): MutableList<Offset> {
    val points = mutableListOf<Offset>()
    val verticalCenter = size.height / 2

    for (x in 0 until size.width.toInt() step 20) {
        val y = (sin(x * (2f * PI / size.width)) * verticalCenter + verticalCenter).toFloat()
        points.add(Offset(x.toFloat() + horizontalOffset, y))
    }
    return points
}

private val canvasModifier = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(60.dp)

private val canvasModifier2 = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(100.dp)


