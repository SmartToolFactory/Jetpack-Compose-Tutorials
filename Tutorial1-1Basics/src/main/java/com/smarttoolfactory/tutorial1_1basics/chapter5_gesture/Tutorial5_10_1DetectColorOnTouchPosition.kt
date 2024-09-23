package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.detectMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial5_10_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        StyleableTutorialText(text = "Detect color at touch position on image.", bullets = false)
        TouchOnImageExample()
    }
}

@Composable
internal fun TouchOnImageExample() {

    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape6
    )

    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height


    // These are for debugging
    var text by remember { mutableStateOf("") }
    var colorAtTouchPosition by remember { mutableStateOf(Color.Unspecified) }

    var imageProperties by remember {
        mutableStateOf(ImageProperties.Zero)
    }

    var touchPosition by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val contentScale = ContentScale.Fit
    val alignment = Alignment.Center

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth().aspectRatio(1f)
                .background(Color.LightGray)
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

                    if (touchPosition != Offset.Unspecified) {

                        drawCircle(
                            color = Color.Red,
                            center = touchPosition,
                            radius = 15f,
                            style = Stroke(2.dp.toPx())
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectMotionEvents(
                        onDown = { pointerInputChange: PointerInputChange ->
                            val offset = pointerInputChange.position
                            touchPosition = offset

                            val result = detectColorOnTouchPosition(
                                imageProperties,
                                offset,
                                imageBitmap,
                                bitmapWidth,
                                bitmapHeight,
                            )

                            result?.let {
                                text = it.first
                                colorAtTouchPosition = it.second
                            }
                        },
                        onMove = { pointerInputChange: PointerInputChange ->
                            val offset = pointerInputChange.position
                            touchPosition = offset

                            val result = detectColorOnTouchPosition(
                                imageProperties,
                                offset,
                                imageBitmap,
                                bitmapWidth,
                                bitmapHeight,
                            )

                            pointerInputChange.consume()

                            result?.let {
                                text = it.first
                                colorAtTouchPosition = it.second
                            }
                        }
                    )
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
            contentDescription = null,
            contentScale = contentScale,
            alignment = alignment
        )

        Row(
            modifier = Modifier.padding(top = 16.dp, start = 8.dp)
        ) {

            if (colorAtTouchPosition != Color.Unspecified) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .background(colorAtTouchPosition)
                        .size(100.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp)
        }
    }
}

private fun detectColorOnTouchPosition(
    imageProperties: ImageProperties,
    offset: Offset,
    imageBitmap: ImageBitmap,
    bitmapWidth: Int,
    bitmapHeight: Int,
): Pair<String, Color>? {
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


        try {
            val pixel: Int =
                imageBitmap
                    .asAndroidBitmap()
                    .getPixel(xOnImage.toInt(), yOnImage.toInt())


            val red = android.graphics.Color.red(pixel)
            val green = android.graphics.Color.green(pixel)
            val blue = android.graphics.Color.blue(pixel)

            val text = "Bitmap width: ${bitmapWidth}, height: $bitmapHeight\n" +
                    "scaledX: ${yOnImage.toInt()}, scaledY: ${yOnImage.toInt()}\n" +
                    "red: $red, green: $green, blue: $blue\n"

            return Pair(text, Color(red, green, blue))
        } catch (e: Exception) {
            println("Exception e: ${e.message}")
        }
    }

    return null
}
