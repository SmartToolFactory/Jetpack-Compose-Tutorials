@file:OptIn(ExperimentalTextApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.chart.ChartData
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.Yellow400
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun Tutorial6_14Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        PieChartWithRoundedStrokesAndLabels()
        Spacer(modifier = Modifier.height(10.dp))
        PieChart()
    }
}

@Preview
@Composable
private fun PieChart() {
    val chartDataList = remember {
        listOf(
            ChartData(Pink400, 10f),
            ChartData(Orange400, 20f),
            ChartData(Yellow400, 15f),
            ChartData(Green400, 5f),
            ChartData(Blue400, 50f),
        )
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

    val animatable = remember {
        Animatable(-90f)
    }

    val finalValue = 270f

    LaunchedEffect(key1 = animatable) {
        animatable.animateTo(
            targetValue = finalValue,
            animationSpec = tween(
                delayMillis = 1000,
                durationMillis = 2000
            )
        )
    }
    val currentSweepAngle = animatable.value


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val width = size.width
            val radius = width / 2f
            val strokeWidth = radius * .4f
            val innerRadius = radius - strokeWidth
            val lineStrokeWidth = 3.dp.toPx()

            var startAngle = -90f

            for (index in 0..chartDataList.lastIndex) {

                val chartData = chartDataList[index]
                val sweepAngle = chartData.data.asAngle
                val angleInRadians = (startAngle + sweepAngle / 2).degreeToRadian
                val textMeasureResult = textMeasureResults[index]
                val textSize = textMeasureResult.size

                if (startAngle <= currentSweepAngle) {
                    drawArc(
                        color = chartData.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.coerceAtMost(currentSweepAngle - startAngle),
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(width - strokeWidth, width - strokeWidth),
                        style = Stroke(strokeWidth)
                    )
                }

                rotate(
                    90f + startAngle
                ) {
                    drawLine(
                        color = Color.White,
                        start = Offset(radius, strokeWidth),
                        end = Offset(radius, 0f),
                        strokeWidth = lineStrokeWidth
                    )
                }

                val textCenter = textSize.center

                if (currentSweepAngle == finalValue) {
                    drawText(
                        textLayoutResult = textMeasureResult,
                        color = Color.Black,
                        topLeft = Offset(
                            -textCenter.x + center.x + (innerRadius + strokeWidth / 2) * cos(
                                angleInRadians
                            ),
                            -textCenter.y + center.y + (innerRadius + strokeWidth / 2) * sin(
                                angleInRadians
                            )
                        )
                    )
                }

                startAngle += sweepAngle
            }
        }
    }
}

@Preview
@Composable
private fun PieChartWithRoundedStrokesAndLabels() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val chartDataList = remember {
            listOf(
                ChartData(Pink400, 10f),
                ChartData(Orange400, 20f),
                ChartData(Yellow400, 15f),
                ChartData(Green400, 5f),
                ChartData(Blue400, 50f),
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth(.7f)
                .aspectRatio(1f)
        ) {
            val width = size.width
            val radius = width / 2f
            val strokeWidth = 20.dp.toPx()

            val circumference = 2 * Math.PI * (radius - strokeWidth / 2)
            // stroke width is diameter of rounded cap half circle
            val strokeAngle = 1.2f * (strokeWidth / circumference * 180f).toFloat()

            var startAngle = -90f

            for (index in 0..chartDataList.lastIndex) {

                val chartData = chartDataList[index]
                val sweepAngle = chartData.data.asAngle
                val angleInRadians = (startAngle + sweepAngle / 2).degreeToRadian

                drawArc(
                    color = chartData.color,
                    startAngle = startAngle + strokeAngle,
                    sweepAngle = (sweepAngle - strokeAngle * 2).coerceAtLeast(0f),
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(width - strokeWidth, width - strokeWidth),
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )

                startAngle += sweepAngle


                val rectWidth = 20.dp.toPx()
                drawRect(
                    color = Color.Red,
                    size = Size(rectWidth, rectWidth),
                    topLeft = Offset(
                        -rectWidth / 2 + center.x + (radius + strokeWidth) * cos(
                            angleInRadians
                        ),
                        -rectWidth / 2 + center.y + (radius + strokeWidth) * sin(
                            angleInRadians
                        )
                    )
                )
            }
        }
    }
}

val Float.degreeToRadian
    get() = (this * Math.PI / 180f).toFloat()

val Float.asAngle: Float
    get() = this * 360f / 100f
