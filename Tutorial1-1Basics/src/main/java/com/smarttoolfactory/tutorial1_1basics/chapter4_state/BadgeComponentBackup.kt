/*
package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*


@Composable
fun BadgeComponent(
    modifier: Modifier = Modifier,
    text: String = "0",
    badgeState: BadgeState = rememberBadgeState()
) {
    Badge(text = text, badgeState = badgeState, modifier)
}

@Composable
private fun Badge(text: String, badgeState: BadgeState, modifier: Modifier = Modifier) {
    BadgeLayout(text = text, badgeState = badgeState, modifier = modifier)
}

@Composable
private fun BadgeLayout(text: String, badgeState: BadgeState, modifier: Modifier = Modifier) {

    var circleRadius = 0
    var size: IntSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    val content = @Composable {

        Text(
            text = text,
            modifier = Modifier.background(Color.Yellow),
            fontSize = 100.sp,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                size = textLayoutResult.size
                circleRadius = size.width.coerceAtLeast(size.height)
            },
        )

    }

    Layout(
        modifier = modifier
            .background(Color.Blue, shape = CircleShape)
            .padding(4.dp),
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        println("🔥 Badge: $circleRadius, size: $size")
        layout(width = circleRadius, height = circleRadius) {
            placeables.first().placeRelative((circleRadius - size.width) / 2, 0)
        }
    }

}

@Composable
fun rememberBadgeState(
    shape: Shape = CircleShape,
    color: Color = Color.Red,
    contentColor: Color = Color.White,
    elevation: Dp = 2.dp,
    borderStroke: BorderStroke? = null
): BadgeState {
    return remember {
        BadgeState(
            shape,
            color,
            contentColor,
            elevation,
            borderStroke
        )
    }
}

class BadgeState(
    shape: Shape,
    color: Color,
    contentColor: Color,
    elevation: Dp,
    borderStroke: BorderStroke? = null
) {
    var shape by mutableStateOf(shape)
    var color by mutableStateOf(color)
    var contentColor by mutableStateOf(contentColor)
    var elevation by mutableStateOf(elevation)
    var borderStroke by mutableStateOf(borderStroke)
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun BadgeComponentPreview() {
    BadgeComponent()
}
*/
