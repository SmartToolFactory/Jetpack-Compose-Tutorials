package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Composable
fun Tutorial4_7_3Screen() {
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
            text = "1-) In this example state is deferred until **InnerDeferred** composable" +
                    "and because of that Composables between don't get recomposed when value" +
                    "of slider changes"
        )
        DeferredComposablesSample()
        StyleableTutorialText(
            text = "2-) In this example value is passed directly, even if **Outer** and **Middle**" +
                    " composables don't read the value they are recomposed"
        )
        Spacer(modifier = Modifier.height(20.dp))
        NonDeferredComposablesSample()
    }
}

@Composable
private fun DeferredComposablesSample() {
    var offsetX by remember { mutableStateOf(0f) }

    Text(text = "OffsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    OuterDeferred {
        offsetX.toInt()
    }
}

@Composable
private fun NonDeferredComposablesSample() {
    var offsetX by remember { mutableStateOf(0f) }

    Text(text = "OffsetX")
    Slider(value = offsetX,
        valueRange = 0f..50f,
        onValueChange = {
            offsetX = it
        }
    )

    Outer(offsetX.toInt())
}

@Composable
private fun OuterDeferred(offset: () -> Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {

        LogCompositions(msg = "🚙 OuterDeferred")
        Text("OuterDeferred")
        MiddleDeferred(offset)
    }
}

@Composable
private fun MiddleDeferred(offset: () -> Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {
        LogCompositions(msg = "🚗 MiddleDeferred")
        Text("MiddleDeferred")
        InnerDeferred(offset)
    }
}

@Composable
private fun InnerDeferred(offset: () -> Int) {
    LogCompositions(msg = "🚕 InnerDeferred")

    Text(
        text = "InnerDeferred",
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .offset {
                IntOffset(x = offset().dp.roundToPx(), 0)
            }
    )
}



@Composable
private fun Outer(offset: Int) {
    LogCompositions(msg = "🍋 Outer")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {

        Text("Outer")
        Middle(offset)
    }
}

@Composable
private fun Middle(offset: Int) {
    LogCompositions(msg = "🍏 Middle")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {
        Text("Middle")
        Inner(offset)
    }
}

@Composable
private fun Inner(offset: Int) {
    LogCompositions(msg = "🍎 Inner")
    Text(
        text = "Inner",
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .offset {
                IntOffset(x = offset.dp.roundToPx(), 0)
            }
    )
}
