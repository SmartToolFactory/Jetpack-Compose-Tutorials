package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CustomDialogWithResultExample
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/*
    For more info about Blend or Porter-Duff modes refer official links below
    https://developer.android.com/reference/android/graphics/PorterDuff.Mode
    https://medium.com/mobile-app-development-publication/practical-image-porterduff-mode-usage-in-android-3b4b5d2e8f5f

    PorteDuffMode is a software image blending method that is available in many software platforms,
    which includes Android. It is based on a mathematical model which runs on the pixels
    of two images to produce a neat output.

    We simply draw destination shape/image first, then draw source shape/image and apply
    blend mode to source.

 */
@Composable
fun Tutorial6_2Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Blend (PorterDuff) Modes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        StyleableTutorialText(
            text = "Blend modes are used to clip, change position of destination/source " +
                    "or manipulate pixels." +
                    "\nFirst drawn shape/image is **Destination**, second one that drawn with " +
                    "**blend mode** is **Source**",
            bullets = false
        )

        TutorialText2(text = "Draw Shapes with Blend Mode")
        DrawShapeBlendMode()
        TutorialText2(text = "Draw Images with Blend Mode")
        DrawImageBlendMode()
        TutorialText2(text = "Clip Image with Blend Mode Via Path")
        ClipImageWithBlendModeViaPath()
        TutorialText2(text = "Clip Image with Blend Mode Via Image")
        ClipImageWithBlendModeViaAnotherImage()

        TutorialText2(text = "Reveal Shape drawn below transparent")
        RevealShapeWithBlendMode()
        TutorialText2(text = "Reveal Shape drawn above transparent")
        RevealShapeWithBlendMode2()

        TutorialText2(text = "Add colors with BlendMode.Plus")
        ColorAddBlendMode()
    }
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
        val srcPath = createPolygonPath(cx, cy, 5, radius)

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
            initialColor = srcColor,
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
            initialColor = dstColor,
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
 * src image is clipped using shape drawn behind as dst.
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
        val path = createPolygonPath(cx.toFloat(), cy.toFloat(), sides.roundToInt(), radius)


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
                srcSize = IntSize(srcBitmap.width, srcBitmap.height),
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

/**
 * Clip landscape using transparent bubbles. Depending on blending mode both, src, dst or neither
 * of them are clipped or pixels are transformed.
 */
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
                srcSize = IntSize(dstBitmap.width, dstBitmap.height),
                dstSize = IntSize(canvasWidth, canvasHeight)
            )

            // Source
            drawImage(
                blendMode = blendMode,
                image = srcBitmap,
                srcSize = IntSize(srcBitmap.width, srcBitmap.height),
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

/**
 *
 * Remove circle from transparent rectangle over image using [BlendMode]
 *
 * This example uses [BlendMode.SrcOut] to clip circle path from transparent rectangle over image.
 *
 * Circle that removed from transparent rectangle is drawn as Destination.
 *
 * Transparent rectangle is drawn as Source with Blend Mode.
 */
@Composable
private fun RevealShapeWithBlendMode() {

    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.landscape10)

    var selectedIndex by remember { mutableStateOf(7) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcOut) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        drawImage(
            image = dstBitmap,
            srcSize = IntSize(dstBitmap.width, dstBitmap.height),
            dstSize = IntSize(canvasWidth, canvasHeight)
        )

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawCircle(
                color = Color.Red,
                radius = 200f,
            )

            // Source
            drawRect(Color(0xaa000000), blendMode = blendMode)

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
 *
 * Remove circle from transparent rectangle over image using [BlendMode]
 *
 * This example uses [BlendMode.Clear] to clip circle path from transparent rectangle over image.
 *
 * Circle that removed from transparent rectangle is drawn as Source with BlendMode
 */
@Composable
private fun RevealShapeWithBlendMode2() {

    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.landscape1)

    var selectedIndex by remember { mutableStateOf(0) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.Clear) }

    Canvas(modifier = canvasModifier) {
        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        drawImage(
            image = dstBitmap,
            srcSize = IntSize(dstBitmap.width, dstBitmap.height),
            dstSize = IntSize(canvasWidth, canvasHeight)
        )

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(Color(0xaa000000))

            // Source
            drawCircle(
                color = Color.Transparent,
                radius = 200f,
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

@Composable
private fun ColorAddBlendMode() {

    Canvas(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .background(Color.White)
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2f
        val cy = canvasHeight / 2f
        val r = canvasWidth / 7f

        val tx = (r * cos(30 * Math.PI / 180)).toFloat()
        val ty = (r * sin(30 * Math.PI / 180)).toFloat()
        val expand = 1.5f

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            drawCircle(
                color = Color.Red,
                radius = expand * r,
                center = Offset(cx, cy - r),
                blendMode = BlendMode.Plus
            )
            drawCircle(
                color = Color.Green,
                radius = expand * r,
                center = Offset(cx - tx, cy + ty),
                blendMode = BlendMode.Plus
            )
            drawCircle(
                color = Color.Blue,
                radius = expand * r,
                center = Offset(cx + tx, cy + ty),
                blendMode = BlendMode.Plus
            )

            restoreToCount(checkPoint)
        }
    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxSize()
    .height(200.dp)
