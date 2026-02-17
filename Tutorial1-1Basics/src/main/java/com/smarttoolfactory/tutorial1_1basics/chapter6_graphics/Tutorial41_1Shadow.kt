package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val colors = listOf(
    Color(0xFF4cc9f0),
    Color(0xFFf72585),
    Color(0xFFb5179e),
    Color(0xFF7209b7),
    Color(0xFF560bad),
    Color(0xFF480ca8),
    Color(0xFF3a0ca3),
    Color(0xFF3f37c9),
    Color(0xFF4361ee),
    Color(0xFF4895ef),
    Color(0xFF4cc9f0)
)

@Preview
@Composable
fun Tutorial6_41Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StyleableTutorialText(
            text = "Use **Modifier.dropShadow** and **Modifier.innerShadow** " +
                    "to create customizable shadows.",
            bullets = false
        )

        TutorialHeader(text = "Drop Shadow")
        DropShadowSample()
        Spacer(modifier = Modifier.height(32.dp))
        TutorialHeader(text = "Inner Shadow")
        InnerShadowSample()
    }
}

@Preview
@Composable
private fun DropShadowSample() {
    var radius by remember {
        mutableFloatStateOf(5f)
    }

    var spread by remember {
        mutableFloatStateOf(5f)
    }

    var offsetX by remember {
        mutableFloatStateOf(5f)
    }

    var offsetY by remember {
        mutableFloatStateOf(5f)
    }

    var alpha by remember {
        mutableFloatStateOf(1f)
    }

    Text(text = "radius: ${radius.roundToInt()}")
    Slider(
        value = radius,
        onValueChange = { radius = it },
        valueRange = 0f..30f,
    )

    Text(text = "spread: ${spread.roundToInt()}")
    Slider(
        value = spread,
        onValueChange = { spread = it },
        valueRange = 0f..30f,
    )

    Text(text = "offsetX: ${offsetX.roundToInt()}")
    Slider(
        value = offsetX,
        onValueChange = { offsetX = it },
        valueRange = -20f..30f,
    )

    Text(text = "offsetY: ${offsetY.roundToInt()}")
    Slider(
        value = offsetY,
        onValueChange = { offsetY = it },
        valueRange = -20f..30f,
    )

    Text(text = "alpha: $alpha")
    Slider(
        value = alpha,
        onValueChange = { alpha = it },
    )

    Spacer(modifier = Modifier.height(32.dp))

            val shape =RoundedCornerShape(16.dp)
//    val shape = CircleShape

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {


        Box(
            Modifier
                .size(120.dp)
                .dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        brush = Brush.sweepGradient(colors),
                        radius = radius.dp,
                        spread = spread.dp,
                        alpha = alpha,
                        offset = DpOffset(x = offsetX.dp, offsetY.dp)
                    )
                )
                .background(
                    color = Color.White,
                    shape = shape
                )
        ) {
            Text(
                "Drop Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        }

        Box(
            Modifier
                .size(120.dp)
                .dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        brush = Brush.sweepGradient(colors),
                        radius = radius.dp,
                        spread = spread.dp,
                        alpha = alpha,
                        offset = DpOffset(x = offsetX.dp, offsetY.dp)
                    )
                )
                .background(
                    color = Color.White.copy(alpha = .7f),
                    shape = shape
                )
        ) {
            Text(
                "Drop Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun InnerShadowSample() {
    var radius by remember {
        mutableFloatStateOf(5f)
    }

    var spread by remember {
        mutableFloatStateOf(5f)
    }

    var offsetX by remember {
        mutableFloatStateOf(5f)
    }

    var offsetY by remember {
        mutableFloatStateOf(5f)
    }

    var alpha by remember {
        mutableFloatStateOf(.5f)
    }

    Text(text = "radius: ${radius.roundToInt()}")
    Slider(
        value = radius,
        onValueChange = { radius = it },
        valueRange = 0f..30f,
    )

    Text(text = "spread: ${spread.roundToInt()}")
    Slider(
        value = spread,
        onValueChange = { spread = it },
        valueRange = 0f..30f,
    )

    Text(text = "offsetX: ${offsetX.roundToInt()}")
    Slider(
        value = offsetX,
        onValueChange = { offsetX = it },
        valueRange = -20f..30f,
    )

    Text(text = "offsetY: ${offsetY.roundToInt()}")
    Slider(
        value = offsetY,
        onValueChange = { offsetY = it },
        valueRange = -20f..30f,
    )

    Text(text = "alpha: $alpha")
    Slider(
        value = alpha,
        onValueChange = { alpha = it },
    )

    Spacer(modifier = Modifier.height(32.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            Modifier
                .size(120.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .innerShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = radius.dp,
                        spread = spread.dp,
                        offset = DpOffset(x = offsetX.dp, offsetY.dp),
                        alpha = alpha
                    )
                )
        ) {
            Text(
                "Inner Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        }

        Box(
            Modifier
                .size(120.dp)
                .background(
                    color = Color.White.copy(alpha = .7f),
                    shape = RoundedCornerShape(20.dp)
                )
                .innerShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = radius.dp,
                        spread = spread.dp,
                        offset = DpOffset(x = offsetX.dp, offsetY.dp),
                        alpha = alpha
                    )
                )
        ) {
            Text(
                "Inner Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF232323
)
@Composable
fun RealisticShadows() {
    Box(Modifier.fillMaxSize()) {
        val dropShadowColor1 = Color(0xB3000000)
        val dropShadowColor2 = Color(0x66000000)

        val innerShadowColor1 = Color(0xCC000000)
        val innerShadowColor2 = Color(0xFF050505)
        val innerShadowColor3 = Color(0x40FFFFFF)
        val innerShadowColor4 = Color(0x1A050505)
        Box(
            Modifier
                .width(300.dp)
                .height(200.dp)
                .align(Alignment.Center)
                .dropShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 40.dp,
                        spread = 0.dp,
                        color = dropShadowColor1,
                        offset = DpOffset(x = 2.dp, 8.dp)
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 0.dp,
                        color = dropShadowColor2,
                        offset = DpOffset(x = 0.dp, 4.dp)
                    )
                )
                // note that the background needs to be defined before defining the inner shadow
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(100.dp)
                )
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 3.dp,
                        color = innerShadowColor1,
                        offset = DpOffset(x = 6.dp, 6.dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 1.dp,
                        color = Color.White,
                        offset = DpOffset(x = 5.dp, 5.dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 5.dp,
                        color = innerShadowColor2,
                        offset = DpOffset(x = (-3).dp, (-12).dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 3.dp,
                        spread = 10.dp,
                        color = innerShadowColor3,
                        offset = DpOffset(x = 0.dp, 0.dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 3.dp,
                        spread = 9.dp,
                        color = innerShadowColor4,
                        offset = DpOffset(x = 1.dp, 1.dp)
                    )
                )
        ) {
            Text(
                "Realistic Shadows",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun NeumorphicRaisedButton(
    shape: RoundedCornerShape = RoundedCornerShape(30.dp)
) {
    val bgColor = Color(0xFFe0e0e0)
    val lightShadow = Color(0xFFFFFFFF)
    val darkShadow = Color(0xFFb1b1b1)
    val upperOffset = -10.dp
    val lowerOffset = 10.dp
    val radius = 15.dp
    val spread = 0.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .wrapContentSize(Alignment.Center)
            .size(240.dp)
            .dropShadow(
                shape,
                shadow = Shadow(
                    radius = radius,
                    color = lightShadow,
                    spread = spread,
                    offset = DpOffset(upperOffset, upperOffset)
                ),
            )
            .dropShadow(
                shape,
                shadow = Shadow(
                    radius = radius,
                    color = darkShadow,
                    spread = spread,
                    offset = DpOffset(lowerOffset, lowerOffset)
                ),

                )
            .background(bgColor, shape)
    )
}

// Define breathing states
enum class BreathingState {
    Inhaling,
    Exhaling
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GradientBasedShadowAnimation() {
    // State for the breathing animation
    var breathingState by remember { mutableStateOf(BreathingState.Inhaling) }

    // Create transition based on breathing state
    val transition = updateTransition(
        targetState = breathingState,
        label = "breathing_transition"
    )

    // Animate spread based on breathing state
    val animatedSpread by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        },
        label = "spread_animation"
    ) { state ->
        when (state) {
            BreathingState.Inhaling -> 10f
            BreathingState.Exhaling -> 2f
        }
    }

    // Animate alpha based on breathing state (optional)
    val animatedAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        },
        label = "alpha_animation"
    ) { state ->
        when (state) {
            BreathingState.Inhaling -> 1f
            BreathingState.Exhaling -> 1f
        }
    }

    // Get text based on current state
    val breathingText = when (breathingState) {
        BreathingState.Inhaling -> "Inhale"
        BreathingState.Exhaling -> "Exhale"
    }

    // Switch states when animation completes
    LaunchedEffect(breathingState) {
        delay(2000) // Wait for animation to complete
        breathingState = when (breathingState) {
            BreathingState.Inhaling -> BreathingState.Exhaling
            BreathingState.Exhaling -> BreathingState.Inhaling
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // [START android_compose_graphics_gradient_shadow]
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(200.dp)
                .dropShadow(
                    shape = RoundedCornerShape(70.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = animatedSpread.dp,
                        brush = Brush.sweepGradient(
                            colors
                        ),
                        offset = DpOffset(x = 0.dp, y = 0.dp),
                        alpha = animatedAlpha
                    )
                )
                .clip(RoundedCornerShape(70.dp))
                .background(Color(0xEDFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = breathingText,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun AnimatedColoredShadows() {
    Box(Modifier.fillMaxSize()) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        // Create transition with pressed state
        val transition = updateTransition(
            targetState = isPressed,
            label = "button_press_transition"
        )

        fun <T> buttonPressAnimation() = tween<T>(
            durationMillis = 400,
            easing = EaseInOut
        )

        // Animate all properties using the transition
        val shadowAlpha by transition.animateFloat(
            label = "shadow_alpha",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) 0f else 1f
        }
        val innerShadowAlpha by transition.animateFloat(
            label = "shadow_alpha",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) 1f else 0f
        }

        val blueDropShadowColor = Color(0x5C007AFF)

        val darkBlueDropShadowColor = Color(0x66007AFF)

        val greyInnerShadowColor1 = Color(0x1A007AFF)

        val greyInnerShadowColor2 = Color(0x1A007AFF)

        val blueDropShadow by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color.Transparent else blueDropShadowColor
        }

        val darkBlueDropShadow by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color.Transparent else darkBlueDropShadowColor
        }

        val innerShadowColor1 by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) greyInnerShadowColor1
            else greyInnerShadowColor2
        }

        val innerShadowColor2 by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color(0x4D007AFF)
            else Color(0x1A007AFF)
        }

        Box(
            Modifier
                .clickable(
                    interactionSource, indication = null
                ) {

                }
                .width(300.dp)
                .height(200.dp)
                .align(Alignment.Center)
                .dropShadow(
                    shape = RoundedCornerShape(70.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 0.dp,
                        color = blueDropShadow,
                        offset = DpOffset(x = 0.dp, -(2).dp),
                        alpha = shadowAlpha
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(70.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 0.dp,
                        color = darkBlueDropShadow,
                        offset = DpOffset(x = 2.dp, 6.dp),
                        alpha = shadowAlpha
                    )
                )
                // note that the background needs to be defined before defining the inner shadow
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(70.dp)
                )
                .innerShadow(
                    shape = RoundedCornerShape(70.dp),
                    shadow = Shadow(
                        radius = 8.dp,
                        spread = 4.dp,
                        color = innerShadowColor2,
                        offset = DpOffset(x = 4.dp, 0.dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(70.dp),
                    shadow = Shadow(
                        radius = 20.dp,
                        spread = 4.dp,
                        color = innerShadowColor1,
                        offset = DpOffset(x = 4.dp, 0.dp),
                        alpha = innerShadowAlpha
                    )
                )

        ) {
            Text(
                "Animated Shadows",
                modifier = Modifier
                    .align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun GlowingShadowAnimationSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val transition: InfiniteTransition = rememberInfiniteTransition()

        // Infinite phase animation for PathEffect
        val phase by transition.animateFloat(
            initialValue = .8f,
            targetValue = .3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val brush = remember {
            Brush.sweepGradient(colors)
        }

        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .dropShadow(
                        shape = RoundedCornerShape(16.dp),
                        shadow = Shadow(
                            radius = (2 + 8 * phase).dp,
                            spread = (2 + phase).dp,
                            offset = DpOffset(0.dp, 0.dp),
                            alpha = phase,
                            brush = brush,
                        )
                    )
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    )
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .innerShadow(
                        shape = RoundedCornerShape(16.dp),
                        shadow = Shadow(
                            radius = (2 + 8 * phase).dp,
                            spread = (2 + phase).dp,
                            offset = DpOffset(0.dp, 0.dp),
                            alpha = phase,
                            brush = brush,
                        )
                    )
            )
        }
    }
}

@Preview
@Composable
private fun GlowingPaintShadowAnimationSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val paint = remember {
            Paint().apply {
                style = PaintingStyle.Stroke
                strokeWidth = 10f
            }
        }

        val frameworkPaint: NativePaint = remember {
            paint.asFrameworkPaint()
        }

        val transition: InfiniteTransition = rememberInfiniteTransition()

        // Infinite phase animation for PathEffect
        val phase by transition.animateFloat(
            initialValue = .8f,
            targetValue = .3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(100.dp)
                    .drawWithCache {
                        paint.shader = SweepGradientShader(
                            center = size.center,
                            colors = colors
                        )

                        onDrawWithContent {
                            this.drawIntoCanvas {
                                frameworkPaint.setMaskFilter(
                                    BlurMaskFilter(
                                        30f * phase,
                                        BlurMaskFilter.Blur.NORMAL
                                    )
                                )

                                it.drawRoundRect(
                                    left = 0f,
                                    top = 0f,
                                    right = size.width,
                                    bottom = size.height,
                                    radiusX = 16.dp.toPx(),
                                    radiusY = 16.dp.toPx(),
                                    paint = paint
                                )


                                drawRoundRect(
                                    Color.White,
                                    topLeft = Offset.Zero,
                                    size = size,
                                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            drawContent()
                        }
                    }
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            )
        }
    }
}

@Preview
@Composable
private fun GlowingPaintShadowAnimationSample2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val transition: InfiniteTransition = rememberInfiniteTransition()

        // Infinite phase animation for PathEffect
        val phase by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val paint = remember {
            Paint().apply {
                style = PaintingStyle.Fill
//                blendMode = BlendMode.SrcIn
            }
        }

        val frameworkPaint: NativePaint = remember {
            paint.asFrameworkPaint()
        }

        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, Brush.sweepGradient(colors), RoundedCornerShape(16.dp))
                    .drawWithCache {

                        onDrawWithContent {

                            val center = this.center

                            paint.shader = SweepGradientShader(
                                center = center,
                                colors = colors
                            )

                            this.drawIntoCanvas { canvas ->
                                with(canvas.nativeCanvas) {
                                    val checkPoint = saveLayer(null, null)

                                    frameworkPaint.setMaskFilter(
                                        // radius should be > 0
                                        BlurMaskFilter(
                                            30f + 120f * phase,
                                            BlurMaskFilter.Blur.NORMAL
                                        )
                                    )
//                                    if (phase > 0) {
//                                        frameworkPaint.setMaskFilter(
//                                            // radius should be > 0
//                                            BlurMaskFilter(
//                                                30f ,
//                                                BlurMaskFilter.Blur.NORMAL
//                                            )
//                                        )
//                                    }else {
//                                        frameworkPaint.setMaskFilter(
//                                           null
//                                        )
//                                    }

                                    // Destination
//                                    drawRoundRect(
//                                        color = Color.Gray,
//                                        topLeft = Offset(-50f, -50f),
//                                        size = Size(size.width + 100f, size.height + 100f),
//                                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
//                                    )

                                    // Source
                                    rotate(angle) {
                                        canvas.drawCircle(
                                            center = center,
                                            radius = size.width / 2f,
                                            paint = paint
                                        )
                                    }

                                    restoreToCount(checkPoint)
                                }
                            }

                            drawContent()
                        }
                    }
//                    .background(Color.White, RoundedCornerShape(16.dp))
            )
        }
    }
}
