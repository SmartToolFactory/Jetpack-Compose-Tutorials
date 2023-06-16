package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.ColorSlider
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial4_7_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "1-) In this example by changing color **Modifier.background** or " +
                    "**Modifier.drawWithContent**  changes in color are read in different phases can" +
                    "be observed. Offset state change for both modifiers are read in **Layout** phase"
        )
        PhasesSample()
    }
}

@Composable
private fun PhasesSample() {
    var offsetX by remember { mutableStateOf(0f) }
    var red by remember { mutableStateOf(0f) }
    var green by remember { mutableStateOf(0f) }
    var blue by remember { mutableStateOf(0f) }

    Text(text = "offsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    ColorSlider(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        title = "Red",
        titleColor = Color.Red,
        rgb = red,
        onColorChanged = {
            red = it
        }
    )

    Spacer(modifier = Modifier.height(4.dp))
    ColorSlider(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        title = "Green",
        titleColor = Color.Green,
        rgb = green,
        onColorChanged = {
            green = it
        }
    )
    Spacer(modifier = Modifier.height(4.dp))

    ColorSlider(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        title = "Blue",
        titleColor = Color.Blue,
        rgb = blue,
        onColorChanged = {
            blue = it
        }
    )

    val modifier1 = Modifier
        // offsetX is read in Layout phase
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("😃️ modifier1 LAYOUT")
                placeable.placeRelative(offsetX.dp.roundToPx(), 0)
            }
        }
        .drawWithContent {
            println("😜 modifier1 DRAW")
            drawContent()
        }
        // 🔥 reading color causes Composition->Layout->Draw phases to be run
        .background(Color(red.toInt(), green.toInt(), blue.toInt()))

    val modifier2 = Modifier
            // offsetX is read in Layout phase
        .offset {
            IntOffset(x = offsetX.dp.roundToPx(), 0)
        }
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("🍏 modifier2 LAYOUT")
                placeable.placeRelative(0, 0)
            }
        }
        // 🔥 deferring color read in lambda only calls Draw and skips Composition and Layout
        .drawWithContent {
            val color = Color(red.toInt(), green.toInt(), blue.toInt())
            println("🍎 modifier2 DRAW color: $color")
            drawRect(color)
            drawContent()
        }

    MyBox(modifier = modifier1, "modifier1")
    Spacer(modifier = Modifier.height(20.dp))
    MyBox(modifier = modifier2, "modifier2")

}

@Composable
private fun MyBox(modifier: Modifier, title: String) {

    LogCompositions(msg = "🔥 MyBox() COMPOSITION $title")

    Column(modifier) {

        // This Text changes color in every recomposition
        Text(
            text = title,
            modifier = Modifier
                .background(getRandomColor())
                .fillMaxWidth()
                .padding(4.dp)
        )

        Text(
            text = "modifier hash: ${modifier.hashCode()}\n"
                    + "Modifier: $modifier"
            ,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
