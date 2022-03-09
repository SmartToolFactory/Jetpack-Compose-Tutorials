package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_2Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        TutorialHeader(text = "Custom Layout")
        StyleableTutorialText(
            text = "1-) Custom layouts can use an object that implements **MeasurePolicy** interface."
        )

        TutorialText2(text = "Custom Column")

        // This layout that will occupy twice as much space as its children,
        // and will position them to be bottom right aligned.
        CustomLayout(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.DarkGray)
        ) {
            Column(modifier = Modifier.background(Color.LightGray)) {
                Text(
                    "First Text",
                    modifier = Modifier
                        .background(Color(0xffF44336)),
                    color = Color.White
                )
                Text(
                    "Second Text",
                    modifier = Modifier
                        .background(Color(0xff9C27B0)),
                    color = Color.White
                )
                Text(
                    "Third Text",
                    modifier = Modifier
                        .background(Color(0xff2196F3)),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CustomLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    // We build a layout that will occupy twice as much space as its children,
    // and will position them to be bottom right aligned.
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        // measurables contains one element corresponding to each of our layout children.
        // constraints are the constraints that our parent is currently measuring us with.
        val childConstraints = Constraints(
            minWidth = constraints.minWidth / 2,
            minHeight = constraints.minHeight / 2,
            maxWidth = if (constraints.hasBoundedWidth) {
                constraints.maxWidth / 2
            } else {
                Constraints.Infinity
            },
            maxHeight = if (constraints.hasBoundedHeight) {
                constraints.maxHeight / 2
            } else {
                Constraints.Infinity
            }
        )
        // We measure the children with half our constraints, to ensure we can be double
        // the size of the children.
        val placeables = measurables.map { it.measure(childConstraints) }
        val layoutWidth = (placeables.maxByOrNull { it.width }?.width ?: 0) * 2
        val layoutHeight = (placeables.maxByOrNull { it.height }?.height ?: 0) * 2
        // We call layout to set the size of the current layout and to provide the positioning
        // of the children. The children are placed relative to the current layout place.
        layout(layoutWidth, layoutHeight) {
            placeables.forEach {
                it.placeRelative(layoutWidth - it.width, layoutHeight - it.height)
            }
        }
    }
}

@Composable
private fun CustomLayoutWithMeasurePolicy(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
// We build a layout that will occupy twice as much space as its children,
// and will position them to be bottom right aligned.
    val measurePolicy = object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            // measurables contains one element corresponding to each of our layout children.
            // constraints are the constraints that our parent is currently measuring us with.
            val childConstraints = Constraints(
                minWidth = constraints.minWidth / 2,
                minHeight = constraints.minHeight / 2,
                maxWidth = if (constraints.hasBoundedWidth) {
                    constraints.maxWidth / 2
                } else {
                    Constraints.Infinity
                },
                maxHeight = if (constraints.hasBoundedHeight) {
                    constraints.maxHeight / 2
                } else {
                    Constraints.Infinity
                }
            )
            // We measure the children with half our constraints, to ensure we can be double
            // the size of the children.
            val placeables = measurables.map { it.measure(childConstraints) }
            val layoutWidth = (placeables.maxByOrNull { it.width }?.width ?: 0) * 2
            val layoutHeight = (placeables.maxByOrNull { it.height }?.height ?: 0) * 2
            // We call layout to set the size of the current layout and to provide the positioning
            // of the children. The children are placed relative to the current layout place.
            return layout(layoutWidth, layoutHeight) {
                placeables.forEach {
                    it.placeRelative(layoutWidth - it.width, layoutHeight - it.height)
                }
            }
        }

        // The min intrinsic width of this layout will be twice the largest min intrinsic
        // width of a child. Note that we call minIntrinsicWidth with h / 2 for children,
        // since we should be double the size of the children.
        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ) = (measurables.map { it.minIntrinsicWidth(height / 2) }.maxByOrNull { it } ?: 0) * 2

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ) = (measurables.map { it.minIntrinsicHeight(width / 2) }.maxByOrNull { it } ?: 0) * 2

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ) = (measurables.map { it.maxIntrinsicHeight(height / 2) }.maxByOrNull { it } ?: 0) * 2

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ) = (measurables.map { it.maxIntrinsicHeight(width / 2) }.maxByOrNull { it } ?: 0) * 2
    }

    Layout(content = content, modifier = modifier, measurePolicy = measurePolicy)
}