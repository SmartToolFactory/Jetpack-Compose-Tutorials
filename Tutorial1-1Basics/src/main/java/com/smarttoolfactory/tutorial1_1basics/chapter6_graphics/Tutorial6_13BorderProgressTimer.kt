package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial6_13Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    BorderProgressSample1()
//    BorderProgressSample2()
}

private fun BorderProgressSample1() {
    val startDurationInSeconds = 20
    var currentTime by remember {
        mutableStateOf(startDurationInSeconds)
    }

    var targetValue by remember {
        mutableStateOf(100f)
    }

    var timerStarted by remember {
        mutableStateOf(false)
    }

    val progress by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(startDurationInSeconds * 1000, easing = LinearEasing)
    )

    LaunchedEffect(key1 = timerStarted) {
        if (timerStarted) {
            while (currentTime > 0) {
                delay(1000)
                currentTime--
            }
        }
        timerStarted = false

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // This is the progress path which wis changed using path measure
        val pathWithProgress by remember {
            mutableStateOf(Path())
        }

        // using path
        val pathMeasure by remember { mutableStateOf(PathMeasure()) }

        val path = remember {
            Path()
        }

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp, 140.dp)) {

                if (path.isEmpty) {
                    path.addRoundRect(
                        RoundRect(
                            Rect(offset = Offset.Zero, size),
                            cornerRadius = CornerRadius(60.dp.toPx(), 60.dp.toPx())
                        )
                    )
                }
                pathWithProgress.reset()

                pathMeasure.setPath(path, forceClosed = false)
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * progress / 100f,
                    pathWithProgress,
                    startWithMoveTo = true
                )


                clipPath(path) {
                    drawRect(Color.LightGray)
                }

                drawPath(
                    path = path,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Gray
                )

                drawPath(
                    path = pathWithProgress,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Blue
                )
            }

            Text(text = "$currentTime", fontSize = 40.sp, color = Color.Blue)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp, 140.dp)) {

                if (path.isEmpty) {
                    path.addRoundRect(
                        RoundRect(
                            Rect(offset = Offset.Zero, size),
                            cornerRadius = CornerRadius(60.dp.toPx(), 60.dp.toPx())
                        )
                    )
                }
                pathWithProgress.reset()

                pathMeasure.setPath(path, forceClosed = false)
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * progress.roundToInt() / 100f,
                    pathWithProgress,
                    startWithMoveTo = true
                )


                clipPath(path) {
                    drawRect(Color.LightGray)
                }

                drawPath(
                    path = path,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Gray
                )

                drawPath(
                    path = pathWithProgress,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Blue
                )
            }

            Text(text = "$currentTime", fontSize = 40.sp, color = Color.Blue)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp, 140.dp)) {

                if (path.isEmpty) {
                    path.addRoundRect(
                        RoundRect(
                            Rect(offset = Offset.Zero, size),
                            cornerRadius = CornerRadius(60.dp.toPx(), 60.dp.toPx())
                        )
                    )
                }
                pathWithProgress.reset()

                pathMeasure.setPath(path, forceClosed = false)
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * ((currentTime.toFloat() / startDurationInSeconds)),
                    pathWithProgress,
                    startWithMoveTo = true
                )


                clipPath(path) {
                    drawRect(Color.LightGray)
                }

                drawPath(
                    path = path,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Gray
                )

                drawPath(
                    path = pathWithProgress,
                    style = Stroke(
                        6.dp.toPx()
                    ),
                    color = Color.Blue
                )
            }

            Text(text = "$currentTime", fontSize = 40.sp, color = Color.Blue)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (currentTime > 0) {
                    targetValue = 0f
                    timerStarted = true
                } else {
                    currentTime = startDurationInSeconds
                    timerStarted = true
                }
            }) {
            Text(text = "Start Timer")
        }

        Text(
            text = "currentTime: $currentTime, " +
                    "progress: ${progress.toInt()}",
            color = if (progress == 0f) Color.DarkGray else Color.Red
        )

    }
}

@Preview
@Composable
fun BorderProgressSample2() {

    val pathMeasure by remember { mutableStateOf(PathMeasure()) }

    val path = remember {
        Path()
    }

    val pathWithProgress by remember {
        mutableStateOf(Path())
    }

    val animatable = remember {
        Animatable(0f)
    }

    val isFilled by remember {
        derivedStateOf { animatable.value == 100f }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

//        Text("Progress: ${animatable.value.toInt()}")
        Slider(
            value = animatable.value,
            onValueChange = {
                coroutineScope.launch {
                    animatable.animateTo(it)
                }
            },
            valueRange = 0f..100f
        )

        Icon(
            modifier = Modifier.size(128.dp)
                .drawBehind {

                    if (path.isEmpty) {
                        path.addRoundRect(
                            RoundRect(
                                Rect(offset = Offset.Zero, size),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                            )
                        )

                        pathMeasure.setPath(path, forceClosed = false)
                    }

                    pathWithProgress.reset()

                    pathMeasure.setPath(path, forceClosed = false)
                    pathMeasure.getSegment(
                        startDistance = 0f,
                        stopDistance = pathMeasure.length * animatable.value / 100f,
                        pathWithProgress,
                        startWithMoveTo = true
                    )

                    drawPath(
                        path = path,
                        style = Stroke(
                            4.dp.toPx()
                        ),
                        color = Color.Black
                    )

                    drawPath(
                        path = pathWithProgress,
                        style = Stroke(
                            4.dp.toPx()
                        ),
                        color = Color.Blue
                    )
                },
            tint = if (isFilled) Color.Blue else Color.Black,
            imageVector = Icons.Default.CarRental,
            contentDescription = null
        )
    }
}