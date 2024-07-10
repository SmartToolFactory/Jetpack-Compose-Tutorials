package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.atan2

@Preview
@Composable
fun DragRotateBox() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var rotation by remember { mutableStateOf(0f) }
        var position by remember { mutableStateOf(Offset.Zero) }

        val boxSize = 100.dp
        val handleSize = 20.dp

        var initialTouch = Offset.Zero

        val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }

        val center = Offset(boxSizePx, boxSizePx)


        // Main Box
        Box(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = rotation,
                    translationX = position.x,
                    translationY = position.y
                )
                .background(Color.Blue)
                .size(boxSize)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        position += pan.rotateBy(rotation)

                    }
                }
        ) {
            // Rotation handler
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .background(Color.Red)
                    .align(Alignment.TopCenter)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                initialTouch = offset
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val angle =
                                    calculateRotationAngle(center, initialTouch, change.position)
                                rotation += angle
                            }
                        )
                    }
            )
        }
    }
}

fun calculateRotationAngle(pivot: Offset, initialTouch: Offset, currentTouch: Offset): Float {
    val initialVector = initialTouch - pivot
    val currentVector = currentTouch - pivot

    val initialAngle = atan2(initialVector.y, initialVector.x)
    val currentAngle = atan2(currentVector.y, currentVector.x)

    return Math.toDegrees((currentAngle - initialAngle).toDouble()).toFloat()
}
