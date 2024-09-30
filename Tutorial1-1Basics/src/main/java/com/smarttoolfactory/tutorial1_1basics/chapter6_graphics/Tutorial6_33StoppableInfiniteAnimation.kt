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
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    StoppableInfiniteAnimationSample()
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

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Text1")
            Column {
                Canvas(
                    modifier = Modifier.size(100.dp).rotate(rotateAnimationState.angle)
                        .border(2.dp, Color.Green)
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
    private val durationPerAngle = duration / 360f

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
                // Duration depends on how far current angle is to 360f
                // total duration is duration per angle multiplied with total angles to rotate
                val durationToZero = (durationPerAngle * (360 - currentValue)).toInt()
                animatable.snapTo(currentValue)
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