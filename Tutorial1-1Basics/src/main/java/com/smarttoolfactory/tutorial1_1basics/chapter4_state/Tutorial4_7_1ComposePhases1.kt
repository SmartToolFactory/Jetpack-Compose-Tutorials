package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

/**
 * https://developer.android.com/jetpack/compose/phases
 * https://developer.android.com/jetpack/compose/performance#defer-reads
 *
 * This tutorial shows hos deferring read of a value effects frame phases
 */
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
                    "modifier **Modifier.offset{}** defers reading state " +
                    "from **Composition to **Layout." +
                    "Because of this Column that uses modifier " +
                    "doesn't get recomposed. Text " +
                    "color changes to visualize recomposition"
        )
        PhasesSample1()
        StyleableTutorialText(
            text = "2-) In this example first one uses " +
                    "**Modifier.drawBehind{}** which defers color read to **Layout** phase. " +
                    "Second one uses **Modifier.background()** which changes" +
                    "background color in each recomposition."
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
        .offset(x = offsetX.dp)
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("😃️ modifier1 LAYOUT")
                placeable.placeRelative(0, 0)
            }
        }
        .background(Blue400)
        .drawWithContent {
            println("😜 modifier1 DRAW")
            drawContent()
        }

    // Deferring state to Layout phase prevents
    // Composables that have this modifier to be recomposed
    val modifier2 = Modifier
        .offset {
            println("🍏 modifier2 LAYOUT")
            val newX = offsetX.dp.roundToPx()
            IntOffset(newX, 0)
        }
        .background(Blue400)
        .drawWithContent {
            println("🍎 modifier2 DRAW")
            drawContent()
        }


    MyBox(modifier = modifier1, "modifier1")
    Spacer(modifier = Modifier.height(20.dp))
    MyBox(modifier = modifier2, "modifier2")
}


@Composable
private fun PhasesSample2() {

    println("SAMPLE2 RECOMPOSING...")

    // This value is for triggering recomposition for PhasesSample2
    var someValue by remember { mutableStateOf(0f) }
    Text(text = "someValue")
    Slider(value = someValue,
        valueRange = 0f..50f,
        onValueChange = {
            someValue = it
        }
    )

    val modifier3 = Modifier
        .offset {
            println("🚕 modifier3 LAYOUT")
            IntOffset(x = 0, 0)
        }
        // 🔥 deferring color read in lambda only calls Draw and skips Composition and Layout
        .drawWithContent {
            // 🔥🔥 Changing offset in PhasesSample2 triggers DRAW phase????
            // https://stackoverflow.com/questions/72457805/jetpack-compose-defering-reads-in-phases-for-performance
            println("🚗 modifier3 DRAW")
            drawRect(getRandomColor())
            drawContent()
        }

    val modifier4 = Modifier
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("⚾️ modifier4 LAYOUT")
                placeable.placeRelative(0, 0)
            }
        }
        .drawWithContent {
            println("🎾 modifier4 DRAW")
            drawContent()
        }
        // 🔥 reading color causes Composition->Layout->Draw
        .background(getRandomColor())

    MyBox(modifier = modifier3, "modifier3")
    Spacer(modifier = Modifier.height(20.dp))
    MyBox(modifier = modifier4, "modifier4")
}

@Composable
internal fun MyBox(modifier: Modifier, title: String) {

    println("🔥MyBox() COMPOSITION $title")

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
            text = "modifier hash: ${modifier.hashCode()}\n" +
                    "Modifier: $modifier",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}