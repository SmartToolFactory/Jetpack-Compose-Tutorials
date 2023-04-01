@file:OptIn(ExperimentalTextApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Tutorial6_14Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    val animatable = remember {
        Animatable(-90f)
    }

    val finalValue = 270f

    LaunchedEffect(key1 = animatable) {
        animatable.animateTo(
            targetValue = finalValue,
            animationSpec = tween(
                delayMillis = 4000,
                durationMillis = 2000
            )
        )
    }
    val currentSweepAngle = animatable.value


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        val chartDataList = listOf(
            ChartData(Pink400, 10f),
            ChartData(Orange400, 20f),
            ChartData(Yellow400, 15f),
            ChartData(Green400, 5f),
            ChartData(Blue400, 50f),
        )

        val textMeasurer = rememberTextMeasurer()
        val textMeasureResults = remember(chartDataList) {
            chartDataList.map {
                textMeasurer.measure(
                    text = "%${it.data.toInt()}",
                    style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
        }

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
                val angleInRadians = (startAngle + sweepAngle / 2).degreeToAngle
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
                        color = Brown400,
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

private val Float.degreeToAngle
    get() = (this * Math.PI / 180f).toFloat()

private val Float.asAngle: Float
    get() = this * 360f / 100f

@Immutable
data class ChartData(val color: Color, val data: Float)