package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun MaterialShadow(
    color: Color,
    alpha: Float,
    useSoftwareLayer: Boolean = true,
    dX: Dp = 1.dp,
    dY: Dp = 1.dp,
    shadowRadius: Dp = 1.dp,
): MaterialShadow {
    return MaterialShadow(
        color, alpha, dX, dY, shadowRadius, useSoftwareLayer
    )
}

fun MaterialShadow(
    color: Color = Color(0x55000000),
    alpha: Float = .7f,
    useSoftwareLayer: Boolean = true,
    elevation: Dp = 1.dp
): MaterialShadow {
    return MaterialShadow(
        color,
        alpha,
        elevation,
        elevation,
        elevation,
        useSoftwareLayer
    )
}

class MaterialShadow internal constructor(
    val color: Color = Color(0x55000000),
    val alpha: Float = 0.7f,
    val shadowRadius: Dp = 1.dp,
    val offsetY: Dp = 1.dp,
    val offsetX: Dp = 1.dp,
    val useSoftwareLayer: Boolean = true
)

fun Modifier.materialShadow(badgeState: BadgeState) = this.then(

    if (badgeState.shadow != null) {

        val shadow: MaterialShadow = badgeState.shadow!!
        val text = badgeState.text
        val circleThreshold = badgeState.circleShapeThreshold
        val isCircleShape = text.length <= circleThreshold

        drawBehind {

            if (shadow.useSoftwareLayer) {
                this.drawIntoCanvas {

                    val color = shadow.color
                    val dx = shadow.offsetX.toPx()
                    val dy = shadow.offsetY.toPx()
                    val radius = shadow.shadowRadius.toPx()

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
                        dx,
                        dy,
                        radius,
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
            } else {

                val dx = shadow.offsetX.toPx()
                val dy = shadow.offsetY.toPx()

                val center = Offset(center.x + dx, center.y + dy)
                if (isCircleShape) {
                    drawCircle(color = shadow.color, this.size.width / 2f, center)
                } else {
                    drawRoundRect(
                        color = shadow.color,
                        alpha = .7f,
                        size = this.size,
                        topLeft = Offset(dx, dy),
                        cornerRadius = CornerRadius(
                            size.height * badgeState.roundedRadiusPercent / 100,
                            size.height * badgeState.roundedRadiusPercent / 100
                        )
                    )
                }
            }
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