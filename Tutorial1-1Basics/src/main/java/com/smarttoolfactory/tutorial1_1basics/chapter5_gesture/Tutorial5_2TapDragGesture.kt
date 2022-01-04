package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Composable
fun Tutorial5_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "1-) **detectTapGestures** Detects tap, double-tap, and long press gestures and calls onTap, " +
                    "onDoubleTap, and onLongPress, respectively, when detected."
        )
        TutorialText2(text = "Press, Tap, LongPress, and DoubleTap")
        DetectTapGesturesExample()
        TutorialText2(text = "onPress tryAwaitRelease")
        DetectPressAwaitExample()
        StyleableTutorialText(
            text = "2-) **pointerInteropFilter** is a special PointerInputModifier that provides access to the underlying " +
                    "MotionEvents originally dispatched to Compose. Prefer pointerInput and " +
                    "use this only for interoperation with existing code that consumes MotionEvents."
        )
        PointerIteropFilterExample()
        StyleableTutorialText(
            text = "3-) **detectDragGestures** Gesture detector that waits for pointer down and touch slop in any " +
                    "direction and then calls onDrag for each drag event. It follows the touch " +
                    "slop detection of awaitTouchSlopOrCancellation but will consume the position " +
                    "change automatically once the touch slop has been crossed."
        )
        TutorialText2(text = "detectDragGestures")
        DetectDragGesturesCycleExample()
        DetectGesturesExample2()
        TutorialText2(text = "detectDragGesturesAfterLongPress")
        DragAfterLongPressExample()
        TutorialText2(text = "detectHorizontalDragGestures")
        HorizontalDragGestureExample()
        TutorialText2(text = "detectVerticalDragGestures")
        VerticalDragGestureExample()
    }
}

private val modifier = Modifier
    .padding(8.dp)
    .fillMaxWidth()
    .height(50.dp)
    .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), clip = true)

@Composable
private fun DetectTapGesturesExample() {

    var gestureText by remember { mutableStateOf("Pres, Tap, LongPress, DoubleTap") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    val pointerModifier = modifier
        .background(gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = Orange400
                },
                onTap = {
                    gestureText = "onTap offset: $it"
                    gestureColor = Pink400
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"
                    gestureColor = Blue400

                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                    gestureColor = Green400
                }
            )
        }


    GestureDisplayBox(pointerModifier, gestureText)
}

@Composable
private fun DetectPressAwaitExample() {
    var gestureText by remember { mutableStateOf("Release press in our out of bounds") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }
    val context = LocalContext.current

    val pointerModifier = modifier
        .background(gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = Orange400

                    val released = tryAwaitRelease()

                    if (released) {
                        gestureText = "onPress Released"
                        gestureColor = Green400

                        Toast
                            .makeText(context, "ACTION_UP", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        gestureText = "onPress canceled"
                        gestureColor = Red400
                        Toast
                            .makeText(context, "CANCELED", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }


    GestureDisplayBox(pointerModifier, gestureText)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PointerIteropFilterExample() {
    var gestureText by remember { mutableStateOf("MotionEvents down, move, up") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }
    val context = LocalContext.current

    val pointerModifier = modifier
        .background(gestureColor, shape = RoundedCornerShape(16.dp))
        .pointerInteropFilter { event: MotionEvent ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Toast
                        .makeText(
                            context, "ACTION_DOWN\n" +
                                    "rawX: ${event.rawX}, rawY: ${event.rawY}, " +
                                    "x: ${event.x}, y: ${event.y}", Toast.LENGTH_SHORT
                        )
                        .show()
                    gestureColor = Orange400
                }
                MotionEvent.ACTION_MOVE -> {
                    gestureText =
                        "ACTION_MOVE\n" +
                                "rawX: ${event.rawX.toInt()}, rawY: ${event.rawY.toInt()}, " +
                                "x: ${event.x.toInt()}, y: ${event.y.toInt()}"

                    gestureColor = Blue400


                }
                MotionEvent.ACTION_UP -> {
                    gestureText =
                        "ACTION_UP\n" +
                                "rawX: ${event.rawX.toInt()}, rawY: ${event.rawY.toInt()}, " +
                                "x: ${event.x.toInt()}, y: ${event.y.toInt()}"
                    gestureColor = Green400
                }
                else -> false
            }
            true
        }



    GestureDisplayBox(pointerModifier, gestureText)
}

@Composable
private fun DetectDragGesturesCycleExample() {
    var gestureText by remember { mutableStateOf("Drag pointer") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    var gestureDetailText by remember { mutableStateOf("Drag button above to see drag details") }

    val context = LocalContext.current

    val pointerModifier = modifier
        .background(gestureColor, shape = RoundedCornerShape(8.dp))
        .pointerInput(Unit) {

            detectDragGestures(
                onDragStart = { offset ->
                    gestureText = "onDragStart offset: $offset"
                    gestureColor = Blue400
                    Toast
                        .makeText(context, "onDragStart", Toast.LENGTH_SHORT)
                        .show()
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    gestureText = "onDrag dragAmount: $dragAmount"
                    gestureColor = Pink400

                    gestureDetailText = """
                       id: ${change.id}, type: ${change.type}, position: ${change.position}, previousPosition: ${change.previousPosition}  
                       consumed downChange: ${change.consumed.downChange}, previousPressed: ${change.previousPressed}
                       previousUptimeMillis: ${change.previousUptimeMillis}, ${change.position}
                       changedToDOwn: ${change.changedToDown()}, changedToUp: ${change.changedToUp()}
                    """.trimIndent()
                },
                onDragEnd = {
                    gestureText = "onDragEnd"
                    gestureColor = Green400
                    Toast
                        .makeText(context, "onDragEnd", Toast.LENGTH_SHORT)
                        .show()
                },
                onDragCancel = {
                    gestureText = "onDragCancel"
                    gestureColor = Yellow400
                }
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        GestureDisplayBox(pointerModifier, gestureText)
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            color = Color.White,
            text = gestureDetailText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DetectGesturesExample2() {

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }
    var boxColor by remember { mutableStateOf(Blue400) }

    var dragDetailText by remember {
        mutableStateOf(
            "Drag blue box to change its Modifier.offset{IntSze}"
        )
    }
    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(200.dp)
            /*
                    🔥 Use this modifier as read-only to not get stuck in recomposition loops

                    Use Layout or SubcomposeLayout to have the size of one component affect the
                    size of another component. Using the size received from the onSizeChanged
                    callback in a MutableState to affect layout will cause the new value to be
                    recomposed and read only in the following frame, causing a one frame lag.
                    You can use onSizeChanged to affect drawing operations.
                 */
            .onSizeChanged {
                println("✏️ DetectGesturesExample2() onSizeChanged() size: $it")
                size = it.toSize()
            }
    ) {

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )


        Box(
            Modifier
                // 🔥 We change offset(position) of blue box by dragging it
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .size(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {
                    detectDragGestures(

                        onDragStart = {
                            boxColor = Green400
                        },

                        onDragEnd = {
                            boxColor = Blue400
                        },
                        onDrag = { _, dragAmount ->

                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + dragAmount

                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )

                            offsetX.value = newValue.x
                            offsetY.value = newValue.y

                            dragDetailText =
                                "dragAmount: $dragAmount\noriginal: $original\nsummed: $summed"


                        }
                    )
                }
        )
    }
}

/**
 * Gesture detector that waits for pointer down and long press,
 * after which it calls onDrag for each drag event. onDragStart will be
 * called when long press in detected with the last known pointer position
 * provided. onDragEnd is called after all pointers are up and onDragCancel
 * is called if another gesture has consumed pointer input, canceling this gesture.
 */
@Composable
private fun DragAfterLongPressExample() {

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }

    var boxColor by remember { mutableStateOf(Blue400) }
    var dragDetailText by remember {
        mutableStateOf(
            "Unlike detectDragGestures you need to long press for drag onDrag to commence"
        )
    }
    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(200.dp)
            .onSizeChanged { size = it.toSize() }
    ) {

        Text(
            text = dragDetailText,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {
                    /*
                    Gesture detector that waits for pointer down and long press,
                    after which it calls onDrag for each drag event. onDragStart will be
                    called when long press in detected with the last known pointer position
                    provided. onDragEnd is called after all pointers are up and onDragCancel
                    is called if another gesture has consumed pointer input, canceling this gesture.
                     */
                    detectDragGesturesAfterLongPress(

                        onDragStart = {
                            boxColor = Green400
                        },

                        onDragEnd = {
                            boxColor = Blue400
                        },

                        onDrag = { _, dragAmount ->
                            val original = Offset(offsetX.value, offsetY.value)
                            val summed = original + dragAmount
                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                            )
                            offsetX.value = newValue.x
                            offsetY.value = newValue.y

                            dragDetailText =
                                "dragAmount: $dragAmount\noriginal: $original\nsummed: $summed"
                        }
                    )
                }
        )
    }
}

@Composable
private fun HorizontalDragGestureExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }

    var boxColor by remember { mutableStateOf(Blue400) }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.LightGray)
            .onSizeChanged { width = it.width.toFloat() }
    ) {
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .fillMaxHeight()
                .width(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {

                    detectHorizontalDragGestures(

                        onDragStart = {
                            boxColor = Green400
                        },

                        onDragEnd = {
                            boxColor = Blue400
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val originalX = offsetX.value
                            val newValue =
                                (originalX + dragAmount).coerceIn(0f, width - 50.dp.toPx())
                            offsetX.value = newValue
                        }
                    )
                }
        )
    }
}

@Composable
private fun VerticalDragGestureExample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    var boxColor by remember { mutableStateOf(Blue400) }

    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .width(60.dp)
            .height(200.dp)
            .background(Color.LightGray)
            .onSizeChanged { height = it.height.toFloat() }
    ) {
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .fillMaxWidth()
                .height(50.dp)
                .background(boxColor)
                .pointerInput(Unit) {

                    detectVerticalDragGestures(
                        onDragStart = {
                            boxColor = Green400
                        },

                        onDragEnd = {
                            boxColor = Blue400
                        },
                        onVerticalDrag = { _, dragAmount ->
                            val originalY = offsetY.value
                            val newValue =
                                (originalY + dragAmount).coerceIn(0f, height - 50.dp.toPx())
                            offsetY.value = newValue
                        }
                    )
                }
        )
    }
}

@Composable
fun GestureDisplayBox(
    modifier: Modifier,
    gestureText: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = gestureText,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}