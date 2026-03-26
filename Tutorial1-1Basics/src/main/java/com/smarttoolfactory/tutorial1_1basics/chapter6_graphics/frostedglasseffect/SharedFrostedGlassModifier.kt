package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Paint as AndroidPaint

/**
 * Host-level configuration for the BitmapBlur (legacy CPU) path.
 * Has no effect when [com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect.FrostedGlassImplementationMode.PlatformBlur] is resolved.
 *
 * @param clipContentUntilReady Clips host content out of target regions until the
 *   async pipeline delivers its first blur result, preventing host content (e.g. text)
 *   from flickering through before the blur is ready.
 * @param stableCaptureBounds When `true`, captures the **full host bounds** instead of
 *   the tight union around targets. This keeps capture dimensions constant so stale
 *   blur results remain valid when targets are added or removed dynamically
 *   (e.g. pager pages appearing/disappearing during a swipe). **Only enable when the
 *   target count changes at runtime.** For a fixed set of targets (e.g. a single
 *   TopAppBar over a LazyColumn) leave `false` to blur a much smaller bitmap each frame.
 */
@Stable
data class LegacyBlurHostConfig(
    val clipContentUntilReady: Boolean = true,
    val stableCaptureBounds: Boolean = false
)

@Stable
data class FrostedGlassBlurProfile(
    val blurRadius: Dp = 32.dp,
    val implementationMode: FrostedGlassImplementationMode =
        FrostedGlassImplementationMode.Auto,
    val legacyDownsampleFactor: Float = FALLBACK_BLUR_DOWNSAMPLE_FACTOR,
    val legacyBlurStrength: Float = FALLBACK_BLUR_STRENGTH
)

internal data class SharedFrostedGlassTargetSnapshot(
    val boundsInRoot: Rect,
    val shape: Shape,
    val profileOverride: FrostedGlassBlurProfile?
)

@Stable
class SharedFrostedGlassState internal constructor() {
    internal val targets = mutableStateMapOf<Any, SharedFrostedGlassTargetSnapshot>()

    internal fun updateTarget(
        key: Any,
        boundsInRoot: Rect,
        shape: Shape,
        profileOverride: FrostedGlassBlurProfile?
    ) {
        val existing = targets[key]
        if (
            existing?.boundsInRoot == boundsInRoot &&
            existing.shape == shape &&
            existing.profileOverride == profileOverride
        ) {
            return
        }
        targets[key] = SharedFrostedGlassTargetSnapshot(
            boundsInRoot = boundsInRoot,
            shape = shape,
            profileOverride = profileOverride
        )
    }

    internal fun clearTarget(key: Any) {
        targets.remove(key)
    }
}

private class SharedBlurSubmissionState {
    var lastRequestSignature: SharedBlurRequestSignature? = null
}

private class SharedBlurPipelineEntry(
    val pipeline: AsyncBlurPipeline,
    val submissionState: SharedBlurSubmissionState,
    val job: Job
)

private class SharedBlurPipelineStore {
    private val entries = LinkedHashMap<FrostedGlassBlurProfile, SharedBlurPipelineEntry>()

    fun syncProfiles(
        profiles: Set<FrostedGlassBlurProfile>,
        coroutineScope: kotlinx.coroutines.CoroutineScope
    ) {
        val iterator = entries.iterator()
        while (iterator.hasNext()) {
            val (profile, entry) = iterator.next()
            if (profile !in profiles) {
                entry.job.cancel()
                iterator.remove()
            }
        }

        profiles.forEach { profile ->
            if (entries.containsKey(profile).not()) {
                val pipeline = AsyncBlurPipeline()
                val submissionState = SharedBlurSubmissionState()
                val job = coroutineScope.launch {
                    pipeline.process()
                }
                entries[profile] = SharedBlurPipelineEntry(
                    pipeline = pipeline,
                    submissionState = submissionState,
                    job = job
                )
            }
        }
    }

    fun entryFor(profile: FrostedGlassBlurProfile): SharedBlurPipelineEntry? {
        return entries[profile]
    }

    fun dispose() {
        entries.values.forEach { entry ->
            entry.job.cancel()
        }
        entries.clear()
    }
}

@Composable
fun rememberSharedFrostedGlassState(): SharedFrostedGlassState {
    return remember { SharedFrostedGlassState() }
}

/**
 * Shared multi-target frosted-glass host that captures and blurs the combined target region
 * once, then reuses that result for every registered target.
 *
 * @param state Shared state that connects this host with its [sharedFrostedGlassTarget]s.
 * @param blurRadius Blur radius applied to the captured content.
 * @param implementationMode Which blur backend to use (GPU RenderEffect or CPU bitmap blur).
 * @param legacyDownsampleFactor *(BitmapBlur only)* Downsample factor for the CPU bitmap
 *   blur path (0.35–1.0). Lower values reduce cost at the expense of quality.
 * @param legacyBlurStrength *(BitmapBlur only)* Strength multiplier for the CPU bitmap
 *   blur radius (0.15–1.0).
 * @param legacyBlurHostConfig *(BitmapBlur only)* Host-level behaviour for the async
 *   bitmap blur pipeline. See [LegacyBlurHostConfig].
 */
fun Modifier.sharedFrostedGlassHost(
    state: SharedFrostedGlassState,
    blurRadius: Dp = 32.dp,
    implementationMode: FrostedGlassImplementationMode = FrostedGlassImplementationMode.Auto,
    legacyDownsampleFactor: Float = FALLBACK_BLUR_DOWNSAMPLE_FACTOR,
    legacyBlurStrength: Float = FALLBACK_BLUR_STRENGTH,
    legacyBlurHostConfig: LegacyBlurHostConfig = LegacyBlurHostConfig()
): Modifier {
    return composed {
        val defaultProfile = FrostedGlassBlurProfile(
            blurRadius = blurRadius,
            implementationMode = implementationMode,
            legacyDownsampleFactor = legacyDownsampleFactor,
            legacyBlurStrength = legacyBlurStrength
        )
        val canUseBlurCapture = LocalView.current.isHardwareAccelerated
        val coroutineScope = rememberCoroutineScope()
        val pipelineStore = remember { SharedBlurPipelineStore() }
        val backdropRefreshState = remember { FrostedGlassBackdropRefreshState() }
        val activeProfiles = state.targets.values
            .map { snapshot -> snapshot.profileOverride ?: defaultProfile }
            .ifEmpty { listOf(defaultProfile) }
            .toSet()

        DisposableEffect(pipelineStore) {
            onDispose {
                pipelineStore.dispose()
            }
        }

        pipelineStore.syncProfiles(
            profiles = activeProfiles,
            coroutineScope = coroutineScope
        )

        val refreshModifier = Modifier.then(
            FrostedGlassBitmapInvalidationElement(
                backdropRefreshState = backdropRefreshState
            )
        )

        var hostBoundsInRoot by remember { androidx.compose.runtime.mutableStateOf(Rect.Zero) }

        refreshModifier
            .onGloballyPositioned { coordinates ->
                hostBoundsInRoot = coordinates.boundsInRoot()
            }
            .drawWithCache {
                val backdropToken = backdropRefreshState.refreshToken
                val hostBounds = Rect(0f, 0f, size.width, size.height)
                val targetSnapshots = state.targets.values.toList()
                val resolvedTargets = targetSnapshots.mapNotNull { snapshot ->
                    val localBounds = snapshot.boundsInRoot.translateBy(
                        dx = -hostBoundsInRoot.left,
                        dy = -hostBoundsInRoot.top
                    ).intersectWith(hostBounds)

                    if (!localBounds.hasPositiveArea()) {
                        null
                    } else {
                        val profile = snapshot.profileOverride ?: defaultProfile
                        val outline = snapshot.shape.createOutline(
                            size = Size(localBounds.width, localBounds.height),
                            layoutDirection = layoutDirection,
                            density = this
                        )
                        SharedFrostedGlassCaptureTarget(
                            profile = profile,
                            bounds = localBounds,
                            outline = outline,
                            clipPath = when (outline) {
                                is Outline.Rounded -> Path().apply {
                                    addRoundRect(outline.roundRect)
                                }

                                is Outline.Generic -> outline.path
                                else -> null
                            }
                        )
                    }
                }
                val captureGroups = resolvedTargets
                    .groupBy { target -> target.profile }
                    .mapNotNull { (profile, targets) ->
                        val entry = pipelineStore.entryFor(profile) ?: return@mapNotNull null
                        val resolvedMode = resolveFrostedGlassImplementationMode(
                            implementationMode = profile.implementationMode
                        )
                        val captureBounds = when {
                            // Full-host capture keeps dimensions constant so
                            // stale results stay valid while targets animate.
                            resolvedMode ==
                                FrostedGlassImplementationMode.BitmapBlur &&
                                legacyBlurHostConfig.stableCaptureBounds -> hostBounds
                            else -> {
                                val unionBounds = targets.unionBounds()
                                unionBounds
                                    .expandBy(
                                        profile.blurRadius.toPx().coerceAtLeast(1f)
                                    )
                                    .intersectWith(hostBounds)
                            }
                        }
                        SharedFrostedGlassCaptureGroup(
                            profile = profile,
                            resolvedMode = resolvedMode,
                            pipeline = entry.pipeline,
                            submissionState = entry.submissionState,
                            captureInfo = SharedFrostedGlassCaptureInfo(
                                captureBounds = captureBounds,
                                intSize = IntSize(
                                    captureBounds.width.roundToInt().coerceAtLeast(1),
                                    captureBounds.height.roundToInt().coerceAtLeast(1)
                                ),
                                targets = targets
                            )
                        )
                    }
                val blurDrawBlocks = captureGroups.mapNotNull { group ->
                    when (group.resolvedMode) {
                        FrostedGlassImplementationMode.PlatformBlur -> {
                            if (!canUseBlurCapture) {
                                null
                            } else {
                                createSharedPlatformFrostedGlassDrawBlock(
                                    captureInfo = group.captureInfo,
                                    blurLayer = obtainGraphicsLayer()
                                        .applyPlatformBlur(
                                            blurRadiusPx = group.profile.blurRadius.toPx()
                                        )
                                )
                            }
                        }

                        FrostedGlassImplementationMode.BitmapBlur -> {
                            if (!canUseBlurCapture) {
                                null
                            } else {
                                val clampedLegacyDownsampleFactor =
                                    group.profile.legacyDownsampleFactor.coerceIn(0.35f, 1f)
                                val clampedLegacyBlurStrength =
                                    group.profile.legacyBlurStrength.coerceIn(0.15f, 1f)
                                val downW =
                                    (
                                        group.captureInfo.intSize.width *
                                            clampedLegacyDownsampleFactor
                                        )
                                        .roundToInt().coerceAtLeast(1)
                                val downH =
                                    (
                                        group.captureInfo.intSize.height *
                                            clampedLegacyDownsampleFactor
                                        )
                                        .roundToInt().coerceAtLeast(1)
                                val scaledRadius = (
                                    group.profile.blurRadius.toPx() *
                                        clampedLegacyDownsampleFactor *
                                        clampedLegacyBlurStrength
                                    )
                                    .roundToInt()
                                    .coerceAtLeast(1)

                                createSharedLayerCaptureBlurDrawBlock(
                                    captureInfo = group.captureInfo,
                                    captureLayer = obtainGraphicsLayer(),
                                    pipeline = group.pipeline,
                                    submissionState = group.submissionState,
                                    downW = downW,
                                    downH = downH,
                                    scaledBlurRadius = scaledRadius,
                                    backdropToken = backdropToken
                                )
                            }
                        }

                        FrostedGlassImplementationMode.Auto -> null
                    }
                }

                onDrawWithContent {
                    val contentScope = this
                    if (legacyBlurHostConfig.clipContentUntilReady) {
                        val pendingTargets = captureGroups
                            .filter { group ->
                                group.resolvedMode ==
                                    FrostedGlassImplementationMode.BitmapBlur &&
                                    group.pipeline.latestResult == null
                            }
                            .flatMap { group -> group.captureInfo.targets }
                        if (pendingTargets.isNotEmpty()) {
                            val exclusionPath = Path()
                            pendingTargets.forEach { target ->
                                exclusionPath.addRect(target.bounds)
                            }
                            clipPath(
                                exclusionPath,
                                clipOp = ClipOp.Difference
                            ) {
                                contentScope.drawContent()
                            }
                        } else {
                            drawContent()
                        }
                    } else {
                        drawContent()
                    }
                    blurDrawBlocks.forEach { drawBlock ->
                        drawBlock.invoke(this)
                    }
                }
            }
    }
}

/**
 * Shared multi-target frosted-glass target. Register multiple targets against the same shared
 * host state to capture and blur their combined region only once.
 */
fun Modifier.sharedFrostedGlassTarget(
    state: SharedFrostedGlassState,
    key: Any,
    shape: Shape = RectangleShape,
    profile: FrostedGlassBlurProfile? = null
): Modifier = this.then(
    SharedFrostedGlassTargetElement(
        state = state,
        key = key,
        shape = shape,
        profile = profile
    )
)

private data class SharedFrostedGlassTargetElement(
    val state: SharedFrostedGlassState,
    val key: Any,
    val shape: Shape,
    val profile: FrostedGlassBlurProfile?
) : ModifierNodeElement<SharedFrostedGlassTargetNode>() {

    override fun create(): SharedFrostedGlassTargetNode {
        return SharedFrostedGlassTargetNode(
            state = state,
            key = key,
            shape = shape,
            profile = profile
        )
    }

    override fun update(node: SharedFrostedGlassTargetNode) {
        if (node.state !== state || node.key != key) {
            node.state.clearTarget(node.key)
        }
        node.state = state
        node.key = key
        node.shape = shape
        node.profile = profile
        node.syncTarget()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "sharedFrostedGlassTarget"
        properties["key"] = key
        properties["shape"] = shape
        properties["profile"] = profile
    }
}

private class SharedFrostedGlassTargetNode(
    var state: SharedFrostedGlassState,
    var key: Any,
    var shape: Shape,
    var profile: FrostedGlassBlurProfile?
) : Modifier.Node(),
    GlobalPositionAwareModifierNode {

    private var boundsInRoot: Rect = Rect.Zero

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        boundsInRoot = coordinates.boundsInRoot()
        syncTarget()
    }

    fun syncTarget() {
        if (isAttached) {
            state.updateTarget(
                key = key,
                boundsInRoot = boundsInRoot,
                shape = shape,
                profileOverride = profile
            )
        }
    }

    override fun onDetach() {
        state.clearTarget(key)
    }
}

private data class SharedFrostedGlassCaptureTarget(
    val profile: FrostedGlassBlurProfile,
    val bounds: Rect,
    val outline: Outline,
    val clipPath: Path?
)

private data class SharedFrostedGlassCaptureInfo(
    val captureBounds: Rect,
    val intSize: IntSize,
    val targets: List<SharedFrostedGlassCaptureTarget>
)

private data class SharedBlurRequestSignature(
    val captureLeft: Int,
    val captureTop: Int,
    val captureWidth: Int,
    val captureHeight: Int,
    val downWidth: Int,
    val downHeight: Int,
    val blurRadius: Int,
    val backdropToken: Any
)

private data class SharedFrostedGlassCaptureGroup(
    val profile: FrostedGlassBlurProfile,
    val resolvedMode: FrostedGlassImplementationMode,
    val pipeline: AsyncBlurPipeline,
    val submissionState: SharedBlurSubmissionState,
    val captureInfo: SharedFrostedGlassCaptureInfo
)

private fun List<SharedFrostedGlassCaptureTarget>.unionBounds(): Rect {
    val first = first()
    return drop(1).fold(first.bounds) { acc, target ->
        Rect(
            left = min(acc.left, target.bounds.left),
            top = min(acc.top, target.bounds.top),
            right = max(acc.right, target.bounds.right),
            bottom = max(acc.bottom, target.bounds.bottom)
        )
    }
}

private fun createSharedPlatformFrostedGlassDrawBlock(
    captureInfo: SharedFrostedGlassCaptureInfo,
    blurLayer: GraphicsLayer
): ContentDrawScope.() -> Unit {
    return drawBlock@{
        blurLayer.record(size = captureInfo.intSize) {
            translate(-captureInfo.captureBounds.left, -captureInfo.captureBounds.top) {
                this@drawBlock.drawContent()
            }
        }

        captureInfo.targets.forEach { target ->
            val sourceOffsetX = target.bounds.left - captureInfo.captureBounds.left
            val sourceOffsetY = target.bounds.top - captureInfo.captureBounds.top

            translate(target.bounds.left, target.bounds.top) {
                drawClippedOutline(
                    outline = target.outline,
                    clipPath = target.clipPath,
                    width = target.bounds.width,
                    height = target.bounds.height
                ) {
                    translate(-sourceOffsetX, -sourceOffsetY) {
                        drawLayer(blurLayer)
                    }
                }
            }
        }
    }
}

private fun createSharedLayerCaptureBlurDrawBlock(
    captureInfo: SharedFrostedGlassCaptureInfo,
    captureLayer: GraphicsLayer,
    pipeline: AsyncBlurPipeline,
    submissionState: SharedBlurSubmissionState,
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
        val requestSignature = SharedBlurRequestSignature(
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
                    blurRadius = scaledBlurRadius,
                    captureBounds = captureInfo.captureBounds
                )
            )
        }

        val blurResult = pipeline.latestResult
        if (blurResult != null) {
            val resultCaptureBounds = if (
                blurResult.width == downW && blurResult.height == downH
            ) {
                captureInfo.captureBounds
            } else {
                blurResult.captureBounds
            }

            captureInfo.targets.forEach { target ->
                val sourceRect = createBitmapSourceRect(
                    targetBounds = target.bounds,
                    captureBounds = resultCaptureBounds,
                    bitmapWidth = blurResult.width,
                    bitmapHeight = blurResult.height
                )

                translate(target.bounds.left, target.bounds.top) {
                    drawClippedBitmapSubset(
                        outline = target.outline,
                        clipPath = target.clipPath,
                        width = target.bounds.width,
                        height = target.bounds.height,
                        bitmap = blurResult.bitmap,
                        sourceRect = sourceRect,
                        paint = compositePaint
                    )
                }
            }
        }
    }
}
