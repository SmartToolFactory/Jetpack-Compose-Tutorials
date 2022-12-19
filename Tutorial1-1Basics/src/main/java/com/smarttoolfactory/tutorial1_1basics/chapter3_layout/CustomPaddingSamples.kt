package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.*

/*
    These padding samples are to show how Constraints.offset or
    Constraints.constrainWidth/Height effect placeable placing when Composable dimensions are
    bigger than parent Composable dimensions.
 */

/**
 * This modifier is similar to **padding** modifier. Uses Constraints.offset() to measure placeable
 * without the area reserved for padding. When size of the Composable is bigger than parent
 * Composable offset limits area to placeable width + horizontal padding when setting width
 */
@Stable
fun Modifier.paddingWithOffsetAndConstrain(all: Dp) =
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

        val offsetConstraints = constraints.offset(-horizontal, -vertical)
        val placeable = measurable.measure(offsetConstraints)

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)

        println(
            "😁 PaddingModifier()\n" +
                    "constraints: $constraints\n" +
                    "offsetConstraints: $offsetConstraints\n" +
                    "horizontal: $horizontal, " +
                    "placeable width: ${placeable.width}, " +
                    "constrainedWidth: $width"
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

/**
 * This modifier is similar to **padding** modifier but
 * `measurable.measure(constraint)`used instead of
 * `measurable.measure(constraints.offset(-horizontal, -vertical))`.
 * offset only reserves area after padding is applied. With this modifier if parent dimensions
 * are bigger we break padding.
 *
 * ## Note
 * This is for demonstration only. Use offset when placing placeables with some rules requires
 *  offset to limit placeable size considering padding size
 *
 */
@Stable
fun Modifier.paddingWithConstrainOnly(all: Dp) =
    this.then(
        PaddingModifierWitConstrain(
            start = all,
            top = all,
            end = all,
            bottom = all,
            rtlAware = true
        )
    )

// Implementation detail
private class PaddingModifierWitConstrain(
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

        val placeable = measurable.measure(constraints)

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)

        println(
            "🤡 PaddingModifierWitConstrainOnly()\n" +
                    "constraints: $constraints\n" +
                    "horizontal: $horizontal, " +
                    "placeable width: ${placeable.width}, " +
                    "constrainedWidth: $width"
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


/**
 * This modifier is similar to **padding** modifier. Uses Constraints.offset() to measure placeable
 * without the area reserved for padding. When size of the Composable is bigger than parent
 * Composable offset limits area to placeable width + horizontal padding when setting width
 */
@Stable
fun Modifier.paddingWithOffsetOnly(all: Dp) =
    this.then(
        PaddingModifierWitOffset(start = all, top = all, end = all, bottom = all, rtlAware = true)
    )

// Implementation detail
private class PaddingModifierWitOffset(
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

        val offsetConstraints = constraints.offset(-horizontal, -vertical)
        val placeable = measurable.measure(offsetConstraints)

        val width = (placeable.width + horizontal)
        val height = (placeable.height + vertical)

        println(
            "😱 PaddingModifierWitOffsetOnly()\n" +
                    "constraints: $constraints\n" +
                    "offsetConstraints: $offsetConstraints\n" +
                    "horizontal: $horizontal, " +
                    "placeable width: ${placeable.width}, " +
                    "width: $width"
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

/**
 * This is the simplest Padding that doesn't use offset or constrain to remove area for padding
 * from the dimensions of Composable. Without removing padding sections when dimensions of element
 * using this modifier is equal or bigger than parent it oveflows from its parent.
 */
@Stable
fun Modifier.paddingNoOffsetNoConstrain(all: Dp) =
    this.then(
        PaddingModifierPlain(
            start = all,
            top = all,
            end = all,
            bottom = all,
            rtlAware = true
        )
    )

// Implementation detail
private class PaddingModifierPlain(
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

        val placeable = measurable.measure(constraints)

        val width = (placeable.width + horizontal)
        val height = (placeable.height + vertical)

        println(
            "😍 PaddingModifierPlain()\n" +
                    "constraints: $constraints\n" +
                    "horizontal: $horizontal, " +
                    "placeable width: ${placeable.width}, " +
                    "width: $width"
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