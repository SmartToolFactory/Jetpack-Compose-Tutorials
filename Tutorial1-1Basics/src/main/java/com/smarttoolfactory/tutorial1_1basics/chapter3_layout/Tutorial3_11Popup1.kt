package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor

@Preview
@Composable
private fun PopupTest() {

    val viewWidth = LocalView.current.width

    var showPopup by remember {
        mutableStateOf(false)
    }

    var anchorBounds by remember {
        mutableStateOf(IntRect.Zero)
    }

    var popUpBounds by remember {
        mutableStateOf(IntRect.Zero)
    }

    val tipOffset = if (popUpBounds == IntRect.Zero) {
        IntOffset.Zero
    } else {
        val popUpTopLeft = calculatePopUpPosition(
            anchorBounds = anchorBounds,
            layoutDirection = LocalLayoutDirection.current,
            popupContentSize = popUpBounds.size
        )

        println("POP UP LEFT: $popUpTopLeft, popUpBounds: $popUpBounds")

        // PopUp overflows from start
        val xPos = if (popUpTopLeft.x < 0) {
            popUpTopLeft.x + popUpBounds.width / 2 - popUpBounds.left
            // PopUp overflows from end
        } else if (popUpTopLeft.x + popUpBounds.width > viewWidth) {
            val paddingEnd = (viewWidth - popUpTopLeft.x - popUpBounds.width)
            val overFlowFromEnd = (popUpTopLeft.x + popUpBounds.width - viewWidth).coerceAtLeast(
                0
            )
            println("Padding end: $paddingEnd, overFlowFromEnd: $overFlowFromEnd")
            anchorBounds.center.x - popUpTopLeft.x + overFlowFromEnd
        } else {
            // PopUp is in bounds
            anchorBounds.center.x - popUpTopLeft.x
        }
        IntOffset(xPos, popUpTopLeft.y)
    }

    var padding by remember {
        mutableFloatStateOf(0f)
    }

    println(
        "🔥 anchorBounds " +
                "viewWidth: $viewWidth, " +
                "center: ${anchorBounds.center.x}, " +
                "popUpTopLeft.x: ${popUpBounds.bottomCenter.x}, " +
                "tipOffset: $tipOffset"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        Slider(
            value = padding,
            onValueChange = {
                padding = it
            },
            valueRange = 0f..350f
        )

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Info")
            Spacer(modifier = Modifier.width(padding.dp))

            Box {

                // This is content
                AnchorContent(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    showPopup = showPopup.not()
                                }
                            )
                        }
                        .onPlaced {
                            anchorBounds = it.boundsInWindow().roundToIntRect()
                        }
                )

                if (showPopup) {
                    PopUpContent(
                        modifier = Modifier
                            .onPlaced {
                                println("Before Padding onPlaced(): ${it.boundsInWindow()}, size: ${it.size}")
                            }
                            .border(2.dp, Color.Red)
                            .padding(start = 20.dp, end = 30.dp)
                            .border(2.dp, Color.Blue)
                            .onPlaced {

                                popUpBounds = it.boundsInWindow().roundToIntRect()

                                val parentBound =
                                    it.parentCoordinates?.boundsInWindow()?.roundToIntRect()
                                        ?: popUpBounds

                                // This are padding values on each side of the popUp
                                // This is difference between red and blue rectangle

                                val topLeft = popUpBounds.topLeft.minus(parentBound.topLeft)
                                val bottomRight =
                                    parentBound.bottomRight.minus(popUpBounds.bottomRight)

                                val paddingValues = PopUpPaddingValues(
                                    start = topLeft.x,
                                    top = topLeft.y,
                                    end = bottomRight.x,
                                    bottom = bottomRight.y
                                )

                                PopUpData(
                                    bounds = popUpBounds,
                                    paddingValues = paddingValues
                                )
                                println(
                                    "After Padding onPlaced(): ${it.boundsInWindow()}, " +
                                            "size: ${it.size}\n" +
                                            "parentBound: $parentBound, " +
                                            "parent size: ${parentBound.size}, " +
                                            "paddingValues: $paddingValues"
                                )


                            },
                        offset = IntOffset(0, popUpBounds.height),
                        tipOffset = tipOffset,
                        onDismissRequest = {
                            showPopup = false
                        }
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray)
        )

    }
}

data class PopUpData(
    val bounds: IntRect,
    val paddingValues: PopUpPaddingValues,
) {
    companion object {
        val Zero = PopUpData(
            bounds = IntRect.Zero,
            paddingValues = PopUpPaddingValues()
        )
    }
}

data class PopUpPaddingValues(
    val start: Int = 0,
    val top: Int = 0,
    val end: Int = 0,
    val bottom: Int = 0,
)

@Composable
private fun CustomPopUp(
    modifier: Modifier = Modifier,
    anchor: @Composable () -> Unit,
    content: @Composable () -> Unit
) {

}

@Composable
private fun PopUpContent(
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    tipOffset: IntOffset = IntOffset.Zero,
    alignment: Alignment = Alignment.BottomCenter,
    onDismissRequest: () -> Unit

) {
    Popup(
        properties = PopupProperties(),
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = modifier
                .drawWithContent {
                    drawContent()

                    drawCircle(
                        radius = 10f,
//                        center = Offset(center.x + tipOffset.x, 0f),
                        center = Offset(tipOffset.x.toFloat(), 0f),
                        color = Color.Green
                    )
                }
                .background(Color.White)
                .padding(8.dp)
        ) {
            Text("Pop up Content Some content")
        }
    }
}

@Composable
private fun AnchorContent(
    modifier: Modifier
) {
    // This is content
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Info,
        contentDescription = null
    )
}

private fun calculatePopUpPosition(
    alignment: Alignment = Alignment.BottomCenter,
    offset: IntOffset = IntOffset.Zero,
    anchorBounds: IntRect,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize
): IntOffset {
    var popupPosition = IntOffset(0, 0)

    // Get the aligned point inside the parent
    val parentAlignmentPoint = alignment.align(
        IntSize.Zero,
        IntSize(anchorBounds.width, anchorBounds.height),
        layoutDirection
    )
    // Get the aligned point inside the child
    val relativePopupPos = alignment.align(
        IntSize.Zero,
        IntSize(popupContentSize.width, popupContentSize.height),
        layoutDirection
    )

    // Add the position of the parent
    popupPosition += IntOffset(anchorBounds.left, anchorBounds.top)

    // Add the distance between the parent's top left corner and the alignment point
    popupPosition += parentAlignmentPoint

    // Subtract the distance between the children's top left corner and the alignment point
    popupPosition -= IntOffset(relativePopupPos.x, relativePopupPos.y)

    // Add the user offset
    val resolvedOffset = IntOffset(
        offset.x * (if (layoutDirection == LayoutDirection.Ltr) 1 else -1),
        offset.y
    )
    popupPosition += resolvedOffset

    return popupPosition
}