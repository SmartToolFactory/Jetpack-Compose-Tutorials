package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.ComposePathEffect
import android.graphics.CornerPathEffect
import android.graphics.DiscretePathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial6_7Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StyleableTutorialText(
            text = "Gooey Effect is combination of paths with path operation",
            bullets = false
        )
        GooeyEffectSample()
        StyleableTutorialText(text = "Gooey Effect with touch", bullets = false)
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
            .aspectRatio(4/3f)
    ) {
        val center = size.center

        if (pathLeft.isEmpty) {
            pathLeft.addOval(
                Rect(
                    center = Offset(center.x, center.y),
                    radius = 200f
                )
            )
        }

        if (pathRight.isEmpty) {
            pathRight.addOval(
                Rect(
                    center = Offset(center.x + 100f, center.y),
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

    val pathLeft = remember { Path() }
    val pathRight = remember { Path() }

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
            detectDragGestures { change, dragAmount ->
                currentPosition = change.position
            }
        }
        .fillMaxWidth()
        .aspectRatio(1f)

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.Red,
                Color.Green
            )
        )
    }

    Canvas(modifier = modifier) {
        val center = size.center

        val position = if (currentPosition == Offset.Unspecified) {
            center
        } else currentPosition

        pathLeft.reset()
        pathLeft.addOval(
            Rect(
                center = position,
                radius = 200f
            )
        )


        pathRight.reset()
        pathRight.addOval(
            Rect(
                center = Offset(center.x, center.y),
                radius = 200f
            )
        )

        val discretePathEffect = DiscretePathEffect(pathMeasure.length / segmentCount, 0f)
        val cornerPathEffect = CornerPathEffect(50f)


        val chainPathEffect = PathEffect.chainPathEffect(
            outer = cornerPathEffect.toComposePathEffect(),
            inner = discretePathEffect.toComposePathEffect()
        )

        pathLeft.op(pathLeft, pathRight, PathOperation.Union)

        pathMeasure.setPath(pathLeft, true)

        drawPath(
            path = pathLeft,
            brush = brush,
            style = Stroke(
                4.dp.toPx(),
                pathEffect = chainPathEffect
            )
        )

    }
}
