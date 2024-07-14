@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        PopUpSample()
    }
}

@Preview
@Composable
private fun PopUpSample() {
    Column(
        modifier = Modifier.fillMaxWidth().border(2.dp, Color.Red)
    ) {
        val density = LocalDensity.current


        val popupState = remember {
            PopupState(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, with(density) { 16.dp.roundToPx() })
            )
        }
        var paddingStart by remember {
            mutableFloatStateOf(200f)
        }

        var paddingTop by remember {
            mutableStateOf(0f)
        }

        var showPopup by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(paddingTop.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(paddingStart.dp))

                Box {
                    PopUpBox(
                        onDismissRequest = {
                            showPopup = false
                        },
                        isVisible = showPopup,
                        popupPositionProvider = AlignmentPopupPositionProvider(
//                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, with(density) { 16.dp.roundToPx() }),
                            popupState = popupState
                        ),
//                        popupPositionProvider = rememberPlainTooltipPositionProvider(
//                            spacingBetweenTooltipAndAnchor = 16.dp
//                        ),
//                        popupPositionProvider = ToolTipPositionProvider(
//                            alignment = Alignment.TopStart,
//                            offset = IntOffset(0, yOffset)
//                        ),
                        anchor = {
                            AnchorContent(
                                modifier = Modifier
                                    .border(2.dp, Color.Blue)
                                    .size(60.dp)
                                    .clickable {
                                        showPopup = showPopup.not()
                                        println("CLICKED showPopup: $showPopup")
                                    }
                            )
                        },
                        content = { anchorLayoutCoordinates: LayoutCoordinates? ->
                            Box(
                                modifier = Modifier
                                    .drawWithCache {

                                        val path = Path()

                                        var caretX = 0f
                                        anchorLayoutCoordinates?.boundsInWindow()
                                            ?.let { rect: Rect ->

                                                val popupContentRect = popupState.contentRect
                                                val screenWidth = popupState.windowSize.width

                                                val tooltipWidth = size.width
                                                val tooltipHeight = size.height
                                                val tooltipLeft = popupContentRect.left
                                                val tooltipRight = popupContentRect.right
                                                val tooltipCenterX = popupContentRect.center.x

                                                val anchorMid = rect.center.x

                                                val caretWidth = 24.dp.toPx()
                                                val caretHeight = 16.dp.toPx()

                                                val caretHalfWidth = caretWidth / 2
                                                val caretAlignment = popupState.caretAlignment

                                                caretX =
                                                        // Popup is positioned left but might overflow from left
                                                        // if clip is enabled
                                                    if (tooltipLeft <= 0) {
                                                        anchorMid - caretHalfWidth

                                                        // pop is center of the screen neither touches right or left
                                                        // side of the screen
                                                    } else if (tooltipRight <= screenWidth) {
                                                        tooltipWidth / 2 + anchorMid - tooltipCenterX - caretHalfWidth

                                                        // Popup is positioned right but might overflow from right
                                                        // if clip is enabled
                                                    } else {
                                                        val diff = tooltipRight - screenWidth
                                                        anchorMid - tooltipLeft + diff + -caretHalfWidth
                                                    }

                                                path.apply {
                                                    println("DRAW with CACHE anchor rect: $rect, popupContentRect: $popupContentRect caretX: $caretX")

                                                    if (caretAlignment.topAlignment()) {
                                                        moveTo(
                                                            caretX,
                                                            0f
                                                        )
                                                        lineTo(
                                                            caretX + caretHalfWidth,
                                                            -caretHeight
                                                        )
                                                        lineTo(
                                                            caretX + caretWidth,
                                                            0f
                                                        )
                                                    } else if (caretAlignment.bottomAlignment()) {
                                                        moveTo(caretX, tooltipHeight)
                                                        lineTo(
                                                            caretX + caretHalfWidth,
                                                            tooltipHeight + caretHeight
                                                        )

                                                        lineTo(
                                                            caretX + caretWidth,
                                                            tooltipHeight
                                                        )
                                                    }
                                                    close()
                                                }
                                            }

                                        onDrawWithContent {
                                            drawContent()
                                            if (path.isEmpty.not()) {
                                                drawPath(
                                                    path = path,
                                                    color = Color.Cyan
                                                )
                                            }

                                            drawCircle(
                                                color = Color.Red,
                                                radius = 10f,
                                                center = Offset(caretX, 0f)
                                            )
                                        }
                                    }
                                    .padding(horizontal = 16.dp)
//                                    .fillMaxWidth()
                                    .border(2.dp, Color.Cyan)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = "This is PopUp Content",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    )
                }
            }
        }

        Column {

            Text("paddingStart: ${paddingStart}.dp")
            Slider(
                value = paddingStart,
                onValueChange = {
                    paddingStart = it
                },
                valueRange = 0f..350f
            )

            Text("paddingTop: ${paddingTop}.dp")
            Slider(
                value = paddingTop,
                onValueChange = {
                    paddingTop = it
                },
                valueRange = 0f..650f
            )
        }
    }
}

@Composable
private fun PopUpBox(
    isVisible: Boolean,
    popupPositionProvider: PopupPositionProvider,
    onDismissRequest: () -> Unit,
    content: @Composable (LayoutCoordinates?) -> Unit,
    anchor: @Composable () -> Unit

) {
    var anchorBounds: LayoutCoordinates? by remember { mutableStateOf(null) }

    val wrappedAnchor: @Composable () -> Unit = {
        Box(
            modifier = Modifier.onGloballyPositioned { anchorBounds = it }
        ) {
            anchor()
        }
    }

    Box {
        if (isVisible) {
            Popup(
                properties = PopupProperties(clippingEnabled = true),
                popupPositionProvider = popupPositionProvider,
                onDismissRequest = onDismissRequest
            ) {
                content(anchorBounds)
            }
        }

        Box {
            wrappedAnchor()
        }
    }
}

@Composable
private fun AnchorContent(
    modifier: Modifier
) {
    // This is anchor for Popup
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Info,
        contentDescription = null
    )
}

class AlignmentPopupPositionProvider(
    val offset: IntOffset,
    val popupState: PopupState,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {

        val alignment = popupState.alignment

        val anchorAlignmentPoint = alignment.align(
            IntSize.Zero,
            anchorBounds.size,
            layoutDirection
        )

        // Note the negative sign. Popup alignment point contributes negative offset.
        val popupAlignmentPoint = -alignment.align(
            IntSize.Zero,
            popupContentSize,
            layoutDirection
        )

        val resolvedUserOffset = IntOffset(
            offset.x * (if (layoutDirection == LayoutDirection.Ltr) 1 else -1),
            offset.y
        )

        var calculatedOffset = anchorBounds.topLeft +
                anchorAlignmentPoint +
                popupAlignmentPoint +
                resolvedUserOffset

        println(
            "😹 PopupPositionProvider " +
                    "anchorBounds: $anchorBounds, " +
                    "anchorAlignmentPoint: $anchorAlignmentPoint, " +
                    "popupAlignmentPoint: ${popupAlignmentPoint}\n" +
                    "resolvedUserOffset: ${resolvedUserOffset}, " +
                    "calculatedOffset: $calculatedOffset, " +
                    "popupContentSize: $popupContentSize"
        )

        // TODO Get statusBarHeight from Window insets
        val statusBarHeight = 150
        var verticalBias = (alignment as BiasAlignment).verticalBias

        if (alignment.topAlignment()) {
            var popupTop = anchorBounds.top - popupContentSize.height - resolvedUserOffset.y

            if (popupTop < statusBarHeight) {
                popupTop = anchorBounds.bottom + resolvedUserOffset.y
                verticalBias = -1f
            } else {
                verticalBias = 1f
            }
            calculatedOffset = IntOffset(calculatedOffset.x, popupTop)

        } else if (alignment.bottomAlignment()) {
            var popupTop = anchorBounds.bottom + resolvedUserOffset.y

            if (popupTop > windowSize.height - popupContentSize.height - resolvedUserOffset.y) {
                popupTop = anchorBounds.top - resolvedUserOffset.y - popupContentSize.height
                verticalBias = 1f
            } else {
                verticalBias = -1f
            }

            calculatedOffset = IntOffset(calculatedOffset.x, popupTop)
        }

        val popupAlignment = popupState.caretAlignment
        (popupAlignment as? BiasAlignment)?.let {
            popupState.caretAlignment = BiasAlignment(
                horizontalBias = popupAlignment.horizontalBias,
                verticalBias = verticalBias
            )
        }

        popupState.contentRect = IntRect(
            offset = calculatedOffset,
            size = popupContentSize
        )

        popupState.windowSize = windowSize

        println(
            "FINAL Calculated offset: $calculatedOffset, " +
                    "popupContentSize: $popupContentSize, " +
                    "windowSize: $windowSize"
        )
        return calculatedOffset
    }
}

fun Alignment.topAlignment() = this ==
        Alignment.TopStart ||
        this == Alignment.TopCenter ||
        this == Alignment.TopEnd

fun Alignment.bottomAlignment() = this ==
        Alignment.BottomStart ||
        this == Alignment.BottomCenter ||
        this == Alignment.BottomEnd


@Stable
class PopupState(
    val alignment: Alignment,
    offset: IntOffset
) {
    var caretAlignment by mutableStateOf(alignment)
        internal set

    var offset by mutableStateOf(offset)
    var contentRect by mutableStateOf(IntRect.Zero)
    var windowSize by mutableStateOf(IntSize.Zero)
}