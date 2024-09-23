package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.unit.IntRect

data class ImageProperties(
    val drawAreaRect: Rect,
    val bitmapRect: Rect,
    val scaleFactor: ScaleFactor,
) {
    companion object {

        @Stable
        val Zero: ImageProperties = ImageProperties(
            drawAreaRect = Rect.Zero,
            bitmapRect = Rect.Zero,
            scaleFactor = ScaleFactor(1f, 1f)
        )
    }
}

/**
 * Get position on Composable from position on a Bitmap.
 *
 * @param offsetBitmap is the position on a Bitmap
 * @param drawAreaRect bounds of Composable draw area. This is the bounds that Bitmap is drawn
 * @param bitmapRect bounds of Bitmap that is drawn into Composable. This is which section
 * of Bitmap is drawn
 * @param scaleFactor scale of draw area to bitmap
 *
 * @return position on Composable
 */
internal fun scaleFromBitmapToScreenPosition(
    offsetBitmap: Offset,
    drawAreaRect: Rect,
    bitmapRect: Rect,
    scaleFactor: ScaleFactor,
): Offset {
    val coordinateX = offsetBitmap.x
    val coordinateY = offsetBitmap.y


    val ratioX = scaleFactor.scaleX
    val ratioY = scaleFactor.scaleY

    val xOnScreen = drawAreaRect.left + (coordinateX - bitmapRect.left) * ratioX
    val yOnScreen = drawAreaRect.top + (coordinateY - bitmapRect.top) * ratioY
    return Offset(xOnScreen, yOnScreen)
}

/**
 * Get position on Bitmap from position on a Composable.
 *
 * @param offsetScreen is the position on Composable.
 * @param drawAreaRect bounds of Composable draw area. This is the bounds that Bitmap is drawn.
 * @param bitmapRect bounds of Bitmap that is drawn into Composable. This is which section
 * of Bitmap is drawn.
 * @param scaleFactor scale of draw area to bitmap
 *
 * @return position on Bitmap
 */
internal fun scaleFromScreenToBitmapPosition(
    offsetScreen: Offset,
    drawAreaRect: Rect,
    bitmapRect: Rect,
    scaleFactor: ScaleFactor,
): Offset {
    val coordinateX = offsetScreen.x
    val coordinateY = offsetScreen.y

    val ratioX = scaleFactor.scaleX
    val ratioY = scaleFactor.scaleY

    val xOnBitmap = bitmapRect.left + (coordinateX - drawAreaRect.left) / ratioX
    val yOnBitmap = bitmapRect.top + (coordinateY - drawAreaRect.top) / ratioY
    return Offset(xOnBitmap, yOnBitmap)
}

internal fun calculateImageDrawProperties(
    srcSize: Size,
    dstSize: Size,
    contentScale: ContentScale,
    alignment: Alignment,
): ImageProperties {
    val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

    // Bitmap scaled size that might be drawn,  this size can be bigger or smaller than
    // draw area. If Bitmap is bigger than container it's cropped only to show
    // which will be on screen
    //
    val scaledSrcSize = Size(
        srcSize.width * scaleFactor.scaleX,
        srcSize.height * scaleFactor.scaleY
    )

    val biasAlignment: BiasAlignment = alignment as BiasAlignment

    // - Left, 0 Center, 1 Right
    val horizontalBias: Float = biasAlignment.horizontalBias
    // -1 Top, 0 Center, 1 Bottom
    val verticalBias: Float = biasAlignment.verticalBias

    // DrawAreaRect returns the area that bitmap is drawn in Container
    // This rectangle is useful for getting are after gaps on any side based on
    // alignment and ContentScale
    val drawAreaRect = getDrawAreaRect(
        dstSize,
        scaledSrcSize,
        horizontalBias,
        verticalBias
    )

    // BitmapRect returns that Rectangle to show which section of Bitmap is visible on screen
    val bitmapRect = getScaledBitmapRect(
        horizontalBias = horizontalBias,
        verticalBias = verticalBias,
        containerWidth = dstSize.width.toInt(),
        containerHeight = dstSize.height.toInt(),
        scaledImageWidth = scaledSrcSize.width,
        scaledImageHeight = scaledSrcSize.height,
        bitmapWidth = srcSize.width.toInt(),
        bitmapHeight = srcSize.height.toInt()
    )

    return ImageProperties(
        drawAreaRect = drawAreaRect,
        bitmapRect = bitmapRect,
        scaleFactor = ScaleFactor(
            scaleX = drawAreaRect.width / bitmapRect.width,
            scaleY = drawAreaRect.height / bitmapRect.height

        )
    )
}

/**
 * Get area of the Bitmap is drawn on Composable on screen.
 * @param dstSize size of the Composable
 * @param scaledSrcSize size of Bitmap after scaled with [ContentScale.computeScaleFactor]
 * @param horizontalBias horizontal bias of [Alignment] of Image or any Composable that draws bitmap
 * @param verticalBias vertical bias of [Alignment] of Image or any Composable that draws bitmap
 */
internal fun getDrawAreaRect(
    dstSize: Size,
    scaledSrcSize: Size,
    horizontalBias: Float,
    verticalBias: Float,
): Rect {
    val horizontalGap = ((dstSize.width - scaledSrcSize.width) / 2).coerceAtLeast(0f)
    val verticalGap = ((dstSize.height - scaledSrcSize.height) / 2).coerceAtLeast(0f)

    val left = when (horizontalBias) {
        -1f -> 0f
        0f -> horizontalGap
        else -> horizontalGap * 2
    }

    val top = when (verticalBias) {
        -1f -> 0f
        0f -> verticalGap
        else -> verticalGap * 2
    }

    val right = (left + scaledSrcSize.width).coerceAtMost(dstSize.width)
    val bottom = (top + scaledSrcSize.height).coerceAtMost(dstSize.height)

    val drawAreaRect = Rect(
        left, top, right, bottom
    )
    return drawAreaRect
}

/**
 * Get Rectangle of [ImageBitmap] with [bitmapWidth] and [bitmapHeight] that is drawn inside
 * Canvas with [scaledImageWidth] and [scaledImageHeight]. [containerWidth] and [containerHeight]
 * belong to Composable that will draw the Bitmap.
 *  @param containerWidth width of the parent container
 *  @param containerHeight height of the parent container
 *  @param scaledImageWidth width of the [Canvas] that draws [ImageBitmap]
 *  @param scaledImageHeight height of the [Canvas] that draws [ImageBitmap]
 *  @param bitmapWidth intrinsic width of the [ImageBitmap]
 *  @param bitmapHeight intrinsic height of the [ImageBitmap]
 *  @return [IntRect] that covers [ImageBitmap] bounds. When image [ContentScale] is crop
 *  this rectangle might return smaller rectangle than actual [ImageBitmap] and left or top
 *  of the rectangle might be bigger than zero.
 */
internal fun getScaledBitmapRect(
    horizontalBias: Float,
    verticalBias: Float,
    containerWidth: Int,
    containerHeight: Int,
    scaledImageWidth: Float,
    scaledImageHeight: Float,
    bitmapWidth: Int,
    bitmapHeight: Int,
): Rect {
    // Get scale of box to width of the image
    // We need a rect that contains Bitmap bounds to pass if any child requires it
    // For a image with 100x100 px with 300x400 px container and image with crop 400x400px
    // So we need to pass top left as 0,50 and size
    val scaledBitmapX = containerWidth / scaledImageWidth
    val scaledBitmapY = containerHeight / scaledImageHeight

    val scaledBitmapWidth = (bitmapWidth * scaledBitmapX).coerceAtMost(bitmapWidth.toFloat())
    val scaledBitmapHeight = (bitmapHeight * scaledBitmapY).coerceAtMost(bitmapHeight.toFloat())

    val horizontalGap = (bitmapWidth - scaledBitmapWidth) / 2
    val verticalGap = (bitmapHeight - scaledBitmapHeight) / 2

    val left = when (horizontalBias) {
        -1f -> 0f
        0f -> horizontalGap
        else -> horizontalGap * 2
    }

    val top = when (verticalBias) {
        -1f -> 0f
        0f -> verticalGap
        else -> verticalGap * 2
    }

    val topLeft = Offset(x = left, y = top)

    val size = Size(
        width = (bitmapWidth * scaledBitmapX).coerceAtMost(bitmapWidth.toFloat()),
        height = (bitmapHeight * scaledBitmapY).coerceAtMost(bitmapHeight.toFloat())
    )

    return Rect(offset = topLeft, size = size)
}
