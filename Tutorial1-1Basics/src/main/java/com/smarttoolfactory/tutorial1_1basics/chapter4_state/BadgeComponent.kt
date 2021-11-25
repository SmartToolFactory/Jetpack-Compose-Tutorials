package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.annotation.IntRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

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
        .then(

            if (badgeState.elevation > 0.dp) {
                modifier.drawBehind {

                    val elevationInPx = with(density) {
                        badgeState.elevation.toPx()
                    }

                    val center = Offset(center.x + elevationInPx, center.y + elevationInPx)
                    if (isCircleShape) {
                        drawCircle(color = badgeState.shadowColor, this.size.width / 2f, center)
                    } else {
                        drawRoundRect(
                            color = badgeState.shadowColor,
                            alpha = .7f,
                            size = this.size,
                            topLeft = Offset(elevationInPx, elevationInPx),
                            cornerRadius = CornerRadius(
                                size.height * badgeState.roundedRadiusPercent / 100,
                                size.height * badgeState.roundedRadiusPercent / 100
                            )
                        )
                    }
                }
            } else {
                modifier
            }
        )
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
                    .placeRelative((badgeHeight - textSize.width) / 2, (badgeHeight - textSize.height) / 2)
            }
        } else {

            // Space left and right of the text, this is drawing area + empty space
            val horizontalSpaceAroundText = with(density) {
                textHeight * .12f + 6 + badgeState.horizontalPadding.toPx()
            }
            val width = (textSize.width + 2 * horizontalSpaceAroundText).toInt()

            layout(width = width, height = badgeHeight) {
                placeables.first()
                    .placeRelative((width - textSize.width) / 2, (-textSize.height + badgeHeight) / 2)
            }
        }
    }
}

@Composable
fun rememberBadgeState(
    maxNumber: Int = 99,
    circleShapeThreshold: Int = 1,
    @IntRange(from = 0, to = 99) roundedRadiusPercent: Int = 50,
    backgroundColor: Color = Color.Red,
    horizontalPadding: Dp = 4.dp,
    verticalPadding: Dp = 0.dp,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    elevation: Dp = 0.dp,
    shadowColor: Color = Color(0x55000000),
    borderStroke: BorderStroke? = null,
): BadgeState {
    return remember {
        BadgeState(
            maxNumber,
            circleShapeThreshold,
            roundedRadiusPercent,
            backgroundColor,
            horizontalPadding,
            verticalPadding,
            textColor,
            fontSize,
            elevation,
            shadowColor,
            borderStroke
        )
    }
}

class BadgeState(
    maxNumber: Int = 99,
    circleShapeThreshold: Int = 1,
    @IntRange(from = 0, to = 99) roundedRadiusPercent: Int = 50,
    backgroundColor: Color,
    var horizontalPadding: Dp = 4.dp,
    var verticalPadding: Dp = 0.dp,
    textColor: Color,
    fontSize: TextUnit,
    elevation: Dp,
    var shadowColor: Color = Color(0x55000000),
    var borderStroke: BorderStroke? = null
) {
    var backgroundColor by mutableStateOf(backgroundColor)
    var textColor by mutableStateOf(textColor)
    var elevation by mutableStateOf(elevation)
    var fontSize by mutableStateOf(fontSize)

    /**
     * This is them maximum number to be displayed
     * Any number above this will be showed as **MAX_NUMBER+**
     */
    var maxNumber = maxNumber

    /**
     * After how many digits should start drawing rectangle shape
     *
     * Setting threshold to 2 will draw circle when badge count is less or equal to 2
     */
    var circleShapeThreshold by mutableStateOf(circleShapeThreshold)

    /**
     * Radius for drawing rounded rect background.
     *
     * It's 50% of the smaller dimension by default
     */
    var roundedRadiusPercent = roundedRadiusPercent


    private var badgeCount = -1
    internal var text by mutableStateOf("0")

    /**
     * Set number to be displayed in Badge.
     */
    fun setBadgeCount(count: String, showWhenZero: Boolean = false) {

        val badgeCount = count.toIntOrNull()

        badgeCount?.let {
            setBadgeCount(it, showWhenZero)
        }
    }


    fun setBadgeCount(count: Int, showWhenZero: Boolean = true) {
        this.badgeCount = count

        when {
            count in 1..maxNumber -> {
                text = count.toString()
            }
            count > maxNumber -> {
                text = "$maxNumber+"
            }
            count <= 0 -> {
                text = "0"
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
