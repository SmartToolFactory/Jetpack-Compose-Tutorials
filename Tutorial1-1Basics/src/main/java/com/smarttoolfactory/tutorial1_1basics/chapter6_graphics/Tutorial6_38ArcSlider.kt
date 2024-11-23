package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
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
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        var value by remember {
            mutableFloatStateOf(0f)
        }

        Box {
            ArcSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                value = value
            ) {
                value = it
            }

            Text(
                text = "Value: ${(value * 100)}",
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
            )
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

    val strokeWidth = 20.dp

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

    val measurePolicy = remember {
        MeasurePolicy { measurables, constraints ->

            val thumbPlaceable =
                measurables.fastFirst { it.layoutId == thumbId }.measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0
                    )
                )
            val trackPlaceable = measurables.fastFirst { it.layoutId == trackId }.measure(constraints)

            val sliderWidth = trackPlaceable.width
            val sliderHeight = trackPlaceable.height

            val thumbWidth = thumbPlaceable.width
            val thumbHeight = thumbPlaceable.height

            val strokeWidthPx = strokeWidth.toPx()

            val radius = sliderWidth / 2
            val centerX = radius
            val centerY = sliderHeight
            val thumbX = centerX + (-radius + strokeWidthPx / 2) * cos(angle.degreeToRadian) - thumbWidth / 2
            val thumbY = centerY + (-radius + strokeWidthPx / 2) * sin(angle.degreeToRadian) - thumbHeight / 2
            thumbPosition = Offset(thumbX, thumbY)

            layout(sliderWidth, sliderHeight) {
                trackPlaceable.placeRelative(0, 0)

                if (thumbPosition != Offset.Unspecified) {
                    thumbPlaceable.placeRelative(thumbPosition.x.toInt(), thumbPosition.y.toInt())
                }
            }
        }
    }

    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(2f)
                    .layoutId(trackId)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (thumbPosition != Offset.Unspecified && thumbSize != IntSize.Zero) {
                                    val radius = thumbSize.width
                                    isTouched = offset.minus(thumbPosition).getDistanceSquared() < radius * radius
                                }
                            },
                            onDrag = { change: PointerInputChange, _: Offset ->
                                if (isTouched) {
                                    val touchPosition: Offset = change.position
                                    val radius = size.width / 2
                                    val centerX = radius

                                    val centerY = size.height
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
                                        // If touch is bottom end set to 180f because it's out of slider bounds
                                        angle = 180f
                                    } else {
                                        // If touch is bottom end set to 0f because it's out of slider bounds
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
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = strokeWidth.toPx()
                    translate(
                        left = strokeWidthPx / 2,
                        top = strokeWidthPx / 2
                    ) {
                        drawArc(
                            color = Blue400.copy(alpha = .25f),
                            size = Size(size.width - strokeWidthPx, (size.height - strokeWidthPx) * 2),
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
                            size = Size(size.width - strokeWidthPx, (size.height - strokeWidthPx) * 2),
                            startAngle = 180f,
                            sweepAngle = scale(0f, 1f, value, 0f, 180f),
                            style = Stroke(
                                strokeWidthPx,
                                cap = StrokeCap.Round
                            ),
                            useCenter = false
                        )
                    }
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
    Box(
        modifier = Modifier.size(40.dp).border(8.dp, Color.White, CircleShape)
            .shadow(4.dp, CircleShape)
            .background(Color.Blue, CircleShape)
    )
}