package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R

@Preview
@Composable
fun ImageBoundsDrawSample() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {

        val pointsOnBitmap = remember {
            mutableStateListOf<Offset>()
        }

        val painter = painterResource(R.drawable.landscape10)

        Text("ContentScale: ContentScale.Fit, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            contentScale = ContentScale.Fit,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.FillBounds, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 2f),
            contentScale = ContentScale.FillBounds,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Fit, alignment: BottomEnd")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(5 / 3f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopStart")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }
    }
}


@Composable
private fun ImageWithBounds(
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    alignment: Alignment = Alignment.Center,
    painter: Painter,
    pointsOnBitmap: List<Offset>,
    onClick: (positionOnBitmap: Offset) -> Unit,
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
                    val bitmapRect = imageProperties.bitmapRect
                    val scaleFactor = imageProperties.scaleFactor

                    // This is for displaying area that bitmap is drawn in Image Composable
                    drawRect(
                        color = Color.Green,
                        topLeft = drawAreaRect.topLeft,
                        size = drawAreaRect.size,
                        style = Stroke(
                            4.dp.toPx(),
                        )
                    )

                    val size = pointsOnBitmap.size
                    pointsOnBitmap.forEachIndexed { index: Int, offset: Offset ->

                        val scaledCurrentOffset = scaleFromBitmapToScreenPosition(
                            offsetBitmap = offset,
                            drawAreaRect = drawAreaRect,
                            bitmapRect = bitmapRect,
                            scaleFactor = scaleFactor
                        )
                        if (size > 1 && index > 0) {

                            val scaledPreviousOffset = scaleFromBitmapToScreenPosition(
                                offsetBitmap = pointsOnBitmap[index - 1],
                                drawAreaRect = drawAreaRect,
                                bitmapRect = bitmapRect,
                                scaleFactor = scaleFactor
                            )

                            drawLine(
                                color = Color.Blue,
                                start = scaledPreviousOffset,
                                end = scaledCurrentOffset,
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        drawCircle(
                            color = Color.Red,
                            center = scaledCurrentOffset,
                            radius = 6.dp.toPx()
                        )
                    }
                }
                .pointerInput(contentScale) {
                    detectTapGestures { offset: Offset ->

                        val bitmapRect = imageProperties.bitmapRect
                        val drawAreaRect = imageProperties.drawAreaRect
                        val isTouchInImage = drawAreaRect.contains(offset)

                        if (isTouchInImage) {

                            val offsetOnBitmap = scaleFromScreenToBitmapPosition(
                                offsetScreen = offset,
                                bitmapRect = bitmapRect,
                                drawAreaRect = drawAreaRect,
                                scaleFactor = imageProperties.scaleFactor
                            )

                            onClick(offsetOnBitmap)
                        }
                    }
                }
                .onSizeChanged {
                    val imageSize = it
                    val dstSize: Size = imageSize.toSize()

                    val srcSize = painter.intrinsicSize

                    imageProperties = calculateImageDrawProperties(
                        srcSize = srcSize,
                        dstSize = dstSize,
                        contentScale = contentScale,
                        alignment = alignment
                    )
                },
            painter = painter,
            contentScale = contentScale,
            alignment = alignment,
            contentDescription = null
        )

    }
}