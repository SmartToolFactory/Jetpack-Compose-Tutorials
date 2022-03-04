package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import java.text.DecimalFormat
import kotlin.math.abs

@Composable
fun Tutorial5_6Screen4() {
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
            text = "1-)In his example Image has two **pointerInput** modifiers. One on top " +
                    "is for **transformations**," +
                    "second one is for **move** gestures to mimic drawing on image.\n" +
                    "Use flags to consume down, position change or up events in " +
                    "second(move) **awaitPointerEventScope**"
        )

        MoveAndTransformationGestureOnImageExample()
        StyleableTutorialText(
            text = "2-)In his example **Image** has **move** **pointerInput**, **Container** " +
                    "has **transform** **pointerInput**. Events are first got by Image since it's" +
                    "child of container." +

                    "Use flags to consume down, position change or up events in " +
                    "move **awaitPointerEventScope** to prevent **Container** to receive events" +
                    "when events are **in bounds of Image**."
        )
        MoveAndTransformationGestureOnSeparateComposablesExample()
        StyleableTutorialText(
            text = "3-)In his example **Image** has **move** **pointerInput**, **Container** " +
                    "has **transform** **pointerInput** but in this example image processes" +
                    "move if only **1 pointer** is down.\n" +
                    "Instead of **detectTransformGestures**, a custom function" +
                    "**detectMultiplePointerInputTransformGestures** is used which only processes" +
                    "transform events if specified number of pointers are down"
        )
        PropagationWithDifferentPointerCountExample()
    }
}

/**
 * In this example [Image] has 2 [Modifier.pointerInput] modifiers. First one the one at the
 * bottom checks down, move and up events while the one at the top is for transformation events
 * such as centroid, zoom, pan, and rotation.
 *
 * * Calling [PointerInputChange.consumeDownChange] has no effect because [detectTransformGestures]
 * calls [awaitFirstDown] with false param
 *
 * ** Calling [PointerInputChange.consumePositionChange] prevents transformation events to
 * proceed because [detectTransformGestures] checks [PointerInputChange.positionChangeConsumed]
 * to cancel these events. Consuming position change also stops other functions like
 * scrolling or dragging either.
 */
@Composable
private fun MoveAndTransformationGestureOnImageExample() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either " +
                    "x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }
    // GESTURE Properties
    var gestureText by remember { mutableStateOf("") }
    var gestureColor by remember { mutableStateOf(Color.White) }
    /*
        FLAGS for consuming events which effects gesture propagation
     */
    var requireUnconsumed by remember { mutableStateOf(true) }
    var consumeDown by remember { mutableStateOf(false) }
    var consumePositionChange by remember { mutableStateOf(false) }
    var consumeUp by remember { mutableStateOf(false) }

    val imageModifier = imageModifier
        .border(4.dp, color = gestureColor)
        // This PointerInput gets events second
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)
                    angle += gestureRotate

                    centroid = gestureCentroid
                    transformDetailText =
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }
        // This PointerInput gets events first
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)
                    gestureColor = Orange400

                    if (consumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃 DOWN id: ${down.id.value}\n" +
                            "changedToDown: ${down.changedToDown()}\n" +
                            "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                            "pressed: ${down.pressed}\n" +
                            "changedUp: ${down.changedToUp()}\n" +
                            "positionChanged: ${down.positionChanged()}\n" +
                            "positionChangeConsumed: ${down.positionChangeConsumed()}\n" +
                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n\n"

                    gestureText += downText

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any {

                            if (!it.pressed) {

                                if (consumeUp) {
                                    it.consumeDownChange()
                                }

                                val upText = "🚀 POINTER UP id: ${down.id.value}\n" +
                                        "changedToDown: ${it.changedToDown()}, " +
                                        "changedToDownIgnoreConsumed: ${it.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${it.pressed}\n" +
                                        "changedUp: ${it.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${it.changedToUpIgnoreConsumed()}\n" +
                                        "positionChanged: ${it.positionChanged()}\n" +
                                        "positionChangeConsumed: ${it.positionChangeConsumed()}\n" +
                                        "anyChangeConsumed: ${it.anyChangeConsumed()}\n\n"

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

                            // 🔥 calling consumePositionChange() sets
                            // positionChange() to 0,
                            // positionChanged() to false,
                            // positionChangeConsumed() to true.
                            // And any parent or pointerInput above this gets no position change
                            // Scrolling or detectGestures check positionChangeConsumed()
                            if (consumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }
                            gestureColor = Blue400

                            event.changes.forEach { pointer ->
                                val moveText =
                                    "🍏 MOVE changes size ${event.changes.size}\n" +
                                            "id: ${pointer.id.value}, " +
                                            "changedToDown: ${pointer.changedToDown()}, " +
                                            "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${pointer.pressed}\n" +
                                            "previousPressed: ${pointer.previousPressed}\n" +
                                            "changedUp: ${pointer.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                            "position: ${pointer.position}\n" +
                                            "positionChange: ${pointer.positionChange()}\n" +
                                            "positionChanged: ${pointer.positionChanged()}\n" +
                                            "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                            "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n\n"
                                gestureText += moveText
                            }

                        } else {
                            // All of the pointers are up
                            gestureText += "ALL UP\n\n"
                            gestureColor = Color.White
                            break
                        }
                    }
                }
            }
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(containerModifier, imageModifier, R.drawable.landscape1, transformDetailText)
    Column {
        CheckBoxWithTextRippleFullRow(label = "requireUnconsumed", requireUnconsumed) {
            gestureText = ""
            requireUnconsumed = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeDown", consumeDown) {
            gestureText = ""
            consumeDown = it
        }
        CheckBoxWithTextRippleFullRow(
            label = "consumePositionChange",
            consumePositionChange
        ) {
            gestureText = ""
            consumePositionChange = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeUp", consumeUp) {
            gestureText = ""
            consumeUp = it
        }
    }
    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}

/**
 * In this example [imageModifier] gets events first and can consume events to prevent
 * [containerModifier]'s [PointerInputChange] to receive position changes when
 * pointers are in bounds of Image. If pointer starts movement out of the image bounds
 * it can receive events even if pointer moves inside Image bounds.
 */
@Composable
private fun MoveAndTransformationGestureOnSeparateComposablesExample() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either " +
                    "x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }

    // GESTURE Properties
    var gestureColor by remember { mutableStateOf(Color.White) }
    /*
        FLAGS for consuming events which effects gesture propagation
     */
    var requireUnconsumed by remember { mutableStateOf(true) }
    var consumeDown by remember { mutableStateOf(false) }
    var consumePositionChange by remember { mutableStateOf(false) }
    var consumeUp by remember { mutableStateOf(false) }


    val containerModifier = containerModifier
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)
                    angle += gestureRotate

                    centroid = gestureCentroid
                    transformDetailText =
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }

    val imageModifier = imageModifier
        .border(4.dp, color = gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)
                    gestureColor = Orange400

                    if (consumeDown) {
                        down.consumeDownChange()
                    }

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any {

                            if (!it.pressed) {
                                if (consumeUp) {
                                    it.consumeDownChange()
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

                            // 🔥 calling consumePositionChange() sets
                            // positionChange() to 0,
                            // positionChanged() to false,
                            // positionChangeConsumed() to true.
                            // And any parent or pointerInput above this gets no position change
                            // Scrolling or detectGestures check positionChangeConsumed()
                            if (consumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }
                            gestureColor = Blue400

                        } else {
                            // All of the pointers are up
                            gestureColor = Color.White
                            break
                        }
                    }
                }
            }
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(containerModifier, imageModifier, R.drawable.landscape1, transformDetailText)
    Column {
        CheckBoxWithTextRippleFullRow(label = "requireUnconsumed", requireUnconsumed) {
            requireUnconsumed = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeDown", consumeDown) {
            consumeDown = it
        }
        CheckBoxWithTextRippleFullRow(
            label = "consumePositionChange",
            consumePositionChange
        ) {
            consumePositionChange = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeUp", consumeUp) {
            consumeUp = it
        }
    }
}

/**
 * In this example [imageModifier] gets events first and can consume events to prevent
 * [containerModifier]'s [PointerInputChange] to receive position changes when
 * pointers are in bounds of Image. If pointer starts movement out of the image bounds
 * it can receive events even if pointer moves inside Image bounds.
 *
 * *`(anyPressed && pointerCount == 1)` prevents any events with more than one pointer on Image
 * while we used [detectMultiplePointerTransformGestures] with **2** param to invoke transform
 * gestures when only multiple pointers(2) are down.
 */
@Composable
private fun PropagationWithDifferentPointerCountExample() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either " +
                    "x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }

    // GESTURE Properties
    var gestureColor by remember { mutableStateOf(Color.White) }
    /*
        FLAGS for consuming events which effects gesture propagation
     */
    var requireUnconsumed by remember { mutableStateOf(true) }
    var consumeDown by remember { mutableStateOf(false) }
    var consumePositionChange by remember { mutableStateOf(false) }
    var consumeUp by remember { mutableStateOf(false) }


    val containerModifier = containerModifier
        .height(200.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectMultiplePointerTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)
                    angle += gestureRotate

                    transformDetailText =
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }

    val imageModifier = imageModifier
        .border(4.dp, color = gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // 🔥🔥 When requireUnconsumed false even if a child Composable or a pointerInput
                    // before this one consumes down, awaitFirstDown gets triggered nonetheless
                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)
                    gestureColor = Orange400

                    if (consumeDown) {
                        down.consumeDownChange()
                    }

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val pointerCount = event.changes.size

                        val anyPressed = event.changes.any {

                            if (!it.pressed) {

                                if (consumeUp) {
                                    it.consumeDownChange()
                                }
                            }
                            it.pressed
                        }

                        if (anyPressed && pointerCount == 1) {

                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            // 🔥 Consuming position change sets pointer.positionChange() to 0
                            //positionChangeConsumed() to true, prevents scrolling
                            if (consumePositionChange) {
                                pointerInputChange.consumePositionChange()
                            }
                            gestureColor = Blue400
                        } else {
                            // All of the pointers are up
                            gestureColor = Color.White
                            break
                        }
                    }
                }
            }
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(containerModifier, imageModifier, R.drawable.landscape2, transformDetailText)
    Column {
        CheckBoxWithTextRippleFullRow(label = "requireUnconsumed", requireUnconsumed) {
            requireUnconsumed = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeDown", consumeDown) {
            consumeDown = it
        }
        CheckBoxWithTextRippleFullRow(
            label = "consumePositionChange",
            consumePositionChange
        ) {
            consumePositionChange = it
        }
        CheckBoxWithTextRippleFullRow(label = "consumeUp", consumeUp) {
            consumeUp = it
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

private val containerModifier = Modifier
    .fillMaxWidth()
    .height(350.dp)
    .clipToBounds()
    .background(Color.LightGray)

private val imageModifier = Modifier
    .height(200.dp)
    .fillMaxWidth()