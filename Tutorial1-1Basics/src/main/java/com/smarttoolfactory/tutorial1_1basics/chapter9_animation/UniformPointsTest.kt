package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*

/* ---------- Math & generation ---------- */

data class Vec3(val x: Float, val y: Float, val z: Float)

/**
 * Spherical Fibonacci (golden-angle) point set on a unit sphere.
 * Quasi-uniform for any n >= 1, no clustering at the poles.
 *
 * @param n number of points
 * @param radius sphere radius
 */
fun fibonacciSphere(n: Int, radius: Float = 1f): List<Vec3> {
    require(n >= 1) { "n must be >= 1" }
    val goldenAngle = Math.PI * (3.0 - sqrt(5.0)) // ~2.399963...
    return List(n) { i ->
        val k = (i + 0.5) / n
        val y = 1.0 - 2.0 * k                         // (-1, 1)
        val r = sqrt(max(0.0, 1.0 - y * y))          // radius at y
        val theta = goldenAngle * i
        val x = cos(theta) * r
        val z = sin(theta) * r
        Vec3(
            (x * radius).toFloat(),
            (y * radius).toFloat(),
            (z * radius).toFloat()
        )
    }
}

/* ---------- Simple 3D transforms & projection ---------- */

private fun rotateX(v: Vec3, rad: Float): Vec3 {
    val s = sin(rad); val c = cos(rad)
    val y = v.y * c - v.z * s
    val z = v.y * s + v.z * c
    return Vec3(v.x, y, z)
}

private fun rotateY(v: Vec3, rad: Float): Vec3 {
    val s = sin(rad); val c = cos(rad)
    val x = v.x * c + v.z * s
    val z = -v.x * s + v.z * c
    return Vec3(x, v.y, z)
}

/**
 * Very small perspective projection: zForward must keep (z + camDist) > 0.
 * Returns screen offset and depth scale for sizing the dot.
 */
private fun projectTo2D(v: Vec3, width: Float, height: Float, camDist: Float): Triple<Offset, Float, Float> {
    val z = v.z + camDist                       // push scene forward
    val invZ = 1f / z
    val f = camDist * invZ                      // perspective factor
    val scale = f
    val cx = width * 0.5f
    val cy = height * 0.5f
    val px = cx + v.x * f * min(cx, cy) * 0.95f
    val py = cy - v.y * f * min(cx, cy) * 0.95f
    return Triple(Offset(px, py), scale, z)     // z is for optional shading
}

/* ---------- Compose UI ---------- */

@Composable
fun SpherePointsDemo(
    modifier: Modifier = Modifier,
    initialN: Int = 800
) {
    var n by remember { mutableStateOf(initialN.coerceIn(1, 10_000)) }
    var rotX by remember { mutableStateOf(0f) } // radians
    var rotY by remember { mutableStateOf(0f) }
    val camDist = 2.8f                          // > radius(=1) so z' stays positive

    // Recompute points only when n changes
    val points = remember(n) { fibonacciSphere(n, radius = 1f) }

    // A bit of polish: smoothen slider updates
    val rotXAnim by animateFloatAsState(targetValue = rotX, label = "rotX")
    val rotYAnim by animateFloatAsState(targetValue = rotY, label = "rotY")

    Column(
        modifier = modifier
            .background(Color(0xFF0E1013))
            .padding(16.dp)
    ) {
        Text(
            "Evenly Distributed Points on a Sphere ($n)",
            color = Color(0xFFE6EAF2),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        // Use pan to rotate around X/Y (intuitive trackball-ish feel)
                        rotY -= pan.x * 0.005f
                        rotX += pan.y * 0.005f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawSpherePoints(
                    points = points,
                    rotX = rotXAnim,
                    rotY = rotYAnim,
                    camDist = camDist
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Controls: N, rotX, rotY
        Text("Point count", color = Color(0xFFB8C2D6))
        Slider(
            value = n.toFloat(),
            onValueChange = { n = it.roundToInt().coerceIn(1, 5000) },
            valueRange = 50f..5000f,
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF7AA2FF))
        )

        Text("Rotate X", color = Color(0xFFB8C2D6))
        Slider(
            value = rotX,
            onValueChange = { rotX = it },
            valueRange = (-PI).toFloat()..(PI).toFloat(),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF7AA2FF))
        )

        Text("Rotate Y", color = Color(0xFFB8C2D6))
        Slider(
            value = rotY,
            onValueChange = { rotY = it },
            valueRange = (-PI).toFloat()..(PI).toFloat(),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF7AA2FF))
        )

        Spacer(Modifier.height(4.dp))
        Text(
            "Tip: drag on the canvas to rotate, or use sliders.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//private fun DrawScope.drawSpherePoints(
//    points: List<Vec3>,
//    rotX: Float,
//    rotY: Float,
//    camDist: Float,
//) {
//    // Light UI touch: draw a faint sphere outline
//    val r = min(size.width, size.height) * 0.45f
////    drawCircle(
////        color = Color(0xFF1B2333),
////        radius = r,
////        center = Offset(size.width / 2f, size.height / 2f),
////        alpha = 1f
////    )
//
//    // Render points with depth-aware sizing & subtle depth tint
//    val baseDot = max(2f, r * 0.008f)
//    for (p in points) {
//        val pr = rotateY(rotateX(p, rotX), rotY)
//        val (screen, scale, zPrime) = projectTo2D(pr, size.width, size.height, camDist)
//
//        // Depth shading: farther points are dimmer & smaller
//        val depth01 = ((zPrime - (camDist - 1f)) / 2f).toFloat().coerceIn(0f, 1f)
//        val dotRadius = baseDot * (0.6f + 0.6f * scale.coerceIn(0.5f, 1.4f))
//
//        // Cool/warm tint by depth (near = warmer)
//        val cNear = Color.Red // near-ish
//        val cFar = Color.Red.copy(alpha = .4f)  // far-ish
//        val tint = lerpColor(cFar, cNear, 1f - depth01)
//
//        drawCircle(
//            color = tint,
//            radius = dotRadius,
//            center = screen,
//            alpha = 0.95f
//        )
//    }
//}

//private fun DrawScope.drawSpherePoints(
//    points: List<Vec3>,
//    rotX: Float,
//    rotY: Float,
//    camDist: Float,
//) {
//    // Draw faint sphere outline
//    val r = min(size.width, size.height) * 0.45f
////    drawCircle(
////        color = Color(0xFF1B2333),
////        radius = r,
////        center = Offset(size.width / 2f, size.height / 2f),
////        alpha = 1f
////    )
//
//    val baseDot = max(2f, r * 0.008f)
//
//    // Identify poles
//    val topPole = points.maxByOrNull { it.y }   // north
//    val bottomPole = points.minByOrNull { it.y } // south
//
//    for (p in points) {
//        val pr = rotateY(rotateX(p, rotX), rotY)
//        val (screen, scale, zPrime) = projectTo2D(pr, size.width, size.height, camDist)
//        val depth01 = ((zPrime - (camDist - 1f)) / 2f).toFloat().coerceIn(0f, 1f)
//
//        val isNorth = p == topPole
//        val isSouth = p == bottomPole
//
//        val (dotColor, dotRadius) = when {
//            isNorth -> Color(0xFFFF6666) to baseDot * 3f  // red for north
//            isSouth -> Color(0xFF66CCFF) to baseDot * 3f  // blue for south
//            else -> {
//                val cNear = Color(0xFFBEE0FF)
//                val cFar = Color(0xFF5875A9)
//                val tint = lerpColor(cFar, cNear, 1f - depth01)
//                tint to baseDot * (0.6f + 0.6f * scale.coerceIn(0.5f, 1.4f))
//            }
//        }
//
//        drawCircle(
//            color = dotColor,
//            radius = dotRadius,
//            center = screen,
//            alpha = if (isNorth || isSouth) 1f else 0.95f
//        )
//    }
//}

private fun DrawScope.drawSpherePoints(
    points: List<Vec3>,
    rotX: Float,
    rotY: Float,
    camDist: Float,
) {
    val r = min(size.width, size.height) * 0.45f
    val center = Offset(size.width / 2f, size.height / 2f)

    // Sphere outline
    drawCircle(
        color = Color(0xFF1B2333),
        radius = r,
        center = center,
        alpha = 1f
    )

    val baseDot = max(2f, r * 0.008f)

    // Identify poles
    val topPole = points.maxByOrNull { it.y }   // north
    val bottomPole = points.minByOrNull { it.y } // south

    // Rotate poles
    val northRot = topPole?.let { rotateY(rotateX(it, rotX), rotY) }
    val southRot = bottomPole?.let { rotateY(rotateX(it, rotX), rotY) }

    // Draw line connecting poles (axis line)
    if (northRot != null && southRot != null) {
        val (north2D, _, _) = projectTo2D(northRot, size.width, size.height, camDist)
        val (south2D, _, _) = projectTo2D(southRot, size.width, size.height, camDist)
        drawLine(
            color = Color(0xFFCCCCCC),
            start = north2D,
            end = south2D,
            strokeWidth = 3f,
            alpha = 0.7f
        )
    }

    // Draw points
    for (p in points) {
        val pr = rotateY(rotateX(p, rotX), rotY)
        val (screen, scale, zPrime) = projectTo2D(pr, size.width, size.height, camDist)
        val depth01 = ((zPrime - (camDist - 1f)) / 2f).toFloat().coerceIn(0f, 1f)

        val isNorth = p == topPole
        val isSouth = p == bottomPole

        val (dotColor, dotRadius) = when {
            isNorth -> Color(0xFFFF6666) to baseDot * 3f  // red for north
            isSouth -> Color(0xFF66CCFF) to baseDot * 3f  // blue for south
            else -> {
                val cNear = Color(0xFFBEE0FF)
                val cFar = Color(0xFF5875A9)
                val tint = lerpColor(cFar, cNear, 1f - depth01)
                tint to baseDot * (0.6f + 0.6f * scale.coerceIn(0.5f, 1.4f))
            }
        }

        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = screen,
            alpha = if (isNorth || isSouth) 1f else 0.95f
        )
    }
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val clamped = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * clamped,
        green = a.green + (b.green - a.green) * clamped,
        blue = a.blue + (b.blue - a.blue) * clamped,
        alpha = a.alpha + (b.alpha - a.alpha) * clamped
    )
}

/* ---------- Previews ---------- */

@Preview(showBackground = true, backgroundColor = 0xFF0E1013)
@Composable
fun Preview_SpherePointsDemo() {
    MaterialTheme {
        Surface(color = Color(0xFF0E1013)) {
            SpherePointsDemo(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                initialN = 400
            )
        }
    }
}