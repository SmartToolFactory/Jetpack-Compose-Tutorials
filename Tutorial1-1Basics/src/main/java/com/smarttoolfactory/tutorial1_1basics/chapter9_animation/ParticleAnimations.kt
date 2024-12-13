package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.MessageStatus
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.SentMessageRowAlt
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.randomInRange
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.scale
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

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

    val particleSize = with(density) {
        5.dp.toPx()
    }

    LaunchedEffect(trajectoryProgressStart, trajectoryProgressEnd) {
        particleState.particleList.clear()

        val velocity = Velocity(
            x = sizePxHalf,
            y = -sizePxHalf * 4
        )

        particleState.addParticle(
            Particle(
                color = Pink400,
                initialCenter = Offset(
                    x = sizePxHalf,
                    y = sizePxHalf,
                ),
                initialSize = Size(particleSize, particleSize),
                endSize = Size(sizePx, sizePx),
                velocity = velocity,
                acceleration = Acceleration(0f, -2 * velocity.y),
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

        Text("Progress: ${(progress * 100).roundToInt() / 100f}")
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
        val particleState2 = rememberParticleState()

        val context = LocalContext.current

        var progress by remember {
            mutableFloatStateOf(0f)
        }

        SentMessageRowAlt(
            modifier = Modifier
                .clickable {
                    particleState.startAnimation()
                }
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
            quotedImage = R.drawable.avatar_4_raster,
            text = "Some long message",
            messageTime = "11.02.2024",
            messageStatus = MessageStatus.READ
        )

        Spacer(Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.avatar_2_raster),
            modifier = Modifier
                .border(2.dp, Color.Red)
                .size(widthDp)
                .clickable {
                    particleState2.forceUpdateProgress = true
                    particleState2.startAnimation()
                }
                .disintegrate(
                    progress = progress,
                    particleState = particleState2,
                    onStart = {
                        Toast.makeText(context, "Animation started...", Toast.LENGTH_SHORT).show()
                    },
                    onEnd = {
                        Toast.makeText(context, "Animation ended...", Toast.LENGTH_SHORT).show()
                    }
                ),
            contentDescription = null
        )

        Text(
            text = "Progress: ${(progress * 100).roundToInt() / 100f}",
            fontSize = 22.sp,
            color = Color.White
        )

        Slider(
            value = progress,
            onValueChange = {
                progress = it
            }
        )
    }
}

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

            withContext(Dispatchers.Default) {

                val currentBitmap = particleState.bitmap?.asAndroidBitmap()

                val bitmap =
                    if (currentBitmap == null || currentBitmap.isRecycled) {
                        graphicsLayer
                            .toImageBitmap()
                            .asAndroidBitmap()
                            .copy(Bitmap.Config.ARGB_8888, false)
                            .apply {
                                this.prepareToDraw()
                            }

                    } else particleState.bitmap?.asAndroidBitmap()

                bitmap?.let {
                    particleState.bitmap = bitmap.asImageBitmap()
                    particleState.createParticles(
                        particleList = particleState.particleList,
                        particleSize = particleSizePx,
                        bitmap = bitmap
                    )

                    withContext(Dispatchers.Main) {
                        particleState.animationStatus = AnimationStatus.Playing
                        particleState.animate(
                            onStart = onStart,
                            onEnd = onEnd
                        )
                    }
                }
            }
        }
    }

    Modifier
        .drawWithCache {
            onDrawWithContent {
                if (animationStatus != AnimationStatus.Playing) {
                    drawContent()
                    graphicsLayer.record {
                        this@onDrawWithContent.drawContent()
                    }
                } else {
                    particleState.updateAndDrawParticles(
                        drawScope = this,
                        particleList = particleState.particleList,
                        bitmap = particleState.bitmap,
                        progress = progress
                    )
                }
            }
        }
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

@Composable
fun rememberParticleState(
    particleSize: Dp = 2.dp,
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 2000,
        easing = FastOutLinearInEasing
    ),
    strategy: ParticleStrategy = DisintegrateStrategy()
): ParticleState {
    return remember(
//        particleSize, strategy, animationSpec
    ) {
        ParticleState(
            particleSize = particleSize,
            animationSpec = animationSpec,
            strategy = strategy
        )
    }
}

@Stable
class ParticleState internal constructor(
    val particleSize: Dp,
    val animationSpec: AnimationSpec<Float>,
    val strategy: ParticleStrategy,
) {
    val animatable = Animatable(0f)
    val particleList = mutableStateListOf<Particle>()

    var animationStatus by mutableStateOf(AnimationStatus.Idle)
        internal set

    val progress: Float
        get() = animatable.value

    var bitmap: ImageBitmap? = null
        internal set

    var forceUpdateProgress: Boolean = false

    fun addParticle(particle: Particle) {
        particleList.add(particle)
    }

    fun updateAndDrawParticles(
        drawScope: DrawScope,
        particleList: SnapshotStateList<Particle>,
        bitmap: ImageBitmap?,
        progress: Float
    ) {
        if (animationStatus != AnimationStatus.Idle) {
            bitmap?.let {
                strategy.updateAndDrawParticles(drawScope, particleList, bitmap, progress)
            }
        }
    }

    fun createParticles(
        particleList: SnapshotStateList<Particle>,
        particleSize: Int,
        bitmap: Bitmap
    ) {
        strategy.createParticles(particleList, particleSize, bitmap)
    }

    fun updateParticle(progress: Float, particle: Particle) {
        strategy.updateParticle(progress, particle)
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
            if (forceUpdateProgress.not()) {
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = animationSpec
                )
            }
        } catch (e: CancellationException) {
            Log.e("Particle", "${e.message}")
        } finally {
            if (forceUpdateProgress.not()) {
                animationStatus = AnimationStatus.Idle
                onEnd()
            }
        }
    }

    fun dispose() {
        bitmap?.asAndroidBitmap()?.recycle()
    }
}

open class DisintegrateStrategy : ParticleStrategy {

    override fun createParticles(
        particleList: SnapshotStateList<Particle>,
        particleSize: Int,
        bitmap: Bitmap
    ) {

        particleList.clear()

        val width = bitmap.width
        val height = bitmap.height

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
                    // Set center
                    val initialCenter = Offset(pixelCenterX.toFloat(), pixelCenterY.toFloat())

                    // If this particle is at 20% of image width in x plane
                    // it returns 0.2f
                    val fractionToImageWidth = (initialCenter.x - particleRadius) / width

                    // Get trajectory for each 5 percent of the image in x direction
                    // This creates wave effect where particles at the start animation earlier
                    val sectionFraction = ParticleCreationFraction / 10f

                    // This range is between 0-0.5f to display all of the particles
                    // until half of the progress is reached
                    var trajectoryProgressRange = getTrajectoryRange(
                        fraction = fractionToImageWidth,
                        sectionFraction = sectionFraction,
                        until = ParticleCreationFraction
                    )

                    // Add randomization for trajectory so particles don't start
                    // animating in each 5% section vertically
                    val minOffset = randomInRange(-sectionFraction, sectionFraction)

                    val start = (trajectoryProgressRange.start + minOffset)
                        .coerceAtLeast(0f)
                    val end = (start + 0.5f).coerceAtMost(1f)
                    trajectoryProgressRange = start..end

                    val imageMinDimension = width.coerceAtMost(height) * 1f

                    val velocityX = randomInRange(
                        // Particles close to end should have less randomization to start of image
                        -(particleSize * 20f * (1 - fractionToImageWidth))
                            .coerceAtMost(imageMinDimension),
                        (particleSize * 20f).coerceAtMost(imageMinDimension)
                    )
                    val velocityY = randomInRange(
                        -(particleSize * 30f).coerceAtMost(imageMinDimension),
                        (particleSize * 30f).coerceAtMost(imageMinDimension)
                    )

                    val acceleration =
                        Acceleration(0f, randomInRange(-velocityY * .1f, -velocityY * .2f))

                    // Set initial and final sizes
                    val initialSize = Size(particleSize.toFloat(), particleSize.toFloat())
                    val endSize = randomInRange(.4f, particleSize.toFloat() * .7f)

                    particleList.add(
                        Particle(
                            initialCenter = initialCenter,
                            initialSize = initialSize,
                            endSize = Size(endSize, endSize),
                            trajectoryProgressRange = trajectoryProgressRange,
                            color = color,
                            velocity = Velocity(
                                x = velocityX,
                                y = velocityY
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

    override fun updateAndDrawParticles(
        drawScope: DrawScope,
        particleList: SnapshotStateList<Particle>,
        imageBitmap: ImageBitmap,
        progress: Float,
    ) {
        with(drawScope) {

            drawWithLayer {
                particleList.forEach { particle ->
                    updateParticle(progress, particle)

                    val color = particle.color
                    val radius = particle.currentSize.width * .5f
                    val position = particle.currentPosition
                    val alpha = particle.alpha

                    // Destination
                    drawCircle(
                        color = color,
                        radius = radius,
                        center = position,
                        alpha = alpha
                    )
                }

                clipRect(
                    left = progress * size.width * 2f
                ) {
                    drawImage(
                        image = imageBitmap,
                        blendMode = BlendMode.SrcOut
                    )
                }

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

    override fun updateParticle(progress: Float, particle: Particle) {
        particle.run {
            // Trajectory progress translates progress from 0f-1f to
            // trajectoryStart-trajectoryEnd
            // range. For instance, for trajectory with 0.1-0.6f, trajectoryProgress starts when
            // progress is at 0.1f and reaches 1f when progress is at 0.6f.

            // Scale from trajectory to progress, for 0.1-06f trajectory 0.35f(half of range)
            // corresponds to 0.5f, to trajectoryProgress
            setTrajectoryProgress(progress)

            currentTime = trajectoryProgress

            // Set size
            val width =
                initialSize.width + (endSize.width - initialSize.width) * currentTime
            val height =
                initialSize.height + (endSize.height - initialSize.height) * currentTime
            currentSize = Size(width, height)

            // Set alpha
            // While trajectory progress is less than 40% have full alpha then slowly
            // reduce to zero for particles to disappear
            alpha = if (trajectoryProgress == 0f) 0f
            else if (trajectoryProgress < .4f) 1f
            else scale(.4f, 1f, trajectoryProgress, 1f, 0f)

            // Set position
            val horizontalDisplacement =
                velocity.x * currentTime + 0.5f * acceleration.x * currentTime * currentTime
            val verticalDisplacement =
                velocity.y * currentTime + 0.5f * acceleration.y * currentTime * currentTime

            currentPosition = Offset(
                x = initialCenter.x + horizontalDisplacement,
                y = initialCenter.y + verticalDisplacement
            )
        }
    }
}

data class ParticleBoundaries(
    val velocityLowerBound: Velocity? = null,
    val velocityUpperBound: Velocity? = null,
    val accelerationLowerBound: Acceleration? = null,
    val accelerationLowerUpperBound: Acceleration? = null,
    val startSizeLowerBound: Size? = null,
    val startSizeUpperBound: Size? = null,
    val endSizeLowerBound: Size? = null,
    val endSizeUpperBound: Size? = null,
    val alphaLowerBound: ClosedRange<Float>? = null,
    val alphaUpperbound: ClosedRange<Float>? = null
)

interface ParticleStrategy {

    fun createParticles(
        particleList: SnapshotStateList<Particle>,
        particleSize: Int,
        bitmap: Bitmap
    )

    fun updateAndDrawParticles(
        drawScope: DrawScope,
        particleList: SnapshotStateList<Particle>,
        imageBitmap: ImageBitmap,
        progress: Float
    )

    fun updateParticle(progress: Float, particle: Particle)
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

    val min = ((fraction - remainder) * (until - from)).coerceIn(from, until)
    val max = (min + sectionFraction).coerceAtMost(until)

    return min..max
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

data class Particle(
    val initialCenter: Offset,
    val initialSize: Size,
    val endSize: Size,
    val trajectoryProgressRange: ClosedRange<Float> = 0f..1f,
    var color: Color,
    var velocity: Velocity = Velocity(0f, 0f),
    var acceleration: Acceleration = Acceleration(0f, 0f)
) {
    var currentPosition: Offset = initialCenter
        internal set
    var currentSize: Size = initialSize
        internal set
    var alpha = 1f
        internal set
    var currentTime: Float = 0f
        internal set
    var trajectoryProgress: Float = 0f
        internal set
}

enum class AnimationStatus {
    Idle, Initializing, Playing
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

private const val ParticleCreationFraction = 0.5f

@Stable
fun Acceleration(x: Float, y: Float) = Acceleration(packFloats(x, y))

/**
 * A two dimensional velocity in pixels per second.
 */
@Immutable
@JvmInline
value class Acceleration internal constructor(private val packedValue: Long) {
    /**
     * The horizontal component of the velocity in pixels per second.
     */
    @Stable
    val x: Float get() = unpackFloat1(packedValue)

    /**
     * The vertical component of the velocity in pixels per second.
     */
    @Stable
    val y: Float get() = unpackFloat2(packedValue)

    /**
     * The horizontal component of the velocity in pixels per second.
     */
    @Stable
    inline operator fun component1(): Float = x

    /**
     * The vertical component of the velocity in pixels per second.
     */
    @Stable
    inline operator fun component2(): Float = y
}
