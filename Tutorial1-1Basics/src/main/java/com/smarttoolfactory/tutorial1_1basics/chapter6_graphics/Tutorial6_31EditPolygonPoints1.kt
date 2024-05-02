package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.MotionEvent
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import kotlin.math.sqrt

@Preview
@Composable
fun Tutorial6_31Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    PolygonDrawingApp()
}

@Preview
@Composable
private fun PolygonDrawingApp() {
    /**
     * Canvas touch state. [MotionEvent.Idle] by default, [MotionEvent.Down] at first contact,
     * [MotionEvent.Move] while dragging and [MotionEvent.Up] when first pointer is up
     */
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    /**
     * Position pointer is touched to device
     */
    var firstTouchPoint by remember { mutableStateOf(Point(Offset.Unspecified)) }

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentTouchPoint by remember { mutableStateOf(Point(Offset.Unspecified)) }

    /**
     * Draw mode, erase mode or touch mode to
     */
    var mode by remember { mutableStateOf(Mode.Draw) }

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPolygon by remember { mutableStateOf(Polygon(), policy = neverEqualPolicy()) }

    LaunchedEffect(mode, motionEvent) {
        if (mode == Mode.Touch && motionEvent == MotionEvent.Idle) {

            val firstPoint: Point? = currentPolygon.firstPoint
            val lastPoint: Point? = currentPolygon.lastPoint

            if (firstPoint != null &&
                lastPoint != null &&
                calculateDistanceFromCenter(firstPoint.position, lastPoint.position) > 30f
            ) {
                currentPolygon.lines.add(
                    Line(lastPoint, firstPoint)
                )
                firstTouchPoint = Point(Offset.Unspecified)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = {
                    mode = if (mode == Mode.Draw) {
                        Mode.Touch
                    } else Mode.Draw
                }
            ) {
                Text("Mode: $mode")
            }

            Button(
                onClick = {
                    currentPolygon = Polygon()
                    mode = Mode.Draw
                }
            ) {
                Text("Reset")
            }
        }


        val drawModifier = Modifier
            .border(2.dp, getRandomColor())
            .fillMaxSize()
            .pointerMotionEvents(
                onDown = { pointerInputChange ->

                    motionEvent = MotionEvent.Down

                    if (mode == Mode.Touch) {
                        // In touch mode check if any points or circles are touched
                        // to move them on drag
                        val position = pointerInputChange.position
                        currentPolygon.lines.forEach { line: Line ->
                            val startPos = line.start.position
                            // Distance from center to touch point
                            val distance = calculateDistanceFromCenter(startPos, position)
                            if (distance < 30f) {
                                line.start.isTouched = true
                            } else {
                                line.start.isTouched = false
                            }
                        }
                    } else {

                        // Check if this is the start of polygon
                        val isPolygonStart = firstTouchPoint.position == Offset.Unspecified

                        firstTouchPoint =
                            if (isPolygonStart) {
                                Point(pointerInputChange.position)
                            } else currentTouchPoint


                        // If it's start of polygon move path to this point before drawing lines
                        // or adding points
                        if (isPolygonStart) {
                            val currentPath = currentPolygon.path
                            currentPath.moveTo(
                                firstTouchPoint.position.x,
                                firstTouchPoint.position.y
                            )
                        }

                    }
                },
                onMove = { pointerInputChange ->

                    motionEvent = MotionEvent.Move

                    if (mode == Mode.Touch) {
                        currentPolygon.lines.forEach { line: Line ->
                            val startPoint = line.start

                            if (startPoint.isTouched) {
                                startPoint.position = pointerInputChange.position
                                currentPolygon = Polygon(
                                    path = currentPolygon.path,
                                    lines = currentPolygon.lines
                                )
                            }
                        }

                        // Reset path to show draw area
                        currentPolygon.path.apply {
                            currentPolygon.lines.firstOrNull()?.start?.let {
                                rewind()
                                moveTo(it.position.x, it.position.y)
                                currentPolygon.lines.forEach { line: Line ->
                                    val endPos = line.end.position
                                    lineTo(endPos.x, endPos.y)
                                }
                            }
                        }

                    } else {
                        currentTouchPoint = Point(pointerInputChange.position)
                    }
                },
                onUp = { pointerInputChange ->

                    if (mode == Mode.Touch) {

                        currentPolygon.lines.forEach {
                            it.start.isTouched = false
                        }

                        // Reset path to show draw area
                        currentPolygon.path.apply {
                            currentPolygon.lines.firstOrNull()?.start?.let {
                                rewind()
                                moveTo(it.position.x, it.position.y)
                                currentPolygon.lines.forEach { line: Line ->
                                    val endPos = line.end.position
                                    lineTo(endPos.x, endPos.y)
                                }
                            }
                        }

                    } else {

                        currentTouchPoint = Point(pointerInputChange.position)

                        val firstPoint: Point? = currentPolygon.firstPoint
                        val lastPoint: Point? = currentPolygon.lastPoint

                        if (firstPoint != null &&
                            lastPoint != null &&
                            calculateDistanceFromCenter(
                                currentTouchPoint.position,
                                firstPoint.position
                            ) < 30f
                        ) {
                            currentPolygon.lines.add(
                                Line(lastPoint, firstPoint)
                            )

                            currentPolygon.path.close()

                            firstTouchPoint = Point(Offset.Unspecified)

                        } else {
                            val line = Line(firstTouchPoint, currentTouchPoint)
                            currentPolygon.lines.add(line)

                            val currentPath = currentPolygon.path

                            val currentPosition = currentTouchPoint.position
                            currentPath.lineTo(
                                currentPosition.x, currentPosition.y
                            )
                            currentPolygon = Polygon(
                                path = currentPath,
                                lines = currentPolygon.lines
                            )
                        }
                    }

                    motionEvent = MotionEvent.Up
                },
                delayAfterDownInMillis = 20
            )

        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.landscape6),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Canvas(modifier = drawModifier) {
                when (motionEvent) {
                    MotionEvent.Move -> {
                        if (mode != Mode.Touch) {
                            drawLine(
                                color = Color.Black,
                                start = firstTouchPoint.position,
                                end = currentTouchPoint.position,
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

                    MotionEvent.Up -> {
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                currentPolygon.lines.forEach { line: Line ->

                    drawLine(
                        color = Blue400,
                        start = line.start.position,
                        end = line.end.position,
                        strokeWidth = 3.dp.toPx()
                    )

                    drawCircle(
                        color = if (line.start.isTouched) Color.Red else Color.Green,
                        radius = 30f,
                        center = line.start.position,
                    )

                    drawCircle(
                        color = Color.Blue,
                        radius = 30f,
                        center = line.end.position,
                        style = Stroke(
                            2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                }


                val path = currentPolygon.path

                drawPath(
                    path = path, color = Blue400.copy(alpha = .5f)
                )

            }
        }
    }
}

@Immutable
data class Polygon(
    val path: Path = Path(),
    val lines: SnapshotStateList<Line> = mutableStateListOf()
) {
    val firstPoint: Point? = lines.firstOrNull()?.start
    val lastPoint: Point? = lines.lastOrNull()?.end
}

@Immutable
data class Line(
    val start: Point,
    val end: Point
) {

    fun isPointExist(offset: Offset): Boolean {
        val startPos = start.position
        val endPos = end.position
        return ((endPos.y - offset.y) / (endPos.x - offset.x)) ==
                ((offset.y - startPos.y) / (offset.x - startPos.x))
    }
}

class Point(
    var position: Offset,
    var isTouched: Boolean = false
)

private enum class Mode {
    Draw, Touch
}

private fun calculateDistanceFromCenter(center: Offset, position: Offset): Float {
    val dy = center.y - position.y
    val dx = position.x - center.x
    return sqrt(dx * dx + dy * dy)
}
