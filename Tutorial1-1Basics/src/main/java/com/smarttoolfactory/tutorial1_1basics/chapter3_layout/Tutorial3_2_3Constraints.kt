package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_2Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // 🔥🔥 NOTE: hasBoundedHeight returns Constraints.Infinity when vertical scroll is
            // set.
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "Constraints determine how children of a Composable is measured, " +
                    "default Constraints use minWidth, maxWidth, minHeight, maxHeight of the " +
                    "layout based on the Modifier.\n",
            bullets = false
        )

        /*
            Logic for measurements when measuring a measurable default constraints return
            min, and max range. If we measure a measurable between these bounds it is placed
            based on this interval.

         */

        DefaultConstraintsSample()
        CustomConstraintsSample()
        CustomConstraintsSample2()
        CustomConstraintsSample3()
        CustomConstraintsSample4()
    }
}

@Composable
private fun Content() {
    Text(
        "First Text",
        modifier = Modifier
            .background(Pink400),
        color = Color.White
    )
    Text(
        "Second Text",
        modifier = Modifier
            .background(Blue400),
        color = Color.White
    )
}

@Composable
private fun DefaultConstraintsSample() {
    StyleableTutorialText(text = "1-) 🍉Create CustomColumns with default Constraints")

    TutorialText2(text = "Modifier.fillMaxWidth()")
    // 🔥🔥🔥 Unlike Column this one sets children width to max when fillMaxWidth() is set
    // because minWidth is also 1080 because of that we measure with 1080px instead of (0,1080)
    // These are for comprehending how Constraints effect parent and children dimensions

    /*
        Logs: For a device with 1080x1920 resolution and density 2.625
        🍉 CustomColumnWithDefaultConstraints() constraints:
        Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithDefaultConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.width(200.dp)")
    /*
        🍉 CustomColumnWithDefaultConstraints() constraints:
        Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
     */
    CustomColumnWithDefaultConstraints(
        modifier = Modifier
            .width(200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(min=200.dp)")
    /*
        🍉 CustomColumnWithDefaultConstraints() constraints:
        Constraints(minWidth = 525, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithDefaultConstraints(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(max= 200.dp)")

    /*
        🍉 CustomColumnWithDefaultConstraints() constraints:
        Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithDefaultConstraints(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.wrapContentSize()")
    /*
        🍉 CustomColumnWithDefaultConstraints() constraints:
        Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithDefaultConstraints(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, Green400)
    ) { Content() }
}

@Composable
private fun CustomConstraintsSample() {
    StyleableTutorialText(
        text = "2-) 🎃 Create CustomColumns with Constraints with " +
                "**minWidth = constraints.maxWidth, maxWidth = constraints.maxWidth**. " +
                "Forces composables to be measured with max width from **Constraints**"
    )

    TutorialText2(text = "Modifier.fillMaxWidth()")
    /*
        🎃 CustomColumnWithCustomConstraints()
        constraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.width(200.dp)")
    /*
        🎃 CustomColumnWithCustomConstraints()
        constraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints(
        modifier = Modifier
            .width(200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(min=200.dp)")

    /*
        🎃 CustomColumnWithCustomConstraints()
        constraints: Constraints(minWidth = 525, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints(
        modifier = Modifier

            .widthIn(min = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(max= 200.dp)")
    /*
        🎃 CustomColumnWithCustomConstraints()
        constraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.wrapContentSize()")

    /*
        🎃 CustomColumnWithCustomConstraints()
        constraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, Green400)
    ) { Content() }
}

@Composable
private fun CustomConstraintsSample2() {
    StyleableTutorialText(
        text = "3-) 🍋 Create CustomColumns with Constraints " +
                "Measure with **Constraints.fixedWidth(), 250.dp** is used. " +
                "If Constraints we measure with are not in bounds of " +
                "default Constraints, composable " +
                "is placed at position (Constraints.maxWidth-layout width)/2"
    )

    TutorialText2(text = "Modifier.fillMaxWidth()")

    /*
        🍋 CustomColumnWithCustomConstraints2()
        constraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 656, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 656, maxWidth: 656, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints2(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.width(200.dp)")

    /*
        🍋 CustomColumnWithCustomConstraints2()
        constraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 656, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 656, maxWidth: 656, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints2(
        modifier = Modifier
            .width(200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(min= 200.dp)")

    /*
        🍋 CustomColumnWithCustomConstraints2()
        constraints: Constraints(minWidth = 525, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 656, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 656, maxWidth: 656, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints2(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(max= 200.dp)")
    /*
        🍋 CustomColumnWithCustomConstraints2()
        constraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 656, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 656, maxWidth: 656, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints2(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.wrapContentSize()")
    /*
        🍋 CustomColumnWithCustomConstraints2()
        constraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 656, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 656, maxWidth: 656, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints2(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, Green400)
    ) { Content() }
}

@Composable
private fun CustomConstraintsSample3() {
    StyleableTutorialText(
        text = "4-) 🍏 Create CustomColumns with **Constraints minWidth = 0**"
    )

    TutorialText2(text = "Modifier.fillMaxWidth()")

    /*
        🍏 CustomColumnWithCustomConstraints3()
        constraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints3(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.width(200.dp)")

    /*
        🍏 CustomColumnWithCustomConstraints3()
        constraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints3(
        modifier = Modifier
            .width(200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(min= 200.dp)")

    /*
        🍏 CustomColumnWithCustomConstraints3()
        constraints: Constraints(minWidth = 525, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints3(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(max= 200.dp)")

    /*
        🍏 CustomColumnWithCustomConstraints3()
        constraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints3(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.wrapContentSize()")

    /*
        🍏 CustomColumnWithCustomConstraints3()
        constraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints3(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, Green400)
    ) { Content() }
}

@Composable
private fun CustomConstraintsSample4() {
    StyleableTutorialText(
        text = "5-) 🌽 Create CustomColumns with **Constraints** " +
                "** minWidth = 150.dp**, and **maxWidth=250.dp**. Child composables can be " +
                "set at least 150.dp width because of measuring with 150.dp min, max width depends" +
                "on content.\n" +
                "If Constraints we measure with are not in bounds of " +
                "default Constraints composable " +
                "is placed at position (Constraints.maxWidth-layout width)/2"
    )

    TutorialText2(text = "Modifier.fillMaxWidth()")

    /*
        🌽 CustomColumnWithCustomConstraints4()
        constraints: Constraints(minWidth = 1080, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 394, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 394, maxWidth: 656, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */

        CustomColumnWithCustomConstraints4(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Green400)
        ) { Content() }

    TutorialText2(text = "Modifier.width(200.dp)")

    /*
        🌽 CustomColumnWithCustomConstraints4()
        constraints: Constraints(minWidth = 525, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 394, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 394, maxWidth: 656, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */

        CustomColumnWithCustomConstraints4(
            modifier = Modifier
                .width(200.dp)
                .border(2.dp, Green400)
        ) { Content() }

    TutorialText2(text = "Modifier.widthIn(min= 200.dp)")

    /*
        🌽 CustomColumnWithCustomConstraints4()
        constraints: Constraints(minWidth = 525, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 394, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 394, maxWidth: 656, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints4(
        modifier = Modifier
            .widthIn(min = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.widthIn(max= 200.dp)")

    /*
        🌽 CustomColumnWithCustomConstraints4()
        constraints: Constraints(minWidth = 0, maxWidth = 525, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 394, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 525
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 525, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 394, maxWidth: 656, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints4(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .border(2.dp, Green400)
    ) { Content() }

    TutorialText2(text = "Modifier.wrapContentSize()")


    /*
        🌽 CustomColumnWithCustomConstraints4()
        constraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = Infinity)
        updatedConstraints: Constraints(minWidth = 394, maxWidth = 656, minHeight = 0, maxHeight = Infinity)
        🔥 CustomColumn Constraints ACTUAL WIDTH 1080
        ⚠️ ORIGINAL CONSTRAINTS: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
        ⚠️⚠️️️ Updated CONSTRAINTS: minWidth 394, maxWidth: 656, boundedWidth: true, fixedWidth: false
        minHeight: 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
     */
    CustomColumnWithCustomConstraints4(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, Green400)
    ) { Content() }
}

@Composable
private fun CustomColumnWithDefaultConstraints(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->
        println("🍉 CustomColumnWithDefaultConstraints() constraints: $constraints")
        createCustomColumnLayout(measurables, constraints, constraints)
    }
}

/**
 * Measure with minWidth = constraints.maxWidth, and maxWidth = constraints.maxWidth
 */
@Composable
private fun CustomColumnWithCustomConstraints(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints =
            constraints.copy(
                minWidth = constraints.maxWidth,
                maxWidth = constraints.maxWidth
            )

        println(
            "🎃 CustomColumnWithCustomConstraints()\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints"
        )

        createCustomColumnLayout(measurables, constraints, updatedConstraints)
    }
}

/**
 * Measure with Constraints.fixedWidth(), 250.dp is used for demonstration.
 * If Constraints we measure with are not in bounds of
 * default Constraints composable
 * is placed at position (Constraints.maxWidth-layout width)/2"
 */
@Composable
private fun CustomColumnWithCustomConstraints2(
    modifier: Modifier,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val widthInDp = with(density) {
        250.dp.roundToPx()
    }
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints = Constraints.fixedWidth(widthInDp)

        println(
            "🍋 CustomColumnWithCustomConstraints2()\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints"
        )
        createCustomColumnLayout(measurables, constraints, updatedConstraints)
    }
}

/**
 * Measure with minWidth = 0
 * If Constraints we measure with are not in bounds of
 * default Constraints composable
 * is placed at position (Constraints.maxWidth-layout width)/2"
 */
@Composable
private fun CustomColumnWithCustomConstraints3(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints = constraints.copy(minWidth = 0)

        println(
            "🍏 CustomColumnWithCustomConstraints3()\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints"
        )
        createCustomColumnLayout(measurables, constraints, updatedConstraints)
    }
}

/**
 * Measure with minWidth = 150.dp, maxWidth = widthInDp, 250.dp is used for demonstration.
 * If Constraints we measure with are not in bounds of
 * default Constraints composable
 * is placed at position (Constraints.maxWidth-layout width)/2"
 */
@Composable
private fun CustomColumnWithCustomConstraints4(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current

    val minWidth = with(density) {
        150.dp.roundToPx()
    }

    val maxWidth = with(density) {
        250.dp.roundToPx()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints =
            constraints.copy(
                minWidth = minWidth,
                maxWidth = maxWidth
            )

        println(
            "🌽 CustomColumnWithCustomConstraints4()\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints"
        )
        createCustomColumnLayout(measurables, constraints, updatedConstraints)
    }
}

/**
 * Sample function to create Column layout to not repeat for each example with different Constraints
 */
private fun MeasureScope.createCustomColumnLayout(
    measurables: List<Measurable>,
    constraints: Constraints,
    updatedConstraints: Constraints
): MeasureResult {
    //
    val placeables = measurables.map { measurable ->
        // Measure each child
        measurable.measure(updatedConstraints)
    }

    // Track the y co-ord we have placed children up to
    var yPosition = 0

    val totalHeight: Int = placeables.sumOf {
        it.height
    }

    // 🔥 This can be sum or longest of Composable widths, or maxWidth of Constraints
    // Change this to
//    val contentWidth: Int = placeables.maxOf {
//        it.width
//    }

    // 🔥 Uncomment to see how it changes layout
//    val contentWidth: Int = placeables.sumOf {
//        it.width
//    }

    // 🔥 Uncomment to see how it changes layout
    val contentWidth = constraints.maxWidth

    // 🔥 Uncomment to see how it changes layout
//    val contentWidth = updatedConstraints.maxWidth

    println("🔥 CustomColumn Constraints ACTUAL WIDTH $contentWidth\n")
    println(
        "⚠️ ORIGINAL CONSTRAINTS: minWidth ${constraints.minWidth}, " +
                "maxWidth: ${constraints.maxWidth}, " +
                "boundedWidth: ${constraints.hasBoundedWidth}, " +
                "fixedWidth: ${constraints.hasFixedWidth}\n" +
                "minHeight: ${constraints.minHeight}, " +
                "maxHeight: ${constraints.maxHeight}, " +
                "hasBoundedHeight: ${constraints.hasBoundedHeight}, " +
                "hasFixedHeight: ${constraints.hasFixedHeight}\n"
    )
    println(
        "⚠️⚠️️️ Updated CONSTRAINTS: minWidth ${updatedConstraints.minWidth}, " +
                "maxWidth: ${updatedConstraints.maxWidth}, " +
                "boundedWidth: ${updatedConstraints.hasBoundedWidth}, " +
                "fixedWidth: ${updatedConstraints.hasFixedWidth}\n" +
                "minHeight: ${updatedConstraints.minHeight}, " +
                "maxHeight: ${updatedConstraints.maxHeight}, " +
                "hasBoundedHeight: ${updatedConstraints.hasBoundedHeight}, " +
                "hasFixedHeight: ${updatedConstraints.hasFixedHeight}\n"
    )


    // Set the size of the layout as big as it can
    return layout(contentWidth, totalHeight) {
        // Place children in the parent layout
        placeables.forEach { placeable ->

            // Position item on the screen
            placeable.placeRelative(x = 0, y = yPosition)

            // Record the y co-ord placed up to
            yPosition += placeable.height
        }
    }
}