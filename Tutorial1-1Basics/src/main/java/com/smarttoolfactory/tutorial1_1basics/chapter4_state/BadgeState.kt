package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.annotation.IntRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Creates a BadgeState that is remembered across compositions.
 *
 * @param maxNumber after this number it's displayed with maxNumber+ such as 99+
 * @param circleShapeThreshold number of digits that this will badge will be drawn as circle
 * @param roundedRadiusPercent corner radius ratio when badge has rounded rectangle shape
 * @param backgroundColor background color for badge
 * @param horizontalPadding horizontal padding for rounded rectangle shape
 * @param verticalPadding for rounded rectangle or general padding for circle shape
 * @param textColor color of the text
 * @param fontSize size of the font used for Text
 * @param shadow nullable shadow for badge
 * @param borderStroke nullable border stroke around badge
 * @param showBadgeThreshold for count to display badge. If badge count is below this
 * threshold don't display a badge. For instance don't display badge number of notification is
 * zero.
 *
 */
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
    shadow: MaterialShadow? = null,
    borderStroke: BorderStroke? = null,
    showBadgeThreshold: Int = Int.MIN_VALUE,
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
            shadow,
            borderStroke,
            showBadgeThreshold
        )
    }
}

/**
 * Creates a BadgeState that is remembered across compositions.
 *
 * @param maxNumber after this number it's displayed with maxNumber+ such as 99+
 * @param circleShapeThreshold number of digits that this will badge will be drawn as circle
 * @param roundedRadiusPercent corner radius ratio when badge has rounded rectangle shape
 * @param backgroundColor background color for badge
 * @param horizontalPadding horizontal padding for rounded rectangle shape
 * @param verticalPadding for rounded rectangle or general padding for circle shape
 * @param textColor color of the text
 * @param fontSize size of the font used for Text
 * @param shadow nullable shadow for badge
 * @param borderStroke nullable border stroke around badge
 * @param showBadgeThreshold for count to display badge. If badge count is below this
 * threshold don't display a badge. For instance don't display badge number of notification is
 * zero.
 *
 */
class BadgeState(
    var maxNumber: Int = 99,
    var circleShapeThreshold: Int = 1,
    @IntRange(from = 0, to = 99) var roundedRadiusPercent: Int = 50,
    backgroundColor: Color,
    var horizontalPadding: Dp = 4.dp,
    var verticalPadding: Dp = 0.dp,
    textColor: Color,
    var fontSize: TextUnit,
    var shadow: MaterialShadow? = null,
    var borderStroke: BorderStroke? = null,
    showBadgeThreshold: Int = Int.MIN_VALUE,
) {
    var backgroundColor by mutableStateOf(backgroundColor)
    var textColor by mutableStateOf(textColor)
    var text by mutableStateOf("0")
        private set

    var numberOnBadge by mutableStateOf(0)
        private set

    var showBadgeThreshold by mutableStateOf(showBadgeThreshold)

    /**
     * Badge has circle or rounded corner rectangle shape
     */
    val isCircleShape: Boolean
        get() = text.length <= circleShapeThreshold


    /**
     * Set number to be displayed on Badge. If this value cannot be parsed to Int it won't update
     * badge.
     *
     * Set text of badge to have custom text
     */
    fun setBadgeCount(count: String) {

        val badgeCount = count.toIntOrNull()

        badgeCount?.let {
            setBadgeCount(it)
        }
    }

    /**
     * Set number to be displayed on Badge.
     */
    fun setBadgeCount(count: Int) {

        this.numberOnBadge = count

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

    override fun toString(): String {
        return "Badge() text: $text, " +
                "numberOnBadge: $numberOnBadge, " +
                "maxNumber: $maxNumber, " +
                "circleShapeThreshold: $circleShapeThreshold, " +
                "roundedRadiusPercent: $roundedRadiusPercent, " +
                "horizontalPadding: $horizontalPadding, " +
                "verticalPadding: $verticalPadding, " +
                "fontSize: $fontSize, " +
                "shadow: $shadow, " +
                "borderStroke: $borderStroke, " +
                "backgroundColor: $backgroundColor, " +
                "isCircleShape: $isCircleShape, " +
                "circleShapeThreshold: $circleShapeThreshold, " +
                "showBadgeThreshold: $showBadgeThreshold"
    }
}