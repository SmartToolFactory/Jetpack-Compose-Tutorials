package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import kotlin.math.PI
import kotlin.math.abs


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
                            abs(rotation * PI.toFloat() * centroidSize / 180f)
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
