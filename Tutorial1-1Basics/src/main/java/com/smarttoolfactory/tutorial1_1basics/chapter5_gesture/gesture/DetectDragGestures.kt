package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import kotlin.math.absoluteValue
import kotlin.math.sign


internal suspend fun PointerInputScope.detectDragGesture(
    shouldAwaitTouchSlop: Boolean = true,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onDragStart: (change: PointerInputChange, initialDelta: Offset) -> Unit = { _, _ -> },
    onDragEnd: (change: PointerInputChange) -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) {
    awaitEachGesture {
        val initialDown =
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

        if (!shouldAwaitTouchSlop) {
            initialDown.consume()
        }
        val down = awaitFirstDown(requireUnconsumed = false)

        println(
            "🔥 Down: ${down.hashCode()}, id:${down.id}," +
                    " consumed: ${down.isConsumed}, " +
                    "initialDown: ${initialDown.hashCode()}\n" +
                    "currentEvent: ${currentEvent.changes.firstOrNull().hashCode()}, " +
                    "consumed: ${currentEvent.changes.firstOrNull()?.isConsumed}" +
                    "down==initialDown ${down == initialDown}"
        )

        var drag: PointerInputChange?
        var overSlop = Offset.Zero
        var initialDelta = Offset.Zero

        if (shouldAwaitTouchSlop) {
            do {
                drag = awaitTouchSlopOrCancellation(
                    pointerId = down.id,
                    requireUnconsumed = requireUnconsumed,
                ) { change, over ->
                    change.consume()
                    overSlop = over
                }

            } while (drag != null && !drag.isConsumed)

            initialDelta = overSlop
        } else {
            drag = initialDown
        }

        println(
            "😹drag: ${drag.hashCode()}, id: ${drag?.id}, " +
                    "currentEvent: ${
                        currentEvent.changes.firstOrNull().hashCode()
                    }, id: ${currentEvent.changes.firstOrNull()?.id}, " +
                    "consumed: ${drag?.isConsumed}, " +
                    "down==drag ${drag == down}"
        )

        if (drag != null) {
            onDragStart.invoke(drag, initialDelta)
            onDrag(drag, overSlop)

            val upEvent = drag(
                pointerId = drag.id,
                pass = pass,
                onDrag = {
                    val dragAmount = if (requireUnconsumed)
                        it.positionChange() else
                        it.positionChangeIgnoreConsumed()
                    onDrag(it, dragAmount)
                },
                orientation = null,
                motionConsumed = {
                    it.isConsumed && requireUnconsumed
                }
            )
            if (upEvent == null) {
                onDragCancel()
            } else {
                onDragEnd(upEvent)
            }
        }
    }
}

suspend fun AwaitPointerEventScope.awaitTouchSlopOrCancellation(
    pointerId: PointerId,
    requireUnconsumed: Boolean,
    pass: PointerEventPass = PointerEventPass.Main,
    onTouchSlopReached: (change: PointerInputChange, overSlop: Offset) -> Unit,
): PointerInputChange? {
    return awaitPointerSlopOrCancellation(
        pointerId = pointerId,
        requireUnconsumed = requireUnconsumed,
        pass = pass,
        pointerType = PointerType.Touch,
        onPointerSlopReached = onTouchSlopReached,
        orientation = null,
    )
}

private suspend inline fun AwaitPointerEventScope.awaitPointerSlopOrCancellation(
    pointerId: PointerId,
    requireUnconsumed: Boolean,
    pass: PointerEventPass,
    pointerType: PointerType,
    orientation: Orientation?,
    onPointerSlopReached: (PointerInputChange, Offset) -> Unit,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    val touchSlop = viewConfiguration.pointerSlop(pointerType)
    var pointer: PointerId = pointerId
    val touchSlopDetector = TouchSlopDetector(orientation)
    while (true) {
        val event = awaitPointerEvent(pass = pass)
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return null

        if (dragEvent.isConsumed && requireUnconsumed) {
            return null
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return null
            } else {
                pointer = otherDown.id
            }
        } else {
            val postSlopOffset = touchSlopDetector.addPointerInputChange(dragEvent, touchSlop)
            if (postSlopOffset != null) {
                onPointerSlopReached(
                    dragEvent,
                    postSlopOffset
                )
                if (dragEvent.isConsumed) {
                    return dragEvent
                } else {
                    touchSlopDetector.reset()
                }
            } else {
                // verify that nothing else consumed the drag event
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.isConsumed && requireUnconsumed) {
                    println("awaitPointerSlopOrCancellation() Consumed in FINAL pass in")
                    return null
                }
            }
        }
    }
}

/**
 * Continues to read drag events until all pointers are up or the drag event is canceled.
 * The initial pointer to use for driving the drag is [pointerId]. [onDrag] is called
 * whenever the pointer moves. The up event is returned at the end of the drag gesture.
 *
 * @param pointerId The pointer where that is driving the gesture.
 * @param onDrag Callback for every new drag event.
 * @param motionConsumed If the PointerInputChange should be considered as consumed.
 *
 * @return The last pointer input event change when gesture ended with all pointers up
 * and null when the gesture was canceled.
 */
internal suspend inline fun AwaitPointerEventScope.drag(
    pointerId: PointerId,
    pass: PointerEventPass = PointerEventPass.Main,
    onDrag: (PointerInputChange) -> Unit,
    orientation: Orientation?,
    motionConsumed: (PointerInputChange) -> Boolean,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    var pointer = pointerId
    while (true) {
        val change = awaitDragOrUp(
            pointerId = pointer,
            pass = pass
        ) {
            val positionChange = it.positionChangeIgnoreConsumed()
            val motionChange = if (orientation == null) {
                positionChange.getDistance()
            } else {
                if (orientation == Orientation.Vertical) positionChange.y else positionChange.x
            }
            motionChange != 0.0f
        } ?: return null

        if (motionConsumed(change)) {
            return null
        }

        if (change.changedToUpIgnoreConsumed()) {
            return change
        }

        onDrag(change)
        pointer = change.id
    }
}

/**
 * Waits for a single drag in one axis, final pointer up, or all pointers are up.
 * When [pointerId] has lifted, another pointer that is down is chosen to be the finger
 * governing the drag. When the final pointer is lifted, that [PointerInputChange] is
 * returned. When a drag is detected, that [PointerInputChange] is returned. A drag is
 * only detected when [hasDragged] returns `true`.
 *
 * `null` is returned if there was an error in the pointer input stream and the pointer
 * that was down was dropped before the 'up' was received.
 */
private suspend inline fun AwaitPointerEventScope.awaitDragOrUp(
    pointerId: PointerId,
    pass: PointerEventPass,
    hasDragged: (PointerInputChange) -> Boolean,
): PointerInputChange? {
    var pointer = pointerId
    while (true) {
        val event = awaitPointerEvent(pass)
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return null
        if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return dragEvent
            } else {
                pointer = otherDown.id
            }
        } else if (hasDragged(dragEvent)) {
            return dragEvent
        }
    }
}


/**
 * Detects if touch slop has been crossed after adding a series of [PointerInputChange].
 * For every new [PointerInputChange] one should add it to this detector using
 * [addPointerInputChange]. If the position change causes the touch slop to be crossed,
 * [addPointerInputChange] will return true.
 */
private class TouchSlopDetector(val orientation: Orientation? = null) {

    fun Offset.mainAxis() = if (orientation == Orientation.Horizontal) x else y
    fun Offset.crossAxis() = if (orientation == Orientation.Horizontal) y else x

    /**
     * The accumulation of drag deltas in this detector.
     */
    private var totalPositionChange: Offset = Offset.Zero

    /**
     * Adds [dragEvent] to this detector. If the accumulated position changes crosses the touch
     * slop provided by [touchSlop], this method will return the post slop offset, that is the
     * total accumulated delta change minus the touch slop value, otherwise this should return null.
     */
    fun addPointerInputChange(
        dragEvent: PointerInputChange,
        touchSlop: Float,
    ): Offset? {
        val currentPosition = dragEvent.position
        val previousPosition = dragEvent.previousPosition
        val positionChange = currentPosition - previousPosition
        totalPositionChange += positionChange

        val inDirection = if (orientation == null) {
            totalPositionChange.getDistance()
        } else {
            totalPositionChange.mainAxis().absoluteValue
        }

        val hasCrossedSlop = inDirection >= touchSlop

        return if (hasCrossedSlop) {
            calculatePostSlopOffset(touchSlop)
        } else {
            null
        }
    }

    /**
     * Resets the accumulator associated with this detector.
     */
    fun reset() {
        totalPositionChange = Offset.Zero
    }

    private fun calculatePostSlopOffset(touchSlop: Float): Offset {
        return if (orientation == null) {
            val touchSlopOffset =
                totalPositionChange / totalPositionChange.getDistance() * touchSlop
            // update postSlopOffset
            totalPositionChange - touchSlopOffset
        } else {
            val finalMainAxisChange = totalPositionChange.mainAxis() -
                    (sign(totalPositionChange.mainAxis()) * touchSlop)
            val finalCrossAxisChange = totalPositionChange.crossAxis()
            if (orientation == Orientation.Horizontal) {
                Offset(finalMainAxisChange, finalCrossAxisChange)
            } else {
                Offset(finalCrossAxisChange, finalMainAxisChange)
            }
        }
    }
}

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true

// TODO(demin): consider this as part of ViewConfiguration class after we make *PointerSlop*
//  functions public (see the comment at the top of the file).
//  After it will be a public API, we should get rid of `touchSlop / 144` and return absolute
//  value 0.125.dp.toPx(). It is not possible right now, because we can't access density.
internal fun ViewConfiguration.pointerSlop(pointerType: PointerType): Float {
    return when (pointerType) {
        PointerType.Mouse -> touchSlop * mouseToTouchSlopRatio
        else -> touchSlop
    }
}

// This value was determined using experiments and common sense.
// We can't use zero slop, because some hypothetical desktop/mobile devices can send
// pointer events with a very high precision (but I haven't encountered any that send
// events with less than 1px precision)
private val mouseSlop = 0.125.dp
private val defaultTouchSlop = 18.dp // The default touch slop on Android devices
private val mouseToTouchSlopRatio = mouseSlop / defaultTouchSlop

