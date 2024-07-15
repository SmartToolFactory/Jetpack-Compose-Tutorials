package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
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
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor

@Preview
@Composable
private fun PopupTest2() {
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
    Box(
        modifier = Modifier.fillMaxSize().border(2.dp, Color.Red)
    ) {

        var paddingStart by remember {
            mutableFloatStateOf(200f)
        }

        var paddingTop by remember {
            mutableFloatStateOf(0f)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {

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
                valueRange = 0f..750f
            )
        }

        PopupSample(
            modifier = Modifier.padding(
                start = paddingStart.dp,
                top = paddingTop.dp
            )
        )
    }
}

@Composable
private fun PopupSample(modifier: Modifier = Modifier) {
    Box(modifier) {
        val density = LocalDensity.current

        val popupState = remember {
            CustomPopupState(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, with(density) { 16.dp.roundToPx() })
            )
        }

        PopUpBox(
            onDismissRequest = {

            },
            popupPositionProvider = PopupPositionProvider(
                offset = IntOffset(0, with(density) { 16.dp.roundToPx() }),
                popupState = popupState
            ),
            popupState = popupState,
            anchor = {
                AnchorContent(
                    modifier = Modifier
                        .border(2.dp, Color.Blue)
                        .clickable {
                            popupState.show()

                        }
                )
            },
            content = { anchorLayoutCoordinates: LayoutCoordinates? ->
                PlainPopupContent(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .border(2.dp, Color.Cyan)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    anchorLayoutCoordinates = anchorLayoutCoordinates,
                    popupState = popupState,
                    caretProperties = CaretProperties(
                        caretWidth = 24.dp,
                        caretHeight = 16.dp
                    ),
                ) {

                    SideEffect {
                        println("😵‍💫CONTENT RECOMPOSING...")
                    }
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

@Composable
private fun PlainPopupContent(
    modifier: Modifier = Modifier,
    popupState: CustomPopupState,
    caretProperties: CaretProperties,
    anchorLayoutCoordinates: LayoutCoordinates?,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .drawCaret(caretProperties, anchorLayoutCoordinates, popupState)
            .then(modifier)
    ) {
        content()
    }
}

@Composable
private fun PopUpBox(
    popupState: CustomPopupState,
    popupPositionProvider: PopupPositionProvider,
    onDismissRequest: () -> Unit = {},
    content: @Composable (LayoutCoordinates?) -> Unit,
    anchor: @Composable () -> Unit
) {
    var anchorBounds: LayoutCoordinates? by remember { mutableStateOf(null) }

    val wrappedAnchor: @Composable () -> Unit = {
        Box(
            modifier = Modifier.onGloballyPositioned {
                anchorBounds = it
                // Get statusBar height
                popupState.statusBarHeight = it.boundsInWindow().top - it.boundsInRoot().top
            }
        ) {
            anchor()
        }
    }

    Box {
        if (popupState.visible) {
            Popup(
                properties = PopupProperties(
                    // This makes sure other composables don't receive gestures
                    focusable = true
                ),
                popupPositionProvider = popupPositionProvider,
                onDismissRequest = {
                    popupState.dismiss()
                    onDismissRequest.invoke()
                }
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

class PopupPositionProvider(
    val offset: IntOffset,
    val popupState: CustomPopupState,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {

        val alignment = popupState.alignment

        // Anchor bounds are boundsInWindow because of that even at (0,0) in root it's not 0 for y
        // it returns y position + status bar

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

        val statusBarHeight = popupState.statusBarHeight.toInt()
        var verticalBias = (alignment as BiasAlignment).verticalBias

        if (alignment.topAlignment) {

            var popupTop = anchorBounds.top - popupContentSize.height - resolvedUserOffset.y

            // Popup is set on top but Popup collides with statusBar or doesn't have
            // enough space on top
            if (popupTop < statusBarHeight) {
                // new position is below anchor point after caret position
                popupTop = anchorBounds.bottom + resolvedUserOffset.y
                // Caret alignment to bottom because Popup is below anchor
                verticalBias = 1f
            } else {
                // Caret alignment to top because Popup is above anchor
                verticalBias = -1f
            }
            calculatedOffset = IntOffset(calculatedOffset.x, popupTop)

        } else if (alignment.bottomAlignment) {
            var popupTop = anchorBounds.bottom + resolvedUserOffset.y

            if (popupTop - statusBarHeight > windowSize.height - popupContentSize.height) {
                popupTop =
                    anchorBounds.top - resolvedUserOffset.y - popupContentSize.height
                verticalBias = -1f
            } else {
                verticalBias = 1f
            }

            calculatedOffset = IntOffset(calculatedOffset.x, popupTop)
        }

        val contentRect = IntRect(
            offset = calculatedOffset,
            size = popupContentSize
        )

        val popupAlignment = popupState.popupAlignment
        (popupAlignment as? BiasAlignment)?.let {
            popupState.popupAlignment = BiasAlignment(
                horizontalBias = popupAlignment.horizontalBias,
                verticalBias = verticalBias
            )
        }
        popupState.contentRect = contentRect
        popupState.windowSize = windowSize

        popupState.calculateCaretOffset(
            screenWidth = windowSize.width,
            anchorBounds = anchorBounds,
            popupAlignment = popupState.popupAlignment,
            contentRect = contentRect
        )

        // 🔥 This is the calculation PlainTooltip positionProvider uses
        // just for cross checking for center alignment
        val xPos = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2

        println(
            "FINAL Calculated offset: $calculatedOffset, xPos: $xPos, " +
                    "popupContentSize: $popupContentSize, " +
                    "windowSize: $windowSize, " +
                    "topAlignment: ${popupState.popupAlignment.topAlignment}, " +
                    "bottomAlignment: ${popupState.popupAlignment.bottomAlignment}"
        )
        return calculatedOffset
    }
}

@Stable
class CustomPopupState(
    val alignment: Alignment,
    offset: IntOffset,
    initialIsVisible: Boolean = false,
) {

    var visible by mutableStateOf(initialIsVisible)
        private set

    var popupAlignment by mutableStateOf(alignment)
        internal set

    var offset by mutableStateOf(offset)
    var contentRect by mutableStateOf(IntRect.Zero)
    var caretTopLeft by mutableStateOf(IntOffset.Zero)
    var windowSize by mutableStateOf(IntSize.Zero)

    var statusBarHeight: Float = 0f

    internal fun calculateCaretOffset(
        screenWidth: Int,
        popupAlignment: Alignment,
        contentRect: IntRect,
        anchorBounds: IntRect
    ) {

        val tooltipRect = contentRect

        val tooltipWidth = contentRect.size.width
        val tooltipHeight = contentRect.size.height
        val tooltipLeft = tooltipRect.left
        val tooltipRight = tooltipRect.right
        val tooltipCenterX = tooltipRect.center.x

        val anchorMid = anchorBounds.center.x

        val caretX =
        // Popup is positioned left but might overflow from left
            // if clip is enabled
            (if (tooltipLeft <= 0) {
                anchorMid

                // pop is center of the screen neither touches right or left
                // side of the screen
            } else if (tooltipRight <= screenWidth) {
                tooltipWidth / 2 + anchorMid - tooltipCenterX

                // Popup is positioned right but might overflow from right
                // if clip is enabled
            } else {
                val diff = tooltipRight - screenWidth
                anchorMid - tooltipLeft + diff
            }
                    ).toInt()

        val caretY = if (popupAlignment.bottomAlignment) {
            0
        } else {
            tooltipHeight
        }

        val position = IntOffset(caretX, caretY)

        println("PopupState calculateCaretRect() $position")

        caretTopLeft = position
    }

    fun show() {
        visible = true
    }

    fun dismiss() {
        visible = false
    }
}

private fun Modifier.drawCaret(
    caretProperties: CaretProperties,
    anchorLayoutCoordinates: LayoutCoordinates?,
    popupState: CustomPopupState
) = this.then(
    Modifier
        .drawWithCache {
            val caretWidthPx = caretProperties.caretWidth.toPx()
            val caretHeightPx = caretProperties.caretHeight.toPx()

            val path = Path()

            anchorLayoutCoordinates?.boundsInWindow()
                ?.let { rect: Rect ->

                    val screenWidth = popupState.windowSize.width
                    val popupAlignment = popupState.popupAlignment
                    val tooltipRect = popupState.contentRect

                    val tooltipWidth = size.width
                    val tooltipHeight = size.height
                    val tooltipLeft = tooltipRect.left
                    val tooltipRight = tooltipRect.right
                    val tooltipCenterX = tooltipRect.center.x

                    val anchorMid = rect.center.x

                    val caretHalfWidth = caretWidthPx / 2

                    val caretX =
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

                    val caretY = if (popupAlignment.bottomAlignment) {
                        0f
                    } else {
                        tooltipHeight
                    }

                    val position = Offset(caretX, caretY)

                    path.apply {
                        println(
                            "DRAW with CACHE anchor " +
                                    "rect: $rect, " +
                                    "popupContentRect:$tooltipRect, " +
                                    "carePosition: $position"
                        )

                        if (popupAlignment.bottomAlignment) {
                            moveTo(
                                position.x,
                                position.y
                            )
                            lineTo(
                                position.x + caretHalfWidth,
                                -caretHeightPx
                            )
                            lineTo(
                                position.x + caretWidthPx,
                                position.y
                            )
                        } else if (popupAlignment.topAlignment) {
                            moveTo(position.x, position.y)
                            lineTo(
                                position.x + caretHalfWidth,
                                position.y + caretHeightPx
                            )

                            lineTo(
                                position.x + caretWidthPx,
                                position.y
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

                drawRect(
                    color = Color.Green,
                    topLeft = popupState.contentRect.topLeft.toOffset(),
                    size = popupState.contentRect.size.toSize(),
                    style = Stroke(2.dp.toPx())
                )

                drawCircle(
                    color = Color.Red,
                    radius = 10f,
                    center = popupState.caretTopLeft.toOffset()
                )
            }
        }
)