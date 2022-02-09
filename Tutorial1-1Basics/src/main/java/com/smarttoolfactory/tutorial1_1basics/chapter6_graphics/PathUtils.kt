package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

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
    val path = Path()
    val angle = 2.0 * Math.PI / sides
    path.moveTo(
        cx + (radius * cos(0.0)).toFloat(),
        cy + (radius * sin(0.0)).toFloat()
    )
    for (i in 1 until sides) {
        path.lineTo(
            cx + (radius * cos(angle * i)).toFloat(),
            cy + (radius * sin(angle * i)).toFloat()
        )
    }
    path.close()
    return path
}