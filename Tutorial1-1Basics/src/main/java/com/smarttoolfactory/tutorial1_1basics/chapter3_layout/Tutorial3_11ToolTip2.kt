@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import kotlinx.coroutines.launch


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


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Info1")

            Spacer(Modifier.width(8.dp))

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

        Spacer(modifier = Modifier.height(80.dp))

//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Text("This is Text before Info 2")
//
//            Spacer(Modifier.width(8.dp))
//
//            ToolTip(
//                showToolTip = showPopup,
////                modifier = Modifier.padding(horizontal = 16.dp),
//                onDismissRequest = {
//                    showPopup = false
//                },
//                toolTipContent = {
//                    Box(
//                        modifier = Modifier
//                            .padding(8.dp)
//                    ) {
//                        Text(
//                            text = "Some ToolTip Message",
//                            fontSize = 16.sp,
//                            color = Color.White
//                        )
//                    }
//                }
//            ) {
//                // This is content
//                IconButton(
//                    modifier = Modifier.border(1.dp, Color.Green).size(20.dp),
//                    onClick = {
//                        showPopup = showPopup.not()
//                    }
//                ) {
//                    Icon(
////                        modifier = Modifier.size(60.dp),
//                        imageVector = Icons.Default.Info,
//                        contentDescription = null,
//                        tint = Color.Red
//                    )
//                }
//            }
//        }

//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Text("Info ")
//
//            ToolTip2(
//                showToolTip = showPopup,
//                onDismissRequest = {
//                    showPopup = false
//                },
//                toolTipContent = {
//                    Box(
//                        modifier = Modifier
//                            .padding(8.dp)
//                    ) {
//                        Text(
//                            text = "Some ToolTip Message",
//                            fontSize = 16.sp,
//                            color = Color.White
//                        )
//                    }
//                }
//            ) {
//                // This is content
//                IconButton(
//                    modifier = Modifier.border(2.dp, Color.Green).size(60.dp),
//                    onClick = {
//                        showPopup = showPopup.not()
//                    }
//                ) {
//                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
//                }
//            }
//        }
        Image(
            modifier = Modifier.fillMaxWidth().aspectRatio(4 / 3f),
            painter = painterResource(R.drawable.landscape6),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun ToolTip(
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

class ToolTipPositionProvider(
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

@Composable
private fun ToolTipSubcomposeLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        // Get max width and height of main component
        val maxSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        val tooltip = subcompose(SlotsEnum.Dependent) {
            dependentContent(maxSize)
        }.map {
            it.measure(constraints)
        }


//        println("🚗maxSize: $maxSize")

        layout(0, 0) {

            // Get List<Measurable> from subcompose function then get List<Placeable> and place them
//            mainPlaceables.forEach {
//                it.placeRelative(0, 0)
//            }
            tooltip.forEach {
                it.placeRelative(0, 0)
            }
        }
    }
}

// TODO There is a bug with Popup that clips content that exceed 16dp height
// out of bounds. Increasing caret height causes it to be clipped
@Preview
@Composable
fun TooltipBoxSample() {
    var caretSize by remember {
        mutableStateOf(16f)
    }

    Column(
        modifier = Modifier.fillMaxSize().border(2.dp, Color.Blue)
    ) {


        val state = rememberTooltipState(
            isPersistent = true
        )
        val coroutineScope = rememberCoroutineScope()

        Spacer(modifier = Modifier.height(120.dp))

        Row(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.width(100.dp))

            TooltipBox(
                positionProvider = rememberPlainTooltipPositionProvider(
                    spacingBetweenTooltipAndAnchor = caretSize.dp
                ),
                state = state,
                tooltip = {

                    PlainTooltip(
                        caretProperties = CaretProperties(
                            caretWidth = caretSize.dp,
                            caretHeight = caretSize.dp
                        ),
//                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.Red
                    ) {
                        Text(
                            text = "Tooltip Content for testing...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                },
                content = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                if (state.isVisible) {
                                    state.dismiss()
                                } else {
                                    coroutineScope.launch {
                                        state.show()
                                    }
                                }
                            },
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                }
            )
        }


        Text("Caret width: ${caretSize}dp", fontSize = 18.sp)

        Slider(
            value = caretSize,
            onValueChange = {
                caretSize = it
            },
            valueRange = 0f..100f
        )

    }
}