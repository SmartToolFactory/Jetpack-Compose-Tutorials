package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.gradientColors
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Composable
 fun ColorPickerWheel(modifier:Modifier =Modifier) {

    // Check if user touches between the valid area of circle
    var isTouched by remember { mutableStateOf(false) }

    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val colorHSV = Color.hsv(120f, 0.5f, 0.5f)

    // Angle from center is required to get Hue which is between 0-360
    var angle by remember { mutableStateOf(0) }
    // Center position of color picker
    var center by remember { mutableStateOf(Offset.Zero) }

    var radiusOuter by remember { mutableStateOf(0f) }
    var radiusInner by remember { mutableStateOf(0f) }

  val colorPickerModifier =  modifier
        .clipToBounds()
        .pointerMotionEvents(
            onDown = {
                val position = it.position
                val distance = calculateDistanceFromCenter(center, position)

                isTouched = (distance in radiusInner..radiusOuter)
                if (isTouched) {
                    angle = calculateAngleFomLocalCoordinates(center, position)
                }
            },
            onMove = {
                if (isTouched) {
                    val position = it.position
                    currentPosition = position
                    angle = calculateAngleFomLocalCoordinates(center, position)
                }
            },
            onUp = {

                isTouched = false
            },
            delayAfterDownInMillis = 20
        )


    Canvas(modifier = colorPickerModifier) {

        val canvasWidth = this.size.width
        val canvasHeight = this.size.height

        val cX = canvasWidth / 2
        val cY = canvasHeight / 2
        val canvasRadius = canvasWidth.coerceAtMost(canvasHeight) / 2f
        center = Offset(cX, cY)

        radiusOuter = canvasRadius * .9f
        radiusInner = canvasRadius * .6f

        val strokeWidth = (radiusOuter - radiusInner)
        val selectorRadius = strokeWidth / 4

        drawCircle(
            brush = Brush.sweepGradient(colors = gradientColors, center = center),
            radius = radiusInner + strokeWidth / 2,

            style = Stroke(
                width = strokeWidth
            )
        )

        val color = if (isTouched) Color.Red else Color.Black
        drawCircle(color, radiusInner - 10f, style = Stroke(width = 20f))
        drawCircle(color, radiusOuter + 10f, style = Stroke(width = 20f))

        // Draw selection circle
        withTransform(
            {
                rotate(degrees = -angle.toFloat())
            }
        ) {
            drawCircle(
                Color.White,
                radius = selectorRadius,
                center = Offset(center.x + radiusInner + strokeWidth / 2f, center.y),
                style = Stroke(width = selectorRadius / 4)
            )
        }
    }
}

private fun calculateDistanceFromCenter(center: Offset, position: Offset): Float {
    val dy = center.y - position.y
    val dx = position.x - center.x
    return sqrt(dx * dx + dy * dy)
}


/**
 * Get angle between 0 and 360 degrees from local coordinate system of a composable
 * Local coordinates of touch are equal to Composable position when in bounds, when
 * touch position is above this composable it returns minus in y axis.
 */
private fun calculateAngleFomLocalCoordinates(center: Offset, position: Offset): Int {
    if (center == Offset.Unspecified || position == Offset.Unspecified) return -1

    val dy = center.y - position.y
    val dx = position.x - center.x
    return ((360 + ((atan2(dy, dx) * 180 / PI))) % 360).roundToInt()

}

/**
 * Get angle between 0 and 360 degrees using coordinates of the root composable. Root composable
 * needs to cover whole screen to return correct results.
 */
private fun calculateAngleFromRootCoordinates(center: Offset, position: Offset): Int {
    if (center == Offset.Unspecified || position == Offset.Unspecified) return -1

    val dy = (position.y - center.y)
    val dx = (center.x - position.x)
    return ((360 + ((atan2(dy, dx) * 180 / PI))) % 360).roundToInt()
}
