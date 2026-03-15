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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
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

/**
 * Available shapes for the static (non-draggable) blob.
 */
enum class StaticShape { Circle, Rect, RoundedRect }

/**
 * Available shapes for the dynamic (pointer-following) blob.
 */
enum class DynamicShape { Circle, Rect, RoundedRect }

/**
 * All tunable parameters for the gooey union computation.
 *
 * @param dynamicShape geometry type of the pointer-following blob
 * @param dynamicCircleRadiusPx radius when [dynamicShape] is [DynamicShape.Circle]
 * @param dynamicRectWidthPx width when [dynamicShape] is Rect or RoundedRect
 * @param dynamicRectHeightPx height when [dynamicShape] is Rect or RoundedRect
 * @param dynamicCornerPx corner radius when [dynamicShape] is [DynamicShape.RoundedRect]
 * @param staticShape geometry type of the stationary blob
 * @param staticCircleRadiusPx radius when [staticShape] is [StaticShape.Circle]
 * @param staticRectWidthPx width when [staticShape] is Rect or RoundedRect
 * @param staticRectHeightPx height when [staticShape] is Rect or RoundedRect
 * @param staticCornerPx corner radius when [staticShape] is [StaticShape.RoundedRect]
 * @param minStretchScaleAtTouch multiplier that limits stretch when shapes barely overlap
 * @param shallowOverlapBandPx depth band over which the overlap ramp goes from 0 to 1
 * @param bridgeThicknessMaxPx ligament half-width at zero bridge strength
 * @param bridgeThicknessMinPx ligament half-width at full bridge strength
 * @param bridgeHandleScale multiplier applied to Bezier handle length of the ligament
 * @param detachWobbleMaxPx peak amplitude of the spring wobble played on detach
 * @param dynamicSampleCount number of boundary samples for the dynamic shape
 * @param staticSampleCount number of boundary samples for the static shape
 * @param neckFocusPower exponent that sharpens the neck deformation mask
 * @param baseStretchPx maximum stretch displacement in pixels at full pull
 * @param stretchBandPx gap range over which stretch ramps from 0 to 1
 * @param bridgeBandPx gap range over which bridge strength ramps from 0 to 1
 * @param detachGapThresholdPx positive gap at which the shapes detach
 * @param attachGapThresholdPx negative gap at which the shapes re-attach
 */
data class GooeyUnionParams(
    val dynamicShape: DynamicShape = DynamicShape.Circle,
    val dynamicCircleRadiusPx: Float = 100f,
    val dynamicRectWidthPx: Float = 220f,
    val dynamicRectHeightPx: Float = 220f,
    val dynamicCornerPx: Float = 60f,

    val staticShape: StaticShape = StaticShape.RoundedRect,
    val staticCircleRadiusPx: Float = 150f,
    val staticRectWidthPx: Float = 650f,
    val staticRectHeightPx: Float = 200f,
    val staticCornerPx: Float = 90f,

    val minStretchScaleAtTouch: Float = 0.60f,
    val shallowOverlapBandPx: Float = 100f,
    val bridgeThicknessMaxPx: Float = 32f,
    val bridgeThicknessMinPx: Float = 2.5f,
    val bridgeHandleScale: Float = 1.0f,
    val detachWobbleMaxPx: Float = 18f,

    val dynamicSampleCount: Int = 170,
    val staticSampleCount: Int = 240,

    val neckFocusPower: Float = 6.5f,
    val baseStretchPx: Float = 60f,
    val stretchBandPx: Float = 90f,
    val bridgeBandPx: Float = 28f,

    val detachGapThresholdPx: Float = 0.5f,
    val attachGapThresholdPx: Float = -1.5f,
)

/**
 * Output produced by [computeGooeyUnion].
 *
 * All [Path] objects are created during computation on a background thread.
 * They are safe to read from the main thread once received, but must not
 * be mutated by the caller.
 *
 * @param unionPath the final merged path (dynamic + static + clipped ligament)
 * @param dynamicPath the deformed dynamic blob path on its own
 * @param staticPath the deformed static blob path on its own
 * @param isAttached whether the two blobs are currently considered attached
 * @param gapPx signed distance between the two shape boundaries (negative means overlapping)
 */
data class GooeyUnionResult(
    val unionPath: Path,
    val dynamicPath: Path,
    val staticPath: Path,
    val isAttached: Boolean,
    val gapPx: Float,
)

/**
 * Pure computation that builds the gooey union [Path] from a static shape
 * at [staticCenter] and a dynamic shape at [dynamicCenter].
 *
 * This function is thread-safe and creates all [Path] objects internally.
 * Call it from any dispatcher; the returned paths are owned by the caller.
 *
 * @param params all shape, stretch, bridge and sampling parameters
 * @param staticCenter world-space center of the stationary blob
 * @param dynamicCenter world-space center of the pointer-following blob
 * @param isAttached current attach/detach state
 * @param isPullingApart whether the user is actively dragging away
 * @param stretchAmount current animated stretch factor in 0..1
 * @param detachWobbleAmount current animated wobble factor in 0..1
 * @return [GooeyUnionResult] containing newly-created paths and state info
 */
fun computeGooeyUnion(
    params: GooeyUnionParams,
    staticCenter: Offset,
    dynamicCenter: Offset,
    isAttached: Boolean,
    isPullingApart: Boolean,
    stretchAmount: Float,
    detachWobbleAmount: Float,
): GooeyUnionResult {
    val dynamicPath = Path()
    val staticPath = Path()
    val ligamentPath = Path()
    val unionPath = Path()

    with(params) {
        val dCorner = dynamicCornerPx.coerceIn(
            0f, min(dynamicRectWidthPx * 0.5f, dynamicRectHeightPx * 0.5f)
        )
        val sHalfW = staticRectWidthPx * 0.5f
        val sHalfH = staticRectHeightPx * 0.5f
        val sCorner = staticCornerPx.coerceIn(0f, min(sHalfW, sHalfH))
        val dHalfW = dynamicRectWidthPx * 0.5f
        val dHalfH = dynamicRectHeightPx * 0.5f

        val pLocal = dynamicCenter - staticCenter
        val sdStatic = when (staticShape) {
            StaticShape.Circle ->
                (dynamicCenter - staticCenter).getDistance() - staticCircleRadiusPx

            StaticShape.Rect ->
                signedDistanceBox(pLocal, sHalfW, sHalfH)

            StaticShape.RoundedRect ->
                signedDistanceRoundedBox(pLocal, sHalfW, sHalfH, sCorner)
        }

        val staticContactPoint = when (staticShape) {
            StaticShape.Circle -> {
                val dir = normalizeSafe(dynamicCenter - staticCenter)
                staticCenter + dir * staticCircleRadiusPx
            }

            StaticShape.Rect ->
                staticCenter + closestPointOnBox(pLocal, sHalfW, sHalfH)

            StaticShape.RoundedRect ->
                staticCenter + closestPointOnRoundedBox(pLocal, sHalfW, sHalfH, sCorner)
        }

        val dynToContactDir = normalizeSafe(staticContactPoint - dynamicCenter)
        val dynamicSupportPx = when (dynamicShape) {
            DynamicShape.Circle -> dynamicCircleRadiusPx
            DynamicShape.Rect -> rayDistanceToBox(dynToContactDir, dHalfW, dHalfH)
            DynamicShape.RoundedRect ->
                rayDistanceToRoundedBox(dynToContactDir, dHalfW, dHalfH, dCorner)
        }
        val gapPx = sdStatic - dynamicSupportPx

        val staticOutwardNormal = when (staticShape) {
            StaticShape.Circle ->
                normalizeSafe(staticContactPoint - staticCenter)

            StaticShape.Rect ->
                outwardNormalOnBox(staticContactPoint - staticCenter, sHalfW, sHalfH)

            StaticShape.RoundedRect ->
                outwardNormalOnRoundedBox(
                    staticContactPoint - staticCenter, sHalfW, sHalfH, sCorner
                )
        }
        val staticToDynamic = dynamicCenter - staticContactPoint
        val staFacingDir = oriented(staticOutwardNormal, staticToDynamic)
        val dynFacingDir = normalizeSafe(staticContactPoint - dynamicCenter)

        val dynamicFacingAngle = angleOf(dynFacingDir)
        val staticFacingAngle = angleOf(staFacingDir)

        val targetStretch =
            if (isAttached && isPullingApart) {
                smoothstep(((gapPx + stretchBandPx) / stretchBandPx).coerceIn(0f, 1f))
            } else 0f

        val bridgeStrength =
            if (isAttached && isPullingApart && gapPx < 0f && gapPx > -bridgeBandPx) {
                smoothstep(((gapPx + bridgeBandPx) / bridgeBandPx).coerceIn(0f, 1f))
            } else 0f

        val overlapDepthPx = max(0f, -gapPx)
        val overlapFrac = (overlapDepthPx / max(1f, shallowOverlapBandPx)).coerceIn(0f, 1f)
        val overlapRamp = smoothstep(overlapFrac)
        val minScale = minStretchScaleAtTouch.coerceIn(0f, 1f)
        val shallowScale = minScale + (1f - minScale) * overlapRamp

        val effectiveStretch = baseStretchPx * stretchAmount * shallowScale
        val attachedStretch = if (isAttached) effectiveStretch else 0f
        val detachedWobble = if (!isAttached) detachWobbleMaxPx * detachWobbleAmount else 0f

        val dynamicDrive = attachedStretch + detachedWobble
        val staticDrive = detachedWobble * 0.9f

        val dynBase = buildBoundarySamplePointsForDynamic(
            center = dynamicCenter,
            shape = dynamicShape,
            circleRadiusPx = dynamicCircleRadiusPx,
            rectWidthPx = dynamicRectWidthPx,
            rectHeightPx = dynamicRectHeightPx,
            cornerRadiusPx = dCorner,
            samplePointCount = dynamicSampleCount
        )
        val dynPoints = deformAlongBoundaryNormal(
            center = dynamicCenter,
            basePoints = dynBase,
            stretchPx = dynamicDrive,
            facingAngleRad = dynamicFacingAngle,
            neckFocusPower = neckFocusPower,
            stretchBias = 1.0f
        )

        val staBase = buildStaticBoundarySamplePoints(
            center = staticCenter,
            shape = staticShape,
            circleRadiusPx = staticCircleRadiusPx,
            rectWidthPx = staticRectWidthPx,
            rectHeightPx = staticRectHeightPx,
            cornerRadiusPx = sCorner,
            samplePointCount = staticSampleCount
        )
        val staPoints = deformAlongBoundaryNormal(
            center = staticCenter,
            basePoints = staBase,
            stretchPx = staticDrive,
            facingAngleRad = staticFacingAngle,
            neckFocusPower = neckFocusPower,
            stretchBias = 0.9f
        )

        fillPathFromPoints(dynamicPath, dynPoints)
        fillPathFromPoints(staticPath, staPoints)

        val intersectionPath = Path()
        intersectionPath.op(dynamicPath, staticPath, PathOperation.Intersect)

        if (isAttached && bridgeStrength > 0f) {
            val lg = computeLigamentFromPointArrays(
                firstCenter = dynamicCenter,
                firstPoints = dynPoints,
                firstFacingDir = dynFacingDir,
                secondCenter = staticCenter,
                secondPoints = staPoints,
                secondFacingDir = staFacingDir,
                strength = bridgeStrength,
                bridgeThicknessMaxPx = bridgeThicknessMaxPx,
                bridgeThicknessMinPx = bridgeThicknessMinPx,
                bridgeHandleScale = bridgeHandleScale
            )
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
        }

        unionPath.op(dynamicPath, staticPath, PathOperation.Union)

        if (!ligamentPath.isEmpty && !intersectionPath.isEmpty) {
            val clipped = Path()
            clipped.op(ligamentPath, intersectionPath, PathOperation.Intersect)
            if (!clipped.isEmpty) {
                val tmp = Path()
                tmp.op(unionPath, clipped, PathOperation.Union)
                unionPath.reset()
                unionPath.addPath(tmp)
            }
        }

        return GooeyUnionResult(
            unionPath = unionPath,
            dynamicPath = dynamicPath,
            staticPath = staticPath,
            isAttached = isAttached,
            gapPx = gapPx,
        )
    }
}

/**
 * Snapshot of every value that feeds into the background computation.
 * Used by [snapshotFlow] inside [rememberGooeyUnionState] to detect changes.
 */
private data class ComputeInput(
    val params: GooeyUnionParams,
    val staticCenter: Offset,
    val dynamicCenter: Offset,
    val isAttached: Boolean,
    val isPullingApart: Boolean,
    val stretchAmount: Float,
    val wobbleAmount: Float,
)

/**
 * Observable state holder for the gooey union effect.
 *
 * Set [params], [staticCenter] and [dynamicCenter] from the outside;
 * the state machine (attach / detach, pull detection, wobble) is managed
 * internally, and the heavy path computation runs on [Dispatchers.Default]
 * via a Flow pipeline wired up by [rememberGooeyUnionState].
 *
 * Read [result] from any draw lambda ([DrawScope], `Modifier.drawBehind`,
 * `Canvas`) to get the latest computed paths.
 */
@Stable
class GooeyUnionState internal constructor(initialParams: GooeyUnionParams) {

    /** All shape, stretch, bridge and sampling parameters. */
    var params by mutableStateOf(initialParams)

    /** World-space center of the stationary blob. */
    var staticCenter by mutableStateOf(Offset.Zero)

    /** World-space center of the pointer-following blob. */
    var dynamicCenter by mutableStateOf(Offset.Zero)

    /**
     * Latest computation result. `null` until the first frame completes.
     * Reading this inside a draw lambda will schedule a redraw whenever
     * a new result arrives from the background pipeline.
     */
    var result: GooeyUnionResult? by mutableStateOf(null)
        internal set

    /**
     * Exposes [result] as a cold [Flow] for callers that prefer
     * collecting in a coroutine rather than reading Compose state.
     */
    val resultFlow: Flow<GooeyUnionResult?>
        get() = snapshotFlow { result }

    internal var isAttached by mutableStateOf(true)
    internal var isPullingApart by mutableStateOf(false)
    internal var previousGapPx by mutableFloatStateOf(Float.NaN)
    internal var gapVelocityEma by mutableFloatStateOf(0f)
    internal val stretchAnim = Animatable(0f)
    internal val wobbleAnim = Animatable(0f)
    internal var prevAttached by mutableStateOf(true)
}

/**
 * Creates and remembers a [GooeyUnionState] whose background computation
 * pipeline and state machine are kept alive for the lifetime of the caller.
 *
 * Internally uses [snapshotFlow] to observe all inputs, runs
 * [computeGooeyUnion] on [Dispatchers.Default] via [mapLatest],
 * and writes the result back to [GooeyUnionState.result].
 *
 * @param initialParams initial set of tuning parameters
 * @return a [GooeyUnionState] you can feed positions into and read paths from
 */
@Composable
fun rememberGooeyUnionState(
    initialParams: GooeyUnionParams = GooeyUnionParams()
): GooeyUnionState {
    val state = remember { GooeyUnionState(initialParams) }

    // Gap is cheap to compute on main thread; the state machine needs it every frame
    val gapPx = remember(
        state.params, state.staticCenter, state.dynamicCenter
    ) {
        computeGapPx(state.params, state.staticCenter, state.dynamicCenter)
    }

    // Attach / detach hysteresis
    LaunchedEffect(gapPx) {
        state.isAttached = when {
            state.isAttached && gapPx > state.params.detachGapThresholdPx -> false
            !state.isAttached && gapPx < state.params.attachGapThresholdPx -> true
            else -> state.isAttached
        }
    }

    // Pull-apart velocity detection
    LaunchedEffect(gapPx) {
        val old = state.previousGapPx
        if (!old.isNaN()) {
            val delta = gapPx - old
            state.gapVelocityEma += (delta - state.gapVelocityEma) * 0.25f
        }
        state.previousGapPx = gapPx
        state.isPullingApart = when {
            state.isPullingApart && state.gapVelocityEma < -0.20f -> false
            !state.isPullingApart && state.gapVelocityEma > 0.20f -> true
            else -> state.isPullingApart
        }
    }

    // Stretch tracking
    val targetStretch =
        if (state.isAttached && state.isPullingApart)
            smoothstep(
                ((gapPx + state.params.stretchBandPx) / state.params.stretchBandPx)
                    .coerceIn(0f, 1f)
            )
        else 0f

    LaunchedEffect(state.isAttached, targetStretch) {
        if (state.isAttached) state.stretchAnim.snapTo(targetStretch)
        else state.stretchAnim.snapTo(0f)
    }

    // Detach wobble spring
    LaunchedEffect(state.isAttached) {
        if (state.prevAttached && !state.isAttached) {
            state.wobbleAnim.snapTo(1f)
            state.wobbleAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.35f, stiffness = 280f)
            )
            state.isPullingApart = false
            state.gapVelocityEma = 0f
        }
        state.prevAttached = state.isAttached
    }

    // Background computation pipeline
    LaunchedEffect(Unit) {
        snapshotFlow {
            ComputeInput(
                params = state.params,
                staticCenter = state.staticCenter,
                dynamicCenter = state.dynamicCenter,
                isAttached = state.isAttached,
                isPullingApart = state.isPullingApart,
                stretchAmount = state.stretchAnim.value,
                wobbleAmount = state.wobbleAnim.value,
            )
        }
            .mapLatest { input ->
                withContext(Dispatchers.Default) {
                    computeGooeyUnion(
                        params = input.params,
                        staticCenter = input.staticCenter,
                        dynamicCenter = input.dynamicCenter,
                        isAttached = input.isAttached,
                        isPullingApart = input.isPullingApart,
                        stretchAmount = input.stretchAmount,
                        detachWobbleAmount = input.wobbleAmount,
                    )
                }
            }
            .collect { result -> state.result = result }
    }

    return state
}

/**
 * Draws [GooeyUnionState.result]'s union path behind the modified content.
 *
 * Reading [GooeyUnionState.result] inside the draw lambda means the
 * framework automatically redraws whenever a new result arrives.
 *
 * @param state the state holder produced by [rememberGooeyUnionState]
 * @param color colour applied to the path
 * @param style [Fill] or [Stroke] drawing style
 */
fun Modifier.drawGooeyUnion(
    state: GooeyUnionState,
    color: Color = Color.White,
    style: DrawStyle = Stroke(width = 4f),
): Modifier = this.drawBehind {
    state.result?.unionPath?.let { path ->
        drawPath(path, color, style = style)
    }
}

/**
 * Draws each blob path individually using the supplied lambdas,
 * giving full control over colour, style, and layering order.
 *
 * @param state the state holder produced by [rememberGooeyUnionState]
 * @param onDraw receives the latest [GooeyUnionResult] inside a [DrawScope]
 */
fun Modifier.drawGooeyUnionCustom(
    state: GooeyUnionState,
    onDraw: DrawScope.(GooeyUnionResult) -> Unit,
): Modifier = this.drawBehind {
    state.result?.let { onDraw(it) }
}

/**
 * Computes the scalar signed gap between the dynamic and static shapes.
 * This is cheap (SDF + ray distance, no path ops) and runs on the main
 * thread so the state machine can react every frame without waiting for
 * the heavy background computation.
 */
private fun computeGapPx(
    params: GooeyUnionParams,
    staticCenter: Offset,
    dynamicCenter: Offset,
): Float {
    with(params) {
        val sHalfW = staticRectWidthPx * 0.5f
        val sHalfH = staticRectHeightPx * 0.5f
        val sCorner = staticCornerPx.coerceIn(0f, min(sHalfW, sHalfH))
        val dHalfW = dynamicRectWidthPx * 0.5f
        val dHalfH = dynamicRectHeightPx * 0.5f
        val dCorner = dynamicCornerPx.coerceIn(0f, min(dHalfW, dHalfH))

        val pLocal = dynamicCenter - staticCenter

        val sd = when (staticShape) {
            StaticShape.Circle ->
                (dynamicCenter - staticCenter).getDistance() - staticCircleRadiusPx

            StaticShape.Rect ->
                signedDistanceBox(pLocal, sHalfW, sHalfH)

            StaticShape.RoundedRect ->
                signedDistanceRoundedBox(pLocal, sHalfW, sHalfH, sCorner)
        }

        val contactPt = when (staticShape) {
            StaticShape.Circle -> {
                val dir = normalizeSafe(dynamicCenter - staticCenter)
                staticCenter + dir * staticCircleRadiusPx
            }

            StaticShape.Rect ->
                staticCenter + closestPointOnBox(pLocal, sHalfW, sHalfH)

            StaticShape.RoundedRect ->
                staticCenter + closestPointOnRoundedBox(pLocal, sHalfW, sHalfH, sCorner)
        }

        val dynDir = normalizeSafe(contactPt - dynamicCenter)
        val dynSupport = when (dynamicShape) {
            DynamicShape.Circle -> dynamicCircleRadiusPx
            DynamicShape.Rect -> rayDistanceToBox(dynDir, dHalfW, dHalfH)
            DynamicShape.RoundedRect ->
                rayDistanceToRoundedBox(dynDir, dHalfW, dHalfH, dCorner)
        }

        return sd - dynSupport
    }
}

/**
 * Hermite smoothstep interpolation: `3x^2 - 2x^3`.
 * Input should be in 0..1 for the standard S-curve.
 */
private fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

/**
 * Returns the angle in radians of [dir],
 * measured counter-clockwise from the positive X axis.
 */
private fun angleOf(dir: Offset): Float = atan2(dir.y, dir.x)

/**
 * Normalises [v] to unit length. Returns `(1, 0)` when the length
 * is near zero to avoid division-by-zero artifacts.
 */
private fun normalizeSafe(v: Offset): Offset {
    val d = v.getDistance()
    return if (d > 1e-4f) v / d else Offset(1f, 0f)
}

/**
 * Dot product of two [Offset] vectors.
 */
private fun dot(a: Offset, b: Offset): Float = a.x * b.x + a.y * b.y

/**
 * 2D cross product (z-component of the 3D cross).
 * Positive when [b] is counter-clockwise from [a].
 */
private fun cross(a: Offset, b: Offset): Float = a.x * b.y - a.y * b.x

/**
 * Returns the unit vector of [v], flipped if necessary so that
 * it points roughly in the same direction as [wantDir].
 */
private fun oriented(v: Offset, wantDir: Offset): Offset {
    val vu = normalizeSafe(v)
    val wd = normalizeSafe(wantDir)
    return if (dot(vu, wd) < 0f) -vu else vu
}

/**
 * Signed distance from point [p] (in local space) to an axis-aligned box
 * with half-extents [hx] x [hy]. Negative inside, positive outside.
 */
private fun signedDistanceBox(p: Offset, hx: Float, hy: Float): Float {
    val dx = abs(p.x) - hx
    val dy = abs(p.y) - hy
    val outside = sqrt(max(dx, 0f).pow(2) + max(dy, 0f).pow(2))
    return outside + min(max(dx, dy), 0f)
}

/**
 * Signed distance from point [p] (in local space) to an axis-aligned
 * rounded box with half-extents [hx] x [hy] and corner radius [r].
 * Negative inside, positive outside.
 */
private fun signedDistanceRoundedBox(p: Offset, hx: Float, hy: Float, r: Float): Float {
    val rr = r.coerceIn(0f, min(hx, hy))
    val qx = abs(p.x) - (hx - rr)
    val qy = abs(p.y) - (hy - rr)
    val outside = sqrt(max(qx, 0f).pow(2) + max(qy, 0f).pow(2))
    return outside + min(max(qx, qy), 0f) - rr
}

/**
 * Returns the closest point on the boundary of an axis-aligned box
 * (half-extents [hx] x [hy]) to point [p] in local space.
 * Works correctly whether [p] is inside or outside the box.
 */
private fun closestPointOnBox(p: Offset, hx: Float, hy: Float): Offset {
    val cx = p.x.coerceIn(-hx, hx)
    val cy = p.y.coerceIn(-hy, hy)
    if (!(abs(p.x) <= hx && abs(p.y) <= hy)) return Offset(cx, cy)
    val dx = hx - abs(p.x)
    val dy = hy - abs(p.y)
    return if (dx < dy)
        Offset(hx * sign(p.x.takeIf { it != 0f } ?: 1f), p.y.coerceIn(-hy, hy))
    else
        Offset(p.x.coerceIn(-hx, hx), hy * sign(p.y.takeIf { it != 0f } ?: 1f))
}

/**
 * Returns the closest point on the boundary of an axis-aligned rounded box
 * (half-extents [hx] x [hy], corner radius [r]) to point [p] in local space.
 */
private fun closestPointOnRoundedBox(p: Offset, hx: Float, hy: Float, r: Float): Offset {
    val rr = r.coerceIn(0f, min(hx, hy))
    val ix = (hx - rr).coerceAtLeast(0f)
    val iy = (hy - rr).coerceAtLeast(0f)
    val clamped = Offset(p.x.coerceIn(-ix, ix), p.y.coerceIn(-iy, iy))
    val delta = p - clamped
    val len = delta.getDistance()
    return if (len > 1e-4f) {
        clamped + (delta / len) * rr
    } else {
        val dx = ix - abs(p.x)
        val dy = iy - abs(p.y)
        if (dx < dy)
            Offset(ix * sign(p.x.takeIf { it != 0f } ?: 1f), p.y.coerceIn(-iy, iy))
        else
            Offset(p.x.coerceIn(-ix, ix), iy * sign(p.y.takeIf { it != 0f } ?: 1f))
    }
}

/**
 * Returns the outward-pointing unit normal at [contactLocal] on
 * the boundary of an axis-aligned box with half-extents [hx] x [hy].
 */
private fun outwardNormalOnBox(contactLocal: Offset, hx: Float, hy: Float): Offset {
    val eps = 1e-3f
    val ax = abs(contactLocal.x)
    val ay = abs(contactLocal.y)
    return when {
        ax >= hx - eps && ay <= hy + eps ->
            Offset(sign(contactLocal.x.takeIf { it != 0f } ?: 1f), 0f)

        ay >= hy - eps && ax <= hx + eps ->
            Offset(0f, sign(contactLocal.y.takeIf { it != 0f } ?: 1f))

        ax > ay -> Offset(sign(contactLocal.x.takeIf { it != 0f } ?: 1f), 0f)
        else -> Offset(0f, sign(contactLocal.y.takeIf { it != 0f } ?: 1f))
    }
}

/**
 * Returns the outward-pointing unit normal at [contactLocal] on
 * the boundary of an axis-aligned rounded box with half-extents
 * [hx] x [hy] and corner radius [r]. Delegates to [outwardNormalOnBox]
 * for flat segments and computes the radial normal on arc segments.
 */
private fun outwardNormalOnRoundedBox(
    contactLocal: Offset, hx: Float, hy: Float, r: Float
): Offset {
    val rr = r.coerceIn(0f, min(hx, hy))
    val ix = (hx - rr).coerceAtLeast(0f)
    val iy = (hy - rr).coerceAtLeast(0f)
    val ax = abs(contactLocal.x)
    val ay = abs(contactLocal.y)
    return if (ax > ix + 1e-3f && ay > iy + 1e-3f) {
        val cx = ix * sign(contactLocal.x.takeIf { it != 0f } ?: 1f)
        val cy = iy * sign(contactLocal.y.takeIf { it != 0f } ?: 1f)
        normalizeSafe(contactLocal - Offset(cx, cy))
    } else {
        outwardNormalOnBox(contactLocal, hx, hy)
    }
}

/**
 * Returns the distance from the origin along [dirUnit]
 * to the boundary of an axis-aligned box with half-extents [hx] x [hy].
 */
private fun rayDistanceToBox(dirUnit: Offset, hx: Float, hy: Float): Float {
    val tx = hx / abs(dirUnit.x).coerceAtLeast(1e-5f)
    val ty = hy / abs(dirUnit.y).coerceAtLeast(1e-5f)
    return min(tx, ty)
}

/**
 * Returns the distance from the origin along [dirUnit]
 * to the boundary of an axis-aligned rounded box with half-extents
 * [hx] x [hy] and corner radius [r]. Falls back to [rayDistanceToBox]
 * when the corner radius is negligible, and solves a ray-circle
 * intersection for corner arc segments.
 */
private fun rayDistanceToRoundedBox(dirUnit: Offset, hx: Float, hy: Float, r: Float): Float {
    val rr = r.coerceIn(0f, min(hx, hy))
    if (rr <= 1e-4f) return rayDistanceToBox(dirUnit, hx, hy)
    val ix = (hx - rr).coerceAtLeast(0f)
    val iy = (hy - rr).coerceAtLeast(0f)
    if (ix <= 1e-4f || iy <= 1e-4f) return solveCornerRayDistance(dirUnit, hx, hy, rr)

    val ax = abs(dirUnit.x)
    val ay = abs(dirUnit.y)
    val tx = if (ax > 1e-5f) ix / ax else Float.POSITIVE_INFINITY
    val ty = if (ay > 1e-5f) iy / ay else Float.POSITIVE_INFINITY
    val tInner = min(tx, ty)
    val pInner = Offset(dirUnit.x * tInner, dirUnit.y * tInner)

    if (abs(pInner.x) >= ix - 1e-3f && abs(pInner.y) <= iy - 1e-3f)
        return if (ax > 1e-5f) hx / ax else tInner
    if (abs(pInner.y) >= iy - 1e-3f && abs(pInner.x) <= ix - 1e-3f)
        return if (ay > 1e-5f) hy / ay else tInner

    return solveCornerRayDistance(dirUnit, hx, hy, rr)
}

/**
 * Solves the ray-circle intersection for a corner arc of a rounded box.
 * The corner circle center is placed at `(hx-r, hy-r)` in the quadrant
 * matching the sign of [dirUnit]. Returns the nearest positive hit distance.
 */
private fun solveCornerRayDistance(dirUnit: Offset, hx: Float, hy: Float, r: Float): Float {
    val ux = dirUnit.x;
    val uy = dirUnit.y
    val cx = (hx - r) * sign(ux.takeIf { it != 0f } ?: 1f)
    val cy = (hy - r) * sign(uy.takeIf { it != 0f } ?: 1f)
    val a = ux * ux + uy * uy
    val b = -2f * (ux * cx + uy * cy)
    val c = cx * cx + cy * cy - r * r
    val disc = b * b - 4f * a * c
    if (disc < 0f) return rayDistanceToBox(dirUnit, hx, hy)
    val sqrtDisc = sqrt(disc)
    val t0 = (-b - sqrtDisc) / (2f * a)
    val t1 = (-b + sqrtDisc) / (2f * a)
    return listOf(t0, t1).filter { it >= 0f }.minOrNull() ?: 0f
}

/**
 * Generates evenly-spaced boundary sample points for the dynamic blob.
 * Circles are sampled by angle; rects and rounded rects delegate to
 * [buildStaticBoundarySamplePoints].
 *
 * @return interleaved x,y [FloatArray] with `(n+1)*2` elements (first == last)
 */
private fun buildBoundarySamplePointsForDynamic(
    center: Offset,
    shape: DynamicShape,
    circleRadiusPx: Float,
    rectWidthPx: Float,
    rectHeightPx: Float,
    cornerRadiusPx: Float,
    samplePointCount: Int,
): FloatArray = when (shape) {
    DynamicShape.Circle -> {
        val n = max(32, samplePointCount)
        FloatArray((n + 1) * 2).also { pts ->
            val step = (2.0 * PI / n).toFloat()
            for (i in 0..n) {
                pts[i * 2] = center.x + circleRadiusPx * cos(i * step)
                pts[i * 2 + 1] = center.y + circleRadiusPx * sin(i * step)
            }
        }
    }

    DynamicShape.Rect -> buildStaticBoundarySamplePoints(
        center, StaticShape.Rect, 0f, rectWidthPx, rectHeightPx, 0f, samplePointCount
    )

    DynamicShape.RoundedRect -> buildStaticBoundarySamplePoints(
        center, StaticShape.RoundedRect, 0f,
        rectWidthPx, rectHeightPx, cornerRadiusPx, samplePointCount
    )
}

/**
 * Generates evenly-spaced boundary sample points for a static blob shape.
 *
 * @return interleaved x,y [FloatArray] with `(n+1)*2` elements (first == last)
 */
private fun buildStaticBoundarySamplePoints(
    center: Offset,
    shape: StaticShape,
    circleRadiusPx: Float,
    rectWidthPx: Float,
    rectHeightPx: Float,
    cornerRadiusPx: Float,
    samplePointCount: Int,
): FloatArray {
    val n = max(32, samplePointCount)
    val points = FloatArray((n + 1) * 2)
    when (shape) {
        StaticShape.Circle -> {
            val step = (2.0 * PI / n).toFloat()
            for (i in 0..n) {
                points[i * 2] = center.x + circleRadiusPx * cos(i * step)
                points[i * 2 + 1] = center.y + circleRadiusPx * sin(i * step)
            }
        }

        StaticShape.Rect -> {
            val hw = rectWidthPx * 0.5f;
            val hh = rectHeightPx * 0.5f
            val per = 2f * (rectWidthPx + rectHeightPx)
            for (i in 0..n) {
                val (x, y) = pointOnRectPerimeter(center, hw, hh, (i.toFloat() / n) * per)
                points[i * 2] = x; points[i * 2 + 1] = y
            }
        }

        StaticShape.RoundedRect -> {
            val hw = rectWidthPx * 0.5f;
            val hh = rectHeightPx * 0.5f
            val r = cornerRadiusPx.coerceIn(0f, min(hw, hh))
            val straightW = max(0f, rectWidthPx - 2f * r)
            val straightH = max(0f, rectHeightPx - 2f * r)
            val arcLen = (0.5f * PI.toFloat()) * r
            val per = 2f * (straightW + straightH) + 4f * arcLen
            for (i in 0..n) {
                val p = pointOnRoundedRectPerimeter(
                    center, hw, hh, r, straightW, straightH, (i.toFloat() / n) * per
                )
                points[i * 2] = p.x; points[i * 2 + 1] = p.y
            }
        }
    }
    return points
}

/**
 * Returns the world-space position at arc-length [s] along the perimeter
 * of an axis-aligned rectangle centerd at [center] with half-extents
 * [hw] x [hh]. Starts at top-left, proceeds clockwise.
 */
private fun pointOnRectPerimeter(
    center: Offset, hw: Float, hh: Float, s: Float
): Pair<Float, Float> {
    val w = 2f * hw;
    val h = 2f * hh;
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

/**
 * Returns the world-space position at arc-length [s] along the perimeter
 * of a rounded rectangle centred at [center] with half-extents [hw] x [hh],
 * corner radius [r], and pre-computed straight segment lengths.
 * Starts at the top-left inner corner, proceeds clockwise.
 */
private fun pointOnRoundedRectPerimeter(
    center: Offset, hw: Float, hh: Float, r: Float,
    straightW: Float, straightH: Float, s: Float,
): Offset {
    val arcLen = (0.5f * PI.toFloat()) * r
    val per = 2f * (straightW + straightH) + 4f * arcLen
    var d = ((s % per) + per) % per
    val left = center.x - hw;
    val right = center.x + hw
    val top = center.y - hh;
    val bottom = center.y + hh
    val tlc = Offset(left + r, top + r)
    val trc = Offset(right - r, top + r)
    val brc = Offset(right - r, bottom - r)
    val blc = Offset(left + r, bottom - r)

    if (d <= straightW) return Offset(tlc.x + d, top)
    d -= straightW
    if (d <= arcLen) {
        val a = (-PI / 2.0 + (PI / 2.0) * (d / arcLen)).toFloat()
        return trc + Offset(r * cos(a), r * sin(a))
    }
    d -= arcLen
    if (d <= straightH) return Offset(right, trc.y + d)
    d -= straightH
    if (d <= arcLen) {
        val a = ((PI / 2.0) * (d / arcLen)).toFloat()
        return brc + Offset(r * cos(a), r * sin(a))
    }
    d -= arcLen
    if (d <= straightW) return Offset(brc.x - d, bottom)
    d -= straightW
    if (d <= arcLen) {
        val a = (PI / 2.0 + (PI / 2.0) * (d / arcLen)).toFloat()
        return blc + Offset(r * cos(a), r * sin(a))
    }
    d -= arcLen
    if (d <= straightH) return Offset(left, blc.y - d)
    d -= straightH
    val t = (d / arcLen).coerceIn(0f, 1f)
    val a = (PI + (PI / 2.0) * t).toFloat()
    return tlc + Offset(r * cos(a), r * sin(a))
}

/**
 * Displaces each sample point along its outward boundary normal to create
 * the gooey "neck" stretch. Points facing [facingAngleRad] inflate outward;
 * points on the far side compress inward. Shaped by a cosine mask raised
 * to [neckFocusPower].
 *
 * @return new interleaved x,y [FloatArray] with deformed positions
 */
private fun deformAlongBoundaryNormal(
    center: Offset,
    basePoints: FloatArray,
    stretchPx: Float,
    facingAngleRad: Float,
    neckFocusPower: Float,
    stretchBias: Float,
): FloatArray {
    val n = (basePoints.size / 2) - 1
    if (n < 8) return basePoints

    fun neckMask(theta: Float): Float {
        val facing = ((cos(theta - facingAngleRad).coerceIn(-1f, 1f)) + 1f) * 0.5f
        return smoothstep(facing).pow(neckFocusPower)
    }

    fun pAt(i: Int): Offset {
        val ii = ((i % n) + n) % n;
        val idx = ii * 2
        return Offset(basePoints[idx], basePoints[idx + 1])
    }

    fun tangentAt(i: Int): Offset = normalizeSafe(pAt(i + 1) - pAt(i - 1))

    val out = FloatArray(basePoints.size)
    for (i in 0 until n) {
        val p = pAt(i)
        val t = tangentAt(i)
        var normal = Offset(-t.y, t.x)
        if (dot(normal, p - center) < 0f) normal = -normal
        val theta = atan2((p - center).y, (p - center).x)
        val mask = neckMask(theta)
        val disp = (stretchPx * stretchBias) * mask + (-0.22f * stretchPx * (1f - mask))
        val q = p + normal * disp
        out[i * 2] = q.x; out[i * 2 + 1] = q.y
    }
    out[n * 2] = out[0]; out[n * 2 + 1] = out[1]
    return out
}

/**
 * Writes the interleaved x,y [points] into [outPath] as a closed polygon.
 */
private fun fillPathFromPoints(outPath: Path, points: FloatArray) {
    if (points.isEmpty()) return
    outPath.moveTo(points[0], points[1])
    var i = 2
    while (i < points.size) {
        outPath.lineTo(points[i], points[i + 1]); i += 2
    }
    outPath.close()
}

/**
 * Control points and endpoints for the cubic-Bezier ligament
 * that bridges two blob shapes during the stretch phase.
 */
private data class LigamentGeometry(
    val firstTop: Offset, val firstBottom: Offset,
    val secondTop: Offset, val secondBottom: Offset,
    val firstTopControl: Offset, val secondTopControl: Offset,
    val secondBottomControl: Offset, val firstBottomControl: Offset,
)

/**
 * A pair of anchor points (top and bottom) on a polyline boundary,
 * together with their tangent directions. Used to attach the ligament
 * Bezier curves to the blob outlines.
 */
data class AnchorOffsetPoints(
    val top: Offset,
    val bottom: Offset,
    val topTangentUnit: Offset,
    val bottomTangentUnit: Offset,
)

/**
 * Finds the top and bottom anchor points on a closed polyline where
 * the ligament should attach. Searches for the boundary sample most
 * aligned with [facingDir], then walks outward to find two points
 * that straddle the facing direction with enough chord width.
 *
 * @param center shape centre, used for radial direction tests
 * @param points interleaved x,y boundary samples (first == last)
 * @param facingDir unit direction the ligament should face toward
 * @param halfThickness desired half-width of the ligament at the anchor
 * @return [AnchorOffsetPoints] with positions and tangent directions
 */
private fun anchorsFromPolyline(
    center: Offset, points: FloatArray, facingDir: Offset, halfThickness: Float,
): AnchorOffsetPoints {
    val n = (points.size / 2) - 1
    if (n < 8) {
        val p = Offset(points.getOrElse(0) { 0f }, points.getOrElse(1) { 0f })
        return AnchorOffsetPoints(p, p, Offset(1f, 0f), Offset(1f, 0f))
    }
    val fd = normalizeSafe(facingDir)
    fun pAt(i: Int): Offset {
        val ii = ((i % n) + n) % n
        return Offset(points[ii * 2], points[ii * 2 + 1])
    }

    fun tangentAt(i: Int): Offset = normalizeSafe(pAt(i + 1) - pAt(i - 1))

    var bestI = 0;
    var bestS = -Float.MAX_VALUE
    for (i in 0 until n) {
        val s = dot(normalizeSafe(pAt(i) - center), fd)
        if (s > bestS) {
            bestS = s; bestI = i
        }
    }

    val target = max(1f, 2f * halfThickness)
    var k = 1;
    val maxK = n / 4
    while (k < maxK) {
        if ((pAt(bestI + k) - pAt(bestI - k)).getDistance() >= target) break
        k++
    }
    k = k.coerceIn(1, max(1, maxK - 1))

    val a0 = pAt(bestI - k);
    val b0 = pAt(bestI + k)
    val aS = cross(fd, normalizeSafe(a0 - center))
    val bS = cross(fd, normalizeSafe(b0 - center))
    val top = if (aS >= bS) a0 else b0
    val bottom = if (aS >= bS) b0 else a0
    val topI = if (aS >= bS) (bestI - k) else (bestI + k)
    val bottomI = if (aS >= bS) (bestI + k) else (bestI - k)
    return AnchorOffsetPoints(top, bottom, tangentAt(topI), tangentAt(bottomI))
}

/**
 * Builds the [LigamentGeometry] (anchors + Bezier handles) that bridges
 * two blob polylines. The ligament tapers from [bridgeThicknessMaxPx]
 * at zero strength to [bridgeThicknessMinPx] at full strength.
 *
 * @param firstCenter center of the first (dynamic) blob
 * @param firstPoints boundary samples of the first blob
 * @param firstFacingDir direction the first blob faces toward the second
 * @param secondCenter center of the second (static) blob
 * @param secondPoints boundary samples of the second blob
 * @param secondFacingDir direction the second blob faces toward the first
 * @param strength current bridge strength in 0..1
 * @param bridgeThicknessMaxPx ligament half-width when strength is 0
 * @param bridgeThicknessMinPx ligament half-width when strength is 1
 * @param bridgeHandleScale multiplier for the Bezier handle length
 * @return [LigamentGeometry] ready to be drawn as two cubic Bezier curves
 */
private fun computeLigamentFromPointArrays(
    firstCenter: Offset, firstPoints: FloatArray, firstFacingDir: Offset,
    secondCenter: Offset, secondPoints: FloatArray, secondFacingDir: Offset,
    strength: Float,
    bridgeThicknessMaxPx: Float, bridgeThicknessMinPx: Float, bridgeHandleScale: Float,
): LigamentGeometry {
    val s = smoothstep(strength.coerceIn(0f, 1f))
    val thickness = bridgeThicknessMaxPx + (bridgeThicknessMinPx - bridgeThicknessMaxPx) * s

    val first = anchorsFromPolyline(firstCenter, firstPoints, firstFacingDir, thickness)
    val second = anchorsFromPolyline(secondCenter, secondPoints, secondFacingDir, thickness)

    val topSpan = (second.top - first.top).getDistance()
    val botSpan = (first.bottom - second.bottom).getDistance()
    val minSpan = min(topSpan, botSpan)
    val maxHandle = max(6f, minSpan * 0.35f)
    val handle = ((minSpan * 0.30f).coerceIn(6f, maxHandle) * bridgeHandleScale)
        .coerceIn(1f, maxHandle)

    fun orient(t: Offset, want: Offset): Offset {
        val u = normalizeSafe(t)
        return if (dot(u, want) < 0f) -u else u
    }

    val topDir = normalizeSafe(second.top - first.top)
    val botDir = normalizeSafe(first.bottom - second.bottom)

    return LigamentGeometry(
        firstTop = first.top, firstBottom = first.bottom,
        secondTop = second.top, secondBottom = second.bottom,
        firstTopControl = first.top + orient(first.topTangentUnit, topDir) * handle,
        secondTopControl = second.top + orient(second.topTangentUnit, -topDir) * handle,
        secondBottomControl = second.bottom + orient(second.bottomTangentUnit, -botDir) * handle,
        firstBottomControl = first.bottom + orient(first.bottomTangentUnit, botDir) * handle,
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
private fun GooeyUnionPreview() {

    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var dynamicShape by remember { mutableStateOf(DynamicShape.Circle) }
    var dynamicCircleRadiusPx by remember { mutableFloatStateOf(100f) }
    var dynamicRectWidthPx by remember { mutableFloatStateOf(220f) }
    var dynamicRectHeightPx by remember { mutableFloatStateOf(220f) }
    var dynamicCornerPx by remember { mutableFloatStateOf(60f) }

    var staticShape by remember { mutableStateOf(StaticShape.RoundedRect) }
    var staticCircleRadiusPx by remember { mutableFloatStateOf(150f) }
    var staticRectWidthPx by remember { mutableFloatStateOf(650f) }
    var staticRectHeightPx by remember { mutableFloatStateOf(200f) }
    var staticCornerPx by remember { mutableFloatStateOf(90f) }

    var minStretchScaleAtTouch by remember { mutableFloatStateOf(0.60f) }
    var shallowOverlapBandPx by remember { mutableFloatStateOf(100f) }
    var bridgeThicknessMaxPx by remember { mutableFloatStateOf(32f) }
    var bridgeThicknessMinPx by remember { mutableFloatStateOf(2.5f) }
    var bridgeHandleScale by remember { mutableFloatStateOf(1.0f) }
    var detachWobbleMaxPx by remember { mutableFloatStateOf(18f) }

    var debugEnabled by remember { mutableStateOf(true) }
    var debugDrawLigament by remember { mutableStateOf(true) }
    var debugDrawHandles by remember { mutableStateOf(true) }
    var debugDrawVectors by remember { mutableStateOf(true) }
    var debugDrawArcSpans by remember { mutableStateOf(true) }

    val params = GooeyUnionParams(
        dynamicShape = dynamicShape,
        dynamicCircleRadiusPx = dynamicCircleRadiusPx,
        dynamicRectWidthPx = dynamicRectWidthPx,
        dynamicRectHeightPx = dynamicRectHeightPx,
        dynamicCornerPx = dynamicCornerPx,
        staticShape = staticShape,
        staticCircleRadiusPx = staticCircleRadiusPx,
        staticRectWidthPx = staticRectWidthPx,
        staticRectHeightPx = staticRectHeightPx,
        staticCornerPx = staticCornerPx,
        minStretchScaleAtTouch = minStretchScaleAtTouch,
        shallowOverlapBandPx = shallowOverlapBandPx,
        bridgeThicknessMaxPx = bridgeThicknessMaxPx,
        bridgeThicknessMinPx = bridgeThicknessMinPx,
        bridgeHandleScale = bridgeHandleScale,
        detachWobbleMaxPx = detachWobbleMaxPx,
    )

    val state = rememberGooeyUnionState(params)

    LaunchedEffect(params) {
        state.params = params
    }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var pointerPosition by remember { mutableStateOf(Offset.Unspecified) }

    LaunchedEffect(canvasSize) {
        val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
        state.staticCenter = center
        if (pointerPosition == Offset.Unspecified) {
            state.dynamicCenter = center
        }
    }

    LaunchedEffect(pointerPosition) {
        if (pointerPosition != Offset.Unspecified) {
            state.dynamicCenter = pointerPosition
        }
    }

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = Color.Red
            strokeWidth = 4f
            isAntiAlias = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            pointerPosition = it
                        }
                    ) { change, _ ->
                        pointerPosition = change.position
                        change.consume()
                    }
                }
        ) {
            val result = state.result ?: return@Canvas

            if (debugEnabled) {
                if (debugDrawLigament) {
                    drawPath(
                        result.dynamicPath,
                        Color.Cyan.copy(alpha = 0.3f),
                        style = Stroke(width = 1.5f)
                    )
                    drawPath(
                        result.staticPath,
                        Color.Yellow.copy(alpha = 0.3f),
                        style = Stroke(width = 1.5f)
                    )
                }

                if (debugDrawVectors) {
                    drawLine(
                        Color.White,
                        state.dynamicCenter,
                        state.staticCenter,
                        strokeWidth = 1f
                    )
                    drawCircle(Color.Green, 6f, state.staticCenter)
                    drawCircle(Color.Magenta, 6f, state.dynamicCenter)
                }

                if (debugDrawHandles) {
                    drawPath(
                        result.unionPath,
                        Color.White.copy(alpha = 0.15f),
                        style = Stroke(width = 8f)
                    )
                }

                if (debugDrawArcSpans) {
                    val gap = result.gapPx
                    val barWidth = 200f
                    val barX = size.width - barWidth - 20f
                    val barY = size.height - 40f
                    val normalizedGap = ((gap + 100f) / 200f).coerceIn(0f, 1f)
                    drawRect(
                        Color(0x44FFFFFF),
                        topLeft = Offset(barX, barY),
                        size = androidx.compose.ui.geometry.Size(barWidth, 12f)
                    )
                    drawRect(
                        Color.Green,
                        topLeft = Offset(barX, barY),
                        size = androidx.compose.ui.geometry.Size(barWidth * normalizedGap, 12f)
                    )
                    drawLine(
                        Color.Red,
                        Offset(barX + barWidth * 0.5f, barY),
                        Offset(barX + barWidth * 0.5f, barY + 12f),
                        strokeWidth = 2f
                    )
                }
            }

            drawIntoCanvas { canvas ->
                canvas.drawPath(result.unionPath, paint)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val result = state.result
            Text(
                text = buildString {
                    append("attached=${state.isAttached}")
                    append(" pulling=${state.isPullingApart}")
                    if (result != null) {
                        append(" gap=${"%.1f".format(result.gapPx)}")
                    }
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
                var selectedTab by rememberSaveable { mutableStateOf(0) }
                val tabTitles = listOf("Shapes", "Tuning")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = Color.White
                    ) {
                        tabTitles.forEachIndexed { i, title ->
                            androidx.compose.material3.Tab(
                                selected = selectedTab == i,
                                onClick = { selectedTab = i },
                                text = { Text(title, maxLines = 1) }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                Text("Dynamic shape", color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DebugToggleText(
                                        "Circle",
                                        dynamicShape == DynamicShape.Circle
                                    ) {
                                        dynamicShape = DynamicShape.Circle
                                    }
                                    DebugToggleText("Rect", dynamicShape == DynamicShape.Rect) {
                                        dynamicShape = DynamicShape.Rect
                                    }
                                    DebugToggleText(
                                        "RoundRect",
                                        dynamicShape == DynamicShape.RoundedRect
                                    ) {
                                        dynamicShape = DynamicShape.RoundedRect
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                when (dynamicShape) {
                                    DynamicShape.Circle -> {
                                        Text(
                                            "Dynamic radius = ${"%.0f".format(dynamicCircleRadiusPx)} px",
                                            color = Color.White
                                        )
                                        Slider(
                                            value = dynamicCircleRadiusPx,
                                            onValueChange = {
                                                dynamicCircleRadiusPx = it.coerceIn(60f, 260f)
                                            },
                                            valueRange = 60f..260f
                                        )
                                    }

                                    DynamicShape.Rect, DynamicShape.RoundedRect -> {
                                        Text(
                                            "Dynamic rect width = ${
                                                "%.0f".format(
                                                    dynamicRectWidthPx
                                                )
                                            } px", color = Color.White
                                        )
                                        Slider(
                                            value = dynamicRectWidthPx,
                                            onValueChange = {
                                                dynamicRectWidthPx = it.coerceIn(120f, 520f)
                                            },
                                            valueRange = 120f..520f
                                        )

                                        Text(
                                            "Dynamic rect height = ${
                                                "%.0f".format(
                                                    dynamicRectHeightPx
                                                )
                                            } px", color = Color.White
                                        )
                                        Slider(
                                            value = dynamicRectHeightPx,
                                            onValueChange = {
                                                dynamicRectHeightPx = it.coerceIn(120f, 520f)
                                            },
                                            valueRange = 120f..520f
                                        )

                                        if (dynamicShape == DynamicShape.RoundedRect) {
                                            val maxCorner =
                                                min(dynamicRectWidthPx, dynamicRectHeightPx) * 0.5f
                                            Text(
                                                "Dynamic corner = ${"%.0f".format(dynamicCornerPx)} px",
                                                color = Color.White
                                            )
                                            Slider(
                                                value = dynamicCornerPx.coerceIn(0f, maxCorner),
                                                onValueChange = {
                                                    dynamicCornerPx = it.coerceIn(0f, maxCorner)
                                                },
                                                valueRange = 0f..max(1f, maxCorner)
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                Text("Static shape", color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DebugToggleText(
                                        "Circle",
                                        staticShape == StaticShape.Circle
                                    ) {
                                        staticShape = StaticShape.Circle
                                    }
                                    DebugToggleText("Rect", staticShape == StaticShape.Rect) {
                                        staticShape = StaticShape.Rect
                                    }
                                    DebugToggleText(
                                        "RoundRect",
                                        staticShape == StaticShape.RoundedRect
                                    ) {
                                        staticShape = StaticShape.RoundedRect
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "Static rect width = ${"%.0f".format(staticRectWidthPx)} px",
                                    color = Color.White
                                )
                                Slider(
                                    value = staticRectWidthPx,
                                    onValueChange = {
                                        staticRectWidthPx = it.coerceIn(240f, 1400f)
                                    },
                                    valueRange = 240f..1400f
                                )

                                Text(
                                    "Static rect height = ${"%.0f".format(staticRectHeightPx)} px",
                                    color = Color.White
                                )
                                Slider(
                                    value = staticRectHeightPx,
                                    onValueChange = {
                                        staticRectHeightPx = it.coerceIn(120f, 720f)
                                    },
                                    valueRange = 120f..720f
                                )

                                val maxStaticCorner =
                                    min(staticRectWidthPx, staticRectHeightPx) * 0.5f
                                Text(
                                    "Static corner radius = ${"%.0f".format(staticCornerPx)} px",
                                    color = Color.White
                                )
                                Slider(
                                    value = staticCornerPx.coerceIn(0f, maxStaticCorner),
                                    onValueChange = {
                                        staticCornerPx = it.coerceIn(0f, maxStaticCorner)
                                    },
                                    valueRange = 0f..max(1f, maxStaticCorner)
                                )

                                if (staticShape == StaticShape.Circle) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Static circle radius = ${
                                            "%.0f".format(
                                                staticCircleRadiusPx
                                            )
                                        } px", color = Color.White
                                    )
                                    Slider(
                                        value = staticCircleRadiusPx,
                                        onValueChange = {
                                            staticCircleRadiusPx = it.coerceIn(80f, 420f)
                                        },
                                        valueRange = 80f..420f
                                    )
                                }

                                Spacer(Modifier.height(10.dp))
                            }

                            else -> {
                                Text(
                                    "Detach wobble amplitude = ${"%.0f".format(detachWobbleMaxPx)} px",
                                    color = Color.White
                                )
                                Slider(
                                    value = detachWobbleMaxPx,
                                    onValueChange = { detachWobbleMaxPx = it.coerceIn(0f, 48f) },
                                    valueRange = 0f..48f
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "minStretchScaleAtTouch = ${
                                        "%.2f".format(
                                            minStretchScaleAtTouch
                                        )
                                    }", color = Color.White
                                )
                                Slider(
                                    value = minStretchScaleAtTouch,
                                    onValueChange = {
                                        minStretchScaleAtTouch = it.coerceIn(0f, 1f)
                                    },
                                    valueRange = 0f..1f
                                )

                                Text(
                                    "shallowOverlapBandPx = ${"%.0f".format(shallowOverlapBandPx)} px",
                                    color = Color.White
                                )
                                Slider(
                                    value = shallowOverlapBandPx,
                                    onValueChange = {
                                        shallowOverlapBandPx = it.coerceIn(5f, 240f)
                                    },
                                    valueRange = 5f..240f
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "bridgeThicknessMaxPx = ${"%.1f".format(bridgeThicknessMaxPx)}",
                                    color = Color.White
                                )
                                Slider(
                                    value = bridgeThicknessMaxPx,
                                    onValueChange = { bridgeThicknessMaxPx = it.coerceIn(2f, 60f) },
                                    valueRange = 2f..60f
                                )

                                Text(
                                    "bridgeThicknessMinPx = ${"%.1f".format(bridgeThicknessMinPx)}",
                                    color = Color.White
                                )
                                Slider(
                                    value = bridgeThicknessMinPx,
                                    onValueChange = {
                                        bridgeThicknessMinPx =
                                            it.coerceIn(0.5f, bridgeThicknessMaxPx)
                                    },
                                    valueRange = 0.5f..60f
                                )

                                Text(
                                    "bridgeHandleScale = ${"%.2f".format(bridgeHandleScale)}",
                                    color = Color.White
                                )
                                Slider(
                                    value = bridgeHandleScale,
                                    onValueChange = {
                                        bridgeHandleScale = it.coerceIn(0.10f, 2.50f)
                                    },
                                    valueRange = 0.10f..2.50f
                                )

                                Spacer(Modifier.height(12.dp))

                                Text("Debug", color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DebugToggleText("On", debugEnabled) {
                                        debugEnabled = !debugEnabled
                                    }
                                    DebugToggleText(
                                        "Ligament",
                                        debugDrawLigament
                                    ) { debugDrawLigament = !debugDrawLigament }
                                    DebugToggleText(
                                        "Handles",
                                        debugDrawHandles
                                    ) { debugDrawHandles = !debugDrawHandles }
                                    DebugToggleText(
                                        "Vectors",
                                        debugDrawVectors
                                    ) { debugDrawVectors = !debugDrawVectors }
                                    DebugToggleText(
                                        "Spans",
                                        debugDrawArcSpans
                                    ) { debugDrawArcSpans = !debugDrawArcSpans }
                                }

                                Spacer(Modifier.height(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
