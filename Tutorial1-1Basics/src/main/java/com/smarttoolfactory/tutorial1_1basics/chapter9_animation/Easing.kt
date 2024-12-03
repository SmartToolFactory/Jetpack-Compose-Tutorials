package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@Preview
@Composable
private fun Easingsample() {

    val animatable = remember {
        Animatable(0f)
    }

    val path = remember {
        Path()
    }

    val coroutineScope = rememberCoroutineScope()

    var startTime by remember {
        mutableLongStateOf(0L)
    }

    var totalVelocity by remember {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current
    val sizeDp = with(density) {
        1000f.toDp()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Canvas(
            modifier = Modifier.size(sizeDp).border(2.dp, Color.Blue)
        ) {

            val progress = animatable.value
            val width = size.width
            val height = size.height

            if (startTime == 0L && animatable.isRunning) {
                startTime = System.nanoTime()
            }

            val currentTime = (System.nanoTime() - startTime) / 1000_000L

            val x = (width * currentTime / 1000f).coerceAtMost(1000f)

//            val y = height * (1 - progress)
            val y = height * progress

            (animatable.velocity as? Float)?.let {
                totalVelocity += it
            }
            println(
                "Time: $currentTime, progress: $progress, x: $x, y: $y, " +
                        "velocity: ${animatable.velocity}, totalVelocity: $totalVelocity"
            )

            if (path.isEmpty.not()) {
                path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(2.dp.toPx())
            )
        }


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    totalVelocity = 0f
                    animatable.snapTo(0f)
                    path.reset()
                    path.moveTo(0f, 0f)
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = LinearEasing
                        )
                    )

                    awaitFrame()
                    startTime = 0
                }
            }
        ) {
            Text("Start")
        }
    }
}


@Preview
@Composable
private fun EasingTest2() {

    val animatableList = remember {
        List(5) {
            Animatable(0f)
        }
    }

    var startTime by remember {
        mutableLongStateOf(0L)
    }

    val coroutineScope = rememberCoroutineScope()

    Column {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {

            animatableList.forEachIndexed { index, animatable ->
                val color = when (index) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    2 -> Color.Green
                    3 -> Color.Magenta
                    else -> Color.Black
                }

                EasingTestBox(
                    animatable = animatable,
                    color = color,
                    startTime = startTime
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                startTime = System.currentTimeMillis()
                animatableList.forEachIndexed { index, animatable ->

                    val animationSpec = when (index) {
                        0 -> tween(
                            durationMillis = 1000,
                            easing = LinearEasing
                        )

                        1 -> {
                            tween<Float>(
                                durationMillis = 1000,
                                easing = LinearOutSlowInEasing
                            )
                        }

                        2 -> tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )

                        3 -> tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )

                        else -> spring()
                    }
                    coroutineScope.launch {
                        animatable.snapTo(0f)
                        animatable.animateTo(
                            targetValue = 0f,
                            animationSpec = animationSpec
                        )

                    }
                }
            }
        ) {
            Text("Start")
        }
    }


}

@Composable
private fun EasingTestBox(
    animatable: Animatable<Float, AnimationVector1D>,
    startTime: Long,
    color: Color,
) {

    val path = remember {
        Path()
    }

    Canvas(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).border(1.dp, Color.Blue)
    ) {
        val progress = animatable.value
        val width = size.width
        val height = size.height

        val currentTime = System.currentTimeMillis() - startTime

        if (path.isEmpty) {
            path.moveTo(0f, height)
        } else {
            path.lineTo(width * currentTime / 1000f, height * (1 - progress))
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(2.dp.toPx())
        )
    }
}
