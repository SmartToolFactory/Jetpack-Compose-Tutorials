package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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

/*
    Refer this answer for more details
    https://stackoverflow.com/a/73316247/5457853
 */
@Preview
@Composable
fun Tutorial3_1Screen5() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "Size Modifiers")

        StyleableTutorialText(
            text = "SizeModifiers return min-max constraints range that " +
                    "these composables are measured with. " +
                    "Measurement takes into consideration these min-max range while " +
                    "measuring, for instance if min=100dp, max=200dp. Content of Composable is " +
                    "measured between 100dp and 200dp\n. " +
                    "For instance, a content with 30.dp width when measured with " +
                    "widthIn(min=100, max=200) the width it takes is 100.dp. Because of this " +
                    "sometimes it's required to change this bounds between min=0, max= parent width" +
                    "or Constraints.Infinity\n" +
                    "In this example observe which modifier returns which Constraints to " +
                    "be familiar with Constraints.",

            bullets = false
        )
        // 🔥 Not vertical scroll returns Constraints.Infinity for maxHeight
        SizeModifierConstraintsSample()
    }
}


@Composable
fun SizeModifierConstraintsSample() {
    Column(modifier = Modifier) {

        TutorialText2(text = "No Dimension Modifier")

        BoxWithConstraints(modifier = Modifier.background(Brown400)) {
            val hasBoundedWidth = constraints.hasBoundedWidth
            val hasFixedWidth = constraints.hasFixedWidth
            val minWidth = minWidth
            val maxWidth = maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = minHeight
            val maxHeight = maxHeight
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
            val minWidth = minWidth
            val maxWidth = maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = minHeight
            val maxHeight = maxHeight
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
            val minWidth = minWidth
            val maxWidth = maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = minHeight
            val maxHeight = maxHeight
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
            val minWidth = minWidth
            val maxWidth = maxWidth

            val hasBoundedHeight = constraints.hasBoundedHeight
            val hasFixedHeight = constraints.hasFixedHeight
            val minHeight = minHeight
            val maxHeight = maxHeight
            Text(
                "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                        "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                        "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                        "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
                color = Color.White
            )
        }
    }

    TutorialText2(text = "200.dp WidthIn(min) and HeightIn(min)")
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .heightIn(200.dp)
            .background(Blue400)
    ) {

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth
        val minWidth = minWidth
        val maxWidth = maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = minHeight
        val maxHeight = maxHeight
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
        val minWidth = minWidth
        val maxWidth = maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = minHeight
        val maxHeight = maxHeight
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
        val minWidth = minWidth
        val maxWidth = maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = minHeight
        val maxHeight = maxHeight
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
        val minWidth = minWidth
        val maxWidth = maxWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight
        val minHeight = minHeight
        val maxHeight = maxHeight
        Text(
            "minWidth: $minWidth, maxWidth: $maxWidth\n" +
                    "hasBoundedWidth: $hasBoundedWidth, hasFixedWidth: $hasFixedWidth\n" +
                    "minHeight: $minHeight, maxHeight: $maxHeight\n" +
                    "hasBoundedHeight: $hasBoundedHeight, hasFixedHeight: $hasFixedHeight",
            color = Color.White
        )
    }
}
