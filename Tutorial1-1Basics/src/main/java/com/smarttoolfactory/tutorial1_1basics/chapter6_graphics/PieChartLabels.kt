package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.Yellow400
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
private fun PieChartWithText() {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        var chartStartAngle by remember {
            mutableFloatStateOf(0f)
        }

        Text("Chart Start angle: ${chartStartAngle.toInt()}")
        Slider(
            value = chartStartAngle,
            onValueChange = {
                chartStartAngle = it
            },
            valueRange = 0f..360f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val chartDataList = listOf(
                PieChartData(Pink400, 10f),
                PieChartData(Orange400, 30f),
                PieChartData(Yellow400, 40f),
                PieChartData(Blue400, 20f)
            )

            val textMeasurer = rememberTextMeasurer()
            val textMeasureResults = remember(chartDataList) {
                chartDataList.map {
                    textMeasurer.measure(
                        text = "${it.data.toInt()}%",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val width = size.width
                val radius = width * .22f
                val strokeWidth = radius * .6f
                val outerRadius = radius + strokeWidth + strokeWidth / 2
                val diameter = (radius + strokeWidth) * 2
                val topLeft = (width - diameter) / 2f

                var startAngle = chartStartAngle

                for (index in 0..chartDataList.lastIndex) {

                    startAngle %= 360

                    val chartData = chartDataList[index]
                    val sweepAngle = chartData.data.asAngle
                    val textMeasureResult = textMeasureResults[index]
                    val textSize = textMeasureResult.size

                    val offset = 10.dp.toPx()

                    drawArc(
                        color = chartData.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(topLeft, topLeft),
                        size = Size(diameter, diameter),
                        style = Stroke(strokeWidth)
                    )

                    val rect = textMeasureResult.getBoundingBox(0)

                    val adjustedAngle = (startAngle) % 360

                    val cos = cos(adjustedAngle.degreeToRadian)
                    val sin = sin(adjustedAngle.degreeToRadian)

                    val textOffset = getTextOffsets(startAngle, textSize)
                    val textOffsetX = textOffset.x
                    val textOffsetY = textOffset.y

                    drawCircle(
                        color = Color.Blue,
                        radius = outerRadius,
                        style = Stroke(2.dp.toPx())
                    )

                    drawCircle(
                        color = Color.Magenta,
                        radius = outerRadius + offset,
                        style = Stroke(2.dp.toPx())
                    )

                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(
                            x = rect.topLeft.x + center.x + textOffsetX + (offset + outerRadius) * cos,
                            y = rect.topLeft.y + center.y + textOffsetY + (offset + outerRadius) * sin
                        ),
                        size = textSize.toSize(),
                        style = Stroke(3.dp.toPx())
                    )

                    drawText(
                        textLayoutResult = textMeasureResult,
                        color = Color.DarkGray,
                        topLeft = Offset(
                            x = center.x + textOffsetX + (offset + outerRadius) * cos,
                            y = center.y + textOffsetY + (offset + outerRadius) * sin
                        )
                    )

                    startAngle += sweepAngle
                }
            }
        }
    }

}

private fun getTextOffsets(startAngle: Float, textSize: IntSize): Offset {
    var textOffsetX: Int = 0
    var textOffsetY: Int = 0

    when (startAngle) {
        in 0f..90f -> {
            textOffsetX = if (startAngle < 60) 0
            else (-textSize.width / 2 * ((startAngle - 60) / 30)).toInt()

            textOffsetY = 0
        }

        in 90f..180f -> {
            textOffsetX = (-textSize.width / 2 - textSize.width / 2 * (startAngle - 90f) / 45).toInt()
                .coerceAtLeast(-textSize.width)

            textOffsetY = if (startAngle < 135) 0
            else (-textSize.height / 2 * ((startAngle - 135) / 45)).toInt()
        }

        in 180f..270f -> {
            textOffsetX = if (startAngle < 240) -textSize.width
            else (-textSize.width + textSize.width / 2 * (startAngle - 240) / 30).toInt()

            textOffsetY = if (startAngle < 225) (-textSize.height / 2 * ((startAngle - 135) / 45)).toInt()
            else -textSize.height
        }

        else -> {
            textOffsetX =
                if (startAngle < 315) (-textSize.width / 2 + (textSize.width / 2) * (startAngle - 270) / 45).toInt()
                else 0

            textOffsetY = if (startAngle < 315) -textSize.height
            else (-textSize.height + textSize.height * (startAngle - 315) / 45).toInt()
        }
    }
    return Offset(textOffsetX.toFloat(), textOffsetY.toFloat())
}

@Immutable
private data class PieChartData(val color: Color, val data: Float)
