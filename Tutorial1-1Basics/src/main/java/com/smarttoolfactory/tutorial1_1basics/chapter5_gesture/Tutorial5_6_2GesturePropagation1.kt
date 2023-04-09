package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Composable
fun Tutorial5_6Screen2() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "1-) This example shows how gestures are propagated " +
                    "when **pointerInput.consume** function is " +
                    "called after down, move or up.\n" +
                    "**NOTE:** The pointer input handling block will be cancelled " +
                    "and re-started when pointerInput is recomposed with any different keys."
        )
        GesturePropagationExample()
        StyleableTutorialText(
            text = "2-) This example uses **awaitFirstDown** and **awaitPointerEvent** to move " +
                    "outer or inner square.\n" +
                    "**NOTE:** The pointer input handling block will be cancelled " +
                    "and re-started when pointerInput is recomposed with any different keys."
        )
        GesturePropagationExample2()
    }
}

/**
 * This example displays how **MOTION** events propagate, and how
 * [PointerInputChange.consume] when a pointer is **down** or **up** or
 * [PointerInputChange.consume] when pointer is **moving** effects
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
 * * If an inner composable consume position change `positionChanged()` returns false
 * and `positionChange()` returns Offset.Zero
 *
 */
@Composable
private fun GesturePropagationExample() {

    var gestureText by remember { mutableStateOf("") }

    val outerColor = Color(0xFFFFA000)
    val centerColor = Color(0xFFFFC107)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorCenter by remember { mutableStateOf(centerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    /*
        FLAGS for consuming events which effect gesture propagation
     */
    var outerRequireUnconsumed by remember { mutableStateOf(true) }
    var outerConsumeDown by remember { mutableStateOf(false) }
    var outerConsumePositionChange by remember { mutableStateOf(false) }
    var outerConsumeUp by remember { mutableStateOf(false) }

    var centerRequireUnconsumed by remember { mutableStateOf(true) }
    var centerConsumeDown by remember { mutableStateOf(false) }
    var centerConsumePositionChange by remember { mutableStateOf(false) }
    var centerConsumeUp by remember { mutableStateOf(false) }

    var innerRequireUnconsumed by remember { mutableStateOf(true) }
    var innerConsumeDown by remember { mutableStateOf(false) }
    var innerConsumePositionChange by remember { mutableStateOf(false) }
    var innerConsumeUp by remember { mutableStateOf(false) }


    val outerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(300.dp)
        .background(gestureColorOuter)
        .pointerInput(
            outerRequireUnconsumed,
            outerConsumeDown,
            outerConsumePositionChange,
            outerConsumeUp
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
                        "consume: ${down.consume()}\n"

                gestureText += downText
                gestureColorOuter = Pink400

                // Main pointer is the one that is down initially
                var pointerId = down.id

                while (true) {

                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any {

                        if (!it.pressed) {
                            if (outerConsumeUp) {
                                it.consume()
                            }

                            val upText = "🚀 OUTER UP id: ${down.id.value}\n" +
                                    "changedToDown: ${it.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                    "changedUp: ${it.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                    "positionChanged: ${it.positionChanged()}\n" +
                                    "isConsumed: ${it.isConsumed}\n\n"

                            gestureText += upText
                        }
                        it.pressed
                    }

                    if (anyPressed) {

                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // positionChangeConsumed() to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check positionChangeConsumed()
                        if (outerConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorOuter = Blue400

                        event.changes.forEach { pointer ->
                            val moveText =
                                "🍏 OUTER MOVE changes size ${event.changes.size}\n" +
                                        "id: ${pointer.id.value}, " +
                                        "changedToDown: ${pointer.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${pointer.pressed}\n" +
                                        "changedUp: ${pointer.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                        "position: ${pointer.position}\n" +
                                        "positionChange: ${pointer.positionChange()}\n" +
                                        "positionChanged: ${pointer.positionChanged()}\n" +
                                        "isConsumed: ${pointer.isConsumed}\n\n"
                            gestureText += moveText
                        }

                    } else {
                        // All of the pointers are up
                        gestureText += "OUTER Up\n\n"
                        gestureColorOuter = outerColor
                        break
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
            centerConsumePositionChange,
            centerConsumeUp
        ) {
            awaitEachGesture {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange =
                // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    awaitFirstDown(requireUnconsumed = centerRequireUnconsumed)

                if (centerConsumeDown) {
                    down.consume()
                }

                val downText = "🎃🎃 CENTER DOWN id: ${down.id.value}\n" +
                        "changedToDown: ${down.changedToDown()}\n" +
                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                        "pressed: ${down.pressed}\n" +
                        "changedUp: ${down.changedToUp()}\n" +
                        "positionChanged: ${down.positionChanged()}\n" +
                        "isConsumed: ${down.isConsumed}\n\n"

                gestureText += downText
                gestureColorCenter = Pink400

                // Main pointer is the one that is down initially
                var pointerId = down.id

                while (true) {

                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any {
                        if (!it.pressed) {
                            if (centerConsumeUp) {
                                it.consume()
                            }
                            val upText = "🚀🚀 CENTER UP id: ${down.id.value}\n" +
                                    "changedToDown: ${it.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                    "changedUp: ${it.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                    "positionChanged: ${it.positionChanged()}\n" +
                                    "isConsumed: ${it.isConsumed}\n\n"

                            gestureText += upText
                        }
                        it.pressed
                    }

                    if (anyPressed) {

                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // positionChangeConsumed() to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check positionChangeConsumed()
                        if (centerConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorCenter = Blue400

                        event.changes.forEach { pointer ->
                            val moveText =
                                "🍏🍏 CENTER MOVE changes size ${event.changes.size}\n" +
                                        "id: ${pointer.id.value}, " +
                                        "changedToDown: ${pointer.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${pointer.pressed}\n" +
                                        "changedUp: ${pointer.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                        "position: ${pointer.position}\n" +
                                        "positionChange: ${pointer.positionChange()}\n" +
                                        "positionChanged: ${pointer.positionChanged()}\n" +
                                        "isConsumed: ${pointer.isConsumed}\n\n"
                            gestureText += moveText
                        }

                    } else {
                        // All of the pointers are up
                        gestureText += "CENTER Up\n\n"
                        gestureColorCenter = centerColor
                        break
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
            innerConsumePositionChange,
            innerConsumeUp
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
                gestureColorInner = Pink400

                // Main pointer is the one that is down initially
                var pointerId = down.id

                while (true) {

                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any {
                        if (!it.pressed) {
                            if (innerConsumeUp) {
                                it.consume()
                            }
                            val upText = "🚀🚀🚀 INNER UP id: ${down.id.value}\n" +
                                    "changedToDown: ${it.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                    "changedUp: ${it.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                    "positionChanged: ${it.positionChanged()}\n" +
                                    "positionChangeConsumed: ${it.positionChangeConsumed()}\n" +
                                    "isConsumed: ${it.isConsumed}\n\n"

                            gestureText += upText
                        }
                        it.pressed
                    }

                    if (anyPressed) {

                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id

                        // 🔥 calling consume() sets
                        // positionChange() to 0,
                        // positionChanged() to false,
                        // positionChangeConsumed() to true.
                        // And any parent or pointerInput above this gets no position change
                        // Scrolling or detectGestures check positionChangeConsumed()
                        if (innerConsumePositionChange) {
                            pointerInputChange.consume()
                        }
                        gestureColorInner = Blue400

                        event.changes.forEach { pointer ->
                            val moveText =
                                "🍏🍏🍏 INNER MOVE changes size ${event.changes.size}\n" +
                                        "id: ${pointer.id.value}, " +
                                        "changedToDown: ${pointer.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${pointer.pressed}\n" +
                                        "changedUp: ${pointer.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                        "position: ${pointer.position}\n" +
                                        "positionChange: ${pointer.positionChange()}\n" +
                                        "positionChanged: ${pointer.positionChanged()}\n" +
                                        "isConsumed: ${pointer.isConsumed}\n\n"
                            gestureText += moveText
                        }

                    } else {
                        // All of the pointers are up
                        gestureText += "INNER Up\n\n"
                        gestureColorInner = innerColor
                        break
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

    /*
            CONTROLS
     */
    var innerCheckBoxesExpanded by remember { mutableStateOf(true) }
    var centerCheckBoxesExpanded by remember { mutableStateOf(true) }
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
            CheckBoxWithTextRippleFullRow(label = "innerConsumeUp", innerConsumeUp) {
                gestureText = ""
                innerConsumeUp = it
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                centerCheckBoxesExpanded = !centerCheckBoxesExpanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "CENTER Composable", modifier = Modifier
                .weight(1f)

                .padding(horizontal = 10.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = centerColor
        )
        // Change vector drawable to expand more or less based on state of expanded
        Icon(
            imageVector = if (centerCheckBoxesExpanded) Icons.Filled.ExpandLess
            else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }
    AnimatedVisibility(visible = centerCheckBoxesExpanded) {
        Column {
            CheckBoxWithTextRippleFullRow(
                label = "centerRequireUnconsumed",
                centerRequireUnconsumed
            ) {
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
            CheckBoxWithTextRippleFullRow(label = "centerConsumeUp", centerConsumeUp) {
                centerConsumeUp = it
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
            CheckBoxWithTextRippleFullRow(label = "outerConsumeUp", outerConsumeUp) {
                gestureText = ""
                outerConsumeUp = it
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
private fun GesturePropagationExample2() {

    val sizeOuter = 200.dp
    val sizeInner = 100.dp

    var size by remember { mutableStateOf(Size.Zero) }

    var offsetOuter by remember { mutableStateOf(Offset.Zero) }
    var offsetInner by remember { mutableStateOf(Offset.Zero) }

    val outerColor = Color(0xFFFFA000)
    val innerColor = Color(0xFFFFD54F)

    var gestureColorOuter by remember { mutableStateOf(outerColor) }
    var gestureColorInner by remember { mutableStateOf(innerColor) }

    /*
        FLAGS for consuming events which effects gesture propagation
     */
    var outerRequireUnconsumed by remember { mutableStateOf(true) }
    var outerConsumeDown by remember { mutableStateOf(false) }
    var outerConsumePositionChange by remember { mutableStateOf(false) }
    var outerConsumeUp by remember { mutableStateOf(false) }

    var innerRequireUnconsumed by remember { mutableStateOf(true) }
    var innerConsumeDown by remember { mutableStateOf(false) }
    var innerConsumePositionChange by remember { mutableStateOf(false) }
    var innerConsumeUp by remember { mutableStateOf(false) }


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
            outerConsumePositionChange,
            outerConsumeUp
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

                gestureColorOuter = Pink400

                // Main pointer is the one that is down initially
                var pointerId = down.id
                while (true) {

                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any {

                        if (!it.pressed) {
                            if (outerConsumeUp) {
                                it.consume()
                            }
                        }
                        it.pressed
                    }

                    if (anyPressed) {

                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id

                        // Calculate offset for this composable after move
                        val summed = offsetOuter + pointerInputChange.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - sizeOuter.toPx()),
                            y = summed.y.coerceIn(0f, size.height - sizeOuter.toPx())
                        )
                        offsetOuter = newValue

                        if (outerConsumePositionChange) {
                            pointerInputChange.consume()
                        }

                        gestureColorOuter = Blue400
                    } else {
                        // All of the pointers are up
                        gestureColorOuter = outerColor
                        break
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
            innerConsumePositionChange,
            innerConsumeUp
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

                gestureColorInner = Pink400

                // Main pointer is the one that is down initially
                var pointerId = down.id
                while (true) {

                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any {
                        if (!it.pressed) {
                            if (innerConsumeUp) {
                                it.consume()
                            }
                        }
                        it.pressed
                    }

                    if (anyPressed) {

                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id

                        // Calculate offset for this composable after move
                        val summed = offsetInner + pointerInputChange.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(
                                -sizeInner.toPx() / 2,
                                sizeOuter.toPx() - 3 * sizeInner.toPx() / 2
                            ),
                            y = summed.y.coerceIn(
                                -sizeInner.toPx() / 2,
                                sizeOuter.toPx() - 3 * sizeInner.toPx() / 2
                            )
                        )
                        offsetInner = newValue

                        if (innerConsumePositionChange) {
                            pointerInputChange.consume()
                        }

                        gestureColorInner = Blue400
                    } else {
                        // All of the pointers are up
                        gestureColorInner = innerColor
                        break
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
        Box(outerModifier, contentAlignment = Alignment.Center) {
            Box(modifier = innerModifier)
        }
    }

    /*
            CONTROLS
     */
    var innerCheckBoxesExpanded by remember { mutableStateOf(true) }
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
            CheckBoxWithTextRippleFullRow(label = "innerConsumeUp", innerConsumeUp) {
                innerConsumeUp = it
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
            CheckBoxWithTextRippleFullRow(label = "outerConsumeUp", outerConsumeUp) {
                outerConsumeUp = it
            }
        }
    }
}

private val gestureTextModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .background(BlueGrey400)
    .height(250.dp)
    .padding(2.dp)