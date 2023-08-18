package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial3_2Screen0() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {

        TutorialHeader(text = "Layout Basics")

        StyleableTutorialText(
            text = "A custom layout is created using **Layout** Composable. A **MeasurePolicy** " +
                    "is assigned to define the measure and layout behavior of a Layout.\n" +
                    "Layout and MeasurePolicy are the way\n" +
                    "Compose layouts (such as `Box`, `Column`, etc.) are built,\n" +
                    "and they can also be used to achieve custom layouts.\n\n" +
                    "During the Layout phase, the tree is traversed using the following 3 step algorithm:\n" +
                    "\n" +
                    "1-) Measure children: A node measures its children, if any.\n" +
                    "2-) Decide own size: Based on those measurements, a node decides on its own size.\n" +
                    "3-) Place children: Each child node is placed relative to a node’s own position.",
            bullets = false
        )

        CustomLayoutSample1()

        StyleableTutorialText(
            text = "In this example in with which Constraints content is measured is overridden." +
                    "And Composable out of bound of min=150.dp, max=300.dp is measured in min or " +
                    "max values of this range.",
            bullets = false
        )
        CustomLayoutSample2()
    }
}

@Preview
@Composable
private fun CustomLayoutSample1() {

    /*
        Prints:
        🔥🔥 Depth-First Tree Traversal

        // COMPOSITION Phase
        I  Parent Scope

        I  Child1 Scope
        I  Box Scope

        I  Child2 Outer Scope
        I  Child2 Inner Scope

        // LAYOUT MeasureScope
        I  🍏 Child1 MeasureScope minWidth: 392.72726.dp, maxWidth: 392.72726.dp,
        minHeight: 50.18182.dp, maxHeight: 50.18182.dp
        I  contentHeight: 50.18182.dp, layoutHeight: 50.18182.dp

        I  🍏 Child2 Inner MeasureScope minWidth: 0.0.dp, maxWidth: 392.72726.dp,
        minHeight: 0.0.dp, maxHeight: 750.1818.dp
        I  contentHeight: 18.90909.dp, layoutHeight: 18.90909.dp
        I  🍏 Child2 Outer MeasureScope minWidth: 0.0.dp, maxWidth: 392.72726.dp,
        minHeight: 0.0.dp, maxHeight: 750.1818.dp
        I  contentHeight: 18.90909.dp, layoutHeight: 18.90909.dp

        I  🍏 Parent MeasureScope minWidth: 392.72726.dp, maxWidth: 392.72726.dp,
        minHeight: 0.0.dp, maxHeight: 750.1818.dp
        I  contentHeight: 69.09091.dp, layoutHeight: 69.09091.dp

        // LAYOUT Placement Scope
        I  🍎 Parent Placement Scope
        I  🍎 Child1 Placement Scope
        I  🍎 Child2 Outer Placement Scope
        I  🍎 Child2 Inner Placement Scope
     */

    // label is for logging, they are not part of real custom
    // layouts
    MyLayout(
        modifier = Modifier
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .background(getRandomColor())
            .fillMaxWidth(),
        label = "Parent"
    ) {
        println("Parent Scope")
        MyLayout(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor())
                .fillMaxWidth()
                .size(50.dp),
            label = "Child1"
        ) {
            println("Child1 Scope")

            // This Box is measured in range of min=50.dp, max=50.dp
            // because of parent size
            Box(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor())
                    .size(100.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                println("Box Scope")
                Text(text = "Box Content", color = Color.White)
            }
        }

        MyLayout(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child2 Outer"
        ) {
            println("Child2 Outer Scope")

            MyLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child2 Inner"
            ) {
                println("Child2 Inner Scope")
                Text("Child2 Bottom Content")
            }
        }
    }
}

@Preview
@Composable
private fun CustomLayoutSample2() {
    /*
        Prints:
        I  CustomConstrainLayout Scope
        I  Top BoxWithConstraints Scope
        I  Middle BoxWithConstraints Scope
        I  Bottom BoxWithConstraints Scope
        I  🚗 CustomConstrainLayout MeasureScope minWidth: 392.72726.dp, maxWidth: 392.72726.dp,
        minHeight: 750.1818.dp, maxHeight: 750.1818.dp
        I  contentHeight: 73.09091.dp, layoutHeight: 750.1818.dp
        I  🚗🚗 CustomConstrainLayout Placement Scope
     */
    CustomConstrainLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        println("CustomConstrainLayout Scope")
        BoxWithConstraints(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor())
                .width(50.dp)
        ) {
            println("Top BoxWithConstraints Scope")
            Text(text = "min: $minWidth, max: $maxWidth")
        }
        BoxWithConstraints(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor())
                .width(250.dp)
        ) {
            println("Middle BoxWithConstraints Scope")
            Text(text = "min: $minWidth, max: $maxWidth")
        }

        BoxWithConstraints(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor())
                .width(350.dp)
        ) {
            println("Bottom BoxWithConstraints Scope")
            Text(text = "min: $minWidth, max: $maxWidth")
        }
    }
}

@Composable
private fun MyLayout(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {

    // A custom layout is created using Layout Composable
    /*
       MeasurePolicy defines the measure and layout behavior of a [Layout].
       [Layout] and [MeasurePolicy] are the way
       Compose layouts (such as `Box`, `Column`, etc.) are built,
       and they can also be used to achieve custom layouts.
     */
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        /*
            During the Layout phase, the tree is traversed using the following 3 step algorithm:

            1-) Measure children: A node measures its children, if any.
            2-) Decide own size: Based on those measurements, a node decides on its own size.
            3-) Place children: Each child node is placed relative to a node’s own position.
         */

        // 🔥 1-) We measure Measurables Contents inside content lambda
        // with Constraints
        // ⚠️ Constraints are the range we measure them with depending on which
        // Size Modifier or Scroll Modifier this Layout Composable assigned with.
        // You can check out this answer to see which Size modifier returns which
        // Constraints
        // https://stackoverflow.com/questions/65779226/android-jetpack-compose-width-height-size-modifier-vs-requiredwidth-requir/73316247#73316247
        val placeables = measurables.map { measurable ->
            measurable.measure(
                // 🔥 This is for changing range min to 0, for example Modifier.width(100)
                // returns minWidth= 100.dp, maxWidth = 100.dp
                // while our content Composables(Text,Image, etc) might have
                // smaller content sizes due to their own measurements or contents.
                constraints.copy(minWidth = 0, minHeight = 0)
            )
        }

        // 2-) After measuring each child we decide how big this Layout/Composable should be
        // Let's say we want to make a Column we need to set width to max of content Composables
        // while sum of content Composables
        val contentWidth = placeables.maxOf { it.width }
        val contentHeight = placeables.sumOf { it.height }

        // 🔥🔥 We calculated total content size however in some situations with Modifiers such as
        // Modifier.fillMaxSize we need to set Layout dimensions to match parent not
        // total dimensions of Content

        val layoutWidth = if (constraints.hasBoundedWidth && constraints.hasFixedWidth) {
            constraints.maxWidth
        } else {
            contentWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        }

        val layoutHeight = if (constraints.hasBoundedHeight && constraints.hasFixedHeight) {
            constraints.maxHeight
        } else {
            contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        }

        println(
            "🍏 $label MeasureScope " +
                    "minWidth: ${constraints.minWidth.toDp()}, " +
                    "maxWidth: ${constraints.maxWidth.toDp()}, " +
                    "minHeight: ${constraints.minHeight.toDp()}, " +
                    "maxHeight: ${constraints.maxHeight.toDp()}\n" +
                    "contentHeight: ${contentHeight.toDp()}, " +
                    "layoutHeight: ${layoutHeight.toDp()}\n"
        )

        // 🔥 Layout dimensions should be in Constraints range we get from parent
        // otherwise this Layout is placed incorrectly
        layout(layoutWidth, layoutHeight) {

            // 3-) 🔥🔥 Place placeables or Composables inside content lambda accordingly
            // In this example we place like a Column vertically

            var y = 0

            println("🍎 $label Placement Scope")

            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}

@Composable
private fun CustomConstrainLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(

                // 🔥🔥 We override how Composables inside this content will be measured
                constraints.copy(
                    minWidth = 150.dp.roundToPx(),
                    maxWidth = 300.dp.roundToPx(),
                    minHeight = 0
                )
            )
        }

        val contentWidth = placeables.maxOf { it.width }
        val contentHeight = placeables.sumOf { it.height }

        val layoutWidth = if (constraints.hasBoundedWidth && constraints.hasFixedWidth) {
            constraints.maxWidth
        } else {
            contentWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        }

        val layoutHeight = if (constraints.hasBoundedHeight && constraints.hasFixedHeight) {
            constraints.maxHeight
        } else {
            contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        }

        println(
            "🚗 CustomConstrainLayout MeasureScope " +
                    "minWidth: ${constraints.minWidth.toDp()}, " +
                    "maxWidth: ${constraints.maxWidth.toDp()}, " +
                    "minHeight: ${constraints.minHeight.toDp()}, " +
                    "maxHeight: ${constraints.maxHeight.toDp()}\n" +
                    "contentHeight: ${contentHeight.toDp()}, " +
                    "layoutHeight: ${layoutHeight.toDp()}\n"
        )

        layout(layoutWidth, layoutHeight) {

            var y = 0

            println("🚗🚗 CustomConstrainLayout Placement Scope")

            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}
