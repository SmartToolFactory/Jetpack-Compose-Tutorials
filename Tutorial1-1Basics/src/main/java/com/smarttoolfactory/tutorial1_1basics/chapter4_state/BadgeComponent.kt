package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun BadgeComponent(
    badgeState: BadgeState = rememberBadgeState()
) {

    badgeState.fontSize = 34.sp
    badgeState.circleShapeThreshold = 1
    badgeState.maxNumber = 49

    Badge(badgeState = badgeState)

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            repeat(100) {
                delay(200)
                badgeState.setBadgeCount(it)
            }
        }
    }
}

@Composable
private fun Badge(badgeState: BadgeState) {

    val text = badgeState.text
    val circleThreshold = badgeState.circleShapeThreshold
    val isCircleShape = text.length <= circleThreshold

    println("🤔 Badge() Text: $text, circleThreshold: $circleThreshold")


    var circleRadius = 0
    var size: IntSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    val content = @Composable {

        Text(
            text = badgeState.text,
//            modifier = Modifier.background(Color.Yellow),
            color = badgeState.textColor,
            fontSize = badgeState.fontSize,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                if (isCircleShape) {
                    size = textLayoutResult.size
                    circleRadius = size.width.coerceAtLeast(size.height)
                }
            },
        )

    }

    Layout(
        modifier = Modifier
            .background(
                badgeState.backgroundColor,
                shape = if (isCircleShape) CircleShape else RoundedCornerShape(24.dp)
            )
            .padding(8.dp),
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }


        if (isCircleShape) {
            println("🔥 Badge: $circleRadius, size: $size")
            layout(width = circleRadius, height = circleRadius) {
                placeables.first().placeRelative((circleRadius - size.width) / 2, 0)
            }
        } else {
            layout(width = placeables.first().width, height = placeables.first().height) {
                placeables.first().placeRelative(0, 0)
            }
        }
    }

}

@Composable
fun rememberBadgeState(
    maxNumber: Int = 99,
    circleShapeThreshold: Int = 1,
    @FloatRange(from = 0.3, to = 1.0) radiusRatio: Float = .5f,
    backgroundColor: Color = Color.Red,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    elevation: Dp = 2.dp,
    borderStroke: BorderStroke? = null
): BadgeState {
    return remember {
        BadgeState(
            maxNumber,
            circleShapeThreshold,
            radiusRatio,
            backgroundColor,
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
    @FloatRange(from = 0.3, to = 1.0) radiusRatio: Float = .5f,
    backgroundColor: Color,
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
    var radiusRatio = radiusRatio
        set(value) {
            if (value < 1) field = value
        }

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

        println("SetBadgeCount $count, max: $maxNumber")

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
    BadgeComponent()
}