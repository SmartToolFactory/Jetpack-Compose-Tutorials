package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.randomInRange
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.scale
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.toPx
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import kotlinx.coroutines.CancellationException
import kotlin.random.Random


@Preview
@Composable
fun SingleParticleTrajectorySample() {

    var progress by remember { mutableFloatStateOf(0f) }

    var trajectoryProgressStart by remember {
        mutableFloatStateOf(0f)
    }

    var trajectoryProgressEnd by remember {
        mutableFloatStateOf(1f)
    }

    val density = LocalDensity.current

    val sizeDp = with(density) {
        1000f.toDp()
    }
    val sizePx = with(density) {
        sizeDp.toPx()
    }
    val sizePxHalf = sizePx / 2

    val particleState = rememberParticleState()

    LaunchedEffect(trajectoryProgressStart, trajectoryProgressEnd) {
        particleState.particleList.clear()
        particleState.addParticle(
            Particle(
                color = Pink400,
                initialCenter = Offset(
                    x = sizePxHalf,
                    y = sizePxHalf,
                ),
                initialSize = Size(5.dp.toPx(), 5.dp.toPx()),
                endSize = Size(sizePx, sizePx),
                velocity = Velocity(
                    x = sizePxHalf,
                    y = sizePxHalf * 4
                ),
                trajectoryProgressRange = trajectoryProgressStart..trajectoryProgressEnd
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {

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

            particleState.particleList.forEach { particle ->
                particleState.updateParticle(progress, particle)
                drawCircle(
                    alpha = particle.alpha,
                    color = particle.color,
                    radius = 5.dp.toPx(),
                    center = particle.currentPosition,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val particle = particleState.particleList.firstOrNull()

        particle?.let {
            Text(
                text = "Progress: ${(progress * 100).toInt() / 100f}\n" +
                        "trajectory: ${(particle.trajectoryProgress * 100).toInt() / 100f}\n" +
                        "currentTime: ${(particle.currentTime * 100f).toInt() / 100f}\n",
                fontSize = 18.sp
            )
        }
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = progress,
            onValueChange = {
                progress = it
            }
        )

        Text("trajectoryProgressStart: $trajectoryProgressStart")
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = trajectoryProgressStart,
            onValueChange = {
                trajectoryProgressStart = it.coerceAtMost(trajectoryProgressEnd)
            },
            valueRange = 0f..1f
        )

        Text("trajectoryProgressEnd: $trajectoryProgressEnd")
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = trajectoryProgressEnd,
            onValueChange = {
                trajectoryProgressEnd = it.coerceAtLeast(trajectoryProgressStart)
            },
            valueRange = 0f..1f
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun ParticleAnimationSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val density = LocalDensity.current
        val widthDp = with(density) {
            500.toDp()
        }

        val particleState = rememberParticleState()

        val context = LocalContext.current

        var progress by remember {
            mutableFloatStateOf(0f)
        }
        Image(
            painter = painterResource(R.drawable.avatar_2_raster),
            modifier = Modifier
                .border(2.dp, Color.Red)
                .size(widthDp)
                .disintegrate(
//                    progress = progress,
                    particleState = particleState,
                    onStart = {
                        Toast.makeText(context, "Animation started...", Toast.LENGTH_SHORT).show()
                    },
                    onEnd = {
                        Toast.makeText(context, "Animation ended...", Toast.LENGTH_SHORT).show()
                    }
                ),
            contentDescription = null
        )

        Slider(
            value = progress,
            onValueChange = {
                progress = it
            }
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                particleState.startAnimation()
            }
        ) {
            Text("Convert graphicsLayer to particles")
        }
    }
}

data class Particle(
    val initialCenter: Offset,
    val initialSize: Size,
    val endSize: Size,
    val color: Color,
    val trajectoryProgressRange: ClosedRange<Float> = 0f..1f,
    var velocity: Velocity = Velocity(0f, 0f),
    var acceleration: Float = -2 * velocity.y
) {
    var currentPosition: Offset = initialCenter
        internal set
    var currentSize: Size = initialSize

    var alpha = 1f
        internal set
    var currentTime: Float = 0f
        internal set
    var trajectoryProgress: Float = 0f
        internal set
}

fun Modifier.disintegrate(
    particleState: ParticleState,
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {}
) = this.then(
    Modifier.disintegrate(
        progress = particleState.progress,
        particleState = particleState,
        onStart = onStart,
        onEnd = onEnd
    )
)

fun Modifier.disintegrate(
    progress: Float,
    particleState: ParticleState,
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {}
) = composed {

    val graphicsLayer = rememberGraphicsLayer()

    val animationStatus = particleState.animationStatus
    val density = LocalDensity.current
    val particleSizePx = with(density) { particleState.particleSize.roundToPx() }

    LaunchedEffect(animationStatus != AnimationStatus.Idle) {
        if (animationStatus != AnimationStatus.Idle) {

            if (particleState.imageBitmap == null) {
                val imageBitmap = graphicsLayer
                    .toImageBitmap()
                    .asAndroidBitmap()
                    .copy(Bitmap.Config.ARGB_8888, false)
                    .asImageBitmap()

                particleState.imageBitmap = imageBitmap

                particleState.createParticles(
                    particleSize = particleSizePx,
                    imageBitmap = imageBitmap
                )
            }

            particleState.animationStatus = AnimationStatus.Playing
            particleState.animate(
                onStart = onStart,
                onEnd = onEnd
            )
        }
    }

    Modifier.drawWithCache {

        onDrawWithContent {

            if (animationStatus != AnimationStatus.Playing) {
                drawContent()
                graphicsLayer.record {
                    this@onDrawWithContent.drawContent()
                }
            }

            if (animationStatus != AnimationStatus.Idle) {

                particleState.particleList.forEach { particle ->

                    particleState.updateParticle(progress, particle)

                    val color = particle.color
                    val radius = particle.currentSize.width * .65f
                    val position = particle.currentPosition
                    val alpha = particle.alpha

                    drawCircle(
                        color = color,
                        radius = radius,
                        center = position,
                        alpha = alpha
                    )
                }

                // TODO disintegrate image non-uniformly, with blend mode of particles
//                clipRect(
//                    left = progress * size.width
//                ) {
//                    this@onDrawWithContent.drawContent()
//                }

                // For debugging
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(progress * size.width, 0f),
                    size = Size(size.width - progress * size.width, size.height),
                    style = Stroke(4.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun rememberParticleState(): ParticleState {
    return remember {
        ParticleState()
    }
}

@Stable
class ParticleState internal constructor() {

    var particleSize by mutableStateOf(2.dp)

    val animatable = Animatable(0f)
    val particleList = mutableStateListOf<Particle>()

    var animationStatus by mutableStateOf(AnimationStatus.Idle)
        internal set

    val progress: Float
        get() = animatable.value

    var imageBitmap: ImageBitmap? = null
        internal set

    fun addParticle(particle: Particle) {
        particleList.add(particle)
    }

    fun createParticles(particleSize: Int, imageBitmap: ImageBitmap) {
        particleList.clear()

        val width = imageBitmap.width
        val height = imageBitmap.height

        val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

        val particleRadius = particleSize / 2

        // divide image into squares based on particle size
        // 110x100x image is divided into 10x10 squares

        for (column in 0 until width step particleSize) {
            for (row in 0 until height step particleSize) {


                // Get pixel at center of this pixel rectangle
                // If last pixel is out of image get it from end of the width or height
                // 🔥x must be < bitmap.width() and y must be < bitmap.height()

                val pixelCenterX = (column + particleRadius).coerceAtMost(width - 1)
                val pixelCenterY = (row + particleRadius).coerceAtMost(height - 1)

                val pixel: Int = bitmap.getPixel(pixelCenterX, pixelCenterY)
                val color = Color(pixel)

                if (color != Color.Unspecified) {
                    val initialCenter = Offset(pixelCenterX.toFloat(), pixelCenterY.toFloat())
                    val horizontalDisplacement = randomInRange(-50f, 50f)

                    val verticalDisplacement = randomInRange(-height * .1f, height * .2f)
                    val acceleration = randomInRange(-2f, 2f)

                    // If this particle is at 20% of image width in x plane
                    // it returns 0.2f
                    val fractionToImageWidth = initialCenter.x / width
                    val fractionToImageHeight = initialCenter.y / height

                    // Get trajectory for each 5 percent of the image in x direction
                    // This creates wave effect where particles at the start animation earlier
                    // For fraction that is 0.2f range returns
                    // (0.15f - offsetMin )..(0.2f + offsetMax)

                    var trajectoryProgressRange = getTrajectoryRange(
                        fraction = fractionToImageWidth,
                        sectionFraction = .05f
                    )

                    // Add some vertical randomization for trajectory so particles don't start
                    // animating vertically as well. Particles with smaller y value in same x
                    // coordinate tend to start earlier
                    trajectoryProgressRange =
                        trajectoryProgressRange.start + fractionToImageHeight * Random.nextFloat()
                            .coerceAtMost(.2f)..trajectoryProgressRange.endInclusive

                    val endSize = randomInRange(0f, particleSize.toFloat() * .5f)

                    particleList.add(
                        Particle(
                            initialCenter = initialCenter,
                            initialSize = Size(particleSize.toFloat(), particleSize.toFloat()),
                            trajectoryProgressRange = trajectoryProgressRange,
                            endSize = Size(endSize, endSize),
                            color = color,
                            velocity = Velocity(
                                x = horizontalDisplacement,
                                y = verticalDisplacement
                            ),
                            acceleration = acceleration
                        )
                    )

                } else {
                    println("Not adding transparent pixel")
                }
            }
        }
    }

    fun updateParticle(progress: Float, particle: Particle) {
        particle.run {
            // Trajectory progress translates progress from 0f-1f to
            // visibilityThresholdLow-visibilityThresholdHigh
            // range. For instance, 0.1-0.6f range movement starts when
            // explosionProgress is at 0.1f and reaches 1f
            // when explosionProgress reaches 0.6f and trajectoryProgress .

            // Each 0.1f change in trajectoryProgress 0.5f total range
            // corresponds to 0.2f change of current time
            setTrajectoryProgress(progress)

            currentTime = trajectoryProgress

            // Set size
            val width =
                initialSize.width + (endSize.width - initialSize.width) * currentTime * .5f
            val height =
                initialSize.height + (endSize.height - initialSize.height) * currentTime * .5f

            currentSize = Size(width, height)

            // Set alpha
            // While trajectory progress is less than 80% have full alpha then slowly
            // reduce to zero for particles to disappear
            alpha = if (trajectoryProgress == 0f) 1f
            else if (trajectoryProgress < .8f) 1f
            else scale(.8f, 1f, trajectoryProgress, 1f, 0f)

            val horizontalDisplacement = velocity.x * trajectoryProgress
            val verticalDisplacement =
                velocity.y * currentTime + 0.5f * acceleration * currentTime * currentTime
            currentPosition = Offset(
                x = initialCenter.x + horizontalDisplacement,
                y = initialCenter.y - verticalDisplacement
            )
        }
    }

    private fun Particle.setTrajectoryProgress(progress: Float) {
        val trajectoryProgressStart = trajectoryProgressRange.start
        val trajectoryProgressEnd = trajectoryProgressRange.endInclusive

        trajectoryProgress =
            if (progress < trajectoryProgressStart) {
                0f
            } else if (progress > trajectoryProgressEnd) {
                1f
            } else {
                scale(
                    a1 = trajectoryProgressStart,
                    b1 = trajectoryProgressEnd,
                    x1 = progress,
                    a2 = 0f,
                    b2 = 1f
                )
            }
    }

    fun startAnimation() {
        animationStatus = AnimationStatus.Initializing
    }

    suspend fun animate(
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        try {
            onStart()
            animatable.snapTo(0f)
            animatable.animateTo(1f, tween(durationMillis = 2400, easing = FastOutSlowInEasing))
            animationStatus = AnimationStatus.Idle
        } catch (e: CancellationException) {
            println("FAILED: ${e.message}")
        } finally {
            onEnd()
            animationStatus = AnimationStatus.Idle
        }
    }
}

/**
 * Calculate range based in [sectionFraction] to create a range for particles to have trajectory.
 * This is for animating particles from start to end instead of every particles animating at once.
 *
 * For [fraction] that might be 0.11, 0.12, 0.15 for [sectionFraction] 0.1
 * range returns 0-0.1 which causes  first 10% to start while rest of the particles are stationary.
 */
fun getTrajectoryRange(
    fraction: Float,
    sectionFraction: Float,
    from: Float = 0f,
    until: Float = 1f,
): ClosedRange<Float> {

    if (sectionFraction == 0f || sectionFraction > 1f) return from..until
    val remainder = fraction % sectionFraction
    val multiplier = fraction / sectionFraction

    val min = (sectionFraction * (multiplier - 4f) - remainder).coerceAtLeast(from)
    val max = (sectionFraction * (multiplier + 15f) - remainder).coerceAtMost(until)

    return min..max
}

enum class AnimationStatus {
    Idle, Initializing, Playing
}
