package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun ArcSliderSample() {
    Column(
        modifier = Modifier
            .fillMaxSize().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var value by remember {
            mutableFloatStateOf(0f)
        }

        val density = LocalDensity.current
        val width = with(density) {
            1000f.toDp()
        }

        Column(
            modifier = Modifier.border(2.dp, Color.Black).width(width).fillMaxHeight()
        ) {
            Box {
                ArcSlider(
                    modifier = Modifier
                        .width(width)
                        .aspectRatio(2f),
                    value = value
                ) {
                    value = it
                }

                Text(
                    text = "Value: ${(value * 100).toInt()}",
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                )
            }
        }
    }
}

private const val thumbId = "thumb"
private const val trackId = "track"

@Composable
fun ArcSlider(
    modifier: Modifier = Modifier,
    thumb: @Composable () -> Unit = {
        Thumb()
    },
    value: Float,
    onValueChange: (Float) -> Unit,
) {

    val density = LocalDensity.current
    val strokeWidth = with(density) {
        40f.toDp()
    }

    var angle by remember {
        mutableFloatStateOf(value * 180f)
    }.apply { this.floatValue = value * 180f }


    var thumbPosition by remember {
        mutableStateOf(Offset.Unspecified)
    }

    var thumbSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    var isTouched by remember {
        mutableStateOf(false)
    }

    var trackRect by remember {
        mutableStateOf(Rect.Zero)
    }

    val measurePolicy = remember {
        MeasurePolicy { measurables, constraints ->
            val strokeWidthPx = strokeWidth.roundToPx()

            val thumbPlaceable =
                measurables.fastFirst { it.layoutId == thumbId }.measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0
                    )
                )

            val thumbWidth = thumbPlaceable.width
            val thumbHeight = thumbPlaceable.height

            // TODO check for infinite constraints
            // Available width is minimum of max width - thumb width versus max height - half height of thumb at the bottom
            // and  of half thumb width - stroke width/2
            val availableWidth = (constraints.maxWidth - thumbWidth) / 2
            val availableHeight = (constraints.maxHeight - thumbHeight + strokeWidthPx / 2)

            val trackMeasurementWidth = availableWidth.coerceAtMost(availableHeight)

            val trackPlaceable = measurables.fastFirst { it.layoutId == trackId }.measure(
                Constraints.fixed(trackMeasurementWidth * 2, trackMeasurementWidth)
            )

            val sliderWidth = trackPlaceable.width
            val sliderHeight = trackPlaceable.height

            // radius calculated at the center of stroke width
            val radius = sliderWidth / 2 - strokeWidthPx / 2

            // Pivot points in bottom center of track for rotating thumb
            val trackPivotX = constraints.maxWidth / 2
            val trackPivotY = sliderHeight + (thumbHeight - strokeWidthPx) / 2

            val thumbX = trackPivotX + (-radius) * cos(angle.degreeToRadian)
            val thumbY = trackPivotY + (-radius) * sin(angle.degreeToRadian)
            thumbPosition = Offset(thumbX, thumbY)

            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight

            layout(layoutWidth, layoutHeight) {
                trackPlaceable.placeRelative(
                    x = (layoutWidth - sliderWidth) / 2,
                    y = (thumbHeight - strokeWidthPx) / 2
                )

                if (thumbPosition != Offset.Unspecified) {
                    thumbPlaceable.placeRelative(
                        x = (thumbPosition.x - thumbWidth / 2).toInt(),
                        y = (thumbPosition.y - thumbHeight / 2).toInt()
                    )
                }
            }
        }
    }

    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                if (thumbPosition != Offset.Unspecified && thumbSize != IntSize.Zero) {
                    val radius = thumbSize.width / 2
                    isTouched = offset.minus(thumbPosition).getDistanceSquared() < radius * radius
                }
            },
            onDrag = { change: PointerInputChange, _: Offset ->
                if (isTouched) {
                    val touchPosition: Offset = change.position

                    val centerX = trackRect.bottomCenter.x
                    val centerY = trackRect.bottomCenter.y

                    angle =
                        (
                                atan2(
                                    x = touchPosition.x.coerceIn(0f, size.width.toFloat()) - centerX,
                                    y = touchPosition.y.coerceIn(0f, size.height.toFloat()) - centerY
                                )
                                ) * 180 / Math.PI.toFloat()


                    // If angle is in top half add 180 degrees since
                    // atan2 returns angle between -PI and PI
                    if (angle < 0) {
                        angle += 180f
                    } else if (angle < 90) {
                        // If touch is in bottom end set to 180f because it's out of slider bounds
                        angle = 180f
                    } else {
                        // If touch is in bottom start set to 0f because it's out of slider bounds
                        angle = 0f
                    }

                    onValueChange(scale(0f, 180f, angle, 0f, 1f))
                }
            },
            onDragEnd = {
                isTouched = false
            },
            onDragCancel = {
                isTouched = false
            }
        )
    }
    Layout(
        modifier = modifier.then(dragModifier)
        // These 2 modifiers are for debugging for container bounds and track bounds
//            .border(2.dp, Color.Red)
//            .drawWithContent {
//            drawContent()
//            drawRect(
//                color = Color.Cyan,
//                topLeft = trackRect.topLeft,
//                size = trackRect.size,
//                style = Stroke(4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
//            )
//        }
        ,
        content = {
            Box(
                modifier = Modifier
                    .layoutId(trackId)
                    .onPlaced {
                        trackRect = it.boundsInParent()
                    }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeWidthPx = strokeWidth.toPx()

                    translate(
                        left = strokeWidthPx / 2,
                        top = strokeWidthPx / 2
                    ) {
                        drawArc(
                            color = Blue400.copy(alpha = .25f),
                            size = Size(size.width - strokeWidthPx, (size.height - strokeWidthPx / 2) * 2),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            style = Stroke(
                                strokeWidthPx,
                                cap = StrokeCap.Round
                            ),
                            useCenter = false
                        )

                        drawArc(
                            color = Blue400,
                            size = Size(size.width - strokeWidthPx, (size.height - strokeWidthPx / 2) * 2),
                            startAngle = 180f,
                            sweepAngle = scale(0f, 1f, value, 0f, 180f),
                            style = Stroke(
                                strokeWidthPx,
                                cap = StrokeCap.Round
                            ),
                            useCenter = false
                        )
                    }

                    // line for debugging angle inside canvas
//                    val lineStrokeWidth = 1.dp.toPx()
//
//                    rotate(
//                        degrees = angle,
//                        pivot = Offset(center.x, size.height)
//                    ) {
//                        drawLine(
//                            color = Color.Black,
//                            start = Offset(center.x, size.height),
//                            end = Offset(0f, size.height),
//                            strokeWidth = lineStrokeWidth
//                        )
//                    }
                }
            }

            Box(modifier = Modifier.layoutId(thumbId)
                .onSizeChanged {
                    thumbSize = it
                }
            ) {
                thumb()
            }
        },
        measurePolicy = measurePolicy
    )
}

@Composable
private fun Thumb() {

    val density = LocalDensity.current
    val size = with(density) {
        100f.toDp()
    }
    Box(
        modifier = Modifier
            .border(8.dp, Color.White, CircleShape)
            .size(size)
            .shadow(4.dp, CircleShape)
            .background(Blue400, CircleShape)

    )
}