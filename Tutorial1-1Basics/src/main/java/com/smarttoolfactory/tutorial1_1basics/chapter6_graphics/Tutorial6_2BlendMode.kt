package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CustomDialogWithResultExample

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

    var selectedIndex by remember { mutableStateOf(3) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.DstOver) }

    var srcColor by remember { mutableStateOf(Color(0xff29B6F6)) }
    var dstColor by remember { mutableStateOf(Color(0xffEC407A)) }

    var showSrcColorDialog by remember { mutableStateOf(false) }
    var showDstColorDialog by remember { mutableStateOf(false) }


    Canvas(modifier = canvasModifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = canvasHeight / 2 - 100

        val horizontalOffset = 70f
        val verticalOffset = 50f

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            drawRect(
                srcColor,
                topLeft = Offset(
                    (canvasWidth - 2 * radius) / 2 - horizontalOffset,
                    (canvasHeight - 2 * radius) / 2 - verticalOffset
                ),
                size = Size(2 * radius, 2 * radius)
            )

            drawCircle(
                blendMode = blendMode,
                color = dstColor,
                radius = radius,
                center = Offset(
                    canvasWidth / 2 + horizontalOffset,
                    canvasHeight / 2 + verticalOffset
                ),
            )
            restoreToCount(checkPoint)
        }
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

    Text(
        text = "Dst BlendMode: $blendMode",
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

@Composable
private fun BlendModeSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onBlendModeSelected: (Int, BlendMode) -> Unit
) {
    val radioOptions = blendModes.keys.toList()


    val (selectedOption: String, onOptionSelected: (String) -> Unit) = remember {
        mutableStateOf(
            radioOptions[selectedIndex]
        )
    }
// Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        Modifier
            .selectableGroup()
            .then(modifier)
    ) {
        radioOptions.forEachIndexed { index, text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            onBlendModeSelected(index, blendModes[text]!!)
                        },
                        role = Role.RadioButton
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun BlendModeCheckBoxRow(
    text: String,
    selectedOption: String,
    onBlendModeSelected: (BlendMode) -> Unit
) {

    var selectedText by remember { mutableStateOf(text) }

    Row(
        Modifier
            .wrapContentWidth()
            .selectable(
                selected = (text == selectedOption),
                onClick = {
                    selectedText = text
                    onBlendModeSelected(blendModes[selectedText]!!)
                },
                role = Role.RadioButton
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (selectedText == selectedOption),
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

val blendModes = hashMapOf(
    "Clear" to BlendMode.Clear,
    "Src" to BlendMode.Src,
    "Dst" to BlendMode.Dst,
    "SrcOver" to BlendMode.SrcOver,
    "DstOver" to BlendMode.DstOver,
    "SrcIn" to BlendMode.SrcIn,
    "DstIn" to BlendMode.DstIn,
    "SrcOut" to BlendMode.SrcOut,
    "DstOut" to BlendMode.DstOut,
    "SrcAtop" to BlendMode.SrcAtop,
    "DstAtop" to BlendMode.DstAtop,
    "Xor" to BlendMode.Xor,
    "Plus" to BlendMode.Plus,
    "Modulate" to BlendMode.Modulate,
    "Screen" to BlendMode.Screen,
    "Overlay" to BlendMode.Overlay,
    "Darken" to BlendMode.Darken,
    "Lighten" to BlendMode.Lighten,
    "ColorDodge" to BlendMode.ColorDodge,
    "ColorBurn" to BlendMode.ColorBurn,
    "HardLight" to BlendMode.Hardlight,
    "Softlight" to BlendMode.Softlight,
    "Difference" to BlendMode.Difference,
    "Exclusion" to BlendMode.Exclusion,
    "Multiply" to BlendMode.Multiply,
    "Hue" to BlendMode.Hue,
    "Saturation" to BlendMode.Saturation,
    "Color" to BlendMode.Color,
    "Luminosity" to BlendMode.Luminosity
)

private val canvasModifier = Modifier
    .background(Color.LightGray)
//    .drawBehind {
//        drawRect(Color.LightGray, style = Stroke(width = 4.dp.toPx()))
//    }
    .fillMaxSize()
    .height(200.dp)
