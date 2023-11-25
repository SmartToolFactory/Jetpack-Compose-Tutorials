package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial3_7Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(10.dp)
    ) {

        // 🔥 Comment out and observe each example separately to see in which order
        // Composition, measurement and placement in Layout phases happen

        TutorialHeader(text = "Layout&SubcomposeLayout Phases")
        StyleableTutorialText(
            text = "In this example how Composition and Layout phases occur for a Composable " +
                    "structure. Check out logs to see that in which order Composition and " +
                    "Layout phases are called for Composables",
            bullets = false
        )
        LayoutPhasesSample1()
        StyleableTutorialText(
            text = "This is similar to example above with more complicate three structure",
            bullets = false
        )
        LayoutPhasesSample2()
        StyleableTutorialText(
            text = "In this example **SubcomposeLayout** **Composition** and **Layout** phases " +
                    "are logged.",
            bullets = false
        )
        SubcomposeLayoutPhasesSample()
    }
}

@Preview
@Composable
private fun LayoutPhasesSample1() {

    /*

            Parent Layout
             /         \
            /           \
           /             \
          /               \
    Child1 Outer         Child2
          |
      Child1 Inner

        Prints:
        I  Parent Scope
        I  Child1 Outer Scope
        I  Child1 Inner Scope
        I  Child2 Scope
        I  🍏 Child1 Inner MeasureScope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
        I  contentHeight: 52, layoutHeight: 52
        I  🍏 Child1 Outer MeasureScope minHeight: 275, maxHeight: 275, minWidth: 0, maxWidth: 1080
        I  contentHeight: 104, layoutHeight: 275
        I  🍏 Child2 MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
        I  contentHeight: 52, layoutHeight: 52
        I  🍏 Parent MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 1080, maxWidth: 1080
        I  contentHeight: 327, layoutHeight: 327
        I  🍏🍏 Parent Placement Scope
        I  🍏🍏 Child1 Outer Placement Scope
        I  🍏🍏 Child1 Inner Placement Scope
        I  🍏🍏 Child2 Placement Scope

     */

    var text by remember {
        mutableStateOf("Type Text")
    }

    Column {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = { text = it }
        )

        CustomLayout(
            modifier = Modifier
                .fillMaxWidth()
                // 🔥🔥 Observe how it changes with Intrinsic sizes when none is set by
                // Custom Layouts
//            .width(IntrinsicSize.Max)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Parent"
        ) {
            println("Parent Scope")
            // label is for logging, they are not part of real custom
            // layouts
            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child1 Outer"
            ) {
                println("Child1 Outer Scope")
                Text("Child1 Outer Content $text")
//            CustomLayout(
//                modifier = Modifier
//                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
//                    .background(getRandomColor()),
//                label = "Child1 Inner"
//            ) {
//                println("Child1 Inner Scope")
//                Text("Child1 Inner Content")
//            }
            }

            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child2"
            ) {
                println("Child2 Scope")
                Text("Child2 Content $text")
            }
        }
    }
}

@Preview
@Composable
private fun LayoutPhasesSample2() {
    /*

                Parent Composable
                 /      |     \
                /       |      \
               /        |       \
              /         |        \
             /          |         \
         C1 Outer     C2Outer    C3outer
           /            |            \
          /            / \            \
         /            /   \            \
        /            /     \            \
    C1 Middle   C2 InnerA C2 InnerB    C3 Inner
        |
    C1 Inner

     Prints:

    I  Child1 Outer Scope
    I  Child1 Middle Scope
    I  Child1 Inner Scope
    I  Child2 Outer Scope
    I  Child2 InnerA Scope
    I  Child2 InnerB Scope
    I  Child3 Outer Scope
    I  Child3 Inner Scope

    I  🍏 Child1 Inner MeasureScope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child1 Middle MeasureScope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 104, layoutHeight: 104
    I  🍏 Child1 Outer MeasureScope minHeight: 275, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 156, layoutHeight: 275

    I  🍏 Child2 InnerA MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child2 InnerB MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child2 Outer MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 156, layoutHeight: 156

    I  🍏 Child3 Inner MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child3 Outer MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52

    I  🍏 Parent MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 1080, maxWidth: 1080
    I  contentHeight: 483, layoutHeight: 483
    I  🍏🍏 Parent Placement Scope
    I  🍏🍏 Child1 Outer Placement Scope
    I  🍏🍏 Child1 Middle Placement Scope
    I  🍏🍏 Child1 Inner Placement Scope
    I  🍏🍏 Child2 Outer Placement Scope
    I  🍏🍏 Child2 InnerA Placement Scope
    I  🍏🍏 Child2 InnerB Placement Scope
    I  🍏🍏 Child3 Outer Placement Scope
    I  🍏🍏 Child3 Inner Placement Scope
  */

    // label is for logging, they are not part of real custom
    // layouts
    CustomLayout(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .background(getRandomColor()),
        label = "Parent"
    ) {
        CustomLayout(
            modifier = Modifier
                .height(100.dp)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child1 Outer"
        ) {

            println("Child1 Outer Scope")
            Text("Child1 Outer Content")

            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child1 Middle"
            ) {
                println("Child1 Middle Scope")
                Text("Child1 Middle Content")


                CustomLayout(
                    modifier = Modifier
                        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                        .background(getRandomColor()),
                    label = "Child1 Inner"
                ) {
                    println("Child1 Inner Scope")

                    Text("Child1 Bottom Content")
                }
            }
        }

        CustomLayout(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child2 Outer"
        ) {
            println("Child2 Outer Scope")
            Text("Child2 Outer Content")

            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child2 InnerA"
            ) {
                println("Child2 InnerA Scope")

                Text("Child2A Inner Content")
            }

            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child2 InnerB"
            ) {
                println("Child2 InnerB Scope")
                Text("Child2B Inner Content")
            }
        }

        CustomLayout(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child3 Outer"
        ) {
            println("Child3 Outer Scope")

            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child3 Inner"
            ) {
                println("Child3 Inner Scope")
                Text("Child3 Bottom Content")
            }
        }
    }
}

@Composable
fun CustomLayout(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {

    val measurePolicy = object : MeasurePolicy {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {

            val placeables = measurables.map { measurable ->
                measurable.measure(
                    constraints.copy(minWidth = 0, minHeight = 0)
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
                "🍏 $label MeasureScope " +
                        "minHeight: ${constraints.minHeight}, " +
                        "maxHeight: ${constraints.maxHeight}, " +
                        "minWidth: ${constraints.minWidth}, " +
                        "maxWidth: ${constraints.maxWidth}\n" +
                        "contentHeight: $contentHeight, " +
                        "layoutHeight: $layoutHeight\n"
            )

            return layout(layoutWidth, layoutHeight) {
                var y = 0
                println("🍏🍏 $label Placement Scope")

                placeables.forEach { placeable: Placeable ->
                    placeable.placeRelative(0, y)
                    y += placeable.height
                }
            }
        }
    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy
    )
}

@Preview
@Composable
private fun SubcomposeLayoutPhasesSample() {

    /*

            MySubcomposeLayout
             /         \
            /           \
           /             \
          /               \
    Child1 Outer         Child2
          |
      Child1 Inner

        Prints:
        I  🌝 MySubcomposeLayout before subcompose()
        I  MySubcomposeLayout Scope
        I  Child1 Outer Scope
        I  Child1 Inner Scope
        I  Child2 Scope
        I  🍏 Child1 Inner MeasureScope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
        I  contentHeight: 52, layoutHeight: 52
        I  🍏 Child1 Outer MeasureScope minHeight: 275, maxHeight: 275, minWidth: 1080, maxWidth: 1080
        I  contentHeight: 104, layoutHeight: 275
        I  🍏 Child2 MeasureScope minHeight: 0, maxHeight: 2063, minWidth: 1080, maxWidth: 1080
        I  contentHeight: 52, layoutHeight: 52
        I  🌝🌝 MySubcomposeLayout after 1st subcompose()
        I  MySubcomposeLayout scope
        I  Child1 Outer Scope
        I  Child1 Inner Scope
        I  Child2 Scope
        I  🍏 Child1 Inner MeasureScope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 2147483647
        I  contentHeight: 52, layoutHeight: 52
        I  🍏 Child1 Outer MeasureScope minHeight: 275, maxHeight: 275, minWidth: 0, maxWidth: 2147483647
        I  contentHeight: 104, layoutHeight: 275
        I  🍏 Child2 MeasureScope minHeight: 275, maxHeight: 2063, minWidth: 0, maxWidth: 2147483647
        I  contentHeight: 52, layoutHeight: 275
        I  🌝🌝🌝 MySubcomposeLayout after 2nd subcompose()
        I  🌝🌝🌝🌝 MySubcomposeLayout Placement Scope rowSize: 597 x 275
        I  🍏🍏 Child1 Outer Placement Scope
        I  🍏🍏 Child1 Inner Placement Scope
        I  🍏🍏 Child2 Placement Scope

     */

    MySubcomposeLayout(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .background(getRandomColor())
    ) {
        println("MySubcomposeLayout Scope")
        // label is for logging, they are not part of real custom
        // layouts
        CustomLayout(
            modifier = Modifier
                .height(100.dp)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child1 Outer"
        ) {
            println("Child1 Outer Scope")
            Text("Child1 Outer Content")
            CustomLayout(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .background(getRandomColor()),
                label = "Child1 Inner"
            ) {
                println("Child1 Inner Scope")
                Text("Child1 Inner Content")
            }
        }

        CustomLayout(
            modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                .background(getRandomColor()),
            label = "Child2"
        ) {
            println("Child2 Scope")
            Text("Child2 Content")
        }
    }
}

@Composable
private fun MySubcomposeLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints: Constraints ->

        var subcomposeIndex = 0

        println(
            "🌝 MySubcomposeLayout before subcompose()"
        )

        var placeables: List<Placeable> = subcompose(subcomposeIndex++, content).map {
            it.measure(constraints)
        }


        println(
            "🌝🌝 MySubcomposeLayout after 1st subcompose()"
        )

        var rowSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = currentMax.width + placeable.width,
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        // Remeasure every element using height of tallest item using it as min height for
        // every composable
        if (placeables.isNotEmpty()) {
            placeables = subcompose(subcomposeIndex, content).map { measurable: Measurable ->
                measurable.measure(
                    Constraints(
                        minHeight = rowSize.height,
                        maxHeight = constraints.maxHeight
                    )
                )
            }

            println(
                "🌝🌝🌝 MySubcomposeLayout after 2nd subcompose()"
            )

            rowSize =
                placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                    IntSize(
                        width = currentMax.width + placeable.width,
                        height = maxOf(currentMax.height, placeable.height)
                    )
                }
        }

        layout(rowSize.width, rowSize.height) {

            println("🌝🌝🌝🌝 MySubcomposeLayout Placement Scope rowSize: $rowSize")

            var xPos = 0
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(xPos, 0)
                xPos += placeable.width
            }
        }
    }
}
