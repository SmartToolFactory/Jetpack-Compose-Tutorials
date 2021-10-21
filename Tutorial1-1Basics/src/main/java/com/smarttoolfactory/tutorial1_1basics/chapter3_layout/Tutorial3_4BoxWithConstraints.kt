package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "BoxWithConstraints")
        StyleableTutorialText(
            text = "1-) **BoxWithConstraints** is composable that defines its own content according " +
                    "to the available space, based on the incoming " +
                    "constraints or the current LayoutDirection."
        )

        TutorialText2(text = "BoxWithConstraints to divide available space")
        BoxWithConstraintsSample(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        TutorialText2(text = "BoxWithConstraints to change layout based on height")

        BoxWithConstraintsSample2(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        BoxWithConstraintsSample2(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
    }
}

@Composable
private fun BoxWithConstraintsSample(modifier: Modifier = Modifier) {

    BoxWithConstraints(modifier.background(Color.LightGray)) {

        val constraints = this.constraints
        val density: Density = LocalDensity.current

        val densityValue = density.density


        val dpValue: Dp = with(density) {
            (constraints.maxHeight * 2 / 3f).toDp()
        }

        Column {
            Text(
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .height(dpValue),
                text = "minWidth: ${constraints.minWidth}, maxWidth: ${constraints.maxWidth}, " +
                        "minHeight: ${constraints.minHeight}, maxHeight: ${constraints.maxHeight}, " +
                        "densityValue: $densityValue\n" +
                        "Covers 2/3 of the available height"
            )

            val bottomHeight: Dp = with(density) {
                (constraints.maxHeight * 1 / 3f).toDp()
            }

            Text(
                text = "Covers 1/3 of the available height",
                modifier = Modifier
                    .background(Color(0xFFFFA000))
                    .fillMaxWidth()
                    .height(bottomHeight)
            )
        }

    }
}

@Composable
private fun BoxWithConstraintsSample2(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.background(Color.LightGray)) {

        val constraints = this.constraints
        val density: Density = LocalDensity.current

        val maxHeightInDp: Dp = with(density) {
            constraints.maxHeight.toDp()
        }

        var selected by remember { mutableStateOf(true) }

        if (maxHeightInDp > 100.dp) {
            Row(modifier = Modifier.padding(8.dp)) {

                RadioButton(selected = selected, onClick = { selected = !selected })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This box Height is greater than 100.dp",
                    modifier = Modifier.background(Color(0xFF8BC34A))
                )
            }
        } else {
            Row(modifier = Modifier.padding(8.dp)) {

                Switch(checked = selected, onCheckedChange = { selected = !selected })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This box Height is smaller or equal to 100.dp",
                    modifier = Modifier.background(Color(0xFFFFA000))
                )
            }
        }
    }


}