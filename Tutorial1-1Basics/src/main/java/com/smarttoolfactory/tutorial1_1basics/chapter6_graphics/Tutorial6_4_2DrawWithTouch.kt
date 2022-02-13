package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial6_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialText2(text = "Draw with Touch")
//        TouchDrawExample1()
        TouchDrawExample2()
    }
}

@Composable
private fun TouchDrawExample1() {

    val path = remember { Path() }
    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.LightGray) }
    var gestureText by remember { mutableStateOf("Touch to Draw") }

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
                        motionEvent = ACTION_DOWN
                        currentPosition = it.position
                        gestureColor = Blue400
                    }

                    do {
                        // This PointerEvent contains details details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges =
                            "DOWN changedToDown: ${down.changedToDown()} changedUp: ${down.changedToUp()}\n"
                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "changedUp: ${pointerInputChange.changedToUp()}" +
                                        "pos: ${pointerInputChange.position}\n"

                                // This necessary to prevent other gestures or scrolling
                                // when at least one pointer is down on canvas to draw
                                pointerInputChange.consumePositionChange()
                            }

                        gestureText = "EVENT changes size ${event.changes.size}\n" + eventChanges

                        gestureColor = Green400
                        motionEvent = ACTION_MOVE
                        currentPosition = event.changes.first().position
                    } while (event.changes.any { it.pressed })

                    motionEvent = ACTION_UP
                    gestureColor = Color.LightGray

                    gestureText += "UP changedToDown: ${down.changedToDown()} " +
                            "changedUp: ${down.changedToUp()}\n"
                }
            }
        }

    Canvas(modifier = drawModifier) {

        println("🔥 CANVAS $motionEvent, position: $currentPosition")

        when (motionEvent) {
            ACTION_DOWN -> {
                path.moveTo(currentPosition.x, currentPosition.y)
            }
            ACTION_MOVE -> {

                if (currentPosition != Offset.Unspecified) {
                    path.lineTo(currentPosition.x, currentPosition.y)
                }
            }

            ACTION_UP -> {
                path.lineTo(currentPosition.x, currentPosition.y)
            }

            else -> Unit
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    Text(gestureText)
}

@Composable
private fun TouchDrawExample2() {

    // Path used for drawing
    val drawPath = remember { Path() }
    // Path used for erasing. In this example erasing is faked by drawing with canvas color
    // above draw path.
    val erasePath = remember { Path() }

    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var eraseModeOn by remember { mutableStateOf(false) }

    val pathOption = rememberPathOption()

    val drawModifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        .background(Color.LightGray)
        .clipToBounds()
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    awaitFirstDown().also {
                        motionEvent = ACTION_DOWN
                        currentPosition = it.position
                    }

                    do {
                        // This PointerEvent contains details details including events, id,
                        // position and more
                        val event: PointerEvent = awaitPointerEvent()
                        motionEvent = ACTION_MOVE
                        currentPosition = event.changes.first().position
                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                it.consumePositionChange()
                            }
                            pressed
                        }
                    )

                    motionEvent = ACTION_UP
                }
            }
        }

    Canvas(modifier = drawModifier) {

        // Draw or erase depending on erase mode is active or not
        val currentPath = if (eraseModeOn) erasePath else drawPath

        when (motionEvent) {

            ACTION_DOWN -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                print("🚀 CANVAS ACTION_DOWN eraseMode: $eraseModeOn")

            }
            ACTION_MOVE -> {

                print("🔥 CANVAS ACTION_MOVE eraseMode: $eraseModeOn")
                currentPath.lineTo(currentPosition.x, currentPosition.y)

            }

            ACTION_UP -> {
                currentPath.lineTo(currentPosition.x, currentPosition.y)
            }
            else -> Unit
        }

        drawPath(
            color = pathOption.color,
            path = drawPath,
            style = Stroke(
                width = pathOption.strokeWidth,
                cap = pathOption.strokeCap,
                join = pathOption.strokeJoin
            )
        )

        drawPath(
            color = Color.LightGray,
            path = erasePath,
            style = Stroke(
                width = 30f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    DrawingControl(pathOption = pathOption, eraseModeOn) {
        motionEvent = ACTION_IDLE
        eraseModeOn = it
    }
}

@Composable
private fun DrawingControl(
    pathOption: PathOption,
    eraseModeOn: Boolean,
    onEraseModeChange: (Boolean) -> Unit
) {

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var eraseMode = eraseModeOn
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                eraseMode = !eraseMode
                onEraseModeChange(eraseMode)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_deblur_24),
                contentDescription = null,
                tint = if (eraseMode) Color.Black else Color.LightGray
            )
        }
        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            Icon(Icons.Filled.ColorLens, contentDescription = null, tint = pathOption.color)
        }

        IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
            Icon(Icons.Filled.BorderColor, contentDescription = null, tint = Color.LightGray)
        }

        Canvas(
            modifier = Modifier
                .height(10.dp)
                .width(100.dp)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            val path = Path()
            path.moveTo(0f, size.height / 2)
            path.lineTo(size.width, size.height / 2)

            drawPath(
                color = pathOption.color,
                path = path,
                style = Stroke(
                    width = pathOption.strokeWidth,
                    cap = pathOption.strokeCap,
                    join = pathOption.strokeJoin
                )
            )
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            pathOption.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                pathOption.color = color
            }
        )
    }

    if (showPropertiesDialog) {
        DrawingMenuDialog(pathOption) {
            showPropertiesDialog = !showPropertiesDialog
        }
    }
}

@Composable
fun ColorSelectionDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red * 255) }
    var green by remember { mutableStateOf(initialColor.green * 255) }
    var blue by remember { mutableStateOf(initialColor.blue * 255) }

    val color = Color(
        red = red.toInt(),
        green = green.toInt(),
        blue = blue.toInt(),
        alpha = 255
    )

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp)
        ) {

            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Select Color",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Color Selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Column {

                        Text(text = "Red ${red.toInt()}")
                        Slider(
                            value = red,
                            onValueChange = { red = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Green ${green.toInt()}")
                        Slider(
                            value = green,
                            onValueChange = { green = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Blue ${blue.toInt()}")
                        Slider(
                            value = blue,
                            onValueChange = { blue = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = CutCornerShape(5.dp),
                            color = color,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {}
                    }
                }

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onNegativeClick) {
                        Text(text = "CANCEL")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = {
                        onPositiveClick(color)
                    }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawingMenuDialog(pathOption: PathOption, onDismiss: () -> Unit) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(6.dp)) {

                Text(
                    text = "Stroke Width ${pathOption.strokeWidth.toInt()}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Slider(
                    value = pathOption.strokeWidth,
                    onValueChange = { pathOption.strokeWidth = it },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )


                ExposedSelectionMenu(title = "Stroke Cap",
                    index = when (pathOption.strokeCap) {
                        StrokeCap.Butt -> 0
                        StrokeCap.Round -> 1
                        else -> 2
                    },
                    options = listOf("Butt", "Round", "Square"),
                    onSelected = {
                        println("STOKE CAP $it")
                        pathOption.strokeCap = when (it) {
                            0 -> StrokeCap.Butt
                            1 -> StrokeCap.Round
                            else -> StrokeCap.Square
                        }
                    }
                )

                ExposedSelectionMenu(title = "Stroke Join",
                    index = when (pathOption.strokeJoin) {
                        StrokeJoin.Miter -> 0
                        StrokeJoin.Round -> 1
                        else -> 2
                    },
                    options = listOf("Miter", "Round", "Bevel"),
                    onSelected = {
                        println("STOKE JOIN $it")

                        pathOption.strokeJoin = when (it) {
                            0 -> StrokeJoin.Miter
                            1 -> StrokeJoin.Round
                            else -> StrokeJoin.Bevel
                        }
                    }
                )
            }
        }

    }
}

val ACTION_IDLE = 0
val ACTION_DOWN = 1
val ACTION_MOVE = 2
val ACTION_UP = 3

