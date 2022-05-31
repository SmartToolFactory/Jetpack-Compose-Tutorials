package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

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
            text = "1-) In this example offset in second " +
                    "modifier **Modifier.offset{}** defers reading state from recomposition to layout." +
                    "Because of this Column that uses modifier doesn't get recomposed. title background" +
                    "color changes at each recomposition"
        )
        PhasesSample1()
        StyleableTutorialText(
            text = "1-) In this example first one uses **Modifier.background()** which changes" +
                    "background color on each recomposition while second one uses " +
                    "**Modifier.drawBehind{}** which defers color read to **Layout** phase"
        )
        PhasesSample2()
    }
}

@Composable
private fun PhasesSample1() {

    var offsetX by remember { mutableStateOf(0f) }

    Text(text = "OffsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    val modifier1 = Modifier
        .fillMaxWidth()
        .offset(x = offsetX.dp)
        .background(Blue400)

    // Deferring state to layout prevents the Composable that has this modifier to be recomposed

    val modifier2 = Modifier
        .fillMaxWidth()
        .offset {
            val newX = offsetX.dp.roundToPx()
            IntOffset(newX, 0)
        }
        .background(Blue400)


    MyBox(modifier = modifier1, "modifier1")
    Spacer(modifier = Modifier.height(20.dp))
    MyBox(modifier = modifier2, "modifier2")
}


@Composable
private fun PhasesSample2() {

    var offsetX by remember { mutableStateOf(0f) }


    Text(text = "OffsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    val modifier1 = Modifier
        .fillMaxWidth()
        .layout { measurable, constraints ->
            println("🍏 modifier1 LAYOUT")
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(offsetX.dp.roundToPx(), 0)
            }
        }
        .drawBehind {
            println("🍎 modifier1 DRAW")
            drawRect(getRandomColor())
        }

    val modifier2 = Modifier
        .offset {
            val newX = offsetX.dp.roundToPx()
            IntOffset(newX, 0)
        }
        .fillMaxWidth()
        .background(getRandomColor())

    MyBox(modifier = modifier1, "modifier1")
    Spacer(modifier = Modifier.height(20.dp))
    MyBox(modifier = modifier2, "modifier2")
}


@Composable
private fun MyBox(modifier: Modifier, title: String) {

    println("🔥MyBox() COMPOSITION $title")

    Column(modifier) {

        // This Text changes color at each recomposition
        Text(
            text = title,
            modifier = Modifier
                .background(getRandomColor())
                .fillMaxWidth()
                .padding(4.dp)
        )

        Text(
            text = "modifier hash: ${modifier.hashCode()}\n" +
                    "Modifier: $modifier",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}