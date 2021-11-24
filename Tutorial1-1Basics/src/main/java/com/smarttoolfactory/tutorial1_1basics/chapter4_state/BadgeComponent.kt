package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.annotation.IntRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
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

    val text = badgeState.text
    val circleThreshold = badgeState.circleShapeThreshold
    val isCircleShape = text.length <= circleThreshold

    val shape =
        if (isCircleShape) CircleShape else RoundedCornerShape(badgeState.roundedRadiusPercent)

    val badgeModifier = modifier
        .then(
            badgeState.borderStroke?.let { borderStroke ->
                modifier.border(borderStroke, shape = shape)
            } ?: modifier
        )
        .background(
            badgeState.backgroundColor,
            shape = shape
        )
        .padding(badgeState.horizontalPadding)

    var size = IntSize(0, 0)

    val content = @Composable {

        println("🔥 BadgeComponent() Text Composable called")
        Text(
            text = badgeState.text,
            color = badgeState.textColor,
            fontSize = badgeState.fontSize,
            lineHeight = badgeState.fontSize,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                size = textLayoutResult.size
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

        println("🤔 BadgeComponent() Layout text: $text, isCircleShape: $isCircleShape, size: $size")

        if (isCircleShape) {
            val circleRadius = size.width.coerceAtLeast(size.height)
            layout(width = circleRadius, height = circleRadius) {
                placeables.first().placeRelative((circleRadius - size.width) / 2, 0)
            }
        } else {
            val width = placeables.first().width + size.width / 2
            layout(width = width, height = placeables.first().height) {
                placeables.first().placeRelative((width - size.width) / 2, 0)
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
    borderStroke: BorderStroke? = null
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
    borderStroke: BorderStroke? = null
) {
    var backgroundColor by mutableStateOf(backgroundColor)
    var textColor by mutableStateOf(textColor)
    var elevation by mutableStateOf(elevation)
    var fontSize by mutableStateOf(fontSize)
    var borderStroke by mutableStateOf(borderStroke)

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


    fun setBadgeCount(count: Int, showWhenZero: Boolean = false) {
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
