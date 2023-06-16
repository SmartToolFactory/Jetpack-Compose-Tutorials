package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Brown400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.Yellow400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial5_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Comment verticalScroll() when testing AwaitFirstDownExample example
            // because it causes waitForUpOrCancellation to return null when
            // there is a vertical gesture which consumes position change which also
            // waitForUpOrCancellation checks in a while loop
            // Check Consume examples to get familiar with it
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) **awaitFirstDown()** Reads events until the first down is received\n" +
                    "**Note** comment **verticalScroll** to observe " +
                    "results of **waitForUpOrCancellation** verticalScroll consumes position change" +
                    "which causes **waitForUpOrCancellation** to return NULL"
        )
        AwaitFirstDownExample()
        StyleableTutorialText(
            text = "2-) **awaitPointerEvent()**  returns pointer and event details"
        )
        AwaitPointerEventExample()
        AwaitPointerEventExample2()

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

    val pointerModifier = Modifier
        .padding(vertical = 8.dp, horizontal = 12.dp)
        .fillMaxWidth()
        .background(gestureColor)
        .height(90.dp)
        .pointerInput(Unit) {
            awaitEachGesture {

                val down: PointerInputChange = awaitFirstDown(requireUnconsumed = true)
                touchText = "DOWN Pointer down position: ${down.position}"
                gestureColor = Orange400

                // 🔥 Wait for Up Event, this is called if only one pointer exits
                // when it's up or moved out of Composable bounds
                // When multiple pointers touch Composable it requires only one to be
                // out of Composable bounds
                val upOrCancel: PointerInputChange? = waitForUpOrCancellation()

                if (upOrCancel?.position != null) {
                    touchText = "UP Pointer up.position: ${(upOrCancel.position)}"
                    gestureColor = Green400
                } else {
                    touchText = "UP CANCEL"
                    gestureColor = Red400
                }
            }
        }
    GestureDisplayBox(pointerModifier, touchText)
}

@Composable
private fun AwaitPointerEventExample() {

    var touchText by remember { mutableStateOf("Use single or multiple pointers.") }
    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    val pointerModifier = Modifier
        .pointerInput(Unit) {
                awaitPointerEventScope {

                    awaitFirstDown()
                    gestureColor = Orange400

                    do {
                        // 🔥🔥 This PointerEvent contains details including events,
                        // id, position and more
                        // Other events such as drag are structured with consume events
                        // using awaitPointerEvent in a while loop
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges = ""

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->

                                // 🔥🔥 If consume() is not called
                                // vertical scroll or other events interfere with current event
                                pointerInputChange.consume()

                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "pos: ${pointerInputChange.position}\n"
                            }

                        touchText = "EVENT changes size ${event.changes.size}\n" + eventChanges
                        gestureColor = Blue400
                    } while (event.changes.any { it.pressed })

                    gestureColor = Green400
                }
        }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(pointerModifier.matchParentSize(), touchText)
    }
}

/**
 * Same example as the one above. This one uses while(true) instead of do-while.
 */
@Composable
private fun AwaitPointerEventExample2() {

    var touchText by remember {
        mutableStateOf(
            "Use single or multiple pointers.\n" +
                    "This example uses while(true) loop"
        )
    }
    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    val pointerModifier = Modifier
        .pointerInput(Unit) {

                awaitPointerEventScope {

                    awaitFirstDown()
                    gestureColor = Orange400

                    // This is preferred way in default Compose gesture codes
                    // to loop gesture events and use consume or position changes to
                    // break while loop
                    while (true) {
                        // 🔥🔥 This PointerEvent contains details including events,
                        // id, position and more
                        // Other events such as drag are structured with consume events
                        // using awaitPointerEvent in a while loop
                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any { it.pressed }

                        // All of the pointers are up
                        if (!anyPressed) {
                            gestureColor = Green400
                            break
                        } else {
                            gestureColor = Blue400
                            var eventChanges = ""

                            event.changes
                                .map { pointerInputChange: PointerInputChange ->
                                    pointerInputChange.consume()
                                    eventChanges += "id: ${pointerInputChange.id}, " +
                                            "pos: ${pointerInputChange.position}\n"
                                }

                            touchText = "EVENT changes size ${event.changes.size}\n" + eventChanges
                        }
                    }
            }
        }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(120.dp)
            .background(gestureColor),
        contentAlignment = Alignment.Center
    ) {
        GestureDisplayBox(pointerModifier.matchParentSize(), touchText)
    }
}

@Composable
private fun AwaitTouchSlopOrCancellationExample() {

    val context = LocalContext.current

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "awaitTouchSlopOrCancellation waits for drag motion to pass touch slop to start drag"
        )
    }

    val modifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(80.dp)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down: PointerInputChange = awaitFirstDown()
                gestureColor = Orange400
                text = "awaitFirstDown() id: ${down.id}"
                println("🍏 DOWN: ${down.position}")

                // 🔥🔥 Waits for drag threshold to be passed by pointer
                // or it returns null if up event is triggered
                var change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->

                        Toast
                            .makeText(
                                context,
                                "awaitTouchSlopOrCancellation(down.id) passed for " +
                                        "id: ${down.id}, ${change.position}, over: $over",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                        println("⛺️ awaitTouchSlopOrCancellation ${change.position}, over: $over")
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + over

                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 80.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 80.dp.toPx())
                        )

                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes
                        // change.positionChanged() to return false.
                        change.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        gestureColor = Brown400
                        text =
                            "awaitTouchSlopOrCancellation()  down.id: ${down.id} change.id: ${change.id}" +
                                    "\nnewValue: $newValue"
                    }

                if (change == null) {
                    gestureColor = Red400
                    text = "awaitTouchSlopOrCancellation() is NULL"
                }

                while (change != null && change.pressed) {

                    gestureColor = Blue400

                    // 🔥 Calls awaitPointerEvent() in a while loop and checks drag change
                    change = awaitDragOrCancellation(change.id)

                    if (change != null && change.pressed) {
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + change.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 80.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 80.dp.toPx())
                        )
                        change.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        text =
                            "awaitDragOrCancellation() down.id: ${down.id} change.id: ${change.id}" +
                                    "\nnewValue: $newValue"
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
        .size(80.dp)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            awaitEachGesture {
                var down: PointerInputChange? = awaitFirstDown()

                gestureColor = Orange400
                text = "awaitFirstDown() id: ${down?.id}"

                while (down != null && down.pressed) {

                    // 🔥 Calls awaitPointerEvent() in a while loop and checks drag change
                    down = awaitDragOrCancellation(down.id)

                    if (down == null) {
                        gestureColor = Red400
                        text = "awaitDragOrCancellation() is NULL"
                    }

                    if (down != null && down.pressed) {

                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + down.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 80.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 80.dp.toPx())
                        )
                        down.consume()
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y

                        gestureColor = Blue400
                        text = "awaitDragOrCancellation()  down.id: ${down.id}" +
                                "\nnewValue: $newValue"
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

