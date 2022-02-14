package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial6_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        TutorialText2(text = "Draw with Touch")
        TouchDrawMotionEventsAndPathExample()
        TutorialText2(
            text = "Drawing using drag gesture",
            modifier = Modifier.padding(top = 10.dp)
        )
        TouchDrawWithDragGesture()
        TutorialText2(
            text = "Drawing with properties and erase",
            modifier = Modifier.padding(top = 10.dp)
        )
        TouchDrawWithPropertiesAndEraseExample()

    }
}

@Composable
private fun TouchDrawMotionEventsAndPathExample() {

    val path = remember { Path() }
    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }
    var gestureText by remember { mutableStateOf("CANVAS STATE IDLE") }

    val drawModifier = canvasModifier
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown().also {
                        motionEvent = ACTION_DOWN
                        currentPosition = it.position
                        gestureColor = Blue400
                    }

                    do {
                        // This PointerEvent contains details details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges =
                            "DOWN changedToDown: ${down.changedToDown()} changedUp: ${down.changedToUp()}\n"
                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "changedUp: ${pointerInputChange.changedToUp()}" +
                                        "pos: ${pointerInputChange.position}\n"

                                // This necessary to prevent other gestures or scrolling
                                // when at least one pointer is down on canvas to draw
                                pointerInputChange.consumePositionChange()
                            }

                        gestureText = "EVENT changes size ${event.changes.size}\n" + eventChanges

                        gestureColor = Green400
                        motionEvent = ACTION_MOVE
                        currentPosition = event.changes.first().position
                    } while (event.changes.any { it.pressed })

                    motionEvent = ACTION_UP
                    gestureColor = Color.White

                    gestureText += "UP changedToDown: ${down.changedToDown()} " +
                            "changedUp: ${down.changedToUp()}\n"
                }
            }
        }

    Canvas(modifier = drawModifier) {

        when (motionEvent) {
            ACTION_DOWN -> {
                path.moveTo(currentPosition.x, currentPosition.y)
            }
            ACTION_MOVE -> {

                if (currentPosition != Offset.Unspecified) {
                    path.lineTo(currentPosition.x, currentPosition.y)
                }
            }

            ACTION_UP -> {
                path.lineTo(currentPosition.x, currentPosition.y)
            }

            else -> Unit
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    Text(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .background(BlueGrey400)
            .heightIn(min = 150.dp)
            .padding(2.dp),
        text = gestureText,
        color = Color.White
    )
}

@Composable
private fun TouchDrawWithDragGesture() {

    val path = remember { Path() }
    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }
    var gestureText by remember { mutableStateOf("CANVAS STATE IDLE") }

    val drawModifier = canvasModifier
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down: PointerInputChange = awaitFirstDown().also {
                        motionEvent = ACTION_DOWN
                        currentPosition = it.position
                        gestureColor = Blue400
                    }


                    gestureText = "awaitFirstDown() id: ${down.id}"
                    println("🍏 DOWN: ${down.position}")

                    var change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->

                            println(
                                "⛺️ awaitTouchSlopOrCancellation()  " +
                                        "down.id: ${down.id} change.id: ${change.id}" +
                                        "\nposition: ${change.position}, " +
                                        "positionChange: ${change.positionChange()}, over: $over"
                            )

                            change.consumePositionChange()

                            gestureColor = Brown400
                            gestureText =
                                "awaitTouchSlopOrCancellation()  " +
                                        "down.id: ${down.id} change.id: ${change.id}" +
                                        "\nposition: ${change.position}, " +
                                        "positionChange: ${change.positionChange()}, over: $over"

                        }


                    if (change == null) {
                        gestureColor = Yellow400
                        motionEvent = ACTION_UP
                        gestureText = "awaitTouchSlopOrCancellation() is NULL"
                    } else {

                        while (change != null && change.pressed) {

                            change = awaitDragOrCancellation(change.id)

                            if (change != null && change.pressed) {

                                gestureText =
                                    "awaitDragOrCancellation() down.id: ${down.id} change.id: ${change.id}" +
                                            "\nposition: ${change.position}, positionChange: ${change.positionChange()}"

                                gestureColor = Green400
                                motionEvent = ACTION_MOVE
                                currentPosition = change.position
                                change.consumePositionChange()


                            }
                        }

                        motionEvent = ACTION_UP
                        gestureColor = Color.White
                    }




                }
            }
        }

    Canvas(modifier = drawModifier) {

        println("🔥 CANVAS $motionEvent, position: $currentPosition")

        when (motionEvent) {
            ACTION_DOWN -> {
                path.moveTo(currentPosition.x, currentPosition.y)
            }
            ACTION_MOVE -> {

                if (currentPosition != Offset.Unspecified) {
                    path.lineTo(currentPosition.x, currentPosition.y)
                }
            }

            ACTION_UP -> {
                path.lineTo(currentPosition.x, currentPosition.y)
            }

            else -> Unit
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    Text(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .background(BlueGrey400)
            .heightIn(min = 150.dp)
            .padding(2.dp),
        text = gestureText,
        color = Color.White
    )
}

/**
 * Another drawing example. This example tracks positions and have a [PathOption] that
 * stores properties for current drawing.
 *
 * Eraser uses eraserPath and BlendMode.Clear to hide draw path.
 * Since only one path to draw and one path to delete is used this one updates whole
 * drawing when a property is changed
 */
@Composable
private fun TouchDrawWithPropertiesAndEraseExample() {

    // Path used for drawing
    val drawPath = remember { Path() }
    // Path used for erasing. In this example erasing is faked by drawing with canvas color
    // above draw path.
    val erasePath = remember { Path() }

    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var eraseMode by remember { mutableStateOf(false) }

    val pathOption = rememberPathOption()

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    awaitFirstDown().also {
                        motionEvent = ACTION_DOWN
                        currentPosition = it.position
                    }

                    do {
                        // This PointerEvent contains details details including events, id,
                        // position and more
                        val event: PointerEvent = awaitPointerEvent()
                        motionEvent = ACTION_MOVE
                        currentPosition = event.changes.first().position
                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                it.consumePositionChange()
                            }
                            pressed
                        }
                    )

                    motionEvent = ACTION_UP
                }
            }
        }

    Canvas(modifier = drawModifier) {

        // Draw or erase depending on erase mode is active or not
        val currentPath = if (eraseMode) erasePath else drawPath

        when (motionEvent) {

            ACTION_DOWN -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition

            }
            ACTION_MOVE -> {

                currentPath.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (previousPosition.x + currentPosition.x) / 2,
                    (previousPosition.y + currentPosition.y) / 2

                )
                previousPosition = currentPosition
            }

            ACTION_UP -> {
                currentPath.lineTo(currentPosition.x, currentPosition.y)
            }
            else -> Unit
        }

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawPath(
                color = pathOption.color,
                path = drawPath,
                style = Stroke(
                    width = pathOption.strokeWidth,
                    cap = pathOption.strokeCap,
                    join = pathOption.strokeJoin
                )
            )

            // Source
            drawPath(
                color = Color.Transparent,
                path = erasePath,
                style = Stroke(
                    width = 30f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkPoint)
        }
    }

    DrawingControl(pathOption = pathOption, eraseMode) {
        motionEvent = ACTION_IDLE
        eraseMode = it
    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .height(400.dp)
    .clipToBounds()

val ACTION_IDLE = 0
val ACTION_DOWN = 1
val ACTION_MOVE = 2
val ACTION_UP = 3

