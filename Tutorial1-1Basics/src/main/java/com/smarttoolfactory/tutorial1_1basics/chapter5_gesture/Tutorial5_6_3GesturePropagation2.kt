package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Preview
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
            text = "4-) This example uses **awaitFirstDown** and **awaitTouchSlopOrCancellation**, " +
                    "and **drag(id)** drag events to drag outer or inner square.\n" +
                    "**NOTE:** The pointer input handling block will be cancelled " +
                    "and re-started when pointerInput is recomposed with any different keys."
        )
        DragPropagationExample2()
    }
}

@Composable
private fun DetectDragGesturesPropagationExample() {

    val outerColor = Color(0xFFFFA000)
    val middleColor = Color(0xFFFFC107)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorMiddle by remember { mutableStateOf(middleColor) }
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

    val middleModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorMiddle)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureColorMiddle = Purple400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureColorMiddle = Blue400

                },
                onDragEnd = {
                    gestureColorMiddle = innerColor
                },
                onDragCancel = {
                    gestureColorMiddle = Red400
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
            Box(middleModifier, contentAlignment = Alignment.Center) {
                Box(modifier = innerModifier)
            }
        }
    }
}

/**
 * This example displays how **DRAG** events propagate, and how
 * [PointerInputChange.consume] when a pointer is **down** or **up** or
 * [PointerInputChange.isConsumed] when pointer is **moving** effects
 * propagation.
 *
 * * Events propagate from child to parent unlike View touch events moving from parent to
 * child with **dispatchTouchEvent->onTouchEvent**
 *
 * * If `awaitFirstDown(requireUnconsumed)` has **true** param and if inner composable
 * consumes down, next Composable does not receive awaitFirstDown
 *
 * * If an inner(child) composable consumes down and current Composable calls `awaitFirstDown(true)`
 * it doesn't receive events from in flow from child to parent
 *
 * * Since `awaitTouchSlopOrCancellation` needs to consume position change
 * positionChange() returns 0 for parent Composables. Unlike touch we can't drag parent
 * with a motion starting from a child when `awaitTouchSlopOrCancellation` consume position change
 *
 */
@Composable
private fun DragPropagationExample() {
    var gestureText by remember { mutableStateOf("") }

    val outerColor = Color(0xFFFFA000)
    val middleColor = Color(0xFFFFC107)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorMiddle by remember { mutableStateOf(middleColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    /*
        FLAGS for consuming events which effects gesture propagation
    */
    var outerRequireUnconsumed by remember { mutableStateOf(true) }
    var outerConsumeDown by remember { mutableStateOf(false) }
    var outerConsumePositionChange by remember { mutableStateOf(false) }

    var middleRequireUnconsumed by remember { mutableStateOf(true) }
    var middleConsumeDown by remember { mutableStateOf(false) }
    var middleConsumePositionChange by remember { mutableStateOf(false) }

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
            awaitEachGesture {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = outerRequireUnconsumed)

                if (outerConsumeDown) {
                    down.consume()
                }

                val downText = "🎃 OUTER DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"
                gestureText += downText
                gestureColorOuter = Purple400

                val change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes change.positionChanged() to return false.
                        change.consume()

                    }

                if (change != null) {
                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) { pointerInputChange: PointerInputChange ->

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // isConsumed to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check isConsumed
                        if (outerConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorOuter = Blue400

                        val outerText =
                            "🍏 OUTER DRAG" +
                                    "id: ${change.id.value}, " +
                                    "changedToDown: ${change.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                    "pressed: ${change.pressed}\n" +
                                    "changedUp: ${change.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                    "position: ${change.position}\n" +
                                    "positionChange: ${change.positionChange()}\n" +
                                    "positionChanged: ${change.positionChanged()}\n" +
                                    "isConsumed: ${change.isConsumed}\n\n"
                        gestureText += outerText
                    }

                    gestureText += "OUTER onDragEnd\n\n"
                    gestureColorOuter = outerColor
                } else {
                    gestureText += "OUTER onDragEnd\n\n"
                    gestureColorOuter = Red400
                }
            }
        }

    val middleModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorMiddle)
        .pointerInput(
            middleRequireUnconsumed,
            middleConsumeDown,
            middleConsumePositionChange
        ) {
            awaitEachGesture {

                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = middleRequireUnconsumed)

                if (middleConsumeDown) {
                    down.consume()
                }

                val downText = "🎃🎃 MIDDLE DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"

                gestureText += downText
                gestureColorMiddle = Purple400

                val change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes change.positionChanged() to return false.
                        change.consume()

                    }

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) { pointerInputChange: PointerInputChange ->

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // isConsumed to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check isConsumed
                        if (middleConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorMiddle = Blue400

                        val middleText =
                            "🍏🍏 MIDDLE DRAG" +
                                    "id: ${change.id.value}, " +
                                    "changedToDown: ${change.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                    "pressed: ${change.pressed}\n" +
                                    "changedUp: ${change.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                    "position: ${change.position}\n" +
                                    "positionChange: ${change.positionChange()}\n" +
                                    "positionChanged: ${change.positionChanged()}\n" +
                                    "isConsumed: ${change.isConsumed}\n\n"
                        gestureText += middleText
                    }

                    gestureText += "MIDDLE onDragEnd\n\n"
                    gestureColorMiddle = middleColor
                } else {
                    gestureText += "MIDDLE onDragEnd\n\n"
                    gestureColorMiddle = Red400
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
            awaitEachGesture {

                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = innerRequireUnconsumed)

                if (innerConsumeDown) {
                    down.consume()
                }

                val downText = "🎃🎃🎃 INNER DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"
                gestureText += downText

                var pointerId = 0L

                val change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes change.positionChanged() to return false.
                        change.consume()
                    }

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) { pointerInputChange: PointerInputChange ->

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // isConsumed to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check isConsumed
                        if (innerConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorInner = Blue400

                        if (pointerId != change.id.value) {
                            val innerText =
                                "🍏🍏🍏 INNER DRAG" +
                                        "id: ${change.id.value}, " +
                                        "changedToDown: ${change.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${change.pressed}\n" +
                                        "changedUp: ${change.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                        "position: ${change.position}\n" +
                                        "positionChange: ${change.positionChange()}\n" +
                                        "positionChanged: ${change.positionChanged()}\n" +
                                        "isConsumed: ${change.isConsumed}\n\n"
                            gestureText += innerText
                            pointerId = change.id.value
                        }
                    }

                    gestureText += "INNER onDragEnd\n\n"
                    gestureColorInner = innerColor
                } else {
                    gestureText += "INNER onDragEnd\n\n"
                    gestureColorInner = Red400
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
            Box(middleModifier, contentAlignment = Alignment.Center) {
                Box(modifier = innerModifier)
            }
        }
    }

    /*
            CONTROLS
     */
    var innerCheckBoxesExpanded by remember { mutableStateOf(true) }
    var middleCheckBoxesExpanded by remember { mutableStateOf(true) }
    var outerCheckBoxesExpanded by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                innerCheckBoxesExpanded = !innerCheckBoxesExpanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "INNER Composable", modifier = Modifier
                .weight(1f)

                .padding(horizontal = 10.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = innerColor
        )
        // Change vector drawable to expand more or less based on state of expanded
        Icon(
            imageVector = if (innerCheckBoxesExpanded) Icons.Filled.ExpandLess
            else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }

    AnimatedVisibility(visible = innerCheckBoxesExpanded) {
        Column {
            CheckBoxWithTextRippleFullRow(
                label = "innerRequireUnconsumed",
                innerRequireUnconsumed
            ) {
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
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                middleCheckBoxesExpanded = !middleCheckBoxesExpanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "MIDDLE Composable", modifier = Modifier
                .weight(1f)

                .padding(horizontal = 10.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = middleColor
        )
        // Change vector drawable to expand more or less based on state of expanded
        Icon(
            imageVector = if (middleCheckBoxesExpanded) Icons.Filled.ExpandLess
            else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }
    AnimatedVisibility(visible = middleCheckBoxesExpanded) {
        Column {
            CheckBoxWithTextRippleFullRow(
                label = "middleRequireUnconsumed",
                middleRequireUnconsumed
            ) {
                gestureText = ""
                middleRequireUnconsumed = it
            }
            CheckBoxWithTextRippleFullRow(label = "middleConsumeDown", middleConsumeDown) {
                gestureText = ""
                middleConsumeDown = it
            }
            CheckBoxWithTextRippleFullRow(
                label = "middleConsumePositionChange", middleConsumePositionChange
            ) {
                gestureText = ""
                middleConsumePositionChange = it
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                outerCheckBoxesExpanded = !outerCheckBoxesExpanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "OUTER Composable", modifier = Modifier
                .weight(1f)

                .padding(horizontal = 10.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = outerColor
        )
        // Change vector drawable to expand more or less based on state of expanded
        Icon(
            imageVector = if (outerCheckBoxesExpanded) Icons.Filled.ExpandLess
            else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }
    AnimatedVisibility(visible = outerCheckBoxesExpanded) {
        Column {
            CheckBoxWithTextRippleFullRow(
                label = "outerRequireUnconsumed",
                outerRequireUnconsumed
            ) {
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
        }
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
            awaitEachGesture {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = outerRequireUnconsumed)

                if (outerConsumeDown) {
                    down.consume()
                }

                val downText = "🎃 OUTER DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"

                gestureText += downText
                gestureColorOuter = Purple400

                val change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                        // 🔥🔥 If consume() is not called drag does not
                        // function properly.
                        // Consuming position change causes change.positionChanged() to return false.
                        change.consume()
                    }

                var pointerId = 0L

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) { pointerInputChange: PointerInputChange ->
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

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // isConsumed to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check isConsumed
                        if (outerConsumePositionChange) {
                            pointerInputChange.consume()
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
                                        "position: ${change.position}\n" +
                                        "positionChange: ${change.positionChange()}\n" +
                                        "positionChanged: ${change.positionChanged()}\n" +
                                        "isConsumed: ${change.isConsumed}\n\n"
                            gestureText += outerText
                            pointerId = change.id.value
                        }
                    }

                    gestureText += "OUTER onDragEnd\n\n"
                    gestureColorOuter = outerColor
                } else {
                    gestureText += "OUTER onDragEnd\n\n"
                    gestureColorOuter = Red400
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
            awaitEachGesture {

                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = innerRequireUnconsumed)

                if (innerConsumeDown) {
                    down.consume()
                }

                val downText = "🎃🎃🎃 INNER DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"
                gestureText += downText

                val change: PointerInputChange? =
                    awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                        change.consume()
                    }

                var pointerId = 0L

                if (change != null) {

                    // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                    drag(change.id) { pointerInputChange: PointerInputChange ->
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

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // positionChangeConsumed() to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check isConsumed
                        if (innerConsumePositionChange) {
                            pointerInputChange.consume()
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
                                        "position: ${change.position}\n" +
                                        "positionChange: ${change.positionChange()}\n" +
                                        "positionChanged: ${change.positionChanged()}\n" +
                                        "isConsumed: ${change.isConsumed}\n\n"
                            gestureText += innerText
                            pointerId = change.id.value
                        }
                    }

                    gestureText += "INNER onDragEnd\n\n"
                    gestureColorInner = innerColor
                } else {
                    gestureText += "INNER onDragEnd\n\n"
                    gestureColorInner = Red400
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
    .height(250.dp)
    .padding(2.dp)