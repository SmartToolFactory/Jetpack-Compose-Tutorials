package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    badgeState: BadgeState = rememberBadgeState(),
) {
    BadgeComponent(badgeState = badgeState, modifier = modifier)
}

@Composable
private fun BadgeComponent(badgeState: BadgeState, modifier: Modifier = Modifier) {

    val density = LocalDensity.current

    val text = badgeState.text
    val circleThreshold = badgeState.circleShapeThreshold
    val isCircleShape = text.length <= circleThreshold

    val shape =
        if (isCircleShape) CircleShape else RoundedCornerShape(badgeState.roundedRadiusPercent)

    val badgeModifier = modifier
        .materialShadow(badgeState = badgeState)
        .then(
            badgeState.borderStroke?.let { borderStroke ->
                modifier.border(borderStroke, shape = shape)
            } ?: modifier
        )
        .background(
            badgeState.backgroundColor,
            shape = shape
        )


    var textSize = IntSize(0, 0)
    var textHeight = 0

    // TODO Just a question: Why does this not survive recompositions?
    var badgeHeight = remember { 0 }

    println("✅ BadgeComponent: text: $text, textHeight: $textHeight, badgeHeight: $badgeHeight")

    val content = @Composable {

        println("🔥 BadgeComponent() Text Composable called")
        Text(
//            modifier = Modifier.background(Color.Blue),
            text = badgeState.text,
            color = badgeState.textColor,
            fontSize = badgeState.fontSize,
            lineHeight = badgeState.fontSize,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                textSize = textLayoutResult.size
                // 🔥🔥 This is text height without padding, result size returns height with font padding
                textHeight = textLayoutResult.firstBaseline.toInt()
                println("✏️ BadgeComponent textHeight: $textHeight")
            },
        )
    }

    Layout(
        modifier = badgeModifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        if (badgeHeight == 0) {

            // Space above and below text, this is drawing area + empty space
            val verticalSpaceAroundText = with(density) {
                textHeight * .12f + 6 + badgeState.verticalPadding.toPx()
            }

            badgeHeight = (textHeight + 2 * verticalSpaceAroundText).toInt()
            println("⚠️ Calculated badge HEIGHT: $badgeHeight")
        }

        println(
            "🤔 BadgeComponent() Layout text: $text, isCircleShape: $isCircleShape, " +
                    "textHeight: $textHeight, badgeHeight: $badgeHeight, " +
                    "placeable width: ${placeables.first().width}, size:$textSize"
        )

        if (isCircleShape) {
            badgeHeight = textSize.width.coerceAtLeast(badgeHeight)

            layout(width = badgeHeight, height = badgeHeight) {
                placeables.first()
                    .placeRelative(
                        (badgeHeight - textSize.width) / 2,
                        (badgeHeight - textSize.height) / 2
                    )
            }
        } else {

            // Space left and right of the text, this is drawing area + empty space
            val horizontalSpaceAroundText = with(density) {
                textHeight * .12f + 6 + badgeState.horizontalPadding.toPx()
            }
            val width = (textSize.width + 2 * horizontalSpaceAroundText).toInt()

            layout(width = width, height = badgeHeight) {
                placeables.first()
                    .placeRelative(
                        x = (width - textSize.width) / 2,
                        y = (-textSize.height + badgeHeight) / 2
                    )
            }
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun BadgeComponentPreview() {
    Badge()
}

