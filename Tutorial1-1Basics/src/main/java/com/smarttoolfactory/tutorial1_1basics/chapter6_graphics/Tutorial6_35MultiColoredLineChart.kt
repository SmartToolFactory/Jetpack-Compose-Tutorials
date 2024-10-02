package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400

@Preview
@Composable
fun Tutorial6_35Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    MultiColoredLineChart()
}

@Preview
@Composable
private fun MultiColoredLineChart() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        val path = remember {
            Path()
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
//              .graphicsLayer {
//                  compositingStrategy = CompositingStrategy.Offscreen
//              }
        ) {

            val canvasHeight = size.height

            if (path.isEmpty) {
                val points = getSinusoidalPoints(size, 30f)
                path.apply {
                    moveTo(30f, center.y)
                    points.forEach { offset: Offset ->
                        lineTo(offset.x, offset.y)
                    }
                }
            }

            drawWithLayer {

                // Destination
                drawPath(
                    color = Green400,
                    path = path,
                    style = Stroke(
                        width = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Source
                drawRect(
                    color = Orange400,
                    topLeft = Offset(0f, -10f),
                    size = Size(size.width, size.height / 3f + 10f),
                    blendMode = BlendMode.SrcIn
                )

                // Source
                drawRect(
                    color = Purple400,
                    topLeft = Offset(0f, size.height / 3f),
                    size = Size(size.width, size.height / 3f),
                    blendMode = BlendMode.SrcIn
                )
            }

            drawRect(
                color = Purple400.copy(alpha = .2f),
                topLeft = Offset(0f, size.height / 3f),
                size = Size(size.width, size.height / 3f),
            )

            drawLine(
                strokeWidth = 2.dp.toPx(),
                start = Offset(0f, size.height / 3f),
                end = Offset(size.width, size.height / 3f),
                color = Color.Gray,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            drawLine(
                strokeWidth = 2.dp.toPx(),
                start = Offset(0f, 2 * size.height / 3f),
                end = Offset(size.width, 2 * size.height / 3f),
                color = Color.Gray,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }
    }
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}
