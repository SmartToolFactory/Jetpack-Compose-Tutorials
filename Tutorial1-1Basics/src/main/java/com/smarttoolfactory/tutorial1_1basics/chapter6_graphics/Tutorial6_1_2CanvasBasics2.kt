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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippled
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt


@Composable
fun Tutorial6_1Screen2() {
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
            "Draw Arc",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DrawArcExamples()


    }

}

@Composable
private fun DrawArcExamples() {

    DrawArc()
    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Negative Angles")

    DrawNegativeArc()

    Spacer(modifier = Modifier.height(10.dp))
    TutorialText2(text = "Multiple Arcs")
    DrawMultipleArcs()
}

@Composable
private fun DrawArc() {
    var startAngle by remember { mutableStateOf(0f) }
    var sweepAngle by remember { mutableStateOf(60f) }
    var useCenter by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "StartAngle ${startAngle.roundToInt()}")
        Slider(
            value = startAngle,
            onValueChange = { startAngle = it },
            valueRange = 0f..360f,
        )

        Text(text = "SweepAngle ${sweepAngle.roundToInt()}")
        Slider(
            value = sweepAngle,
            onValueChange = { sweepAngle = it },
            valueRange = 0f..360f,
        )

        CheckBoxWithTextRippled(label = "useCenter", useCenter) {
            useCenter = it
        }
    }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawArc(
            color = Red400,
            startAngle,
            sweepAngle,
            useCenter,
            topLeft = Offset((canvasWidth - canvasHeight) / 2, 0f),
            size = Size(canvasHeight, canvasHeight)
        )
    }
}

@Composable
private fun DrawNegativeArc() {
    var startAngle2 by remember { mutableStateOf(0f) }
    var sweepAngle2 by remember { mutableStateOf(60f) }
    var useCenter2 by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "StartAngle ${startAngle2.roundToInt()}")
        Slider(
            value = startAngle2,
            onValueChange = { startAngle2 = it },
            valueRange = -180f..180f,
        )

        Text(text = "SweepAngle ${sweepAngle2.roundToInt()}")
        Slider(
            value = sweepAngle2,
            onValueChange = { sweepAngle2 = it },
            valueRange = -180f..180f,
        )

        CheckBoxWithTextRippled(label = "useCenter", useCenter2) {
            useCenter2 = it
        }
    }


    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawArc(
            color = Red400,
            startAngle2,
            sweepAngle2,
            useCenter2,
            topLeft = Offset((canvasWidth - canvasHeight) / 2, 0f),
            size = Size(canvasHeight, canvasHeight)
        )
    }
}

@Composable
private fun DrawMultipleArcs() {
    var startAngleBlue by remember { mutableStateOf(0f) }
    var sweepAngleBlue by remember { mutableStateOf(120f) }
    var useCenterBlue by remember { mutableStateOf(false) }

    var startAngleRed by remember { mutableStateOf(120f) }
    var sweepAngleRed by remember { mutableStateOf(120f) }
    var useCenterRed by remember { mutableStateOf(false) }

    var startAngleGreen by remember { mutableStateOf(240f) }
    var sweepAngleGreen by remember { mutableStateOf(120f) }
    var useCenterGreen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "StartAngle ${startAngleBlue.roundToInt()}", color = Blue400)
        Slider(
            value = startAngleBlue,
            onValueChange = { startAngleBlue = it },
            valueRange = 0f..360f,
        )

        Text(text = "SweepAngle ${sweepAngleBlue.roundToInt()}", color = Blue400)
        Slider(
            value = sweepAngleBlue,
            onValueChange = { sweepAngleBlue = it },
            valueRange = 0f..360f,
        )

//        CheckBoxWithTextRippled(label = "useCenter", useCenterBlue) {
//            useCenterBlue = it
//        }
    }


    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "StartAngle ${startAngleRed.roundToInt()}", color = Red400)
        Slider(
            value = startAngleRed,
            onValueChange = { startAngleRed = it },
            valueRange = 0f..360f,
        )

        Text(text = "SweepAngle ${sweepAngleRed.roundToInt()}", color = Red400)
        Slider(
            value = sweepAngleRed,
            onValueChange = { sweepAngleRed = it },
            valueRange = 0f..360f,
        )

//        CheckBoxWithTextRippled(label = "useCenter", useCenterRed) {
//            useCenterRed = it
//        }
    }


    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "StartAngle ${startAngleGreen.roundToInt()}", color = Green400)
        Slider(
            value = startAngleGreen,
            onValueChange = { startAngleGreen = it },
            valueRange = 0f..360f,
        )

        Text(text = "SweepAngle ${sweepAngleGreen.roundToInt()}", color = Green400)
        Slider(
            value = sweepAngleGreen,
            onValueChange = { sweepAngleGreen = it },
            valueRange = 0f..360f,
        )

//        CheckBoxWithTextRippled(label = "useCenter", useCenterGreen) {
//            useCenterGreen = it
//        }
    }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val arcHeight = canvasHeight - 20.dp.toPx()
        val arcStrokeWidth = 10.dp.toPx()

        drawArc(
            color = Blue400,
            startAngleBlue,
            sweepAngleBlue,
            useCenterBlue,
            topLeft = Offset(
                (canvasWidth - canvasHeight) / 2,
                (canvasHeight - arcHeight) / 2
            ),
            size = Size(arcHeight, arcHeight),
            style = Stroke(
                arcStrokeWidth
            )
        )

        drawArc(
            color = Red400,
            startAngleRed,
            sweepAngleRed,
            useCenterRed,
            topLeft = Offset(
                (canvasWidth - canvasHeight) / 2,
                (canvasHeight - arcHeight) / 2
            ),
            size = Size(arcHeight, arcHeight),
            style = Stroke(arcStrokeWidth)
        )

        drawArc(
            color = Green400,
            startAngleGreen,
            sweepAngleGreen,
            useCenterGreen,
            topLeft = Offset(
                (canvasWidth - canvasHeight) / 2,
                (canvasHeight - arcHeight) / 2
            ),
            size = Size(arcHeight, arcHeight),
            style = Stroke(arcStrokeWidth)
        )
    }
}

private val canvasModifier = Modifier
    .background(Color.LightGray)
    .fillMaxSize()
    .height(200.dp)

