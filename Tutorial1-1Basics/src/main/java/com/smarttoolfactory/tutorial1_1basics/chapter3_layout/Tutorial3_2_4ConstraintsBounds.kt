package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_2Screen4() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConstraintsSample()
    }
}

@Composable
private fun ConstraintsSample() {

    val density = LocalDensity.current
    val containerWidth = with(density) {
        700f.toDp()
    }

    Column(
        modifier = Modifier
            .width(containerWidth)
            .background(Color(0xffFBE9E7))
            .fillMaxHeight()
    ) {

        StyleableTutorialText(
            text = "When layout width is not in bounds of " +
                    "**Constraints.minWidth**..**Constraints.maxWidth** " +
                    "Parent is placed at (layout width-Constraints.maxWidth)/2 or " +
                    "(layout width-Constraints.minWidth)/2\n" +
                    "Constraints used for measuring measurables determine size " +
                    "of child Composables.\n" +
                    "Setting layout width determines where parent will be positioned and which" +
                    "size it will cover",
            bullets = false
        )

        StyleableTutorialText(
            text = "1-) In this example child composables are measured with " +
                    "**constraints** which limits maxWidth to **containerWidth=700**",
        )
        MyLayout(
            modifier = Modifier.border(3.dp, Green400)
        ) { Content() }

        Spacer(modifier = Modifier.height(10.dp))

        StyleableTutorialText(
            text = "2-) In this example child composables are measured with " +
                    "**constraints.copy(minWidth = 750, maxWidth = 900)**\n" +
                    "Since child Composables' widths are bigger than container they overflow from" +
                    "parent Composable.",
        )

        MyLayout2(
            modifier = Modifier.border(3.dp, Green400)
        ) { Content() }

        Spacer(modifier = Modifier.height(10.dp))

        StyleableTutorialText(
            text = "3-) In this example child composables are measured with " +
                    "**constraints.copy(minWidth = 750, maxWidth = 900)**\n" +
                    "Since child Composables' widths are bigger than container they overflow from" +
                    "parent Composable.\n" +
                    "MyLayout3(green border) " +
                    "overflows from parent as **(Constraints.maxWidth-layout width)/2** " +
                    "maxWidth is 700px while layout width is 900px because of this **MyLayout3** " +
                    "is moved to left by 100px.",
        )

        MyLayout3(modifier = Modifier.border(3.dp, Green400)) {
            Content()
        }

        val minWidth = with(density) {
            700f.toDp()
        }

        StyleableTutorialText(
            text = "4-) In this example layout width is 400px while " +
                    "**Constraints.minWidth = 500f**, Constrains.maxWidth = 1080f\n" +
                    "MyLayout3(green border) " +
                    "overflows from parent as **(layout width-Constraints.minWidth)/2**.\n" +
                    "Also child Composable is measured with " +
                    "**constraints.copy(minWidth = 100, maxWidth = 500)**",
        )
        MyLayout4(
            modifier = Modifier
                .widthIn(min = minWidth)
                .border(3.dp, Green400)
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    color = Color.White,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .background(Pink400)
                )
            }
        }

        StyleableTutorialText(
            text = "5-) In this example min/max width of modifier in px, constraint min/max width" +
                    " in px and layout width is adjustable via sliders to observe how " +
                    "child Composables and parent Composable is laid out",
        )

        ConstraintsOffsetAndBoundsSample()
    }
}

@Composable
private fun ConstraintsOffsetAndBoundsSample() {

    var minWidth by remember { mutableStateOf(100f) }
    var maxWidth by remember { mutableStateOf(700f) }
    var constraintsMinWidth by remember { mutableStateOf(100f) }
    var constraintsMaxWidth by remember { mutableStateOf(700f) }
    var layoutWidth by remember { mutableStateOf(700f) }

    LayoutWidthWidthParams(
        minWidth = minWidth.toInt(),
        maxWidth = maxWidth.toInt(),
        constraintsMinWidth = constraintsMinWidth.toInt(),
        constraintsMaxWidth = constraintsMaxWidth.toInt(),
        layoutWidth = layoutWidth.toInt()
    ) {
        BoxWithConstraints {
            Text(
                text = "Constraints: $constraints",
                color = Color.White,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .background(Orange400)
            )
        }
    }

    TutorialText2(
        text = "Modifier min and max widths, " +
                "Original Constraints are derived from min/max width values"
    )

    SliderWithLabel(
        label = "MinWidth: ${minWidth.toInt()}",
        value = minWidth
    ) {
        if (it <= maxWidth) {
            minWidth = it
        }
    }

    SliderWithLabel(
        label = "MaxWidth: ${maxWidth.toInt()}",
        value = maxWidth
    ) {
        if (it >= minWidth) {
            maxWidth = it
        }
    }

    TutorialText2(
        text = "Width of the parent Composable. If it's out of original Constraints " +
                "parent is placed difference between layout width and (min/max) constraints width"
    )

    SliderWithLabel(
        label = "Layout Width: ${layoutWidth.toInt()}",
        value = layoutWidth
    ) {
        layoutWidth = it
    }

    TutorialText2(text = "Child composable are measured with these values")

    SliderWithLabel(
        label = "Child Constraints MinWidth: ${constraintsMinWidth.toInt()}",
        value = constraintsMinWidth
    ) {
        if (it <= constraintsMaxWidth) {
            constraintsMinWidth = it
        }
    }

    SliderWithLabel(
        label = "Child Constraints MaxWidth: ${constraintsMaxWidth.toInt()}",
        value = constraintsMaxWidth
    ) {
        if (it >= constraintsMinWidth) {
            constraintsMaxWidth = it
        }
    }
}

@Composable
private fun SliderWithLabel(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text(label)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1000f
        )
    }
}

@Composable
private fun Content() {

    val density = LocalDensity.current

    val child1Width = with(density) {
        800.toDp()
    }

    val child2Width = with(density) {
        600.toDp()
    }

    BoxWithConstraints(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .width(child1Width)
            .background(Pink400)
            .clickable {}
    ) {
        Text("constraints1: $constraints", color = Color.White)
    }

    BoxWithConstraints(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .width(child2Width)
            .background(Blue400)
            .clickable {}
    ) {
        Text("constraints2: $constraints", color = Color.White)
    }
}

@Composable
private fun MyLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(constraints)
        }

        val totalHeight = placeables.sumOf { it.height }

        var posY = 0
        layout(constraints.maxWidth, totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}

@Composable
private fun MyLayout2(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // Measure with Constraints bigger than parent has.
        val updatedConstraints = constraints.copy(minWidth = 750, maxWidth = 900)

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(updatedConstraints)
        }

        println(
            "🔥 MyLayout2\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints\n"
        )

        val totalHeight = placeables.sumOf { it.height }

        var posY = 0

        layout(constraints.maxWidth, totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}

@Composable
private fun MyLayout3(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints = constraints.copy(minWidth = 750, maxWidth = 900)

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(updatedConstraints)
        }

        println(
            "🔥 MyLayout3\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints\n"
        )

        val totalHeight = placeables.sumOf { it.height }

        var posY = 0


        // 🔥🔥 Changing  width changes where this Composable is positioned if it's not
        // in parents bounds
        layout(width = 900, height = totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}

@Composable
private fun MyLayout4(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints = constraints.copy(minWidth = 100, maxWidth = 500)

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(updatedConstraints)
        }

        println(
            "🔥 MyLayout4\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints\n"
        )

        val totalHeight = placeables.sumOf { it.height }

        var posY = 0


        // 🔥🔥 Changing  width changes where this Composable is positioned if it's not
        // in parents bounds
        layout(width = 500, height = totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}


@Composable
private fun LayoutWidthWidthParams(
    minWidth: Int,
    maxWidth: Int,
    constraintsMinWidth: Int,
    constraintsMaxWidth: Int,
    layoutWidth: Int,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val minWidthDp = density.run { minWidth.toDp() }
    val maxWidthDp = density.run { maxWidth.toDp() }

    Layout(
        modifier = Modifier
            .widthIn(min = minWidthDp, max = maxWidthDp)
            .border(3.dp, Green400),
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val updatedConstraints =
            constraints.copy(minWidth = constraintsMinWidth, maxWidth = constraintsMaxWidth)

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(updatedConstraints)
        }

        val totalHeight = placeables.sumOf { it.height }
        var posY = 0
        // 🔥🔥 Changing  width changes where this Composable is positioned if it's not
        // in parents bounds
        layout(width = layoutWidth, height = totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}
