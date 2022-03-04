package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

enum class MotionEvent {
    Idle, Down, Move, Up
}


/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1].
 */
fun Modifier.detectMotionEvents(
    key1: Any? = Unit,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
        properties["key1"] = key1
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
        properties["delayAfterDownInMillis"] = delayAfterDownInMillis
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(key1) {
            detectMotionEvents(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1] or [key2].
 */
fun Modifier.detectMotionEvents(
    key1: Any?,
    key2: Any?,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
        properties["delayAfterDownInMillis"] = delayAfterDownInMillis
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(key1, key2) {
            detectMotionEvents(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with any different [keys].
 */
fun Modifier.detectMotionEvents(
    vararg keys: Any?,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
        properties["keys"] = keys
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
        properties["delayAfterDownInMillis"] = delayAfterDownInMillis
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(keys) {
            detectMotionEvents(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 *  The pointer input handling block will be cancelled and re-started when pointerInput
 *  is recomposed with a different [key1].
 */
fun Modifier.pointerMotionEvents(
    key1: Any? = Unit,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(key1) {
            detectMotionEventPointers(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)


/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1] or [key2].
 */
fun Modifier.pointerMotionEvents(
    key1: Any? = Unit,
    key2: Any? = Unit,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
    },
    factory = {
        val scope = rememberCoroutineScope()
        Modifier.pointerInput(key1, key2) {
            detectMotionEventPointers(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)


/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then  [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 *
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with any different [keys].
 */
fun Modifier.pointerMotionEvents(
    vararg keys: Any?,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = composed(
    inspectorInfo = {
        name = "awaitPointerMotionEvent"
    },
    factory = {
        val scope = rememberCoroutineScope()

        Modifier.pointerInput(keys) {
            detectMotionEventPointers(onDown, onMove, onUp, scope, delayAfterDownInMillis)
        }
    }
)


