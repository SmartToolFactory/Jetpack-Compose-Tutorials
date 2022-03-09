package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import kotlin.math.roundToInt

// FIXME Figure how to create HSL gradient instead of drawing each point which has long loading time
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

    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density

        /**
         * Width and height of the rhombus is geometrically equal so it's sufficient to
         * use either width or height to have a length parameter
         */
        val length = maxWidth.value * density

        /**
         * This is list of points with changing [saturation] up while going from left to right
         * and [lightness] up while going from down to up.
         *
         */
        val colorPoints: MutableList<ColorPoint> = remember {
            getPointsInRhombus(length)
        }

        /**
         * Circle selector radius for setting [saturation] and [lightness] by gesture
         */
        val selectorRadius =
            if (selectionRadius > 0.dp) selectionRadius.value * density else length * .04f

        /**
         *  Current position is initially set by [saturation] and [lightness] that is bound
         *  in rhombus since (1,1) points to bottom left corner of a rectangle but it's bounded
         *  in rhombus by [setSelectorPositionFromColorParams].
         *  When user touches anywhere in rhombus current position is updaed and
         *  this composable is recomposed
         */
        var currentPosition by remember(saturation, lightness) {
            mutableStateOf(
                setSelectorPositionFromColorParams(saturation, lightness, length)
            )
        }

        /**
         * Check if first pointer that touched this compsable inside bounds of rhombus
         */
        var isTouched by remember { mutableStateOf(false) }

        val canvasModifier = Modifier
            .size(maxWidth)
            .pointerMotionEvents(
                onDown = {
                    val position = it.position

                    val posX = position.x
                    val posY = position.y

                    // Horizontal range for keeping x position in rhombus bounds
                    val range = getBoundsInLength(length, posY)
                    val start = range.start - selectorRadius
                    val end = range.endInclusive + selectorRadius

                    isTouched = posX in start..end

                    println("🔥 onDown()\n" +
                            " posX: $posX, posY: $posY\n" +
                            "range: $range\n" +
                            "start: $start, end: $end, isTouched: $isTouched")

                    if (isTouched) {

                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / length).coerceIn(0f, 1f)

                        // Send x position as saturation and reverse of y position as lightness
                        // lightness increases while going up but android drawing system is opposite
                        onChange(posXInPercent, 1 - posYInPercent)
                        currentPosition = Offset(posX, posY)
                    }
                    it.consumeDownChange()
                },
                onMove = {
                    if (isTouched) {

                        val position = it.position
                        val posX = position.x.coerceIn(0f, length)
                        val posY = position.y.coerceIn(0f, length)

                        // Horizontal range for keeping x position in rhombus bounds
                        val range = getBoundsInLength(length, posY)


                        val posXInPercent = (posX / length).coerceIn(0f, 1f)
                        val posYInPercent = (posY / length).coerceIn(0f, 1f)

                        // Send x position as saturation and reverse of y position as lightness
                        // lightness increases while going up but android drawing system is opposite
                        onChange(posXInPercent, 1 - posYInPercent)

                        currentPosition = Offset(posX.coerceIn(range), posY)
                    }
                    it.consumePositionChange()
                },
                onUp = {
                    isTouched = false
                    it.consumeDownChange()
                }
            )

        val rhombusPath = remember { rhombusPath(Size(length, length)) }
        Canvas(modifier = canvasModifier) {

//            drawPath(rhombusPath, Color.hsl(hue, saturation = 1f, lightness = 0.5f))

            // TODO Draw gradient instead of points, or maybe smaller rhombuses with s and l
            colorPoints.forEach { colorPoint: ColorPoint ->
                drawCircle(
                    Color.hsl(hue, colorPoint.saturation, colorPoint.lightness),
                    center = colorPoint.point,
                    radius = 5f
                )
            }

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
 *
 * @param saturation of the current color from Composable parameters
 * @param lightness of the current color from Composable parameters
 * @param length of the rhombus
 */
private fun setSelectorPositionFromColorParams(
    saturation: Float,
    lightness: Float,
    length: Float
): Offset {
    // Get possible horizontal range for the current position of lightness on rhombus
    val range = getBoundsInLength(length, lightness * length)
    // Since lightness must increase while going up we need to reverse position
    val verticalPositionOnRhombus = (1 - lightness) * length
    // limit saturation bounds to range to not overflow from rhombus
    val horizontalPositionOnRhombus = (saturation * length).coerceIn(range)
    return Offset(horizontalPositionOnRhombus, verticalPositionOnRhombus)
}

/**
 * Get range that this position can be. This is for limiting touch position inside rhombus.
 * For instance if y position is 10, then x should either be center - 10 or center + 10 to maintain
 * triangular bounds in both axes.
 *
 * @param length of the rhombus
 * @param position current position in x,y coordinates in rhombus
 */
fun getBoundsInLength(
    length: Float,
    position: Float
): ClosedFloatingPointRange<Float> {

    val center = length / 2
    // If it's at top half length in y axis is the same as left and right part in x axis
    return if (position <= center) {
        (center - position)..(center + position)
    } else {
        // If vertical position is below center we just need to use length between bottom and
        // current position to get horizontal range
        val heightAfterCenter = length - position
        (center - heightAfterCenter)..(center + heightAfterCenter)
    }
}

/**
 * Returns range with integers instead of floats
 * @param length of the rhombus
 * @param position current position in x,y coordinates in rhombus
 */
fun getIntRangeInLength(
    length: Float,
    position: Float
): IntRange {

    val center = length / 2
    // If it's at top half length in y axis is the same as left and right part in x axis
    return if (position <= center) {
        (center - position).roundToInt()..(center + position).roundToInt()
    } else {
        // If vertical position is below center we just need to use length between bottom and
        // current position to get horizontal range
        val heightAfterCenter = length - position
        (center - heightAfterCenter).roundToInt()..(center + heightAfterCenter).roundToInt()
    }
}

// TODO this is temporary until i find a more performant and better
//  looking like gradient or smaller rhombus paths
/**
 * Get each point and saturation and lightness of the point. This function is for
 * creating points to draw like gradient effect for HSL color
 */
fun getPointsInRhombus(length: Float): MutableList<ColorPoint> {

    val step = length.toInt() / 100
    val colorPints = mutableListOf<ColorPoint>()

    for (yPos in 0..length.toInt() step step) {
        val range = getIntRangeInLength(length = length, yPos.toFloat())
        for (xPos in range step step) {

            val saturation = xPos / length
            val lightness = 1 - (yPos / length)
            val colorPoint =
                ColorPoint(Offset(xPos.toFloat(), yPos.toFloat()), saturation, lightness)
            colorPints.add(colorPoint)
        }
    }
    return colorPints
}

/**
 * Rhombus path as below with equal length and width
 * ```
 *      / \
 *     /   \
 *    /     \
 *    \     /
 *     \   /
 *      \ /
 * ```
 */
fun rhombusPath(size: Size) = Path().apply {
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}

val rhombusShape = GenericShape { size: Size, _: LayoutDirection ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}

data class ColorPoint(val point: Offset, val saturation: Float, val lightness: Float)

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun SaturationRhombusPreview() {
    ComposeTutorialsTheme {

        val hue by remember { mutableStateOf(0f) }
        var saturation by remember { mutableStateOf(0.5f) }
        var lightness by remember { mutableStateOf(0.5f) }

        SaturationRhombus(
            modifier = Modifier.size(200.dp),
            hue = hue,
            saturation = saturation,
            lightness = lightness
        ) { s, l ->
            println("CHANGING sat: $s, lightness: $l")
            saturation = s
            lightness = l
        }
    }
}