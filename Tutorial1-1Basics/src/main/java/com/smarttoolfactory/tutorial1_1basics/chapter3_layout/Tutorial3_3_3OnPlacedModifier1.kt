package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial3_3Screen3() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    val density = LocalDensity.current

    val outerWidth: Dp
    val middleWidth: Dp
    val innerWidth: Dp

    with(density) {
        outerWidth = 500f.toDp()
        middleWidth = 300f.toDp()
        innerWidth = 100f.toDp()
    }

    /*
        Prints
        I  🚙 INNER layout() width: 100, height: 100
        I  🚙 MIDDLE layout() width: 300, height: 300
        I  🚙 OUTER layout() width: 500, height: 500
        I  🍏 OUTER onPlaced() positionInParent: 0.0
        I  😶‍🌫️ OUTER CustomLayout layout...
        I  🍏 MIDDLE onPlaced() positionInParent: 0.0
        I  😶‍🌫️ MIDDLE CustomLayout layout...
        I  🍏 INNER onPlaced() positionInParent: 407.0
        I  😶‍🌫️ INNER CustomLayout layout...
        I  🍎 OUTER onGloballyPositioned() positionInParent: 0.0
        I  🍎 MIDDLE onGloballyPositioned() positionInParent: 0.0
        I  🍎 INNER onGloballyPositioned() positionInParent: 407.0
        I  🚗 OUTER drawWithContent()
        I  🚗 MIDDLE drawWithContent()
        I  🚗 INNER drawWithContent()
     */

    var outerOffsetX by remember {
        mutableStateOf(0f)
    }

    var middleOffsetX by remember {
        mutableStateOf(0f)
    }

    var innerOffsetX by remember {
        mutableStateOf(0f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        StyleableTutorialText(
            text = "**Modifier.onPlaced(onPlaced)** invokes **onPlaced** after the parent " +
                    "LayoutModifier and parent layout has been placed " +
                    "and before child LayoutModifier is placed. " +
                    "This allows child LayoutModifier to adjust its own placement " +
                    "based on where the parent is.",
            bullets = false
        )

        SliderWithLabel(
            label = "Outer offsetX: ${outerOffsetX.toInt()}",
            value = outerOffsetX,
            onValueChange = { outerOffsetX = it }
        )

        SliderWithLabel(
            label = "Middle offsetX: ${middleOffsetX.toInt()}",
            value = middleOffsetX,
            onValueChange = { middleOffsetX = it }
        )

        SliderWithLabel(
            label = "Inner offsetX: ${innerOffsetX.toInt()}",
            value = innerOffsetX,
            onValueChange = { innerOffsetX = it }
        )

        CustomBox(
            modifier = Modifier
                .offset {
                    IntOffset(outerOffsetX.toInt(), 0)
                }
                .layoutPlacementDraw("OUTER")
                .size(outerWidth)
                .background(Pink400),
            title = "OUTER",
        ) {
            CustomBox(
                modifier = Modifier
                    .offset {
                        IntOffset(middleOffsetX.toInt(), 0)
                    }
                    .layoutPlacementDraw("MIDDLE")
                    .size(middleWidth)
                    .background(Orange400),
                title = "MIDDLE",
            ) {
                CustomBox(
                    modifier = Modifier
                        .offset {
                            IntOffset(innerOffsetX.toInt(), 0)
                        }
                        .layoutPlacementDraw("INNER")
                        .size(innerWidth)
                        .background(Blue400),
                    title = "INNER",
                ) {

                }
            }
        }
    }
}

fun Modifier.layoutPlacementDraw(
    title: String
) = this.then(
    Modifier
        .onPlaced { layoutCoordinates: LayoutCoordinates ->
            println(
                "🍏 $title onPlaced() " +
                        "positionInParent: ${layoutCoordinates.positionInParent().x}\n"
            )
        }

        .drawWithContent {
            println("🚗 $title drawWithContent()\n")
            drawContent()
        }
        .onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
            println(
                "🍎 $title onGloballyPositioned() " +
                        "positionInParent: ${layoutCoordinates.positionInParent().x}\n"
            )
        }
        .layout { measurable, constraints ->

            val placeable = measurable.measure(constraints = constraints)

            println(
                "🚙 $title layout() " +
                        "width: ${placeable.width}, " +
                        "height: ${placeable.height}\n"
            )

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
)

@Composable
private fun CustomBox(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(
                constraints = constraints.copy(
                    minWidth = 0,
                    minHeight = 0
                )
            )
        }

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            println("😶‍🌫️ $title CustomLayout layout...")
            placeables.forEach {
                it.placeRelative(0, 0)
            }
        }
    }
}

