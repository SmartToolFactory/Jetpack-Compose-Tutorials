package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial5_9Screen5() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        // In this example blue boxes are draggable and nested scrollable
        // while pink boxes are only draggable which doesn't have build in nested scrolling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                repeat(12) {
                    if (it % 2 == 0) {
                        Draggable()
                    } else {
                        DraggableWithNestedScroll()
                    }
                }
            }
        }
    }
}

/**
 * This composable supports nested scrolling. When scrolled parent waits for this
 * Composable's scroll to end to continue scrolling
 */
@Composable
private fun DraggableWithNestedScroll() {
    // Let's take Modifier.draggable (which doesn't have nested scroll build in, unlike Modifier
    // .scrollable) and add nested scroll support our component that contains draggable

    // this will be a generic components that will work inside other nested scroll components.
    // put it inside LazyColumn or / Modifier.verticalScroll to see how they will interact

    // first, state and it's bounds
    val basicState = remember { mutableStateOf(0f) }
    val minBound = -180f
    val maxBound = 180f
    // lambda to update state and return amount consumed
    val onNewDelta: (Float) -> Float = { delta ->
        val oldState = basicState.value
        val newState = (basicState.value + delta).coerceIn(minBound, maxBound)
        basicState.value = newState
        newState - oldState
    }
    // create a dispatcher to dispatch nested scroll events (participate like a nested scroll child)
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }

    // create nested scroll connection to react to nested scroll events (participate like a parent)
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // we have no fling, so we're interested in the regular post scroll cycle
                // let's try to consume what's left if we need and return the amount consumed
                val vertical = available.y
                val weConsumed = onNewDelta(vertical)
                return Offset(x = 0f, y = weConsumed)
            }
        }
    }

    val modifier = Modifier
        .size(180.dp)
        .background(Blue400)
        // attach ourselves to nested scroll system
        .nestedScroll(connection = nestedScrollConnection, dispatcher = nestedScrollDispatcher)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->

                // here's regular drag. Let's be good citizens and ask parents first if they
                // want to pre consume (it's a nested scroll contract)
                val parentsConsumed = nestedScrollDispatcher.dispatchPreScroll(
                    available = Offset(x = 0f, y = delta),
                    source = NestedScrollSource.UserInput
                )

                // adjust what's available to us since might have consumed smth
                val adjustedAvailable = delta - parentsConsumed.y
                // we consume
                val weConsumed = onNewDelta(adjustedAvailable)
                // dispatch as a post scroll what's left after pre-scroll and our consumption
                val totalConsumed = Offset(x = 0f, y = weConsumed) + parentsConsumed
                val left = adjustedAvailable - weConsumed

                nestedScrollDispatcher.dispatchPostScroll(
                    consumed = totalConsumed,
                    available = Offset(x = 0f, y = left),
                    source = NestedScrollSource.UserInput
                )

                // we won't dispatch pre/post fling events as we have no flinging here, but the
                // idea is very similar:
                // 1. dispatch pre fling, asking parents to pre consume
                // 2. fling (while dispatching scroll events like above for any fling tick)
                // 3. dispatch post fling, allowing parent to react to velocity left
            }
        )

    Box(modifier) {
        Text(
            "State: ${basicState.value.roundToInt()}",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun Draggable() {

    var offsetY by remember { mutableStateOf(0f) }

    val modifier = Modifier
        // The draggable modifier is the high-level entry point to
        // drag gestures in a single orientation, and reports the drag distance in pixels.

        //It's important to note that this modifier is similar to scrollable,
        // in that it only detects the gesture. You need to hold the state and represent
        // it on screen by, for example, moving the element via the offset modifier:
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                offsetY += delta
            }
        )
        .size(180.dp)
        .background(Pink400)


    Box(modifier) {
        Text(
            "Offset: ${offsetY.roundToInt()}",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
