package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Composable
fun Tutorial5_8Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        StyleableTutorialText(
            text = "The draggable modifier is the high-level entry point to drag " +
                    "gestures in a single orientation, and reports the drag distance in pixels.\n" +
                    "It's important to note that this modifier is similar to " +
                    "scrollable, in that it only detects the gesture. " +
                    "You need to hold the state and represent it on screen by, for example," +
                    "moving the element via the offset modifier",
            bullets = false
        )

        var orientation by remember { mutableStateOf(Orientation.Horizontal) }

        var offsetX by remember(orientation) { mutableStateOf(0f) }
        var offsetY by remember(orientation) { mutableStateOf(0f) }

        ExposedSelectionMenu(title = "Orientation",
            index = when (orientation) {
                Orientation.Horizontal -> 0
                else -> 1
            },
            options = listOf("Horizontal", "Vertical"),
            onSelected = {
                orientation = when (it) {
                    0 -> Orientation.Horizontal
                    else -> Orientation.Vertical
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                // The draggable modifier is the high-level entry point to
                // drag gestures in a single orientation, and reports the drag distance in pixels.

                //It's important to note that this modifier is similar to scrollable,
                // in that it only detects the gesture. You need to hold the state and represent
                // it on screen by, for example, moving the element via the offset modifier:
                .draggable(
                    orientation = orientation,
                    state = rememberDraggableState { delta ->
                        if (orientation == Orientation.Horizontal) {
                            offsetX += delta
                        } else {
                            offsetY += delta
                        }
                    }
                )
                .background(Blue400)
                .padding(20.dp),
            text = "Drag me!",
            fontSize = 20.sp,
            color = Color.White
        )
    }
}