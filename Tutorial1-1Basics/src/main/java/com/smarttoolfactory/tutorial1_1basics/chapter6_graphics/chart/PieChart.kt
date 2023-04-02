@file:OptIn(ExperimentalTextApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.degreeToRadian
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PieChart(
    modifier: Modifier,
    data: List<ChartData>,
    startAngle:Float = 0f
) {

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        val density = LocalDensity.current

        val width = constraints.maxWidth.toFloat()
        // Outer radius of chart. This is edge of strokw width as
        val radius = (width / 2f) * .9f
        val outerStrokeWidth = radius * .4f
        val innerRadius = radius - outerStrokeWidth

        val lineStrokeWidth = with(density) { 3.dp.toPx() }
        val innerStrokeWidth = outerStrokeWidth * .3f

        // Start angle of chart. Top center is -90, right center 0,
        // bottom center 90, left center 180
        val chartStartAngle = startAngle
        val animatableInitialSweepAngle = remember {
            Animatable(chartStartAngle)
        }

        val chartEndAngle = 360f + chartStartAngle

        val sum = data.sumOf {
            it.data.toDouble()
        }.toFloat()

        val coEfficient = 360f / sum
        var currentAngle = 0f
        val currentSweepAngle = animatableInitialSweepAngle.value

        val chartDataList = remember(data) {
            data.map {

                val chartData = it.data
                val range = currentAngle..currentAngle + chartData * coEfficient
                currentAngle += chartData * coEfficient

                AnimatedChartData(
                    color = it.color,
                    data = it.data,
                    selected = false,
                    range = range
                )
            }
        }

        chartDataList.forEach {
            LaunchedEffect(key1 = it.isSelected) {
                val targetValue = if (it.isSelected) 1.1f else 1f
                it.animatable.animateTo(targetValue, animationSpec = tween(500))
            }
        }

        val textMeasurer = rememberTextMeasurer()
        val textMeasureResults: List<TextLayoutResult> = remember(chartDataList) {
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

        LaunchedEffect(key1 = animatableInitialSweepAngle) {
            animatableInitialSweepAngle.animateTo(
                targetValue = chartEndAngle,
                animationSpec = tween(
                    delayMillis = 1000,
                    durationMillis = 1500
                )
            )
        }

        val chartModifier = Modifier
            .border(1.dp, Color.Red)
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position: Offset ->
                        val xPos = size.center.x - position.x
                        val yPos = size.center.y - position.y
                        val length = sqrt(xPos * xPos + yPos * yPos)
                        val isTouched = length in innerRadius..radius

                        if (isTouched) {
                            var touchAngle =
                                (-chartStartAngle + 180f + atan2(
                                    yPos,
                                    xPos
                                ) * 180 / Math.PI) % 360f

                            if (touchAngle < 0) {
                                touchAngle += 360f
                            }


                            chartDataList.forEach {
                                val range = it.range

                                val isTouchInArcSegment = touchAngle in range
                                if (it.isSelected) {
                                    it.isSelected = false
                                } else {
                                    it.isSelected = isTouchInArcSegment
                                }

                            }
                        }
                    }
                )
            }


        PieChartImpl(
            modifier = chartModifier,
            chartDataList = chartDataList,
            textMeasureResults = textMeasureResults,
            currentSweepAngle = currentSweepAngle,
            chartStartAngle = chartStartAngle,
            chartEndAngle = chartEndAngle,
            outerStrokeWidth = outerStrokeWidth,
            innerRadius = innerRadius,
            innerStrokeWidth = innerStrokeWidth,
            lineStrokeWidth = lineStrokeWidth
        )

    }
}

@Composable
private fun PieChartImpl(
    modifier: Modifier = Modifier,
    chartDataList: List<AnimatedChartData>,
    textMeasureResults: List<TextLayoutResult>,
    currentSweepAngle: Float,
    chartStartAngle: Float,
    chartEndAngle: Float,
    outerStrokeWidth: Float,
    innerRadius: Float,
    innerStrokeWidth: Float,
    lineStrokeWidth: Float
) {
    Canvas(modifier = modifier) {

        val width = size.width
        var startAngle = chartStartAngle

        for (index in 0..chartDataList.lastIndex) {

            val chartData = chartDataList[index]
            val range = chartData.range
            val sweepAngle = range.endInclusive - range.start
            val angleInRadians = (startAngle + sweepAngle / 2).degreeToRadian
            val textMeasureResult = textMeasureResults[index]
            val textSize = textMeasureResult.size

            val currentStrokeWidth = outerStrokeWidth

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

                    // Outer Arc Segment
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


                    // Inner Arc Segment
                    drawArc(
                        color = chartData.color.copy(alpha = .7f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.coerceAtMost(
                            currentSweepAngle - startAngle
                        ),
                        useCenter = false,
                        topLeft = Offset(
                            (width - 2 * innerRadius) / 2 + innerStrokeWidth / 2,
                            (width - 2 * innerRadius) / 2 + innerStrokeWidth / 2
                        ),
                        size = Size(
                            2 * innerRadius - innerStrokeWidth,
                            2 * innerRadius - innerStrokeWidth
                        ),
                        style = Stroke(innerStrokeWidth)
                    )
                }

                val textCenter = textSize.center

                if (currentSweepAngle == chartEndAngle) {
                    drawText(
                        textLayoutResult = textMeasureResult,
                        color = Color.Black,
                        topLeft = Offset(
                            -textCenter.x + center.x
                                    + (innerRadius + outerStrokeWidth / 2) * cos(angleInRadians),
                            -textCenter.y + center.y
                                    + (innerRadius + outerStrokeWidth / 2) * sin(angleInRadians)
                        )
                    )
                }
            }

            // Divider
            rotate(
                90f + startAngle
            ) {
                drawLine(
                    color = Color.White,
                    start = Offset(center.x, innerRadius + innerStrokeWidth),
                    end = Offset(center.x, 0f),
                    strokeWidth = lineStrokeWidth
                )
            }

            startAngle += sweepAngle
        }

    }
}


@Immutable
data class ChartData(val color: Color, val data: Float)

@Immutable
internal class AnimatedChartData(
    val color: Color,
    val data: Float,
    selected: Boolean = false,
    val range: ClosedFloatingPointRange<Float>,
    val animatable: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {
    var isSelected by mutableStateOf(selected)
}