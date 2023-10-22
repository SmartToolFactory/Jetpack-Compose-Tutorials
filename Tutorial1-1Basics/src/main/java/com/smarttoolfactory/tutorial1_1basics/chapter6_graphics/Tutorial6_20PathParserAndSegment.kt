package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.annotation.SuppressLint
import android.graphics.Matrix
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.gradientColors

@Preview
@Composable
fun Tutorial6_20Screen() {
    Column {
        TutorialHeader(text = "PathParser and Segments", modifier = Modifier.padding(8.dp))

        StyleableTutorialText(
            text = "Use **PathParser** to create path from string and **PathMeasure** to " +
                    "create segments of a **Path**.",
            bullets = false
        )

        TutorialContent()
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun TutorialContent() {

    var progress1 by remember {
        mutableStateOf(0f)
    }

    val transition: InfiniteTransition = rememberInfiniteTransition(label = "heart animation")

    // Infinite phase animation for PathEffect
    val progress2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),
        ), label = "segment animation"
    )


    // This is the progress path which wis changed using path measure
    val pathWithProgress by remember {
        mutableStateOf(Path())
    }

    // using path
    val pathMeasure by remember { mutableStateOf(PathMeasure()) }

    val path = remember {
        Path()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(text = "Progress1: $progress1")
        Slider(
            value = progress1,
            onValueChange = { progress1 = it },
            valueRange = 0f..1f
        )

        Canvas(modifier = Modifier.size(150.dp)) {
            val width: Float = size.width
            val height: Float = size.height

            if (path.isEmpty) {
                path.apply {
                    moveTo(width / 2, height / 5)
                    cubicTo(
                        5 * width / 14, 0f,
                        0f, height / 15,
                        width / 28, 2 * height / 5
                    )
                    cubicTo(
                        width / 14, 2 * height / 3,
                        3 * width / 7, 5 * height / 6,
                        width / 2, height
                    )
                    cubicTo(
                        4 * width / 7, 5 * height / 6,
                        13 * width / 14, 2 * height / 3,
                        27 * width / 28, 2 * height / 5
                    )
                    cubicTo(
                        width, height / 15,
                        9 * width / 14, 0f,
                        width / 2, height / 5
                    )
                }
            }

            pathWithProgress.reset()
            pathMeasure.setPath(path, forceClosed = false)
            pathMeasure.getSegment(
                startDistance = 0f,
                stopDistance = pathMeasure.length * progress1,
                pathWithProgress,
                startWithMoveTo = true
            )

            drawPath(color = Color.Red, path = pathWithProgress, style = Stroke(8f))

            pathWithProgress.reset()
            pathMeasure.setPath(path, forceClosed = false)
            pathMeasure.getSegment(
                startDistance = 0f,
                stopDistance = pathMeasure.length * progress2,
                pathWithProgress,
                startWithMoveTo = true
            )

            translate(left = 600f) {
                drawPath(
                    color = Color.Blue,
                    path = pathWithProgress,
                    style = Stroke(5.dp.toPx())
                )
            }

        }

        val starPath = remember {
            PathParser.createPathFromPathData("M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z")
                .asComposePath()
                .apply {
                    val matrix = Matrix()
                    matrix.postScale(30f, 30f)
                    this.asAndroidPath().transform(matrix)
                }
        }

        val pathSize = starPath.getBounds().size

        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {

                pathWithProgress.reset()
                pathMeasure.setPath(starPath, forceClosed = false)
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * progress2,
                    pathWithProgress,
                    startWithMoveTo = true
                )

                val brush = Brush.sweepGradient(gradientColors)

                onDrawWithContent {
                    translate(
                        left = (size.width - pathSize.width) / 2,
                        top = (size.height - pathSize.height) / 2
                    ) {
                        drawPath(
                            path = pathWithProgress,
                            color = Orange400,
                            style = Stroke(
                                width = 6.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                }
            }
        )

    }
}