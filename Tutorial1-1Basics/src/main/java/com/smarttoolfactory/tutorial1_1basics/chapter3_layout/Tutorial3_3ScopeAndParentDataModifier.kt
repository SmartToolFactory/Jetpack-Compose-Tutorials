package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Scope and ParentDataModifier")
        TutorialText(
            text = "1-) Using a scope for a Composable it's possible to add Modifier " +
                    "only available in that scope. Modifier.horizontalAlign " +
                    "is only available in CustomColumnScope."
        )

        TutorialText2(text = "Custom Column with Scope")

        CustomColumnWithScope(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.LightGray)
        ) {

            Text(
                "Align Start",
                modifier = Modifier
                    .background(Color(0xffF44336))
                    .horizontalAlign(HorizontalAlignment.Start),
                color = Color.White
            )
            Text(
                "Align Center",
                modifier = Modifier
                    .background(Color(0xff9C27B0))
                    .horizontalAlign(HorizontalAlignment.Center),
                color = Color.White
            )
            Text(
                "Align End",
                modifier = Modifier
                    .background(Color(0xff2196F3))
                    .horizontalAlign(HorizontalAlignment.End),
                color = Color.White
            )
            Text(
                "Align Start",
                modifier = Modifier
                    .background(Color(0xff8BC34A))
                    .horizontalAlign(HorizontalAlignment.Start),
                color = Color.White
            )
        }
    }
}

/*
1- Create a enum for setting horizontal alignment options
 */
enum class HorizontalAlignment {
    Start, Center, End
}

/*
2- Create a class that implements ParentDataModifier and implement functions
 */
private class CustomColumnData(
    val alignment: HorizontalAlignment
) : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?) = this@CustomColumnData


    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as CustomColumnData

        if (alignment != other.alignment) return false

        return true
    }

    override fun hashCode(): Int {
        return alignment.hashCode()
    }

    override fun toString(): String =
        "CustomColumnData(alignment=$alignment)"
}

/*
3- Create a interface for Scope that has an extension function that returns a class
that implements ParentDataModifier interface
 */
interface CustomColumnScope {

    @Stable
    fun Modifier.horizontalAlign(align: HorizontalAlignment) = this.then(
        CustomColumnData(align)
    )

    companion object : CustomColumnScope
}

/*
4- Create extension functions to set this ParentDataModifier in custom Layout using measurable
 */

private val Measurable.childData: CustomColumnData?
    get() = parentData as? CustomColumnData

private val Measurable.alignment: HorizontalAlignment
    get() = childData?.alignment ?: HorizontalAlignment.Start


@Composable
fun CustomColumnWithScope(
    modifier: Modifier = Modifier,
    content: @Composable CustomColumnScope.() -> Unit
) {

    Layout(
        modifier = modifier,
        content = { CustomColumnScope.content() },
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // We need to set minWidth to zero to wrap only placeable width
        val looseConstraints = constraints.copy(
            minWidth = 0,
            minHeight = constraints.minHeight
        )

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(looseConstraints)
        }

        // 🔥 We will use this alignment to set position of our composables
        val measurableAlignment: List<HorizontalAlignment> = measurables.map { measurable ->
            measurable.alignment
        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0

        val totalHeight: Int = placeables.map {
            it.height
        }.sum()

        val maxWidth = constraints.maxWidth

        println(
            "🤯 Constraints minWidth: ${constraints.minWidth}, " +
                    "minHeight: ${constraints.minHeight}, " +
                    "maxWidth: ${constraints.maxWidth}, " +
                    "maxHeight: ${constraints.maxHeight}"
        )

        // Set the size of the layout as big as it can
        layout(maxWidth, totalHeight) {
            // Place children in the parent layout
            placeables.forEachIndexed { index, placeable ->

                val x = when (measurableAlignment[index]) {
                    HorizontalAlignment.Start -> 0
                    HorizontalAlignment.Center -> (maxWidth - placeable.measuredWidth) / 2
                    HorizontalAlignment.End -> maxWidth - placeable.measuredWidth
                }

                // Position item on the screen
                placeable.placeRelative(x = x, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}
