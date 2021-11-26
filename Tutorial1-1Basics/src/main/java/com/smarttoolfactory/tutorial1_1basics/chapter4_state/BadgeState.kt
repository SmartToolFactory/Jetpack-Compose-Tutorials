package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.annotation.IntRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    fun setBadgeCount(count: String, showWhenZero: Boolean = true) {

        val badgeCount = count.toIntOrNull()

        badgeCount?.let {
            if (it > 0 || (it == 0 && showWhenZero)) {
                setBadgeCount(it, showWhenZero)
            }

        }
    }


    fun setBadgeCount(count: Int, showWhenZero: Boolean = true) {

        if (count > 0 || (count == 0 && showWhenZero)) {
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
}