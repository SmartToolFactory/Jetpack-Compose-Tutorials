package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Composable
fun Tutorial6_4Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    DrawingApp()
}

@Composable
private fun DrawingApp() {

    val context = LocalContext.current

    /*
        Paths that are added, this is required to have paths with different options and paths
        with erase to keep over each other
     */
    val paths = remember { linkedMapOf<Path, PathProperties>() }

    // Canvas touch state. Idle by default, Down at first contact, Move while dragging and UP
    // when first pointer is up
    var motionEvent by remember { mutableStateOf(ACTION_IDLE) }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    val currentPathOption = remember { PathProperties() }

    var currentPath = remember { Path() }
    var isPathCompleted = remember { false }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        val drawModifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
//            .background(Color.White)
            .background(getRandomColor())
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {

                        // Wait for at least one pointer to press down, and set first contact position
                        val down: PointerInputChange = awaitFirstDown().also {
                            motionEvent = ACTION_DOWN
                            currentPosition = it.position
                        }

                        do {
                            // This PointerEvent contains details including events, id, position and more
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

        Canvas(modifier = drawModifier.background(getRandomColor())) {

            println("CANVAS motionEvent: $motionEvent")

            when (motionEvent) {
                ACTION_DOWN -> {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                    isPathCompleted = false
                }
                ACTION_MOVE -> {
                    if (currentPosition != Offset.Unspecified) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)
                    }
                }

                ACTION_UP -> {
                    println(
                        "🔥 Canvas ACTION_UP " +
                                "currentPath: ${currentPath.hashCode()}, " +
                                "currentPathOption: ${currentPathOption.hashCode()}"
                    )

                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    paths[currentPath] = PathProperties(
                        strokeWidth = currentPathOption.strokeWidth,
                        color = currentPathOption.color,
                        strokeCap = currentPathOption.strokeCap,
                        strokeJoin = currentPathOption.strokeJoin,
                    ).apply {
                        eraseMode = currentPathOption.eraseMode
                    }

                    currentPath = Path()

                    println(
                        "🔥🔥 Canvas ACTION_UP " +
                                "currentPath: ${currentPath.hashCode()}, " +
                                "currentPathOption: ${currentPathOption.hashCode()}"
                    )


                }

                else -> {
                    println(
                        "🔥🔥🔥 Canvas ACTION_IDLE " +
                                "currentPath: ${currentPath.hashCode()}, " +
                                "currentPathOption: ${currentPathOption.hashCode()}"
                    )
                }
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)

                paths.forEach {

                    val path = it.key
                    val pathOption = it.value

                    println("🍏 PATHS DRAW path: ${path.hashCode()}, pathOption: ${pathOption.hashCode()}")

                    if (!pathOption.eraseMode) {
                        drawPath(
                            color = pathOption.color,
                            path = path,
                            style = Stroke(
                                width = pathOption.strokeWidth,
                                cap = pathOption.strokeCap,
                                join = pathOption.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = 30f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                if (!isPathCompleted) {

                    println("🍒 Drawing CURRENT PATH")

                    if (!currentPathOption.eraseMode) {
                        drawPath(
                            color = currentPathOption.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathOption.strokeWidth,
                                cap = currentPathOption.strokeCap,
                                join = currentPathOption.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathOption.strokeWidth,
                                cap = currentPathOption.strokeCap,
                                join = currentPathOption.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }

        DrawingPropertiesMenu(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp),
            properties = currentPathOption,
            drawMode = drawMode,
            onPathPropertiesChange = {
//                motionEvent = ACTION_IDLE
            },
            onDrawModeChanged = {
//                motionEvent = ACTION_IDLE
                drawMode = it
                Toast.makeText(
                    context, "Draw Mode: $drawMode", Toast.LENGTH_SHORT
                ).show()
            }
        )

    }
}

@Composable
private fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    properties: PathProperties,
    drawMode: DrawMode,
    onPathPropertiesChange: (PathProperties) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                    DrawMode.Draw
                } else {
                    DrawMode.Touch
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                Icons.Filled.TouchApp,
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Touch) Color.Black else Color.LightGray
            )
        }
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                    DrawMode.Draw
                } else {
                    DrawMode.Erase
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_deblur_24),
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Erase) Color.Black else Color.LightGray
            )
        }
        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            Icon(Icons.Filled.ColorLens, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
            Icon(Icons.Filled.BorderColor, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Undo, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Redo, contentDescription = null, tint = Color.LightGray)
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
                color = properties.color,
                path = path,
                style = Stroke(
                    width = properties.strokeWidth,
                    cap = properties.strokeCap,
                    join = properties.strokeJoin
                )
            )
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.color = color
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = !showPropertiesDialog
        }
    }
}

@Composable
internal fun PropertiesMenuDialog(pathOption: PathProperties, onDismiss: () -> Unit) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Stroke Width ${pathOption.strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
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

data class PathProperties(
    var strokeWidth: Float = 10f,
    var color: Color = Color.Black,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var eraseMode: Boolean = false
)