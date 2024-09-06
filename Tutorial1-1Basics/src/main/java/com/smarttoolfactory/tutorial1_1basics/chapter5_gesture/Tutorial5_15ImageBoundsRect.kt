package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import java.util.UUID


private fun getDrawAreaRect(
    dstSize: Size,
    scaledSrcSize: Size,
    horizontalBias: Float,
    verticalBias: Float
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
 * Canvas with [scaledImageWidth] and [scaledImageHeight]. [containerWidth] and [containerHeight] belong
 * to [BoxWithConstraints] that contains Canvas.
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
    bitmapHeight: Int
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

@Preview
@Composable
fun ImageWithMarkersSample() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(R.drawable.landscape1)

    val imgAnnotationList = imgAnnotations()

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp)
    ) {

        Text("ContentScale: ContentScale.Fit, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            contentScale = ContentScale.Fit,
            imgAnnotationList = imgAnnotationList,
            imageBitmap = imageBitmap
        ) {
            Toast.makeText(
                context,
                "Clicked ${it.uid.substring(0, 4)} at " +
                        "x: ${it.coordinateX}, y: ${it.coordinateY}",
                Toast.LENGTH_SHORT
            ).show()
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.FillBounds, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 2f),
            contentScale = ContentScale.FillBounds,
            imgAnnotationList = imgAnnotationList,
            imageBitmap = imageBitmap
        ) {
            Toast.makeText(
                context,
                "Clicked ${it.uid.substring(0, 4)} " +
                        "at x: ${it.coordinateX}, y: ${it.coordinateY}",
                Toast.LENGTH_SHORT
            ).show()
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Fit, alignment: BottomEnd")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(5 / 3f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            imgAnnotationList = imgAnnotationList,
            imageBitmap = imageBitmap
        ) {
            Toast.makeText(
                context,
                "Clicked ${it.uid.substring(0, 4)} at " +
                        "x: ${it.coordinateX}, y: ${it.coordinateY}",
                Toast.LENGTH_SHORT
            ).show()
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            imgAnnotationList = imgAnnotationList,
            imageBitmap = imageBitmap
        ) {
            Toast.makeText(
                context,
                "Clicked ${it.uid.substring(0, 4)} " +
                        "at x: ${it.coordinateX}, y: ${it.coordinateY}",
                Toast.LENGTH_SHORT
            ).show()
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopStart")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart,
            imgAnnotationList = imgAnnotationList,
            imageBitmap = imageBitmap
        ) {
            Toast.makeText(
                context,
                "Clicked ${it.uid.substring(0, 4)} " +
                        "at x: ${it.coordinateX}, y: ${it.coordinateY}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
private fun imgAnnotations(): SnapshotStateList<ImgAnnotation> {
    val imgAnnotationList = remember {
        mutableStateListOf<ImgAnnotation>()
            .apply {
                add(
                    ImgAnnotation(
                        uid = "45224",
                        coordinateX = 10f,
                        coordinateY = 10f,
                        note = "Sample text 1"
                    )
                )
                add(
                    ImgAnnotation(
                        uid = "6454",
                        coordinateX = 50f,
                        coordinateY = 50f,
                        note = "Sample text 2"
                    )
                )
                add(
                    ImgAnnotation(
                        uid = "211111",
                        coordinateX = 200f,
                        coordinateY = 90f,
                        note = "Sample text 3"
                    )
                )
                add(
                    ImgAnnotation(
                        uid = "21555",
                        coordinateX = 32f,
                        coordinateY = 93f,
                        note = "Sample text 4"
                    )
                )
            }
    }
    return imgAnnotationList
}

@Composable
private fun ImageWithMarkers(
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    alignment: Alignment = Alignment.Center,
    imgAnnotationList: SnapshotStateList<ImgAnnotation>,
    imageBitmap: ImageBitmap,
    onClick: (ImgAnnotation) -> Unit
) {

    var imageProperties by remember {
        mutableStateOf(ImageProperties.Zero)
    }

    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {

        Image(
            modifier = modifier
                .drawWithContent {
                    drawContent()
                    val drawAreaRect = imageProperties.drawAreaRect

                    // This is for displaying area that bitmap is drawn in Image Composable
                    drawRect(
                        color = Color.Green,
                        topLeft = drawAreaRect.topLeft,
                        size = drawAreaRect.size,
                        style = Stroke(
                            4.dp.toPx(),
                        )
                    )
                }
                .pointerInput(contentScale) {
                    detectTapGestures { offset: Offset ->

                        val drawAreaRect = imageProperties.drawAreaRect
                        val isTouchInImage = drawAreaRect.contains(offset)

                        if (isTouchInImage) {
                            val bitmapRect = imageProperties.bitmapRect

                            // Calculate touch position scaled into Bitmap
                            // using scale between area on screen / bitmap on screen
                            // Bitmap on screen might change based on crop or other
                            // ContentScales that clip image
                            val ratioX = drawAreaRect.width / bitmapRect.width
                            val ratioY = drawAreaRect.height / bitmapRect.height

                            val xOnImage =
                                bitmapRect.left + (offset.x - drawAreaRect.left) / ratioX
                            val yOnImage =
                                bitmapRect.top + (offset.y - drawAreaRect.top) / ratioY

                            imgAnnotationList.add(
                                ImgAnnotation(UUID.randomUUID().toString(), xOnImage, yOnImage)
                            )
                        }
                    }
                }
                .onSizeChanged {
                    val imageSize = it
                    val dstSize: Size = imageSize.toSize()
                    val srcSize =
                        Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

                    imageProperties = calculateImageDrawProperties(
                        srcSize = srcSize,
                        dstSize = dstSize,
                        contentScale = contentScale,
                        alignment = alignment
                    )
                },
            bitmap = imageBitmap,
            contentScale = contentScale,
            alignment = alignment,
            contentDescription = null
        )

        ShapesOnImage(
            list = imgAnnotationList,
            imageProperties = imageProperties,
            onClick = onClick
        )
    }
}

@Composable
fun ShapesOnImage(
    list: List<ImgAnnotation>,
    imageProperties: ImageProperties,
    onClick: (ImgAnnotation) -> Unit
) {
    if (imageProperties != ImageProperties.Zero) {
        list.forEachIndexed { index, imgAnnotation ->

            val coordinateX = imgAnnotation.coordinateX
            val coordinateY = imgAnnotation.coordinateY

            val drawAreaRect = imageProperties.drawAreaRect
            val bitmapRect = imageProperties.bitmapRect

            val ratioX = drawAreaRect.width / bitmapRect.width
            val ratioY = drawAreaRect.height / bitmapRect.height

            val xOnScreen = drawAreaRect.left + (coordinateX - bitmapRect.left) * ratioX
            val yOnScreen = drawAreaRect.top + (coordinateY - bitmapRect.top) * ratioY

            Box(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        val width = placeable.width
                        val height = placeable.height

                        val xPos = (xOnScreen - width / 2).toInt()
                        val yPos = (yOnScreen - height / 2).toInt()

                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(xPos, yPos)
                        }
                    }
                    .size(20.dp)
                    .background(Color.Red, CircleShape)
                    .clickable {
                        onClick(imgAnnotation)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = Color.White)
            }

        }
    }
}

data class ImgAnnotation(
    val uid: String,
    val coordinateX: Float,
    val coordinateY: Float,
    val note: String = ""
)

data class ImageProperties(
    val drawAreaRect: Rect,
    val bitmapRect: Rect
) {
    companion object {

        @Stable
        val Zero: ImageProperties = ImageProperties(
            drawAreaRect = Rect.Zero,
            bitmapRect = Rect.Zero
        )
    }
}

private fun calculateImageDrawProperties(
    srcSize: Size,
    dstSize: Size,
    contentScale: ContentScale,
    alignment: Alignment
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
        bitmapRect = bitmapRect
    )
}