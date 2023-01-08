@file:OptIn(ExperimentalComposeUiApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.MotionEvent
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Tutorial6_4Screen0() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    /*
        This tutorial shows how Canvas can react to Down, Move and Up events with different gestures.
        Drag even after DragStart, and awaitPointerEvent after awaitDown happen so quickly and
        because of this Canvas does not draw anything in first down events unless we use
        some tweaks or workarounds.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "1-) Canvas refresh time cannot keep up with the short delay after " +
                    "**dragStart** before  **drag** events. Because of that " +
                    "**Down** events are never invoked in Canvas."
        )
        DragCanvasMotionEventsExample()
        StyleableTutorialText(
            text = "2-) PointerInterOpFilter **android.view.MotionEvent** has about 20ms delay between" +
                    "ACTION_DOWN, and ACTION_MOVE so both events are invoked in Canvas."
        )
        PointerInterOpFilterCanvasMotionEventsExample()
        StyleableTutorialText(
            text = "3-) **awaitPointerEvent** after **awaitFirstDown** has not enough delay " +
                    "so **Down** events are skipped if pointer is moved fast enough"
        )
        AwaitPointerEventCanvasStateExample()
        StyleableTutorialText(
            text = "4-) Adding delay(16-25ms) after **awaitFirstDown** makes sure that" +
                    " Canvas will always handle down event no matter how fast pointers moved down then up."
        )
        AwaitPointerEventWithDelayCanvasStateExample()
    }
}


@Composable
private fun DragCanvasMotionEventsExample() {

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    val canvasText = remember { StringBuilder() }
    val gestureText = remember {
        StringBuilder().apply {
            append("Touch Canvas above to display motion events")
        }
    }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    val sdf = remember { SimpleDateFormat("mm:ss.SSS", Locale.ROOT) }

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureText.clear()
                    motionEvent = MotionEvent.Down
                    currentPosition = offset
                    gestureText.append("🔥 MotionEvent.Down time: ${sdf.format(System.currentTimeMillis())}\n")
                },
                onDrag = { change: PointerInputChange, _: Offset ->
                    motionEvent = MotionEvent.Move
                    currentPosition = change.position
                    gestureText.append("🔥🔥 MotionEvent.Move time: ${sdf.format(System.currentTimeMillis())}\n")

                },
                onDragEnd = {
                    motionEvent = MotionEvent.Up
                    gestureText.append("🔥🔥🔥 MotionEvent.Up time: ${sdf.format(System.currentTimeMillis())}\n")
                }
            )
        }

    CanvasAndGestureText(
        modifier = drawModifier,
        motionEvent = motionEvent,
        currentPosition = currentPosition,
        dateFormat = sdf,
        canvasText = canvasText,
        gestureText = gestureText
    )
}

@Composable
private fun PointerInterOpFilterCanvasMotionEventsExample() {

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    val canvasText = remember { StringBuilder() }
    val gestureText = remember {
        StringBuilder().apply {
            append("Touch Canvas above to display motion events")
        }
    }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    val sdf = remember { SimpleDateFormat("mm:ss.SSS", Locale.ROOT) }

    val requestDisallowInterceptTouchEvent = RequestDisallowInterceptTouchEvent()

    // 🔥 Requests other touch events like scrolling to not intercept this event
    // If this is not set to true scrolling stops pointerInteropFilter getting move events
    requestDisallowInterceptTouchEvent.invoke(true)
    val drawModifier = canvasModifier
        .background(Color.White)

        .pointerInteropFilter(requestDisallowInterceptTouchEvent) { event: android.view.MotionEvent ->

            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    gestureText.clear()

                    motionEvent = MotionEvent.Down
                    currentPosition = Offset(event.x, event.y)

//                    canvasColor = Orange400
                    gestureText.append("🔥 MotionEvent.Down time: ${sdf.format(System.currentTimeMillis())}\n")
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    motionEvent = MotionEvent.Move
                    currentPosition = Offset(event.x, event.y)

//                    canvasColor = Blue400
                    gestureText.append("🔥🔥 MotionEvent.Move time: ${sdf.format(System.currentTimeMillis())}\n")

                }
                android.view.MotionEvent.ACTION_UP -> {
                    motionEvent = MotionEvent.Up
                    currentPosition = Offset(event.x, event.y)

//                    canvasColor = Green400
                    gestureText.append("🔥🔥🔥 MotionEvent.Up time: ${sdf.format(System.currentTimeMillis())}\n")
                }
                else -> false
            }
            requestDisallowInterceptTouchEvent.invoke(true)
            true
        }

    CanvasAndGestureText(
        modifier = drawModifier,
        motionEvent = motionEvent,
        currentPosition = currentPosition,
        dateFormat = sdf,
        canvasText = canvasText,
        gestureText = gestureText
    )
}

@Composable
private fun AwaitPointerEventCanvasStateExample() {

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    val canvasText = remember { StringBuilder() }
    val gestureText = remember {
        StringBuilder().apply {
            append("Touch Canvas above to display motion events")
        }
    }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    val sdf = remember { SimpleDateFormat("mm:ss.SSS", Locale.ROOT) }

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            awaitEachGesture {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown()

                    currentPosition = down.position
                    motionEvent = MotionEvent.Down
                    gestureText.clear()
                    gestureText.append("🔥 MotionEvent.Down time: ${sdf.format(System.currentTimeMillis())}\n")

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any { it.pressed }

                        if (anyPressed) {

                            // Get pointer that is down, if first pointer is up
                            // get another and use it if other pointers are also down
                            // event.changes.first() doesn't return same order
                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            currentPosition = pointerInputChange.position
                            motionEvent = MotionEvent.Move
                            gestureText.append("🔥🔥 MotionEvent.Move time: ${sdf.format(System.currentTimeMillis())}\n")

                            // This necessary to prevent other gestures or scrolling
                            // when at least one pointer is down on canvas to draw
                            pointerInputChange.consume()


                        } else {
                            // All of the pointers are up
                            motionEvent = MotionEvent.Up
                            gestureText.append("🔥🔥🔥 MotionEvent.Up time: ${sdf.format(System.currentTimeMillis())}\n")
                            break
                        }
                    }
            }
        }

    CanvasAndGestureText(
        modifier = drawModifier,
        motionEvent = motionEvent,
        currentPosition = currentPosition,
        dateFormat = sdf,
        canvasText = canvasText,
        gestureText = gestureText
    )
}


@Composable
private fun AwaitPointerEventWithDelayCanvasStateExample() {

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    val canvasText = remember { StringBuilder() }
    val gestureText = remember {
        StringBuilder().apply {
            append("Touch Canvas above to display motion events")
        }
    }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    val sdf = remember { SimpleDateFormat("mm:ss.SSS", Locale.ROOT) }

    // 🔥 This coroutineScope is used for adding delay after first down event
    val scope = rememberCoroutineScope()

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            awaitEachGesture {
                    var waitedAfterDown = false

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown()

                    currentPosition = down.position
                    motionEvent = MotionEvent.Down
                    gestureText.clear()
                    gestureText.append("🔥 MotionEvent.Down time: ${sdf.format(System.currentTimeMillis())}\n")

                    // 🔥 Without this delay Canvas misses down event
                    scope.launch {
                        delay(20)
                        waitedAfterDown = true
                    }

                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()

                        val anyPressed = event.changes.any { it.pressed }

                        if (anyPressed) {

                            // Get pointer that is down, if first pointer is up
                            // get another and use it if other pointers are also down
                            // event.changes.first() doesn't return same order
                            val pointerInputChange =
                                event.changes.firstOrNull { it.id == pointerId }
                                    ?: event.changes.first()

                            // Next time will check same pointer with this id
                            pointerId = pointerInputChange.id

                            if (waitedAfterDown) {
                                currentPosition = pointerInputChange.position
                                motionEvent = MotionEvent.Move
                            }
                            gestureText.append("🔥🔥 MotionEvent.Move time: ${sdf.format(System.currentTimeMillis())}\n")

                            // This necessary to prevent other gestures or scrolling
                            // when at least one pointer is down on canvas to draw
                            pointerInputChange.consume()


                        } else {
                            // All of the pointers are up
                            motionEvent = MotionEvent.Up
                            gestureText.append("🔥🔥🔥 MotionEvent.Up time: ${sdf.format(System.currentTimeMillis())}\n")
                            break
                        }
                    }
            }
        }

    CanvasAndGestureText(
        modifier = drawModifier,
        motionEvent = motionEvent,
        currentPosition = currentPosition,
        dateFormat = sdf,
        canvasText = canvasText,
        gestureText = gestureText
    )
}

@Composable
private fun CanvasAndGestureText(
    modifier: Modifier,
    motionEvent: MotionEvent,
    currentPosition: Offset,
    dateFormat: SimpleDateFormat,
    canvasText: StringBuilder,
    gestureText: StringBuilder
) {

    val paint = remember {
        Paint().apply {
            textSize = 36f
            color = Color.Black.toArgb()
        }
    }

    Canvas(modifier = modifier) {

        when (motionEvent) {
            MotionEvent.Down -> {
                canvasText.clear()
                canvasText.append(
                    "🍏 CANVAS DOWN, " +
                            "time: ${dateFormat.format(System.currentTimeMillis())}, " +
                            "x: ${currentPosition.x}, y: ${currentPosition.y}\n"
                )
            }
            MotionEvent.Move -> {
                canvasText.append(
                    "🍎 CANVAS MOVE " +
                            "time: ${dateFormat.format(System.currentTimeMillis())}, " +
                            "x: ${currentPosition.x}, y: ${currentPosition.y}\n"
                )
            }
            MotionEvent.Up -> {
                canvasText.append(
                    "🍌 CANVAS UP, " +
                            "time: ${dateFormat.format(System.currentTimeMillis())}, " +
                            "event: $motionEvent, " +
                            "x: ${currentPosition.x}, y: ${currentPosition.y}\n"
                )
            }
            else -> Unit
        }
        drawText(text = canvasText.toString(), x = 0f, y = 60f, paint)
    }

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText.toString(),
        color = Color.White,
    )
}

private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {


    val lines = text.split("\n")
    // 🔥🔥 There is not a built-in function as of 1.0.0
    // for drawing text so we get the native canvas to draw text and use a Paint object
    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxWidth()
    .height(220.dp)

private val gestureTextModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .background(BlueGrey400)
    .height(120.dp)
    .padding(2.dp)
