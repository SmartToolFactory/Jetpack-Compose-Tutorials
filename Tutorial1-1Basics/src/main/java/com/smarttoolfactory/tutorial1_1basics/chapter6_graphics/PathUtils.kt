package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin


/**
 * Creates a polygon with number of [sides] centered at ([cx],[cy]) with [radius].
 * ```
 *  To generate regular polygons (i.e. where each interior angle is the same),
 *  polar coordinates are extremely useful. You can calculate the angle necessary
 *  to produce the desired number of sides (as the interior angles total 360º)
 *  and then use multiples of this angle with the same radius to describe each point.
 * val x = radius * Math.cos(angle);
 * val y = radius * Math.sin(angle);
 *
 * For instance to draw triangle loop thrice with angle
 * 0, 120, 240 degrees in radians and draw lines from each coordinate.
 * ```
 */
fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {
    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}

fun roundedRectanglePath(size: Size, cornerRadius: Float): Path {
    return Path().apply {
        reset()

        // Top left arc
        val radius = cornerRadius * 2
        arcTo(
            rect = Rect(
                left = 0f,
                top = 0f,
                right = radius,
                bottom = radius
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )

        lineTo(x = size.width - radius, y = 0f)

        // Top right arc
        arcTo(
            rect = Rect(
                left = size.width - radius,
                top = 0f,
                right = size.width,
                bottom = radius
            ),
            startAngleDegrees = -90.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )

        lineTo(x = size.width, y = size.height - radius)

        // Bottom right arc
        arcTo(
            rect = Rect(
                left = size.width - radius,
                top = size.height - radius,
                right = size.width ,
                bottom = size.height
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )

        lineTo(x = radius, y = size.height)

        // Bottom left arc
        arcTo(
            rect = Rect(
                left = 0f,
                top = size.height - radius,
                right = radius,
                bottom = size.height
            ),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )

        lineTo(x = 0f, y = radius)
        close()
    }
}

/**
 *
 *
 */
fun ticketPath(size: Size, cornerRadius: Float): Path {
    return Path().apply {
        reset()
        // Top left arc
        arcTo(
            rect = Rect(
                left = -cornerRadius,
                top = -cornerRadius,
                right = cornerRadius,
                bottom = cornerRadius
            ),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width - cornerRadius, y = 0f)
        // Top right arc
        arcTo(
            rect = Rect(
                left = size.width - cornerRadius,
                top = -cornerRadius,
                right = size.width + cornerRadius,
                bottom = cornerRadius
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width, y = size.height - cornerRadius)
        // Bottom right arc
        arcTo(
            rect = Rect(
                left = size.width - cornerRadius,
                top = size.height - cornerRadius,
                right = size.width + cornerRadius,
                bottom = size.height + cornerRadius
            ),
            startAngleDegrees = 270.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = cornerRadius, y = size.height)
        // Bottom left arc
        arcTo(
            rect = Rect(
                left = -cornerRadius,
                top = size.height - cornerRadius,
                right = cornerRadius,
                bottom = size.height + cornerRadius
            ),
            startAngleDegrees = 0.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = 0f, y = cornerRadius)
        close()
    }
}