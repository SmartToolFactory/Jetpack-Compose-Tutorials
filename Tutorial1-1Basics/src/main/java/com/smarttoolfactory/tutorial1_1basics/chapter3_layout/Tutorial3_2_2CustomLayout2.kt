package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Brown400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Yellow400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_2Screen2() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "CustomLayout")
        StyleableTutorialText(
            text = "1-) Custom layouts can use an object that implements **MeasurePolicy** " +
                    "interface. This example uses"
        )

        TutorialText2(text = "CustomLayout with fillMaxWidth")

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

        TutorialText2(text = "CustomLayout with no width Modifier")
        CustomLayout(
            modifier = Modifier
                .padding(8.dp)
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

        StyleableTutorialText(
            text = "2-) Intrinsic dimensions can be used to set dimensions like placeholders" +
                    "which a Composable recursively checks children to find suitable one. Even" +
                    "if this is a column is laid out with total height of its children " +
                    "setting fixed(this is for demonstration) min and max intrinsic heights " +
                    "forces to the space according to intrinsic value not actual height."
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            TutorialText2(text = "No height Modifier")
            CustomColumnWithIntrinsicDimensions(
                modifier = Modifier
                    .width(100.dp)
                    .background(Green400)
                    .padding(4.dp)
            ) {
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
            }

            TutorialText2(text = "height(IntrinsicSize.Min)")
            CustomColumnWithIntrinsicDimensions(
                modifier = Modifier
                    .width(100.dp)
                    .height(IntrinsicSize.Min)
                    .background(Yellow400)
                    .padding(4.dp)
            ) {
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
            }

            TutorialText2(text = "height(IntrinsicSize.Max)")
            CustomColumnWithIntrinsicDimensions(
                modifier = Modifier
                    .width(100.dp)
                    .height(IntrinsicSize.Max)
                    .background(Blue400)
                    .padding(4.dp)
            ) {
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
            }

            TutorialText2(text = "height(IntrinsicSize.Min) siblings")

            // 🔥🔥 Height is determined by biggest Intrinsic.Min provided by Layouts
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .border(1.dp, Color.Red)
            ) {

                CustomColumnWithIntrinsicDimensions(
                    modifier = Modifier
                        // This effects height of this composable
                        // Parent height comes from the one in Layout by comparing
                        // it with other Composable's intrinsic height
                        // Even without this parent will have same height
                        .height(IntrinsicSize.Min)
                        .width(100.dp)
                        .background(Blue400)
                        .padding(4.dp)
                ) {
                    Text(
                        "First Text",
                        modifier = Modifier
                            .background(Color(0xffF44336)),
                        color = Color.White
                    )

                }

                Spacer(modifier = Modifier.width(20.dp))

                CustomColumnWithIntrinsicDimensions2(
                    modifier = Modifier
                        // This effects height of this composable
                        // Parent height comes from the one in Layout by comparing
                        // it with other Composable's intrinsic height
                        // Even without this parent will have same height
                        .height(IntrinsicSize.Min)
                        .width(100.dp)
                        .background(Yellow400)
                        .padding(4.dp)
                ) {
                    Text(
                        "First Text",
                        modifier = Modifier
                            .background(Color(0xffF44336)),
                        color = Color.White
                    )
                }
            }

            // 🔥🔥 Height is determined by biggest Intrinsic.Min provided by Layouts
            TutorialText2(text = "height(IntrinsicSize.Max) siblings")
            Row(
                modifier = Modifier
                    .border(1.dp, Color.Red)
                    .height(IntrinsicSize.Max)
            ) {

                CustomColumnWithIntrinsicDimensions(
                    modifier = Modifier
                        // This effects height of this composable
                        // Parent height comes from the one in Layout by comparing
                        // it with other Composable's intrinsic height
                        // Even without this parent will have same height
                        .height(IntrinsicSize.Max)
                        .width(100.dp)
                        .background(Blue400)
                        .padding(4.dp)
                ) {
                    Text(
                        "First Text",
                        modifier = Modifier
                            .border(2.dp, Green400)
                            .background(Color(0xffF44336)),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                CustomColumnWithIntrinsicDimensions2(
                    modifier = Modifier
                        // This effects height of this composable
                        // Parent height comes from the one in Layout by comparing
                        // it with other Composable's intrinsic height
                        // Even without this parent will have same height
                        .height(IntrinsicSize.Max)
                        .width(100.dp)
                        .background(Yellow400)
                        .padding(4.dp)
                ) {
                    Text(
                        "First Text",
                        modifier = Modifier
                            .border(2.dp, Brown400)
                            .background(Color(0xffF44336)),
                        color = Color.White
                    )
                }
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
    val measurePolicy = MeasurePolicy { measurables, constraints ->

        println(
            "🔥 CustomLayout Constraints\n" +
                    "minWidth ${constraints.minWidth}, " +
                    "maxWidth: ${constraints.maxWidth}, " +
                    "boundedWidth: ${constraints.hasBoundedWidth}, " +
                    "fixedWidth: ${constraints.hasFixedWidth}\n" +
                    "minHeight ${constraints.minHeight}, " +
                    "maxHeight: ${constraints.maxHeight}, " +
                    "hasBoundedHeight: ${constraints.hasBoundedHeight}, " +
                    "hasFixedHeight: ${constraints.hasFixedHeight}\n"
        )

        // measurables contain each element corresponding to each of our layout children.
        // Constraints object contains min/max bounds that our parent is currently measuring
        // child Composables with.
        val childConstraints = Constraints(
            minWidth = constraints.minWidth,
            minHeight = constraints.minHeight
        )
        // Measure children with childConstraints
        val placeables = measurables.map { it.measure(childConstraints) }

        // We set dimension of this Composable that contains child Composable to
        // double width and height of longest and tallest composables
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
    Layout(modifier = modifier, content = content, measurePolicy = measurePolicy)
}

/**
 * CustomColumn same as in previous tutorial except with Intrinsic Height that
 * overrides its children's total height with fixed values set for this example
 */
@Composable
fun CustomColumnWithIntrinsicDimensions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val measurePolicy = object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {

            val looseConstraints = constraints.copy(minHeight = 0)
            val placeables = measurables.map { measurable ->
                measurable.measure(looseConstraints)
            }

            var yPosition = 0

            val totalHeight: Int = placeables.sumOf {
                it.height
            }

            // 🔥 This can be sum or longest of Composable widths, or maxWidth of Constraints
            val maxWidth: Int = placeables.maxOf {
                it.width
            }

            return layout(maxWidth, totalHeight) {
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = yPosition)
                    yPosition += placeable.height
                }
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {

            println("🍏 minIntrinsicHeight() width: $width, measurables: ${measurables.size}")
            // 🔥 This is just sample to show usage of minIntrinsicHeight, don't set
            // static values
            return 200
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {

            println("🍎 maxIntrinsicHeight() width: $width, measurables: ${measurables.size}")

            // 🔥 This is just sample to show usage of maxIntrinsicHeight, don't set
            // static values
            return 400
        }
    }

    Layout(modifier = modifier, content = content, measurePolicy = measurePolicy)
}


/**
 * CustomColumn with smaller `minIntrinsicHeight`
 * and bigger `maxIntrinsicHeight
 */
@Composable
fun CustomColumnWithIntrinsicDimensions2(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val measurePolicy = object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {

            val looseConstraints = constraints.copy(minHeight = 0)
            val placeables = measurables.map { measurable ->
                measurable.measure(looseConstraints)
            }

            var yPosition = 0

            val totalHeight: Int = placeables.sumOf {
                it.height
            }

            // 🔥 This can be sum or longest of Composable widths, or maxWidth of Constraints
            val maxWidth: Int = placeables.maxOf {
                it.width
            }

            return layout(maxWidth, totalHeight) {
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = yPosition)
                    yPosition += placeable.height
                }
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {

            println("🚙 minIntrinsicHeight() width: $width, measurables: ${measurables.size}")
            // 🔥 This is just sample to show usage of minIntrinsicHeight, don't set
            // static values
            return 80
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {

            println("🚗 maxIntrinsicHeight() width: $width, measurables: ${measurables.size}")

            // 🔥 This is just sample to show usage of maxIntrinsicHeight, don't set
            // static values
            return 500
        }
    }

    Layout(modifier = modifier, content = content, measurePolicy = measurePolicy)
}