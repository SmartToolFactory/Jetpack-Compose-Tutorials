package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


@Preview
@Composable
fun Tutorial6_33Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        InfiniteRotationInterruptionSample()
        Spacer(Modifier.height(16.dp))
        StoppableInfiniteAnimationSample()
    }
}

@Preview
@Composable
fun AnimatableIntteruptionSample() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val animatable = remember {
            Animatable(0f)
        }

        val coroutineScope = rememberCoroutineScope()

        Canvas(
            modifier = Modifier.size(100.dp).rotate(animatable.value)
                .border(2.dp, Color.Green, CircleShape)
        ) {

            drawLine(
                start = center,
                end = Offset(center.x, 0f),
                color = Color.Red,
                strokeWidth = 4.dp.toPx()
            )
        }

        Text(
            "animatable2: ${animatable.value.toInt()}\n"
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    try {
                        val result = animatable.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                        )

                        println("Result: $result")
                    } catch (e: CancellationException) {
                        println("Exception: ${e.message}")
                    }

                }
            }
        ) {
            Text("Animate to 360")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    animatable.snapTo(350f)
                }
            }
        ) {
            Text("Snap to 350")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    animatable.snapTo(0f)
                }
            }
        ) {
            Text("Snap to 0")
        }
    }
}

@Preview
@Composable
fun InfiniteRotationInterruptionSample() {

    var animationDuration by remember { mutableIntStateOf(2000) }

    val animatable = remember {
        Animatable(0f)
    }

    val animatable2 = remember {
        Animatable(0f)
    }

    LaunchedEffect(animationDuration) {
        while (isActive) {
            try {
                animatable.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
            } catch (e: CancellationException) {
                println("Animation canceled with: $e")
            }

            if (animatable.value >= 360f) {
                animatable.snapTo(targetValue = 0f)
            }
        }
    }

    LaunchedEffect(animationDuration) {
        while (isActive) {
            val currentValue = animatable2.value
            try {
                animatable2.animateTo(
                    targetValue = 360f,
                    animationSpec = tween((animationDuration * (360f - currentValue) / 360f).toInt(), easing = LinearEasing)
                )

            } catch (e: CancellationException) {
                println("Animation2 canceled with: $e")
            }

            if (animatable2.value >= 360f) {
                animatable2.snapTo(targetValue = 0f)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Default Animatable Behaviour", fontSize = 24.sp)
        Canvas(
            modifier = Modifier.size(100.dp).rotate(animatable.value)
                .border(2.dp, Color.Green, CircleShape)
        ) {

            drawLine(
                start = center,
                end = Offset(center.x, 0f),
                color = Color.Red,
                strokeWidth = 4.dp.toPx()
            )
        }

        Text(
            "animatable2: ${animatable.value.toInt()}\n" +
                    "animationDuration: $animationDuration"
        )

        Text("Adjust duration after Interruption", fontSize = 24.sp)

        Canvas(
            modifier = Modifier.size(100.dp).rotate(animatable2.value)
                .border(2.dp, Color.Green, CircleShape)
        ) {

            drawLine(
                start = center,
                end = Offset(center.x, 0f),
                color = Color.Red,
                strokeWidth = 4.dp.toPx()
            )
        }

        Text(
            "animatable: ${animatable2.value.toInt()}\n" +
                    "animationDuration: $animationDuration"
        )

        Button(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            onClick = {
                animationDuration += 4000
            }
        ) {
            Text("Change duration")
        }
    }
}

@Preview
@Composable
private fun StoppableInfiniteAnimationSample() {
    val coroutineScope = rememberCoroutineScope()

    val rotateAnimationState = remember {
        RotateAnimationState(
            coroutineScope = coroutineScope,
            duration = 2000
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {


        Text("Infinite Stoppable Animatable", fontSize = 24.sp)

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Text1")
            Column {
                Canvas(
                    modifier = Modifier.size(100.dp)
                        .rotate(rotateAnimationState.angle)
                        .border(2.dp, Color.Green, CircleShape)
                ) {

                    drawLine(
                        start = center,
                        end = Offset(center.x, 0f),
                        color = Color.Red,
                        strokeWidth = 4.dp.toPx()
                    )
                }

                Spacer(Modifier.height(16.dp))
                Icon(
                    modifier = Modifier
                        .size(60.dp)
                        .graphicsLayer {
                            rotationZ = rotateAnimationState.angle
                        },
                    imageVector = Icons.Default.ScreenRotation,
                    contentDescription = null
                )
            }
            Text("Text2")
        }

        Spacer(Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Angle: ${rotateAnimationState.angle.toInt()}\n" +
                    "status: ${rotateAnimationState.rotationStatus}",
            fontSize = 24.sp
        )

        Button(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            onClick = {
                rotateAnimationState.start()
            }
        ) {
            Text("Start")
        }

        Button(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            onClick = {
                rotateAnimationState.stop()
            }
        ) {
            Text("Stop")
        }
    }
}

class RotateAnimationState(
    val coroutineScope: CoroutineScope,
    val duration: Int,
) {
    val angle: Float
        get() = animatable.value

    private val animatable = Animatable(0f)

    var rotationStatus: RotationStatus = RotationStatus.Idle

    fun start() {
        if (rotationStatus == RotationStatus.Idle) {
            coroutineScope.launch {
                rotationStatus = RotationStatus.Rotating

                while (isActive && rotationStatus == RotationStatus.Rotating) {
                    animatable.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(
                            durationMillis = duration,
                            easing = LinearEasing
                        )
                    )

                    yield()

                    if (rotationStatus == RotationStatus.Rotating) {
                        animatable.snapTo(0f)
                    }
                }
            }
        }
    }

    fun stop() {
        if (rotationStatus == RotationStatus.Rotating) {
            coroutineScope.launch {
                rotationStatus = RotationStatus.Stopping
                val currentValue = animatable.value
                val durationPerAngle = duration / 360f
                // Duration depends on how far current angle is to 360f
                // total duration is duration per angle multiplied with total angles to rotate
                val durationToZero = (durationPerAngle * (360 - currentValue)).toInt()
                animatable.animateTo(
                    targetValue = 360f,
                    tween(
                        durationMillis = durationToZero,
                        easing = LinearEasing
                    )
                )
                animatable.snapTo(0f)
                rotationStatus = RotationStatus.Idle
            }
        }
    }
}

enum class RotationStatus {
    Idle, Rotating, Stopping
}