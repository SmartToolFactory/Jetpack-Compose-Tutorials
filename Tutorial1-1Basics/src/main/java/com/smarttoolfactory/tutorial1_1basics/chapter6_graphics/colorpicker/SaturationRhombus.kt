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

/**
 * This is a [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV)
 * saturation and lightness selector in shape of [Rhombus](https://en.wikipedia.org/wiki/Rhombus)
 *
 *  * Since this is not a rectangle, initially current position of selector is
 *  set by [setSelectorPositionFromColorParams] which limits position of saturation to a range
 *  determined by 1-length of dimension since sum of x and y position should be equal to
 *  **length of  rhombus**.
 *
 *  * On touch and selection process same principle applies to bound positions.
 *
 *  * Since **lightness* should be on top but position system of **Canvas** starts from top-left
 *  corner(0,0) we need to reverse **lightness**.
 *
 */
@Composable
fun SaturationRhombus(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float = 0.5f,
    lightness: Float = 0.5f,
    selectionRadius: Dp = (-1).dp,
    onChange: (Float, Float) -> Unit
) {

    BoxWithConstraints(modifier.background(Color.LightGray)) {

        val density = LocalDensity.current.density

        val length = maxWidth.value * density
        val heightInPx = maxHeight.value * density

        val selectorRadius =
            if (selectionRadius > 0.dp) selectionRadius.value * density else length * .04f


        var currentPosition by remember(saturation, lightness) {
            mutableStateOf(
                setSelectorPositionFromColorParams(saturation, lightness, length)
            )
        }

        var isTouched by remember { mutableStateOf(false) }

        val canvasModifier = Modifier
            .size(maxWidth)
            .pointerMotionEvents(
                onDown = {
                    val position = it.position

                    val posX = position.x
                    val posY = position.y

                    // Horizontal
                    val range = getHorizontalBoundForY(length, length, posY)
                    val range2 = getBoundsInLength(length, posY)

                    isTouched = range2.contains(posX)

                    if (isTouched) {

                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / length).coerceIn(0f, 1f)

                        onChange(posXInPercent, 1 - posYInPercent)
                        currentPosition = Offset(posX, posY)

                        println(
                            "🔥 onDown() Position: $position\n" +
                                    "range: $range\n" +
                                    "range2: $range2\n" +
                                    "isTouched: $isTouched\n" +
                                    "posXInPercent: $posXInPercent, posYInPercent: $posYInPercent\n" +
                                    "currentPosition: $currentPosition"
                        )
                    }
                },
                onMove = {
                    if (isTouched) {

                        val position = it.position
                        val posX = position.x.coerceIn(0f, length)
                        val posY = position.y.coerceIn(0f, length)

                        val range = getHorizontalBoundForY(length, length, posY)
                        val range2 = getBoundsInLength(length, posY)


                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / heightInPx).coerceIn(0f, 1f)

                        onChange(posXInPercent, 1 - posYInPercent)

                        currentPosition = Offset(posX.coerceIn(range2), posY)

                        println(
                            "🍏 onMove()\n" +
                                    "range: $range\n" +
                                    "range2: $range2\n" +
                                    "isTouched: $isTouched\n" +
                                    "posXInPercent: $posXInPercent, posYInPercent: $posYInPercent\n" +
                                    "currentPosition: $currentPosition"
                        )
                    }
                },
                onUp = {
                    isTouched = false
                }
            )

        val rhombusPath = remember { rhombusPath(Size(length, length)) }
        Canvas(modifier = canvasModifier) {

//            println("🚌 CANVAS height: ${size.height}, heightInPx: $heightInPx, currentPosition: $currentPosition")
            drawPath(rhombusPath, Color.hsl(hue, saturation = 1f, lightness = 0.5f))

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

/**
 * This is for setting initial position of selector when saturation and lightness is set by
 * an external Composable. Without setting a bound
 * `saturation=1f and lightness
 */
private fun setSelectorPositionFromColorParams(
    saturation: Float,
    lightness: Float,
    length: Float
): Offset {
    val range = getBoundsInLength(length, lightness*length)

    val verticalPositionOnRhombus = (1-lightness) *length
    val horizontalPositionOnRhombus = (saturation *length).coerceIn(range)
    println("😍 saturation: $saturation, lightness: $lightness, range: $range")
    return Offset(horizontalPositionOnRhombus, verticalPositionOnRhombus)
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

/**
 * Get range that this position can be. This is for limiting touch position inside rhombus.
 * For instance if y position is 10, then x should either be center - 10 or center + 10 to maintain
 * triangular bounds in both axes.
 */
fun getBoundsInLength(
    length: Float,
    position: Float
): ClosedFloatingPointRange<Float> {

    val center = length / 2

    return if (position <= center) {
        (center - position)..(center + position)
    } else {
        val heightAfterCenter = length - position
        (center - heightAfterCenter)..(center + heightAfterCenter)
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