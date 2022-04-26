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
            .verticalScroll(rememberScrollState())
    ) {


        StyleableTutorialText(
            text = "  Constraints determine how children of a Composable is measured, " +
                    "default Constraints use minWidth, maxWidth, minHeight, maxHeight of the " +
                    "layout based on the Modifier.\n",
            bullets = false
        )
        StyleableTutorialText(text = "1-) Create CustomColumns with default Constraints")

        TutorialText2(text = "Modifier.fillMaxWidth()")
        // 🔥🔥 NOTE: hasBoundedHeight returns Constraints.Infinity when vertical scroll is
        // set.
        /*
            Logs: This is for my device with 1080x1920 resolution
            I: 🔥 CustomColumn Constraints
            I: minWidth 1080, maxWidth: 1080, boundedWidth: true, fixedWidth: true
            I: minHeight 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
         */
        CustomColumn(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.width(200.dp)")
        /*
            I: 🔥 CustomColumn Constraints
            I: minWidth 525, maxWidth: 525, boundedWidth: true, fixedWidth: true
            I: minHeight 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
         */
        CustomColumn(
            modifier = Modifier
                .width(200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(min=200.dp)")
        /*
            I: 🔥 CustomColumn Constraints
            I: minWidth 525, maxWidth: 1080, boundedWidth: true, fixedWidth: false
            I: minHeight 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
         */
        CustomColumn(
            modifier = Modifier
                .widthIn(min = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(max= 200.dp)")
        CustomColumn(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.wrapContentSize()")
        /*
            I: 🔥 CustomColumn Constraints
            I: minWidth 0, maxWidth: 1080, boundedWidth: true, fixedWidth: false
            I: minHeight 0, maxHeight: 2147483647, hasBoundedHeight: false, hasFixedHeight: false
         */
        CustomColumn(
            modifier = Modifier
                .wrapContentSize()
                .border(2.dp, Green400)
        ) { Content() }

        StyleableTutorialText(text = "2-) Create CustomColumns with Constraints with " +
                "minWidth = constraints.maxWidth, maxWidth = constraints.maxWidth")
        CustomColumnWithCustomConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.width(200.dp)")
        CustomColumnWithCustomConstraints(
            modifier = Modifier
                .width(200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(min=200.dp)")
        CustomColumnWithCustomConstraints(
            modifier = Modifier
                .widthIn(min = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(max= 200.dp)")
        CustomColumnWithCustomConstraints(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.wrapContentSize()")
        CustomColumnWithCustomConstraints(
            modifier = Modifier
                .wrapContentSize()
                .border(2.dp, Green400)
        ) { Content() }


        StyleableTutorialText(
            text = "3-) Create CustomColumns with Constraints " +
                    "minWidth = constraints.minWidth, maxWidth = constraints.maxWidth"
        )

        TutorialText2(text = "Modifier.fillMaxWidth()")
        CustomColumnWithCustomConstraints2(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.width(200.dp)")
        CustomColumnWithCustomConstraints2(
            modifier = Modifier
                .width(200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(min= 200.dp)")
        CustomColumnWithCustomConstraints2(
            modifier = Modifier
                .widthIn(min = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.widthIn(max= 200.dp)")
        CustomColumnWithCustomConstraints2(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .border(2.dp, Green400)
        ) { Content() }

        TutorialText2(text = "Modifier.wrapContentSize()")
        CustomColumnWithCustomConstraints2(
            modifier = Modifier
                .wrapContentSize()
                .border(2.dp, Green400)
        ) { Content() }

    }
}

@Composable
private fun Content() {
    Text(
        "Second Text",
        modifier = Modifier
            .background(Pink400),
        color = Color.White
    )
    Text(
        "Third Text",
        modifier = Modifier
            .background(Blue400),
        color = Color.White
    )
}

@Composable
private fun CustomColumn(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->
        createCustomColumnLayout(measurables, constraints)
    }
}

@Composable
private fun CustomColumnWithCustomConstraints(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val looseConstraints =
            constraints.copy(minWidth = constraints.maxWidth, maxWidth = constraints.maxWidth)
        createCustomColumnLayout(measurables, looseConstraints)
    }
}

@Composable
private fun CustomColumnWithCustomConstraints2(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val looseConstraints =
            constraints.copy(
                minWidth = constraints.minWidth, maxWidth = constraints.maxWidth
            )
        createCustomColumnLayout(measurables, looseConstraints)
    }
}

/**
 * Sample function to create Column layout to not repeat for each example with different Constraints
 */
private fun MeasureScope.createCustomColumnLayout(
    measurables: List<Measurable>,
    constraints: Constraints
): MeasureResult {
    //
    val placeables = measurables.map { measurable ->
        // Measure each child
        measurable.measure(constraints)
    }
    println(
        "🔥 CustomColumn Constraints\n" +
                "minWidth ${constraints.minWidth}, maxWidth: ${constraints.maxWidth}, " +
                "boundedWidth: ${constraints.hasBoundedWidth}, fixedWidth: ${constraints.hasFixedWidth}\n" +
                "minHeight ${constraints.minHeight}, maxHeight: ${constraints.maxHeight}, " +
                "hasBoundedHeight: ${constraints.hasBoundedHeight}, hasFixedHeight: ${constraints.hasFixedHeight}\n"
    )
    // Track the y co-ord we have placed children up to
    var yPosition = 0

    val totalHeight: Int = placeables.sumOf {
        it.height
    }

    // 🔥 This can be sum or longest of Composable widths, or maxWidth of Constraints
    // Change this to
    val maxWidth: Int = placeables.maxOf {
        it.width
    }

    // 🔥 Uncomment to see how it changes layout
//    val maxWidth: Int = placeables.sumOf {
//        it.width
//    }

    // 🔥 Uncomment to see how it changes layout
//    val maxWidth = constraints.maxWidth

    // Set the size of the layout as big as it can
    return layout(maxWidth, totalHeight) {
        // Place children in the parent layout
        placeables.forEach { placeable ->

            // Position item on the screen
            placeable.placeRelative(x = 0, y = yPosition)

            // Record the y co-ord placed up to
            yPosition += placeable.height
        }
    }
}