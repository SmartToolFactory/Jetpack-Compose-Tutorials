package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp].
 *
 * To prevent other pointer functions that call [awaitFirstDown] or [awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consumeDownChange] in [onDown],
 * and call [PointerInputChange.consumePositionChange]
 * in [onMove] block.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *

 */
suspend fun PointerInputScope.detectMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) {
    coroutineScope {
        forEachGesture {
            awaitPointerEventScope {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange = awaitFirstDown()
                onDown(down)

                var pointer = down
                // Main pointer is the one that is down initially
                var pointerId = down.id

                // If a move event is followed fast enough down is skipped, especially by Canvas
                // to prevent it we add delay after first touch
                var waitedAfterDown = false

                launch {
                    delay(delayAfterDownInMillis)
                    waitedAfterDown = true
                }

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
        }
    }
}

/**
 * Reads [awaitFirstDown], and [awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp]. Unlike overload of this function [onMove] returns
 * list of [PointerInputChange] to get data about all pointers that are on the screen.
 *
 * To prevent other pointer functions that call [awaitFirstDown] or [awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consumeDownChange] in [onDown],
 * and call [PointerInputChange.consumePositionChange]
 * in [onMove] block.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 */
suspend fun PointerInputScope.detectMotionEventsAsList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) {

    coroutineScope {
        forEachGesture {
            awaitPointerEventScope {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange = awaitFirstDown()
                onDown(down)

                var pointer = down
                // Main pointer is the one that is down initially
                var pointerId = down.id

                // If a move event is followed fast enough down is skipped, especially by Canvas
                // to prevent it we add delay after first touch
                var waitedAfterDown = false

                launch {
                    delay(delayAfterDownInMillis)
                    waitedAfterDown = true
                }

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
                            onMove(event.changes)
                        }

                    } else {
                        // All of the pointers are up
                        onUp(pointer)
                        break
                    }
                }
            }
        }
    }
}
