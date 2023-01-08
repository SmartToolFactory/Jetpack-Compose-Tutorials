package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Tutorial6_10Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        RippleSample()
        RippleOnCanvasSample()
    }
}

@Composable
private fun RippleSample() {
    Box(modifier = Modifier
        .size(150.dp)
        .background(Color.Cyan)
        .clickable(
            interactionSource = MutableInteractionSource(),
            indication = rememberRipple(
                bounded = false,
                radius = 300.dp
            ),
            onClick = {

            }
        )
    )
}

@Composable
private fun RippleOnCanvasSample() {

    var rectangleCoordinates by remember { mutableStateOf(Rect.Zero) }
    val animatableAlpha = remember { Animatable(0f) }
    val animatableRadius = remember { Animatable(0f) }

    var touchPosition by remember { mutableStateOf(Offset.Unspecified) }

    var isTouched by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {

                val size = this.size
                val radius = size.width.coerceAtLeast(size.height) / 2

                awaitEachGesture {
                    val down: PointerInputChange = awaitFirstDown(requireUnconsumed = true)

                    val position = down.position

                    if (rectangleCoordinates.contains(position)) {

                        touchPosition = position

                        coroutineScope.launch {
                            animatableAlpha.animateTo(
                                targetValue = .3f,
                                animationSpec = keyframes {
                                    durationMillis = 150
                                    0.0f at 0 with LinearOutSlowInEasing
                                    0.2f at 75 with FastOutLinearInEasing
                                    0.25f at 100
                                    0.3f at 150
                                }
                            )
                        }

                        coroutineScope.launch {
                            animatableRadius.animateTo(
                                targetValue = radius.toFloat(),
                                animationSpec = keyframes {
                                    durationMillis = 150
                                    0.0f at 0 with LinearOutSlowInEasing
                                    radius * 0.4f at 30 with FastOutLinearInEasing
                                    radius * 0.5f at 75 with FastOutLinearInEasing
                                    radius * 0.7f at 100
                                    radius * 1f at 150
                                }
                            )
                        }

                        isTouched = true
                    }

                    waitForUpOrCancellation()

                    if (isTouched && touchPosition.isSpecified && touchPosition.isFinite) {
                        coroutineScope.launch {
                            animatableAlpha.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(150)
                            )
                            animatableRadius.snapTo(0f)
                        }
                    }

                    isTouched = false
                }
            }

    ) {
        val rectSize = Size(150.dp.toPx(), 150.dp.toPx())

        rectangleCoordinates = Rect(center, rectSize)

        drawRect(
            topLeft = center,
            size = rectSize,
            color = Color.Cyan
        )

        if (touchPosition.isSpecified && touchPosition.isFinite) {
//            clipRect(
//                left = rectangleCoordinates.left,
//                top = rectangleCoordinates.top,
//                right = rectangleCoordinates.right,
//                bottom = rectangleCoordinates.bottom
//            ) {
            drawCircle(
                center = touchPosition,
                color = Color.Gray.copy(alpha = animatableAlpha.value),
                radius = animatableRadius.value
            )
        }
//        }
    }
}
