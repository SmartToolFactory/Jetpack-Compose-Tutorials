@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

enum class StretchPhase { None, Early, Late }
enum class StaticBlobShape { Circle, Rect, RoundedRect }

data class GooeyDebugState(
    val isAttached: Boolean,
    val isPullingApart: Boolean,
    val gapPx: Float,
    val overlapDepthPx: Float,
    val targetStretch: Float,
    val bridgeStrength: Float,
    val phase: StretchPhase,
    val ligamentThicknessPx: Float?,
    val handleLengthPx: Float?
)

private data class GooeyGeometryResult(
    val dynamicPoints: FloatArray,
    val staticPoints: FloatArray,
    val ligament: LigamentGeometry?
)

@Composable
fun GooeyStretchAndSnapSample(
    modifier: Modifier = Modifier,
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // ---- controls ----
    var staticShape by remember { mutableStateOf(StaticBlobShape.RoundedRect) }

    var dynamicRadiusPx by remember { mutableFloatStateOf(150f) }
    var staticCircleRadiusPx by remember { mutableFloatStateOf(150f) }

    // For your setup: wide rounded rect
    var staticRectWidthPx by remember { mutableFloatStateOf(820f) }
    var staticRectHeightPx by remember { mutableFloatStateOf(260f) }
    var staticCornerPx by remember { mutableFloatStateOf(90f) }

    var minStretchScaleAtTouch by remember { mutableFloatStateOf(0.60f) }
    var shallowOverlapBandPx by remember { mutableFloatStateOf(100f) }

    var bridgeThicknessMaxPx by remember { mutableFloatStateOf(32f) }
    var bridgeThicknessMinPx by remember { mutableFloatStateOf(2.5f) }
    var bridgeHandleScale by remember { mutableFloatStateOf(1.0f) }

    var detachWobbleMaxPx by remember { mutableFloatStateOf(18f) } // wobble amplitude after detach

    var debugEnabled by remember { mutableStateOf(true) }
    var debugDrawLigament by remember { mutableStateOf(true) }
    var debugDrawHandles by remember { mutableStateOf(true) }
    var debugDrawVectors by remember { mutableStateOf(true) }
    var debugDrawArcSpans by remember { mutableStateOf(true) }

    var debugState by remember {
        mutableStateOf(
            GooeyDebugState(
                isAttached = true,
                isPullingApart = false,
                gapPx = 0f,
                overlapDepthPx = 0f,
                targetStretch = 0f,
                bridgeStrength = 0f,
                phase = StretchPhase.None,
                ligamentThicknessPx = null,
                handleLengthPx = null
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14))
    ) {
        GooeyStretchAndSnapCanvas(
            modifier = Modifier.fillMaxSize(),

            dynamicRadiusPx = dynamicRadiusPx,
            staticCircleRadiusPx = staticCircleRadiusPx,
            staticRectWidthPx = staticRectWidthPx,
            staticRectHeightPx = staticRectHeightPx,
            staticRectCornerRadiusPx = staticCornerPx,
            staticShape = staticShape,

            minStretchScaleAtTouch = minStretchScaleAtTouch,
            shallowOverlapBandPx = shallowOverlapBandPx,

            bridgeThicknessMaxPx = bridgeThicknessMaxPx,
            bridgeThicknessMinPx = bridgeThicknessMinPx,
            bridgeHandleScale = bridgeHandleScale,

            detachWobbleMaxPx = detachWobbleMaxPx,

            debugEnabled = debugEnabled,
            debugDrawLigament = debugDrawLigament,
            debugDrawHandles = debugDrawHandles,
            debugDrawVectors = debugDrawVectors,
            debugDrawArcSpans = debugDrawArcSpans,

            onDebugState = { debugState = it }
        )

        // Header (Column so button never disappears)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = buildString {
                    append("Phase: ${debugState.phase}")
                    append(" | attached=${debugState.isAttached}")
                    append(" pulling=${debugState.isPullingApart}")
                    append(" gap=${"%.1f".format(debugState.gapPx)}")
                    append(" stretch=${"%.2f".format(debugState.targetStretch)}")
                    append(" bridge=${"%.2f".format(debugState.bridgeStrength)}")
                },
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { showSheet = true }) { Text("Controls") }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showSheet = false },
                containerColor = Color(0xFF111826),
                contentWindowInsets = { WindowInsets.safeDrawing }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("Static shape", color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DebugToggleText("Circle", staticShape == StaticBlobShape.Circle) { staticShape = StaticBlobShape.Circle }
                        DebugToggleText("Rect", staticShape == StaticBlobShape.Rect) { staticShape = StaticBlobShape.Rect }
                        DebugToggleText("RoundRect", staticShape == StaticBlobShape.RoundedRect) { staticShape = StaticBlobShape.RoundedRect }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Dynamic radius = ${"%.0f".format(dynamicRadiusPx)} px", color = Color.White)
                    Slider(dynamicRadiusPx, { dynamicRadiusPx = it.coerceIn(60f, 260f) }, valueRange = 60f..260f)

                    Spacer(Modifier.height(6.dp))

                    Text("Static rect width = ${"%.0f".format(staticRectWidthPx)} px", color = Color.White)
                    Slider(staticRectWidthPx, { staticRectWidthPx = it.coerceIn(240f, 1200f) }, valueRange = 240f..1200f)

                    Text("Static rect height = ${"%.0f".format(staticRectHeightPx)} px", color = Color.White)
                    Slider(staticRectHeightPx, { staticRectHeightPx = it.coerceIn(120f, 560f) }, valueRange = 120f..560f)

                    val maxCorner = min(staticRectWidthPx, staticRectHeightPx) * 0.5f
                    Text("Corner radius = ${"%.0f".format(staticCornerPx)} px", color = Color.White)
                    Slider(
                        value = staticCornerPx.coerceIn(0f, maxCorner),
                        onValueChange = { staticCornerPx = it.coerceIn(0f, maxCorner) },
                        valueRange = 0f..max(1f, maxCorner)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("Detach wobble amplitude = ${"%.0f".format(detachWobbleMaxPx)} px", color = Color.White)
                    Slider(detachWobbleMaxPx, { detachWobbleMaxPx = it.coerceIn(0f, 48f) }, valueRange = 0f..48f)

                    Spacer(Modifier.height(10.dp))

                    Text("minStretchScaleAtTouch = ${"%.2f".format(minStretchScaleAtTouch)}", color = Color.White)
                    Slider(minStretchScaleAtTouch, { minStretchScaleAtTouch = it.coerceIn(0f, 1f) }, valueRange = 0f..1f)

                    Text("shallowOverlapBandPx = ${"%.0f".format(shallowOverlapBandPx)} px", color = Color.White)
                    Slider(shallowOverlapBandPx, { shallowOverlapBandPx = it.coerceIn(5f, 240f) }, valueRange = 5f..240f)

                    Text("bridgeThicknessMaxPx = ${"%.1f".format(bridgeThicknessMaxPx)}", color = Color.White)
                    Slider(bridgeThicknessMaxPx, { bridgeThicknessMaxPx = it.coerceIn(2f, 60f) }, valueRange = 2f..60f)

                    Text("bridgeThicknessMinPx = ${"%.1f".format(bridgeThicknessMinPx)}", color = Color.White)
                    Slider(
                        bridgeThicknessMinPx,
                        { bridgeThicknessMinPx = it.coerceIn(0.5f, bridgeThicknessMaxPx) },
                        valueRange = 0.5f..60f
                    )

                    Text("bridgeHandleScale = ${"%.2f".format(bridgeHandleScale)}", color = Color.White)
                    Slider(bridgeHandleScale, { bridgeHandleScale = it.coerceIn(0.10f, 2.50f) }, valueRange = 0.10f..2.50f)

                    Spacer(Modifier.height(10.dp))

                    Text("Debug", color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DebugToggleText("On", debugEnabled) { debugEnabled = !debugEnabled }
                        DebugToggleText("Ligament", debugDrawLigament) { debugDrawLigament = !debugDrawLigament }
                        DebugToggleText("Handles", debugDrawHandles) { debugDrawHandles = !debugDrawHandles }
                        DebugToggleText("Vectors", debugDrawVectors) { debugDrawVectors = !debugDrawVectors }
                        DebugToggleText("Spans", debugDrawArcSpans) { debugDrawArcSpans = !debugDrawArcSpans }
                    }

                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

/**
 * KEY FIX:
 * - gapPx is computed using signed distance to the actual static shape boundary (Rect/RoundRect),
 *   not distance to staticCenter.
 * - facing direction uses the closest point on the static boundary (stable when moving left/right).
 */
@Composable
private fun GooeyStretchAndSnapCanvas(
    modifier: Modifier,

    dynamicRadiusPx: Float,
    staticCircleRadiusPx: Float,
    staticRectWidthPx: Float,
    staticRectHeightPx: Float,
    staticRectCornerRadiusPx: Float,
    staticShape: StaticBlobShape,

    minStretchScaleAtTouch: Float,
    shallowOverlapBandPx: Float,

    bridgeThicknessMaxPx: Float,
    bridgeThicknessMinPx: Float,
    bridgeHandleScale: Float,

    detachWobbleMaxPx: Float,

    debugEnabled: Boolean,
    debugDrawLigament: Boolean,
    debugDrawHandles: Boolean,
    debugDrawVectors: Boolean,
    debugDrawArcSpans: Boolean,

    onDebugState: (GooeyDebugState) -> Unit
) {
    val dynamicBlobPath = remember { Path() }
    val staticBlobPath = remember { Path() }

    val ligamentPath = remember { Path() }
    val unionPath = remember { Path() }
    val tmpPathA = remember { Path() }

    val intersectionPath = remember { Path() }
    val clippedLigamentPath = remember { Path() }

    var pointerPosition by remember { mutableStateOf(Offset.Unspecified) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = Color.Red
            strokeWidth = 4f
            isAntiAlias = true
        }
    }

    var isAttached by remember { mutableStateOf(true) }
    val detachGapThresholdPx = 0.5f
    val attachGapThresholdPx = -1.5f

    var previousGapPx by remember { mutableFloatStateOf(Float.NaN) }
    var gapVelocityEma by remember { mutableFloatStateOf(0f) }
    var isPullingApart by remember { mutableStateOf(false) }

    var lastDynamicFacingAngleRad by remember { mutableFloatStateOf(0f) }
    var lastStaticFacingAngleRad by remember { mutableFloatStateOf(PI.toFloat()) }

    val stretchAmount = remember { Animatable(0f) }

    val detachWobble = remember { Animatable(0f) }
    var prevAttached by remember { mutableStateOf(true) }

    val staticCenter = remember(canvasSize) { Offset(canvasSize.width / 2f, canvasSize.height / 2f) }
    val dynamicCenter = if (pointerPosition == Offset.Unspecified) staticCenter else pointerPosition

    val halfW = staticRectWidthPx * 0.5f
    val halfH = staticRectHeightPx * 0.5f
    val corner = staticRectCornerRadiusPx.coerceIn(0f, min(halfW, halfH))

    // ---------- TRUE GAP ----------
    // sdStatic: signed distance from point to static boundary (positive outside, negative inside)
    val sdStatic: Float = when (staticShape) {
        StaticBlobShape.Circle -> {
            val d = (dynamicCenter - staticCenter).getDistance()
            d - staticCircleRadiusPx
        }
        StaticBlobShape.Rect -> signedDistanceBox(dynamicCenter - staticCenter, halfW, halfH)
        StaticBlobShape.RoundedRect -> signedDistanceRoundedBox(dynamicCenter - staticCenter, halfW, halfH, corner)
    }

    // gap between circle and static shape boundary
    // >0 separated, <0 overlapping
    val gapPx = sdStatic - dynamicRadiusPx

    // closest point on static boundary (used for facing vector stability)
    val staticContactPoint: Offset = when (staticShape) {
        StaticBlobShape.Circle -> {
            val dir = normalizeSafe(dynamicCenter - staticCenter)
            staticCenter + dir * staticCircleRadiusPx
        }
        StaticBlobShape.Rect -> staticCenter + closestPointOnBox(dynamicCenter - staticCenter, halfW, halfH)
        StaticBlobShape.RoundedRect -> staticCenter + closestPointOnRoundedBox(dynamicCenter - staticCenter, halfW, halfH, corner)
    }

    LaunchedEffect(gapPx) {
        isAttached = when {
            isAttached && gapPx > detachGapThresholdPx -> false
            !isAttached && gapPx < attachGapThresholdPx -> true
            else -> isAttached
        }
    }

    LaunchedEffect(gapPx) {
        val old = previousGapPx
        if (!old.isNaN()) {
            val delta = gapPx - old
            val a = 0.25f
            gapVelocityEma = gapVelocityEma + (delta - gapVelocityEma) * a
        }
        previousGapPx = gapPx

        val onTh = 0.20f
        val offTh = -0.20f
        isPullingApart = when {
            isPullingApart && gapVelocityEma < offTh -> false
            !isPullingApart && gapVelocityEma > onTh -> true
            else -> isPullingApart
        }
    }

    // ---------- Facing angles (use contact point) ----------
    val distToContact = (staticContactPoint - dynamicCenter).getDistance()
    val dynamicFacingAngleRad: Float
    val staticFacingAngleRad: Float
    if (distToContact > 2f) {
        val d2c = staticContactPoint - dynamicCenter          // dynamic faces towards contact
        val c2d = dynamicCenter - staticContactPoint          // static faces towards dynamic
        lastDynamicFacingAngleRad = atan2(d2c.y, d2c.x)
        lastStaticFacingAngleRad = atan2(c2d.y, c2d.x)
        dynamicFacingAngleRad = lastDynamicFacingAngleRad
        staticFacingAngleRad = lastStaticFacingAngleRad
    } else {
        dynamicFacingAngleRad = lastDynamicFacingAngleRad
        staticFacingAngleRad = lastStaticFacingAngleRad
    }

    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

    val stretchBandPx = 90f
    val bridgeBandPx = 28f

    val targetStretch =
        if (isAttached && isPullingApart) {
            // IMPORTANT: normalized off TRUE gap
            val normalized = ((gapPx + stretchBandPx) / stretchBandPx).coerceIn(0f, 1f)
            smoothstep(normalized)
        } else 0f

    val bridgeStrength =
        if (isAttached && isPullingApart) {
            // late only when *actually near detachment* (true gap close to 0)
            if (gapPx < 0f && gapPx > -bridgeBandPx) {
                val normalized = ((gapPx + bridgeBandPx) / bridgeBandPx).coerceIn(0f, 1f)
                smoothstep(normalized)
            } else 0f
        } else 0f

    LaunchedEffect(isAttached, targetStretch) {
        if (isAttached) stretchAmount.snapTo(targetStretch) else stretchAmount.snapTo(0f)
    }

    LaunchedEffect(isAttached) {
        if (prevAttached && !isAttached) {
            detachWobble.snapTo(1f)
            detachWobble.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.35f, stiffness = 280f)
            )
            isPullingApart = false
            gapVelocityEma = 0f
        }
        prevAttached = isAttached
    }

    val overlapDepthPx = max(0f, -gapPx)
    val safeBandPx = max(1f, shallowOverlapBandPx)
    val overlapDepthFraction = (overlapDepthPx / safeBandPx).coerceIn(0f, 1f)
    val overlapRamp = smoothstep(overlapDepthFraction)

    val minScale = minStretchScaleAtTouch.coerceIn(0f, 1f)
    val shallowOverlapStretchScale = minScale + (1f - minScale) * overlapRamp

    val baseStretchPx = 60f * stretchAmount.value
    val effectiveStretchPx = baseStretchPx * shallowOverlapStretchScale
    val neckFocusPower = 6.5f

    val attachedStretchPx = if (isAttached) effectiveStretchPx else 0f
    val detachedWobblePx = if (!isAttached) detachWobbleMaxPx * detachWobble.value else 0f

    // Dynamic stretches while attached, wobbles after detach
    val dynamicDrivePx = attachedStretchPx + detachedWobblePx

    // Static does NOT stretch while attached; wobbles after detach only
    val staticDrivePx = detachedWobblePx * 0.9f

    val phase =
        if (!isAttached) StretchPhase.None
        else if (!(isAttached && isPullingApart) || targetStretch <= 0f) StretchPhase.None
        else if (bridgeStrength <= 0f) StretchPhase.Early else StretchPhase.Late

    var geometryResult by remember { mutableStateOf<GooeyGeometryResult?>(null) }

    LaunchedEffect(
        dynamicCenter,
        staticCenter,
        dynamicRadiusPx,
        staticCircleRadiusPx,
        staticRectWidthPx,
        staticRectHeightPx,
        staticRectCornerRadiusPx,
        staticShape,
        dynamicFacingAngleRad,
        staticFacingAngleRad,
        dynamicDrivePx,
        staticDrivePx,
        neckFocusPower,
        bridgeStrength,
        bridgeThicknessMaxPx,
        bridgeThicknessMinPx,
        bridgeHandleScale,
    ) {
        geometryResult = withContext(Dispatchers.Default) {
            val dynamicPoints = buildStretchedBlobPoints(
                center = dynamicCenter,
                baseRadiusPx = dynamicRadiusPx,
                samplePointCount = 140,
                stretchPx = dynamicDrivePx,
                facingAngleRad = dynamicFacingAngleRad,
                neckFocusPower = neckFocusPower,
                stretchBias = 1.0f
            )

            val staticBase = buildStaticBoundarySamplePoints(
                center = staticCenter,
                shape = staticShape,
                circleRadiusPx = staticCircleRadiusPx,
                rectWidthPx = staticRectWidthPx,
                rectHeightPx = staticRectHeightPx,
                cornerRadiusPx = staticRectCornerRadiusPx,
                samplePointCount = 220
            )

            val staticPoints = deformAlongBoundaryNormal(
                center = staticCenter,
                basePoints = staticBase,
                stretchPx = staticDrivePx,
                facingAngleRad = staticFacingAngleRad,
                neckFocusPower = neckFocusPower,
                stretchBias = 0.9f
            )

            val ligament =
                if (isAttached && bridgeStrength > 0f) {
                    computeLigamentFromPointArrays(
                        firstCenter = dynamicCenter,
                        firstPoints = dynamicPoints,
                        firstFacingAngleRad = dynamicFacingAngleRad,

                        secondCenter = staticCenter,
                        secondPoints = staticPoints,
                        secondFacingAngleRad = staticFacingAngleRad,

                        strength = bridgeStrength,
                        bridgeThicknessMaxPx = bridgeThicknessMaxPx,
                        bridgeThicknessMinPx = bridgeThicknessMinPx,
                        bridgeHandleScale = bridgeHandleScale
                    )
                } else null

            GooeyGeometryResult(dynamicPoints, staticPoints, ligament)
        }
    }

    SideEffect {
        val ligament = geometryResult?.ligament
        onDebugState(
            GooeyDebugState(
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
                detectDragGestures(onDragStart = { pointerPosition = it }) { change, _ ->
                    pointerPosition = change.position
                    change.consume()
                }
            }
    ) {
        val geom = geometryResult ?: return@Canvas

        dynamicBlobPath.reset()
        fillPathFromPoints(dynamicBlobPath, geom.dynamicPoints)

        staticBlobPath.reset()
        fillPathFromPoints(staticBlobPath, geom.staticPoints)

        intersectionPath.reset()
        intersectionPath.op(dynamicBlobPath, staticBlobPath, PathOperation.Intersect)

        ligamentPath.reset()
        clippedLigamentPath.reset()

        geom.ligament?.let { lg ->
            ligamentPath.moveTo(lg.firstTop.x, lg.firstTop.y)
            ligamentPath.cubicTo(
                lg.firstTopControl.x, lg.firstTopControl.y,
                lg.secondTopControl.x, lg.secondTopControl.y,
                lg.secondTop.x, lg.secondTop.y
            )
            ligamentPath.lineTo(lg.secondBottom.x, lg.secondBottom.y)
            ligamentPath.cubicTo(
                lg.secondBottomControl.x, lg.secondBottomControl.y,
                lg.firstBottomControl.x, lg.firstBottomControl.y,
                lg.firstBottom.x, lg.firstBottom.y
            )
            ligamentPath.close()

            // Clip ligament to overlap
            if (!intersectionPath.isEmpty) {
                clippedLigamentPath.op(ligamentPath, intersectionPath, PathOperation.Intersect)
            }
        }

        unionPath.reset()
        unionPath.op(dynamicBlobPath, staticBlobPath, PathOperation.Union)

        if (!clippedLigamentPath.isEmpty) {
            tmpPathA.reset()
            tmpPathA.op(unionPath, clippedLigamentPath, PathOperation.Union)
            unionPath.reset()
            unionPath.addPath(tmpPathA)
        }

        if (debugEnabled) {
            if (debugDrawLigament && !clippedLigamentPath.isEmpty) {
                drawPath(clippedLigamentPath, Color.Cyan, style = Stroke(width = 3f))
            }
            if (!intersectionPath.isEmpty) {
                drawPath(intersectionPath, Color(0x55FFFF00), style = Stroke(width = 2f))
            }
            if (debugDrawVectors) {
                // show contact direction
                drawLine(Color.White, dynamicCenter, staticContactPoint, strokeWidth = 1.5f)
            }
        }

        drawIntoCanvas { canvas ->
            canvas.drawPath(unionPath, paint)
        }
    }
}

// -------------------- Signed distance + closest point helpers --------------------

private fun signedDistanceBox(p: Offset, hx: Float, hy: Float): Float {
    // axis-aligned box centered at origin with half extents hx, hy
    val dx = abs(p.x) - hx
    val dy = abs(p.y) - hy
    val ax = max(dx, 0f)
    val ay = max(dy, 0f)
    val outside = sqrt(ax * ax + ay * ay)
    val inside = min(max(dx, dy), 0f)
    return outside + inside
}

private fun signedDistanceRoundedBox(p: Offset, hx: Float, hy: Float, r: Float): Float {
    // rounded box = box shrunk by r, then distance - r
    val qx = abs(p.x) - (hx - r)
    val qy = abs(p.y) - (hy - r)
    val ax = max(qx, 0f)
    val ay = max(qy, 0f)
    val outside = sqrt(ax * ax + ay * ay)
    val inside = min(max(qx, qy), 0f)
    return outside + inside - r
}

private fun closestPointOnBox(p: Offset, hx: Float, hy: Float): Offset {
    // closest point on boundary of axis-aligned box centered at origin
    val cx = p.x.coerceIn(-hx, hx)
    val cy = p.y.coerceIn(-hy, hy)

    // if inside, push to nearest edge
    val inside = abs(p.x) <= hx && abs(p.y) <= hy
    if (!inside) return Offset(cx, cy)

    val dx = hx - abs(p.x)
    val dy = hy - abs(p.y)
    return if (dx < dy) {
        Offset(hx * sign(p.x.takeIf { it != 0f } ?: 1f), p.y.coerceIn(-hy, hy))
    } else {
        Offset(p.x.coerceIn(-hx, hx), hy * sign(p.y.takeIf { it != 0f } ?: 1f))
    }
}

private fun closestPointOnRoundedBox(p: Offset, hx: Float, hy: Float, r: Float): Offset {
    // boundary of rounded rect centered at origin
    // 1) clamp to inner box (hx-r, hy-r)
    val ix = (hx - r).coerceAtLeast(0f)
    val iy = (hy - r).coerceAtLeast(0f)

    val clamped = Offset(p.x.coerceIn(-ix, ix), p.y.coerceIn(-iy, iy))
    val delta = p - clamped

    // if outside inner box => project to corner arc
    val len = delta.getDistance()
    return if (len > 1e-4f) {
        clamped + (delta / len) * r
    } else {
        // inside inner box: project to nearest straight segment (then no arc offset)
        val bx = p.x.coerceIn(-hx, hx)
        val by = p.y.coerceIn(-hy, hy)

        val inside = abs(p.x) <= ix && abs(p.y) <= iy
        if (!inside) Offset(bx, by) // rare edge case
        else {
            val dx = ix - abs(p.x)
            val dy = iy - abs(p.y)
            if (dx < dy) Offset(ix * sign(p.x.takeIf { it != 0f } ?: 1f), p.y.coerceIn(-iy, iy))
            else Offset(p.x.coerceIn(-ix, ix), iy * sign(p.y.takeIf { it != 0f } ?: 1f))
        }
    }
}

// -------------------- stretch + static sampling + ligament (same as before) --------------------

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
    val angleStepRad = (2.0 * PI / samplePointCount).toFloat()

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

    for (i in 0..samplePointCount) {
        val angleRad = i * angleStepRad
        val r = radiusAtAngle(angleRad)
        val idx = i * 2
        points[idx] = center.x + r * cos(angleRad)
        points[idx + 1] = center.y + r * sin(angleRad)
    }

    return points
}

private fun buildStaticBoundarySamplePoints(
    center: Offset,
    shape: StaticBlobShape,
    circleRadiusPx: Float,
    rectWidthPx: Float,
    rectHeightPx: Float,
    cornerRadiusPx: Float,
    samplePointCount: Int
): FloatArray {
    val n = max(32, samplePointCount)
    val points = FloatArray((n + 1) * 2)

    when (shape) {
        StaticBlobShape.Circle -> {
            val step = (2.0 * PI / n).toFloat()
            for (i in 0..n) {
                val a = i * step
                val idx = i * 2
                points[idx] = center.x + circleRadiusPx * cos(a)
                points[idx + 1] = center.y + circleRadiusPx * sin(a)
            }
        }

        StaticBlobShape.Rect -> {
            val hw = rectWidthPx * 0.5f
            val hh = rectHeightPx * 0.5f
            val per = 2f * (rectWidthPx + rectHeightPx)
            for (i in 0..n) {
                val s = (i.toFloat() / n) * per
                val (x, y) = pointOnRectPerimeter(center, hw, hh, s)
                val idx = i * 2
                points[idx] = x
                points[idx + 1] = y
            }
        }

        StaticBlobShape.RoundedRect -> {
            val hw = rectWidthPx * 0.5f
            val hh = rectHeightPx * 0.5f
            val r = cornerRadiusPx.coerceIn(0f, min(hw, hh))

            val straightW = max(0f, rectWidthPx - 2f * r)
            val straightH = max(0f, rectHeightPx - 2f * r)
            val arcLen = (0.5f * PI.toFloat()) * r
            val per = 2f * (straightW + straightH) + 4f * arcLen

            for (i in 0..n) {
                val s = (i.toFloat() / n) * per
                val p = pointOnRoundedRectPerimeter(center, hw, hh, r, straightW, straightH, s)
                val idx = i * 2
                points[idx] = p.x
                points[idx + 1] = p.y
            }
        }
    }

    return points
}

private fun pointOnRectPerimeter(center: Offset, hw: Float, hh: Float, s: Float): Pair<Float, Float> {
    val w = 2f * hw
    val h = 2f * hh
    val per = 2f * (w + h)
    var d = ((s % per) + per) % per

    if (d <= w) return (center.x - hw + d) to (center.y - hh)
    d -= w
    if (d <= h) return (center.x + hw) to (center.y - hh + d)
    d -= h
    if (d <= w) return (center.x + hw - d) to (center.y + hh)
    d -= w
    return (center.x - hw) to (center.y + hh - d)
}

private fun pointOnRoundedRectPerimeter(
    center: Offset,
    hw: Float,
    hh: Float,
    r: Float,
    straightW: Float,
    straightH: Float,
    s: Float
): Offset {
    val arcLen = (0.5f * PI.toFloat()) * r
    val per = 2f * (straightW + straightH) + 4f * arcLen
    var d = ((s % per) + per) % per

    val left = center.x - hw
    val right = center.x + hw
    val top = center.y - hh
    val bottom = center.y + hh

    val tlc = Offset(left + r, top + r)
    val trc = Offset(right - r, top + r)
    val brc = Offset(right - r, bottom - r)
    val blc = Offset(left + r, bottom - r)

    if (d <= straightW) return Offset(tlc.x + d, top)
    d -= straightW

    if (d <= arcLen) {
        val t = d / arcLen
        val ang = (-PI / 2.0 + (PI / 2.0) * t).toFloat()
        return trc + Offset(r * cos(ang), r * sin(ang))
    }
    d -= arcLen

    if (d <= straightH) return Offset(right, trc.y + d)
    d -= straightH

    if (d <= arcLen) {
        val t = d / arcLen
        val ang = (0.0 + (PI / 2.0) * t).toFloat()
        return brc + Offset(r * cos(ang), r * sin(ang))
    }
    d -= arcLen

    if (d <= straightW) return Offset(brc.x - d, bottom)
    d -= straightW

    if (d <= arcLen) {
        val t = d / arcLen
        val ang = (PI / 2.0 + (PI / 2.0) * t).toFloat()
        return blc + Offset(r * cos(ang), r * sin(ang))
    }
    d -= arcLen

    if (d <= straightH) return Offset(left, blc.y - d)
    d -= straightH

    val t = (d / arcLen).coerceIn(0f, 1f)
    val ang = (PI + (PI / 2.0) * t).toFloat()
    return tlc + Offset(r * cos(ang), r * sin(ang))
}

private fun deformAlongBoundaryNormal(
    center: Offset,
    basePoints: FloatArray,
    stretchPx: Float,
    facingAngleRad: Float,
    neckFocusPower: Float,
    stretchBias: Float
): FloatArray {
    val n = (basePoints.size / 2) - 1
    if (n < 8) return basePoints

    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

    fun neckMask(thetaRad: Float): Float {
        val facingCos = cos(thetaRad - facingAngleRad).coerceIn(-1f, 1f)
        val facingFraction = (facingCos + 1f) * 0.5f
        val smoothedFacing = smoothstep(facingFraction)
        return smoothedFacing.pow(neckFocusPower)
    }

    fun pAt(i: Int): Offset {
        val ii = ((i % n) + n) % n
        val idx = ii * 2
        return Offset(basePoints[idx], basePoints[idx + 1])
    }

    fun tangentAt(i: Int): Offset {
        val prev = pAt(i - 1)
        val next = pAt(i + 1)
        return normalizeSafe(next - prev)
    }

    val out = FloatArray(basePoints.size)
    for (i in 0 until n) {
        val p = pAt(i)
        val t = tangentAt(i)

        var normal = Offset(-t.y, t.x)
        if (dot(normal, p - center) < 0f) normal = -normal

        val v = p - center
        val theta = atan2(v.y, v.x)

        val mask = neckMask(theta)
        val neckInflationPx = (stretchPx * stretchBias) * mask
        val farMask = 1f - mask
        val farCompressionPx = -0.22f * stretchPx * farMask
        val disp = neckInflationPx + farCompressionPx

        val q = p + normal * disp
        val idx = i * 2
        out[idx] = q.x
        out[idx + 1] = q.y
    }

    out[n * 2] = out[0]
    out[n * 2 + 1] = out[1]
    return out
}

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

private data class LigamentGeometry(
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

private data class AnchorPair(
    val top: Offset,
    val bottom: Offset,
    val topTangentUnit: Offset,
    val bottomTangentUnit: Offset
)

private fun normalizeSafe(v: Offset): Offset {
    val d = v.getDistance()
    return if (d > 1e-4f) v / d else Offset(1f, 0f)
}

private fun dot(a: Offset, b: Offset): Float = a.x * b.x + a.y * b.y
private fun cross(a: Offset, b: Offset): Float = a.x * b.y - a.y * b.x

private fun anchorsFromPolyline(
    center: Offset,
    points: FloatArray,
    facingAngleRad: Float,
    halfThicknessPx: Float
): AnchorPair {
    val n = (points.size / 2) - 1
    if (n < 8) {
        val p = Offset(points.getOrElse(0) { 0f }, points.getOrElse(1) { 0f })
        val t = Offset(1f, 0f)
        return AnchorPair(p, p, t, t)
    }

    val facingDir = Offset(cos(facingAngleRad), sin(facingAngleRad))

    fun pAt(i: Int): Offset {
        val ii = ((i % n) + n) % n
        val idx = ii * 2
        return Offset(points[idx], points[idx + 1])
    }

    var bestI = 0
    var bestScore = -Float.MAX_VALUE
    for (i in 0 until n) {
        val p = pAt(i)
        val v = normalizeSafe(p - center)
        val s = dot(v, facingDir)
        if (s > bestScore) {
            bestScore = s
            bestI = i
        }
    }

    val targetChord = max(1f, 2f * halfThicknessPx)

    var k = 1
    val maxK = n / 4
    while (k < maxK) {
        val a = pAt(bestI - k)
        val b = pAt(bestI + k)
        val chord = (b - a).getDistance()
        if (chord >= targetChord) break
        k++
    }
    k = k.coerceIn(1, max(1, maxK - 1))

    val a0 = pAt(bestI - k)
    val b0 = pAt(bestI + k)

    val aRad = normalizeSafe(a0 - center)
    val bRad = normalizeSafe(b0 - center)
    val aSide = cross(facingDir, aRad)
    val bSide = cross(facingDir, bRad)

    val top = if (aSide >= bSide) a0 else b0
    val bottom = if (aSide >= bSide) b0 else a0

    fun tangentAt(i: Int): Offset {
        val pPrev = pAt(i - 1)
        val pNext = pAt(i + 1)
        return normalizeSafe(pNext - pPrev)
    }

    val topIndex = if (aSide >= bSide) (bestI - k) else (bestI + k)
    val bottomIndex = if (aSide >= bSide) (bestI + k) else (bestI - k)

    return AnchorPair(top, bottom, tangentAt(topIndex), tangentAt(bottomIndex))
}

private fun computeLigamentFromPointArrays(
    firstCenter: Offset,
    firstPoints: FloatArray,
    firstFacingAngleRad: Float,

    secondCenter: Offset,
    secondPoints: FloatArray,
    secondFacingAngleRad: Float,

    strength: Float,
    bridgeThicknessMaxPx: Float,
    bridgeThicknessMinPx: Float,
    bridgeHandleScale: Float
): LigamentGeometry {
    fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)
    fun clamp01(x: Float): Float = x.coerceIn(0f, 1f)

    val strengthSmoothed = smoothstep(clamp01(strength))

    val ligamentThicknessPx =
        bridgeThicknessMaxPx + (bridgeThicknessMinPx - bridgeThicknessMaxPx) * strengthSmoothed

    val first = anchorsFromPolyline(firstCenter, firstPoints, firstFacingAngleRad, ligamentThicknessPx)
    val second = anchorsFromPolyline(secondCenter, secondPoints, secondFacingAngleRad, ligamentThicknessPx)

    val topSpanPx = (second.top - first.top).getDistance()
    val bottomSpanPx = (first.bottom - second.bottom).getDistance()
    val minSpanPx = min(topSpanPx, bottomSpanPx)

    val maxHandlePx = max(6f, minSpanPx * 0.35f)
    val baseHandlePx = (minSpanPx * 0.30f).coerceIn(6f, maxHandlePx)
    val handleLengthPx = (baseHandlePx * bridgeHandleScale).coerceIn(1f, maxHandlePx)

    fun oriented(t: Offset, wantDir: Offset): Offset {
        val tu = normalizeSafe(t)
        return if (dot(tu, wantDir) < 0f) -tu else tu
    }

    val topBridgeDir = normalizeSafe(second.top - first.top)
    val bottomBridgeDir = normalizeSafe(first.bottom - second.bottom)

    val firstTopTan = oriented(first.topTangentUnit, topBridgeDir)
    val secondTopTan = oriented(second.topTangentUnit, -topBridgeDir)

    val secondBottomTan = oriented(second.bottomTangentUnit, -bottomBridgeDir)
    val firstBottomTan = oriented(first.bottomTangentUnit, bottomBridgeDir)

    val firstTopControl = first.top + firstTopTan * handleLengthPx
    val secondTopControl = second.top + secondTopTan * handleLengthPx

    val secondBottomControl = second.bottom + secondBottomTan * handleLengthPx
    val firstBottomControl = first.bottom + firstBottomTan * handleLengthPx

    return LigamentGeometry(
        firstCenter = firstCenter,
        secondCenter = secondCenter,
        firstTop = first.top,
        firstBottom = first.bottom,
        secondTop = second.top,
        secondBottom = second.bottom,
        firstTopControl = firstTopControl,
        secondTopControl = secondTopControl,
        secondBottomControl = secondBottomControl,
        firstBottomControl = firstBottomControl,
        ligamentThicknessPx = ligamentThicknessPx,
        handleLengthPx = handleLengthPx
    )
}

@Composable
private fun DebugToggleText(label: String, enabled: Boolean, onToggle: () -> Unit) {
    Text(
        text = if (enabled) "[$label]" else label,
        color = if (enabled) Color(0xFF7CFFB2) else Color(0xFFB0B0B0),
        modifier = Modifier
            .background(Color(0x33000000))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .clickable { onToggle() }
    )
}

@Preview(showBackground = true, widthDp = 420, heightDp = 720)
@Composable
private fun GooeyStretchAndSnapSample_Preview() {
    GooeyStretchAndSnapSample(Modifier.fillMaxSize())
}