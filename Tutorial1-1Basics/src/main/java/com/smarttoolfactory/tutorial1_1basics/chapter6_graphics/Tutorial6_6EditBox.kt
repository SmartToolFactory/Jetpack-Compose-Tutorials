package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.pointerMotionEvents
import kotlin.math.sqrt

@Composable
fun Tutorial6_6Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(Color(0xff424242))
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Column(modifier = Modifier.fillMaxSize()) {

            var text by remember { mutableStateOf("") }

            val density = LocalDensity.current
            val size = (1000 / density.density).dp

            EditBox(
                Modifier.size(size),
                onTextChange = {
                    text = it
                }
            ) {
                Image(
                    modifier = Modifier
                        .size(size),
                    painter = painterResource(id = R.drawable.landscape1),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = ""
                )
            }

            Text(text, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun EditBox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTextChange: (String) -> Unit,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    var xScale by remember { mutableStateOf(1f) }
    var yScale by remember { mutableStateOf(1f) }

    var xTranslation by remember { mutableStateOf(0f) }
    var yTranslation by remember { mutableStateOf(0f) }

    var touchRegion by remember { mutableStateOf(TouchRegion.None) }
    val touchRegionWidth = LocalDensity.current.density* 8

//    var offset by remember { mutableStateOf(IntOffset.Zero) }
    var positionInParent by remember { mutableStateOf(Offset.Zero) }

    var positionRaw by remember { mutableStateOf(Offset.Zero) }
    var positionScaled by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier) {

        val rectBounds by remember {
            mutableStateOf(
                Rect(
                    offset = Offset.Zero,
                    size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
                )
            )
        }

        var size by remember {
            mutableStateOf(
                IntSize(
                    constraints.maxWidth,
                    constraints.maxHeight
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
                    size = Size(size.width.toFloat(), size.height.toFloat())
                )
            )
        }

        val editModifier = if (enabled) {
            Modifier
        } else {
            Modifier
        }
            .drawAnimatedDashRectBorder(rect = rectDraw)
            .drawWithContent {
                drawContent()
                if (rectDraw != Rect.Zero) {
                    val radius = touchRegionWidth

//                    drawLine(
//                        Color.Red,
//                        start = Offset(size.width / 4f, 0f),
//                        end = Offset(size.width / 4f, size.height.toFloat()),
//                        strokeWidth = 5f
//                    )
//
//                    drawLine(
//                        Color.Red,
//                        start = Offset(size.width / 2f, 0f),
//                        end = Offset(size.width / 2f, size.height.toFloat()),
//                        strokeWidth = 5f
//                    )
//
//                    drawLine(
//                        Color.Red,
//                        start = Offset(size.width * 3f / 4f, 0f),
//                        end = Offset(size.width * 3f / 4f, size.height.toFloat()),
//                        strokeWidth = 5f
//                    )
//                    drawRect(
//                        Color.Yellow,
//                        topLeft = rectBounds.topLeft,
//                        size = rectBounds.size,
//                        style = Stroke(1.dp.toPx())
//                    )
//
//                    drawRect(
//                        Color.Blue,
//                        topLeft = rectTemp.topLeft,
//                        size = rectTemp.size,
//                        style = Stroke(5.dp.toPx())
//                    )
//
//                    drawRect(
//                        Color.White,
//                        topLeft = rectDraw.topLeft,
//                        size = rectDraw.size,
//                        style = Stroke(2.dp.toPx())
//                    )
                    drawBorderCircle(radius = radius, center = rectDraw.topLeft)
                    drawBorderCircle(radius = radius, center = rectDraw.topRight)
                    drawBorderCircle(radius = radius, center = rectDraw.bottomLeft)
                    drawBorderCircle(radius = radius, center = rectDraw.bottomRight)

                    drawCircle(color = Color.Cyan, radius = 15f, center = positionRaw)
                    drawCircle(color = Color.Magenta, radius = 15f, center = positionScaled)

                }
            }
            .graphicsLayer {
                translationX = xTranslation
                translationY = yTranslation
                scaleX = xScale
                scaleY = yScale
                transformOrigin = TransformOrigin(0f, 0f)
            }

            .pointerMotionEvents(Unit,
                onDown = { change: PointerInputChange ->

                    rectTemp = rectDraw.copy()

                    positionRaw = change.position

                    val scaledX =
                        rectDraw.left + positionRaw.x * rectDraw.width / rectBounds.width
                    val scaledY =
                        rectDraw.top + positionRaw.y * rectDraw.height / rectBounds.height

                    positionScaled = Offset(scaledX, scaledY)

                    val translatedRect = Rect(offset = Offset.Zero, size = rectTemp.size)

                    touchRegion = getTouchRegion(
                        position = positionScaled,
                        rect = rectDraw,
                        threshold = touchRegionWidth * 4
                    )

                    println("✊ onDown() positionRaw: $positionRaw, touchRegion: $touchRegion, translatedRect: $translatedRect")

                    onTextChange(
                        "✊ onDown() region: $touchRegion\n" +
                                "positionRaw: $positionRaw\n" +
                                "positionChange: ${change.positionChange()}\n" +
                                "positionInParent: $positionInParent\n" +
                                "size: $size\n" +
                                "RECT translatedRect: $translatedRect\n" +
                                "RECT DRAW: $rectDraw\n" +
                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
                                "RECT TEMP: $rectTemp\n" +
                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
                                "xScale: $xScale, yScale: $yScale\n" +
                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
                    )

                    Toast
                        .makeText(
                            context,
                            "Clicked position: ${change.position}",
                            Toast.LENGTH_SHORT
                        )
                        .show()

                },
                onMove = { change: PointerInputChange ->

                    val position = change.position
                    positionRaw = position
                    val scaledX = rectDraw.left + position.x * rectDraw.width / rectBounds.width
                    val scaledY = rectDraw.top + position.y * rectDraw.height / rectBounds.height
                    positionScaled = Offset(scaledX, scaledY)

                    when (touchRegion) {
                        TouchRegion.TopLeft -> {

                            rectDraw = Rect(
                                left = scaledX,
                                top = scaledY,
                                right = rectTemp.right,
                                bottom = rectTemp.bottom,
                            )

                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height
                            xTranslation = scaledX / 1f
                            yTranslation = scaledY / 1f

                        }

                        TouchRegion.BottomLeft -> {

                            rectDraw = Rect(
                                left = scaledX,
                                top = rectTemp.top,
                                right = rectTemp.right,
                                bottom = scaledY,
                            )

                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height
                            xTranslation = scaledX / 1f
                            yTranslation = scaledY / 1f - rectDraw.height


                        }

                        TouchRegion.TopRight -> {
                            rectDraw = Rect(
                                left = rectTemp.left,
                                top = scaledY,
                                right = scaledX,
                                bottom = rectTemp.bottom,
                            )

                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = scaledX / 1f - rectDraw.width
                            yTranslation = scaledY / 1f
                        }

                        TouchRegion.BottomRight -> {
                            rectDraw = Rect(
                                left = rectTemp.left,
                                top = rectTemp.top,
                                right = scaledX,
                                bottom = scaledY,
                            )

                            xScale = rectDraw.width / rectBounds.width
                            yScale = rectDraw.height / rectBounds.height

                            xTranslation = scaledX / 1f - rectDraw.width
                            yTranslation = scaledY / 1f - rectDraw.height
                        }

                        else -> {

                        }
                    }

                    if (touchRegion != TouchRegion.None) {
                        change.consume()
                    }

                    onTextChange(
                        "🚀 onMove() region: $touchRegion\n" +
                                "position: $position\n" +
                                "scaledX: $scaledX, scaledY: $scaledY\n" +
                                "positionChange: ${change.positionChange()}\n" +
                                "positionScaled: $positionScaled\n" +
                                "positionInParent: $positionInParent\n" +
                                "RECT DRAW: $rectDraw\n" +
                                "width: ${rectDraw.width}, height: ${rectDraw.height}\n" +
                                "RECT TEMP: $rectTemp\n" +
                                " width: ${rectTemp.width.toInt()}, height: ${rectTemp.height.toInt()}\n" +
                                "xScale: $xScale, yScale: $yScale\n" +
                                "xTranslation: $xTranslation, yTranslation: $yTranslation\n\n"
                    )

                    println(
                        "🚀 onMove() region: $touchRegion\n" +
                                "position: $position\n" +
                                "scaledX: $scaledX, scaledY: $scaledY\n" +
                                "positionChange: ${change.positionChange()}\n" +
                                "positionScaled: $positionScaled\n" +
                                "positionInParent: $positionInParent\n" +
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

                    println("😜 onUp() rectTemp: $rectTemp")
                }
            )
        Box(
            editModifier


        ) {
            content()
        }
    }
}

private fun getTouchRegion(
    position: Offset,
    rect: Rect,
    threshold: Float
): TouchRegion {
    return when {

        inDistance(
            position,
            rect.topLeft, threshold
        ) -> TouchRegion.TopLeft
        inDistance(
            position,
            rect.topRight,
            threshold
        ) -> TouchRegion.TopRight
        inDistance(
            position,
            rect.bottomLeft,
            threshold
        ) -> TouchRegion.BottomLeft
        inDistance(
            position,
            rect.bottomRight,
            threshold
        ) -> TouchRegion.BottomRight
        rect.contains(offset = position) -> TouchRegion.Inside
        else -> TouchRegion.None
    }
}


private fun inDistance(offset1: Offset, offset2: Offset, target: Float): Boolean {
    val x1 = offset1.x
    val y1 = offset1.y

    val x2 = offset2.x
    val y2 = offset2.y

    val distance = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    return distance < target
}

enum class TouchRegion {
    TopLeft, TopRight, BottomLeft, BottomRight, Inside, None
}

private fun DrawScope.drawBorderCircle(
    radius: Float,
    center: Offset
) {
    drawCircle(color = blue, radius = radius, center = center)
    drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(1.dp.toPx()))
}

val blue = Color(0xff2196F3)

/**
 * Composed [Modifier] that draws animated dashed border with [Modifier.drawWithContent]
 *
 * @param width width of the border in dp
 * @param shape shape of the border
 * @param durationMillis duration of the animation spec
 * @param intervals [FloatArray] with 2 elements that contain on(first), and off(second) interval
 * @param animatedColor color that is animated with [InfiniteTransition]
 * @param staticColor this color is drawn behind the [animatedColor] color to act as layout
 * for animated color
 */
fun Modifier.drawAnimatedDashBorder(
    width: Dp = 2.dp,
    shape: Shape = RectangleShape,
    durationMillis: Int = 500,
    intervals: FloatArray = floatArrayOf(20f, 20f),
    animatedColor: Color = Color.Black,
    staticColor: Color = Color.White

) = composed(
    inspectorInfo = debugInspectorInfo {
        // name should match the name of the modifier
        name = "drawAnimatedDashBorder"
        // add name and value of each argument
        properties["width"] = width
        properties["shape"] = shape
        properties["durationMillis"] = durationMillis
        properties["intervals"] = intervals
        properties["animatedColor"] = animatedColor
        properties["staticColor"] = staticColor
    },

    factory = {

        require(intervals.size == 2) {
            "There should be on and off values in intervals array"
        }

        val density = LocalDensity.current

        val transition: InfiniteTransition = rememberInfiniteTransition()

        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = (4 * intervals.average()).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val pathEffect = PathEffect.dashPathEffect(
            intervals = intervals,
            phase = phase
        )
        // add your modifier implementation here
        this.then(
            Modifier
                .drawWithContent {
                    drawContent()
                    val outline: Outline =
                        shape.createOutline(
                            size,
                            layoutDirection = layoutDirection,
                            density = density
                        )

                    drawOutline(
                        outline = outline, color = staticColor, style = Stroke(
                            width = width.toPx()
                        )
                    )
                    drawOutline(
                        outline = outline, color = animatedColor, style = Stroke(
                            width = width.toPx(),
                            pathEffect = pathEffect
                        )
                    )
                }
        )
    }
)

/**
 * Composed [Modifier] that draws animated dashed border with [Modifier.drawWithContent]
 *
 * @param width width of the border in dp
 * @param rect that covers border we draw
 * @param durationMillis duration of the animation spec
 * @param intervals [FloatArray] with 2 elements that contain on(first), and off(second) interval
 * @param animatedColor color that is animated with [InfiniteTransition]
 * @param staticColor this color is drawn behind the [animatedColor] color to act as layout
 * for animated color
 */
fun Modifier.drawAnimatedDashRectBorder(
    width: Dp = 2.dp,
    rect: Rect,
    durationMillis: Int = 500,
    intervals: FloatArray = floatArrayOf(20f, 20f),
    animatedColor: Color = Color.Black,
    staticColor: Color = Color.White

) = composed(
    inspectorInfo = debugInspectorInfo {
        // name should match the name of the modifier
        name = "drawAnimatedDashBorder"
        // add name and value of each argument
        properties["width"] = width
        properties["rect"] = rect
        properties["durationMillis"] = durationMillis
        properties["intervals"] = intervals
        properties["animatedColor"] = animatedColor
        properties["staticColor"] = staticColor
    },

    factory = {

        require(intervals.size == 2) {
            "There should be on and off values in intervals array"
        }

        val density = LocalDensity.current

        val transition: InfiniteTransition = rememberInfiniteTransition()

        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = (4 * intervals.average()).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val pathEffect = PathEffect.dashPathEffect(
            intervals = intervals,
            phase = phase
        )
        // add your modifier implementation here
        this.then(
            Modifier
                .drawWithContent {

                    drawRect(
                        topLeft = rect.topLeft,
                        size = rect.size,
                        color = staticColor,
                        style = Stroke(
                            width = width.toPx()
                        )
                    )
                    drawRect(
                        topLeft = rect.topLeft,
                        size = rect.size,
                        color = animatedColor,
                        style = Stroke(
                            width = width.toPx(),
                            pathEffect = pathEffect
                        )
                    )

                    drawContent()
                }
        )
    }
)

