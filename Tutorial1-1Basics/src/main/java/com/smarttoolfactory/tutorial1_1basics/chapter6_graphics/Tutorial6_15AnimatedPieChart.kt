@file:OptIn(ExperimentalTextApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Tutorial6_15Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    val chartDataList = remember {
        listOf(
            AnimatedChartData(Pink400, 10f),
            AnimatedChartData(Orange400, 20f),
            AnimatedChartData(Yellow400, 15f),
            AnimatedChartData(Green400, 5f),
            AnimatedChartData(Blue400, 50f),
        )
    }

    chartDataList.forEach {
        LaunchedEffect(key1 = it.isSelected) {
            val targetValue = if (it.isSelected) 1.1f else 1f
            it.animatable.animateTo(targetValue, animationSpec = tween(500))
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val textMeasureResults = remember(chartDataList) {
        chartDataList.map {
            textMeasurer.measure(
                text = "%${it.data.toInt()}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    val chartStartAngle = -90f
    val animatableInitialSweepAngle = remember {
        Animatable(chartStartAngle)
    }

    val chartEndAngle = 360f + chartStartAngle

    LaunchedEffect(key1 = animatableInitialSweepAngle) {
        animatableInitialSweepAngle.animateTo(
            targetValue = chartEndAngle,
            animationSpec = tween(
                delayMillis = 1000,
                durationMillis = 2000
            )
        )
    }
    val currentSweepAngle = animatableInitialSweepAngle.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .border(1.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    val radius = (size.width / 2f) * .9f
                    val strokeWidth = radius * .4f
                    val innerRadius = radius - strokeWidth

                    detectTapGestures(
                        onPress = { position: Offset ->
                            val xPos = size.center.x - position.x
                            val yPos = size.center.y - position.y
                            val length = sqrt(xPos * xPos + yPos * yPos)
                            val isTouched = length in innerRadius..radius

                            if (isTouched) {
                                val touchAngle =
                                    (atan2(yPos, xPos) * 180 / Math.PI + 270f) % 360f

                                var startAngle = 0f
                                chartDataList.forEach {
                                    val angle = it.data.asAngle
                                    val range = startAngle..startAngle + angle
                                    val newSelectedValue = touchAngle in range
                                    if (it.isSelected) {
                                        it.isSelected = false
                                    } else {
                                        it.isSelected = newSelectedValue
                                    }

                                    startAngle += angle
                                }
                            }

                        }
                    )
                }

        ) {

            val width = size.width
            val radius = (width / 2f) * .9f
            val strokeWidth = radius * .4f
            val innerRadius = radius - strokeWidth
            val lineStrokeWidth = 3.dp.toPx()
            val shadeStrokeWidth = strokeWidth * .3f

            var startAngle = -90f

            for (index in 0..chartDataList.lastIndex) {

                val chartData = chartDataList[index]
                val sweepAngle = chartData.data.asAngle
                val angleInRadians = (startAngle + sweepAngle / 2).degreeToRadian
                val textMeasureResult = textMeasureResults[index]
                val textSize = textMeasureResult.size
                val currentStrokeWidth = strokeWidth

                withTransform(
                    {
                        val scale = chartData.animatable.value
                        scale(
                            scaleX = scale,
                            scaleY = scale
                        )
                    }
                ) {

                    if (startAngle <= currentSweepAngle) {
                        drawArc(
                            color = chartData.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.coerceAtMost(
                                currentSweepAngle - startAngle
                            ),
                            useCenter = false,
                            topLeft = Offset(
                                (width - 2 * innerRadius - currentStrokeWidth) / 2,
                                (width - 2 * innerRadius - currentStrokeWidth) / 2
                            ),
                            size = Size(
                                innerRadius * 2 + currentStrokeWidth,
                                innerRadius * 2 + currentStrokeWidth
                            ),
                            style = Stroke(currentStrokeWidth)
                        )

                        drawArc(
                            color = chartData.color.copy(alpha = .7f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.coerceAtMost(
                                currentSweepAngle - startAngle
                            ),
                            useCenter = false,
                            topLeft = Offset(
                                (width - 2 * innerRadius) / 2 + shadeStrokeWidth / 2,
                                (width - 2 * innerRadius) / 2 + shadeStrokeWidth / 2
                            ),
                            size = Size(
                                2 * innerRadius - shadeStrokeWidth,
                                2 * innerRadius - shadeStrokeWidth
                            ),
                            style = Stroke(shadeStrokeWidth)
                        )
                    }

                    val textCenter = textSize.center

                    if (currentSweepAngle == chartEndAngle) {
                        drawText(
                            textLayoutResult = textMeasureResult,
                            color = Color.Black,
                            topLeft = Offset(
                                -textCenter.x + center.x
                                        + (innerRadius + strokeWidth / 2) * cos(angleInRadians),
                                -textCenter.y + center.y
                                        + (innerRadius + strokeWidth / 2) * sin(angleInRadians)
                            )
                        )
                    }
                }

                rotate(
                    90f + startAngle
                ) {
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, innerRadius + shadeStrokeWidth),
                        end = Offset(center.x, 0f),
                        strokeWidth = lineStrokeWidth
                    )
                }

                startAngle += sweepAngle
            }
        }
    }
}

@Preview
@Composable
private fun CanvasRectTest() {

    var target by remember {
        mutableStateOf(1f)
    }
    val scale by animateFloatAsState(targetValue = target)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    target = if (target == 1f) 1.3f else 1f
                }
            }
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, Color.Red),
        ) {

            val radius = size.width / 2f * .8f
            val strokeWidth = (size.width - 2 * radius) / 2
            val newStrokeWidth = strokeWidth * scale
            drawRect(
                color = Color.Green,
                style = Stroke(width = newStrokeWidth),
                topLeft = Offset(
                    (size.width - 2 * radius - newStrokeWidth) / 2,
                    (size.width - 2 * radius - newStrokeWidth) / 2
                ),
                size = Size(2 * radius + newStrokeWidth, 2 * radius + newStrokeWidth)
            )

            drawCircle(color = Color.Blue, radius = radius)
        }
    }
}

@Preview
@Composable
private fun CanvasRectTest2() {

    var target by remember {
        mutableStateOf(1f)
    }
    val scale by animateFloatAsState(targetValue = target)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    target = if (target == 1f) 1.3f else 1f
                }
            }
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, Color.Red),
        ) {

            val radius = size.width / 2f * .8f
            val strokeWidth = (size.width - 2 * radius) / 2
            val newStrokeWidth = strokeWidth * scale
            drawRect(
                color = Color.Green,
                style = Stroke(width = newStrokeWidth),
                topLeft = Offset(
                    newStrokeWidth / 2,
                    newStrokeWidth / 2
                ),
                size = Size(size.width - newStrokeWidth, size.height - newStrokeWidth)
            )
        }
    }
}

@Preview
@Composable
private fun TranslateScaleTest() {
    var scale by remember {
        mutableStateOf(1f)
    }

    var angle by remember {
        mutableStateOf(0f)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {

        Slider(
            value = angle,
            onValueChange = { angle = it },
            valueRange = 0f..360f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        scale = if (scale == 1f) 1.3f else 1f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(2.dp, Color.Red),
            ) {

                val radius = size.width / 2f * .4f
                val strokeWidth = 2.dp.toPx()

                drawCircle(
                    color = Color.Red,
                    style = Stroke(2.dp.toPx()),
                    radius = radius
                )

                withTransform(
                    {
                        scale(
                            scaleX = scale,
                            scaleY = 1f,
                            pivot = Offset(
                                center.x + radius * cos(angle * Math.PI / 180f).toFloat(),
                                center.y + radius * sin(angle * Math.PI / 180f).toFloat()
                            ),
                        )
                    }
                ) {


                    drawRect(
                        color = Color.Green,
                        style = Stroke(width = strokeWidth),
                        topLeft = Offset(
                            center.x + radius * cos(angle * Math.PI / 180f).toFloat(),
                            -100f + center.y + radius * sin(angle * Math.PI / 180f).toFloat()
                        ),
                        size = Size(200f, 200f)
                    )

                    drawArc(
                        color = Color.Magenta,
                        topLeft = Offset(
                            center.x + radius * cos(angle * Math.PI / 180f).toFloat(),
                            -100f + center.y + radius * sin(angle * Math.PI / 180f).toFloat()
                        ),
                        size = Size(200f, 200f),
                        startAngle = -60f,
                        sweepAngle = 120f,
                        useCenter = true
                    )
                }

                drawCircle(
                    color = Color.Blue,
                    radius = 20f,
                    Offset(
                        center.x + radius * cos(angle * Math.PI / 180f).toFloat(),
                        center.y + radius * sin(angle * Math.PI / 180f).toFloat()
                    )
                )

            }
        }
    }
}

@Immutable
class AnimatedChartData(
    val color: Color,
    val data: Float,
    selected: Boolean = false,
    val animatable: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {
    var isSelected by mutableStateOf(selected)
}