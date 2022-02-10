package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400

@Composable
fun Tutorial6_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column() {
        TouchDrawExample1()
    }
}

@Composable
private fun TouchDrawExample1() {

    val path = remember { Path() }

    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }


    var nextPosition by remember { mutableStateOf(Offset.Unspecified) }

    var gestureColor by remember { mutableStateOf(Color.LightGray) }
    var gestureText by remember { mutableStateOf("Drag pointer") }

    val drawModifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        .background(gestureColor)
        .clipToBounds()
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown().also {
                        nextPosition = it.position
                        motionEvent = ACTION_DOWN
                    }

                    gestureColor = Blue400

                    do {
                        gestureColor = Green400

                        // This PointerEvent contains details details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges =
                            "DOWN changedToDown: ${down.changedToDown()} changedUp: ${down.changedToUp()}\n"

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, changedUp: ${pointerInputChange.changedToUp()}" +
                                        "pos: ${pointerInputChange.position}\n"

//                                pointerInputChange.consumePositionChange()
                            }

                        gestureText = "EVENT changes size ${event.changes.size}\n" + eventChanges

                        nextPosition = event.changes.first().position
                        motionEvent = ACTION_MOVE

                    } while (event.changes.any { it.pressed })

                    motionEvent = ACTION_UP
                    gestureColor = Color.LightGray

                    gestureText += "UP changedToDown: ${down.changedToDown()} changedUp: ${down.changedToUp()}\n"
                }
            }
        }

    Canvas(modifier = drawModifier) {

        when (motionEvent) {
            ACTION_DOWN -> {
                path.moveTo(nextPosition.x, nextPosition.y)
            }
            ACTION_MOVE -> {
                if (nextPosition != Offset.Unspecified) {
                    path.lineTo(nextPosition.x, nextPosition.y)
                }
            }

            ACTION_UP -> {
                path.lineTo(nextPosition.x, nextPosition.y)
            }

            else -> Unit
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    Text(gestureText)
}

val ACTION_IDLE = 0
val ACTION_DOWN = 1
val ACTION_MOVE = 2
val ACTION_UP = 3