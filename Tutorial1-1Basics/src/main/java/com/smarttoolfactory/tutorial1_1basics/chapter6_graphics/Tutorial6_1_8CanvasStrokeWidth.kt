package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun Tutorial6_1Screen8() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(text = "Default Stroke Drawing(Both Directions)")
        CanvasDefaultStroke()
        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(text = "Stroke Inwards")
        CanvasStrokeInside()
        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(text = "Stroke Outwards")
        CanvasStrokeOutside()
    }
}

@Composable
private fun CanvasDefaultStroke() {

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
                style = Stroke(width = newStrokeWidth)
            )
        }
    }
}

@Composable
private fun CanvasStrokeOutside() {

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
private fun CanvasStrokeInside() {

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
