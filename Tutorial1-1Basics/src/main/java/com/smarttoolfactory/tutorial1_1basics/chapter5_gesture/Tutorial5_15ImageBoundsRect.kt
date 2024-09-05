package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import java.util.UUID


private fun calculateRect(srcSize: Size, dstSize: Size, contentScale: ContentScale): Rect {
    val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

    val scaledSrcSize = Size(
        srcSize.width * scaleFactor.scaleX,
        srcSize.height * scaleFactor.scaleY
    )
    val left = ((dstSize.width - scaledSrcSize.width) / 2).coerceAtLeast(0f)
    val top = ((dstSize.height - scaledSrcSize.height) / 2).coerceAtLeast(0f)

    val right = (left + scaledSrcSize.width).coerceAtMost(dstSize.width)
    val bottom = (top + scaledSrcSize.height).coerceAtMost(dstSize.height)

    return Rect(
        left, top, right, bottom
    )
}

data class ImgAnnotation(
    val uid: String,
    val coordinateX: Float,
    val coordinateY: Float,
    val note: String = ""
)

@Preview
@Composable
fun ImageWithMarkersSample() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(R.drawable.landscape3)

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

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {

        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(1f),
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
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
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
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(5 / 3f),
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
    }
}

@Composable
private fun ImageWithMarkers(
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    imgAnnotationList: SnapshotStateList<ImgAnnotation>,
    imageBitmap: ImageBitmap,
    onClick: (ImgAnnotation) -> Unit
) {

    var rect by remember {
        mutableStateOf(Rect.Zero)
    }

    var scaleFactor by remember {
        mutableStateOf(ScaleFactor(1f, 1f))
    }

    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {

        Image(
            modifier = modifier
                .drawWithContent {
                    drawContent()

                    // This is for displaying area that bitmap is drawn in Image Composable
                    drawRect(
                        color = Color.Green,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(
                            4.dp.toPx(),
                        )
                    )
                }
                .pointerInput(contentScale) {
                    detectTapGestures { offset: Offset ->
                        val isTouchInImage = rect.contains(offset)
                        if (isTouchInImage) {
                            // Calculate touch position scaled into Bitmap
                            val xOnImage = (offset.x - rect.left) / scaleFactor.scaleX
                            val yOnImage = (offset.y - rect.top) / scaleFactor.scaleY
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

                    rect = calculateRect(srcSize, dstSize, contentScale)

                    val rectWidth = rect.width
                    val rectHeight = rect.height
                    val ratioX = rectWidth / srcSize.width
                    val ratioY = rectHeight / srcSize.height
                    scaleFactor = ScaleFactor(ratioX, ratioY)
                },
            bitmap = imageBitmap,
            contentScale = contentScale,
            contentDescription = null
        )

        ShapesOnImage(
            list = imgAnnotationList,
            rect = rect,
            scaleFactor = scaleFactor,
            onClick = onClick
        )
    }
}

@Composable
fun ShapesOnImage(
    list: List<ImgAnnotation>,
    rect: Rect,
    scaleFactor: ScaleFactor,
    onClick: (ImgAnnotation) -> Unit
) {
    if (rect != Rect.Zero) {
        list.forEachIndexed { index, imgAnnotation ->

            val coordinateX = imgAnnotation.coordinateX
            val coordinateY = imgAnnotation.coordinateY

            val xOnScreen = rect.left + coordinateX * scaleFactor.scaleX
            val yOnScreen = rect.top + coordinateY * scaleFactor.scaleY

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
                    .size(30.dp)
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
