package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Random

@Preview
@Composable
fun ControlledExplosion() {

    var progress by remember { mutableFloatStateOf(0f) }

    var visibilityThresholdLow by remember {
        mutableFloatStateOf(0f)
    }

    var visibilityThresholdHigh by remember {
        mutableFloatStateOf(1f)
    }

    val particleCount = 100

    val density = LocalDensity.current

    val sizeDp = with(density) {
        1000f.toDp()
    }
    val sizePx = with(density) {
        sizeDp.toPx()
    }
    val sizePxHalf = sizePx / 2

    val particles = remember(
        visibilityThresholdLow,
        visibilityThresholdHigh
    ) {
        List(particleCount) {

            val initialDisplacementX: Float = 1f
            val initialDisplacementY: Float = 1f


//            with(density) {
//                initialDisplacementX = 10.dp.toPx() * randomInRange(-1f, 1f)
//                initialDisplacementY = 10.dp.toPx() * randomInRange(-1f, 1f)
//            }

            val min = randomInRange(0f, visibilityThresholdLow)
            val max = randomInRange(min, visibilityThresholdHigh)

            ExplodingParticle(
                color = Color(listOf(0xffea4335, 0xff4285f4, 0xfffbbc05, 0xff34a853).random()),
                startXPosition = sizePxHalf.toInt(),
                startYPosition = sizePxHalf.toInt(),
                maxHorizontalDisplacement = sizePxHalf * randomInRange(-.9f, .9f),
                maxVerticalDisplacement = sizePxHalf * randomInRange(0.2f, 0.38f),
                visibilityThresholdLow = min,
                visibilityThresholdHigh = max,
                initialDisplacementX = initialDisplacementX,
                initialDisplacementY = initialDisplacementY
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {

        Explosion(
            progress = progress,
            particles = particles,
            sizeDp = sizeDp
        )

        Spacer(Modifier.height(16.dp))

        val particle = particles.first()
        Text(
            text = "Progress: ${(progress * 100).toInt() / 100f}\n" +
                    "trajectory: ${(particle.trajectoryProgress * 100).toInt() / 100f}\n" +
                    "currentTime: ${(particle.currentTime * 100f).toInt() / 100f}\n",
            fontSize = 18.sp
        )
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = progress,
            onValueChange = {
                progress = it
            }
        )

        Text("visibilityThresholdLow: $visibilityThresholdLow")
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = visibilityThresholdLow,
            onValueChange = {
                visibilityThresholdLow = it.coerceAtMost(visibilityThresholdHigh)
            },
            valueRange = 0f..1f
        )

        Text("visibilityThresholdHigh: $visibilityThresholdHigh")
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = visibilityThresholdHigh,
            onValueChange = {
                visibilityThresholdHigh = it.coerceAtLeast(visibilityThresholdLow)
            },
            valueRange = 0f..1f
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun Explosion(
    sizeDp: Dp,
    particles: List<ExplodingParticle>,
    progress: Float
) {
    val density = LocalDensity.current
    val sizePx = with(density) {
        sizeDp.toPx()
    }

    val sizePxHalf = sizePx / 2

    particles.forEach { it.updateProgress(progress) }

    Canvas(
        modifier = Modifier
            .border(width = 1.dp, color = Color(0x26000000))
            .size(sizeDp)
    ) {
        drawLine(
            color = Color.Black,
            start = Offset(sizePxHalf, 0f),
            end = Offset(sizePxHalf, sizePx),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, sizePxHalf),
            end = Offset(sizePx, sizePxHalf),
            strokeWidth = 2.dp.toPx()
        )
        particles.forEach { particle ->
            drawCircle(
                alpha = particle.alpha,
                color = particle.color,
                radius = 5.dp.toPx(),
                center = Offset(particle.currentXPosition, particle.currentYPosition),
            )
        }
    }
}

class ExplodingParticle(
    val color: Color,
    val startXPosition: Int,
    val startYPosition: Int,
    val maxHorizontalDisplacement: Float,
    val maxVerticalDisplacement: Float,
    val visibilityThresholdLow: Float,
    val visibilityThresholdHigh: Float,
    val initialDisplacementX: Float,
    val initialDisplacementY: Float
) {
    private val velocity = 4 * maxVerticalDisplacement
    private val acceleration = -2 * velocity
    var currentXPosition = 0f
    var currentYPosition = 0f

    var alpha = 1f

    var currentTime: Float = 0f
        private set
    var trajectoryProgress: Float = 0f
        private set

    fun updateProgress(explosionProgress: Float) {

        // Trajectory progress translates progress from 0f-1f to
        // visibilityThresholdLow-visibilityThresholdHigh
        // range. For instance, 0.1-0.6f range movement starts when
        // explosionProgress is at 0.1f and reaches 1f
        // when explosionProgress reaches 0.6f and trajectoryProgress .

        // Each 0.1f change in trajectoryProgress 0.5f total range
        // corresponds to 0.2f change of current time
        trajectoryProgress =
            if (explosionProgress < visibilityThresholdLow) {
                0f
            } else if (explosionProgress > visibilityThresholdHigh) {
                1f
            } else {
                scale(
                    a1 = visibilityThresholdLow,
                    b1 = visibilityThresholdHigh,
                    x1 = explosionProgress,
                    a2 = 0f,
                    b2 = 1f
                )
            }

        currentTime = scale(0f, 1f, trajectoryProgress, 0f, 1.4f)

        // While trajectory progress is less than 70% have full alpha then slowly cre
        alpha = if (trajectoryProgress < .7f) 1f else
            scale(.7f, 1f, trajectoryProgress, 1f, 0f)

        val verticalDisplacement =
            currentTime * velocity + 0.5 * acceleration * currentTime * currentTime

        currentXPosition =
            startXPosition + initialDisplacementX + maxHorizontalDisplacement * trajectoryProgress
        currentYPosition = (startYPosition + initialDisplacementY - verticalDisplacement).toFloat()

    }
}

private val random = Random()
fun Float.randomTillZero() = this * random.nextFloat()
fun randomInRange(min: Float, max: Float) = min + (max - min).randomTillZero()
fun randomBoolean(trueProbabilityPercentage: Int) =
    random.nextFloat() < trueProbabilityPercentage / 100f