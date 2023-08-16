package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2


val composableBackgroundColor = Color(0xffFF9800)
val textBackgroundColor = Color(0xff2196F3)

/**
 * This tutorial shows when and how to use [Constraints.offset] or [Constraints.constrainWidth].
 *
 * Setting padding with or without these functions and how they effect layout
 * especially when our Composable dimensions are at same size of or bigger than its Parent.
 *
 */
@Preview
@Composable
fun Tutorial3_2Screen8() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    var message by remember { mutableStateOf("Type to monitor overflow") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TutorialHeader(text = "Constraints and Offset")

        val density = LocalDensity.current
        val containerWidth = with(density) {
            800f.toDp()
        }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .width(containerWidth)
                .fillMaxHeight()
                .background(Color(0xffFBE9E7))

        ) {

            StyleableTutorialText(
                text = "Custom padding modifiers use **Constraints.offset** and/or " +
                        "**Constraints.constrainWidth** to set usable area by any Composable. " +
                        "When offset or constraints are not used Composable " +
                        "can overflow from its parent.",
                bullets = false
            )
            CustomPaddingSamples(message)
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = message,
            label = { Text("Main") },
            placeholder = { Text("Set text to change main width") },
            onValueChange = { newValue: String ->
                message = newValue
            }
        )
    }
}

@Composable
private fun CustomPaddingSamples(message: String) {

    val density = LocalDensity.current
    val padding = with(density) {
        50f.toDp()
    }

    SideEffect {
        println("🍎 CustomPaddingSamples() composing\n" +
                "message: $message")
    }

    TutorialText2(text = "paddingNoOffsetNoConstrain")
    Column(
        modifier = Modifier
            .background(composableBackgroundColor)
            .paddingNoOffsetNoConstrain(padding)
    ) {
        Text(
            text = message, color = Color.White,
            modifier = Modifier.background(textBackgroundColor)
        )
    }

    TutorialText2(text = "paddingWithConstrainOnly")
    Column(
        modifier = Modifier
            .background(composableBackgroundColor)
            .paddingWithConstrainOnly(padding)
    ) {
        Text(
            text = message, color = Color.White,
            modifier = Modifier.background(textBackgroundColor)
        )
    }

    TutorialText2(text = "paddingWithOffsetOnly")
    Column(
        modifier = Modifier
            .background(composableBackgroundColor)
            .paddingWithOffsetOnly(padding)
    ) {
        Text(
            text = message, color = Color.White,
            modifier = Modifier.background(textBackgroundColor)
        )
    }

    TutorialText2(text = "paddingWithOffsetAndConstrain")
    Column(
        modifier = Modifier
            .background(composableBackgroundColor)
            .paddingWithOffsetAndConstrain(padding)
    ) {
        Text(
            text = message, color = Color.White,
            modifier = Modifier.background(textBackgroundColor)
        )
    }
}
