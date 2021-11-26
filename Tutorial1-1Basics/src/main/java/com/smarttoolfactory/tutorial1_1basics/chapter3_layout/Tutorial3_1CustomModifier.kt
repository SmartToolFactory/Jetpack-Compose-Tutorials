package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Custom Modifier")
        StyleableTutorialText(
            text = "1-) To create custom modifier use layout extension function " +
                    "of Modifier which returns a Modifier. " +
                    "Get a placeable measuring with measurable, get width, height of placeable " +
                    "and call generic layout function that returns **MeasureResult**."
        )

        TutorialText2(text = "customAlign Modifier")

        val modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.LightGray)

        Column(modifier.wrapContentHeight()) {
            Text(
                text = "Align Start with space",
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .customAlign(align = HorizontalAlign.START)
            )

            Text(
                text = "Align Center with space",
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .customAlign(align = HorizontalAlign.CENTER)
            )

            Text(
                text = "Align End with space",
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .customAlign(align = HorizontalAlign.END)
            )
        }

        TutorialText2(text = "firstBaselineToTop Modifier")
        Row(modifier.wrapContentHeight()) {
            Text(
                text = "Padding 32dp",
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .padding(top = 32.dp)
            )


            Text(
                text = "Baseline 32dp",
                modifier = Modifier
                    .background(Color(0xFF8BC34A))
                    .firstBaselineToTop(32.dp)
            )

        }

        StyleableTutorialText(
            text = "2-) **LayoutModifier** class and it's **MeasureScope.measure** function can be" +
                    "used to measure a measurable to get a placeable and place it to " +
                    "add padding."
        )
        TutorialText2(text = "Custom Padding Modifier")
        Text(
            text = "Custom Padding",
            modifier = Modifier
                .background(Color(0xFF8BC34A))
                .customPadding(all = 32.dp)
        )
    }
}

/**
 * This is a fake modifier that adds space on both sides of [Measurable] with specified dp
 * and aligns this [Measurable] based on specified horizontal alignment
 */
/**
 * This is a fake modifier that adds space on both sides of [Measurable] with specified dp
 * and aligns this [Measurable] based on specified horizontal alignment
 */
fun Modifier.customAlign(
    space: Int = 60,
    align: HorizontalAlign = HorizontalAlign.CENTER
) = this.then(

    layout { measurable: Measurable, constraints: Constraints ->

        val placeable = measurable.measure(constraints)
        val width = placeable.measuredWidth + 2 * space

        layout(width, placeable.measuredHeight) {
            when (align) {
                HorizontalAlign.START -> {
                    placeable.placeRelative(0, 0)
                }

                HorizontalAlign.CENTER -> {
                    placeable.placeRelative(space, 0)
                }

                HorizontalAlign.END -> {
                    placeable.placeRelative(2 * space, 0)
                }
            }
        }
    }
)

enum class HorizontalAlign {
    START, CENTER, END
}

/**
 * Let's say you want to display a Text on the screen and control the distance from the top to
 * the baseline of the first line of texts. In order to do that, you'd need to manually place
 * the composable on the screen using the layout modifier.
 *
 */
/**
 * Let's say you want to display a Text on the screen and control the distance from the top to
 * the baseline of the first line of texts. In order to do that, you'd need to manually place
 * the composable on the screen using the layout modifier.
 *
 */
fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints ->

        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable gets placed
            placeable.placeRelative(0, placeableY)
        }
    }
)

/**
 * This modifier is similar to **padding** modifier
 */
/**
 * This modifier is similar to **padding** modifier
 */
@Stable
fun Modifier.customPadding(all: Dp) =
    this.then(
        PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true)
    )

// Implementation detail
private class PaddingModifier(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val rtlAware: Boolean,
) : LayoutModifier {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)

        println(
            "😁 PaddingModifier() " +
                    "horizontal: $horizontal, " +
                    "vertical: $vertical, placeable width: ${placeable.width}"
        )

        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}

// let's create you own custom stateful modifier with multiple arguments
fun Modifier.myModifier(width: Dp, height: Dp, color: Color) = composed(
    // pass inspector information for debug
    inspectorInfo = debugInspectorInfo {
        // name should match the name of the modifier
        name = "myModifier"
        // add name and value of each argument
        properties["width"] = width
        properties["height"] = height
        properties["color"] = color
    },
    // pass your modifier implementation that resolved per modified element

    factory = {

        val density = LocalDensity.current

        // add your modifier implementation here
        Modifier.drawBehind {

            val widthInPx = with(density) { width.toPx() }
            val heightInPx = with(density) { height.toPx() }

            drawRect(color = color, topLeft = Offset.Zero, size = Size(widthInPx, heightInPx))
        }
    }
)