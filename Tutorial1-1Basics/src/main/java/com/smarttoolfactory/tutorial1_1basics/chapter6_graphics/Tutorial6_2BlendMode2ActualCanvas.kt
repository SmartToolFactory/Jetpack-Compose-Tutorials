package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CustomDialogWithResultExample
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
@Composable
fun Tutorial6_2Screen2() {
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
            "Canvas(ImageBitmap) and Blend Modes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        StyleableTutorialText(
            text = "In this example **Canvas(ImageBitmap)** is used to draw on **ImageBitmap** " +
                    "itself",
            bullets = false
        )

        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(
            text = "Draw Shapes with Blend Mode"
        )
        DrawShapeBlendMode()

        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(
            text = "Draw on Bitmap with Blend Mode\n" +
                    "Source is Circle"
        )
        NativeCanvasSample()

        Spacer(modifier = Modifier.height(30.dp))
        TutorialText2(
            text = "Draw on Bitmap with Blend Mode\n" +
                    "Source is Image"
        )
        NativeCanvasSample2()
    }
}

private enum class SaveLayer {
    Paint, BlendModePaint, NoPaint
}

@Composable
private fun DrawShapeBlendMode() {

    var selectedBlendModeIndex by remember { mutableStateOf(3) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcOver) }


    var selectedSaveLayerIndex by remember { mutableStateOf(0) }
    val radioOptions = SaveLayer.values()

    val (selectedSaveLayerType: SaveLayer, onOptionSelected: (SaveLayer) -> Unit) = remember {
        mutableStateOf(
            radioOptions[selectedSaveLayerIndex]
        )
    }

    val imageBitmap = ImageBitmap
        .imageResource(
            LocalContext.current.resources,
            R.drawable.landscape2
        )
        .asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)
        .asImageBitmap()


    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ) {

        val canvas: Canvas = Canvas(imageBitmap)

        val blendModePaint = remember {
            Paint().apply {
                this.color = Color(0xffEC407A)
            }
        }

        blendModePaint.blendMode = blendMode

        val paint = remember {
            Paint().apply {
                this.color = Color(0xff29B6F6)
            }
        }

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()

            with(canvas.nativeCanvas) {

                // 🔥 imagePaint is used for saving this layer
                when (selectedSaveLayerType) {
                    SaveLayer.BlendModePaint -> saveLayer(
                        nativeCanvas.clipBounds.toComposeRect(),
                        blendModePaint
                    )
                    SaveLayer.Paint -> saveLayer(nativeCanvas.clipBounds.toComposeRect(), paint)
                    else -> Unit
                }

                val checkPoint = if (selectedSaveLayerType == SaveLayer.NoPaint) {
                    saveLayer(null, null)
                } else 0

                // Destination
                drawRect(0f, 0f, 200f, 200f, paint)

                // Source
                drawCircle(
                    center = Offset(canvasWidth / 2, canvasHeight / 2),
                    radius = canvasHeight / 3,
                    paint = blendModePaint
                )

                if (selectedSaveLayerType == SaveLayer.NoPaint) {
                    restoreToCount(checkPoint)
                } else {
                    restore()
                }
            }
        }

        Image(
            modifier = Modifier
                .matchParentSize()
                .background(Color.LightGray)
                .border(2.dp, Color.Red),
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "SaveLayer with Paint: $selectedSaveLayerType",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )


    Column(
        Modifier
            .selectableGroup()
    ) {
        radioOptions.forEachIndexed { index, saveLayer ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (saveLayer == selectedSaveLayerType),
                        onClick = {
                            selectedSaveLayerIndex = index
                            onOptionSelected(saveLayer)
                        },
                        role = Role.RadioButton
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (saveLayer == selectedSaveLayerType),
                    onClick = null
                )
                Text(
                    text = saveLayer.toString(),
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
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
        selectedIndex = selectedBlendModeIndex,
        onBlendModeSelected = { index, mode ->
            blendMode = mode
            selectedBlendModeIndex = index
        }
    )
}

@Composable
fun NativeCanvasSample() {

    var selectedBlendModeIndex by remember { mutableStateOf(5) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcIn) }

    var color by remember { mutableStateOf(Color(0xff29B6F6)) }
    var showColorDialog by remember { mutableStateOf(false) }


    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape3
    ).asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()

    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ){

        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight

        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height

        var offset by remember {
            mutableStateOf(Offset(bitmapWidth / 2f, bitmapHeight / 2f))
        }

        val imageBitmapDrawOn: ImageBitmap = remember {
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888).asImageBitmap()
        }

        val canvas: Canvas = remember {
            Canvas(imageBitmapDrawOn)
        }

        val paint = remember {
            Paint().apply {
                this.color = Color(0xffEC407A)
            }
        }

        val erasePaint = remember {
            Paint()
        }

        erasePaint.blendMode = blendMode
        erasePaint.color = color

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()

            with(canvas.nativeCanvas) {

                // Clear previous drawing
                drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                // Destination
                drawImageRect(
                    image = imageBitmap,
                    dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                    paint = paint
                )

                // Source
                drawCircle(
                    center = offset,
                    radius = 50f,
                    paint = erasePaint
                )

            }
        }

        val canvasModifier = Modifier.pointerMotionEvents(
            Unit,
            onDown = {
                val position = it.position
                val offsetX = position.x * bitmapWidth / imageWidth
                val offsetY = position.y * bitmapHeight / imageHeight
                offset = Offset(offsetX, offsetY)
                it.consume()
            },
            onMove = {
                val position = it.position
                val offsetX = position.x * bitmapWidth / imageWidth
                val offsetY = position.y * bitmapHeight / imageHeight
                offset = Offset(offsetX, offsetY)
                it.consume()
            },
            delayAfterDownInMillis = 20
        )


        Image(
            modifier = canvasModifier
                .matchParentSize()
                .background(Color.LightGray)
                .border(2.dp, Color.Red),
            bitmap = imageBitmapDrawOn,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

    OutlinedButton(onClick = { showColorDialog = true }) {
        Text(
            text = "Dst Color",
            fontSize = 16.sp,
            color = color,
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
        selectedIndex = selectedBlendModeIndex,
        onBlendModeSelected = { index, mode ->
            blendMode = mode
            selectedBlendModeIndex = index
        }
    )

    if (showColorDialog) {
        CustomDialogWithResultExample(
            initialColor = color,
            onDismiss = {
                showColorDialog = false
            },
            onNegativeClick = {
                showColorDialog = false
            },
            onPositiveClick = {
                showColorDialog = false
                color = it
            }
        )
    }
}

@Composable
fun NativeCanvasSample2() {

    var selectedBlendModeIndex by remember { mutableStateOf(5) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.SrcIn) }

    var color by remember { mutableStateOf(Color(0xff29B6F6)) }
    var showColorDialog by remember { mutableStateOf(false) }


    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape3
    ).asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()

    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ){

        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight

        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height

        var offset by remember {
            mutableStateOf(Offset(bitmapWidth / 2f, bitmapHeight / 2f))
        }

        val imageBitmapDrawOn: ImageBitmap = remember {
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888).asImageBitmap()
        }

        val canvas: Canvas = remember {
            Canvas(imageBitmapDrawOn)
        }

        val paint = remember {
            Paint().apply {
                this.color = Color(0xffEC407A)
            }
        }

        val erasePaint = remember {
            Paint()
        }

        erasePaint.blendMode = blendMode
        erasePaint.color = color

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()

            with(canvas.nativeCanvas) {

                // Clear previous drawing
                drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                // Destination
                drawCircle(
                    center = offset,
                    radius = 50f,
                    paint = paint
                )

                // Source
                drawImageRect(
                    image = imageBitmap,
                    dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                    paint = erasePaint
                )
            }
        }

        val canvasModifier = Modifier.pointerMotionEvents(
            Unit,
            onDown = {
                val position = it.position
                val offsetX = position.x * bitmapWidth / imageWidth
                val offsetY = position.y * bitmapHeight / imageHeight
                offset = Offset(offsetX, offsetY)
                it.consume()
            },
            onMove = {
                val position = it.position
                val offsetX = position.x * bitmapWidth / imageWidth
                val offsetY = position.y * bitmapHeight / imageHeight
                offset = Offset(offsetX, offsetY)
                it.consume()
            },
            delayAfterDownInMillis = 20
        )


        Image(
            modifier = canvasModifier
                .matchParentSize()
                .background(Color.LightGray)
                .border(2.dp, Color.Red),
            bitmap = imageBitmapDrawOn,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

    OutlinedButton(onClick = { showColorDialog = true }) {
        Text(
            text = "Dst Color",
            fontSize = 16.sp,
            color = color,
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
        selectedIndex = selectedBlendModeIndex,
        onBlendModeSelected = { index, mode ->
            blendMode = mode
            selectedBlendModeIndex = index
        }
    )

    if (showColorDialog) {
        CustomDialogWithResultExample(
            initialColor = color,
            onDismiss = {
                showColorDialog = false
            },
            onNegativeClick = {
                showColorDialog = false
            },
            onPositiveClick = {
                showColorDialog = false
                color = it
            }
        )
    }
}