package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*

@Composable
fun Tutorial3_2Screen7() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .border(2.dp, Color.Red)
    ) {

        Box(modifier = Modifier
//            .width(190.dp)
            .border(2.dp, Color.Green)
            .layout { measurable, constraints ->
                val arrowWidth = 100
                val placeable = measurable.measure(
                    constraints.offset(
                        horizontal = -arrowWidth,
                        vertical = 0
                    )
                )
                val width = constraints.constrainWidth(placeable.width)

                layout(width, placeable.height) {
                    placeable.placeRelative(arrowWidth, 0)
                }
            }
            .border(2.dp, Color.Magenta)
        ) {
            Text(
                text = "Hello World",
                fontSize = 20.sp,
                modifier = Modifier
                    .background(
                        Color.Red,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(4.dp),
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color.Yellow)
        )

    }
}

fun Modifier.drawBubble(
    arrowWidth: Dp,
    arrowHeight: Dp,
    bubbleOffset: Dp,
    arrowDirection: ArrowDirection
) = composed {

    val arrowWidthPx: Float
    val arrowHeightPx: Float
    val bubbleOffsetPx: Float

    with(LocalDensity.current) {
        arrowWidthPx = arrowWidth.toPx()
        arrowHeightPx = arrowHeight.toPx()
        bubbleOffsetPx = bubbleOffset.toPx()
    }

    val shape = remember(arrowWidth, arrowHeight, bubbleOffset, arrowDirection) {
        createBubbleShape(arrowWidthPx, arrowHeightPx, bubbleOffsetPx, arrowDirection)
    }

    Modifier.clip(shape)
}

fun createBubbleShape(
    arrowWidth: Float,
    arrowHeight: Float,
    bubbleOffset: Float,
    arrowDirection: ArrowDirection
): GenericShape {

    return GenericShape { size: Size, layoutDirection: LayoutDirection ->

        val width = size.width
        val height = size.height

        when (arrowDirection) {
            ArrowDirection.Left -> {
                moveTo(arrowWidth, bubbleOffset)
                lineTo(0f, bubbleOffset)
                lineTo(arrowWidth, arrowHeight + bubbleOffset)
                this.addRoundRect(
                    RoundRect(
                        rect = Rect(left = arrowWidth, top = 0f, right = width, bottom = height),
                        cornerRadius = CornerRadius(x = 20f, y = 20f)
                    )
                )
            }

            ArrowDirection.Right -> {

            }

            ArrowDirection.Top -> {

            }

            else -> {

            }
        }
    }

}

enum class ArrowDirection {
    Left, Right, Top, Bottom
}
