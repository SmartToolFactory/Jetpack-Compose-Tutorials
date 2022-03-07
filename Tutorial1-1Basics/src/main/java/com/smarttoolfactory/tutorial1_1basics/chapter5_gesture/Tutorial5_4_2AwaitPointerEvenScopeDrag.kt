package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Composable
fun Tutorial5_4Screen2() {
    TutorialContent()
}

/*
    Source code of drag

    suspend fun AwaitPointerEventScope.drag(
    pointerId: PointerId,
    onDrag: (PointerInputChange) -> Unit
): Boolean {
    var pointer = pointerId
    while (true) {
        val change = awaitDragOrCancellation(pointer) ?: return false

        if (change.changedToUpIgnoreConsumed()) {
            return true
        }

        onDrag(change)
        pointer = change.id
    }
}
 */
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        StyleableTutorialText(
            text = "Instead of using a do while loop like in previous drag example we use " +
                    "**drag(pointerId)**, horizontalDrag(pointerId), and **verticalDrag(pointerId)**",
            bullets = false
        )
        TutorialText2(text = "drag(pointerId)")
        DragExample()
        TutorialText2(text = "horizontalDrag(pointerId)")
        HorizontalDragExample()
        TutorialText2(text = "verticalDrag(pointerId)")
        VerticalDragExample()
    }
}

private val WIDTH = 80.dp

@Composable
private fun DragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "Drag."
        )
    }

    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down = awaitFirstDown()
                    gestureColor = Orange400
                    text = "awaitFirstDown() id: ${down.id}"

                    // 🔥 Function to detect if our down pointer passed
                    // viewConfiguration.pointerSlop(pointerType)
                    val change = awaitTouchSlopOrCancellation(down.id) { change, over ->

                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + over
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - WIDTH.toPx()),
                            y = summed.y.coerceIn(0f, size.height - WIDTH.toPx())
                        )
                        change.consumePositionChange()
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

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) {
                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + it.positionChange()
                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - WIDTH.toPx()),
                                y = summed.y.coerceIn(0f, size.height - WIDTH.toPx())
                            )

                            it.consumePositionChange()
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y

                            gestureColor = Blue400
                            text = "drag()  down.id: ${down.id} change.id: ${change.id}" +
                                    "\nnewValue: $newValue"
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
            .fillMaxWidth()
            .background(gestureColor)
            .height(200.dp)
            .onSizeChanged { size = it.toSize() }
    ) {

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(dragModifier)
    }
}

@Composable
private fun HorizontalDragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }

    var text by remember {
        mutableStateOf(
            "Without awaitTouchSlopOrCancellation drag starts when awaitFirstDown is invoked."
        )
    }

    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down = awaitFirstDown()
                    gestureColor = Orange400
                    text = "awaitFirstDown() id: ${down.id}"

                    // 🔥 Function to detect if our down pointer passed
                    // viewConfiguration.pointerSlop(pointerType)
                    val change =
                        awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                            val originalX = offsetX.value
                            val newValue =
                                (originalX + over).coerceIn(0f, width - WIDTH.toPx())
                            change.consumePositionChange()
                            offsetX.value = newValue

                            gestureColor = Brown400

                            text = "awaitHorizontalTouchSlopOrCancellation()" +
                                    "\nnewValue: $newValue"
                        }

                    if (change == null) {
                        gestureColor = Red400
                        text = "awaitHorizontalTouchSlopOrCancellation() is NULL"

                    }

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        horizontalDrag(change.id) {
                            val originalX = offsetX.value
                            val newValue = (originalX + it.positionChange().x)
                                .coerceIn(0f, width - WIDTH.toPx())
                            it.consumePositionChange()
                            offsetX.value = newValue

                            gestureColor = Blue400
                            text = "horizontalDrag()" +
                                    "\nnewValue: $newValue"
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
            .background(gestureColor)
            .fillMaxWidth()
            .height(100.dp)
            .onSizeChanged { width = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            color = Color.White
        )

        Box(dragModifier)
    }
}

@Composable
private fun VerticalDragExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }


    val dragModifier = Modifier
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .size(WIDTH)
        .shadow(2.dp, RoundedCornerShape(8.dp))
        .background(Yellow400)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down = awaitFirstDown()
                    gestureColor = Orange400

                    // 🔥 Function to detect if our down pointer passed
                    // viewConfiguration.pointerSlop(pointerType)
                    val change =
                        awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                            val originalY = offsetY.value
                            val newValue = (originalY + over)
                                .coerceIn(0f, height - WIDTH.toPx())
                            change.consumePositionChange()
                            offsetY.value = newValue

                            gestureColor = Brown400
                        }

                    if (change == null) {
                        gestureColor = Red400

                    }

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        verticalDrag(change.id) {
                            val originalY = offsetY.value
                            val newValue = (originalY + it.positionChange().y)
                                .coerceIn(0f, height - WIDTH.toPx())
                            it.consumePositionChange()
                            offsetY.value = newValue

                            gestureColor = Blue400
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
            .background(gestureColor)
            .width(100.dp)
            .fillMaxHeight()
            .onSizeChanged { height = it.height.toFloat() },
        contentAlignment = Alignment.TopCenter

    ) {
        Box(dragModifier)
    }
}
