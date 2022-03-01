package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_6Screen2() {
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
            text = "This example shows how gestures are propagated " +
                    "when **pointerInput.consumeX** functions are " +
                    "called after down, move or up.\n" +
                    "**NOTE:** After changing a flag in a level(outer, center, inner) " +
                    "press that level to make sure that it's recomposed and " +
                    "flags work properly in that modifier",
            bullets = false
        )
        GesturePropagationExample()
    }
}

/**
 * This example is to display how events propagate and how
 * [PointerInputChange.consumeDownChange] when a pointer is **down** or **up** or
 * [PointerInputChange.consumePositionChange] when pointer is **moving** effects
 * propagation.
 */
@Composable
private fun GesturePropagationExample() {

//    val context = LocalContext.current

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
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    println(
                        "😍outerRequireUnconsumed: $outerRequireUnconsumed, " +
                                "outerConsumeDown: $outerConsumeDown, " +
                                "outerConsumePositionChange: $outerConsumePositionChange, " +
                                "outerConsumeUp: $outerConsumeUp"
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

//                    Toast
//                        .makeText(context, downText, Toast.LENGTH_SHORT)
//                        .show()

                    gestureColorOuter = Pink400

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    // This is for not logging move events if pointer size didn't change
                    var pointerSize = 0

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any {

                            if (!it.pressed) {
                                if (outerConsumeUp) {
                                    it.consumeDownChange()
                                }

                                val upText = "🚀 OUTER UP id: ${down.id.value}\n" +
                                        "changedToDown: ${it.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${it.pressed}\n" +
                                        "changedUp: ${it.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                        "positionChanged: ${it.positionChanged()}\n" +
                                        "positionChangeConsumed: ${it.positionChangeConsumed()}\n" +
                                        "anyChangeConsumed: ${it.anyChangeConsumed()}\n"

                                gestureText += upText

//                                Toast
//                                    .makeText(context, upText, Toast.LENGTH_SHORT)
//                                    .show()
                            }
                            it.pressed
                        }

                        if (anyPressed) {

                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            if (outerConsumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }

                            if (pointerSize != event.changes.size) {
                                event.changes.forEach { pointer ->
                                    val outerText =
                                        "🍏 OUTER MOVE changes size ${event.changes.size}\n" +
                                                "id: ${pointer.id.value}, " +
                                                "changedToDown: ${pointer.changedToDown()}, " +
                                                "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                                "pressed: ${pointer.pressed}\n" +
                                                "changedUp: ${pointer.changedToUp()}\n" +
                                                "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                                "positionChanged: ${pointer.positionChanged()}\n" +
                                                "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                                "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n"
                                    gestureText += outerText
//                                    Toast
//                                        .makeText(context, outerText, Toast.LENGTH_SHORT)
//                                        .show()
                                }

                                pointerSize = event.changes.size
                            }

                            gestureColorOuter = Blue400
                        } else {
                            // All of the pointers are up
                            gestureText = "Outer Up\n"
                            gestureColorOuter = outerColor
                            break
                        }
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
                                "centerConsumePositionChange: $centerConsumePositionChange, " +
                                "centerConsumeUp: $centerConsumeUp"
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

//                    Toast
//                        .makeText(context, downText, Toast.LENGTH_SHORT)
//                        .show()

                    gestureColorCenter = Pink400

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    // This is for not logging move events if pointer size didn't change
                    var pointerSize = 0

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any {
                            if (!it.pressed) {
                                if (centerConsumeUp) {
                                    it.consumeDownChange()
                                }
                                val upText = "🚀🚀 CENTER UP id: ${down.id.value}\n" +
                                        "changedToDown: ${it.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${it.pressed}\n" +
                                        "changedUp: ${it.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                        "positionChanged: ${it.positionChanged()}\n" +
                                        "positionChangeConsumed: ${it.positionChangeConsumed()}\n" +
                                        "anyChangeConsumed: ${it.anyChangeConsumed()}\n"

                                gestureText += upText

//                                Toast
//                                    .makeText(context, upText, Toast.LENGTH_SHORT)
//                                    .show()
                            }
                            it.pressed
                        }

                        if (anyPressed) {

                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            if (centerConsumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }

                            if (pointerSize != event.changes.size) {
                                event.changes.forEach { pointer ->
                                    val outerText =
                                        "🍏🍏 CENTER MOVE changes size ${event.changes.size}\n" +
                                                "id: ${pointer.id.value}, " +
                                                "changedToDown: ${pointer.changedToDown()}, " +
                                                "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                                                "pressed: ${pointer.pressed}\n" +
                                                "changedUp: ${pointer.changedToUp()}\n" +
                                                "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                                "positionChanged: ${pointer.positionChanged()}\n" +
                                                "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                                "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n"
                                    gestureText += outerText
//                                    Toast
//                                        .makeText(context, outerText, Toast.LENGTH_SHORT)
//                                        .show()
                                }

                                pointerSize = event.changes.size
                            }

                            gestureColorCenter = Blue400
                        } else {
                            // All of the pointers are up
                            gestureText += "Center Up\n"
                            gestureColorCenter = centerColor
                            break
                        }
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
                                "innerConsumePositionChange: $innerConsumePositionChange, " +
                                "innerConsumeUp: $innerConsumeUp"
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

//                    Toast
//                        .makeText(context, downText, Toast.LENGTH_SHORT)
//                        .show()

                    gestureColorInner = Pink400

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    // This is for not logging move events if pointer size didn't change
                    var pointerSize = 0

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any {
                            if (!it.pressed) {
                                if (innerConsumeUp) {
                                    it.consumeDownChange()
                                }
                                val upText = "🚀🚀🚀 INNER UP id: ${down.id.value}\n" +
                                        "changedToDown: ${it.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${it.pressed}\n" +
                                        "changedUp: ${it.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                        "positionChanged: ${it.positionChanged()}\n" +
                                        "positionChangeConsumed: ${it.positionChangeConsumed()}\n" +
                                        "anyChangeConsumed: ${it.anyChangeConsumed()}\n"

                                gestureText += upText

//                                Toast
//                                    .makeText(context, upText, Toast.LENGTH_SHORT)
//                                    .show()
                            }
                            it.pressed
                        }

                        if (anyPressed) {

                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            if (innerConsumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }

                            if (pointerSize != event.changes.size) {
                                event.changes.forEach { pointer ->
                                    val outerText =
                                        "🍏🍏🍏 INNER MOVE changes size ${event.changes.size}\n" +
                                                "id: ${pointer.id.value}, " +
                                                "changedToDown: ${pointer.changedToDown()}, " +
                                                "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                                "pressed: ${pointer.pressed}\n" +
                                                "changedUp: ${pointer.changedToUp()}\n" +
                                                "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                                "positionChanged: ${pointer.positionChanged()}\n" +
                                                "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                                "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n"
                                    gestureText += outerText
//                                    Toast
//                                        .makeText(context, outerText, Toast.LENGTH_SHORT)
//                                        .show()
                                }

                                pointerSize = event.changes.size
                            }

                            gestureColorInner = Blue400
                        } else {
                            // All of the pointers are up
                            gestureText += "Inner Up\n"
                            gestureColorInner = innerColor
                            break
                        }
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
    CheckBoxWithTextRippleFullRow(label = "innerConsumeUp", innerConsumeUp) {
        innerConsumeUp = it
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
    CheckBoxWithTextRippleFullRow(label = "centerConsumeUp", centerConsumeUp) {
        centerConsumeUp = it
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
    CheckBoxWithTextRippleFullRow(label = "outerConsumeUp", outerConsumeUp) {
        outerConsumeUp = it
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
    .height(400.dp)
    .padding(2.dp)