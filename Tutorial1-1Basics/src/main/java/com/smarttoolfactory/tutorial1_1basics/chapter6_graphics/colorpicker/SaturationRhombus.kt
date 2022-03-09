package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400

@Composable
fun SaturationRhombus(
    modifier: Modifier = Modifier,
    color: Color,
    saturation: Float = 0.5f,
    lightness: Float = 0.5f,
    selectionRadius: Dp = (-1).dp,
    onChange: (Float, Float) -> Unit
) {

    BoxWithConstraints(modifier.background(Color.LightGray)) {

        var boxColor by remember { mutableStateOf(Blue400) }

        val density = LocalDensity.current.density

        val length = maxWidth.value * density
        val heightInPx = maxHeight.value * density
        val center = length / 2


        val selectorRadius =
            if (selectionRadius > 0.dp) selectionRadius.value * density else length * .04f


        var saturationChange by remember { mutableStateOf(saturation) }
        var lightnessChange by remember { mutableStateOf(lightness) }

        var currentPosition by remember {
               mutableStateOf(
                   Offset(
                       length * saturation,
                       length * lightness
                   )

               )
        }

//        val newOffset = Offset(
//            length * saturation,
//            length * lightness
//        )
//        currentPosition =  newOffset

        println(
            "😆 SaturationRhombus() " +
                    "saturation: $saturation, lightness: $lightness\n" +
                    "saturationChange: $saturationChange, lightnessChange: $lightnessChange\n" +
//                    "newOffset: $newOffset\n" +
                    "currentPosition: $currentPosition\n"
//                    "minWidth: $minWidth, maxWidth: $maxWidth}\n" +
//                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
//                    "widthInPx: $length, heightInPx: $heightInPx"
        )


        var isTouched by remember { mutableStateOf(false) }

        val canvasModifier = Modifier
            .size(maxWidth)
            .pointerMotionEvents(
                onDown = {
                    boxColor = Orange400
                    val position = it.position

                    val posX = position.x
                    val posY = position.y

                    // Horizontal
                    val range = getHorizontalBoundForY(length, length, posY)

                    isTouched = range.contains(posX)

                    if (isTouched) {

                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / length).coerceIn(0f, 1f)

                        onChange(posXInPercent, posYInPercent)
                        currentPosition = Offset(posX, posY)

                        println(
                            "🔥 onDown() Position: $position\n" +
                                    "range: $range, isTouched: $isTouched\n" +
                                    "posXInPercent: $posXInPercent, posYInPercent: $posYInPercent\n" +
                                    "currentPosition: $currentPosition"
                        )
                    }
                },
                onMove = {
                    if (isTouched) {
                        boxColor = Blue400

                        val position = it.position
                        val posX = position.x.coerceIn(0f, length)
                        val posY = position.y.coerceIn(0f, length)

                        val range = getHorizontalBoundForY(length, length, posY)

                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / heightInPx).coerceIn(0f, 1f)

                        onChange(posXInPercent, posYInPercent)

                        currentPosition = Offset(posX.coerceIn(range), posY)

                        println(
                            "🍏 onMove() " +
                                    "posXInPercent: $posXInPercent, posYInPercent: $posYInPercent, " +
                                    "currentPosition: $currentPosition"
                        )
                    }
                },
                onUp = {
                    boxColor = Green400
                    isTouched = false
                }
            )

        val rhombusPath = remember { rhombusPath(Size(length, length)) }
        Canvas(modifier = canvasModifier) {

//            println("🚌 CANVAS height: ${size.height}, heightInPx: $heightInPx, currentPosition: $currentPosition")
            drawPath(rhombusPath, color)

            // Saturation and Value or Lightness selector
            drawCircle(
                Color.White,
                radius = selectorRadius,
                center = currentPosition,
                style = Stroke(width = selectorRadius / 2)
            )
        }
    }
}

fun getHorizontalBoundForY(
    width: Float,
    height: Float,
    y: Float
): ClosedFloatingPointRange<Float> {

    val horizontalCenter = width / 2
    val verticalCenter = height / 2

    return if (y <= verticalCenter) {
        (horizontalCenter - y)..(horizontalCenter + y)
    } else {
        val heightAfterCenter = height - y
        (horizontalCenter - heightAfterCenter)..(horizontalCenter + heightAfterCenter)
    }
}

fun getBoundsInLength(
    length: Float,
    position: Float
): ClosedFloatingPointRange<Float> {
    return if (position <= length) {
        (length - position)..(length + position)
    } else {
        val heightAfterCenter = length - position
        (length - heightAfterCenter)..(length + heightAfterCenter)
    }
}


val rhombusShape = GenericShape { size: Size, _: LayoutDirection ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}

fun rhombusPath(size: Size) = Path().apply {
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}