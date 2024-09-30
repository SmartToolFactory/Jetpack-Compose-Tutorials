package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.MotionEvent
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Preview
@Composable
fun Tutorial6_11Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        StyleableTutorialText(
            text = "In this example using Canvas(imageBitmap) and reading pixels we compare" +
                    "which percentage of original image is erased.",
            bullets = false
        )

        val imageBitmap = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape5
        ).asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()

        val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()


        EraseBitmapSample(
            imageBitmap = imageBitmap,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
        )
    }
}

@Composable
fun EraseBitmapSample(imageBitmap: ImageBitmap, modifier: Modifier) {


    var matchPercent by remember {
        mutableStateOf(100f)
    }

    BoxWithConstraints(modifier) {

        // Path used for erasing. In this example erasing is faked by drawing with canvas color
        // above draw path.
        val erasePath = remember { Path() }

        var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
        // This is our motion event we get from touch motion
        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
        // This is previous motion event before next touch is saved into this current position
        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight


        val drawImageBitmap = remember {
            Bitmap.createScaledBitmap(imageBitmap.asAndroidBitmap(), imageWidth, imageHeight, false)
                .asImageBitmap()
        }

        // Pixels of scaled bitmap, we scale it to composable size because we will erase
        // from Composable on screen
        val originalPixels: IntArray = remember {
            val buffer = IntArray(imageWidth * imageHeight)
            drawImageBitmap
                .readPixels(
                    buffer = buffer,
                    startX = 0,
                    startY = 0,
                    width = imageWidth,
                    height = imageHeight
                )

            buffer
        }

        val erasedBitmap: ImageBitmap = remember {
            Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888).asImageBitmap()
        }

        val canvas: Canvas = remember {
            Canvas(erasedBitmap)
        }

        val paint = remember {
            Paint()
        }

        val erasePaint = remember {
            Paint().apply {
                blendMode = BlendMode.Clear
                this.style = PaintingStyle.Stroke
                strokeWidth = 50f
            }
        }

        LaunchedEffect(key1 = currentPosition) {
            snapshotFlow {
                currentPosition
            }
                .map {
                    compareBitmaps(
                        originalPixels,
                        erasedBitmap,
                        imageWidth,
                        imageHeight
                    )
                }
                .onEach {
                    matchPercent = it
                }
                .launchIn(this)
        }

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()

            when (motionEvent) {

                MotionEvent.Down -> {
                    erasePath.moveTo(currentPosition.x, currentPosition.y)
                    previousPosition = currentPosition

                }
                MotionEvent.Move -> {

                    erasePath.quadraticTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2
                    )
                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    erasePath.lineTo(currentPosition.x, currentPosition.y)
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }
                else -> Unit
            }

            with(canvas.nativeCanvas) {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                drawImageRect(
                    image = drawImageBitmap,
                    dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                    paint = paint
                )

                drawPath(
                    path = erasePath,
                    paint = erasePaint
                )
            }
        }

        val canvasModifier = Modifier.pointerMotionEvents(
            Unit,
            onDown = { pointerInputChange ->
                motionEvent = MotionEvent.Down
                currentPosition = pointerInputChange.position
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange ->
                motionEvent = MotionEvent.Move
                currentPosition = pointerInputChange.position
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 20
        )

        Image(
            modifier = canvasModifier
                .clipToBounds()
                .drawBehind {
                    val width = this.size.width
                    val height = this.size.height

                    val checkerWidth = 10.dp.toPx()
                    val checkerHeight = 10.dp.toPx()

                    val horizontalSteps = (width / checkerWidth).toInt()
                    val verticalSteps = (height / checkerHeight).toInt()

                    for (y in 0..verticalSteps) {
                        for (x in 0..horizontalSteps) {
                            val isGrayTile = ((x + y) % 2 == 1)
                            drawRect(
                                color = if (isGrayTile) androidx.compose.ui.graphics.Color.LightGray else androidx.compose.ui.graphics.Color.White,
                                topLeft = Offset(x * checkerWidth, y * checkerHeight),
                                size = Size(checkerWidth, checkerHeight)
                            )
                        }
                    }
                }
                .matchParentSize()
                .border(2.dp, androidx.compose.ui.graphics.Color.Green),
            bitmap = erasedBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

    Text("Original Bitmap")

    Image(
        modifier = modifier,
        bitmap = imageBitmap,
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )

    Text(
        "Bitmap match ${matchPercent.toInt()}%",
        color = androidx.compose.ui.graphics.Color.Red,
        fontSize = 22.sp
    )

}

@Synchronized
private fun compareBitmaps(
    originalPixels: IntArray,
    erasedBitmap: ImageBitmap,
    imageWidth: Int,
    imageHeight: Int
): Float {

    var match = 0f

    val size = imageWidth * imageHeight
    val erasedBitmapPixels = IntArray(size)

    erasedBitmap.readPixels(
        buffer = erasedBitmapPixels,
        startX = 0,
        startY = 0,
        width = imageWidth,
        height = imageHeight
    )

    erasedBitmapPixels.forEachIndexed { index, pixel: Int ->
        if (originalPixels[index] == pixel) {
            match++
        }
    }

    return 100f * match / size
}