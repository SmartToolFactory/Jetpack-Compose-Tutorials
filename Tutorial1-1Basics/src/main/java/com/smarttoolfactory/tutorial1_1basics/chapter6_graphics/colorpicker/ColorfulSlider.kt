package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.gradientColorsReversed
import kotlin.math.roundToInt

@Composable
fun ColorfulSlider(color: Color=Color.Red) {

    BoxWithConstraints(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .height(40.dp)
            .border(2.dp, Color.Green),
        contentAlignment = Alignment.CenterStart
    ) {

        val density = LocalDensity.current.density

        val width = maxWidth
        val height = maxHeight
        val widthInPx = maxWidth * density
        val heightInPx = maxHeight * density

        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        var boxColor by remember { mutableStateOf(Blue400) }


        Canvas(
            modifier = Modifier
                .fillMaxSize()
//            .onSizeChanged { width = it.width.toFloat() }
        ) {

            val canvasWidth = size.width
            val canvasHeight = size.height
            val trackHeight = canvasHeight * .4f
            val spaceHeight = canvasHeight * .3f
            drawLine(
                Brush.linearGradient(gradientColorsReversed),
                start = Offset(trackHeight, center.y),
                end = Offset(canvasWidth - trackHeight, center.y),
                strokeWidth = trackHeight,
                cap = StrokeCap.Round
            )
        }

        Spacer(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .shadow(2.dp, shape = CircleShape)
                .size(20.dp)
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
                                (originalX + dragAmount).coerceIn(
                                    (maxHeight / 4).toPx(),
                                    width.toPx() - 20.dp.toPx()
                                )
                            offsetX.value = newValue
                        }
                    )
                }
        )
    }
}
