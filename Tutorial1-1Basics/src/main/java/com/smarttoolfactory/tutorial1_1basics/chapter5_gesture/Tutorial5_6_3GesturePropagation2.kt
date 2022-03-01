package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

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
            text = "1-) This example uses **awaitFirstDown** and **awaitPointerEvent**, and " +
                    "**drag(id)** drag events to propagate gestures from parent to child composable.\n" +
                    "**NOTE:** After changing a flag in a level(outer, center, inner) " +
                    "press that level to make sure that it's recomposed and " +
                    "flags work properly in that modifier"
        )
        DragPropagationExample()
    }
}

@Composable
private fun DetectDragGesturesPropagationExample() {
    var gestureText by remember { mutableStateOf("") }

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
                    gestureText += "Outer onDragStart offset: $offset\n"
                    gestureColorOuter = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Outer onDrag dragAmount: $dragAmount\n"
                    gestureColorOuter = Blue400

                },
                onDragEnd = {
                    gestureText = "Outer onDragEnd\n"
                    gestureColorOuter = outerColor
                },
                onDragCancel = {
                    gestureText = "Outer onDragCancel\n"
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
                    gestureText += "Center onDragStart offset: $offset\n"
                    gestureColorCenter = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Center onDrag dragAmount: $dragAmount\n"
                    gestureColorCenter = Blue400

                },
                onDragEnd = {
                    gestureText += "Center onDragEnd\n"
                    gestureColorCenter = innerColor
                },
                onDragCancel = {
                    gestureText += "Center onDragCancel\n"
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
                    gestureText += "Inner onDragStart offset: $offset\n"
                    gestureColorInner = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Inner onDrag dragAmount: $dragAmount\n"
                    gestureColorInner = Blue400

                },
                onDragEnd = {
                    gestureText += "Inner onDragEnd\n"
                    gestureColorInner = innerColor
                },
                onDragCancel = {
                    gestureText += "Inner onDragCancel\n"
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

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
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
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    println(
                        "😍outerRequireUnconsumed: $outerRequireUnconsumed, " +
                                "outerConsumeDown: $outerConsumeDown, " +
                                "outerConsumePositionChange: $outerConsumePositionChange"
                    )

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = outerRequireUnconsumed)

                    if (outerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃OUTER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}, " +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n"

                    gestureText += downText
                    gestureColorOuter = Purple400

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
                            gestureText += "Outer onDrag dragAmount: ${pointerInputChange.positionChange()}\n"
                            gestureColorOuter = Blue400

                            if (outerConsumePositionChange) {
                                change.consumePositionChange()
                            }
                        }

                        gestureText = "Outer onDragEnd\n"
                        gestureColorOuter = outerColor
                    } else {
                        gestureText = "Outer onDragCancel\n"
                        gestureColorOuter = Red400
                    }

                }
            }
        }

    val centerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorCenter)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    println(
                        "🚌centerRequireUnconsumed: $centerRequireUnconsumed, " +
                                "centerConsumeDown: $centerConsumeDown, " +
                                "centerConsumePositionChange: $centerConsumePositionChange"
                    )

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = centerRequireUnconsumed)

                    if (centerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃🎃 CENTER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}, " +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n"
                    gestureText += downText
                    gestureColorCenter = Purple400

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
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    println(
                        "🤡innerRequireUnconsumed: $innerRequireUnconsumed, " +
                                "innerConsumeDown: $innerConsumeDown, " +
                                "innerConsumePositionChange: $innerConsumePositionChange"
                    )

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = innerRequireUnconsumed)

                    if (innerConsumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃🎃🎃 INNER DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}, " +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n"
                    gestureText += downText

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
        innerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "innerConsumeDown", innerConsumeDown) {
        innerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "innerConsumePositionChange",
        innerConsumePositionChange
    ) {
        innerConsumePositionChange = it
    }


    Text("CENTER Composable")
    CheckBoxWithTextRippleFullRow(label = "centerRequireUnconsumed", centerRequireUnconsumed) {
        centerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "centerConsumeDown", centerConsumeDown) {
        centerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "centerConsumePositionChange", centerConsumePositionChange
    ) {
        centerConsumePositionChange = it
    }

    Text("OUTER Composable")
    CheckBoxWithTextRippleFullRow(label = "outerRequireUnconsumed", outerRequireUnconsumed) {
        outerRequireUnconsumed = it
    }
    CheckBoxWithTextRippleFullRow(label = "outerConsumeDown", outerConsumeDown) {
        outerConsumeDown = it
    }
    CheckBoxWithTextRippleFullRow(
        label = "outerConsumePositionChange",
        outerConsumePositionChange
    ) {
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
    .height(300.dp)
    .padding(2.dp)