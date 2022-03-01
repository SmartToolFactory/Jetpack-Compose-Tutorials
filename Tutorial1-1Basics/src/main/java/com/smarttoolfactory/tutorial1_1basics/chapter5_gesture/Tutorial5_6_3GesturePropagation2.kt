package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Composable
fun Tutorial5_6Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) This example uses **detectDragGestures** drag events to propagate " +
                    "gestures from parent to child composable.\n" +
                    "**detectDragGestures** uses **awaitFirstDown** and **awaitPointerEvent** under the hood."
        )

        DetectDragGesturesPropagationExample()
        StyleableTutorialText(
            text = "2-) This example uses **awaitFirstDown** and **awaitPointerEvent**, and " +
                    "**drag(id)** drag events to propagate gestures from parent to child composable.\n" +
                    "**NOTE:** The pointer input handling block will be cancelled " +
                    "and re-started when pointerInput is recomposed with any different keys."
        )
        DragPropagationExample()
        StyleableTutorialText(
            text = "3-) This example uses **detectDragGestures** drag events to propagate " +
                    "gestures from parent to child composable by having 2 draggable squares."
        )
        DetectDragGesturesPropagationExample2()
        StyleableTutorialText(
            text = "4-) This example uses **awaitFirstDown** and **awaitPointerEvent**, and " +
                    "**drag(id)** drag events to drag outer or inner square.\n" +
                    "**NOTE:** The pointer input handling block will be cancelled " +
                    "and re-started when pointerInput is recomposed with any different keys."
        )
        DragPropagationExample2()
    }
}

@Composable
private fun DetectDragGesturesPropagationExample() {

    val outerColor = Color(0xFFFFA000)
    val centerColor = Color(0xFFFFC107)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorCenter by remember { mutableStateOf(centerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    val outerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(300.dp)
        .background(gestureColorOuter)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureColorOuter = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureColorOuter = Blue400

                },
                onDragEnd = {
                    gestureColorOuter = outerColor
                },
                onDragCancel = {
                    gestureColorOuter = Red400
                }
            )
        }

    val centerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorCenter)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureColorCenter = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureColorCenter = Blue400

                },
                onDragEnd = {
                    gestureColorCenter = innerColor
                },
                onDragCancel = {
                    gestureColorCenter = Red400
                }
            )
        }

    val innerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(100.dp)
        .background(gestureColorInner)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureColorInner = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureColorInner = Blue400

                },
                onDragEnd = {
                    gestureColorInner = innerColor
                },
                onDragCancel = {
                    gestureColorInner = Red400
                }
            )
        }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(outerModifier, contentAlignment = Alignment.Center) {
            Box(centerModifier, contentAlignment = Alignment.Center) {
                Box(modifier = innerModifier)
            }
        }
    }
}

@Composable
private fun DragPropagationExample() {
    var gestureText by remember { mutableStateOf("") }

    val outerColor = Color(0xFFFFA000)
    val centerColor = Color(0xFFFFC107)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorCenter by remember { mutableStateOf(centerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    /*
        FLAGS for consuming events which effects gesture propagation
    */
    var outerRequireUnconsumed by remember { mutableStateOf(true) }
    var outerConsumeDown by remember { mutableStateOf(false) }
    var outerConsumePositionChange by remember { mutableStateOf(false) }

    var centerRequireUnconsumed by remember { mutableStateOf(true) }
    var centerConsumeDown by remember { mutableStateOf(false) }
    var centerConsumePositionChange by remember { mutableStateOf(false) }

    var innerRequireUnconsumed by remember { mutableStateOf(true) }
    var innerConsumeDown by remember { mutableStateOf(false) }
    var innerConsumePositionChange by remember { mutableStateOf(false) }

    val outerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(300.dp)
        .background(gestureColorOuter)
        .pointerInput(
            outerRequireUnconsumed,
            outerConsumeDown,
            outerConsumePositionChange
        ) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = outerRequireUnconsumed)

                    if (outerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃 OUTER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                            "outerRequireUnconsumed: $outerRequireUnconsumed, " +
                            "outerConsumeDown: $outerConsumeDown, " +
                            "outerConsumePositionChange: $outerConsumePositionChange\n\n"

                    gestureText += downText
                    gestureColorOuter = Purple400

                    var pointerId = 0L

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            // 🔥🔥 If consumePositionChange() is not consumed drag does not
                            // function properly.
                            // Consuming position change causes change.positionChanged() to return false.
                            change.consumePositionChange()
                        }

                    if (change != null) {
                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->
                            gestureColorOuter = Blue400

                            if (outerConsumePositionChange) {
                                change.consumePositionChange()
                            }

                            if (pointerId != change.id.value) {
                                val outerText =
                                    "🍏 OUTER DRAG" +
                                            "id: ${change.id.value}, " +
                                            "changedToDown: ${change.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n\n"
                                gestureText += outerText
                                pointerId = change.id.value
                            }
                        }

                        gestureText += "OUTER onDragEnd\n"
                        gestureColorOuter = outerColor
                    } else {
                        gestureText += "OUTER onDragCancel\n"
                        gestureColorOuter = Red400
                    }

                }
            }
        }

    val centerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorCenter)
        .pointerInput(
            centerRequireUnconsumed,
            centerConsumeDown,
            centerConsumePositionChange
        ) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = centerRequireUnconsumed)

                    if (centerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃🎃 CENTER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                            "centerRequireUnconsumed: $centerRequireUnconsumed, " +
                            "centerConsumeDown: $centerConsumeDown, " +
                            "centerConsumePositionChange: $centerConsumePositionChange\n\n"
                    gestureText += downText
                    gestureColorCenter = Purple400

                    var pointerId = 0L

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            // 🔥🔥 If consumePositionChange() is not consumed drag does not
                            // function properly.
                            // Consuming position change causes change.positionChanged() to return false.
                            change.consumePositionChange()
                        }

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->
                            gestureText += "CENTER onDrag dragAmount: ${pointerInputChange.positionChange()}\n"
                            gestureColorCenter = Blue400

                            if (centerConsumePositionChange) {
                                change.consumePositionChange()
                            }

                            if (pointerId != change.id.value) {
                                val centerText =
                                    "🍏🍏 CENTER DRAG" +
                                            "id: ${change.id.value}, " +
                                            "changedToDown: ${change.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n\n"
                                gestureText += centerText
                                pointerId = change.id.value
                            }
                        }

                        gestureText += "CENTER onDragEnd\n"
                        gestureColorCenter = centerColor
                    } else {
                        gestureText += "CENTER onDragCancel\n"
                        gestureColorCenter = Red400
                    }

                }
            }
        }

    val innerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(100.dp)
        .background(gestureColorInner)
        .pointerInput(
            innerRequireUnconsumed,
            innerConsumeDown,
            innerConsumePositionChange
        ) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = innerRequireUnconsumed)

                    if (innerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃🎃🎃 INNER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                            "innerRequireUnconsumed: $innerRequireUnconsumed, " +
                            "innerConsumeDown: $innerConsumeDown, " +
                            "innerConsumePositionChange: $innerConsumePositionChange\n\n"
                    gestureText += downText

                    var pointerId = 0L

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            change.consumePositionChange()
                        }

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->
                            gestureText += "INNER onDrag ${pointerInputChange.positionChanged()}," +
                                    " dragAmount: ${pointerInputChange.positionChange()}\n"
                            gestureColorInner = Blue400

                            if (innerConsumePositionChange) {
                                change.consumePositionChange()
                            }

                            if (pointerId != change.id.value) {
                                val innerText =
                                    "🍏🍏🍏 INNER DRAG" +
                                            "id: ${change.id.value}, " +
                                            "changedToDown: ${change.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n\n"
                                gestureText += innerText
                                pointerId = change.id.value
                            }
                        }

                        gestureText += "INNER onDragEnd\n"
                        gestureColorInner = innerColor
                    } else {
                        gestureText += "INNER onDragCancel\n"
                        gestureColorInner = Red400
                    }
                }
            }
        }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(outerModifier, contentAlignment = Alignment.Center) {
            Box(centerModifier, contentAlignment = Alignment.Center) {
                Box(modifier = innerModifier)
            }
        }
    }

    Text("INNER Composable")
    CheckBoxWithTextRippleFullRow(label = "innerRequireUnconsumed", innerRequireUnconsumed) {
        gestureText = ""
        innerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "innerConsumeDown", innerConsumeDown) {
        gestureText = ""
        innerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "innerConsumePositionChange",
        innerConsumePositionChange
    ) {
        gestureText = ""
        innerConsumePositionChange = it
    }


    Text("CENTER Composable")
    CheckBoxWithTextRippleFullRow(label = "centerRequireUnconsumed", centerRequireUnconsumed) {
        gestureText = ""
        centerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "centerConsumeDown", centerConsumeDown) {
        gestureText = ""
        centerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "centerConsumePositionChange", centerConsumePositionChange
    ) {
        gestureText = ""
        centerConsumePositionChange = it
    }

    Text("OUTER Composable")
    CheckBoxWithTextRippleFullRow(label = "outerRequireUnconsumed", outerRequireUnconsumed) {
        gestureText = ""
        outerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "outerConsumeDown", outerConsumeDown) {
        gestureText = ""
        outerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "outerConsumePositionChange",
        outerConsumePositionChange
    ) {
        gestureText = ""
        outerConsumePositionChange = it
    }

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}

@Composable
private fun DetectDragGesturesPropagationExample2() {


    val sizeOuter = 200.dp
    val sizeInner = 100.dp

    var offsetOuter by remember { mutableStateOf(Offset.Zero) }
    var offsetInner by remember { mutableStateOf(Offset.Zero) }

    var size by remember { mutableStateOf(Size.Zero) }

    val outerColor = Color(0xFFFFA000)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    var dragDetailText by remember {
        mutableStateOf(
            "Drag outer or/and inner square to display drag motion propagation"
        )
    }

    val outerModifier = Modifier
        // 🔥 Change offset(position) of outer box by dragging it
        .offset {
            IntOffset(offsetOuter.x.roundToInt(), offsetOuter.y.roundToInt())
        }
        .border(4.dp, color = gestureColorOuter, shape = RoundedCornerShape(8.dp))
        .size(sizeOuter)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    gestureColorOuter = Purple400
                },

                onDragEnd = {
                    gestureColorOuter = outerColor
                },
                onDragCancel = {
                    gestureColorOuter = Red400
                },
                onDrag = { _, dragAmount: Offset ->
                    gestureColorOuter = Blue400

                    val summed = offsetOuter + dragAmount

                    val newValue = Offset(
                        x = summed.x.coerceIn(0f, size.width - sizeOuter.toPx()),
                        y = summed.y.coerceIn(0f, size.height - sizeOuter.toPx())
                    )

                    offsetOuter = newValue

                    dragDetailText =
                        "🔥DRAG OUTER\n" +
                                "OUTER OFFSET: $offsetOuter\n" +
                                "INNER OFFSET: $offsetInner"
                }
            )
        }


    val innerModifier = Modifier
        // 🔥 Change offset(position) of inner box by dragging it
        .offset {
            IntOffset(offsetInner.x.roundToInt(), offsetInner.y.roundToInt())
        }
        .border(4.dp, color = gestureColorInner, shape = RoundedCornerShape(8.dp))
        .size(sizeInner)
        .pointerInput(Unit) {
            detectDragGestures(

                onDragStart = {
                    gestureColorInner = Purple400
                },
                onDragEnd = {
                    gestureColorInner = innerColor
                },
                onDragCancel = {
                    gestureColorInner = Red400
                },
                onDrag = { _, dragAmount ->

                    gestureColorInner = Blue400
                    val summed = offsetInner + dragAmount

                    val newValue = Offset(
                        x = summed.x.coerceIn(0f, sizeOuter.toPx() - sizeInner.toPx()),
                        y = summed.y.coerceIn(0f, sizeOuter.toPx() - sizeInner.toPx())
                    )

                    offsetInner = newValue

                    dragDetailText =
                        "🚀DRAG INNER\n" +
                                "OUTER OFFSET: $offsetOuter\n" +
                                "INNER OFFSET: $offsetInner"
                }
            )
        }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(300.dp)
            .onSizeChanged {
                size = it.toSize()
            }
    ) {

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )


        Box(
            modifier = outerModifier,
        ) {
            Box(modifier = innerModifier)
        }
    }
}


@Composable
private fun DragPropagationExample2() {

    var gestureText by remember { mutableStateOf("") }


    val sizeOuter = 200.dp
    val sizeInner = 100.dp

    var offsetOuter by remember { mutableStateOf(Offset.Zero) }
    var offsetInner by remember { mutableStateOf(Offset.Zero) }

    var size by remember { mutableStateOf(Size.Zero) }

    val outerColor = Color(0xFFFFA000)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    var dragDetailText by remember {
        mutableStateOf(
            "Drag outer or/and inner square to display drag motion propagation"
        )
    }

    /*
        FLAGS for consuming events which effects gesture propagation
    */
    var outerRequireUnconsumed by remember { mutableStateOf(true) }
    var outerConsumeDown by remember { mutableStateOf(false) }
    var outerConsumePositionChange by remember { mutableStateOf(false) }

    var innerRequireUnconsumed by remember { mutableStateOf(true) }
    var innerConsumeDown by remember { mutableStateOf(false) }
    var innerConsumePositionChange by remember { mutableStateOf(false) }

    val outerModifier = Modifier
        // 🔥 Change offset(position) of outer box by dragging it
        .offset {
            IntOffset(offsetOuter.x.roundToInt(), offsetOuter.y.roundToInt())
        }
        .border(4.dp, color = gestureColorOuter, shape = RoundedCornerShape(8.dp))

        .size(200.dp)
        .pointerInput(
            outerRequireUnconsumed,
            outerConsumeDown,
            outerConsumePositionChange
        ) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = outerRequireUnconsumed)

                    if (outerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃 OUTER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                            "outerRequireUnconsumed: $outerRequireUnconsumed, " +
                            "outerConsumeDown: $outerConsumeDown, " +
                            "outerConsumePositionChange: $outerConsumePositionChange\n\n"

                    gestureText += downText
                    gestureColorOuter = Purple400

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            // 🔥🔥 If consumePositionChange() is not consumed drag does not
                            // function properly.
                            // Consuming position change causes change.positionChanged() to return false.
                            change.consumePositionChange()
                        }

                    var pointerId = 0L

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->
                            gestureText += "Outer onDrag dragAmount: ${pointerInputChange.positionChange()}\n"
                            gestureColorOuter = Blue400

                            gestureColorOuter = Blue400

                            val summed = offsetOuter + pointerInputChange.positionChange()

                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - sizeOuter.toPx()),
                                y = summed.y.coerceIn(0f, size.height - sizeOuter.toPx())
                            )

                            offsetOuter = newValue

                            dragDetailText =
                                "🔥DRAG OUTER\n" +
                                        "OUTER OFFSET: $offsetOuter\n" +
                                        "INNER OFFSET: $offsetInner"

                            if (outerConsumePositionChange) {
                                change.consumePositionChange()
                            }

                            if (pointerId != change.id.value) {
                                val outerText =
                                    "🍏 OUTER DRAG" +
                                            "id: ${change.id.value}, " +
                                            "changedToDown: ${change.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n\n"
                                gestureText += outerText
                                pointerId = change.id.value
                            }
                        }

                        gestureText += "OUTER onDragEnd\n"
                        gestureColorOuter = outerColor
                    } else {
                        gestureText += "OUTER onDragCancel\n"
                        gestureColorOuter = Red400
                    }

                }
            }
        }

    val innerModifier = Modifier
        // 🔥 Change offset(position) of outer box by dragging it
        .offset {
            IntOffset(offsetInner.x.roundToInt(), offsetInner.y.roundToInt())
        }
        .border(4.dp, color = gestureColorInner, shape = RoundedCornerShape(8.dp))
        .size(100.dp)
        .pointerInput(
            innerRequireUnconsumed,
            innerConsumeDown,
            innerConsumePositionChange
        ) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = innerRequireUnconsumed)

                    if (innerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃🎃🎃 INNER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                            "innerRequireUnconsumed: $innerRequireUnconsumed, " +
                            "innerConsumeDown: $innerConsumeDown, " +
                            "innerConsumePositionChange: $innerConsumePositionChange\n\n"
                    gestureText += downText

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            change.consumePositionChange()
                        }

                    var pointerId = 0L

                    if (change != null) {

                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->

                            gestureText += "INNER onDrag ${pointerInputChange.positionChanged()}," +
                                    " dragAmount: ${pointerInputChange.positionChange()}\n"

                            gestureColorInner = Blue400

                            val summed = offsetInner + pointerInputChange.positionChange()

                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, sizeOuter.toPx() - sizeInner.toPx()),
                                y = summed.y.coerceIn(0f, sizeOuter.toPx() - sizeInner.toPx())
                            )

                            offsetInner = newValue

                            dragDetailText =
                                "🚀DRAG INNER\n" +
                                        "OUTER OFFSET: $offsetOuter\n" +
                                        "INNER OFFSET: $offsetInner"

                            if (innerConsumePositionChange) {
                                change.consumePositionChange()
                            }

                            if (pointerId != change.id.value) {
                                val innerText =
                                    "🍏🍏🍏 INNER DRAG" +
                                            "id: ${change.id.value}, " +
                                            "changedToDown: ${change.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n\n"
                                gestureText += innerText
                                pointerId = change.id.value
                            }
                        }

                        gestureText += "INNER onDragEnd\n"
                        gestureColorInner = innerColor
                    } else {
                        gestureText += "INNER onDragCancel\n"
                        gestureColorInner = Red400
                    }
                }
            }
        }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(300.dp)
            .onSizeChanged {
                size = it.toSize()
            }
    ) {

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )


        Box(
            modifier = outerModifier,
        ) {
            Box(modifier = innerModifier)
        }
    }

    Text("INNER Composable")
    CheckBoxWithTextRippleFullRow(label = "innerRequireUnconsumed", innerRequireUnconsumed) {
        gestureText = ""
        innerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "innerConsumeDown", innerConsumeDown) {
        gestureText = ""
        innerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "innerConsumePositionChange",
        innerConsumePositionChange
    ) {
        gestureText = ""
        innerConsumePositionChange = it
    }

    Text("OUTER Composable")
    CheckBoxWithTextRippleFullRow(label = "outerRequireUnconsumed", outerRequireUnconsumed) {
        gestureText = ""
        outerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "outerConsumeDown", outerConsumeDown) {
        gestureText = ""
        outerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "outerConsumePositionChange",
        outerConsumePositionChange
    ) {
        gestureText = ""
        outerConsumePositionChange = it
    }

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}

private val gestureTextModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .background(BlueGrey400)
    .height(200.dp)
    .padding(2.dp)