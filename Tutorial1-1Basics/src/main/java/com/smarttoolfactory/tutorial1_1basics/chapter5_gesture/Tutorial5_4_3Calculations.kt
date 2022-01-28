package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun Tutorial5_4Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scrollState: ScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        StyleableTutorialText(
            text =
            "1-) Use **awaitPointerEventScope** functions to calculate centroid " +
                    "size and position, zoom, pan, and rotation.\n" +
                    "In this example scrollState interferes with **awaitPointerEventScope** " +
                    "because of this a callback is used for calling **scrollState.stop()**"
        )

        TutorialText2(
            text = "Calculate Centroid",
            modifier = Modifier.padding(top = 8.dp)
        )

        CalculateCentroidExample {
            coroutineScope.launch {
                scrollState.stopScroll(MutatePriority.PreventUserInput)
                println("🔥 CANCELING SCROLL")
            }
        }

        TutorialText2(
            text = "Calculate Zoom",
            modifier = Modifier.padding(top = 8.dp)
        )
        CalculateZoomExample {
            coroutineScope.launch {
                scrollState.stopScroll(MutatePriority.PreventUserInput)
                println("🔥 CANCELING SCROLL")
            }
        }

        TutorialText2(
            text = "Calculate Pan",
            modifier = Modifier.padding(top = 20.dp)
        )
        CalculatePanExample {
            coroutineScope.launch {
                scrollState.stopScroll(MutatePriority.PreventUserInput)
                println("🔥 CANCELING SCROLL")
            }
        }

        TutorialText2(
            text = "Calculate Rotation",
            modifier = Modifier.padding(top = 20.dp)
        )
        CalculateRotationExample {
            coroutineScope.launch {
                scrollState.stopScroll(MutatePriority.PreventUserInput)
                println("🔥 CANCELING SCROLL")
            }
        }


    }
}


@Composable
private fun CalculateCentroidExample(onDown: () -> Unit) {

    var centroidSize by remember { mutableStateOf(50f) }
    var position by remember { mutableStateOf(Offset.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    val pointerModifier = Modifier
        .drawWithContent {
            drawContent()
            // Draw a circle where the gesture is
            drawCircle(Pink400, centroidSize, center = position)
        }
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    // This is for canceling ScrollState in Parent Column, not related with example
                    // More spaced need to scroll so added verticalScroll to parent
                    onDown()

                    // Wait for at least one pointer to press down, and set first contact position
                    awaitFirstDown().also {
                        position = it.position
                    }

                    gestureColor = Blue400

                    do {
                        // This is for canceling ScrollState in Parent Column, not related with example
                        // More spaced need to scroll so added verticalScroll to parent
                        onDown()

                        // This PointerEvent contains details details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        val size: Float = event.calculateCentroidSize()
                        if (size != 0f) {
                            centroidSize = event.calculateCentroidSize()
                        }

                        val centroid: Offset = event.calculateCentroid()
                        if (centroid != Offset.Unspecified) {
                            position = centroid
                        }

                    } while (event.changes.any { it.pressed })

                    gestureColor = Green400
                }
            }
        }

    // 🔥 This outer box uses clipToBounds() to clip circle if it's out of box bounds
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clipToBounds()
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(
            pointerModifier.matchParentSize(),
            "Use pointers to calculate center of pointers and draw a circle"
        )
    }
}

@Composable
private fun CalculateZoomExample(onDown: () -> Unit) {
    var zoom by remember { mutableStateOf(1f) }

    var text by remember {
        mutableStateOf(
            "Use pinch gesture to zoom"
        )
    }

    val imageModifier = Modifier
        .graphicsLayer(scaleX = zoom, scaleY = zoom)
        .background(Color.Blue)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    // This is for canceling ScrollState in Parent Column, not related with example
                    // More spaced need to scroll so added verticalScroll to parent
                    onDown()

                    // Wait for at least one pointer to press down
                    awaitFirstDown()
                    do {
                        // This is for canceling ScrollState in Parent Column, not related with example
                        // More spaced need to scroll so added verticalScroll to parent
                        onDown()

                        val event = awaitPointerEvent()
                        zoom *= event.calculateZoom()
                        text = "Zoom $zoom"
                    } while (event.changes.any { it.pressed })
                }
            }
        }
        .fillMaxWidth()

    ImageBox(boxModifier, imageModifier, R.drawable.landscape3, text)
}


@Composable
private fun CalculatePanExample(onDown: () -> Unit) {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    var text by remember {
        mutableStateOf(
            " Move image with single finger in either x or y coordinates."
        )
    }

    val imageModifier =
        Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer()
            .background(Color.Blue)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        // This is for canceling ScrollState in Parent Column, not related with example
                        // More spaced need to scroll so added verticalScroll to parent
                        onDown()

                        // Wait for at least one pointer to press down
                        awaitFirstDown()

                        do {
                            // This is for canceling ScrollState in Parent Column, not related with example
                            // More spaced need to scroll so added verticalScroll to parent
                            onDown()

                            val event = awaitPointerEvent()
                            val offset = event.calculatePan()
                            offsetX.value += offset.x
                            offsetY.value += offset.y

                            text = "Pan $offset"
                        } while (event.changes.any { it.pressed })
                    }
                }
            }
            .fillMaxWidth()

    ImageBox(boxModifier, imageModifier, R.drawable.landscape1, text, Blue400)

}

@Composable
private fun CalculateRotationExample(onDown: () -> Unit) {
    var angle by remember { mutableStateOf(0f) }

    var text by remember {
        mutableStateOf("Rotate image using two fingers with twisting gesture.")
    }


    val imageModifier = Modifier
        .graphicsLayer(rotationZ = angle)
        .background(Color.Blue)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // This is for canceling ScrollState in Parent Column, not related with example
                    // More spaced need to scroll so added verticalScroll to parent
                    onDown()

                    // Wait for at least one pointer to press down
                    awaitFirstDown()

                    do {
                        // This is for canceling ScrollState in Parent Column, not related with example
                        // More spaced need to scroll so added verticalScroll to parent
                        onDown()

                        val event = awaitPointerEvent()
                        val rotation = event.calculateRotation()
                        angle += rotation

                        text = "Angle $angle"
                    } while (event.changes.any { it.pressed })
                }
            }
        }
        .fillMaxWidth()

    ImageBox(boxModifier, imageModifier, R.drawable.landscape2, text, Green400)
}
