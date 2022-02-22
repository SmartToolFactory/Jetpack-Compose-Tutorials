package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

enum class MotionEvent {
    Idle, Down, Move, Up
}

suspend fun AwaitPointerEventScope.awaitPointerMotionEvent(
    onTouchEvent: (MotionEvent, PointerInputChange) -> Unit
) {

    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onTouchEvent(MotionEvent.Down, down)

    var pointer = down
    // Main pointer is the one that is down initially
    var pointerId = down.id

    // This is preferred way in default Compose gesture codes
    // to loop gesture events and use consume or position changes to
    // break while loop
    while (true) {

        val event: PointerEvent = awaitPointerEvent()

        val anyPressed = event.changes.any { it.pressed }

        // There are at least one pointer pressed
        if (anyPressed) {
            // Get pointer that is down, if first pointer is up
            // get another and use it if other pointers are also down
            // event.changes.first() doesn't return same order
            val pointerInputChange =
                event.changes.firstOrNull { it.id == pointerId }
                    ?: event.changes.first()

            // Next time will check same pointer with this id
            pointerId = pointerInputChange.id
            pointer = pointerInputChange

            onTouchEvent(MotionEvent.Move, pointer)
        } else {
            // All of the pointers are up
            onTouchEvent(MotionEvent.Up, pointer)
            break
        }
    }
}


suspend fun AwaitPointerEventScope.awaitPointerMotionEvent(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit= {},
    onUp: (PointerInputChange) -> Unit= {},
) {

    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onDown(down)

    var pointer = down
    // Main pointer is the one that is down initially
    var pointerId = down.id

    // This is preferred way in default Compose gesture codes
    // to loop gesture events and use consume or position changes to
    // break while loop
    while (true) {

        val event: PointerEvent = awaitPointerEvent()

        val anyPressed = event.changes.any { it.pressed }

        // There are at least one pointer pressed
        if (anyPressed) {
            // Get pointer that is down, if first pointer is up
            // get another and use it if other pointers are also down
            // event.changes.first() doesn't return same order
            val pointerInputChange =
                event.changes.firstOrNull { it.id == pointerId }
                    ?: event.changes.first()

            // Next time will check same pointer with this id
            pointerId = pointerInputChange.id
            pointer = pointerInputChange

            onMove(pointer)
        } else {
            // All of the pointers are up
            onUp(pointer)
            break
        }
    }
}

fun Modifier.awaitPointerMotionEvent(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit= {},
    onUp: (PointerInputChange) -> Unit= {},
) =
    this.then(
        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitPointerMotionEvent(onDown, onMove, onUp)
                }
            }
        }
    )

fun Modifier.awaitPointerMotionEvent(onTouchEvent: (MotionEvent, PointerInputChange) -> Unit) =
    this.then(
        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitPointerMotionEvent(onTouchEvent)
                }
            }
        }
    )

suspend fun AwaitPointerEventScope.awaitMotionEvent(
    onTouchEvent: (MotionEvent, List<PointerInputChange>) -> Unit
) {

    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onTouchEvent(MotionEvent.Down, listOf(down))

    var pointer = down
    // Main pointer is the one that is down initially
    var pointerId = down.id

    // This is preferred way in default Compose gesture codes
    // to loop gesture events and use consume or position changes to
    // break while loop
    while (true) {

        val event: PointerEvent = awaitPointerEvent()

        val anyPressed = event.changes.any { it.pressed }

        // There are at least one pointer pressed
        if (anyPressed) {
            // Get pointer that is down, if first pointer is up
            // get another and use it if other pointers are also down
            // event.changes.first() doesn't return same order
            val pointerInputChange =
                event.changes.firstOrNull { it.id == pointerId }
                    ?: event.changes.first()

            // Next time will check same pointer with this id
            pointerId = pointerInputChange.id
            pointer = pointerInputChange

            onTouchEvent(MotionEvent.Move, event.changes)

        } else {
            // All of the pointers are up
            onTouchEvent(MotionEvent.Up, listOf(pointer))
            break
        }
    }
}
