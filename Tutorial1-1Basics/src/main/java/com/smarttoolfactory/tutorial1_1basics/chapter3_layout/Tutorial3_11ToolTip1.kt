package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor


@Preview
@Composable
private fun PopupTest() {

    var showPopup by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.DarkGray))

        var paddingStart by remember {
            mutableFloatStateOf(8f)
        }

        Slider(
            value = paddingStart,
            onValueChange = {
                paddingStart = it
            },
            valueRange = 0f..300f
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Info1")

            Spacer(Modifier.width(paddingStart.dp))

            ToolTip(
                modifier = Modifier,
                showToolTip = showPopup,
                onDismissRequest = {
                    showPopup = false
                },
                toolTipContent = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Some ToolTip Message",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            ) {
                // This is anchor
                IconButton(
                    modifier = Modifier.border(1.dp, Color.Green),
                    onClick = {
                        showPopup = showPopup.not()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(60.dp),
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        }

    }
}

@Composable
private fun ToolTip(
    showToolTip: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    toolTipContent: @Composable () -> Unit,
    anchor: @Composable () -> Unit,
) {

    val density = LocalDensity.current

    var arrowTipOffset by remember {
        mutableStateOf(0.dp)
    }

    var anchorRect by remember {
        mutableStateOf(Rect.Zero)
    }

    var toolTipContentRect: Rect by remember {
        mutableStateOf(Rect.Zero)
    }

    val toolTipHalfWidth = toolTipContentRect.width / 2
    val anchorCenterX = anchorRect.center.x
    val tooltipCenterX = toolTipContentRect.center.x

    arrowTipOffset = with(density) {
        (toolTipHalfWidth + (anchorCenterX - tooltipCenterX)).toDp() - 12.dp
    }

    Box(
        modifier = Modifier
            .onPlaced {
                anchorRect = it.boundsInWindow()
            }
    ) {
        anchor()

        val toolTipBox = @Composable {
            Box(
                modifier = modifier
                    .onGloballyPositioned {
                        toolTipContentRect = it.boundsInWindow()
                        println("toolTipContentRect: $toolTipContentRect")
                    }
                    .drawBubble(
                        arrowWidth = 24.dp,
                        arrowHeight = 24.dp,
                        arrowOffset = arrowTipOffset,
                        arrowDirection = ArrowDirection.Top,
                        elevation = 4.dp,
                        color = Color.Red
                    )
            ) {
                toolTipContent()
            }
        }

        if (showToolTip) {
            ToolTipPopUp(
                onDismissRequest = onDismissRequest,
                rect = toolTipContentRect,
                toolTipContent = toolTipBox,
            )
        }
    }
}

@Composable
private fun ToolTipPopUp(
    rect: Rect = Rect.Zero,
    toolTipContent: @Composable () -> Unit,
    onDismissRequest: () -> Unit = {}
) {

    val offset = IntOffset(0, rect.height.toInt())

    Popup(
        popupPositionProvider = ToolTipPositionProvider(
            alignment = Alignment.BottomCenter,
            offset = offset
        ),
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier.onGloballyPositioned {
                println("Inside ToolTipPopUp() boundsInWindow:  ${it.boundsInWindow()}")
            }
        ) {
            toolTipContent()
        }
    }
}

private class ToolTipPositionProvider(
    val alignment: Alignment = Alignment.TopCenter,
    val offset: IntOffset = IntOffset(0, 0)
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
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

        println(
            "🍏 PROVIDER: " +
                    "anchorBounds: $anchorBounds, " +
                    "popupContentSize: $popupContentSize, " +
                    "popupPosition: $popupPosition"
        )

        return popupPosition
    }
}
