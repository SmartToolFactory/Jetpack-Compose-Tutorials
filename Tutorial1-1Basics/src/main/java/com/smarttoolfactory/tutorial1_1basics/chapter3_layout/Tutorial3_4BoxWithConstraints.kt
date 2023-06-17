package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Brown400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
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
            text = "1-) **BoxWithConstraints** is Composable that defines its own content according " +
                    "to the available space, based on the incoming " +
                    "constraints or the current LayoutDirection."
        )

        TutorialText2(text = "BoxWithConstraints to divide available space")
        BoxWithConstraintsExample(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraintsExample(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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

        Spacer(modifier = Modifier.height(8.dp))
        StyleableTutorialText(
            text = "2-) **BoxWithConstraints** can return bounded or fixed dimensions which" +
                    "change based on how Modifier dimensions are set"
        )
        BoxWithConstraintsSample3()
        Spacer(modifier = Modifier.height(8.dp))
        BoxWithConstraintsSample4()
    }
}

@Composable
private fun ConstrainsSample1() {
    TutorialText2(text = "Fixed Size")
    BoxWithConstraints(
        modifier = Modifier
            .size(100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Red)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    BoxWithConstraints(
        modifier = Modifier
            .size(100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Red)
        )
    }

    TutorialText2(text = "widthIn(min)")

    BoxWithConstraints(
        modifier = Modifier
            .widthIn(min = 100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Red)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(min = 100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Red)
        )
    }


    TutorialText2(text = "widthIn(max)")

    BoxWithConstraints(
        modifier = Modifier
            .widthIn(max = 100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Red)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(max = 100.dp)
            .border(3.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Red)
        )
    }
}

@Composable
private fun ConstrainsSample2() {

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(30.dp)
        .border(4.dp, Color.Cyan)) {

        TutorialText2(text = "Chaining size modifiers")
        Box(modifier = Modifier
            .size(50.dp)
            .background(Color.Yellow))
        Box(modifier = Modifier
            .size(50.dp)
            .size(100.dp)
            .background(Color.Red))
        Box(modifier = Modifier
            .size(50.dp)
            .requiredSizeIn(minWidth = 100.dp)
            .background(Color.Green))


        TutorialText2(text = "widthIn(max)")

        BoxWithConstraints(
            modifier = Modifier
                .width(100.dp)
                .border(3.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier
                    .requiredWidthIn(min = 20.dp, max = 50.dp)
                    .height(50.dp)
                    .background(Color.Red)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        BoxWithConstraints(
            modifier = Modifier
                .width(100.dp)
                .border(3.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier
                    .requiredWidthIn(min = 150.dp, max = 200.dp)
                    .height(50.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsExample(modifier: Modifier = Modifier) {

    BoxWithConstraints(modifier.background(Color.LightGray)) {

        // The constraints given by the parent layout in pixels.
        val constraints = this.constraints
        val density: Density = LocalDensity.current

        val densityValue = density.density

        // We can get dp value as
//        val dpValue = maxHeight

        // or like this either
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
                        "hasBoundedHeight: ${constraints.hasBoundedHeight}, " +
                        "hasBoundedHeight: ${constraints.hasBoundedWidth}, " +
                        "hasFixedWidth: ${constraints.hasFixedWidth}, " +
                        "hasFixedHeight: ${constraints.hasFixedHeight}, " +
                        "\nCovers 2/3 of the available height"
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

        // The constraints given by the parent layout in pixels.
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

@Composable
fun BoxWithConstraintsSample3() {
    Column(modifier = Modifier) {

        TutorialText2(text = "No Dimension Modifier")

        BoxWithConstraints(modifier = Modifier.background(Brown400)) {
            val hasBoundedWidth = constraints.hasBoundedWidth
            val hasFixedWidth = constraints.hasFixedWidth
            val minWidth = constraints.minWidth
            val maxWidth = constraints.maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = constraints.minHeight
            val maxHeight = constraints.maxHeight
            Text(
                "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                        "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                        "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                        "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        TutorialText2(text = "FillMaxWidth and 200.dp Height")
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Red400)
        ) {
            val hasBoundedWidth = constraints.hasBoundedWidth
            val hasFixedWidth = constraints.hasFixedWidth
            val minWidth = constraints.minWidth
            val maxWidth = constraints.maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = constraints.minHeight
            val maxHeight = constraints.maxHeight
            Text(
                "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                        "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                        "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                        "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        TutorialText2(text = "wrapContentSize()")
        BoxWithConstraints(
            modifier = Modifier
                .wrapContentSize()
                .background(Orange400)
        ) {

            val hasBoundedWidth = constraints.hasBoundedWidth
            val hasFixedWidth = constraints.hasFixedWidth
            val minWidth = constraints.minWidth
            val maxWidth = constraints.maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = constraints.minHeight
            val maxHeight = constraints.maxHeight
            Text(
                "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                        "hasBoundedWidth: $hasBoundedWidth\n" +
                        "hasFixedWidth: $hasFixedWidth\n" +
                        "minHeight: $minHeight\n" +
                        "maxHeight: $maxHeight\n" +
                        "hasBoundedHeight: $hasBoundedHeight\n" +
                        "hasFixedHeight: $hasFixedHeight",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        TutorialText2(text = "200.dp Width and Height")
        BoxWithConstraints(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .background(Green400)
        ) {

            val hasBoundedWidth = constraints.hasBoundedWidth
            val hasFixedWidth = constraints.hasFixedWidth
            val minWidth = constraints.minWidth
            val maxWidth = constraints.maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = constraints.minHeight
            val maxHeight = constraints.maxHeight
            Text(
                "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                        "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                        "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                        "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
                color = Color.White
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsSample4() {
    TutorialText2(text = "200.dp WidthIn(min) and HeightIn(min)")
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .heightIn(200.dp)
            .background(Blue400)
    ) {

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth
        val minWidth = constraints.minWidth
        val maxWidth = constraints.maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = constraints.minHeight
        val maxHeight = constraints.maxHeight
        Text(
            "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                    "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                    "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
            color = Color.White
        )
    }

    TutorialText2(text = "200.dp requiredWidth(min) and requiredHeight(min)")
    BoxWithConstraints(
        modifier = Modifier
            .requiredWidthIn(min = 200.dp)
            .requiredHeightIn(200.dp)
            .background(Pink400)
    ) {

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth
        val minWidth = constraints.minWidth
        val maxWidth = constraints.maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = constraints.minHeight
        val maxHeight = constraints.maxHeight
        Text(
            "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                    "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                    "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
            color = Color.White
        )
    }

    TutorialText2(text = "200.dp defaultMinSize()")
    BoxWithConstraints(
        modifier = Modifier
            .defaultMinSize(200.dp)
            .background(Pink400)
    ) {

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth
        val minWidth = constraints.minWidth
        val maxWidth = constraints.maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = constraints.minHeight
        val maxHeight = constraints.maxHeight
        Text(
            "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                    "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                    "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
            color = Color.White
        )
    }

    TutorialText2(text = "200.dp WidthIn(max)")
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .background(Purple400)
    ) {

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth
        val minWidth = constraints.minWidth
        val maxWidth = constraints.maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = constraints.minHeight
        val maxHeight = constraints.maxHeight
        Text(
            "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                    "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                    "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
            color = Color.White
        )
    }
}
