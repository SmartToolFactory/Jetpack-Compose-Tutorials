package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.math.atan2

@Preview
@Composable
fun Tutorial5_13Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Rotate Pan DragBox")
        StyleableTutorialText(
            "Rotate box from red circle or pan to change position.",
            bullets = false
        )
        DragRotateBox()
    }
}

@Preview
@Composable
private fun DragRotateBox() {

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
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Blue400)
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
                    .background(Red400, CircleShape)
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
