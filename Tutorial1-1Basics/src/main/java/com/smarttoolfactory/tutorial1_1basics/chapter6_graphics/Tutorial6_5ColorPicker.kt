package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.ColorSlider
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker.ColorPickerWheel
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker.ColorfulSlider
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.colorpicker.SaturationRhombus
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400

// FIXME There are bugs with ColorWheel and SaturationRhombus
@Composable
fun Tutorial6_5Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        var hue by remember { mutableStateOf(0f) }
        var saturation by remember { mutableStateOf(0.5f) }
        var lightness by remember { mutableStateOf(0.5f) }
        var value by remember { mutableStateOf(0.5f) }

        val color = Color.hsl(hue = hue, saturation = saturation, lightness = lightness)

//        println("😡 Saturation $saturation, lightness: $lightness")

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = "⚠️ UNDER CONSTRUCTION", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ColorPickerWheel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            SaturationRhombus(
                modifier = Modifier.size(200.dp),
                hue = hue,
                saturation = saturation,
                lightness = lightness
            ) { s, l ->
                println("CHANGING sat: $s, lightness: $l")
                saturation = s
                lightness = l
            }


            Text(
                text = "Color",
                color = Blue400,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Initial and Current Colors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp, vertical = 20.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            Color.Black,
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            color,
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        )
                )
            }


            // Sliders
            ColorSlider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                title = "Hue",
                titleColor = Color.Red,
                rgb = hue,
                onColorChanged = {
                    hue = it
                },
                valueRange = 0f..360f
            )
            Spacer(modifier = Modifier.height(4.dp))
            ColorSlider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                title = "Saturation",
                titleColor = Color.Green,
                rgb = saturation,
                onColorChanged = {
                    saturation = it
                },
                valueRange = 0f..1f

            )
            Spacer(modifier = Modifier.height(4.dp))

            ColorSlider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                title = "Lightness",
                titleColor = Color.Blue,
                rgb = lightness,
                onColorChanged = {
                    lightness = it
                },
                valueRange = 0f..1f
            )

        }
        ColorfulSlider()
    }
}
