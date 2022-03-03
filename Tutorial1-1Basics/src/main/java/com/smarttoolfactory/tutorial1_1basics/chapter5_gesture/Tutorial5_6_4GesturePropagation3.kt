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
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
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
        PropagationExample1()
//        PropagationExample2()
        PropagationExample3()
    }
}

@Composable
private fun PropagationExample1() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
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
        .border(4.dp, color = gestureColor, shape = RoundedCornerShape(8.dp))
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

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)

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
                    gestureColor = Pink400

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    // This is for not logging move events if pointer size didn't change
                    var pointerSize = 0

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

                            if (pointerSize != event.changes.size) {
                                event.changes.forEach { pointer ->
                                    val moveText =
                                        "🍏 MOVE changes size ${event.changes.size}\n" +
                                                "id: ${pointer.id.value}, " +
                                                "changedToDown: ${pointer.changedToDown()}, " +
                                                "changedToDownIgnoreConsumed: ${pointer.changedToDownIgnoreConsumed()}\n" +
                                                "pressed: ${pointer.pressed}\n" +
                                                "changedUp: ${pointer.changedToUp()}\n" +
                                                "changedToUpIgnoreConsumed: ${pointer.changedToUpIgnoreConsumed()}\n" +
                                                "position: ${pointer.position}\n" +
                                                "positionChange: ${pointer.positionChange()}\n" +
                                                "positionChanged: ${pointer.positionChanged()}\n" +
                                                "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                                "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n\n"
                                    gestureText += moveText
                                }

                                pointerSize = event.changes.size
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
    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}

@Composable
private fun PropagationExample2() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
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


    val containerModifier = containerModifier
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    gestureText += "Container POINTER"

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
                    transformDetailText +=
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }

    val imageModifier = imageModifier
        .border(4.dp, color = gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)

                    if (consumeDown) {
                        down.consumeDownChange()
                    }

                    gestureColor = Pink400

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
    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}


@Composable
private fun PropagationExample3() {

    // TRANSFORMATION Properties
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
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


    val containerModifier = containerModifier
        .height(200.dp)
        .fillMaxWidth()
        .border(4.dp, color = gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {
            detectTwoPointerTransformGestures(
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
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange =
                    // 🔥🔥 When requireUnconsumed false if a parent consumes this pointer
                        // this down never occurs
                        awaitFirstDown(requireUnconsumed = requireUnconsumed)

                    if (consumeDown) {
                        down.consumeDownChange()
                    }

                    val downText = "🎃 OUTER DOWN id: ${down.id.value}\n" +
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

                    // This is for not logging move events if pointer size didn't change
                    var pointerSize = 0

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val pointerCount = event.changes.size

                        val anyPressed = event.changes.any {

                            if (!it.pressed) {

                                if (consumeUp) {
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
                                        "anyChangeConsumed: ${it.anyChangeConsumed()}\n\n"

                                gestureText += upText
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

                            if (pointerSize != event.changes.size) {
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
                                                "positionChangeConsumed: ${pointer.positionChangeConsumed()}\n" +
                                                "anyChangeConsumed: ${pointer.anyChangeConsumed()}\n\n"
                                    gestureText += moveText
                                }

                                pointerSize = event.changes.size
                            }

                        } else {
                            // All of the pointers are up
                            gestureText += "OUTER Up\n\n"
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
    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}

/**
 * Returns the rotation, in degrees, of the pointers between the
 * [PointerInputChange.previousPosition] and [PointerInputChange.position] states.
 *
 * Only two pointers that are down in both previous and current states are considered.
 *
 */
suspend fun PointerInputScope.detectTwoPointerTransformGestures(
    panZoomLock: Boolean = false,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            var rotation = 0f
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var lockedToPanZoom = false

            awaitFirstDown(requireUnconsumed = false)

            do {
                val event = awaitPointerEvent()

                val pointerCount = event.changes.size

                // If any position change is consumed from another pointer or pointer
                // count that is pressed is not equal to 2 cancel this gesture
                val canceled = event.changes.any { it.positionChangeConsumed() } || (pointerCount != 2)

                if (!canceled) {
                    val zoomChange = event.calculateZoom()
                    val rotationChange = event.calculateRotation()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        rotation += rotationChange
                        pan += panChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        val rotationMotion =
                            abs(rotation * kotlin.math.PI.toFloat() * centroidSize / 180f)
                        val panMotion = pan.getDistance()

                        if (zoomMotion > touchSlop ||
                            rotationMotion > touchSlop ||
                            panMotion > touchSlop
                        ) {
                            pastTouchSlop = true
                            lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                        }
                    }

                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                        if (effectiveRotation != 0f ||
                            zoomChange != 1f ||
                            panChange != Offset.Zero
                        ) {
                            onGesture(centroid, panChange, zoomChange, effectiveRotation)
                        }
                        event.changes.forEach {
                            if (it.positionChanged()) {
                                it.consumeAllChanges()
                            }
                        }
                    }
                }
            } while (!canceled && event.changes.any { it.pressed })
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