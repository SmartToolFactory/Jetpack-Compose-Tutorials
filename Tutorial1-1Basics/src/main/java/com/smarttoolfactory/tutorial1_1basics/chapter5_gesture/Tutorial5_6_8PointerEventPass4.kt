package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.abs


@Composable
fun Tutorial5_6Screen8() {
    TutorialContent()
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun TutorialContent() {
    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val outer = (1..60).toList().chunked(6)

    /**
     * The enumeration of passes where [PointerInputChange] traverses up and down the UI tree.
     *
     * PointerInputChanges traverse throw the hierarchy in the following passes:
     *
     * 1. [Initial]: Down the tree from ancestor to descendant.
     * 2. [Main]: Up the tree from descendant to ancestor.
     * 3. [Final]: Down the tree from ancestor to descendant.
     *
     * These passes serve the following purposes:
     *
     * 1. Initial: Allows ancestors to consume aspects of [PointerInputChange] before descendants.
     * This is where, for example, a scroller may block buttons from getting tapped by other fingers
     * once scrolling has started.
     * 2. Main: The primary pass where gesture filters should react to and consume aspects of
     * [PointerInputChange]s. This is the primary path where descendants will interact with
     * [PointerInputChange]s before parents. This allows for buttons to respond to a tap before a
     * container of the bottom to respond to a tap.
     * 3. Final: This pass is where children can learn what aspects of [PointerInputChange]s were
     * consumed by parents during the [Main] pass. For example, this is how a button determines that
     * it should no longer respond to fingers lifting off of it because a parent scroller has
     * consumed movement in a [PointerInputChange].
     */
    var pass by remember {
        mutableStateOf(PointerEventPass.Main)
    }

    val transformModifier = Modifier
        .clipToBounds()
        // 🔥 Important to reset this on pass change or it uses old value
        .pointerInput(pass) {
            //zoom in/out and move around
            customDetectTransformGestures(
                // Consuming this cancels clicks when position of pointer changes
                consume = false,
                pass = pass,
                onGesture = { gestureCentroid: Offset,
                              gesturePan: Offset,
                              gestureZoom: Float,
                              _,
                              _,
                              changes: List<PointerInputChange> ->

                    val oldScale = zoom
                    val newScale = (zoom * gestureZoom).coerceIn(1f..5f)
                    offset =
                        (offset + gestureCentroid / oldScale) - (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale


                    // 🔥Consume touch when multiple fingers down
                    // This prevents click and long click if your finger touches a
                    // button while pinch gesture is being invoked
                    val size = changes.size
                    if (size > 1) {
                        changes.forEach { it.consume() }
                    }
                }
            )
        }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        StyleableTutorialText(
            text = "Changing **PointerEventPass** for transfer gestures changes order " +
                    "of pinch and move gesture invocation before click and long click. " +
                    "With **Main** gesture propagates from child to parent. " +
                    "If first click position" +
                    " is one of the buttons it will consume event and pinch/zoom won't work",
            bullets = false
        )

        ExposedSelectionMenu(title = "PointerEventPass",
            index = when (pass) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                pass = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = transformModifier
                .clipToBounds()
                .fillMaxSize()
                .graphicsLayer {
                    translationX = -offset.x * zoom
                    translationY = -offset.y * zoom
                    scaleX = zoom
                    scaleY = zoom
                }
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly

        ) {
            outer.forEach { inner ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    inner.forEach { tile ->

                        var text by remember {
                            mutableStateOf(tile.toString())
                        }

                        val color = if (text == "CL") Green400
                        else if (text == "LO") Orange400
                        else Pink400

                        Box(
                            Modifier
                                .size(50.dp)
                                .background(color)
                                .combinedClickable(
                                    onClick = {
                                        text = "CL"
                                    },
                                    onLongClick = {
                                        text = "LO"
                                    }
                                )
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun PointerInputScope.customDetectTransformGestures(
    panZoomLock: Boolean = false,
    consume: Boolean = false,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = {}
) {
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        // Wait for at least one pointer to press down, and set first contact position
        val down: PointerInputChange = awaitFirstDown(
            requireUnconsumed = false,
            pass = pass
        )
        onGestureStart(down)

        println("PASS pass: $pass")

        var pointer = down
        // Main pointer is the one that is down initially
        var pointerId = down.id

        do {
            val event = awaitPointerEvent(pass = pass)

            // If any position change is consumed from another PointerInputChange
            // or pointer count requirement is not fulfilled
            val canceled =
                event.changes.any { it.isConsumed }

            if (!canceled) {

                // Get pointer that is down, if first pointer is up
                // get another and use it if other pointers are also down
                // event.changes.first() doesn't return same order
                val pointerInputChange =
                    event.changes.firstOrNull { it.id == pointerId }
                        ?: event.changes.first()

                // Next time will check same pointer with this id
                pointerId = pointerInputChange.id
                pointer = pointerInputChange

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
                        onGesture(
                            centroid,
                            panChange,
                            zoomChange,
                            effectiveRotation,
                            pointer,
                            event.changes
                        )
                    }

                    if (consume) {
                        event.changes.forEach {
                            if (it.positionChanged()) {
                                it.consume()
                            }
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        onGestureEnd(pointer)
    }
}