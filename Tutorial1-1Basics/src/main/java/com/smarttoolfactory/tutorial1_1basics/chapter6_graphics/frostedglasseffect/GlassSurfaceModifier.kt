package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class GlassSurfaceStyle {
    Flat,
    Convex,
    Concave
}

/**
 * Draws a reusable glass-like surface treatment that can be used on its own or layered over
 * [com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect.frostedGlassTarget].
 *
 * [GlassSurfaceStyle.Flat] uses the provided tint, border, and shadow parameters directly.
 * [GlassSurfaceStyle.Convex] and [GlassSurfaceStyle.Concave] use predefined highlight and shadow
 * treatments to create raised or recessed glass looks.
 */
fun Modifier.glassSurface(
    shape: Shape = RectangleShape,
    style: GlassSurfaceStyle = GlassSurfaceStyle.Flat,
    tint: Color = Color.White.copy(alpha = 0.12f),
    borderColor: Color = Color.White.copy(alpha = 0.18f),
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 20.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.22f)
): Modifier {
    return when (style) {
        GlassSurfaceStyle.Flat -> {
            flatGlassSurface(
                shape = shape,
                tint = tint,
                borderColor = borderColor,
                borderWidth = borderWidth,
                shadowElevation = shadowElevation,
                shadowColor = shadowColor
            )
        }

        GlassSurfaceStyle.Convex -> {
            styledGlassConvexSurface(shape = shape)
        }

        GlassSurfaceStyle.Concave -> {
            styledGlassConcaveSurface(shape = shape)
        }
    }
}

private fun Modifier.flatGlassSurface(
    shape: Shape,
    tint: Color,
    borderColor: Color,
    borderWidth: Dp,
    shadowElevation: Dp,
    shadowColor: Color
): Modifier {
    var modifier = this

    if (shadowElevation > 0.dp && shadowColor.alpha > 0f) {
        modifier = modifier.shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
    }

    modifier = modifier.background(color = tint, shape = shape)

    if (borderWidth > 0.dp && borderColor.alpha > 0f) {
        modifier = modifier.border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
    }

    return modifier
}

private fun Modifier.styledGlassConvexSurface(shape: Shape): Modifier {
    val baseTint = Color.White.copy(alpha = 0.10f)
    val edgeColor = Color.White.copy(alpha = 0.18f)
    val topHighlight = Color.White.copy(alpha = 0.18f)
    val bottomShade = Color.Black.copy(alpha = 0.10f)
    val shadowColor = Color.Black.copy(alpha = 0.28f)

    return this
        .shadow(
            elevation = 24.dp,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .background(baseTint, shape)
        .border(width = 1.dp, color = edgeColor, shape = shape)
        .drawWithCache {
            val outline = shape.createOutline(
                size = size,
                layoutDirection = layoutDirection,
                density = this
            )
            val topGlowBrush = Brush.verticalGradient(
                colors = listOf(topHighlight, Color.Transparent),
                endY = size.height * 0.52f
            )
            val sheenBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.03f)
                )
            )
            val bottomShadeBrush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, bottomShade),
                startY = size.height * 0.56f,
                endY = size.height
            )

            onDrawWithContent {
                drawContent()
                drawClippedGlassOutline(outline = outline, width = size.width, height = size.height) {
                    drawRect(brush = topGlowBrush)
                    drawRect(brush = sheenBrush)
                    drawRect(brush = bottomShadeBrush)
                }
                drawOutline(
                    outline = outline,
                    color = Color.White.copy(alpha = 0.10f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
}

private fun Modifier.styledGlassConcaveSurface(shape: Shape): Modifier {
    val baseTint = Color.Black.copy(alpha = 0.18f)
    val rimColor = Color.Black.copy(alpha = 0.12f)

    return this
        .background(baseTint, shape)
        .border(width = 1.dp, color = rimColor, shape = shape)
        .drawWithCache {
            val outline = shape.createOutline(
                size = size,
                layoutDirection = layoutDirection,
                density = this
            )
            val topShadowBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.32f),
                    Color.Black.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = size.height * 0.42f
            )
            val bottomCausticBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.06f),
                    Color.White.copy(alpha = 0.16f),
                    Color.White.copy(alpha = 0.22f)
                ),
                startY = size.height * 0.52f,
                endY = size.height
            )
            val leftInnerShadow = Brush.horizontalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.18f),
                    Color.Transparent
                ),
                startX = 0f,
                endX = size.width * 0.25f
            )
            val rightInnerShadow = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.18f)
                ),
                startX = size.width * 0.75f,
                endX = size.width
            )
            val bottomEdgeShadow = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.08f)
                ),
                startY = size.height * 0.88f,
                endY = size.height
            )
            val centerGlow = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                startY = size.height * 0.30f,
                endY = size.height * 0.70f
            )
            val innerStrokeColor = Color.Black.copy(alpha = 0.22f)
            val causticRimColor = Color.White.copy(alpha = 0.14f)

            onDrawWithContent {
                drawContent()
                drawClippedGlassOutline(outline = outline, width = size.width, height = size.height) {
                    drawRect(brush = topShadowBrush)
                    drawRect(brush = leftInnerShadow)
                    drawRect(brush = rightInnerShadow)
                    drawRect(brush = bottomEdgeShadow)
                    drawRect(brush = centerGlow)
                    drawRect(brush = bottomCausticBrush)
                }
                drawOutline(
                    outline = outline,
                    color = innerStrokeColor,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawOutline(
                    outline = outline,
                    color = causticRimColor,
                    style = Stroke(width = 0.5.dp.toPx())
                )
            }
        }
}

private fun DrawScope.drawClippedGlassOutline(
    outline: Outline,
    width: Float,
    height: Float,
    content: DrawScope.() -> Unit
) {
    when (outline) {
        is Outline.Rectangle -> {
            clipRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height
            ) {
                content()
            }
        }

        is Outline.Rounded -> {
            clipPath(
                Path().apply {
                    addRoundRect(outline.roundRect)
                }
            ) {
                content()
            }
        }

        is Outline.Generic -> {
            clipPath(outline.path) {
                content()
            }
        }
    }
}
