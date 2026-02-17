package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.sin

@Composable
fun MetaballGooeyWobbleSample(
    modifier: Modifier = Modifier,
    fieldRadiusPx: Float,
    isoThreshold: Float,
    gridStepPx: Float,

    wobbleMaxPx: Float,
    wobbleSpeedScale: Float,
    wobbleDecayPerSecond: Float,
    wobbleFrequency: Float,

    strokeWidthPx: Float = 4f,
    debugDrawCenters: Boolean = true,
    onDebugText: (String) -> Unit = {}
) {
    var pointerPosition by remember { mutableStateOf(Offset.Unspecified) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val staticCenter = remember(canvasSize) { Offset(canvasSize.width / 2f, canvasSize.height / 2f) }
    val dynamicCenter = if (pointerPosition == Offset.Unspecified) staticCenter else pointerPosition

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = strokeWidthPx
            isAntiAlias = true
        }
    }
    var isShaderInitialized by remember { mutableStateOf(false) }

    // --- Motion energy -> wobble amplitude ---
    var wobbleAmplitudePx by remember { mutableFloatStateOf(0f) }
    var phaseSeconds by remember { mutableFloatStateOf(0f) }

    // Track velocity cheaply on main thread (no allocations, no heavy math).
    LaunchedEffect(dynamicCenter, wobbleMaxPx, wobbleSpeedScale, wobbleDecayPerSecond) {
        // We still want continuous decay even when pointer stops updating.
        // Use a frame loop for stable decay.
        var lastTimeNanos = 0L
        var lastPos = dynamicCenter

        while (true) {
            withFrameNanos { now ->
                if (lastTimeNanos == 0L) {
                    lastTimeNanos = now
                    lastPos = dynamicCenter
                    return@withFrameNanos
                }

                val dt = ((now - lastTimeNanos).coerceAtLeast(1L)) / 1_000_000_000f
                lastTimeNanos = now
                phaseSeconds += dt

                val dx = dynamicCenter.x - lastPos.x
                val dy = dynamicCenter.y - lastPos.y
                val speedPxPerSec = sqrt(dx * dx + dy * dy) / dt
                lastPos = dynamicCenter

                val target = (speedPxPerSec / wobbleSpeedScale).coerceIn(0f, 1f) * wobbleMaxPx

                // Attack fast, decay smoothly (keeps wobble after detach/stop)
                val attack = 0.35f
                val attacked = wobbleAmplitudePx + (target - wobbleAmplitudePx) * attack

                val decay = (1f - (wobbleDecayPerSecond * dt).coerceIn(0f, 1f))
                wobbleAmplitudePx = attacked * decay
            }
        }
    }

    // Recomputed off-main-thread whenever these inputs change.
    val metaballPathState = remember { mutableStateOf<Path?>(null) }

    LaunchedEffect(
        canvasSize,
        staticCenter,
        dynamicCenter,
        fieldRadiusPx,
        isoThreshold,
        gridStepPx,
        wobbleAmplitudePx,
        phaseSeconds,
        wobbleFrequency
    ) {
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@LaunchedEffect

        val w = canvasSize.width.toFloat()
        val h = canvasSize.height.toFloat()
        val step = gridStepPx.coerceAtLeast(5f)

        onDebugText(
            "wobble=${"%.1f".format(wobbleAmplitudePx)}px  " +
                    "fieldRadius=${fieldRadiusPx.toInt()}  " +
                    "iso=${"%.2f".format(isoThreshold)}  " +
                    "grid=${step.toInt()}"
        )

        val computed = withContext(Dispatchers.Default) {
            buildMetaballIsoPathWithWobble(
                widthPx = w,
                heightPx = h,
                gridStepPx = step,
                centerA = staticCenter,
                centerB = dynamicCenter,
                fieldRadiusPx = fieldRadiusPx,
                isoThreshold = isoThreshold,
                wobbleAmplitudePx = wobbleAmplitudePx,
                timeSeconds = phaseSeconds,
                wobbleFrequency = wobbleFrequency
            )
        }
        metaballPathState.value = computed
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { canvasSize = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { start -> pointerPosition = start }
                ) { change, _ ->
                    pointerPosition = change.position
                    change.consume()
                }
            }
    ) {
        if (!isShaderInitialized) {
            paint.shader = LinearGradientShader(
                from = Offset.Zero,
                to = Offset(size.width, size.height),
                colors = listOf(Color(0xffFFEB3B), Color(0xffE91E63)),
                tileMode = TileMode.Clamp
            )
            isShaderInitialized = true
        }

        val path = metaballPathState.value
        if (path != null && !path.isEmpty) {
            drawIntoCanvas { canvas ->
                canvas.drawPath(path, paint)
            }
        }

        if (debugDrawCenters) {
            drawCircle(Color(0x55FFFFFF), radius = 6f, center = staticCenter)
            drawCircle(Color(0x55FFFFFF), radius = 6f, center = dynamicCenter)
            drawLine(Color(0x55FFFFFF), staticCenter, dynamicCenter, strokeWidth = 1.5f)
        }
    }
}

/**
 * Marching Squares iso-contour for a two-metaball field with contour wobble.
 *
 * Field:
 *   f(p) = (R^2 / (dA^2 + eps)) + (R^2 / (dB^2 + eps))
 *
 * Iso-surface:
 *   f(p) = isoThreshold
 *
 * Wobble:
 *   After locating the edge intersection point, displace it along the edge-normal by:
 *     wobbleAmplitudePx * noise(edgeMid, time)
 */
private fun buildMetaballIsoPathWithWobble(
    widthPx: Float,
    heightPx: Float,
    gridStepPx: Float,
    centerA: Offset,
    centerB: Offset,
    fieldRadiusPx: Float,
    isoThreshold: Float,
    wobbleAmplitudePx: Float,
    timeSeconds: Float,
    wobbleFrequency: Float
): Path {
    val path = Path()

    val cols = max(2, (widthPx / gridStepPx).toInt())
    val rows = max(2, (heightPx / gridStepPx).toInt())

    val eps = 1f
    val r2 = fieldRadiusPx * fieldRadiusPx

    fun fieldValueAt(x: Float, y: Float): Float {
        val dxA = x - centerA.x
        val dyA = y - centerA.y
        val dxB = x - centerB.x
        val dyB = y - centerB.y
        val invA = r2 / (dxA * dxA + dyA * dyA + eps)
        val invB = r2 / (dxB * dxB + dyB * dyB + eps)
        return invA + invB
    }

    // Deterministic, cheap pseudo-noise in [-1..1]
    fun noiseSigned(x: Float, y: Float): Float {
        // Spatial frequency is scaled by wobbleFrequency and grid step.
        val sx = x * 0.07f * wobbleFrequency
        val sy = y * 0.07f * wobbleFrequency
        val t = timeSeconds * (2.2f * wobbleFrequency)
        return sin(sx + sy + t)
    }

    fun lerpPoint(
        x0: Float, y0: Float, v0: Float,
        x1: Float, y1: Float, v1: Float
    ): Offset {
        val denom = (v1 - v0)
        val t = if (abs(denom) < 1e-6f) 0.5f else ((isoThreshold - v0) / denom).coerceIn(0f, 1f)
        return Offset(
            x = x0 + (x1 - x0) * t,
            y = y0 + (y1 - y0) * t
        )
    }

    fun addSegment(p0: Offset, p1: Offset) {
        path.moveTo(p0.x, p0.y)
        path.lineTo(p1.x, p1.y)
    }

    fun wobbleEdgePoint(p: Offset, edgeDir: Offset): Offset {
        if (wobbleAmplitudePx <= 0.01f) return p

        // Normal of the edge direction
        val len = max(1e-4f, sqrt(edgeDir.x * edgeDir.x + edgeDir.y * edgeDir.y))
        val nx = -edgeDir.y / len
        val ny = edgeDir.x / len

        // Use noise at the point location (stable spatial wobble)
        val n = noiseSigned(p.x, p.y)

        return Offset(
            x = p.x + nx * (wobbleAmplitudePx * n),
            y = p.y + ny * (wobbleAmplitudePx * n)
        )
    }

    // Corners in each cell:
    //  3 ----- 2
    //  |       |
    //  0 ----- 1
    for (row in 0 until rows - 1) {
        val y0 = row * gridStepPx
        val y1 = (row + 1) * gridStepPx

        for (col in 0 until cols - 1) {
            val x0 = col * gridStepPx
            val x1 = (col + 1) * gridStepPx

            val v0 = fieldValueAt(x0, y0) // bottom-left
            val v1 = fieldValueAt(x1, y0) // bottom-right
            val v2 = fieldValueAt(x1, y1) // top-right
            val v3 = fieldValueAt(x0, y1) // top-left

            val b0 = v0 >= isoThreshold
            val b1 = v1 >= isoThreshold
            val b2 = v2 >= isoThreshold
            val b3 = v3 >= isoThreshold

            val mask =
                (if (b0) 1 else 0) or
                        (if (b1) 2 else 0) or
                        (if (b2) 4 else 0) or
                        (if (b3) 8 else 0)

            if (mask == 0 || mask == 15) continue

            // Raw edge points
            val e0 = lerpPoint(x0, y0, v0, x1, y0, v1) // bottom edge dir = +x
            val e1 = lerpPoint(x1, y0, v1, x1, y1, v2) // right edge dir = +y
            val e2 = lerpPoint(x1, y1, v2, x0, y1, v3) // top edge dir = -x
            val e3 = lerpPoint(x0, y1, v3, x0, y0, v0) // left edge dir = -y

            // Wobbled edge points (displace along edge-normal)
            val w0 = wobbleEdgePoint(e0, edgeDir = Offset(1f, 0f))
            val w1 = wobbleEdgePoint(e1, edgeDir = Offset(0f, 1f))
            val w2 = wobbleEdgePoint(e2, edgeDir = Offset(-1f, 0f))
            val w3 = wobbleEdgePoint(e3, edgeDir = Offset(0f, -1f))

            // Resolve ambiguous cases using center value (stable)
            val centerVal = fieldValueAt((x0 + x1) * 0.5f, (y0 + y1) * 0.5f)
            val centerInside = centerVal >= isoThreshold

            when (mask) {
                1, 14 -> addSegment(w3, w0)
                2, 13 -> addSegment(w0, w1)
                3, 12 -> addSegment(w3, w1)
                4, 11 -> addSegment(w1, w2)
                6, 9 -> addSegment(w0, w2)
                7, 8 -> addSegment(w3, w2)

                5 -> {
                    if (centerInside) {
                        addSegment(w3, w2)
                        addSegment(w0, w1)
                    } else {
                        addSegment(w3, w0)
                        addSegment(w2, w1)
                    }
                }

                10 -> {
                    if (centerInside) {
                        addSegment(w3, w0)
                        addSegment(w2, w1)
                    } else {
                        addSegment(w3, w2)
                        addSegment(w0, w1)
                    }
                }
            }
        }
    }

    return path
}

@Preview(showBackground = true, widthDp = 420, heightDp = 720)
@Composable
private fun MetaballGooeyWobbleSample_Preview() {
    var fieldRadiusPx by remember { mutableFloatStateOf(150f) }
    var isoThreshold by remember { mutableFloatStateOf(1.10f) }
    var gridStepPx by remember { mutableFloatStateOf(10f) }

    var wobbleMaxPx by remember { mutableFloatStateOf(10f) }
    var wobbleSpeedScale by remember { mutableFloatStateOf(1400f) }
    var wobbleDecayPerSecond by remember { mutableFloatStateOf(1.7f) }
    var wobbleFrequency by remember { mutableFloatStateOf(1.2f) }

    var debugLine by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14))
            .padding(16.dp)
    ) {
        Text(text = debugLine, color = Color.White)

        Spacer(Modifier.height(10.dp))

        Text("fieldRadiusPx = ${fieldRadiusPx.toInt()}", color = Color.White)
        Slider(
            value = fieldRadiusPx,
            onValueChange = { fieldRadiusPx = it.coerceIn(60f, 260f) },
            valueRange = 60f..260f
        )

        Text("isoThreshold = ${"%.2f".format(isoThreshold)}", color = Color.White)
        Slider(
            value = isoThreshold,
            onValueChange = { isoThreshold = it.coerceIn(0.60f, 2.00f) },
            valueRange = 0.60f..2.00f
        )

        Text("gridStepPx = ${gridStepPx.toInt()} (lower = smoother, slower)", color = Color.White)
        Slider(
            value = gridStepPx,
            onValueChange = { gridStepPx = it.coerceIn(6f, 28f) },
            valueRange = 6f..28f
        )

        Spacer(Modifier.height(10.dp))

        Text("wobbleMaxPx = ${"%.1f".format(wobbleMaxPx)}", color = Color.White)
        Slider(
            value = wobbleMaxPx,
            onValueChange = { wobbleMaxPx = it.coerceIn(0f, 30f) },
            valueRange = 0f..30f
        )

        Text("wobbleSpeedScale = ${wobbleSpeedScale.toInt()} (bigger = less wobble)", color = Color.White)
        Slider(
            value = wobbleSpeedScale,
            onValueChange = { wobbleSpeedScale = it.coerceIn(200f, 4000f) },
            valueRange = 200f..4000f
        )

        Text("wobbleDecayPerSecond = ${"%.2f".format(wobbleDecayPerSecond)}", color = Color.White)
        Slider(
            value = wobbleDecayPerSecond,
            onValueChange = { wobbleDecayPerSecond = it.coerceIn(0.2f, 6f) },
            valueRange = 0.2f..6f
        )

        Text("wobbleFrequency = ${"%.2f".format(wobbleFrequency)}", color = Color.White)
        Slider(
            value = wobbleFrequency,
            onValueChange = { wobbleFrequency = it.coerceIn(0.2f, 4f) },
            valueRange = 0.2f..4f
        )

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            MetaballGooeyWobbleSample(
                modifier = Modifier.fillMaxSize(),
                fieldRadiusPx = fieldRadiusPx,
                isoThreshold = isoThreshold,
                gridStepPx = gridStepPx,
                wobbleMaxPx = wobbleMaxPx,
                wobbleSpeedScale = wobbleSpeedScale,
                wobbleDecayPerSecond = wobbleDecayPerSecond,
                wobbleFrequency = wobbleFrequency,
                debugDrawCenters = true,
                onDebugText = { debugLine = it }
            )
        }
    }
}
