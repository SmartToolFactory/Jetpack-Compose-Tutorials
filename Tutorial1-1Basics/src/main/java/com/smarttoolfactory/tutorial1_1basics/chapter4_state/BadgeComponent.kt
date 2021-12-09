package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

class BadgeData(var textHeight: Int = 0, var badgeHeight: Int = 0, var textSize: IntSize? = null) {

    override fun toString(): String {
        return "BadgeData() textHeight: $textHeight, dimension: $textSize"
    }
}

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    badgeState: BadgeState = rememberBadgeState(),
) {

    val showBadge = remember {
        derivedStateOf {
            badgeState.showBadgeThreshold < badgeState.numberOnBadge
        }
    }

    if (showBadge.value) {
        BadgeComponent(badgeState = badgeState, modifier = modifier)
    }
}

@Composable
private fun BadgeComponent(badgeState: BadgeState, modifier: Modifier = Modifier) {

    val badgeData = remember { BadgeData() }

    val density = LocalDensity.current
    val isCircleShape = badgeState.isCircleShape

    val shape =
        if (isCircleShape) CircleShape else RoundedCornerShape(badgeState.roundedRadiusPercent)

    val content = @Composable {
        Text(
            text = badgeState.text,
            color = badgeState.textColor,
            fontSize = badgeState.fontSize,
            lineHeight = badgeState.fontSize,
            fontWeight = badgeState.fontWeight,
            fontFamily = badgeState.fontFamily,
            textDecoration = badgeState.textDecoration,
            fontStyle = badgeState.fontStyle,
            maxLines = 1,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                // 🔥🔥 This is text height without padding, result size returns height with font padding
                badgeData.textHeight = textLayoutResult.firstBaseline.toInt()
                badgeData.textSize = textLayoutResult.size
            }
        )
    }

    val badgeModifier = modifier.getBadgeModifier(badgeState, shape)

    Layout(
        modifier = badgeModifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val placeable = placeables.first()
        val textHeight = badgeData.textHeight


        // Space above and below text, this is drawing area + empty space
        val verticalSpaceAroundText = with(density) {
            textHeight * .12f + 6 + badgeState.verticalPadding.toPx()
        }

        if (isCircleShape) {

            if (badgeData.badgeHeight == 0) {
                // Height of our badge is sum of text height(without font padding)
                // and vertical space on top and at the bottom
                badgeData.badgeHeight = (textHeight + 2 * verticalSpaceAroundText).toInt()

                // Use bigger dimension to have circle that covers 2 digit counts either
                badgeData.badgeHeight = placeable.width.coerceAtLeast(badgeData.badgeHeight)
            }

            val badgeHeight = badgeData.badgeHeight

            layout(width = badgeHeight, height = badgeHeight) {
                placeable.placeRelative(
                    (badgeHeight - placeable.width) / 2,
                    (badgeHeight - placeable.height) / 2
                )
            }
        } else {

            if (badgeData.badgeHeight == 0) {
                // Height of our badge is sum of text height(without font padding)
                // and vertical space on top and at the bottom
                badgeData.badgeHeight = (textHeight + 2 * verticalSpaceAroundText).toInt()
            }

            val badgeHeight = badgeData.badgeHeight

            // Space left and right of the text, this is drawing area + empty space
            val horizontalSpaceAroundText = with(density) {
                textHeight * .12f + 6 + badgeState.horizontalPadding.toPx()
            }
            val width = (placeable.width + 2 * horizontalSpaceAroundText).toInt()

            layout(width = width, height = badgeHeight) {
                placeable.placeRelative(
                    x = (width - placeable.width) / 2,
                    y = (-placeable.height + badgeHeight) / 2
                )
            }
        }
    }
}

private fun Modifier.getBadgeModifier(
    badgeState: BadgeState,
    shape: Shape
) = this
    .materialShadow(badgeState = badgeState)
    .then(
        badgeState.borderStroke?.let { borderStroke ->
            this.border(borderStroke, shape = shape)
        } ?: this
    )
    .background(
        badgeState.backgroundColor,
        shape = shape
    )

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun BadgeComponentPreview() {
    Badge()
}

