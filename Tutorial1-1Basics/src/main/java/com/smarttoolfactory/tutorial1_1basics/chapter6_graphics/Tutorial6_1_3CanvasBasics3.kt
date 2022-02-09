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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Composable
fun Tutorial6_1Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Draw Path",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawPathExample()
    }
}

@Composable
private fun DrawPathExample() {

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Absolute and Relative positions")
    DrawPath()
    TutorialText2(text = "Draw path with progress")
    DrawPathProgress()
    TutorialText2(text = "Polygon Path and CornerRadius")
    DrawPolygonPath()
    TutorialText2(text = "Polygon Path Progress")
    DrawPolygonPathWithProgress()
}

@Composable
private fun DrawPath() {

    val path1 = remember { Path() }
    val path2 = remember { Path() }

    Canvas(modifier = canvasModifier) {
        // Since we remember paths from each recomposition we reset them to have fresh ones
        // You can create paths here if you want to have new path instances
        path1.reset()
        path2.reset()

        path1.moveTo(100f, 100f)
        // Draw a line from top right corner (100, 100) to (100,300)
        path1.lineTo(100f, 300f)
        // Draw a line from (100, 300) to (300,300)
        path1.lineTo(300f, 300f)
        // Draw a line from (300, 300) to (300,100)
        path1.lineTo(300f, 100f)
        // Draw a line from (300, 100) to (100,100)
        path1.lineTo(100f, 100f)


        // Using relatives to draw blue path, relative is based on previous position of path
        path2.relativeMoveTo(100f, 100f)
        // Draw a line from (100,100) from (100, 300)
        path2.relativeLineTo(0f, 200f)
        // Draw a line from (100, 300) to (300,300)
        path2.relativeLineTo(200f, 0f)
        // Draw a line from (300, 300) to (300,100)
        path2.relativeLineTo(0f, -200f)
        // Draw a line from (300, 100) to (100,100)
        path2.relativeLineTo(-200f, 0f)

        // Add rounded rectangle to path1
        path1.addRoundRect(
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

        // Add rounded rectangle to path2
        path2.addRoundRect(
            RoundRect(
                left = 700f,
                top = 200f,
                right = 900f,
                bottom = 400f,
                radiusX = 20f,
                radiusY = 20f
            )
        )


        path1.addOval(Rect(left = 400f, top = 50f, right = 500f, bottom = 150f))
        path2.addArc(
            Rect(400f, top = 50f, right = 500f, bottom = 150f),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f
        )

        drawPath(
            color = Color.Red,
            path = path1,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        drawPath(
            color = Color.Blue,
            path = path2,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
            )
        )
    }
}

@Composable
private fun DrawPathProgress() {

    var progressStart by remember { mutableStateOf(20f) }
    var progressEnd by remember { mutableStateOf(80f) }


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


        fullPath.moveTo(0f, canvasHeight/2f)
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
//        }

        drawPath(
            color = Color.Red,
            path = fullPath,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )
        )

        drawPath(
            color = Color.Blue,
            path = pathWithProgress,
            style = Stroke(
                width = 2.dp.toPx(),
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

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
    var cornerRadius by remember { mutableStateOf(1f) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2
        val path = createPolygonPath(cx, cy, sides.roundToInt(), radius)

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(cornerRadius)
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )

        Text(text = "CornerRadius ${cornerRadius.roundToInt()}")

        Slider(
            value = cornerRadius,
            onValueChange = { cornerRadius = it },
            valueRange = 0f..50f,
        )
    }
}

/**
 * [PathMeasure.getSegment] returns a new path segment from original path it's set with.
 * Start and stop distances determine which sections are set to new path.
 */
@Composable
private fun DrawPolygonPathWithProgress() {

    var sides by remember { mutableStateOf(3f) }
    var cornerRadius by remember { mutableStateOf(1f) }
    val pathMeasure by remember { mutableStateOf(PathMeasure()) }
    var progress by remember { mutableStateOf(50f) }

    val pathWithProgress by remember {
        mutableStateOf(Path())
    }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2

        val fullPath = createPolygonPath(cx, cy, sides.roundToInt(), radius)
        pathWithProgress.reset()
        if (progress >= 100f) {
            pathWithProgress.addPath(fullPath)
        } else {
            pathMeasure.setPath(fullPath, forceClosed = false)
            pathMeasure.getSegment(
                startDistance = 0f,
                stopDistance = pathMeasure.length * progress / 100f,
                pathWithProgress,
                startWithMoveTo = true
            )
        }

        drawPath(
            color = Color.Red,
            path = pathWithProgress,
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(cornerRadius)
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        Text(text = "Progress ${progress.roundToInt()}%")
        Slider(
            value = progress,
            onValueChange = { progress = it },
            valueRange = 0f..100f,
        )

        Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )

        Text(text = "CornerRadius ${cornerRadius.roundToInt()}")
        Slider(
            value = cornerRadius,
            onValueChange = { cornerRadius = it },
            valueRange = 0f..50f,
        )
    }
}

private val canvasModifier = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(200.dp)