package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.runtime.remember
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

/**
 * Creates a colorable shadow instance.
 *
 * @param color Color of the shadow
 * @param alpha of the color of the shadow
 * @param useSoftwareLayer use software layer to draw shadow with blur
 * @param dX x offset of shadow blur
 * @param dY y offset of shadow blur
 * @param shadowRadius radius of shadow blur if useSoftwareLayer is set to true
 */
fun MaterialShadow(
    color: Color = Color(0x55000000),
    alpha: Float = .7f,
    useSoftwareLayer: Boolean = true,
    dX: Dp = 1.dp,
    dY: Dp = 1.dp,
    shadowRadius: Dp = 1.dp,
): MaterialShadow {
    return MaterialShadow(
        color,
        alpha,
        dX,
        dY,
        shadowRadius,
        useSoftwareLayer
    )
}

/**
 * Creates a colorable shadow instance.
 *
 * @param color Color of the shadow
 * @param alpha of the color of the shadow
 * @param useSoftwareLayer use software layer to draw shadow with blur
 * @param elevation elevation of the badge with shadow. Sets dx, dy,
 * and shadowRadius if software layer is used
 */
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

/**
 *
 * Draws customizable shadow behind badge using software layer to have blurred shadow
 * or solid shapes only..
 *
 * With **composed** we can use `remember` and instantiate Paint objects only once
 * for each element. Modifier is **stateful**.
 */
fun Modifier.materialShadow(badgeState: BadgeState) = composed(
    inspectorInfo = {
        name = "shadow"
        value = badgeState.shadow
    },
    factory = {
       badgeState.shadow?.let {shadow: MaterialShadow->

           val paint = remember(badgeState) {
               Paint()
           }

           val frameworkPaint = remember(badgeState) {
               paint.asFrameworkPaint()
           }

           drawBehind {

               if (shadow.useSoftwareLayer) {
                   this.drawIntoCanvas {

                       val color = shadow.color
                       val dx = shadow.offsetX.toPx()
                       val dy = shadow.offsetY.toPx()
                       val radius = shadow.shadowRadius.toPx()


                       val shadowColor = color
                           .copy(alpha = shadow.alpha)
                           .toArgb()
                       val transparent = color
                           .copy(alpha = 0f)
                           .toArgb()

                       frameworkPaint.color = transparent

                       frameworkPaint.setShadowLayer(
                           dx,
                           dy,
                           radius,
                           shadowColor
                       )

                       if (badgeState.isCircleShape) {
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

                   if (badgeState.isCircleShape) {
                       drawCircle(color = shadow.color, this.size.width / 2f, center)
                   } else {
                       drawRoundRect(
                           color = shadow.color,
                           alpha = shadow.alpha,
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
       }?:this
    }
)
