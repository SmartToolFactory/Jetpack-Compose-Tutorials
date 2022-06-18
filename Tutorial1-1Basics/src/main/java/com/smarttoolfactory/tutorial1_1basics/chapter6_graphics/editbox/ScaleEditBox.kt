package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.editbox

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import kotlin.math.abs


@Composable
fun ScaleEditBox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    onTextChange: (String) -> Unit,
    content: @Composable () -> Unit
) {

    val transition: InfiniteTransition = rememberInfiniteTransition()

    // Infinite phase animation for PathEffect
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    var xScale by remember { mutableStateOf(1f) }
    var yScale by remember { mutableStateOf(1f) }

    var xTranslation by remember { mutableStateOf(0f) }
    var yTranslation by remember { mutableStateOf(0f) }

    var angle by remember { mutableStateOf(0f) }

    var touchRegion by remember { mutableStateOf(TouchRegion.None) }
    val touchRegionRadius = with(LocalDensity.current) {
        handleRadius.toPx()
    }

    // This is the real position of touch in screen, when scaled
    // right bottom corner of 1000x1000px Composable is always (1000,1000)
    // but we need screen coordinated to set draw rectangle after scaling and translating
    // rectDraw is drawn based on touch position on screen
    var positionActual by remember { mutableStateOf(Offset.Zero) }

    // Touch position for edge of the rectangle, used for not jumping to edge of rectangle
    // when user moves a handle. We set positionActual as position of selected handle
    // and using this distance as offset to not have a jump from touch position

    var distanceToEdgeFromTouch by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier.wrapContentSize()) {

        val rectBounds by remember {
            mutableStateOf(
                Rect(
                    offset = Offset.Zero,
                    size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
                )
            )
        }

        var rectDraw by remember {
            mutableStateOf(rectBounds)
        }

        var rectTemp by remember {
            mutableStateOf(
                Rect(
                    offset = Offset.Zero,
                    size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
                )
            )
        }

        val editModifier = if (enabled) {
            Modifier
        } else {
            Modifier
        }
            .graphicsLayer {
                translationX = xTranslation
                translationY = yTranslation
                scaleX = xScale
                scaleY = yScale
                rotationZ = angle
            }

            .pointerMotionEvents(Unit,
                onDown = { change: PointerInputChange ->

                    rectTemp = rectDraw.copy()

                    val position = change.position

                    val scaledX = rectDraw.left + position.x * rectDraw.width / rectBounds.width
                    val scaledY = rectDraw.top + position.y * rectDraw.height / rectBounds.height

                    positionActual = Offset(scaledX, scaledY)

                    touchRegion = getTouchRegion(
                        position = positionActual,
                        rect = rectDraw,
                        threshold = touchRegionRadius * 2
                    )

                    // This is the difference between touch position and edge
                    // This is required for not moving edge of draw rect to touch position on move
                    when (touchRegion) {
                        TouchRegion.TopLeft -> {
                            distanceToEdgeFromTouch = rectTemp.topLeft - positionActual
                        }
                        TouchRegion.TopRight -> {
                            distanceToEdgeFromTouch = rectTemp.topRight - positionActual
                        }
                        TouchRegion.BottomLeft -> {
                            distanceToEdgeFromTouch = rectTemp.bottomLeft - positionActual
                        }
                        TouchRegion.BottomRight -> {
                            distanceToEdgeFromTouch = rectTemp.bottomRight - positionActual
                        }
                        else -> {
                            distanceToEdgeFromTouch = Offset.Zero
                        }
                    }

                    println(
                        "✊ onDown() region: $touchRegion\n" +
                                "position: $position\n" +
                                "positionActual: $positionActual\n" +
                                "scaledX: $scaledX, scaledY: $scaledY\n" +
                                "distanceToEdgeFromTouch: $distanceToEdgeFromTouch\n" +
                                "RECT DRAW: $rectDraw\n" +
                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
                                "RECT TEMP: $rectTemp\n" + "" +
                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
                                "xScale: $xScale, yScale: $yScale\n" +
                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
                    )

                    onTextChange(
                        "✊ onDown() region: $touchRegion\n" +
                                "position: $position\n" +
                                "positionActual: $positionActual\n" +
                                "scaledX: $scaledX, scaledY: $scaledY\n" +
                                "distanceToEdgeFromTouch: $distanceToEdgeFromTouch\n" +
                                "RECT DRAW: $rectDraw\n" +
                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
                                "RECT TEMP: $rectTemp\n" + "" +
                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
                                "xScale: $xScale, yScale: $yScale\n" +
                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
                    )

                },
                onMove = { change: PointerInputChange ->

                    val position = change.position
                    val scaledX = rectDraw.left + position.x * rectDraw.width / rectBounds.width +
                            distanceToEdgeFromTouch.x
                    val scaledY = rectDraw.top + position.y * rectDraw.height / rectBounds.height +
                            distanceToEdgeFromTouch.y


                    when (touchRegion) {
                        TouchRegion.TopLeft -> {

                            // Set position of top left while moving with top left handle and
                            // limit position to not intersect other handles
                            positionActual = Offset(
                                scaledX.coerceAtMost(rectTemp.right - touchRegionRadius * 4),
                                scaledY.coerceAtMost(rectTemp.bottom - touchRegionRadius * 4)
                            )

                            rectDraw = Rect(
                                left = positionActual.x,
                                top = positionActual.y,
                                right = rectTemp.right,
                                bottom = rectTemp.bottom
                            )

                            // TransformOrion is center of composable, by changing
                            // it to 0,0 only for translation we move the composable
                            // from left and top
                            val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                            val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                            // Scale is ratio of
                            // rectangle drawn(It's the outer rectangle that covers handles)
                            // to initial rectangle of this Composable
                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = positionActual.x + horizontalCenter
                            yTranslation = positionActual.y + verticalCenter
                        }

                        TouchRegion.BottomLeft -> {

                            // Set position of top left while moving with bottom left handle and
                            // limit position to not intersect other handles
                            positionActual = Offset(
                                scaledX.coerceAtMost(rectTemp.right - touchRegionRadius * 4),
                                scaledY.coerceAtLeast(rectTemp.top + touchRegionRadius * 4)
                            )

                            rectDraw = Rect(
                                left = positionActual.x,
                                top = rectTemp.top,
                                right = rectTemp.right,
                                bottom = positionActual.y,
                            )

                            // TransformOrion is center of composable, by changing
                            // it to 0,0 only for translation we move the composable
                            // from left and bottom
                            val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                            val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                            // Scale is ratio of
                            // rectangle drawn(It's the outer rectangle that covers handles)
                            // to initial rectangle of this Composable
                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = positionActual.x + horizontalCenter
                            yTranslation = positionActual.y - rectDraw.height + verticalCenter
                        }

                        TouchRegion.TopRight -> {

                            // Set position of top left while moving with top right handle and
                            // limit position to not intersect other handles
                            positionActual = Offset(
                                scaledX.coerceAtLeast(rectTemp.left + touchRegionRadius * 4),
                                scaledY.coerceAtMost(rectTemp.bottom - touchRegionRadius * 4)
                            )

                            rectDraw = Rect(
                                left = rectTemp.left,
                                top = positionActual.y,
                                right = positionActual.x,
                                bottom = rectTemp.bottom,
                            )

                            // TransformOrion is center of composable, by changing
                            // it to 0,0 only for translation we move the composable
                            // from top and right
                            val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                            val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                            // Scale is ratio of
                            // rectangle drawn(It's the outer rectangle that covers handles)
                            // to initial rectangle of this Composable
                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = positionActual.x - rectDraw.width + horizontalCenter
                            yTranslation = positionActual.y + verticalCenter
                        }

                        TouchRegion.BottomRight -> {

                            // Set position of top left while moving with bottom right handle and
                            // limit position to not intersect other handles
                            positionActual = Offset(
                                scaledX.coerceAtLeast(rectTemp.left + touchRegionRadius * 4),
                                scaledY.coerceAtLeast(rectTemp.top + touchRegionRadius * 4)
                            )

                            rectDraw = Rect(
                                left = rectTemp.left,
                                top = rectTemp.top,
                                right = positionActual.x,
                                bottom = positionActual.y,
                            )

                            // TransformOrion is center of composable, by changing
                            // it to 0,0 only for translation we move the composable
                            // from right and bottom
                            val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                            val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                            // Scale is ratio of
                            // rectangle drawn(It's the outer rectangle that covers handles)
                            // to initial rectangle of this Composable
                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = positionActual.x - rectDraw.width + horizontalCenter
                            yTranslation = positionActual.y - rectDraw.height + verticalCenter
                        }

                        TouchRegion.Inside -> {
                            val drag = change.positionChange()

                            val scaledDragX = drag.x * rectDraw.width / rectBounds.width
                            val scaledDragY = drag.y * rectDraw.height / rectBounds.height

                            xTranslation += scaledDragX
                            yTranslation += scaledDragY

                            rectDraw = rectDraw.translate(scaledDragX, scaledDragY)
                        }

                        else -> Unit
                    }

                    if (touchRegion != TouchRegion.None) {
                        change.consume()
                    }

//                    onTextChange(
//                        "🚀 onMove() region: $touchRegion\n" +
//                                "position: $position\n" +
//                                "scaledX: $scaledX, scaledY: $scaledY\n" +
//                                "positionChange: ${change.positionChange()}\n" +
//                                "positionScaled: $positionScaled\n" +
//                                "RECT DRAW: $rectDraw\n" +
//                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
//                                "RECT TEMP: $rectTemp\n" +
//                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
//                                "xScale: $xScale, yScale: $yScale\n" +
//                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
//                    )

                    println(
                        "🚀 onMove() region: $touchRegion\n" +
                                "position: $position\n" +
                                "scaledX: $scaledX, scaledY: $scaledY\n" +
                                "positionChange: ${change.positionChange()}\n" +
                                "positionScaled: $positionActual\n" +
                                "RECT DRAW: $rectDraw\n" +
                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
                                "RECT TEMP: $rectTemp\n" +
                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
                                "xScale: $xScale, yScale: $yScale\n" +
                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
                    )

                },
                onUp = {
                    touchRegion = TouchRegion.None
                    rectTemp = rectDraw.copy()
                }
            )
            // Padding is required to keep touch position of handles inside composable
            // without padding only 1 quarter of corner handles are in composable
            // padding is scaled because scale change also changes padding dimensions
            .padding(
                horizontal = handleRadius / abs(xScale),
                vertical = handleRadius / abs(yScale)
            )

        Box(
            editModifier
        ) {
            content()
        }

        EditBoxImpl(
            modifier = Modifier,
            radius = touchRegionRadius,
            rectDraw = rectDraw,
            phase = phase
        )
    }
}

@Composable
private fun EditBoxImpl(
    modifier: Modifier,
    radius: Float,
    rectDraw: Rect,
    phase: Float,
) {
    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 20f),
        phase = phase
    )

    // This rect what we get after offsetting inside padding area to draw actual composable
    val rect = Rect(
        topLeft = Offset(
            rectDraw.topLeft.x + radius,
            rectDraw.topLeft.y + radius
        ),
        bottomRight = Offset(
            rectDraw.bottomRight.x - radius,
            rectDraw.bottomRight.y - radius
        )
    )

    androidx.compose.foundation.Canvas(modifier = modifier) {


        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.White,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.Black,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = pathEffect
            )
        )

        drawBorderCircle(
            radius = radius,
            center = rect.topLeft
        )
        drawBorderCircle(
            radius = radius,
            center = rect.topRight
        )
        drawBorderCircle(
            radius = radius,
            center = rect.bottomLeft
        )
        drawBorderCircle(
            radius = radius,
            center = rect.bottomRight
        )
    }
}

private fun DrawScope.drawBorderCircle(
    radius: Float,
    center: Offset
) {
    drawCircle(color = Color.White.copy(alpha = .7f), radius = radius, center = center)
    drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(1.dp.toPx()))
}


