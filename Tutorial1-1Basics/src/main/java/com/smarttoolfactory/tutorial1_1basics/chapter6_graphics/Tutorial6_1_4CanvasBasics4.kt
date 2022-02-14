package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Composable
fun Tutorial6_1Screen4() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Clip Path/Rect",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        TutorialText2(text = "path.op Stroke")
        PathOp1()
        TutorialText2(text = "path.op Fill")
        PathOp2()
        TutorialText2(text = "DrawScope.clipPath")
        ClipPath()
        TutorialText2(text = "DrawScope.clipRect")
        ClipRect()
    }
}


@Composable
private fun PathOp1() {

    var sides1 by remember { mutableStateOf(5f) }
    var radius1 by remember { mutableStateOf(300f) }

    var sides2 by remember { mutableStateOf(7f) }
    var radius2 by remember { mutableStateOf(300f) }

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
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        drawPath(
            color = Color.Blue,
            path = path2,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
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
                width = 4.dp.toPx(),
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {

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
            valueRange = 100f..500f
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
            valueRange = 100f..500f
        )
    }
}

@Composable
private fun PathOp2() {

    var sides1 by remember { mutableStateOf(5f) }
    var radius1 by remember { mutableStateOf(400f) }

    var sides2 by remember { mutableStateOf(7f) }
    var radius2 by remember { mutableStateOf(300f) }

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
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        drawPath(
            color = Color.Blue,
            path = path2,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
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
            path = newPath
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {

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
            valueRange = 100f..500f
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
            valueRange = 100f..500f
        )
    }
}


@Composable
private fun ClipPath() {

    var sides1 by remember { mutableStateOf(5f) }
    var radius1 by remember { mutableStateOf(400f) }

    var sides2 by remember { mutableStateOf(7f) }
    var radius2 by remember { mutableStateOf(300f) }

    var clipOp by remember { mutableStateOf(ClipOp.Difference) }


    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val cx1 = canvasWidth / 3
        val cx2 = canvasWidth * 2 / 3
        val cy = canvasHeight / 2

        val path1 = createPolygonPath(cx1, cy, sides1.roundToInt(), radius1)
        val path2 = createPolygonPath(cx2, cy, sides2.roundToInt(), radius2)


        // Draw path1 to display it as reference, it's for demonstration
        drawPath(
            color = Color.Red,
            path = path1,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f))
            )
        )


        // We apply clipPath operation to pah1 and draw after this operation
        /*
            Reduces the clip region to the intersection of the current clip and the given path.
            This method provides a callback to issue drawing commands within the region defined
            by the clipped path. After this method is invoked, this clip is no longer applied
         */
        clipPath(path = path1, clipOp = clipOp) {

            // Draw path1 to display it as reference, it's for demonstration
            drawPath(
                color = Color.Green,
                path = path1,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 40f))
                )
            )


            // Anything inside this scope will be clipped according to path1 shape
            drawRect(
                color = Color.Yellow,
                topLeft = Offset(100f, 100f),
                size = Size(canvasWidth - 300f, canvasHeight - 300f)
            )

            drawPath(
                color = Color.Blue,
                path = path2
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Red, Color.Green, Color.Magenta, Color.Cyan, Color.Yellow)
                ),
                radius = 200f
            )

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 10f
            )

        }


    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {

        ExposedSelectionMenu(title = "Clip Operation",
            index = when (clipOp) {
                ClipOp.Difference -> 0

                else -> 1
            },
            options = listOf("Difference", "Intersect"),
            onSelected = {
                clipOp = when (it) {
                    0 -> ClipOp.Difference
                    else -> ClipOp.Intersect
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
            valueRange = 100f..500f
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
            valueRange = 100f..500f
        )
    }
}


@Composable
private fun ClipRect() {

    var clipOp by remember { mutableStateOf(ClipOp.Difference) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height


        drawRect(
            color = Color.Red,
            topLeft = Offset(100f, 80f),
            size = Size(600f, 320f),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        /*
            Reduces the clip region to the intersection of the current clip and the
            given rectangle indicated by the given left, top, right and bottom bounds.
            This provides a callback to issue drawing commands within the clipped region.
            After this method is invoked, this clip is no longer applied.
         */
        clipRect(left = 100f, top = 80f, right = 700f, bottom = 400f, clipOp = clipOp) {

            drawCircle(
                center = Offset(canvasWidth / 2 + 100, +canvasHeight / 2 + 50),
                brush = Brush.sweepGradient(
                    center = Offset(canvasWidth / 2 + 100, +canvasHeight / 2 + 50),
                    colors = listOf(Color.Red, Color.Green, Color.Magenta, Color.Cyan, Color.Yellow)
                ),
                radius = 300f
            )

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 10f
            )
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {

        ExposedSelectionMenu(title = "Clip Operation",
            index = when (clipOp) {
                ClipOp.Difference -> 0

                else -> 1
            },
            options = listOf("Difference", "Intersect"),
            onSelected = {
                clipOp = when (it) {
                    0 -> ClipOp.Difference
                    else -> ClipOp.Intersect
                }
            }
        )
    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxSize()
    .clipToBounds()
    .height(300.dp)