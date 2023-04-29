package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

@Composable
fun Tutorial3_3Screen4() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        StyleableTutorialText(
            text = "**Modifier.onPlaced(onPlaced)** invokes **onPlaced** after the parent " +
                    "LayoutModifier and parent layout has been placed " +
                    "and before child LayoutModifier is placed. " +
                    "This allows child LayoutModifier to adjust its own placement " +
                    "based on where the parent is.",
            bullets = false
        )
        OnPlacedModifierSample()
        StyleableTutorialText(
            text = "In this example offset from **Modifier.onPlaced(onPlaced)** sets  with " +
                    "-0.5 multiplier is used to set **Modifier.offset{}**.",
            bullets = false
        )
        OnPlacedAndOffsetModifierSample()
    }
}

@Composable
private fun OnPlacedModifierSample() {
    var alignment by remember {
        mutableStateOf(Alignment.Center)
    }

    var alignmentValue by remember {
        mutableStateOf(0f)
    }

    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }

    alignment = when (alignmentValue.roundToInt()) {
        0 -> Alignment.TopStart
        1 -> Alignment.TopCenter
        2 -> Alignment.TopEnd
        3 -> Alignment.CenterStart
        4 -> Alignment.Center
        5 -> Alignment.CenterEnd
        6 -> Alignment.BottomStart
        7 -> Alignment.BottomCenter
        else -> Alignment.BottomEnd
    }

    val text = when (alignmentValue.roundToInt()) {
        0 -> "Alignment.TopStart"
        1 -> "Alignment.TopCenter"
        2 -> "Alignment.TopEnd"
        3 -> "Alignment.CenterStart"
        4 -> "Alignment.Center"
        5 -> "Alignment.CenterEnd"
        6 -> "Alignment.BottomStart"
        7 -> "Alignment.BottomCenter"
        else -> "Alignment.BottomEnd"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Text(text = "Alignment: $text", fontSize = 20.sp)
        Text(text = "Offset: $targetOffset", fontSize = 20.sp)
        Slider(
            value = alignmentValue,
            onValueChange = {
                alignmentValue = it
            },
            valueRange = 0f..8f,
            steps = 7
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(4.dp)
                .border(2.dp, Orange400)
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned {
                        val position = it
                            .positionInParent()
                            .round()
                        println("🍎 onGloballyPositioned() position: $position")
                    }
                    .onPlaced {
                        // Calculate the position in the parent layout
                        targetOffset = it
                            .positionInParent()
                            .round()
                        println("🍏 onPlaced() targetOffset: $targetOffset")
                    }
                    .align(alignment)
                    .size(100.dp)
                    .background(Pink400)
            )
        }
    }
}

@Composable
private fun OnPlacedAndOffsetModifierSample() {
    var alignment by remember {
        mutableStateOf(Alignment.Center)
    }

    var alignmentValue by remember {
        mutableStateOf(0f)
    }

    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }

    alignment = when (alignmentValue.roundToInt()) {
        0 -> Alignment.TopStart
        1 -> Alignment.TopCenter
        2 -> Alignment.TopEnd
        3 -> Alignment.CenterStart
        4 -> Alignment.Center
        5 -> Alignment.CenterEnd
        6 -> Alignment.BottomStart
        7 -> Alignment.BottomCenter
        else -> Alignment.BottomEnd
    }

    val text = when (alignmentValue.roundToInt()) {
        0 -> "Alignment.TopStart"
        1 -> "Alignment.TopCenter"
        2 -> "Alignment.TopEnd"
        3 -> "Alignment.CenterStart"
        4 -> "Alignment.Center"
        5 -> "Alignment.CenterEnd"
        6 -> "Alignment.BottomStart"
        7 -> "Alignment.BottomCenter"
        else -> "Alignment.BottomEnd"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Text(text = "Alignment: $text", fontSize = 20.sp)
        Text(text = "Offset: $targetOffset", fontSize = 20.sp)
        Slider(
            value = alignmentValue,
            onValueChange = {
                alignmentValue = it
            },
            valueRange = 0f..8f,
            steps = 7
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(4.dp)
                .border(2.dp, Orange400)
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned {
                        val position = it
                            .positionInParent()
                            .round()
                        println("🍎 onGloballyPositioned() position: $position")
                    }
                    .onPlaced {
                        // Calculate the position in the parent layout
                        targetOffset = it
                            .positionInParent()
                            .round()
                        println("🍏 onPlaced() targetOffset: $targetOffset")

                    }
                    .offset {
                        println("🏈 offset() ")
                        targetOffset.div(-2f)
                    }

                    .align(alignment)
                    .size(100.dp)
                    .background(Pink400)
            )
        }
    }
}


