package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

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
suspend fun PointerInputScope.detectMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    scope: CoroutineScope,
    delayAfterDown:Long = 0L
) {
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

            scope.launch {
                delay(delayAfterDown)
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
suspend fun PointerInputScope.detectMotionEventPointers(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: ( List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    scope: CoroutineScope,
    delayAfterDown:Long = 0L
) {

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

            scope.launch {
                delay(delayAfterDown)
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
                        onMove( event.changes)
                    }

                } else {
                    // All of the pointers are up
                    onUp( pointer)
                    break
                }
            }
        }
    }
}


/**
 * Returns the rotation, in degrees, of the pointers between the
 * [PointerInputChange.previousPosition] and [PointerInputChange.position] states.
 *
 * Only number of pointers that equal to [numberOfPointersRequired] that are down
 * in both previous and current states are considered.
 *
 */
suspend fun PointerInputScope.detectMultiplePointerTransformGestures(
    panZoomLock: Boolean = false,
    numberOfPointersRequired: Int = 2,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,

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

                val downPointerCount = event.changes.size

                // If any position change is consumed from another pointer or pointer
                // count that is pressed is not equal to pointerCount cancel this gesture
                val canceled = event.changes.any { it.positionChangeConsumed() } || (
                        downPointerCount != numberOfPointersRequired)

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

