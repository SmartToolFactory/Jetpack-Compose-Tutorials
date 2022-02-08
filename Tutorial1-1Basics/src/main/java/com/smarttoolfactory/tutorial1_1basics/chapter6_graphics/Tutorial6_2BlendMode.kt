package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CustomDialogWithResultExample
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

/*
    For more info about Blend or Porter-Duff modes refer official links below
    https://developer.android.com/reference/android/graphics/PorterDuff.Mode
    https://medium.com/mobile-app-development-publication/practical-image-porterduff-mode-usage-in-android-3b4b5d2e8f5f

    PorteDuffMode is a software image blending method that is available in many software platforms,
    which includes Android. It is based on a mathematic model which runs on the pixels
    of two images to produce a neat output.

    We simply draw destination shape/image first, then draw source shape/image and apply
    blend mode to source.

 */
@Composable
fun Tutorial6_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BlendModeExample()
    }

}

@Composable
private fun BlendModeExample() {

    TutorialText2(text = "Draw Shapes with Blend Mode")
    DrawShapeBlendMode()
    TutorialText2(text = "Draw Images with Blend Mode")
    DrawImageBlendMode()
    TutorialText2(text = "Clip Image with Blend Mode Via Path")
    ClipImageWithBlendModeViaPath()
    TutorialText2(text = "Clip Image with Blend Mode Via Image")
    ClipImageWithBlendModeViaAnotherImage()
}


/**
 * In this example destination path is drawn first, then source path with blend mode.
 * Since we don't have full screen paths blend modes are applied to intersection of these
 * shapes.
 */
@Composable
private fun DrawShapeBlendMode() {
    var selectedIndex by remember { mutableStateOf(3) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcOver) }

    var dstColor by remember { mutableStateOf(Color(0xffEC407A)) }
    var srcColor by remember { mutableStateOf(Color(0xff29B6F6)) }

    var showDstColorDialog by remember { mutableStateOf(false) }
    var showSrcColorDialog by remember { mutableStateOf(false) }


    Canvas(modifier = canvasModifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2 - 100

        val horizontalOffset = 70f
        val verticalOffset = 50f

        val cx = canvasWidth / 2 - horizontalOffset
        val cy = canvasHeight / 2 + verticalOffset
        val srcPath = createPath(cx, cy, 5, radius)

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawCircle(
                color = dstColor,
                radius = radius,
                center = Offset(
                    canvasWidth / 2 + horizontalOffset,
                    canvasHeight / 2 - verticalOffset
                ),
            )

            // Source
            drawPath(path = srcPath, color = srcColor, blendMode = blendMode)

            restoreToCount(checkPoint)
        }
    }

    OutlinedButton(onClick = { showDstColorDialog = true }) {
        Text(
            text = "Dst Color",
            fontSize = 16.sp,
            color = dstColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }

    OutlinedButton(onClick = {
        showSrcColorDialog = true
    }) {
        Text(
            text = "Src Color",
            fontSize = 16.sp,
            color = srcColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }

    Text(
        text = "Src BlendMode: $blendMode",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )

    BlendModeSelection(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState()),
        selectedIndex = selectedIndex,
        onBlendModeSelected = { index, mode ->
            blendMode = mode
            selectedIndex = index
        }
    )

    if (showSrcColorDialog) {
        CustomDialogWithResultExample(
            onDismiss = {
                showSrcColorDialog = false
            },
            onNegativeClick = {
                showSrcColorDialog = false
            },
            onPositiveClick = {
                showSrcColorDialog = false
                srcColor = it
            }
        )
    }

    if (showDstColorDialog) {
        CustomDialogWithResultExample(
            onDismiss = {
                showDstColorDialog = false
            },
            onNegativeClick = {
                showDstColorDialog = false
            },
            onPositiveClick = {
                showDstColorDialog = false
                dstColor = it
            }
        )
    }
}

/**
 * Images are overlapped on other. src is drawn on top of dst image, both images have transparent
 * pixels. Setting blend mode and color of dst and/or src will change clipping or coloring
 * of canvas.
 */
@Composable
private fun DrawImageBlendMode() {

    var selectedIndex by remember { mutableStateOf(3) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcOver) }

    val dstImage = ImageBitmap.imageResource(id = R.drawable.composite_dst)
    val srcImage = ImageBitmap.imageResource(id = R.drawable.composite_src)

    Canvas(modifier = canvasModifier) {

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawImage(
                image = dstImage,
                srcSize = IntSize(canvasWidth / 2, canvasHeight / 2),
                dstSize = IntSize(canvasWidth, canvasHeight),
            )

            // Source
            drawImage(
                image = srcImage,
                srcSize = IntSize(canvasWidth / 2, canvasHeight / 2),
                dstSize = IntSize(canvasWidth, canvasHeight),
                blendMode = blendMode
            )
            restoreToCount(checkPoint)
        }
    }

    Text(
        text = "Src BlendMode: $blendMode",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )

    BlendModeSelection(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState()),
        selectedIndex = selectedIndex,
        onBlendModeSelected = { index, mode ->
            blendMode = mode
            selectedIndex = index
        }
    )
}

/**
 * src image is clipped using shape we draw behind as dst.
 */
@Composable
private fun ClipImageWithBlendModeViaPath() {
    var sides by remember { mutableStateOf(6f) }
    val srcBitmap = ImageBitmap.imageResource(id = R.drawable.landscape1)

    var selectedIndex by remember { mutableStateOf(5) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcIn) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2
        val path = createPath(cx.toFloat(), cy.toFloat(), sides.roundToInt(), radius)


        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawPath(
                color = Color.Blue,
                path = path
            )
            // Source
            drawImage(
                blendMode = blendMode,
                image = srcBitmap,
                srcSize = IntSize( srcBitmap.width, srcBitmap.height),
                dstSize = IntSize(canvasWidth, canvasHeight)
            )

            restoreToCount(checkPoint)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )

        Text(
            text = "Src BlendMode: $blendMode",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        BlendModeSelection(
            modifier = Modifier
                .height(200.dp)
                .verticalScroll(rememberScrollState()),
            selectedIndex = selectedIndex,
            onBlendModeSelected = { index, mode ->
                blendMode = mode
                selectedIndex = index
            }
        )
    }
}

@Composable
private fun ClipImageWithBlendModeViaAnotherImage() {
    val srcBitmap = ImageBitmap.imageResource(id = R.drawable.landscape9)
    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.dots_transparent)

    var selectedIndex by remember { mutableStateOf(5) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcIn) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()


        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawImage(
                image = dstBitmap,
                srcSize = IntSize( dstBitmap.width, dstBitmap.height),
                dstSize = IntSize(canvasWidth, canvasHeight)
            )

            // Source
            drawImage(
                blendMode = blendMode,
                image = srcBitmap,
                srcSize = IntSize( srcBitmap.width, srcBitmap.height),
                dstSize = IntSize(canvasWidth, canvasHeight)
            )

            restoreToCount(checkPoint)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Src BlendMode: $blendMode",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        BlendModeSelection(
            modifier = Modifier
                .height(200.dp)
                .verticalScroll(rememberScrollState()),
            selectedIndex = selectedIndex,
            onBlendModeSelected = { index, mode ->
                blendMode = mode
                selectedIndex = index
            }
        )
    }
}

private val canvasModifier = Modifier
//    .background(Color.LightGray)
    .drawBehind {
        drawRect(Color.LightGray, style = Stroke(width = 4.dp.toPx()))
    }
    .fillMaxSize()
    .height(200.dp)
