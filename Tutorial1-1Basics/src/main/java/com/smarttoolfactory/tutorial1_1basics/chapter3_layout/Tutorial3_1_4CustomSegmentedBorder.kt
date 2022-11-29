package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial3_1Screen4() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        StyleableTutorialText(
            text = "Use **composed** to create segmented border and segmented clip modifiers",
            bullets = false
        )
        SegmentedBorderSample()
    }
}

@Composable
private fun SegmentedBorderSample() {
    Row {
        repeat(3) {

            val order = when (it) {
                0 -> BorderOrder.Start
                2 -> BorderOrder.End
                else -> BorderOrder.Center
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .segmentedClip(borderOrder = order, cornerPercent = 40)
                    .segmentedBorder(
                        strokeWidth = 2.dp,
                        color = Color(0xff9E9D24),
                        borderOrder = order,
                        cornerPercent = 40,
                        divider = false
                    )
                    .clickable {

                    }
                    .padding(4.dp)
            ) {
                Text(text = "$it")
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row {
        repeat(4) {

            val order = when (it) {
                0 -> BorderOrder.Start
                3 -> BorderOrder.End
                else -> BorderOrder.Center
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .segmentedBorder(
                        strokeWidth = 2.dp,
                        color = Color(0xffEC407A),
                        borderOrder = order,
                        cornerPercent = 50,
                        divider = true
                    )
                    .padding(4.dp)
            ) {
                Text(text = "$it")
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row {
        repeat(5) {

            val order = when (it) {
                0 -> BorderOrder.Start
                4 -> BorderOrder.End
                else -> BorderOrder.Center
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .segmentedClip(borderOrder = order, cornerPercent = 20)
                    .background(Blue400)
                    .segmentedBorder(
                        strokeWidth = 3.dp,
                        color = Color(0xffF9A825),
                        borderOrder = order,
                        cornerPercent = 20,
                        divider = true
                    )
                    .clickable {

                    }
                    .padding(4.dp)
            ) {
                Text(text = "$it", color = Color.White)
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .segmentedClip(borderOrder = BorderOrder.Start, cornerPercent = 50)
                .segmentedBorder(
                    strokeWidth = 3.dp,
                    color = Color(0xffAFB42B),
                    borderOrder = BorderOrder.Start,
                    cornerPercent = 50,
                    divider = true
                )
                .clickable {

                }
                .padding(4.dp)
        ) {
            Text(text = "Songs", color = Color(0xffAFB42B))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .segmentedClip(borderOrder = BorderOrder.Center, cornerPercent = 50)
                .segmentedBorder(
                    strokeWidth = 3.dp,
                    color = Color(0xffAFB42B),
                    borderOrder = BorderOrder.Center,
                    cornerPercent = 50,
                    divider = true
                )
                .clickable {

                }
                .padding(4.dp)
        ) {
            Text(text = "Albums", color = Color(0xffAFB42B))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .segmentedClip(borderOrder = BorderOrder.End, cornerPercent = 50)
                .segmentedBorder(
                    strokeWidth = 3.dp,
                    color = Color(0xffAFB42B),
                    borderOrder = BorderOrder.End,
                    cornerPercent = 50,
                    divider = true
                )
                .clickable {

                }
                .padding(4.dp)
        ) {
            Text(text = "Podcasts", color = Color(0xffAFB42B))
        }
    }
}

enum class BorderOrder {
    Start, Center, End
}

fun Modifier.segmentedBorder(
    strokeWidth: Dp,
    color: Color,
    borderOrder: BorderOrder,
    cornerPercent: Int = 0,
    divider: Boolean = false
) = composed(
    factory = {

        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawWithContent {
            val width = size.width
            val height = size.height
            val cornerRadius = height * cornerPercent / 100

            drawContent()

            when (borderOrder) {
                BorderOrder.Start -> {

                    drawLine(
                        color = color,
                        start = Offset(x = width, y = 0f),
                        end = Offset(x = cornerRadius, y = 0f),
                        strokeWidth = strokeWidthPx
                    )

                    // Top left arc
                    drawArc(
                        color = color,
                        startAngle = 180f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset.Zero,
                        size = Size(cornerRadius * 2, cornerRadius * 2),
                        style = Stroke(width = strokeWidthPx)
                    )
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = cornerRadius),
                        end = Offset(x = 0f, y = height - cornerRadius),
                        strokeWidth = strokeWidthPx
                    )
                    // Bottom left arc
                    drawArc(
                        color = color,
                        startAngle = 90f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(x = 0f, y = height - 2 * cornerRadius),
                        size = Size(cornerRadius * 2, cornerRadius * 2),
                        style = Stroke(width = strokeWidthPx)
                    )
                    drawLine(
                        color = color,
                        start = Offset(x = cornerRadius, y = height),
                        end = Offset(x = width, y = height),
                        strokeWidth = strokeWidthPx
                    )
                }
                BorderOrder.Center -> {
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = width, y = 0f),
                        strokeWidth = strokeWidthPx
                    )
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = height),
                        end = Offset(x = width, y = height),
                        strokeWidth = strokeWidthPx
                    )

                    if (divider) {
                        drawLine(
                            color = color,
                            start = Offset(x = 0f, y = 0f),
                            end = Offset(x = 0f, y = height),
                            strokeWidth = strokeWidthPx
                        )
                    }
                }
                else -> {

                    if (divider) {
                        drawLine(
                            color = color,
                            start = Offset(x = 0f, y = 0f),
                            end = Offset(x = 0f, y = height),
                            strokeWidth = strokeWidthPx
                        )
                    }

                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = width - cornerRadius, y = 0f),
                        strokeWidth = strokeWidthPx
                    )

                    // Top right arc
                    drawArc(
                        color = color,
                        startAngle = 270f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(x = width - cornerRadius * 2, y = 0f),
                        size = Size(cornerRadius * 2, cornerRadius * 2),
                        style = Stroke(width = strokeWidthPx)
                    )
                    drawLine(
                        color = color,
                        start = Offset(x = width, y = cornerRadius),
                        end = Offset(x = width, y = height - cornerRadius),
                        strokeWidth = strokeWidthPx
                    )
                    // Bottom right arc
                    drawArc(
                        color = color,
                        startAngle = 0f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(
                            x = width - 2 * cornerRadius,
                            y = height - 2 * cornerRadius
                        ),
                        size = Size(cornerRadius * 2, cornerRadius * 2),
                        style = Stroke(width = strokeWidthPx)
                    )
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = height),
                        end = Offset(x = width - cornerRadius, y = height),
                        strokeWidth = strokeWidthPx
                    )
                }
            }
        }
    }
)

fun Modifier.segmentedClip(
    borderOrder: BorderOrder,
    cornerPercent: Int = 0,
) = composed(
    factory = {

        val shape = remember {
            when (borderOrder) {
                BorderOrder.Start -> {
                    RoundedCornerShape(
                        topStartPercent = cornerPercent,
                        bottomStartPercent = cornerPercent
                    )
                }
                BorderOrder.End -> {
                    RoundedCornerShape(
                        topEndPercent = cornerPercent,
                        bottomEndPercent = cornerPercent
                    )
                }
                else -> {
                    RectangleShape
                }
            }
        }

        Modifier.clip(shape)
    }
)

