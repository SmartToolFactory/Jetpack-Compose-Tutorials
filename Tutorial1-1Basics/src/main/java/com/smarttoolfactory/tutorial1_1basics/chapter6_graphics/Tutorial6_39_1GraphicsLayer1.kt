package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import kotlinx.coroutines.launch
import kotlin.random.Random

@Preview
@Composable
fun GraphicsLayerToImageBitmapSample() {

    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var imageBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    var touchPosition by remember {
        mutableStateOf(Offset.Unspecified)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.avatar_2_raster),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset: Offset ->
                        touchPosition = offset
                    }
                }
                .drawWithContent {
                    drawContent()
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
                .drawWithContent {
                    drawContent()
                    if (touchPosition != Offset.Unspecified) {
                        drawCircle(
                            color = Color.Blue,
                            radius = size.width * .1f,
                            center = touchPosition,
                            style = Stroke(
                                8.dp.toPx(), pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(20f, 20f)
                                )
                            )
                        )
                    }
                }
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .aspectRatio(1f),
            contentDescription = null
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    imageBitmap = graphicsLayer.toImageBitmap()
                }
            }
        ) {
            Text("Convert graphicsLayer to ImageBitmap")
        }

        Text(text = "Screenshot of Composable", fontSize = 22.sp)
        imageBitmap?.let {
            Image(
                bitmap = it,
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .aspectRatio(1f),
                contentDescription = null
            )
        }
    }
}

data class TestParticle(
    val initialCenter: Offset,
    val initialSize: Size,
    val initialAlpha: Float,
    val color: Color,
    val scale: Float = 1f,
    val decayFactor: Int,
) {

    val initialRadius: Float = initialSize.width.coerceAtMost(initialSize.height) / 2f
    var radius: Float = scale * initialRadius

    var alpha: Float = initialAlpha

    val active: Boolean
        get() = radius > 0 && alpha > 0

    var center: Offset = initialCenter

    val initialRect: Rect
        get() = Rect(
            offset = Offset(
                x = initialCenter.x - initialRadius,
                y = initialCenter.y - initialRadius
            ),
            size = initialSize
        )

    val rect: Rect
        get() = Rect(
            offset = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
}

@Preview
@Composable
fun GraphicsLayerToParticles() {
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val particleList = remember {
        mutableStateListOf<TestParticle>()
    }

    var particleSize by remember {
        mutableFloatStateOf(10f)
    }

    val animatable = remember {
        Animatable(1f)
    }

    var duration by remember {
        mutableFloatStateOf(3000f)
    }

    var animateSize by remember {
        mutableStateOf(true)
    }

    var animateAlpha by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val density = LocalDensity.current
        val widthDp = with(density) {
            500.toDp()
        }

        Image(
            painter = painterResource(R.drawable.avatar_2_raster),
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
                .size(widthDp),
            contentDescription = null
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    animatable.snapTo(1f)
                    graphicsLayer.toImageBitmap().let {
                        particleList.clear()
                        particleList.addAll(
                            createParticles(
                                imageBitmap = it.asAndroidBitmap()
                                    .copy(Bitmap.Config.ARGB_8888, false)
                                    .asImageBitmap(),
                                particleSize = particleSize.toInt()
                            )
                        )
                    }
                }
            }
        ) {
            Text("Convert graphicsLayer to particles")
        }

        if (particleList.isEmpty().not()) {

            Canvas(
                modifier = Modifier
                    .border(2.dp, if (animatable.isRunning) Color.Green else Color.Red)
                    .clickable {
                        coroutineScope.launch {
                            animatable.snapTo(0f)
                            animatable.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = duration.toInt(),
                                    easing = LinearEasing
                                ),
                                block = {

                                    val progress = this.value

                                    particleList.forEachIndexed { _, particle ->

                                        if (particle.active) {
                                            val posX = particle.center.x
                                            val posY = particle.center.y

                                            val newX =
                                                posX + 3f * Random.nextFloat()
                                            val newY = posY - 15f * Random.nextFloat()

                                            particle.center =
                                                Offset(newX, newY)

                                            val particleDecayFactor = particle.decayFactor

                                            val decayFactor =
                                                if (progress < .80f) particleDecayFactor
                                                else if (progress < .85f) particleDecayFactor + 1
                                                else if (progress < .9f) particleDecayFactor + 4
                                                else if (progress < .97)
                                                    particleDecayFactor
                                                        .coerceAtLeast(5) + 1
                                                else 1

                                            if (animateSize) {
                                                val radius = particle.radius
                                                val newRadius =
                                                    radius - progress * decayFactor * particle.initialRadius / 100f

                                                particle.radius = newRadius.coerceAtLeast(0f)
                                            }
                                            if (animateAlpha) {
                                                particle.alpha -= (progress) * Random.nextFloat() / 20f
                                            }

                                            if (progress == 1f) {
                                                particle.alpha = 0f
                                            }

                                        }
                                    }

                                    val aliveParticle = particleList.filter { it.active }.size

                                    println("alive particle size: $aliveParticle, progress: $progress")
                                }
                            )
                        }
                    }
                    .size(widthDp)
            ) {

                // TODO Remove this and invalidate Canvas more gracefully
                drawCircle(color = Color.Transparent, radius = animatable.value)

                particleList.forEach { particle: TestParticle ->

                    if (particle.active) {
                        // For debugging borders of particles
//                    val rect = particle.rect
//                    drawRect(
//                        color = Color.Red,
//                        topLeft = rect.topLeft,
//                        size = rect.size,
//                        style = Stroke(1.dp.toPx())
//                    )

                        drawCircle(
                            color = particle.color.copy(alpha = particle.alpha),
                            radius = particle.radius,
                            center = particle.center,
                        )
                    }
                }
            }

            Text(text = "Particle size: ${particleSize.toInt()}px", fontSize = 22.sp)

            Slider(
                value = particleSize,
                onValueChange = {
                    particleSize = it
                },
                valueRange = 2f..100f
            )

            Text("Duration: ${duration.toInt()}", fontSize = 22.sp)
            Slider(
                value = duration,
                onValueChange = {
                    duration = it
                },
                valueRange = 1000f..7000f
            )

            CheckBoxWithTextRippleFullRow(
                label = "Animate size",
                state = animateSize,
                onStateChange = {
                    animateSize = it
                }
            )

            CheckBoxWithTextRippleFullRow(
                label = "Animate alpha",
                state = animateAlpha,
                onStateChange = {
                    animateAlpha = it
                }
            )
        }
    }
}

fun createParticles(imageBitmap: ImageBitmap, particleSize: Int): List<TestParticle> {
    val particleList = mutableStateListOf<TestParticle>()

    val width = imageBitmap.width
    val height = imageBitmap.height

    val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

    val columnCount = width / particleSize
    val rowCount = height / particleSize

    println(
        "Bitmap width: $width, height: $height, " +
                "columnCount: $columnCount, rowCount: $rowCount"
    )

    val particleRadius = particleSize / 2

    // divide image into squares based on particle size
    // 110x100x image is divided into 10x10 squares

    for (posX in 0 until width step particleSize) {
        for (posY in 0 until height step particleSize) {

            // TODO Assign these params
            val scale = Random.nextInt(95, 110) / 100f
//            val scale = 1f
            val decayFactor = Random.nextInt(10)
//            val alpha = Random.nextFloat().coerceAtLeast(.5f)
            val alpha = 1f

            // Get pixel at center of this pixel rectangle
            // If last pixel is out of image get it from end of the width or height
            // 🔥x must be < bitmap.width() and y must be < bitmap.height()
            val pixelCenterX = (posX + particleRadius).coerceAtMost(width - 1)
            val pixelCenterY = (posY + particleRadius).coerceAtMost(height - 1)

            val pixel: Int = bitmap.getPixel(pixelCenterX, pixelCenterY)
            val color = Color(pixel)

            if (color != Color.Unspecified) {
                val size = particleSize * 1f

                particleList.add(
                    TestParticle(
                        initialCenter = Offset(
                            x = pixelCenterX.toFloat(),
                            y = pixelCenterY.toFloat()
                        ),
                        initialSize = Size(size, size),
                        initialAlpha = alpha,
                        color = color,
                        scale = scale,
                        decayFactor = decayFactor
                    )
                )
            } else {
                println("Not adding transparent pixel")
            }
        }
    }

    return particleList
}
