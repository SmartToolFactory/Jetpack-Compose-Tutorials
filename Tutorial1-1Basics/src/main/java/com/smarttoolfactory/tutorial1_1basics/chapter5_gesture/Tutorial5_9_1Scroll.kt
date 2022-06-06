package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.launch

@Composable
fun Tutorial5_9Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        StyleableTutorialText(
            text = "The scrollable modifier differs from the scroll modifiers " +
                    "in that scrollable detects the scroll gestures, but does not offset its contents." +
                    "A ScrollableState is required for this modifier to work correctly.",
            bullets = false
        )
        ScrollableModifierSample()
        StyleableTutorialText(
            text = "The verticalScroll and horizontalScroll modifiers provide the simplest way " +
                    "to allow the user to scroll an element when the bounds of " +
                    "its contents are larger than its maximum size constraints.",
            bullets = false
        )
        ScrollExample()
    }
}

@Composable
private fun ScrollableModifierSample() {
    // actual composable state
    var offset by remember { mutableStateOf(0f) }
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scrollable(
                orientation = Orientation.Vertical,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            )
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(offset.toString())
    }
}

@Composable
private fun ScrollExample() {

    Column(modifier = Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()

        // Smoothly scroll 100px on first composition
        val state = rememberScrollState()

        Column(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .verticalScroll(state)
        ) {
            repeat(30) {
                Text("Item $it", modifier = Modifier.padding(2.dp), fontSize = 20.sp)
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    state.animateScrollTo(100)
                }
            }
        ) {
            Text(text = "Smooth Scroll to top")
        }


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    state.scrollTo(100)
                }
            }) {
            Text(text = "Scroll to top")
        }
    }
}