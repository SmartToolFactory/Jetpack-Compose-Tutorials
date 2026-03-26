package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Canvas as AndroidFrameworkCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Rect as AndroidRect

internal const val FALLBACK_BLUR_DOWNSAMPLE_FACTOR = 0.6f
internal const val FALLBACK_BLUR_STRENGTH = 0.55f
private const val FALLBACK_BLUR_PASS_COUNT = 2

@Stable
internal class FrostedGlassBackdropRefreshState {
    var refreshToken by mutableStateOf(Any())
        private set

    fun markChanged() {
        refreshToken = Any()
    }
}

internal data class BlurRequest(
    val captureLayer: GraphicsLayer,
    val sourceWidth: Int,
    val sourceHeight: Int,
    val downWidth: Int,
    val downHeight: Int,
    val blurRadius: Int,
    val captureBounds: Rect = Rect.Zero
)

internal data class BlurResult(
    val bitmap: Bitmap,
    val width: Int,
    val height: Int,
    val captureBounds: Rect = Rect.Zero
)

@Stable
internal class AsyncBlurPipeline {

    private val requests = MutableSharedFlow<BlurRequest>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    var latestResult: BlurResult? by mutableStateOf(null, neverEqualPolicy())
        private set

    private var workerBitmapPrimary: Bitmap? = null
    private var workerBitmapSecondary: Bitmap? = null

    @Volatile
    private var displayedResultBitmap: Bitmap? = null
    private var pooledPixels: IntArray? = null
    private var pooledScratchPixels: IntArray? = null

    fun submit(request: BlurRequest) {
        requests.tryEmit(request)
    }

    suspend fun process() {
        requests.collectLatest { request ->
            val coroutineContext = currentCoroutineContext()
            coroutineContext.ensureActive()

            // Snapshot the recorded layer on the main thread, then normalize it to a
            // software bitmap before sending it to the CPU blur pipeline.
            val capturedBitmap = ensureSoftwareBitmap(
                request.captureLayer.toImageBitmap().asAndroidBitmap()
            )

            // Resize into a pooled working bitmap and run the blur off the main thread.
            val result = withContext(Dispatchers.Default) {
                coroutineContext.ensureActive()

                val resultBitmap = obtainNextResultBitmap(
                    width = request.downWidth,
                    height = request.downHeight
                )
                coroutineContext.ensureActive()
                renderBitmapIntoBitmap(
                    source = capturedBitmap,
                    sourceWidth = request.sourceWidth,
                    sourceHeight = request.sourceHeight,
                    target = resultBitmap
                )
                coroutineContext.ensureActive()
                val blurredBitmap = blurBitmapWithPixelBoxBlur(
                    source = resultBitmap,
                    radius = request.blurRadius,
                    pixels = obtainPixels(resultBitmap.width * resultBitmap.height),
                    scratch = obtainScratchPixels(resultBitmap.width * resultBitmap.height),
                    checkCancelled = { coroutineContext.ensureActive() }
                )
                coroutineContext.ensureActive()
                BlurResult(
                    blurredBitmap,
                    request.downWidth,
                    request.downHeight,
                    request.captureBounds
                )
            }

            // Publish the finished frame and protect its bitmap from worker reuse until
            // a newer result replaces it on the UI thread.
            latestResult = result
            displayedResultBitmap = result.bitmap
        }
    }

    private fun obtainNextResultBitmap(width: Int, height: Int): Bitmap {
        val protectedDisplayedBitmap = displayedResultBitmap
        return if (workerBitmapPrimary !== protectedDisplayedBitmap) {
            obtainReusableBitmap(workerBitmapPrimary, width, height).also {
                workerBitmapPrimary = it
            }
        } else {
            obtainReusableBitmap(workerBitmapSecondary, width, height).also {
                workerBitmapSecondary = it
            }
        }
    }

    private fun obtainReusableBitmap(
        current: Bitmap?,
        width: Int,
        height: Int
    ): Bitmap {
        val targetWidth = width.coerceAtLeast(1)
        val targetHeight = height.coerceAtLeast(1)
        return if (
            current != null &&
            current.width == targetWidth &&
            current.height == targetHeight &&
            current.isMutable &&
            !current.isRecycled &&
            current.config == Bitmap.Config.ARGB_8888
        ) {
            current.eraseColor(0)
            current
        } else {
            createBitmap(targetWidth, targetHeight)
        }
    }

    private fun obtainPixels(size: Int): IntArray {
        val existing = pooledPixels
        return if (existing != null && existing.size >= size) {
            existing
        } else {
            IntArray(size).also { pooledPixels = it }
        }
    }

    private fun obtainScratchPixels(size: Int): IntArray {
        val existing = pooledScratchPixels
        return if (existing != null && existing.size >= size) {
            existing
        } else {
            IntArray(size).also { pooledScratchPixels = it }
        }
    }
}

internal data class FrostedGlassBitmapInvalidationElement(
    val backdropRefreshState: FrostedGlassBackdropRefreshState
) :
    ModifierNodeElement<FrostedGlassBitmapInvalidationNode>() {

    override fun create(): FrostedGlassBitmapInvalidationNode {
        return FrostedGlassBitmapInvalidationNode(
            backdropRefreshState = backdropRefreshState
        )
    }

    override fun update(node: FrostedGlassBitmapInvalidationNode) {
        node.backdropRefreshState = backdropRefreshState
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "frostedGlassBitmapInvalidation"
    }
}

internal class FrostedGlassBitmapInvalidationNode(
    var backdropRefreshState: FrostedGlassBackdropRefreshState
) :
    Modifier.Node(),
    DrawModifierNode,
    ObserverModifierNode {

    override fun ContentDrawScope.draw() {
        observeReads {
            drawContent()
        }
    }

    override fun onObservedReadsChanged() {
        backdropRefreshState.markChanged()
        invalidateDraw()
    }
}

internal fun resolveFrostedGlassImplementationMode(
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

internal fun GraphicsLayer.applyPlatformBlur(blurRadiusPx: Float): GraphicsLayer {
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

internal fun Rect.translateBy(dx: Float, dy: Float): Rect {
    return Rect(
        left = left + dx,
        top = top + dy,
        right = right + dx,
        bottom = bottom + dy
    )
}

internal fun Rect.expandBy(padding: Float): Rect {
    return Rect(
        left = left - padding,
        top = top - padding,
        right = right + padding,
        bottom = bottom + padding
    )
}

internal fun Rect.intersectWith(other: Rect): Rect {
    val left = max(this.left, other.left)
    val top = max(this.top, other.top)
    val right = min(this.right, other.right)
    val bottom = min(this.bottom, other.bottom)
    return if (left < right && top < bottom) Rect(left, top, right, bottom) else Rect.Zero
}

internal fun Rect.hasPositiveArea(): Boolean {
    return width > 0f && height > 0f
}

internal fun DrawScope.drawClippedBitmap(
    outline: Outline,
    clipPath: Path?,
    width: Float,
    height: Float,
    bitmap: Bitmap,
    paint: AndroidPaint?
) {
    drawClippedOutline(
        outline = outline,
        clipPath = clipPath,
        width = width,
        height = height
    ) {
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

internal fun DrawScope.drawClippedBitmapSubset(
    outline: Outline,
    clipPath: Path?,
    width: Float,
    height: Float,
    bitmap: Bitmap,
    sourceRect: AndroidRect,
    paint: AndroidPaint?
) {
    drawClippedOutline(
        outline = outline,
        clipPath = clipPath,
        width = width,
        height = height
    ) {
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawBitmap(
                bitmap,
                sourceRect,
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

internal fun createBitmapSourceRect(
    targetBounds: Rect,
    captureBounds: Rect,
    bitmapWidth: Int,
    bitmapHeight: Int
): AndroidRect {
    val sourceLeft = (
        (targetBounds.left - captureBounds.left) * bitmapWidth / captureBounds.width
        )
        .roundToInt()
        .coerceIn(0, bitmapWidth - 1)
    val sourceTop = (
        (targetBounds.top - captureBounds.top) * bitmapHeight / captureBounds.height
        )
        .roundToInt()
        .coerceIn(0, bitmapHeight - 1)
    val sourceRight = (
        (targetBounds.right - captureBounds.left) * bitmapWidth / captureBounds.width
        )
        .roundToInt()
        .coerceIn(sourceLeft + 1, bitmapWidth)
    val sourceBottom =
        ((targetBounds.bottom - captureBounds.top) * bitmapHeight / captureBounds.height)
            .roundToInt()
            .coerceIn(sourceTop + 1, bitmapHeight)

    return AndroidRect(sourceLeft, sourceTop, sourceRight, sourceBottom)
}

internal fun DrawScope.drawClippedOutline(
    outline: Outline,
    clipPath: Path?,
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
            clipPath(requireNotNull(clipPath)) {
                content()
            }
        }

        is Outline.Generic -> {
            clipPath(requireNotNull(clipPath)) {
                content()
            }
        }
    }
}

private fun ensureSoftwareBitmap(source: Bitmap): Bitmap {
    val isHardwareBitmap = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        source.config == Bitmap.Config.HARDWARE
    return if (
        isHardwareBitmap ||
        source.config != Bitmap.Config.ARGB_8888
    ) {
        source.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        source
    }
}

private fun renderBitmapIntoBitmap(
    source: Bitmap,
    sourceWidth: Int,
    sourceHeight: Int,
    target: Bitmap
) {
    target.eraseColor(0)
    val canvas = AndroidFrameworkCanvas(target)
    val scaleX = target.width.toFloat() / sourceWidth.coerceAtLeast(1)
    val scaleY = target.height.toFloat() / sourceHeight.coerceAtLeast(1)

    canvas.withScale(scaleX, scaleY) {
        drawBitmap(source, 0f, 0f, null)
    }
}

private fun blurBitmapWithPixelBoxBlur(
    source: Bitmap,
    radius: Int,
    pixels: IntArray,
    scratch: IntArray,
    checkCancelled: () -> Unit
): Bitmap {
    val mutableBitmap = if (source.config == Bitmap.Config.ARGB_8888 && source.isMutable) {
        source
    } else {
        source.copy(Bitmap.Config.ARGB_8888, true)
    }
    mutableBitmap.getPixels(
        pixels,
        0,
        mutableBitmap.width,
        0,
        0,
        mutableBitmap.width,
        mutableBitmap.height
    )
    blurPixelsInPlace(
        pixels = pixels,
        scratch = scratch,
        width = mutableBitmap.width,
        height = mutableBitmap.height,
        radius = radius,
        checkCancelled = checkCancelled
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

private fun blurPixelsInPlace(
    pixels: IntArray,
    scratch: IntArray,
    width: Int,
    height: Int,
    radius: Int,
    checkCancelled: () -> Unit
) {
    if (radius <= 0 || width <= 1 || height <= 1) return

    repeat(FALLBACK_BLUR_PASS_COUNT) {
        boxBlurHorizontal(
            input = pixels,
            output = scratch,
            width = width,
            height = height,
            radius = radius,
            checkCancelled = checkCancelled
        )
        boxBlurVertical(
            input = scratch,
            output = pixels,
            width = width,
            height = height,
            radius = radius,
            checkCancelled = checkCancelled
        )
    }
}

/**
 * Applies the horizontal half of the separable box blur.
 *
 * For each row, this keeps a rolling color sum over a `(radius * 2 + 1)` window so the
 * output can be updated in O(1) per pixel instead of re-summing the whole neighborhood.
 */
private fun boxBlurHorizontal(
    input: IntArray,
    output: IntArray,
    width: Int,
    height: Int,
    radius: Int,
    checkCancelled: () -> Unit
) {
    val windowSize = radius * 2 + 1

    for (y in 0 until height) {
        if (y % 8 == 0) {
            checkCancelled()
        }
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
            output[y * width + x] = packColor(
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

/**
 * Applies the vertical half of the separable box blur.
 *
 * This mirrors [boxBlurHorizontal] over columns, consuming the horizontally blurred buffer
 * and writing the final pass back into the destination array.
 */
private fun boxBlurVertical(
    input: IntArray,
    output: IntArray,
    width: Int,
    height: Int,
    radius: Int,
    checkCancelled: () -> Unit
) {
    val windowSize = radius * 2 + 1

    for (x in 0 until width) {
        if (x % 8 == 0) {
            checkCancelled()
        }
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
            output[y * width + x] = packColor(
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

private fun packColor(alpha: Int, red: Int, green: Int, blue: Int): Int {
    return (alpha.coerceIn(0, 255) shl 24) or
        (red.coerceIn(0, 255) shl 16) or
        (green.coerceIn(0, 255) shl 8) or
        blue.coerceIn(0, 255)
}
