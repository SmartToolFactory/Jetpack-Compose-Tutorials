package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Preview
@Composable
private fun ClockArcSample() {
    Column {

        var seconds by remember {
            mutableStateOf(0)
        }

        var startClock by remember {
            mutableStateOf<ClockStartEvent?>(null)
        }

        LaunchedEffect(startClock) {

            if (startClock != null) {
                seconds = 0

                while (seconds != 60) {
                    delay(1000)
                    seconds++
                }
            }
        }

        val sweepAngle by rememberUpdatedState((seconds * 6).toFloat())

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .aspectRatio(1f)
        ) {
            val canvasWidth = size.width
            val arcDiameter = canvasWidth * .55f

            drawCircle(
                color = Color.White,
                style = Stroke(canvasWidth * 0.04f),
                radius = canvasWidth * .35f
            )

            drawArc(
                color = Color.White,
                270f,
                sweepAngle,
                true,
                topLeft = Offset((canvasWidth - arcDiameter) / 2, (canvasWidth - arcDiameter) / 2),
                size = Size(arcDiameter, arcDiameter)
            )

            val strokeWidth = 2.dp.toPx()
            for (i in 0..360 step 6) {
                rotate(i.toFloat()) {
                    drawLine(
                        color = Color.White,
                        strokeWidth = strokeWidth,
                        start = Offset(
                            x = canvasWidth * 0.90f,
                            y = center.y
                        ),
                        end = Offset(
                            x = canvasWidth * 0.93f,
                            y = center.y
                        ),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Button(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            onClick = {
                startClock = ClockStartEvent()
            }
        ) {
            Text("Start Clock...")
        }

    }
}

class ClockStartEvent