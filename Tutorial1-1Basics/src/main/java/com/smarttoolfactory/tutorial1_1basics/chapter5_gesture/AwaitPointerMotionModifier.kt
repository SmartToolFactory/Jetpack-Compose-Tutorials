package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MotionEvent {
    Idle, Down, Move, Up
}


/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp].
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 * @param scope [CoroutineScope] is required to have a delay after first down
 * to give enough time for [onDown] to be processed before [onMove]
 */
suspend fun AwaitPointerEventScope.awaitPointerMotionEvent(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    scope: CoroutineScope
) {

    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onDown(down)

    var pointer = down
    // Main pointer is the one that is down initially
    var pointerId = down.id

    // If a move event is followed fast enough down is skipped, especially by Canvas
    // to prevent it we add 20ms delay after first touch
    var waitedAfterDown = false

    scope.launch {
        delay(20)
        waitedAfterDown = true
    }

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

            if (waitedAfterDown) {
                onMove(pointer)
            }
        } else {
            // All of the pointers are up
            onUp(pointer)
            break
        }
    }
}

/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp]. Unlike overload of this function [onMove] returns
 * list of [PointerInputChange] to get data about all pointers that are on the screen.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 *
 * ### Note
 * There is a 20ms delay after [onDown] to let composables process this event.
 * When pointer is moved fast [Canvas] is not able to react first down at each occurrence.
 *
 */
suspend fun AwaitPointerEventScope.awaitPointerMotionEvents(

    onDown: (PointerInputChange) -> Unit = {},
    onMove: ( List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    scope: CoroutineScope
) {

    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onDown(down)

    var pointer = down
    // Main pointer is the one that is down initially
    var pointerId = down.id

    // If a move event is followed fast enough down is skipped, especially by Canvas
    // to prevent it we add 20ms delay after first touch
    var waitedAfterDown = false

    scope.launch {
        delay(20)
        waitedAfterDown = true
    }

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

            if (waitedAfterDown) {
                onMove( event.changes)
            }

        } else {
            // All of the pointers are up
            onUp( pointer)
            break
        }
    }
}


/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp].
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 *
 * ### Note
 * There is a 20ms delay after [onDown] to let composables process this event.
 * When pointer is moved fast [Canvas] is not able to react first down at each occurrance.
 *
 */
fun Modifier.awaitPointerMotionEvent(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitPointerMotionEvent(onDown, onMove, onUp, scope)
                }
            }
        }
    }
)

/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp].
 *
 * [onMove] returns list of [PointerEvent] that are on screen
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 *
 * ### Note
 * There is a 20ms delay after [onDown] to let composables process this event.
 * When pointer is moved fast [Canvas] is not able to react first down at each occurrance.
 *
 */
fun Modifier.awaitPointerMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitPointerMotionEvents(onDown, onMove, onUp, scope)
                }
            }
        }
    }
)

