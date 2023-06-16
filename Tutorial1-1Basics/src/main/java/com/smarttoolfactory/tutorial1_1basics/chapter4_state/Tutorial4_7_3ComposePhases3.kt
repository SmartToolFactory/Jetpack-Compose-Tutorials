package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
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
            text = "1-) In this example state is deferred until **InnerDeferred** composable " +
                    "reads. Because of that Composables between don't get recomposed when value " +
                    "of Slider changes"
        )
        DeferredComposablesSample()

        StyleableTutorialText(
            text = "2-) In this example value is passed directly, even " +
                    "if **Outer** and **Middle** " +
                    "composables don't read the value they are recomposed"
        )
        NonDeferredComposablesSample()

        StyleableTutorialText(
            text = "3-) In this example state is deferred until **InnerDeferred** composable " +
                    "reads the value. Even if there is no **Modifier.padding{}** " +
                    "with lambda we send lambda " +
                    "to inner composable to make sure only inner Composable is recomposed"
        )
        DeferredPaddingComposablesSample()
    }
}

@Composable
private fun DeferredComposablesSample() {
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

    OuterDeferred {
        offsetX.toInt()
    }
}

@Composable
private fun NonDeferredComposablesSample() {
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

    Outer(offsetX.toInt())
}

@Composable
private fun DeferredPaddingComposablesSample() {
    var padding by remember { mutableStateOf(0f) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Padding")
        Spacer(modifier = Modifier.width(5.dp))
        Slider(value = padding,
            valueRange = 0f..50f,
            onValueChange = {
                padding = it
            }
        )

    }
    PaddingOuterDeferred {
        padding.dp
    }
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


@Composable
private fun PaddingOuterDeferred(padding: () -> Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {

        LogCompositions(msg = "😍 PaddingOuterDeferred")
        Text("PaddingOuterDeferred")
        PaddingMiddleDeferred(padding)
    }
}

@Composable
private fun PaddingMiddleDeferred(padding: () -> Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(10.dp)
    ) {
        LogCompositions(msg = "😃 PaddingMiddleDeferred")
        Text("PaddingMiddleDeferred")
        PaddingInnerDeferred(padding)
    }
}

@Composable
private fun PaddingInnerDeferred(padding: () -> Dp) {

    LogCompositions(msg = "😜 PaddingInnerDeferred")

    Text(
        text = "PaddingInnerDeferred",
        modifier = Modifier
            .padding(start = padding())
            .fillMaxWidth()
            .background(getRandomColor())

    )
}

