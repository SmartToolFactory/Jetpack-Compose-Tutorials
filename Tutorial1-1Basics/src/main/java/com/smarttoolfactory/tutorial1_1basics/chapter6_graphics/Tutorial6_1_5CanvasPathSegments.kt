package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathSegment
import androidx.core.graphics.flatten
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Composable
fun Tutorial6_1Screen5() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Path Segments",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        StyleableTutorialText(
            text = "Draw segments of paths by flattening paths. Use selections to display " +
                    "**start** or/and **end** of any segment.",
            bullets = false
        )
        DrawPath()
        DrawArcToPath()
        DrawTicketPathWithArc()
        DrawRoundedRectangleWithArc()
        DrawPathProgress()
        DrawPolygonPath()
        DrawPathOperation()
        DrawQuad()
    }
}


@Composable
private fun DrawPath() {

    val path = remember { Path() }

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    Canvas(modifier = canvasModifier) {
        // Since we remember paths from each recomposition we reset them to have fresh ones
        // You can create paths here if you want to have new path instances
        path.reset()

        // Draw line
        path.moveTo(50f, 50f)
        path.lineTo(50f, 80f)
        path.lineTo(50f, 110f)
        path.lineTo(50f, 130f)
        path.lineTo(50f, 150f)
        path.lineTo(50f, 250f)
        path.lineTo(50f, 400f)
        path.lineTo(50f, size.height - 30)

        // Draw Rectangle
        path.moveTo(100f, 100f)
        // Draw a line from top right corner (100, 100) to (100,300)
        path.lineTo(100f, 300f)
        // Draw a line from (100, 300) to (300,300)
        path.lineTo(300f, 300f)
        // Draw a line from (300, 300) to (300,100)
        path.lineTo(300f, 100f)
        // Draw a line from (300, 100) to (100,100)
        path.lineTo(100f, 100f)


        // Add rounded rectangle to path
        path.addRoundRect(
            RoundRect(
                left = 400f,
                top = 200f,
                right = 600f,
                bottom = 400f,
                topLeftCornerRadius = CornerRadius(10f, 10f),
                topRightCornerRadius = CornerRadius(30f, 30f),
                bottomLeftCornerRadius = CornerRadius(50f, 20f),
                bottomRightCornerRadius = CornerRadius(0f, 0f)
            )
        )

        // Add rounded rectangle to path
        path.addRoundRect(
            RoundRect(
                left = 700f,
                top = 200f,
                right = 900f,
                bottom = 400f,
                radiusX = 20f,
                radiusY = 20f
            )
        )

        path.addOval(Rect(left = 400f, top = 50f, right = 500f, bottom = 150f))

        drawPath(
            color = Color.Blue,
            path = path,
            style = Stroke(width = 1.dp.toPx())
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }
    }
}

@Composable
private fun DrawArcToPath() {
    val path = remember { Path() }

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    var startAngle by remember { mutableStateOf(0f) }
    var sweepAngle by remember { mutableStateOf(90f) }

    Canvas(modifier = canvasModifier) {
        // Since we remember paths from each recomposition we reset them to have fresh ones
        // You can create paths here if you want to have new path instances
        path.reset()

        val rect = Rect(0f, 0f, size.width, size.height)
        path.arcTo(
            rect,
            startAngleDegrees = startAngle,
            sweepAngleDegrees = sweepAngle,
            forceMoveTo = false
        )


        drawPath(
            color = Color.Blue,
            path = path,
            style = Stroke(width = 1.dp.toPx())
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }

        Text(text = "StartAngle ${startAngle.roundToInt()}")
        Slider(
            value = startAngle,
            onValueChange = { startAngle = it },
            valueRange = -360f..360f,
        )

        Text(text = "SweepAngle ${sweepAngle.roundToInt()}")
        Slider(
            value = sweepAngle,
            onValueChange = { sweepAngle = it },
            valueRange = -360f..360f,
        )
    }
}

@Composable
private fun DrawTicketPathWithArc() {

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    Canvas(modifier = canvasModifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        // Black background
        val ticketBackgroundWidth = canvasWidth * .8f
        val horizontalSpace = (canvasWidth - ticketBackgroundWidth) / 2

        val ticketBackgroundHeight = canvasHeight * .8f
        val verticalSpace = (canvasHeight - ticketBackgroundHeight) / 2

        // Get ticket path for background
        val path1 = ticketPath(
            topLeft = Offset(horizontalSpace, verticalSpace),
            size = Size(ticketBackgroundWidth, ticketBackgroundHeight),
            cornerRadius = 20.dp.toPx()
        )
        drawPath(path1, color = Color.Black)

        // Dashed path in foreground
        val ticketForegroundWidth = ticketBackgroundWidth * .95f
        val horizontalSpace2 = (canvasWidth - ticketForegroundWidth) / 2

        val ticketForegroundHeight = ticketBackgroundHeight * .9f
        val verticalSpace2 = (canvasHeight - ticketForegroundHeight) / 2

        // Get ticket path for background
        val path2 = ticketPath(
            topLeft = Offset(horizontalSpace2, verticalSpace2),
            size = Size(ticketForegroundWidth, ticketForegroundHeight),
            cornerRadius = 20.dp.toPx()
        )
        drawPath(
            path2,
            color = Color.Red,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(20f, 20f)
                )
            )
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path1.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }
    }
}

@Composable
private fun DrawRoundedRectangleWithArc() {

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    Canvas(modifier = canvasModifier) {

        val path = roundedRectanglePath(
            topLeft = Offset(100f, 100f),
            size = Size(400f, 300f),
            cornerRadius = 20.dp.toPx()
        )

        path.addPath(
            roundedRectanglePath(
                topLeft = Offset(600f, 200f),
                size = Size(200f, 200f),
                cornerRadius = 8.dp.toPx()
            )
        )

        drawPath(path, color = Color.Blue)

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }
    }
}

@Composable
private fun DrawPathProgress() {

    var progressStart by remember { mutableStateOf(0f) }
    var progressEnd by remember { mutableStateOf(100f) }

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    // This is the progress path which wis changed using path measure
    val pathWithProgress by remember {
        mutableStateOf(Path())
    }

    // using path
    val pathMeasure by remember { mutableStateOf(PathMeasure()) }


    Canvas(modifier = canvasModifier) {

        /*
            Draw  function with progress like sinus wave
         */
        val canvasHeight = size.height

        val points = getSinusoidalPoints(size)

        val fullPath = Path()
        fullPath.moveTo(0f, canvasHeight / 2f)
        points.forEach { offset: Offset ->
            fullPath.lineTo(offset.x, offset.y)
        }

        pathWithProgress.reset()

        pathMeasure.setPath(fullPath, forceClosed = false)
        pathMeasure.getSegment(
            startDistance = pathMeasure.length * progressStart / 100f,
            stopDistance = pathMeasure.length * progressEnd / 100f,
            pathWithProgress,
            startWithMoveTo = true
        )

        drawPath(
            color = Color.Blue,
            path = pathWithProgress,
            style = Stroke(
                width = 1.dp.toPx(),
            )
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = pathWithProgress.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }

        Text(text = "Progress Start ${progressStart.roundToInt()}%")
        Slider(
            value = progressStart,
            onValueChange = { progressStart = it },
            valueRange = 0f..100f,
        )

        Text(text = "Progress End ${progressEnd.roundToInt()}%")
        Slider(
            value = progressEnd,
            onValueChange = { progressEnd = it },
            valueRange = 0f..100f,
        )
    }
}

@Composable
private fun DrawPolygonPath() {
    var sides by remember { mutableStateOf(3f) }

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2
        val path = createPolygonPath(cx, cy, sides.roundToInt(), radius)

        drawPath(
            color = Color.Blue,
            path = path,
            style = Stroke(width = 1.dp.toPx())
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }

        Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )
    }
}

@Composable
private fun DrawPathOperation() {

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    var sides1 by remember { mutableStateOf(5f) }
    var radius1 by remember { mutableStateOf(240f) }

    var sides2 by remember { mutableStateOf(7f) }
    var radius2 by remember { mutableStateOf(240f) }

    var operation by remember { mutableStateOf(PathOperation.Difference) }

    val newPath = remember { Path() }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val cx1 = canvasWidth / 3
        val cx2 = canvasWidth * 2 / 3
        val cy = canvasHeight / 2


        val path1 = createPolygonPath(cx1, cy, sides1.roundToInt(), radius1)
        val path2 = createPolygonPath(cx2, cy, sides2.roundToInt(), radius2)

        drawPath(
            color = Color.Red,
            path = path1,
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )
        )

        drawPath(
            color = Color.Blue,
            path = path2,
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )
        )

        // We apply operation to path1 and path2 and setting this new path to our newPath
        /*
            Set this path to the result of applying the Op to the two specified paths.
            The resulting path will be constructed from non-overlapping contours.
            The curve order is reduced where possible so that cubics may be turned into quadratics,
            and quadratics maybe turned into lines.
         */
        newPath.op(path1, path2, operation = operation)

        drawPath(
            color = Color.Green,
            path = newPath,
            style = Stroke(
                width = 2.dp.toPx(),
            )
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = newPath.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }

        ExposedSelectionMenu(title = "Path Operation",
            index = when (operation) {
                PathOperation.Difference -> 0
                PathOperation.Intersect -> 1
                PathOperation.Union -> 2
                PathOperation.Xor -> 3
                else -> 4
            },
            options = listOf("Difference", "Intersect", "Union", "Xor", "ReverseDifference"),
            onSelected = {
                operation = when (it) {
                    0 -> PathOperation.Difference
                    1 -> PathOperation.Intersect
                    2 -> PathOperation.Union
                    3 -> PathOperation.Xor
                    else -> PathOperation.ReverseDifference
                }
            }
        )

        Text(text = "Sides left: ${sides1.roundToInt()}")
        Slider(
            value = sides1,
            onValueChange = { sides1 = it },
            valueRange = 3f..12f,
            steps = 10
        )
        Text(text = "radius left: ${radius1.roundToInt()}")
        Slider(
            value = radius1,
            onValueChange = { radius1 = it },
            valueRange = 100f..300f
        )

        Text(text = "Sides right: ${sides2.roundToInt()}")
        Slider(
            value = sides2,
            onValueChange = { sides2 = it },
            valueRange = 3f..12f,
            steps = 10
        )
        Text(text = "radius right: ${radius2.roundToInt()}")
        Slider(
            value = radius2,
            onValueChange = { radius2 = it },
            valueRange = 100f..300f
        )
    }
}

@Composable
private fun DrawQuad() {

    val density = LocalDensity.current.density
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val screenWidthInPx = screenWidth.value * density

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    // (x0, y0) is initial coordinate where path is moved with path.moveTo(x0,y0)
    var x0 by remember { mutableStateOf(0f) }
    var y0 by remember { mutableStateOf(0f) }

    /*
        Adds a quadratic bezier segment that curves from the current point(x0,y0) to the
        given point (x2, y2), using the control point (x1, y1).
     */
    var x1 by remember { mutableStateOf(0f) }
    var y1 by remember { mutableStateOf(screenWidthInPx) }
    var x2 by remember { mutableStateOf(screenWidthInPx) }
    var y2 by remember { mutableStateOf(screenWidthInPx) }

    val path = remember { Path() }
    Canvas(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .background(Color.White)
            .size(screenWidth, screenWidth)
    ) {
        path.reset()
        path.moveTo(x0, y0)
        path.quadraticBezierTo(x1 = x1, y1 = y1, x2 = x2, y2 = y2)

        drawPath(
            color = Color.Blue,
            path = path,
            style = Stroke(width = 1.dp.toPx())
        )

        // Draw Control Point on screen
        drawPoints(
            listOf(Offset(x1, y1)),
            color = Color.Green,
            pointMode = PointMode.Points,
            cap = StrokeCap.Round,
            strokeWidth = 40f
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Color.Cyan,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {
                    drawCircle(
                        color = Red400,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }

        Text(text = "X0: ${x0.roundToInt()}")
        Slider(
            value = x0,
            onValueChange = { x0 = it },
            valueRange = 0f..screenWidthInPx,
        )

        Text(text = "Y0: ${y0.roundToInt()}")
        Slider(
            value = y0,
            onValueChange = { y0 = it },
            valueRange = 0f..screenWidthInPx,
        )

        Text(text = "X1: ${x1.roundToInt()}")
        Slider(
            value = x1,
            onValueChange = { x1 = it },
            valueRange = 0f..screenWidthInPx,
        )

        Text(text = "Y1: ${y1.roundToInt()}")
        Slider(
            value = y1,
            onValueChange = { y1 = it },
            valueRange = 0f..screenWidthInPx,
        )

        Text(text = "X2: ${x2.roundToInt()}")
        Slider(
            value = x2,
            onValueChange = { x2 = it },
            valueRange = 0f..screenWidthInPx,
        )

        Text(text = "Y2: ${y2.roundToInt()}")
        Slider(
            value = y2,
            onValueChange = { y2 = it },
            valueRange = 0f..screenWidthInPx,
        )
    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxSize()
    .height(200.dp)