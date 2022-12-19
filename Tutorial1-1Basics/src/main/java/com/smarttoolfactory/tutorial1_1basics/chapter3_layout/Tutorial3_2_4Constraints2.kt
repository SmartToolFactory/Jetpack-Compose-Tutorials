package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

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
            .fillMaxHeight()
            .border(1.dp, Color.Red)
    ) {

        StyleableTutorialText(
            text = "When layout width is set different than **Constraints.maxWidth** " +
                    "Parent is placed at (Constraints.maxWidth-layout width)/2\n" +
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
            text = "2-) In this example child composables are measured with " +
                    "**constraints.copy(minWidth = 750, maxWidth = 900)**\n" +
                    "Since child Composables' widths are bigger than container they overflow from" +
                    "parent Composable.\n" +
                    "MyLayout3(green border) " +
                    "overflows from parent as **(Constraints.maxWidth-layout width) " +
                    "maxWidth is 700px while layout width is 900px because of this **MyLayout3** " +
                    "is moved to right by 100px.",
        )

        MyLayout3(modifier = Modifier.border(3.dp, Green400)) {
            Content()
        }
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
            .width(child1Width)
            .background(Pink400)
            .clickable {}
    ) {
        Text("constraints1: $constraints", color = Color.White)
    }

    BoxWithConstraints(
        modifier = Modifier
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
            "🔥 MyLayout2\n" +
                    "constraints: $constraints\n" +
                    "updatedConstraints: $updatedConstraints\n"
        )

        val totalHeight = placeables.sumOf { it.height }

        var posY = 0


        // 🔥🔥 Changing  width changes where this Compoasble is positioned if it's not
        // in parents bounds
        layout(width = 900, height = totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}
