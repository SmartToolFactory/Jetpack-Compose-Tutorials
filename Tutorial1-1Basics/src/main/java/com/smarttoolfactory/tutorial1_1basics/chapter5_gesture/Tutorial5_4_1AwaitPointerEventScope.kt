package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Tutorial5_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
        // ⚠️ Vertical Scroll cancels awaitPointerEventScope events, calls up
        // or scrolls when on a Composable with awaitPointerEventScope touch events
//            .verticalScroll(scrollState)
    ) {

        StyleableTutorialText(
            text = "1-) **awaitFirstDown()** Reads events until the first down is received")
        AwaitFirstDownExample()
        StyleableTutorialText(
            text = "2-) **awaitPointerEvent()**  returns pointer and event details")
        AwaitPointerEventExample()

        StyleableTutorialText(
            text = "3-) **awaitDragOrCancellation()** reads pointer events until " +
                    "drag detected or all pointers are up."
        )
        TutorialText2(text = "awaitTouchSlopOrCancellation -> awaitDragOrCancellation")
        AwaitTouchSlopOrCancellationExample()
        TutorialText2(text = "awaitDragOrCancellation only")
        AwaitDragOrCancellationExample()
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

    Modifier
    val pointerModifier = Modifier
        .padding(vertical = 8.dp, horizontal = 12.dp)
        .fillMaxWidth()
        .background(gestureColor)
        .height(90.dp)
        .pointerInput(Unit) {
            forEachGesture {
                coroutineScope {
                    awaitPointerEventScope {

                        val down: PointerInputChange = awaitFirstDown(requireUnconsumed = true)
                        touchText = "DOWN Pointer down position: ${down.position}"
                        gestureColor = Orange400

                        // 🔥 Wait for Up Event, this is called if only one pointer exits
                        // when it's up or moved out of Composable bounds
                        // When multiple pointers touch Composable it requires only one to be
                        // out of Composable bounds
                        val up = waitForUpOrCancellation()

                        if (up?.position != null) {
                            touchText = "UP Pointer up.position: ${(up.position)}"
                            gestureColor = Green400
                        } else {
                            touchText = "UP CANCEL"
                            gestureColor = Red400
                        }

                    }
                }
            }
        }
        .indication(interactionSource, rememberRipple())

    GestureDisplayBox(pointerModifier, touchText)

}

@Composable
private fun AwaitPointerEventExample() {

    var touchText by remember {
        mutableStateOf(
            "Use single or multiple pointers to calculate centroid position and size."
        )
    }

    val scope = rememberCoroutineScope()

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    val pointerModifier = Modifier
        .pointerInput(Unit) {
            forEachGesture {

                awaitPointerEventScope {

                    awaitFirstDown()
                    gestureColor = Orange400

                    // This is to show like touch down gesture before going to move event
                    scope.launch {
                        delay(50)
                        gestureColor = Blue400
                    }

                    do {
                        // 🔥 This PointerEvent contains details details including events,
                        // id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges = ""

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "pos: ${pointerInputChange.position}\n"
                            }

                        touchText = "EVENT changes size ${event.changes.size}\n" + eventChanges
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
            .height(120.dp)
            .clipToBounds()
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(pointerModifier.matchParentSize(), touchText)
    }
}

@Composable
private fun AwaitTouchSlopOrCancellationExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "awaitTouchSlopOrCancellation waits for drag motion to pass touch slop to start drag"
        )
    }

    val modifier =      Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(50.dp)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .background(Yellow400)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down: PointerInputChange = awaitFirstDown()
                    gestureColor = Orange400
                    text = "awaitFirstDown()"

                    var change: PointerInputChange? =
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
                            gestureColor = Brown400
                            text = "awaitTouchSlopOrCancellation() newValue: $newValue"

                        }


                    if (change == null) {
                        gestureColor = Red400
                        text = "awaitTouchSlopOrCancellation() is NULL"
                    }

                    while (change != null && change.pressed) {

                        gestureColor = Blue400

                        change = awaitDragOrCancellation(change.id)

                        if (change != null && change.pressed) {
                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + change.positionChange()
                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )
                            change.consumePositionChange()
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y

                            text = "awaitDragOrCancellation()\nnewValue: $newValue"
                        }
                    }

                    if (gestureColor != Red400) {
                        gestureColor = Color.LightGray
                    }
                }
            }
        }

    Box(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor)
            .onSizeChanged { size = it.toSize() }
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(modifier=modifier)
    }
}

@Composable
private fun AwaitDragOrCancellationExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "Without awaitTouchSlopOrCancellation drag starts when awaitFirstDown is invoked."
        )
    }

    val modifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(50.dp)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    var down: PointerInputChange? = awaitFirstDown()

                    gestureColor = Orange400
                    text = "awaitFirstDown()"

                    while (down != null && down.pressed) {

                        down = awaitDragOrCancellation(down.id)

                        if (down == null) {
                            gestureColor = Red400
                            text = "awaitDragOrCancellation() is NULL"

                        }

                        if (down != null && down.pressed) {

                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + down.positionChange()
                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )
                            down.consumePositionChange()
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y

                            gestureColor = Blue400
                            text = "awaitDragOrCancellation()\nnewValue: $newValue"
                        }
                    }
                }

                if (gestureColor != Red400) {
                    gestureColor = Color.LightGray
                }
            }
        }

    Box(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor)
            .onSizeChanged { size = it.toSize() }
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )
        Box(modifier = modifier)
    }
}

