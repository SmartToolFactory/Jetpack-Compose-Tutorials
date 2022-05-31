package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

/**
 * In this example
 * how changing a state or lambda a Modifier reads effects that Modifier is displayed.
 * Using a lambda instead of value with [Modifier.offset] defers read which causes it to be
 * not recomposed.
 *
 * Check the [link](https://developer.android.com/jetpack/compose/performance)
 * or next tutorial [Tutorial4_7_1Screen] for Compose phases
 *
 */
@Composable
fun Tutorial4_6Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {

        StyleableTutorialText(
            text = "Changing a state that is read by a **Modifier** causes " +
                    "that section to be composed." +
                    "For instance if we change state that sets padding for " +
                    "**Modifier.padding().background().size()** " +
                    "only the **PaddingModifier** is changed while Size and Background " +
                    "are the same " +
                    "modifiers from previous recomposition",
            bullets = false
        )
        ModifierRecompositionSample()
    }
}

@Composable
private fun ModifierRecompositionSample() {

    var padding by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(150f) }

    Text(text = "Padding")
    Slider(value = padding,
        valueRange = 0f..50f,
        onValueChange = {
            padding = it
        }
    )

    Text(text = "OffsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    Text(text = "Height")
    Slider(value = height,
        valueRange = 150f..350f,
        onValueChange = {
            height = it
        }
    )

    // This modifier is never recomposed when Column is recomposed(border changes)
    // since it doesn't read any value from any state
    val modifier1 = Modifier
        .fillMaxWidth()
        .heightIn(max = 350.dp)
        .background(Red400)
    // Using new background returns new Modifier
//        .background(getRandomColor())

    val modifier2 = Modifier
        .padding(start = padding.dp)
        .fillMaxWidth()
        .heightIn(max = 350.dp)
        .background(Blue400)
//        .background(getRandomColor())


    val modifier3 = Modifier
        .offset(x = offsetX.dp)
        .fillMaxWidth()
        .height(height.dp)
        .background(Pink400  )
//        .background(getRandomColor())


    val modifier4 = Modifier
        // 🔥 Using a lambda instead of Modifier.offset(x) defers read from composition
        // phase as displayed in next tutorial
        .offset {
            // this is only to move same amount as Modifier3, it's not for showing
            // Modifier recomposition
            val newX = offsetX.dp.roundToPx()
            IntOffset(newX, 0)
        }
        .fillMaxWidth()
        .height(height.dp)
        .background(Green400)
//        .background(getRandomColor())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .border(4.dp, getRandomColor())
    ) {

        Box(modifier1) {
            Text(
                text = "modifier1 hash: ${modifier1.hashCode()}\n" +
                        "Modifier: $modifier1",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Box(modifier2) {
            Text(
                text = "modifier2 hash: ${modifier2.hashCode()}\n" +
                        "Modifier: $modifier2",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Box(modifier3) {
            Text(
                text = "modifier3 hash: ${modifier3.hashCode()}\n" +
                        "Modifier: $modifier3",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Box(modifier4) {
            Text(
                text = "modifier4 hash: ${modifier4.hashCode()}\n" +
                        "Modifier: $modifier4",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
