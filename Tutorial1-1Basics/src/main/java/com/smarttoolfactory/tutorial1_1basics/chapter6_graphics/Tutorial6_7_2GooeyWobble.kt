@file:OptIn(ExperimentalComposeUiApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private enum class GooeyStretchPhase {
    None, Early, Late
}

private data class DebugState(
    val isAttached: Boolean,
    val isPullingApart: Boolean,
    val gapPx: Float,
    val overlapDepthPx: Float,
    val targetStretch: Float,
    val bridgeStrength: Float,
    val phase: GooeyStretchPhase,
    val ligamentThicknessPx: Float?,
    val handleLengthPx: Float?
)

/**
 * Geometry computed off-main-thread.
 *
 * Points are stored as [FloatArray] in x,y interleaved form:
 *   [x0, y0, x1, y1, ...]
 */
private data class GeometryResult(
    val dynamicPoints: FloatArray,
    val staticPoints: FloatArray,
    val ligament: PathLigamentGeometry?
)

@Composable
private fun GooeyStretchAndSnapWobbleSample(
    modifier: Modifier = Modifier,

    // Stretch limiter near shallow overlap
    minStretchScaleAtTouch: Float = 0.20f,
    shallowOverlapBandPx: Float = 45f,

    // Bridge thickness (px)
    bridgeThicknessMaxPx: Float = 14f,
    bridgeThicknessMinPx: Float = 2.5f,

    // Bridge curvature control (dimensionless)
    bridgeHandleScale: Float = 1.0f,

    // Debug rendering toggles
    debugEnabled: Boolean = false,
    debugDrawLigament: Boolean = true,
    debugDrawHandles: Boolean = true,
    debugDrawVectors: Boolean = true,
    debugDrawArcSpans: Boolean = true,

    // Debug state callback (Preview uses this to show EARLY/LATE)
    onDebugState: (DebugState) -> Unit = {}
) {
    // Paths built on main thread (safe)
    val dynamicBlobPath = remember { Path() }
    val staticBlobPath = remember { Path() }
    val ligamentPath = remember { Path() }
    val unionPath = remember { Path() }
    val tmpUnionPath = remember { Path() }

    var pointerPosition by remember { mutableStateOf(Offset.Unspecified) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val unionPaint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = Color.Red
            strokeWidth = 4f
            isAntiAlias = true
        }
    }

    val stretchAmount = remember { Animatable(0f) }

    // Attachment hysteresis
    var isAttached by remember { mutableStateOf(true) }
    val detachGapThresholdPx = 2f
    val attachGapThresholdPx = -2f

    // Pulling apart detection (EMA + hysteresis)
    var previousGapPx by remember { mutableFloatStateOf(Float.NaN) }
    var gapVelocityEma by remember { mutableFloatStateOf(0f) }
    var isPullingApart by remember { mutableStateOf(false) }

    // Stable angles
    var lastDynamicFacingAngleRad by remember { mutableFloatStateOf(0f) }
    var lastStaticFacingAngleRad by remember { mutableFloatStateOf(Math.PI.toFloat()) }

    // Geometry
    val staticCenter =
        remember(canvasSize) { Offset(canvasSize.width / 2f, canvasSize.height / 2f) }
    val dynamicCenter = if (pointerPosition == Offset.Unspecified) staticCenter else pointerPosition

    val dynamicRadiusPx = 150f
    val staticRadiusPx = 150f
    val sumRadiiPx = dynamicRadiusPx + staticRadiusPx

    val centersDistancePx = (dynamicCenter - staticCenter).getDistance()

    /**
     * gapPx:
     *  <= 0  -> overlapping (attached)
     *  >  0  -> separated (detached)
     */
    val gapPx = centersDistancePx - sumRadiiPx

    // Update attached state with hysteresis
    LaunchedEffect(gapPx) {
        isAttached = when {
            isAttached && gapPx > detachGapThresholdPx -> false
            !isAttached && gapPx < attachGapThresholdPx -> true
            else -> isAttached
        }
    }

    // Update pulling apart detection
    LaunchedEffect(gapPx) {
        val oldGap = previousGapPx
        if (!oldGap.isNaN()) {
            val gapDeltaPx = gapPx - oldGap
            val emaAlpha = 0.25f
            gapVelocityEma = gapVelocityEma + (gapDeltaPx - gapVelocityEma) * emaAlpha
        }
        previousGapPx = gapPx

        val pullingOnThreshold = 0.20f
        val pullingOffThreshold = -0.20f

        isPullingApart = when {
            isPullingApart && gapVelocityEma < pullingOffThreshold -> false
            !isPullingApart && gapVelocityEma > pullingOnThreshold -> true
            else -> isPullingApart
        }
    }

    // Facing angles
    val dynamicFacingAngleRad: Float
    val staticFacingAngleRad: Float
    if (centersDistancePx > 6f) {
        val dynamicToStatic = staticCenter - dynamicCenter
        val staticToDynamic = dynamicCenter - staticCenter

        lastDynamicFacingAngleRad = atan2(dynamicToStatic.y, dynamicToStatic.x)
        lastStaticFacingAngleRad = atan2(staticToDynamic.y, staticToDynamic.x)

        dynamicFacingAngleRad = lastDynamicFacingAngleRad
        staticFacingAngleRad = lastStaticFacingAngleRad
    } else {
        dynamicFacingAngleRad = lastDynamicFacingAngleRad
        staticFacingAngleRad = lastStaticFacingAngleRad
    }

    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

    // Stretch + bridge gating bands
    val stretchBandPx = 90f
    val bridgeBandPx = 28f

    val targetStretch =
        if (isAttached && isPullingApart) {
            val normalized = ((gapPx + stretchBandPx) / stretchBandPx).coerceIn(0f, 1f)
            smoothstep(normalized)
        } else 0f

    /**
     * bridgeStrength rises when gap is near 0 (still negative).
     * 0 -> no ligament
     * 1 -> right near detachment
     */
    val bridgeStrength =
        if (isAttached && isPullingApart) {
            if (gapPx < 0f && gapPx > -bridgeBandPx) {
                val normalized = ((gapPx + bridgeBandPx) / bridgeBandPx).coerceIn(0f, 1f)
                smoothstep(normalized)
            } else 0f
        } else 0f

    // Drive stretch anim
    LaunchedEffect(targetStretch, isAttached) {
        if (isAttached) {
            stretchAmount.snapTo(targetStretch)
        } else {
            stretchAmount.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.35f, stiffness = 320f)
            )
        }
    }

    // Shallow-overlap limiter (prevents huge neck when overlap is tiny)
    val overlapDepthPx = max(0f, -gapPx)
    val safeBandPx = max(1f, shallowOverlapBandPx)
    val overlapDepthFraction = (overlapDepthPx / safeBandPx).coerceIn(0f, 1f)
    val overlapRamp = smoothstep(overlapDepthFraction)

    val minScale = minStretchScaleAtTouch.coerceIn(0f, 1f)
    val shallowOverlapStretchScale = minScale + (1f - minScale) * overlapRamp

    val baseStretchPx = 60f * stretchAmount.value
    val effectiveStretchPx = baseStretchPx * shallowOverlapStretchScale
    val neckFocusPower = 6.5f

    // Phase indicator
    val phase =
        if (!(isAttached && isPullingApart) || targetStretch <= 0f) {
            GooeyStretchPhase.None
        } else {
            if (bridgeStrength <= 0f) GooeyStretchPhase.Early else GooeyStretchPhase.Late
        }

    // --- Off-main-thread geometry computation ---
    var geometryResult by remember { mutableStateOf<GeometryResult?>(null) }

    LaunchedEffect(
        dynamicCenter,
        staticCenter,
        dynamicRadiusPx,
        staticRadiusPx,
        dynamicFacingAngleRad,
        staticFacingAngleRad,
        effectiveStretchPx,
        neckFocusPower,
        bridgeStrength,
        bridgeThicknessMaxPx,
        bridgeThicknessMinPx,
        bridgeHandleScale
    ) {
        geometryResult = withContext(Dispatchers.Default) {
            val dynamicPoints = buildStretchedBlobPoints(
                center = dynamicCenter,
                baseRadiusPx = dynamicRadiusPx,
                samplePointCount = 140,
                stretchPx = effectiveStretchPx,
                facingAngleRad = dynamicFacingAngleRad,
                neckFocusPower = neckFocusPower,
                stretchBias = 1.0f
            )

            val staticPoints = buildStretchedBlobPoints(
                center = staticCenter,
                baseRadiusPx = staticRadiusPx,
                samplePointCount = 160,
                stretchPx = effectiveStretchPx * 0.8f,
                facingAngleRad = staticFacingAngleRad,
                neckFocusPower = neckFocusPower,
                stretchBias = 0.9f
            )

            val ligamentGeometry =
                if (bridgeStrength > 0f) {
                    computeLigamentGeometry(
                        firstCenter = dynamicCenter,
                        firstRadiusPx = dynamicRadiusPx,
                        firstFacingAngleRad = dynamicFacingAngleRad,
                        secondCenter = staticCenter,
                        secondRadiusPx = staticRadiusPx,
                        secondFacingAngleRad = staticFacingAngleRad,
                        strength = bridgeStrength,
                        bridgeThicknessMaxPx = bridgeThicknessMaxPx,
                        bridgeThicknessMinPx = bridgeThicknessMinPx,
                        bridgeHandleScale = bridgeHandleScale
                    )
                } else null

            GeometryResult(
                dynamicPoints = dynamicPoints,
                staticPoints = staticPoints,
                ligament = ligamentGeometry
            )
        }
    }

    // Feed debug info back to Preview
    SideEffect {
        val ligament = geometryResult?.ligament
        onDebugState(
            DebugState(
                isAttached = isAttached,
                isPullingApart = isPullingApart,
                gapPx = gapPx,
                overlapDepthPx = overlapDepthPx,
                targetStretch = targetStretch,
                bridgeStrength = bridgeStrength,
                phase = phase,
                ligamentThicknessPx = ligament?.ligamentThicknessPx,
                handleLengthPx = ligament?.handleLengthPx
            )
        )
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { canvasSize = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { startOffset -> pointerPosition = startOffset }
                ) { change, _ ->
                    pointerPosition = change.position
                    change.consume()
                }
            }
    ) {
        val geom = geometryResult ?: return@Canvas

        // Build paths on main thread using precomputed points.
        dynamicBlobPath.reset()
        fillPathFromPoints(dynamicBlobPath, geom.dynamicPoints)

        staticBlobPath.reset()
        fillPathFromPoints(staticBlobPath, geom.staticPoints)

        ligamentPath.reset()
        val ligament = geom.ligament
        if (ligament != null) {
            ligamentPath.moveTo(ligament.firstTop.x, ligament.firstTop.y)
            ligamentPath.cubicTo(
                ligament.firstTopControl.x, ligament.firstTopControl.y,
                ligament.secondTopControl.x, ligament.secondTopControl.y,
                ligament.secondTop.x, ligament.secondTop.y
            )
            ligamentPath.lineTo(ligament.secondBottom.x, ligament.secondBottom.y)
            ligamentPath.cubicTo(
                ligament.secondBottomControl.x, ligament.secondBottomControl.y,
                ligament.firstBottomControl.x, ligament.firstBottomControl.y,
                ligament.firstBottom.x, ligament.firstBottom.y
            )
            ligamentPath.close()
        }

        // Debug overlays
        if (debugEnabled) {
            if (debugDrawLigament && !ligamentPath.isEmpty) {
                drawPath(
                    path = ligamentPath,
                    color = Color.Cyan,
                    style = Stroke(width = 3f)
                )
            }

            if (ligament != null) {
                if (debugDrawHandles) {
                    drawLine(
                        Color.Magenta,
                        ligament.firstTop,
                        ligament.firstTopControl,
                        strokeWidth = 2f
                    )
                    drawLine(
                        Color.Magenta,
                        ligament.secondTop,
                        ligament.secondTopControl,
                        strokeWidth = 2f
                    )
                    drawLine(
                        Color.Magenta,
                        ligament.secondBottom,
                        ligament.secondBottomControl,
                        strokeWidth = 2f
                    )
                    drawLine(
                        Color.Magenta,
                        ligament.firstBottom,
                        ligament.firstBottomControl,
                        strokeWidth = 2f
                    )

                    drawCircle(Color.Magenta, radius = 4f, center = ligament.firstTopControl)
                    drawCircle(Color.Magenta, radius = 4f, center = ligament.secondTopControl)
                    drawCircle(Color.Magenta, radius = 4f, center = ligament.secondBottomControl)
                    drawCircle(Color.Magenta, radius = 4f, center = ligament.firstBottomControl)
                }

                if (debugDrawArcSpans) {
                    drawCircle(Color.Yellow, radius = 5f, center = ligament.firstTop)
                    drawCircle(Color.Yellow, radius = 5f, center = ligament.firstBottom)
                    drawCircle(Color.Yellow, radius = 5f, center = ligament.secondTop)
                    drawCircle(Color.Yellow, radius = 5f, center = ligament.secondBottom)

                    drawLine(
                        Color.Yellow,
                        ligament.firstCenter,
                        ligament.firstTop,
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        Color.Yellow,
                        ligament.firstCenter,
                        ligament.firstBottom,
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        Color.Yellow,
                        ligament.secondCenter,
                        ligament.secondTop,
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        Color.Yellow,
                        ligament.secondCenter,
                        ligament.secondBottom,
                        strokeWidth = 1.5f
                    )
                }
            }

            if (debugDrawVectors) {
                drawLine(Color.White, dynamicCenter, staticCenter, strokeWidth = 1.5f)

                val dynDir = Offset(cos(dynamicFacingAngleRad), sin(dynamicFacingAngleRad))
                val staDir = Offset(cos(staticFacingAngleRad), sin(staticFacingAngleRad))

                drawLine(
                    Color.Green,
                    dynamicCenter,
                    dynamicCenter + dynDir * (dynamicRadiusPx * 0.9f),
                    strokeWidth = 2f
                )
                drawLine(
                    Color.Green,
                    staticCenter,
                    staticCenter + staDir * (staticRadiusPx * 0.9f),
                    strokeWidth = 2f
                )

                drawCircle(
                    Color(0x55FFFFFF),
                    radius = dynamicRadiusPx,
                    center = dynamicCenter,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    Color(0x55FFFFFF),
                    radius = staticRadiusPx,
                    center = staticCenter,
                    style = Stroke(width = 1f)
                )
            }
        }

        // Final union: (dynamic ∪ static) ∪ ligament
        unionPath.reset()
        unionPath.op(dynamicBlobPath, staticBlobPath, PathOperation.Union)

        if (!ligamentPath.isEmpty) {
            tmpUnionPath.reset()
            tmpUnionPath.op(unionPath, ligamentPath, PathOperation.Union)
            unionPath.reset()
            unionPath.addPath(tmpUnionPath)
        }

        drawIntoCanvas { canvas ->
            canvas.drawPath(unionPath, unionPaint)
        }
    }
}

/**
 * Heavy trig/pow loop moved off-main-thread:
 * returns polyline points describing the stretched blob boundary.
 */
private fun buildStretchedBlobPoints(
    center: Offset,
    baseRadiusPx: Float,
    samplePointCount: Int,
    stretchPx: Float,
    facingAngleRad: Float,
    neckFocusPower: Float,
    stretchBias: Float
): FloatArray {
    if (samplePointCount < 16) return FloatArray(0)

    val points = FloatArray((samplePointCount + 1) * 2)

    val angleStepRad = (2.0 * Math.PI / samplePointCount).toFloat()

    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

    fun neckMask(thetaRad: Float): Float {
        val facingCos = cos(thetaRad - facingAngleRad).coerceIn(-1f, 1f)
        val facingFraction = (facingCos + 1f) * 0.5f
        val smoothedFacing = smoothstep(facingFraction)
        return smoothedFacing.pow(neckFocusPower)
    }

    fun radiusAtAngle(thetaRad: Float): Float {
        val mask = neckMask(thetaRad)

        val neckInflationPx = (stretchPx * stretchBias) * mask
        val farMask = 1f - mask
        val farCompressionPx = -0.22f * stretchPx * farMask

        return baseRadiusPx + neckInflationPx + farCompressionPx
    }

    var angleRad = 0f
    for (i in 0..samplePointCount) {
        angleRad = i * angleStepRad
        val radiusPx = radiusAtAngle(angleRad)
        val x = center.x + radiusPx * cos(angleRad)
        val y = center.y + radiusPx * sin(angleRad)
        val idx = i * 2
        points[idx] = x
        points[idx + 1] = y
    }

    return points
}

/**
 * Cheap main-thread reconstruction:
 * consumes [FloatArray] x,y points and builds a closed polygonal [Path].
 */
private fun fillPathFromPoints(outPath: Path, points: FloatArray) {
    if (points.isEmpty()) return
    outPath.moveTo(points[0], points[1])
    var i = 2
    while (i < points.size) {
        outPath.lineTo(points[i], points[i + 1])
        i += 2
    }
    outPath.close()
}

private data class PathLigamentGeometry(
    val firstCenter: Offset,
    val secondCenter: Offset,

    val firstTop: Offset,
    val firstBottom: Offset,
    val secondTop: Offset,
    val secondBottom: Offset,

    val firstTopControl: Offset,
    val secondTopControl: Offset,
    val secondBottomControl: Offset,
    val firstBottomControl: Offset,

    val ligamentThicknessPx: Float,
    val handleLengthPx: Float
)

private fun computeLigamentGeometry(
    firstCenter: Offset,
    firstRadiusPx: Float,
    firstFacingAngleRad: Float,
    secondCenter: Offset,
    secondRadiusPx: Float,
    secondFacingAngleRad: Float,
    strength: Float,
    bridgeThicknessMaxPx: Float,
    bridgeThicknessMinPx: Float,
    bridgeHandleScale: Float
): PathLigamentGeometry {
    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)
    fun clamp(x: Float): Float = x.coerceIn(0f, 1f)

    val strengthSmoothed = smoothstep(clamp(strength))

    val ligamentThicknessPx =
        bridgeThicknessMaxPx + (bridgeThicknessMinPx - bridgeThicknessMaxPx) * strengthSmoothed

    fun angleSpanFor(radiusPx: Float): Float {
        val ratio = (ligamentThicknessPx / max(1f, radiusPx)).coerceIn(0f, 0.35f)
        return asin(ratio)
    }

    val firstAngleSpanRad = angleSpanFor(firstRadiusPx)
    val secondAngleSpanRad = angleSpanFor(secondRadiusPx)

    fun pointOnCircle(center: Offset, radiusPx: Float, angleRad: Float): Offset =
        Offset(center.x + radiusPx * cos(angleRad), center.y + radiusPx * sin(angleRad))

    fun tangentUnit(angleRad: Float): Offset = Offset(-sin(angleRad), cos(angleRad))

    val firstTop =
        pointOnCircle(firstCenter, firstRadiusPx, firstFacingAngleRad + firstAngleSpanRad)
    val firstBottom =
        pointOnCircle(firstCenter, firstRadiusPx, firstFacingAngleRad - firstAngleSpanRad)

    val secondTop =
        pointOnCircle(secondCenter, secondRadiusPx, secondFacingAngleRad - secondAngleSpanRad)
    val secondBottom =
        pointOnCircle(secondCenter, secondRadiusPx, secondFacingAngleRad + secondAngleSpanRad)

    val topSpanPx = (secondTop - firstTop).getDistance()
    val bottomSpanPx = (firstBottom - secondBottom).getDistance()
    val minSpanPx = min(topSpanPx, bottomSpanPx)

    val maxHandlePx = min(firstRadiusPx, secondRadiusPx) * 0.25f
    val baseHandlePx = (minSpanPx * 0.30f).coerceIn(4f, maxHandlePx)
    val handleLengthPx = (baseHandlePx * bridgeHandleScale).coerceIn(1f, maxHandlePx)

    val firstTopTangent = tangentUnit(firstFacingAngleRad + firstAngleSpanRad)
    val firstBottomTangent = tangentUnit(firstFacingAngleRad - firstAngleSpanRad)

    val secondTopTangent = tangentUnit(secondFacingAngleRad - secondAngleSpanRad)
    val secondBottomTangent = tangentUnit(secondFacingAngleRad + secondAngleSpanRad)

    val firstTopControl = firstTop + firstTopTangent * handleLengthPx
    val secondTopControl = secondTop - secondTopTangent * handleLengthPx

    val secondBottomControl = secondBottom + secondBottomTangent * handleLengthPx
    val firstBottomControl = firstBottom - firstBottomTangent * handleLengthPx

    return PathLigamentGeometry(
        firstCenter = firstCenter,
        secondCenter = secondCenter,
        firstTop = firstTop,
        firstBottom = firstBottom,
        secondTop = secondTop,
        secondBottom = secondBottom,
        firstTopControl = firstTopControl,
        secondTopControl = secondTopControl,
        secondBottomControl = secondBottomControl,
        firstBottomControl = firstBottomControl,
        ligamentThicknessPx = ligamentThicknessPx,
        handleLengthPx = handleLengthPx
    )
}

@Preview(showBackground = true, widthDp = 420, heightDp = 720)
@Composable
private fun GooeyStretchAndSnapSamplePreview() {
    var minStretchScaleAtTouch by remember { mutableFloatStateOf(0.60f) }
    var shallowOverlapBandPx by remember { mutableFloatStateOf(100f) }

    var bridgeThicknessMaxPx by remember { mutableFloatStateOf(32f) }
    var bridgeThicknessMinPx by remember { mutableFloatStateOf(2.5f) }

    var bridgeHandleScale by remember { mutableFloatStateOf(1.0f) }

    var debugEnabled by remember { mutableStateOf(true) }
    var debugDrawLigament by remember { mutableStateOf(true) }
    var debugDrawHandles by remember { mutableStateOf(true) }
    var debugDrawVectors by remember { mutableStateOf(true) }
    var debugDrawArcSpans by remember { mutableStateOf(true) }

    var debugState by remember {
        mutableStateOf(
            DebugState(
                isAttached = true,
                isPullingApart = false,
                gapPx = 0f,
                overlapDepthPx = 0f,
                targetStretch = 0f,
                bridgeStrength = 0f,
                phase = GooeyStretchPhase.None,
                ligamentThicknessPx = null,
                handleLengthPx = null
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14))
            .padding(16.dp)
    ) {
        Text(
            text = buildString {
                append("Phase: ${debugState.phase}")
                append("  |  attached=${debugState.isAttached}")
                append("  pulling=${debugState.isPullingApart}")
                append("  gap=${"%.1f".format(debugState.gapPx)}")
                append("  stretch=${"%.2f".format(debugState.targetStretch)}")
                append("  bridge=${"%.2f".format(debugState.bridgeStrength)}")
                debugState.ligamentThicknessPx?.let { append("  thick=${"%.1f".format(it)}") }
                debugState.handleLengthPx?.let { append("  handle=${"%.1f".format(it)}") }
            },
            color = Color.White
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "minStretchScaleAtTouch = ${"%.2f".format(minStretchScaleAtTouch)}",
            color = Color.White
        )
        Slider(
            value = minStretchScaleAtTouch,
            onValueChange = { minStretchScaleAtTouch = it.coerceIn(0f, 1f) },
            valueRange = 0f..1f
        )

        Text(
            "shallowOverlapBandPx = ${"%.0f".format(shallowOverlapBandPx)} px",
            color = Color.White
        )
        Slider(
            value = shallowOverlapBandPx,
            onValueChange = { shallowOverlapBandPx = it.coerceIn(5f, 200f) },
            valueRange = 5f..200f
        )

        Text("bridgeThicknessMaxPx = ${"%.1f".format(bridgeThicknessMaxPx)}", color = Color.White)
        Slider(
            value = bridgeThicknessMaxPx,
            onValueChange = { bridgeThicknessMaxPx = it.coerceIn(2f, 40f) },
            valueRange = 2f..40f
        )

        Text("bridgeThicknessMinPx = ${"%.1f".format(bridgeThicknessMinPx)}", color = Color.White)
        Slider(
            value = bridgeThicknessMinPx,
            onValueChange = { bridgeThicknessMinPx = it.coerceIn(0.5f, bridgeThicknessMaxPx) },
            valueRange = 0.5f..40f
        )

        Text("bridgeHandleScale = ${"%.2f".format(bridgeHandleScale)}", color = Color.White)
        Slider(
            value = bridgeHandleScale,
            onValueChange = { bridgeHandleScale = it.coerceIn(0.10f, 2.50f) },
            valueRange = 0.10f..2.50f
        )

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DebugToggleText("Debug", debugEnabled) { debugEnabled = !debugEnabled }
            DebugToggleText("Ligament", debugDrawLigament) {
                debugDrawLigament = !debugDrawLigament
            }
            DebugToggleText("Handles", debugDrawHandles) { debugDrawHandles = !debugDrawHandles }
            DebugToggleText("Vectors", debugDrawVectors) { debugDrawVectors = !debugDrawVectors }
            DebugToggleText("Spans", debugDrawArcSpans) { debugDrawArcSpans = !debugDrawArcSpans }
        }

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GooeyStretchAndSnapWobbleSample(
                modifier = Modifier.fillMaxSize(),
                minStretchScaleAtTouch = minStretchScaleAtTouch,
                shallowOverlapBandPx = shallowOverlapBandPx,
                bridgeThicknessMaxPx = bridgeThicknessMaxPx,
                bridgeThicknessMinPx = bridgeThicknessMinPx,
                bridgeHandleScale = bridgeHandleScale,
                debugEnabled = debugEnabled,
                debugDrawLigament = debugDrawLigament,
                debugDrawHandles = debugDrawHandles,
                debugDrawVectors = debugDrawVectors,
                debugDrawArcSpans = debugDrawArcSpans,
                onDebugState = { debugState = it }
            )
        }
    }
}

@Composable
private fun DebugToggleText(
    label: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Text(
        text = if (enabled) "[$label]" else label,
        color = if (enabled) Color(0xFF7CFFB2) else Color(0xFFB0B0B0),
        modifier = Modifier
            .background(Color(0x33000000))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .clickable { onToggle() }
    )
}
