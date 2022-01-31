package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.launch

@Composable
fun Tutorial5_6Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        StyleableTutorialText(
            text = "Add **indication** to modifier and use **pointerInput** with " +
                    "**detectTapGestures** to add ripple when a gesture occurs.",
            bullets = false
        )
        TapGesturesIndicationSample()
        StyleableTutorialText(
            text = "Add **indication** to modifier and use **awaitPointerEventScope** " +
                    "with **awaitFirstDown** and emit PressInteraction.Release after " +
                    "**waitForUpOrCancellation**.",
            bullets = false
        )
        AwaitPointerEventScopeIndicationSample()
    }
}

@Composable
private fun TapGesturesIndicationSample() {

    var gestureText by remember { mutableStateOf("Tap, press or long press gestures") }

    // This is for emitting press or release event
    val interactionSource = remember { MutableInteractionSource() }


    val pointerModifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(Color(0xffBDBDBD))
        .indication(interactionSource, rememberRipple())
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset: Offset ->
                    gestureText = "onPress"

                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    // Waits for the press to be released before returning.
                    // If the press was released, true is returned, or if the gesture
                    // was canceled by motion being consumed by another gesture, false is returned.
                    tryAwaitRelease()
                    // We emit a release press interaction here
                    interactionSource.emit(PressInteraction.Release(press))

                },
                onTap = { offset ->
                    gestureText = "onTap offset: $offset"
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"

                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                }
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
    }
}

@Composable
private fun AwaitPointerEventScopeIndicationSample() {
    var gestureText by remember { mutableStateOf("Drag pointer") }

    // Thi is for emitting press or release event
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    val pointerModifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(Color(0xffBDBDBD))
        .indication(interactionSource, rememberRipple())
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {


                    val down = awaitFirstDown()
                    val press = PressInteraction.Press(down.position)
                    coroutineScope.launch {
                        interactionSource.emit(press)
                    }

                    gestureText = "DOWN"
                    val up = waitForUpOrCancellation()

                    gestureText = if (up?.position != null) {
                        "UP Pointer up.position: ${(up.position)}"
                    } else {
                        "UP CANCEL"
                    }

                    // We emit a release press interaction here
                    coroutineScope.launch {
                        interactionSource.emit(PressInteraction.Release(press))
                    }

                }
            }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
    }
}
