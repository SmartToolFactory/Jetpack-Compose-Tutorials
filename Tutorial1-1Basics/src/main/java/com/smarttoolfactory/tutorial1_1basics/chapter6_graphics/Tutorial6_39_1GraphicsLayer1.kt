package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Direction {
    Top, TopStart, TopEnd, Bottom, BottomStart, BottomEnd
}

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

@Preview
@Composable
fun GraphicsLayerToParticles() {
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val particleList = remember {
        mutableStateListOf<Particle>()
    }

    var particleSize by remember {
        mutableFloatStateOf(10f)
    }

    val animatable = remember {
        Animatable(1f)
    }

    var ticker by remember {
        mutableIntStateOf(0)
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

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            ticker++
        }
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
                    .clickable {
                        coroutineScope.launch {
                            animatable.snapTo(1f)
                            animatable.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(duration.toInt()),
                                block = {
                                    val progress = this.value
                                    particleList.forEachIndexed { index, particle ->
                                        particle.x =
                                            (particle.x + 5f * progress * Random.nextFloat()).toInt()

                                        particle.y =
                                            (particle.y - 20f * progress * Random.nextFloat()).toInt()


                                        if (animateSize) {
                                            val newRadius =
                                                (particle.radius - (1 - progress) * Random.nextInt(2))
                                            particle.radius = newRadius

                                        }
                                        if (animateAlpha) {
                                            particle.alpha -= (1 - progress) * Random.nextFloat()
                                                .coerceAtMost(.2f)
                                        }
                                    }
                                }
                            )
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
                    .size(widthDp)
            ) {

                // TODO Remove this and invalidate Canvas more gracefully
                drawCircle(color = Color.Transparent, radius = ticker.toFloat())

                particleList.forEach { particle: Particle ->

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
                        radius = particle.radius * 1f,
                        center = particle.center,
                    )
                }
            }

            Text(text = "Particle size: ${particleSize.toInt()}px", fontSize = 22.sp)

            Slider(
                value = particleSize,
                onValueChange = {
                    particleSize = it
                },
                valueRange = 2f..50f
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

fun createParticles(imageBitmap: ImageBitmap, particleSize: Int): List<Particle> {
    val particleList = mutableStateListOf<Particle>()

    val width = imageBitmap.width
    val height = imageBitmap.height

    val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

    val columnCount = width / particleSize
    val rowCount = height / particleSize

    println(
        "Bitmap width: $width, height: $height, " +
                "columnCount: $columnCount, rowCount: $rowCount"
    )

    for (posX in 0 until width step particleSize) {
        for (posY in 0 until height step particleSize) {

            val pixel: Int = bitmap.getPixel(posX, posY)
            val color = Color(pixel)

            println("posX: $posX, posY: $posY, color: $color")

            if (color != Color.Unspecified) {
                particleList.add(
                    Particle(
                        x = posX,
                        y = posY,
                        width = particleSize,
                        height = particleSize,
                        color = color
                    )
                )
            } else {
                println("Not adding transparent pixel")
            }
        }
    }

    return particleList
}

