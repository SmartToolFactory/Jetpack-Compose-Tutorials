package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.model.Snack
import com.smarttoolfactory.tutorial1_1basics.model.snacks
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.FavoriteButton
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import kotlinx.coroutines.launch


@Preview
@Composable
fun Tutorial6_30Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    LerpAnimationSample()
}

@Preview
@Composable
fun LerpAnimationSample() {
    lerp(1f, 1f, 0f)

    val animatable = remember {
        Animatable(0f)
    }

    val coroutineScope = rememberCoroutineScope()
    val animatedCardList = remember {
        mutableStateListOf<AnimatedCard>().apply {
            snacks.forEach {
                add(AnimatedCard(snack = it, color = getRandomColor()))
            }
        }
    }
    var selectedIndex by remember {
        mutableStateOf(0)
    }


    BackHandler(enabled = animatable.targetValue != 0f) {
        coroutineScope.launch {
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        BoxWithConstraints {
            val maxWidth = constraints.maxWidth.toFloat()

            val density = LocalDensity.current
            val endHeight = density.run { 310.dp.toPx() }

            val animatedCard = animatedCardList[selectedIndex]

            val startRect = animatedCard.rect

            val endRect = Rect(
                offset = Offset.Zero,
                size = Size(
                    maxWidth, endHeight
                )
            )

            TopAppBar(
                backgroundColor = Color.White,
                title = {
                    Text("Title")
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )

            Column {
                Spacer(modifier = Modifier.fillMaxWidth().height(72.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {

                    itemsIndexed(snacks) { index, snack ->
                        SnackCard(
                            modifier = Modifier.size(210.dp)
                                .onGloballyPositioned {

                                    // 🔥boundsInX rectangles return from (0,0) position and
                                    // size is set as actual size - size offscreen
                                    val rect = Rect(
                                        offset = it.positionInRoot(),
                                        size = it.size.toSize()
                                    )
                                    animatedCardList[index] = animatedCardList[index]
                                        .copy(rect = rect)

                                },
                            snack = snack,
                            textColor = animatedCardList[index].color,
                            onClick = {
                                selectedIndex = index
                                coroutineScope.launch {
                                    animatable.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            1000,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (animatable.value > 0) {

                println("Animating start: $startRect, endRect: $endRect")

                val progress = animatable.value

                val interpolatedRect =
                    androidx.compose.ui.geometry.lerp(startRect, endRect, progress)

                val sizeDp = density.run { interpolatedRect.size.toDpSize() }
                val intOffset = interpolatedRect.topLeft.round()

                SnackCard(
                    modifier = Modifier
                        .offset {
                            intOffset
                        }
                        .size(sizeDp),
                    snack = animatedCard.snack,
                    progress = progress,
                    textColor = animatedCardList[selectedIndex].color,
                    onClick = {}
                )
            }
        }
    }
}

data class AnimatedCard(
    val snack: Snack,
    val color: Color,
    val rect: Rect = Rect.Zero
)

@Composable
fun SnackCard(
    modifier: Modifier = Modifier,
    snack: Snack,
    progress: Float = 0f,
    textColor: Color,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            // 🔥 Interpolate corner radius
            .clip(RoundedCornerShape(lerp(20.dp, 0.dp, progress)))
            .background(Color.White)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.TopEnd
    ) {


        // 🔥 This is lerping between .77f and 1f by changing start from 0f to .6f
        val fraction = scale(0f, 1f, progress, .77f, 1f)

        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(fraction),
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = snack.imageUrl)
                    .apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                        placeholder(drawableResId = R.drawable.placeholder)
                    }).build()
            ),
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.BottomStart)

        ) {
            Text(
                // 🔥 Interpolate Font size
                fontSize = lerp(18.sp, 40.sp, progress),
                // 🔥 Interpolate Color
                color = lerp(textColor, Color.Black, progress),
                fontWeight = FontWeight.Bold,
                text = snack.name
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    // 🔥 Interpolate Font size
                    fontSize = lerp(12.sp, 24.sp, progress),
                    // 🔥 Interpolate Color
                    color = lerp(textColor, Color.Black, progress),
                    text = "$${snack.price}"
                )
            }
        }

        FavoriteButton(
            modifier = Modifier.graphicsLayer {
                alpha = 1 - progress
            }
                .padding(12.dp),
            color = textColor
        )
    }
}

@Preview
@Composable
fun Test() {
    val scaledValue = scale(0f, 1f, 0.5f, 100f, 200f)

    println("scaledValue: $scaledValue")
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))


// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)
