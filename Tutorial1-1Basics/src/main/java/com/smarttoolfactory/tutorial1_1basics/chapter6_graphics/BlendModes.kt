package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun BlendModeSelection(
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

val blendModes = linkedMapOf(
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
    "SoftLight" to BlendMode.Softlight,
    "Difference" to BlendMode.Difference,
    "Exclusion" to BlendMode.Exclusion,
    "Multiply" to BlendMode.Multiply,
    "Hue" to BlendMode.Hue,
    "Saturation" to BlendMode.Saturation,
    "Color" to BlendMode.Color,
    "Luminosity" to BlendMode.Luminosity
)