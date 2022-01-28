package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_5Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StyleableTutorialText(
            text = "Combine drag and tap gestures with different **pointerInput** functions",
            bullets = false
        )
        CombinedDragTapGestureSample()

        StyleableTutorialText(
            text = "Combine **awaitPointerEventScope** with **detectTapGestures**. In this example" +
                    "**awaitPointerEventScope** is in first order and doesn't receive events",
            bullets = false
        )
        CombinedPointerInputSample()
        StyleableTutorialText(
            text = "Combine  **detectTapGestures** and **awaitPointerEventScope**. In this example" +
                    "**awaitPointerEventScope** is in second order and receives events",
            bullets = false
        )
        CombinedPointerInputSample2()
    }
}

@Composable
private fun CombinedDragTapGestureSample() {

    var gestureText by remember { mutableStateOf("Drag pointer") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    val context = LocalContext.current

    val pointerModifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(gestureColor)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = Orange400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onTap = {
                    gestureText = "onTap offset: $it"
                    gestureColor = Pink400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"
                    gestureColor = Blue400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                    gestureColor = Brown400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureText = "onDragStart offset: $offset"
                    gestureColor = BlueGrey400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()

                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText = "onDrag dragAmount: $dragAmount"
                    gestureColor = Purple400

                },
                onDragEnd = {
                    gestureText = "onDragEnd"
                    gestureColor = Green400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onDragCancel = {
                    gestureText = "onDragCancel"
                    gestureColor = Pink400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
    }
}

@Composable
private fun CombinedPointerInputSample() {
    var gestureText by remember { mutableStateOf("Drag pointer") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    val context = LocalContext.current

    val pointerModifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down = awaitFirstDown()

                    gestureText = "DOWN"
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()

                    do {
                        val event: PointerEvent = awaitPointerEvent()
                        gestureColor = Blue400
                        var eventChanges = ""

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "pos: ${pointerInputChange.position}\n"
                            }

                        gestureText = "EVENT changes size ${event.changes.size}\n" + eventChanges
                    } while (event.changes.any { it.pressed })


                    gestureColor = Green400
                    gestureText = "UP"
                }
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = Orange400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onTap = {
                    gestureText = "onTap offset: $it"
                    gestureColor = Pink400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"
                    gestureColor = Blue400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()

                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                    gestureColor = Brown400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
    }
}


@Composable
private fun CombinedPointerInputSample2() {
    var gestureText by remember { mutableStateOf("Drag pointer") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    val context = LocalContext.current

    val pointerModifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(gestureColor)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = Orange400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onTap = {
                    gestureText = "onTap offset: $it"
                    gestureColor = Pink400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"
                    gestureColor = Blue400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()

                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                    gestureColor = Brown400
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down = awaitFirstDown()

                    gestureText = "DOWN"
                    Toast
                        .makeText(context, gestureText, Toast.LENGTH_SHORT)
                        .show()

                    do {
                        val event: PointerEvent = awaitPointerEvent()
                        gestureColor = Blue400
                        var eventChanges = ""

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "pos: ${pointerInputChange.position}\n"
                            }

                        gestureText = "EVENT changes size ${event.changes.size}\n" + eventChanges
                    } while (event.changes.any { it.pressed })


                    gestureColor = Green400
                    gestureText = "UP"
                }
            }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
    }
}