package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Preview
@Composable
private fun WrapTest() {
    BoxWithConstraints(
        modifier = Modifier
            .border(1.dp, Color.Green)
            .size(200.dp)
            .wrapContentSize()
    ) {
        Text(text = "Constraints", Modifier.border(2.dp, Color.Red))
    }
}

@Preview
@Composable
private fun ColorLerpSample() {
    var color by remember {
        mutableStateOf(Color.Red)
    }

    Column {

    }

    val state = produceState(initialValue = 0,
        producer = {
            delay(1000)
            value = 1
        }
    )

    Column {

        Text(text = "State: ${state.value}")

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color
            ),
            modifier = Modifier.pointerInput(Unit) {
                val width = size.width

                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        val x = change.position.x
                        val progress = (x / width).coerceIn(0f, 1f)
                        color = lerp(Color.Red, Color.Green, progress)

                    },
                    onDragCancel = {
                        color = Color.Red
                    },
                    onDragEnd = {
                        color = Color.Red
                    }
                )
            },
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Drag to change color", color = Color.White)

        }
    }
}

@Preview
@Composable
private fun Test() {

    Column(
        modifier = Modifier
            .border(1.dp, Color.Red)
            .fillMaxWidth()
    ) {
        BoxWithConstraints(
            Modifier
                .border(2.dp, Color.Green)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Constraints: $constraints"
            )
        }
    }
}


@Preview
@Composable
private fun BrushFusionTest() {
    Column(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {


        val brushVertical = Brush.verticalGradient(
            listOf(
                Color.Red.copy(alpha = 0f),
                Color.Red.copy(alpha = 0.4f),
                Color.Red.copy(alpha = 1f),
            )
        )

        val brushVertical2 = Brush.horizontalGradient(
            listOf(
                Color.Green.copy(alpha = 0f),
                Color.Green.copy(alpha = 0.7f),
                Color.Green.copy(alpha = 1f),
            )
        )
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

//            drawRect(brush = brushVertical)
            drawRect(
                brush = brushVertical2,
//                blendMode = BlendMode.Overlay
            )
        }
    }
}