package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.*

@Composable
fun Tutorial5_6Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        CombinedGestureExample1()
        CombinedGestureExample2()
    }
}

@Composable
private fun CombinedGestureExample1() {
    var gestureText by remember { mutableStateOf("Drag boxes to monitor motion propagation\n") }
    var gestureColorOuter by remember { mutableStateOf(Color.White) }
    var gestureColorInner by remember { mutableStateOf(Color.White) }

    val outerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorOuter)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureText += "Outer onDragStart offset: $offset\n"
                    gestureColorOuter = Blue400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Outer onDrag dragAmount: $dragAmount\n"
                    gestureColorOuter = Pink400

                },
                onDragEnd = {
                    gestureText += "Outer onDragEnd\n"
                    gestureColorOuter = Green400
                },
                onDragCancel = {
                    gestureText += "Outer onDragCancel\n"
                    gestureColorOuter = Yellow400
                }
            )
        }

    val innerModifier = Modifier
        .border(2.dp, color = gestureColorInner, shape = RoundedCornerShape(8.dp))
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(100.dp)
        .background(Color.White)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureText += "Inner onDragStart offset: $offset\n"
                    gestureColorInner = Blue400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Inner onDrag dragAmount: $dragAmount\n"
                    gestureColorInner = Blue400

                },
                onDragEnd = {
                    gestureText += "Inner onDragEnd\n"
                    gestureColorInner = Green400
                },
                onDragCancel = {
                    gestureText += "Inner onDragCancel\n"
                    gestureColorInner = Yellow400
                }
            )
        }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(outerModifier, contentAlignment = Alignment.Center) {
            Box(modifier = innerModifier)
        }
    }

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}


@Composable
private fun CombinedGestureExample2() {
    var gestureText by remember { mutableStateOf("Drag boxes to monitor motion propagation\n") }
    var gestureColorOuter by remember { mutableStateOf(Color.White) }
    var gestureColorInner by remember { mutableStateOf(Color.White) }

    val outerModifier = Modifier
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(200.dp)
        .background(gestureColorOuter)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    gestureText = ""
                    gestureText += "Outer onDragStart offset: $offset\n"
                    gestureColorOuter = Blue400
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText += "Outer onDrag dragAmount: $dragAmount\n"
                    gestureColorOuter = Pink400

                },
                onDragEnd = {
                    gestureText += "Outer onDragEnd\n"
                    gestureColorOuter = Green400
                },
                onDragCancel = {
                    gestureText += "Outer onDragCancel\n"
                    gestureColorOuter = Yellow400
                }
            )
        }
//        .pointerInput(Unit) {
//            forEachGesture {
//                awaitPointerEventScope {
//
//                    val down: PointerInputChange = awaitFirstDown()
//                    down.consumeDownChange()
//
//                    var pointer = down
//                    gestureText += "Inner Down"
//                    gestureColorOuter = Blue400
//
//                    val change: PointerInputChange? =
//                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
//                            // 🔥🔥 If consumePositionChange() is not consumed drag does not
//                            // function properly.
//                            // Consuming position change causes change.positionChanged() to return false.
//                            change.consumePositionChange()
//                        }
//
//                    if (change != null) {
//                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
//                        drag(change.id) { pointerInputChange: PointerInputChange ->
//                            pointer = pointerInputChange
//                            gestureText += "Inner onDrag dragAmount: ${pointer.positionChange()}\n"
//                            gestureColorOuter = Pink400
//                            change.consumePositionChange()
//                        }
//
//                        gestureText += "Inner onDragEnd\n"
//                        gestureColorOuter = Green400
//                    } else {
//                        gestureText += "Inner onDragCancel\n"
//                        gestureColorOuter = Yellow400
//                    }
//
//                }
//            }
//        }

    val innerModifier = Modifier
        .border(2.dp, color = Brown400, shape = RoundedCornerShape(8.dp))
        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
        .size(100.dp)
        .background(gestureColorInner)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    gestureText = ""

                    val down: PointerInputChange = awaitFirstDown()
//                    down.consumeDownChange()

                    var pointer = down
                    gestureText += "Inner Down consumed: ${down.changedToDown()}\n"
                    gestureColorInner = Blue400

                    val change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            change.consumePositionChange()
                        }

                    if (change != null) {
                        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
                        drag(change.id) { pointerInputChange: PointerInputChange ->
                            pointer = pointerInputChange
                            gestureText += "Inner onDrag ${pointer.positionChanged()}," +
                                    " dragAmount: ${pointer.positionChange()}\n"
                            gestureColorInner = Pink400
                            change.consumePositionChange()
                        }

                        gestureText += "Inner onDragEnd\n"
                        gestureColorInner = Green400
                    } else {
                        gestureText += "Inner onDragCancel\n"
                        gestureColorInner = Yellow400
                    }

                }
            }
        }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(outerModifier, contentAlignment = Alignment.Center) {
            Box(modifier = innerModifier)
        }
    }

    Text(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        text = gestureText,
        color = Color.White
    )
}



private val gestureTextModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .background(BlueGrey400)
    .height(100.dp)
    .padding(2.dp)