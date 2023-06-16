package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.ComposePathEffect
import android.graphics.CornerPathEffect
import android.graphics.DiscretePathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toComposePathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial6_7Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        StyleableTutorialText(
            text = "Gooey Effect is combination of paths with path operation. **Blue** path " +
                    "is the original path without any operation. Other paths are drawn using" +
                    "**Path.combine** and **PathEffect**s to create gooey effect.",
            bullets = false
        )
        GooeyEffectSample()
        StyleableTutorialText(text = "Gooey Effect with touch. Touch on Canvas to move " +
                "one of the circles", bullets = false)
        GooeyEffectSample2()
    }
}

@Composable
private fun GooeyEffectSample() {

    val pathLeft = remember { Path() }
    val pathRight = remember { Path() }

    val segmentCount = 20
    val pathMeasure = PathMeasure()
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
    ) {
        val center = size.center

        if (pathLeft.isEmpty) {
            pathLeft.addOval(
                Rect(
                    center = Offset(center.x - 90f, center.y),
                    radius = 200f
                )
            )
        }

        if (pathRight.isEmpty) {
            pathRight.addOval(
                Rect(
                    center = Offset(center.x + 90f, center.y),
                    radius = 200f
                )
            )
        }

        val path = Path.combine(PathOperation.Union, pathLeft, pathRight)
        pathMeasure.setPath(pathLeft, true)

        val discretePathEffect = DiscretePathEffect(pathMeasure.length / segmentCount, 0f)
        val cornerPathEffect = CornerPathEffect(50f)

        val composePathEffect = ComposePathEffect(cornerPathEffect, discretePathEffect)


        val chainPathEffect = PathEffect.chainPathEffect(
            outer = cornerPathEffect.toComposePathEffect(),
            inner = discretePathEffect.toComposePathEffect()
        )
        drawPath(path, Color.Blue, style = Stroke(4.dp.toPx()))

        translate(top = -50f) {
            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(
                    4.dp.toPx(),
                    pathEffect = chainPathEffect
                )
            )
        }

        translate(top = -100f) {
            drawPath(
                path = path,
                color = Color.Cyan,
                style = Stroke(
                    4.dp.toPx(),
                    pathEffect = composePathEffect.toComposePathEffect()
                )
            )
        }
    }
}


@Composable
private fun GooeyEffectSample2() {

    val pathDynamic = remember { Path() }
    val pathStatic = remember { Path() }

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    val segmentCount = 20
    val pathMeasure = remember {
        PathMeasure()
    }

    val modifier = Modifier
        .pointerInput(Unit) {
            detectDragGestures { change, _ ->
                currentPosition = change.position
            }
        }
        .fillMaxSize()

    val paint = remember {
        Paint()
    }

    var isPaintSetUp by remember {
        mutableStateOf(false)
    }

    Canvas(modifier = modifier) {
        val center = size.center

        val position = if (currentPosition == Offset.Unspecified) {
            center
        } else currentPosition

        pathDynamic.reset()
        pathDynamic.addOval(
            Rect(
                center = position,
                radius = 100f
            )
        )

        pathStatic.reset()
        pathStatic.addOval(
            Rect(
                center = Offset(center.x, center.y),
                radius = 150f
            )
        )

        pathMeasure.setPath(pathDynamic, true)

        val discretePathEffect = DiscretePathEffect(pathMeasure.length / segmentCount, 0f)
        val cornerPathEffect = CornerPathEffect(50f)


        val chainPathEffect = PathEffect.chainPathEffect(
            outer = cornerPathEffect.toComposePathEffect(),
            inner = discretePathEffect.toComposePathEffect()
        )

        if (!isPaintSetUp) {

            paint.shader = LinearGradientShader(
                from = Offset.Zero,
                to = Offset(size.width, size.height),
                colors = listOf(
                    Color(0xffFFEB3B),
                    Color(0xffE91E63)
                ),
                tileMode = TileMode.Clamp
            )
            isPaintSetUp = true
            paint.pathEffect = chainPathEffect
        }

        val newPath = Path.combine(PathOperation.Union, pathDynamic, pathStatic)

        with(drawContext.canvas) {
            this.drawPath(
                newPath,
                paint
            )
        }
    }
}