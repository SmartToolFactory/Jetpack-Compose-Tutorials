package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial3_8Screen() {
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

    }
}

@Composable
private fun MySubcomposeLayout(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints: Constraints ->

        var subcomposeIndex = 0

        var placeables: List<Placeable> = subcompose(subcomposeIndex++, content).map {
            it.measure(constraints)
        }

        println(
            "🌝 SubcomposeLayout $label measure() called, " +
                    "minHeight: ${constraints.minHeight}, " +
                    "maxHeight: ${constraints.maxHeight}\n"
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

            rowSize =
                placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                    IntSize(
                        width = currentMax.width + placeable.width,
                        height = maxOf(currentMax.height, placeable.height)
                    )
                }
        }

        layout(rowSize.width, rowSize.height) {

            println("🌻 SubcomposeLayout $label layout() called!!!")

            var xPos = 0
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(xPos, 0)
                xPos += placeable.width
            }
        }
    }
}

@Preview
@Composable
private fun LayoutPhasesSample() {
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

    I  🍏 Child1 Inner Measurement Scope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child1 Middle Measurement Scope minHeight: 0, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 104, layoutHeight: 104
    I  🍏 Child1 Outer Measurement Scope minHeight: 275, maxHeight: 275, minWidth: 0, maxWidth: 1080
    I  contentHeight: 156, layoutHeight: 275

    I  🍏 Child2 InnerA Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child2 InnerB Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child2 Outer Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 156, layoutHeight: 156

    I  🍏 Child3 Inner Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52
    I  🍏 Child3 Outer Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 0, maxWidth: 1080
    I  contentHeight: 52, layoutHeight: 52

    I  🍏 Parent Measurement Scope minHeight: 0, maxHeight: 2063, minWidth: 1080, maxWidth: 1080
    I  contentHeight: 483, layoutHeight: 483
    I  🍎 Parent Placement Scope
    I  🍎 Child1 Outer Placement Scope
    I  🍎 Child1 Middle Placement Scope
    I  🍎 Child1 Inner Placement Scope
    I  🍎 Child2 Outer Placement Scope
    I  🍎 Child2 InnerA Placement Scope
    I  🍎 Child2 InnerB Placement Scope
    I  🍎 Child3 Outer Placement Scope
    I  🍎 Child3 Inner Placement Scope
  */

    // label is for logging, they are not part of real custom
    // layouts
    MyLayout(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor()),
        label = "Parent"
    ) {
        MyLayout(
            modifier = Modifier
                .height(100.dp)
                .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                .background(getRandomColor()),
            label = "Child1 Outer"
        ) {

            println("Child1 Outer Scope")
            Text("Child1 Outer Content")

            MyLayout(
                modifier = Modifier
                    .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                    .background(getRandomColor()),
                label = "Child1 Middle"
            ) {
                println("Child1 Middle Scope")
                Text("Child1 Middle Content")


                MyLayout(
                    modifier = Modifier
                        .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                        .background(getRandomColor()),
                    label = "Child1 Inner"
                ) {
                    println("Child1 Inner Scope")

                    Text("Child1 Bottom Content")
                }
            }
        }

        MyLayout(
            modifier = Modifier
                .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                .background(getRandomColor()),
            label = "Child2 Outer"
        ) {
            println("Child2 Outer Scope")
            Text("Child2 Outer Content")

            MyLayout(
                modifier = Modifier
                    .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                    .background(getRandomColor()),
                label = "Child2 InnerA"
            ) {
                println("Child2 InnerA Scope")

                Text("Child2A Inner Content")
            }

            MyLayout(
                modifier = Modifier
                    .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                    .background(getRandomColor()),
                label = "Child2 InnerB"
            ) {
                println("Child2 InnerB Scope")
                Text("Child2B Bottom Content")
            }
        }

        MyLayout(
            modifier = Modifier
                .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
                .background(getRandomColor()),
            label = "Child3 Outer"
        ) {
            println("Child3 Outer Scope")

            MyLayout(
                modifier = Modifier.border(3.dp, BlueGrey400),
                label = "Child3 Inner"
            ) {
                println("Child3 Inner Scope")
                Text("Child3 Bottom Content")
            }
        }

    }
}

@Composable
private fun MyLayout(
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
                "🍏 $label Measurement Scope " +
                        "minHeight: ${constraints.minHeight}, " +
                        "maxHeight: ${constraints.maxHeight}, " +
                        "minWidth: ${constraints.minWidth}, " +
                        "maxWidth: ${constraints.maxWidth}\n" +
                        "contentHeight: $contentHeight, " +
                        "layoutHeight: $layoutHeight\n"
            )

            return layout(layoutWidth, layoutHeight) {
                var y = 0
                println("🍎 $label Placement Scope")

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