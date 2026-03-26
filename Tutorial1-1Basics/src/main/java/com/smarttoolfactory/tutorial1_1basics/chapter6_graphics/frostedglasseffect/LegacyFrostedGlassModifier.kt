package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Picture
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Canvas as AndroidFrameworkCanvas
import android.graphics.Paint as AndroidPaint

private const val LEGACY_FALLBACK_BLUR_DOWNSAMPLE_FACTOR = 0.6f
private const val LEGACY_FALLBACK_BLUR_PASS_COUNT = 2
private const val LEGACY_FALLBACK_BLUR_STRENGTH = 0.55f

enum class LegacyFrostedGlassBitmapBlurAlgorithm {
    PixelBoxBlur,
    MaskFilter
}

@Stable
class LegacyFrostedGlassState internal constructor() {
    internal var targetBoundsInRoot by mutableStateOf(Rect.Zero)
    internal var targetShape by mutableStateOf<Shape>(RectangleShape)

    internal fun updateTarget(boundsInRoot: Rect, shape: Shape) {
        targetBoundsInRoot = boundsInRoot
        targetShape = shape
    }

    internal fun clearTarget() {
        targetBoundsInRoot = Rect.Zero
        targetShape = RectangleShape
    }
}

@Composable
fun rememberLegacyFrostedGlassState(): LegacyFrostedGlassState {
    return remember { LegacyFrostedGlassState() }
}

private data class LegacyBlurRequest(
    val picture: Picture,
    val sourceWidth: Int,
    val sourceHeight: Int,
    val downWidth: Int,
    val downHeight: Int,
    val blurRadius: Int,
    val algorithm: LegacyFrostedGlassBitmapBlurAlgorithm,
    val signature: LegacyPictureBlurRequestSignature
)

private data class LegacyBlurResult(
    val bitmap: Bitmap,
    val width: Int,
    val height: Int,
    val signature: LegacyPictureBlurRequestSignature
)

private class LegacyPictureBlurSubmissionState {
    var lastRequestSignature: LegacyPictureBlurRequestSignature? = null
}

private data class LegacyPictureBlurRequestSignature(
    val captureLeft: Int,
    val captureTop: Int,
    val captureWidth: Int,
    val captureHeight: Int,
    val downWidth: Int,
    val downHeight: Int,
    val blurRadius: Int,
    val backdropToken: Any
)

@Stable
private class LegacyAsyncBlurPipeline {

    private val requests = MutableSharedFlow<LegacyBlurRequest>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    var latestResult: LegacyBlurResult? by mutableStateOf(null)
        private set

    fun submit(request: LegacyBlurRequest) {
        requests.tryEmit(request)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process() {
        requests
            .mapLatest { request ->
                val downsampledBitmap = legacyCreateBitmapFromPicture(
                    picture = request.picture,
                    sourceWidth = request.sourceWidth,
                    sourceHeight = request.sourceHeight,
                    width = request.downWidth,
                    height = request.downHeight
                )
                val blurredBitmap = when (request.algorithm) {
                    LegacyFrostedGlassBitmapBlurAlgorithm.PixelBoxBlur -> {
                        legacyBlurBitmapWithPixelBoxBlur(
                            source = downsampledBitmap,
                            radius = request.blurRadius
                        )
                    }

                    LegacyFrostedGlassBitmapBlurAlgorithm.MaskFilter -> {
                        legacyBlurBitmapWithMaskFilter(
                            source = downsampledBitmap,
                            radiusPx = request.blurRadius.toFloat()
                        )
                    }
                }
                LegacyBlurResult(
                    bitmap = blurredBitmap,
                    width = request.downWidth,
                    height = request.downHeight,
                    signature = request.signature
                )
            }
            .flowOn(Dispatchers.Default)
            .collect { result ->
                latestResult = result
            }
    }
}

/**
 * Legacy frosted-glass host kept for side-by-side performance comparison with the current
 * implementation. This preserves the older invalidation and bitmap-allocation behavior.
 */
fun Modifier.legacyFrostedGlassHost(
    state: LegacyFrostedGlassState,
    blurRadius: Dp = 32.dp,
    implementationMode: FrostedGlassImplementationMode = FrostedGlassImplementationMode.Auto,
    bitmapBlurAlgorithm: LegacyFrostedGlassBitmapBlurAlgorithm =
        LegacyFrostedGlassBitmapBlurAlgorithm.PixelBoxBlur,
    legacyDownsampleFactor: Float = LEGACY_FALLBACK_BLUR_DOWNSAMPLE_FACTOR,
    legacyBlurStrength: Float = LEGACY_FALLBACK_BLUR_STRENGTH
): Modifier {
    return composed {
        val resolvedMode = legacyResolveFrostedGlassImplementationMode(
            implementationMode = implementationMode
        )

        val asyncPipeline: LegacyAsyncBlurPipeline? =
            if (resolvedMode == FrostedGlassImplementationMode.BitmapBlur) {
                val pipeline = remember { LegacyAsyncBlurPipeline() }
                LaunchedEffect(pipeline) { pipeline.process() }
                pipeline
            } else {
                null
            }
        val backdropRefreshState = remember { FrostedGlassBackdropRefreshState() }
        val blurSubmissionState = remember { LegacyPictureBlurSubmissionState() }

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
                val localTargetBounds = targetBoundsInRoot.legacyTranslateBy(
                    dx = -hostBoundsInRoot.left,
                    dy = -hostBoundsInRoot.top
                )
                val hostBounds = Rect(0f, 0f, size.width, size.height)
                val targetBounds = localTargetBounds.legacyIntersectWith(hostBounds)
                val canDrawBlur = targetBounds.legacyHasPositiveArea()
                val captureBounds = if (canDrawBlur) {
                    targetBounds.expandBy(blurPaddingPx).legacyIntersectWith(hostBounds)
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
                    LegacyFrostedGlassCaptureInfo(
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

                    resolvedMode == FrostedGlassImplementationMode.PlatformBlur -> {
                        legacyCreatePlatformFrostedGlassDrawBlock(
                            captureInfo = captureInfo,
                            blurLayer = obtainGraphicsLayer()
                                .legacyApplyPlatformBlur(blurRadiusPx = blurRadiusPx)
                        )
                    }

                    resolvedMode == FrostedGlassImplementationMode.BitmapBlur &&
                        asyncPipeline != null -> {
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

                        legacyCreatePictureCaptureBlurDrawBlock(
                            captureInfo = captureInfo,
                            pipeline = asyncPipeline,
                            submissionState = blurSubmissionState,
                            downW = downW,
                            downH = downH,
                            scaledBlurRadius = scaledRadius,
                            bitmapBlurAlgorithm = bitmapBlurAlgorithm,
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

/**
 * Legacy frosted-glass target kept for side-by-side performance comparison with the current
 * implementation.
 */
fun Modifier.legacyFrostedGlassTarget(
    state: LegacyFrostedGlassState,
    shape: Shape = RectangleShape
): Modifier = composed {
    SideEffect {
        state.targetShape = shape
    }

    DisposableEffect(state) {
        onDispose {
            state.clearTarget()
        }
    }

    Modifier.onGloballyPositioned { coordinates ->
        state.updateTarget(boundsInRoot = coordinates.boundsInRoot(), shape = shape)
    }
}

private data class LegacyFrostedGlassCaptureInfo(
    val targetBounds: Rect,
    val captureBounds: Rect,
    val intSize: IntSize,
    val outline: Outline,
    val clipPath: Path?
)

private fun Rect.legacyTranslateBy(dx: Float, dy: Float): Rect {
    return Rect(
        left = left + dx,
        top = top + dy,
        right = right + dx,
        bottom = bottom + dy
    )
}

private fun Rect.legacyIntersectWith(other: Rect): Rect {
    val left = max(left, other.left)
    val top = max(top, other.top)
    val right = min(right, other.right)
    val bottom = min(bottom, other.bottom)
    return if (left < right && top < bottom) Rect(left, top, right, bottom) else Rect.Zero
}

private fun Rect.legacyHasPositiveArea(): Boolean {
    return width > 0f && height > 0f
}

private fun legacyResolveFrostedGlassImplementationMode(
    implementationMode: FrostedGlassImplementationMode
): FrostedGlassImplementationMode {
    return when (implementationMode) {
        FrostedGlassImplementationMode.Auto -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FrostedGlassImplementationMode.PlatformBlur
            } else {
                FrostedGlassImplementationMode.BitmapBlur
            }
        }

        FrostedGlassImplementationMode.PlatformBlur -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FrostedGlassImplementationMode.PlatformBlur
            } else {
                FrostedGlassImplementationMode.BitmapBlur
            }
        }

        FrostedGlassImplementationMode.BitmapBlur -> implementationMode
    }
}

private fun GraphicsLayer.legacyApplyPlatformBlur(blurRadiusPx: Float): GraphicsLayer {
    return apply {
        renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect
                .createBlurEffect(
                    blurRadiusPx,
                    blurRadiusPx,
                    Shader.TileMode.DECAL
                )
                .asComposeRenderEffect()
        } else {
            null
        }
    }
}

private fun legacyCreatePlatformFrostedGlassDrawBlock(
    captureInfo: LegacyFrostedGlassCaptureInfo,
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
            legacyDrawClippedOutline(
                outline = captureInfo.outline,
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

private fun legacyCreatePictureCaptureBlurDrawBlock(
    captureInfo: LegacyFrostedGlassCaptureInfo,
    pipeline: LegacyAsyncBlurPipeline,
    submissionState: LegacyPictureBlurSubmissionState,
    downW: Int,
    downH: Int,
    scaledBlurRadius: Int,
    bitmapBlurAlgorithm: LegacyFrostedGlassBitmapBlurAlgorithm,
    backdropToken: Any
): ContentDrawScope.() -> Unit {
    val compositePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        isDither = true
    }

    return drawBlock@{
        val requestSignature = LegacyPictureBlurRequestSignature(
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
            val picture = Picture()
            val pictureCanvas = picture.beginRecording(
                captureInfo.intSize.width,
                captureInfo.intSize.height
            )
            pictureCanvas.translate(-captureInfo.captureBounds.left, -captureInfo.captureBounds.top)

            draw(
                density = this@drawBlock,
                layoutDirection = layoutDirection,
                canvas = Canvas(pictureCanvas),
                size = size
            ) {
                this@drawBlock.drawContent()
            }
            picture.endRecording()

            submissionState.lastRequestSignature = requestSignature
            pipeline.submit(
                LegacyBlurRequest(
                    picture = picture,
                    sourceWidth = captureInfo.intSize.width,
                    sourceHeight = captureInfo.intSize.height,
                    downWidth = downW,
                    downHeight = downH,
                    blurRadius = scaledBlurRadius,
                    algorithm = bitmapBlurAlgorithm,
                    signature = requestSignature
                )
            )
        }

        val blurResult = pipeline.latestResult
        if (blurResult != null &&
            blurResult.width == downW &&
            blurResult.height == downH &&
            blurResult.signature == requestSignature
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

private fun DrawScope.legacyDrawClippedBitmap(
    outline: Outline,
    width: Float,
    height: Float,
    bitmap: Bitmap,
    paint: AndroidPaint?
) {
    legacyDrawClippedOutline(outline = outline, width = width, height = height) {
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawBitmap(
                bitmap,
                null,
                RectF(
                    0f,
                    0f,
                    width.coerceAtLeast(1f),
                    height.coerceAtLeast(1f)
                ),
                paint
            )
        }
    }
}

private fun DrawScope.legacyDrawClippedOutline(
    outline: Outline,
    width: Float,
    height: Float,
    content: DrawScope.() -> Unit
) {
    when (outline) {
        is Outline.Rectangle -> {
            clipRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height
            ) {
                content()
            }
        }

        is Outline.Rounded -> {
            clipPath(
                Path().apply {
                    addRoundRect(outline.roundRect)
                }
            ) {
                content()
            }
        }

        is Outline.Generic -> {
            clipPath(outline.path) {
                content()
            }
        }
    }
}

private fun legacyCreateBitmapFromPicture(
    picture: Picture,
    sourceWidth: Int,
    sourceHeight: Int,
    width: Int,
    height: Int
): Bitmap {
    val targetWidth = width.coerceAtLeast(1)
    val targetHeight = height.coerceAtLeast(1)
    val bitmap = createBitmap(targetWidth, targetHeight)
    val canvas = AndroidFrameworkCanvas(bitmap)
    val scaleX = targetWidth.toFloat() / sourceWidth.coerceAtLeast(1)
    val scaleY = targetHeight.toFloat() / sourceHeight.coerceAtLeast(1)

    canvas.withScale(scaleX, scaleY) {
        drawPicture(picture)
    }

    return bitmap
}

private fun legacyBlurBitmapWithPixelBoxBlur(
    source: Bitmap,
    radius: Int
): Bitmap {
    val mutableBitmap = if (source.config == Bitmap.Config.ARGB_8888 && source.isMutable) {
        source
    } else {
        source.copy(Bitmap.Config.ARGB_8888, true)
    }
    val pixels = IntArray(mutableBitmap.width * mutableBitmap.height)
    mutableBitmap.getPixels(
        pixels,
        0,
        mutableBitmap.width,
        0,
        0,
        mutableBitmap.width,
        mutableBitmap.height
    )
    legacyBlurPixelsInPlace(
        pixels = pixels,
        width = mutableBitmap.width,
        height = mutableBitmap.height,
        radius = radius
    )
    mutableBitmap.setPixels(
        pixels,
        0,
        mutableBitmap.width,
        0,
        0,
        mutableBitmap.width,
        mutableBitmap.height
    )
    return mutableBitmap
}

private fun legacyBlurBitmapWithMaskFilter(
    source: Bitmap,
    radiusPx: Float
): Bitmap {
    val outputBitmap = Bitmap.createBitmap(
        source.width.coerceAtLeast(1),
        source.height.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val blurPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        isDither = true
        maskFilter = if (radiusPx > 0f) {
            BlurMaskFilter(radiusPx, BlurMaskFilter.Blur.NORMAL)
        } else {
            null
        }
    }
    AndroidFrameworkCanvas(outputBitmap).drawBitmap(source, 0f, 0f, blurPaint)
    return outputBitmap
}

private fun legacyBlurPixelsInPlace(
    pixels: IntArray,
    width: Int,
    height: Int,
    radius: Int
) {
    if (radius <= 0 || width <= 1 || height <= 1) return

    val buffer = IntArray(width * height)

    repeat(LEGACY_FALLBACK_BLUR_PASS_COUNT) {
        legacyBoxBlurHorizontal(
            input = pixels,
            output = buffer,
            width = width,
            height = height,
            radius = radius
        )
        legacyBoxBlurVertical(
            input = buffer,
            output = pixels,
            width = width,
            height = height,
            radius = radius
        )
    }
}

private fun legacyBoxBlurHorizontal(
    input: IntArray,
    output: IntArray,
    width: Int,
    height: Int,
    radius: Int
) {
    val windowSize = radius * 2 + 1

    for (y in 0 until height) {
        var alphaSum = 0
        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (sampleX in -radius..radius) {
            val color = input[y * width + sampleX.coerceIn(0, width - 1)]
            alphaSum += color ushr 24
            redSum += color shr 16 and 0xFF
            greenSum += color shr 8 and 0xFF
            blueSum += color and 0xFF
        }

        for (x in 0 until width) {
            output[y * width + x] = legacyPackColor(
                alpha = alphaSum / windowSize,
                red = redSum / windowSize,
                green = greenSum / windowSize,
                blue = blueSum / windowSize
            )

            val removeColor = input[y * width + (x - radius).coerceIn(0, width - 1)]
            val addColor = input[y * width + (x + radius + 1).coerceIn(0, width - 1)]

            alphaSum += (addColor ushr 24) - (removeColor ushr 24)
            redSum += ((addColor shr 16) and 0xFF) - ((removeColor shr 16) and 0xFF)
            greenSum += ((addColor shr 8) and 0xFF) - ((removeColor shr 8) and 0xFF)
            blueSum += (addColor and 0xFF) - (removeColor and 0xFF)
        }
    }
}

private fun legacyBoxBlurVertical(
    input: IntArray,
    output: IntArray,
    width: Int,
    height: Int,
    radius: Int
) {
    val windowSize = radius * 2 + 1

    for (x in 0 until width) {
        var alphaSum = 0
        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (sampleY in -radius..radius) {
            val color = input[sampleY.coerceIn(0, height - 1) * width + x]
            alphaSum += color ushr 24
            redSum += color shr 16 and 0xFF
            greenSum += color shr 8 and 0xFF
            blueSum += color and 0xFF
        }

        for (y in 0 until height) {
            output[y * width + x] = legacyPackColor(
                alpha = alphaSum / windowSize,
                red = redSum / windowSize,
                green = greenSum / windowSize,
                blue = blueSum / windowSize
            )

            val removeColor = input[(y - radius).coerceIn(0, height - 1) * width + x]
            val addColor = input[(y + radius + 1).coerceIn(0, height - 1) * width + x]

            alphaSum += (addColor ushr 24) - (removeColor ushr 24)
            redSum += ((addColor shr 16) and 0xFF) - ((removeColor shr 16) and 0xFF)
            greenSum += ((addColor shr 8) and 0xFF) - ((removeColor shr 8) and 0xFF)
            blueSum += (addColor and 0xFF) - (removeColor and 0xFF)
        }
    }
}

private fun legacyPackColor(alpha: Int, red: Int, green: Int, blue: Int): Int {
    return (alpha.coerceIn(0, 255) shl 24) or
        (red.coerceIn(0, 255) shl 16) or
        (green.coerceIn(0, 255) shl 8) or
        blue.coerceIn(0, 255)
}
