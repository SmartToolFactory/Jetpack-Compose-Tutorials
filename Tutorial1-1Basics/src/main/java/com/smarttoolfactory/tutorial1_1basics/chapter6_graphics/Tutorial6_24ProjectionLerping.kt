package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun Tutorial6_24Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.padding(8.dp)) {
        TutorialHeader(text = "Projection change with Lerp")
        StyleableTutorialText(
            text = "Animate projection of rotating circle between inner and " +
                    "outer projection with lerp function.",
            bullets = false
        )
        ProjectionLerpSample()
    }
}

@Preview
@Composable
private fun ProjectionLerpSample() {

    val outerCircleRadius = 450f
    val innerCircleRadius = 250f

    var isOut by remember {
        mutableStateOf(false)
    }

    val transition = rememberInfiniteTransition(label = "infinite angle transition")

    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0.0f at 0 with LinearEasing
                360f at 3000 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    val progress by animateFloatAsState(
        if (isOut) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "distance"
    )

    val distance = remember(progress) {
        lerp(innerCircleRadius, outerCircleRadius, progress)
    }

    var position by remember {
        mutableStateOf(Offset.Unspecified)
    }

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {

            drawCircle(
                color = Color.Blue,
                radius = outerCircleRadius,
                style = Stroke(2.dp.toPx())
            )

            drawCircle(
                color = Color.Blue,
                radius = innerCircleRadius,
                style = Stroke(2.dp.toPx())
            )

            rotate(angle) {
                drawCircle(
                    color = Color.Red,
                    radius = 50f,
                    center = Offset(center.x + distance, center.y)

                )
            }

            val angleInRadians = angle * DEGREE_TO_RAD
            position = Offset(
                center.x + distance * cos(angleInRadians), center.y + distance * sin(angleInRadians)
            )
        }

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            onClick = {
                isOut = isOut.not()
            }
        ) {
            Text("Out $isOut")
        }

        if (position != Offset.Unspecified) {
            Text(
                text = "Position: $position",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        }
    }
}

private const val DEGREE_TO_RAD = (Math.PI / 180f).toFloat()