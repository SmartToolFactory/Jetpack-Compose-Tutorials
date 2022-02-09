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
    TutorialText2(text = "Line, Bezier")
    DrawPath()
    TutorialText2(text = "Polygon Path and CornerRadius")
    DrawPolygonPath()
    TutorialText2(text = "Polygon Path Progress")
    DrawPolygonPathWithProgress()
}

@Composable
private fun DrawPath() {

    val path = remember {Path()}

    Canvas(modifier = canvasModifier) {

        path.moveTo(100f,100f)
        // Draw a line from top right corner (100, 100) coordinate to (100,200)
        path.lineTo(100f,200f)
        path.moveTo(100f,100f)
        path.relativeLineTo(100f,200f)
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

    val newPath by remember {
        mutableStateOf(Path())
    }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2

        val path = createPolygonPath(cx, cy, sides.roundToInt(), radius)
        newPath.reset()
        if (progress >= 100f) {
            newPath.addPath(path)
        } else {
            pathMeasure.setPath(path, forceClosed = false)
            pathMeasure.getSegment(
                startDistance = 0f,
                stopDistance = pathMeasure.length * progress / 100f,
                newPath,
                startWithMoveTo = true
            )
        }

        drawPath(
            color = Color.Red,
            path = newPath,
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