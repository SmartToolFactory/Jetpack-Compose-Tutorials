package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import android.graphics.Paint as AndroidPaint

// Enums
enum class FrostedGlassImplementationMode {
    Auto,
    PlatformBlur,
    BitmapBlur
}

// FrostedGlassState
@Stable
class FrostedGlassState internal constructor() {
    internal var targetBoundsInRoot by mutableStateOf(Rect.Zero)
    internal var targetShape by mutableStateOf<Shape>(RectangleShape)

    internal fun updateTarget(boundsInRoot: Rect, shape: Shape) {
        if (targetBoundsInRoot != boundsInRoot || targetShape != shape) {
            targetBoundsInRoot = boundsInRoot
            targetShape = shape
        }
    }

    internal fun clearTarget() {
        if (targetBoundsInRoot != Rect.Zero || targetShape != RectangleShape) {
            targetBoundsInRoot = Rect.Zero
            targetShape = RectangleShape
        }
    }
}

private class LegacyBlurSubmissionState {
    var lastRequestSignature: LegacyBlurRequestSignature? = null
}

@Composable
fun rememberFrostedGlassState(): FrostedGlassState {
    return remember { FrostedGlassState() }
}

// Host modifier
fun Modifier.frostedGlassHost(
    state: FrostedGlassState,
    blurRadius: Dp = 16.dp,
    implementationMode: FrostedGlassImplementationMode = FrostedGlassImplementationMode.Auto,
    legacyDownsampleFactor: Float = FALLBACK_BLUR_DOWNSAMPLE_FACTOR,
    legacyBlurStrength: Float = FALLBACK_BLUR_STRENGTH
): Modifier {
    return composed {
        val resolvedMode = resolveFrostedGlassImplementationMode(
            implementationMode = implementationMode
        )
        val canUseBlurCapture = LocalView.current.isHardwareAccelerated

        val asyncPipeline = remember { AsyncBlurPipeline() }
        val backdropRefreshState = remember { FrostedGlassBackdropRefreshState() }
        val blurSubmissionState = remember { LegacyBlurSubmissionState() }
        LaunchedEffect(asyncPipeline) { asyncPipeline.process() }

        val refreshModifier =
            if (resolvedMode == FrostedGlassImplementationMode.BitmapBlur) {
                Modifier.then(
                    FrostedGlassBitmapInvalidationElement(
                        backdropRefreshState = backdropRefreshState
                    )
                )
            } else {
                Modifier
            }

        var hostBoundsInRoot by remember { mutableStateOf(Rect.Zero) }

        refreshModifier
            .onGloballyPositioned { coordinates ->
                hostBoundsInRoot = coordinates.boundsInRoot()
            }
            .drawWithCache {
                val blurRadiusPx = blurRadius.toPx()
                val blurPaddingPx = blurRadiusPx.coerceAtLeast(1f)
                val clampedLegacyDownsampleFactor = legacyDownsampleFactor.coerceIn(0.35f, 1f)
                val clampedLegacyBlurStrength = legacyBlurStrength.coerceIn(0.15f, 1f)
                val backdropToken = backdropRefreshState.refreshToken

                val targetBoundsInRoot = state.targetBoundsInRoot
                val localTargetBounds = targetBoundsInRoot.translateBy(
                    dx = -hostBoundsInRoot.left,
                    dy = -hostBoundsInRoot.top
                )
                val hostBounds = Rect(0f, 0f, size.width, size.height)
                val targetBounds = localTargetBounds.intersectWith(hostBounds)
                val canDrawBlur = targetBounds.hasPositiveArea()
                val captureBounds = if (canDrawBlur) {
                    targetBounds.expandBy(blurPaddingPx).intersectWith(hostBounds)
                } else {
                    Rect.Zero
                }

                val captureWidth = captureBounds.width.roundToInt().coerceAtLeast(1)
                val captureHeight = captureBounds.height.roundToInt().coerceAtLeast(1)
                val captureIntSize = IntSize(captureWidth, captureHeight)

                val captureInfo = if (canDrawBlur) {
                    val outline = state.targetShape.createOutline(
                        size = Size(targetBounds.width, targetBounds.height),
                        layoutDirection = layoutDirection,
                        density = this
                    )
                    FrostedGlassCaptureInfo(
                        targetBounds = targetBounds,
                        captureBounds = captureBounds,
                        intSize = captureIntSize,
                        outline = outline,
                        clipPath = when (outline) {
                            is Outline.Rounded -> Path().apply {
                                addRoundRect(outline.roundRect)
                            }

                            is Outline.Generic -> outline.path
                            else -> null
                        }
                    )
                } else {
                    null
                }

                val blurDrawBlock: (ContentDrawScope.() -> Unit)? = when {
                    captureInfo == null -> null

                    resolvedMode == FrostedGlassImplementationMode.PlatformBlur &&
                        canUseBlurCapture -> {
                        createPlatformFrostedGlassDrawBlock(
                            captureInfo = captureInfo,
                            blurLayer = obtainGraphicsLayer()
                                .applyPlatformBlur(blurRadiusPx = blurRadiusPx)
                        )
                    }

                    resolvedMode == FrostedGlassImplementationMode.BitmapBlur &&
                        canUseBlurCapture -> {
                        val downW = (captureIntSize.width * clampedLegacyDownsampleFactor)
                            .roundToInt().coerceAtLeast(1)
                        val downH = (captureIntSize.height * clampedLegacyDownsampleFactor)
                            .roundToInt().coerceAtLeast(1)
                        val scaledRadius = (
                            blurRadiusPx *
                                clampedLegacyDownsampleFactor *
                                clampedLegacyBlurStrength
                            )
                            .roundToInt()
                            .coerceAtLeast(1)

                        createLayerCaptureBlurDrawBlock(
                            captureInfo = captureInfo,
                            captureLayer = obtainGraphicsLayer(),
                            pipeline = asyncPipeline,
                            submissionState = blurSubmissionState,
                            downW = downW,
                            downH = downH,
                            scaledBlurRadius = scaledRadius,
                            backdropToken = backdropToken
                        )
                    }

                    else -> null
                }

                onDrawWithContent {
                    drawContent()
                    blurDrawBlock?.invoke(this)
                }
            }
    }
}

// Target modifier
fun Modifier.frostedGlassTarget(
    state: FrostedGlassState,
    shape: Shape = RectangleShape
): Modifier = this.then(
    FrostedGlassTargetElement(
        state = state,
        shape = shape
    )
)

private data class FrostedGlassTargetElement(
    val state: FrostedGlassState,
    val shape: Shape
) : ModifierNodeElement<FrostedGlassTargetNode>() {

    override fun create(): FrostedGlassTargetNode {
        return FrostedGlassTargetNode(
            state = state,
            shape = shape
        )
    }

    override fun update(node: FrostedGlassTargetNode) {
        if (node.state !== state) {
            node.state.clearTarget()
        }
        node.state = state
        node.shape = shape
        node.syncTarget()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "frostedGlassTarget"
        properties["shape"] = shape
    }
}

private class FrostedGlassTargetNode(
    var state: FrostedGlassState,
    var shape: Shape
) : Modifier.Node(),
    GlobalPositionAwareModifierNode {

    private var boundsInRoot: Rect = Rect.Zero

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        boundsInRoot = coordinates.boundsInRoot()
        syncTarget()
    }

    fun syncTarget() {
        if (isAttached) {
            state.updateTarget(boundsInRoot = boundsInRoot, shape = shape)
        }
    }

    override fun onDetach() {
        state.clearTarget()
    }
}

// Internal data types
private data class FrostedGlassCaptureInfo(
    val targetBounds: Rect,
    val captureBounds: Rect,
    val intSize: IntSize,
    val outline: Outline,
    val clipPath: Path?
)

private data class LegacyBlurRequestSignature(
    val captureLeft: Int,
    val captureTop: Int,
    val captureWidth: Int,
    val captureHeight: Int,
    val downWidth: Int,
    val downHeight: Int,
    val blurRadius: Int,
    val backdropToken: Any
)

private fun createPlatformFrostedGlassDrawBlock(
    captureInfo: FrostedGlassCaptureInfo,
    blurLayer: GraphicsLayer
): ContentDrawScope.() -> Unit {
    return drawBlock@{
        blurLayer.record(size = captureInfo.intSize) {
            translate(-captureInfo.captureBounds.left, -captureInfo.captureBounds.top) {
                this@drawBlock.drawContent()
            }
        }

        val sourceOffsetX = captureInfo.targetBounds.left - captureInfo.captureBounds.left
        val sourceOffsetY = captureInfo.targetBounds.top - captureInfo.captureBounds.top

        translate(captureInfo.targetBounds.left, captureInfo.targetBounds.top) {
            drawClippedOutline(
                outline = captureInfo.outline,
                clipPath = captureInfo.clipPath,
                width = captureInfo.targetBounds.width,
                height = captureInfo.targetBounds.height
            ) {
                translate(-sourceOffsetX, -sourceOffsetY) {
                    drawLayer(blurLayer)
                }
            }
        }
    }
}

private fun createLayerCaptureBlurDrawBlock(
    captureInfo: FrostedGlassCaptureInfo,
    captureLayer: GraphicsLayer,
    pipeline: AsyncBlurPipeline,
    submissionState: LegacyBlurSubmissionState,
    downW: Int,
    downH: Int,
    scaledBlurRadius: Int,
    backdropToken: Any
): ContentDrawScope.() -> Unit {
    val compositePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        isDither = true
    }

    return drawBlock@{
        val requestSignature = LegacyBlurRequestSignature(
            captureLeft = captureInfo.captureBounds.left.roundToInt(),
            captureTop = captureInfo.captureBounds.top.roundToInt(),
            captureWidth = captureInfo.intSize.width,
            captureHeight = captureInfo.intSize.height,
            downWidth = downW,
            downHeight = downH,
            blurRadius = scaledBlurRadius,
            backdropToken = backdropToken
        )
        if (submissionState.lastRequestSignature != requestSignature) {
            captureLayer.record(size = captureInfo.intSize) {
                translate(-captureInfo.captureBounds.left, -captureInfo.captureBounds.top) {
                    this@drawBlock.drawContent()
                }
            }
            submissionState.lastRequestSignature = requestSignature
            pipeline.submit(
                BlurRequest(
                    captureLayer = captureLayer,
                    sourceWidth = captureInfo.intSize.width,
                    sourceHeight = captureInfo.intSize.height,
                    downWidth = downW,
                    downHeight = downH,
                    blurRadius = scaledBlurRadius
                )
            )
        }

        val blurResult = pipeline.latestResult
        if (blurResult != null &&
            blurResult.width == downW &&
            blurResult.height == downH
        ) {
            val sourceRect = createBitmapSourceRect(
                targetBounds = captureInfo.targetBounds,
                captureBounds = captureInfo.captureBounds,
                bitmapWidth = blurResult.width,
                bitmapHeight = blurResult.height
            )

            translate(captureInfo.targetBounds.left, captureInfo.targetBounds.top) {
                drawClippedBitmapSubset(
                    outline = captureInfo.outline,
                    clipPath = captureInfo.clipPath,
                    width = captureInfo.targetBounds.width,
                    height = captureInfo.targetBounds.height,
                    bitmap = blurResult.bitmap,
                    sourceRect = sourceRect,
                    paint = compositePaint
                )
            }
        }
    }
}
