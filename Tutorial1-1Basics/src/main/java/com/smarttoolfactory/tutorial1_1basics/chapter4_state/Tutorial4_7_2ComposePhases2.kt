package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
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
 * This tutorial shows deferring read of a value effects frame phases
 */
@Preview
@Composable
fun Tutorial4_7_2Screen() {
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
            text = " **Modifier.offset{}** defers reading state " +
                    "from **Composition** to **Layout**",
            bullets = false
        )
        Column(
            modifier = Modifier
                .shadow(2.dp)
                .background(getRandomColor())
        ) {
            PhasesSample1()
        }

        StyleableTutorialText(
            text = "**Modifier.drawBehind{}** defers reading state to **Draw**",
            bullets = false
        )
        Column(
            modifier = Modifier
                .shadow(2.dp)
                .background(getRandomColor())
        ) {
            PhasesSample2()
        }
    }
}

@Composable
private fun PhasesSample1() {

    LogCompositions(msg = "1️⃣ PhasesSample1")

    var offsetX by remember { mutableStateOf(0f) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "OffsetX")
        Spacer(modifier = Modifier.width(5.dp))
        Slider(value = offsetX,
            valueRange = 0f..50f,
            onValueChange = {
                offsetX = it
            }
        )
    }

    val modifier1 = Modifier
        // Reads value directly
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


    val modifier2 = Modifier
        // Deferring state to Layout phase prevents
        // Composables that have this modifier to be recomposed
        .offset {
            val newX = offsetX.dp.roundToPx()
            IntOffset(newX, 0)
        }
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("🍏 modifier2 LAYOUT")
                placeable.placeRelative(0, 0)
            }
        }
        .background(Blue400)
        .drawWithContent {
            println("🍎 modifier2 DRAW")
            drawContent()
        }

    MyBox(modifier = modifier1, "modifier1")
    Spacer(modifier = Modifier.height(8.dp))
    MyBox(modifier = modifier2, "modifier2")
}

@Composable
private fun PhasesSample2() {

    LogCompositions(msg = "2️⃣  PhasesSample2")

    // This state is for triggering recomposition for PhasesSample2,
    // child composables don't read this state
    var someValue by remember { mutableStateOf(0f) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "someValue")
        Spacer(modifier = Modifier.width(5.dp))
        Slider(value = someValue,
            valueRange = 0f..50f,
            onValueChange = {
                someValue = it
            }
        )
    }

    val modifier3 = Modifier
        .layout { measurable, constraints ->
            val placeable: Placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                println("🚕 modifier3 LAYOUT")
                placeable.placeRelative(0, 0)
            }
        }
        // 🔥 deferring color read in lambda only calls Draw and skips Composition and Layout
        .drawWithContent {
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
    Spacer(modifier = Modifier.height(8.dp))
    MyBox(modifier = modifier4, "modifier4")
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
                .padding(2.dp)
        )

        Text(
            text = "modifier hash: ${modifier.hashCode()}\n"
                    + "Modifier: $modifier",
            color = Color.White,
            modifier = Modifier.heightIn(max = 120.dp),
            fontSize = 10.sp
        )
    }
}