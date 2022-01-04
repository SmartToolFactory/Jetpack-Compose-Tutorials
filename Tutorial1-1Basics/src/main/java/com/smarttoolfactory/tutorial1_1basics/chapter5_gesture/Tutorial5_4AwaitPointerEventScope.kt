package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Tutorial5_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(text = "1-) **awaitFirstDown()** Reads events until the first down is received")
//        AwaitFirstDownExample()
        TutorialText2(
            text = "Calculate Centroid",
            modifier = Modifier.padding(top = 8.dp)
        )
        CalculateCentroidExample()
        TutorialText2(
            text = "Calculate Pan",
            modifier = Modifier.padding(top = 8.dp)
        )
        CalculatePanExample()
        TutorialText2(
            text = "calculate Zoom",
            modifier = Modifier.padding(top = 8.dp)
        )
        CalculateZoomExample()
        TutorialText2(
            text = "Calculate Rotation",
            modifier = Modifier.padding(top = 8.dp)
        )
        CalculateRotationExample()
//        StyleableTutorialText(text = "3-) **awaitFirstDown()** Reads events until the first down is received")
//        AwaitTouchSlopOrCancellationExample()
    }

}


@Composable
private fun AwaitFirstDownExample() {
    var touchText by remember {
        mutableStateOf(
            "Touch to get awaitFirstDown() " +
                    "reads events until the first down is received."
        )
    }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }


    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    // This for showing on touch even we are actually updated even down.position is not
    var touchCounter by remember { mutableStateOf(0) }

    Modifier
    val pointerModifier = Modifier
        .padding(vertical = 8.dp, horizontal = 12.dp)
        .fillMaxWidth()
        .background(gestureColor)
        .height(250.dp)
        .pointerInput(Unit) {
            forEachGesture {
                coroutineScope {
                    awaitPointerEventScope {

                        val down: PointerInputChange = awaitFirstDown(requireUnconsumed = false)

                        val downPress: PressInteraction.Press =
                            PressInteraction.Press(down.position)

                        touchText = "DOWN Pointer down position: ${down.position}"
                        gestureColor = Orange400

                        val heldButtonJob = launch {
                            interactionSource.emit(downPress)

                            while (down.pressed) {
                                // 🔥 down.position is the down position we can't get current pointer position with down.position
                                touchText = "MOVE Pointer down " +
                                        "position: ${down.position}, touchCounter: $touchCounter"
                                delay(500)
                                touchCounter++
                                gestureColor = Blue400

                            }
                        }

                        val up = waitForUpOrCancellation()
                        heldButtonJob.cancel()

                        // If we move beyond Composable the final up change is returned
                        // or `null` if the event was canceled.
                        touchText = "UP Pointer up.position: ${(up?.position)}"

                        // Determine whether a cancel or release occurred, and create the interaction
                        val releaseOrCancel = when (up) {
                            null -> {
                                gestureColor = Red400
                                PressInteraction.Cancel(downPress)
                            }
                            else -> {
                                gestureColor = Green400
                                PressInteraction.Release(downPress)
                            }
                        }
                        launch {
                            // Send the result through the interaction source
                            interactionSource.emit(releaseOrCancel)
                        }


                    }
                }
            }
        }
        .indication(interactionSource, rememberRipple())

    GestureDisplayBox(pointerModifier, touchText)

}

@Composable
private fun CalculateCentroidExample() {

    var centroidSize by remember { mutableStateOf(50f) }
    var position by remember { mutableStateOf(Offset.Zero) }

    var touchText by remember {
        mutableStateOf(
            "Use single or multiple pointers to calculate centroid position and size."
        )
    }

    val scope = rememberCoroutineScope()

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

                    awaitFirstDown().also {
                        position = it.position
                    }

                    gestureColor = Orange400


                    // This is to show like touch down gesture before going to move event
                    scope.launch {
                        delay(50)
                        gestureColor = Blue400
                    }

                    do {
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

                        var eventChanges = ""

                        event.changes.forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                            eventChanges += "Index: $index, id: ${pointerInputChange.id}, pos: ${pointerInputChange.position}\n"
                        }

                        touchText = "EVENT changes size ${event.changes.size}\n" +
                                eventChanges +
                                "Centroid size: $size, position: $position"

                    } while (event.changes.any { it.pressed })

                    gestureColor = Green400
                }
            }
        }

    // 🔥 This outer box uses clipToBounds() to clip circle if it's out of box bounds
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clipToBounds()
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(pointerModifier.matchParentSize(), touchText)
    }

}

@Composable
private fun CalculatePanExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    var text by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }

    val modifier =
        Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer()
            .background(Color.Blue)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                            val offset = event.calculatePan()
                            offsetX.value += offset.x
                            offsetY.value += offset.y

                            text = "$offset"
                        } while (event.changes.any { it.pressed })
                    }
                }
            }
            .fillMaxWidth()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clipToBounds()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.landscape3),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )

        Text(
            text = text,
            color = Orange400,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(vertical = 2.dp)
                .align(Alignment.BottomStart)
        )

    }
}

@Composable
private fun CalculateRotationExample() {
    var angle by remember { mutableStateOf(0f) }

    var text by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }


    val modifier = Modifier
        .graphicsLayer(rotationZ = angle)
        .background(Color.Blue)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val rotation = event.calculateRotation()
                        angle += rotation
                    } while (event.changes.any { it.pressed })
                }
            }
        }
        .fillMaxWidth()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clipToBounds()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.landscape3),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )

        Text(
            text = text,
            color = Orange400,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(vertical = 2.dp)
                .align(Alignment.BottomStart)
        )

    }

}

@Composable
private fun CalculateZoomExample() {
    var zoom by remember { mutableStateOf(1f) }

    var text by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }


    val modifier = Modifier
        .graphicsLayer(scaleX = zoom, scaleY = zoom)
        .background(Color.Blue)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        zoom *= event.calculateZoom()
                    } while (event.changes.any { it.pressed })
                }
            }
        }
        .fillMaxWidth()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clipToBounds()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.landscape2),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )

        Text(
            text = text,
            color = Green400,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(vertical = 2.dp)
                .align(Alignment.BottomStart)
        )

    }

}

@Composable
private fun AwaitTouchSlopOrCancellationExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .onSizeChanged { size = it.toSize() }
    ) {
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(50.dp)
                .background(Color.Blue)
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val down = awaitFirstDown()
                            var change =
                                awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->

                                    val original = Offset(offsetX.value, offsetY.value)
                                    val summed = original + over

                                    val newValue = Offset(
                                        x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                        y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                                    )


                                    change.consumePositionChange()
                                    offsetX.value = newValue.x
                                    offsetY.value = newValue.y

                                    println("🔥 awaitTouchSlopOrCancellation() newValue: $newValue")
                                }

//                            while (change != null && change.pressed) {
//                                change = awaitDragOrCancellation(change.id)
//                                if (change != null && change.pressed) {
//                                    val original = Offset(offsetX.value, offsetY.value)
//                                    val summed = original + change.positionChange()
//                                    val newValue = Offset(
//                                        x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
//                                        y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
//                                    )
//                                    change.consumePositionChange()
//                                    offsetX.value = newValue.x
//                                    offsetY.value = newValue.y
//                                }
//                            }
                        }
                    }
                }
        )
    }
}