package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class MaterialShadow(
    val color: Color,
    val alpha: Float,
    val shape: Shape,
    val shadowRadius: Dp,
    val offsetY: Dp = 0.dp,
    val offsetX: Dp = 0.dp,
    val useSoftwareLayer: Boolean = true
)

fun Modifier.materialShadow(badgeState: BadgeState) = this.then(
    if (badgeState.elevation > 0.dp) {

        val text = badgeState.text
        val circleThreshold = badgeState.circleShapeThreshold
        val isCircleShape = text.length <= circleThreshold

        drawBehind {

            this.drawIntoCanvas {

                val color = badgeState.shadowColor
                val elevationInPx = with(density) {
                    badgeState.elevation.toPx()
                }

                val paint = Paint()
                val shadowColor = color
                    .copy(alpha = .7f)
                    .toArgb()
                val transparent = color
                    .copy(alpha = 0f)
                    .toArgb()

                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = transparent

                frameworkPaint.setShadowLayer(
                    elevationInPx,
                    elevationInPx,
                    elevationInPx,
                    shadowColor
                )

                if (isCircleShape) {
                    it.drawCircle(
                        center = Offset(center.x, center.y),
                        radius = this.size.width / 2f,
                        paint = paint
                    )
                } else {
                    it.drawRoundRect(
                        left = 0f,
                        top = 0f,
                        right = this.size.width,
                        bottom = this.size.height,
                        radiusX = size.height * badgeState.roundedRadiusPercent / 100,
                        radiusY = size.height * badgeState.roundedRadiusPercent / 100,
                        paint = paint
                    )
                }

            }

//                        val elevationInPx = with(density) {
//                            badgeState.elevation.toPx()
//                        }
//
//                        val center = Offset(center.x + elevationInPx, center.y + elevationInPx)
//                        if (isCircleShape) {
//                            drawCircle(color = badgeState.shadowColor, this.size.width / 2f, center)
//                        } else {
//                            drawRoundRect(
//                                color = badgeState.shadowColor,
//                                alpha = .7f,
//                                size = this.size,
//                                topLeft = Offset(elevationInPx, elevationInPx),
//                                cornerRadius = CornerRadius(
//                                    size.height * badgeState.roundedRadiusPercent / 100,
//                                    size.height * badgeState.roundedRadiusPercent / 100
//                                )
//                            )
//                        }
        }
    } else {
        this
    }
)

fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = composed {


    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()

    this
        .padding(horizontal = offsetX, vertical = offsetY)
        .drawBehind {

            this.drawIntoCanvas {
                val paint = Paint()

                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = transparent

                frameworkPaint.setShadowLayer(
                    shadowRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    shadowColor
                )

                it.drawRoundRect(
                    0f,
                    0f,
                    this.size.width,
                    this.size.height,
                    borderRadius.toPx(),
                    borderRadius.toPx(),
                    paint
                )
            }
        }
}