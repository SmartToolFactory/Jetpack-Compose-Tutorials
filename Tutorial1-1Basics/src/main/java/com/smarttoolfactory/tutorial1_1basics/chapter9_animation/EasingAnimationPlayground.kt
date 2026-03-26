package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.EaseInOutQuint
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.EaseInQuint
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.lerp as colorLerp

private const val EASING_PLAYGROUND_MIN_DURATION = 200f
private const val EASING_PLAYGROUND_MAX_DURATION = 2400f
private val PlaygroundBackgroundColor = Color(0xFFF4F5F7)
private val PlaygroundPanelColor = Color(0xFFFFFFFF)
private val PlaygroundTrackColor = Color(0xFFD7DCE2)
private val PlaygroundPrimaryTextColor = Color(0xFF171A1F)
private val PlaygroundSecondaryTextColor = Color(0xFF69707A)
private val PlaygroundAccentColor = Color(0xFFFFB300)
private val PlaygroundAccentSecondaryColor = Color(0xFFFFC83D)
private val PlaygroundReferencePanelColor = Color(0xFFF1F2F4)
private val PlaygroundReferenceBorderColor = Color(0xFF242B33)
private val PlaygroundReferenceSquareColor = Color(0xFF58E0A2)
private val PlaygroundReferenceBlueColor = Color(0xFF4AA2FF)

private data class EasingOption(
    val name: String,
    val easing: Easing,
)

private val EasingOptions = listOf(
    EasingOption("Linear", LinearEasing),
    EasingOption("FastOutSlowIn", FastOutSlowInEasing),
    EasingOption("FastOutLinearIn", FastOutLinearInEasing),
    EasingOption("LinearOutSlowIn", LinearOutSlowInEasing),
    EasingOption("Ease", Ease),
    EasingOption("EaseIn", EaseIn),
    EasingOption("EaseOut", EaseOut),
    EasingOption("EaseInOut", EaseInOut),
    EasingOption("EaseInBack", EaseInBack),
    EasingOption("EaseOutBack", EaseOutBack),
    EasingOption("EaseInOutBack", EaseInOutBack),
    EasingOption("EaseInBounce", EaseInBounce),
    EasingOption("EaseOutBounce", EaseOutBounce),
    EasingOption("EaseInOutBounce", EaseInOutBounce),
    EasingOption("EaseInElastic", EaseInElastic),
    EasingOption("EaseOutElastic", EaseOutElastic),
    EasingOption("EaseInOutElastic", EaseInOutElastic),
    EasingOption("EaseInExpo", EaseInExpo),
    EasingOption("EaseOutExpo", EaseOutExpo),
    EasingOption("EaseInOutExpo", EaseInOutExpo),
    EasingOption("EaseInQuad", EaseInQuad),
    EasingOption("EaseOutQuad", EaseOutQuad),
    EasingOption("EaseInOutQuad", EaseInOutQuad),
    EasingOption("EaseInCubic", EaseInCubic),
    EasingOption("EaseOutCubic", EaseOutCubic),
    EasingOption("EaseInOutCubic", EaseInOutCubic),
    EasingOption("EaseInQuart", EaseInQuart),
    EasingOption("EaseOutQuart", EaseOutQuart),
    EasingOption("EaseInOutQuart", EaseInOutQuart),
    EasingOption("EaseInQuint", EaseInQuint),
    EasingOption("EaseOutQuint", EaseOutQuint),
    EasingOption("EaseInOutQuint", EaseInOutQuint),
    EasingOption("EaseInSine", EaseInSine),
    EasingOption("EaseOutSine", EaseOutSine),
    EasingOption("EaseInOutSine", EaseInOutSine),
    EasingOption("EaseInCirc", EaseInCirc),
    EasingOption("EaseOutCirc", EaseOutCirc),
    EasingOption("EaseInOutCirc", EaseInOutCirc),
)

@Preview(
    name = "Easing Playground",
    showBackground = true,
    backgroundColor = 0xFFF4F5F7,
    widthDp = 420,
    heightDp = 960,
)
@Composable
private fun EasingAnimationPlaygroundPreview() {
    MaterialTheme {
        EasingAnimationPlayground(
            modifier = Modifier
                .fillMaxSize()
                .background(PlaygroundBackgroundColor)
        )
    }
}

@Composable
fun EasingAnimationPlayground(
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    var animationJob by remember { mutableStateOf<Job?>(null) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var durationMillis by remember { mutableFloatStateOf(900f) }
    var autoReplay by remember { mutableStateOf(true) }

    val selectedEasing = EasingOptions[selectedIndex]

    val replayAnimation: () -> Unit = {
        val clampedDuration = durationMillis.roundToInt().coerceAtLeast(1)
        animationJob?.cancel()
        animationJob = coroutineScope.launch {
            progress.stop()
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = clampedDuration,
                    easing = selectedEasing.easing,
                ),
            )
        }
    }

    val maybeReplay: () -> Unit = {
        if (autoReplay) {
            replayAnimation()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Easing Playground",
            color = PlaygroundPrimaryTextColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Preview the motion curve and the actual tween movement for different Compose easings.",
            color = PlaygroundSecondaryTextColor,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            pageSpacing = 12.dp,
        ) {
            when (it) {
                0 -> {
                    EasingTrackSamplePage(
                        easing = selectedEasing.easing,
                        progress = progress.value,
                    )
                }

                else -> {
                    EasingReferenceExamplesPage(
                        easingName = selectedEasing.name,
                        easing = selectedEasing.easing,
                        progress = progress.value,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedIndex = if (selectedIndex == 0) {
                        EasingOptions.lastIndex
                    } else {
                        selectedIndex - 1
                    }
                    maybeReplay()
                },
            ) {
                Text("Previous")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = replayAnimation,
            ) {
                Text("Play")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedIndex = if (selectedIndex == EasingOptions.lastIndex) {
                        0
                    } else {
                        selectedIndex + 1
                    }
                    maybeReplay()
                },
            ) {
                Text("Next")
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = selectedEasing.name,
                    color = PlaygroundPrimaryTextColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${selectedIndex + 1} / ${EasingOptions.size}",
                    color = PlaygroundSecondaryTextColor,
                    fontSize = 14.sp,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Auto replay on change",
                    color = PlaygroundPrimaryTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
                Switch(
                    checked = autoReplay,
                    onCheckedChange = { autoReplay = it },
                )
            }

            EasingSlider(
                label = "Duration",
                valueText = "${durationMillis.roundToInt()} ms",
                value = durationMillis,
                valueRange = EASING_PLAYGROUND_MIN_DURATION..EASING_PLAYGROUND_MAX_DURATION,
                onValueChange = { durationMillis = it },
                onValueChangeFinished = maybeReplay,
            )

            Text(
                text = "Curve sample",
                color = PlaygroundPrimaryTextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )

            EasingSampleTable(
                easing = selectedEasing.easing,
                progress = progress.value,
            )
        }
    }
}

@Composable
private fun EasingTrackSamplePage(
    easing: Easing,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(PlaygroundPanelColor)
            .padding(24.dp)
    ) {
        val dotSize = lerp(18.dp, 30.dp, progress)
        val trackWidth = (maxWidth - dotSize).coerceAtLeast(0.dp)
        val dotOffset = trackWidth * progress

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(PlaygroundTrackColor)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .align(Alignment.TopStart)
        ) {
            drawLine(
                color = PlaygroundTrackColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = PlaygroundTrackColor,
                start = Offset(0f, size.height),
                end = Offset(0f, 0f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )

            val path = Path().apply {
                moveTo(0f, size.height)
                for (step in 1..120) {
                    val fraction = step / 120f
                    val eased = easing.transform(fraction)
                    lineTo(
                        x = size.width * fraction,
                        y = size.height - (size.height * eased),
                    )
                }
            }

            drawPath(
                path = path,
                color = PlaygroundAccentColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

            drawCircle(
                color = PlaygroundAccentSecondaryColor,
                radius = 5.dp.toPx(),
                center = Offset(
                    x = size.width * progress,
                    y = size.height - (size.height * easing.transform(progress)),
                ),
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 144.dp)
                .height(40.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .offset(x = dotOffset)
                    .size(dotSize)
                    .align(Alignment.CenterStart)
                    .background(
                        color = PlaygroundAccentColor,
                        shape = CircleShape,
                    )
            )
        }
    }
}

@Composable
private fun EasingReferenceExamplesPage(
    easingName: String,
    easing: Easing,
    progress: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(PlaygroundReferencePanelColor)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EasingReferenceChart(
            easingName = easingName,
            easing = easing,
            progress = progress,
            modifier = Modifier
                .weight(0.82f)
                .fillMaxHeight(),
        )

        EasingStoryboardExample(
            easing = easing,
            progress = progress,
            modifier = Modifier
                .weight(1.08f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun EasingReferenceChart(
    easingName: String,
    easing: Easing,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = easingName,
            color = PlaygroundPrimaryTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val axisInsetX = 16.dp.toPx()
            val axisInsetBottom = 18.dp.toPx()
            val topInset = 8.dp.toPx()
            val rightInset = 8.dp.toPx()

            val origin = Offset(axisInsetX, size.height - axisInsetBottom)
            val contentWidth = (size.width - axisInsetX - rightInset).coerceAtLeast(1f)
            val contentHeight = (size.height - axisInsetBottom - topInset).coerceAtLeast(1f)

            drawLine(
                color = PlaygroundReferenceBorderColor,
                start = origin,
                end = Offset(origin.x + contentWidth, origin.y),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = PlaygroundReferenceBorderColor,
                start = origin,
                end = Offset(origin.x, origin.y - contentHeight),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )

            val path = Path().apply {
                moveTo(origin.x, origin.y)
                for (step in 1..120) {
                    val fraction = step / 120f
                    val eased = easing.transform(fraction)
                    lineTo(
                        x = origin.x + contentWidth * fraction,
                        y = origin.y - contentHeight * eased,
                    )
                }
            }

            drawPath(
                path = path,
                color = Color(0xFF3B82F6),
                style = Stroke(
                    width = 3.2.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

            val easedProgress = easing.transform(progress)
            drawCircle(
                color = PlaygroundReferenceSquareColor,
                radius = 13.dp.toPx(),
                center = Offset(
                    x = origin.x + contentWidth * progress,
                    y = origin.y - contentHeight * easedProgress,
                ),
            )
        }
    }
}

@Composable
private fun EasingStoryboardExample(
    easing: Easing,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .border(width = 1.dp, color = PlaygroundReferenceBorderColor)
    ) {
        val leftWidth = maxWidth * 0.34f
        val rightWidth = maxWidth - leftWidth
        val cellWidth = rightWidth / 2
        val halfHeight = maxHeight / 2
        val baseSquareSize = 24.dp
        val easedProgress = easing.transform(progress)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 1.dp.toPx()
            val leftSplit = size.width * 0.34f
            val middleSplit = leftSplit + (size.width - leftSplit) / 2f
            val half = size.height / 2f

            drawLine(
                color = PlaygroundReferenceBorderColor,
                start = Offset(leftSplit, 0f),
                end = Offset(leftSplit, size.height),
                strokeWidth = stroke,
            )
            drawLine(
                color = PlaygroundReferenceBorderColor,
                start = Offset(middleSplit, 0f),
                end = Offset(middleSplit, size.height),
                strokeWidth = stroke,
            )
            drawLine(
                color = PlaygroundReferenceBorderColor,
                start = Offset(leftSplit, half),
                end = Offset(size.width, half),
                strokeWidth = stroke,
            )
        }

        StoryboardSquare(
            containerLeft = 0.dp,
            containerTop = 0.dp,
            containerWidth = leftWidth,
            containerHeight = maxHeight,
            xFraction = 0.5f,
            yFraction = 0.06f + (0.78f - 0.06f) * easedProgress,
            squareSize = baseSquareSize,
        )

        StoryboardSquare(
            containerLeft = leftWidth,
            containerTop = 0.dp,
            containerWidth = cellWidth,
            containerHeight = halfHeight,
            xFraction = 0.5f,
            yFraction = 0.5f,
            squareSize = lerp(0.dp, 36.dp, easedProgress),
        )

        StoryboardSquare(
            containerLeft = leftWidth,
            containerTop = halfHeight,
            containerWidth = cellWidth,
            containerHeight = halfHeight,
            xFraction = 0.5f,
            yFraction = 0.5f,
            squareSize = baseSquareSize,
            rotationZ = 180f * easedProgress,
        )

        StoryboardSquare(
            containerLeft = leftWidth + cellWidth,
            containerTop = 0.dp,
            containerWidth = cellWidth,
            containerHeight = halfHeight,
            xFraction = 0.5f,
            yFraction = 0.5f,
            squareSize = baseSquareSize,
            color = colorLerp(
                start = PlaygroundReferenceSquareColor,
                stop = PlaygroundReferenceBlueColor,
                fraction = easedProgress,
            ),
        )

        StoryboardSquare(
            containerLeft = leftWidth + cellWidth,
            containerTop = halfHeight,
            containerWidth = cellWidth,
            containerHeight = halfHeight,
            xFraction = 0.5f,
            yFraction = 0.5f,
            squareSize = baseSquareSize,
            alpha = easedProgress,
        )
    }
}

@Composable
private fun StoryboardSquare(
    containerLeft: Dp,
    containerTop: Dp,
    containerWidth: Dp,
    containerHeight: Dp,
    xFraction: Float,
    yFraction: Float,
    squareSize: Dp,
    color: Color = PlaygroundReferenceSquareColor,
    alpha: Float = 1f,
    rotationZ: Float = 0f,
) {
    val maxX = (containerWidth - squareSize).coerceAtLeast(0.dp)
    val maxY = (containerHeight - squareSize).coerceAtLeast(0.dp)
    val xOffset = containerLeft + maxX * xFraction.coerceIn(0f, 1f)
    val yOffset = containerTop + maxY * yFraction.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(squareSize)
            .graphicsLayer {
                this.alpha = alpha.coerceIn(0f, 1f)
                this.rotationZ = rotationZ
            }
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}

@Composable
private fun EasingSlider(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = PlaygroundPrimaryTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = valueText,
                color = PlaygroundSecondaryTextColor,
                fontSize = 14.sp,
            )
        }

        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}

@Composable
private fun EasingSampleTable(
    easing: Easing,
    progress: Float,
) {
    val samples = listOf(0f, 0.1f, 0.2f, 0.35f, 0.5f, 0.65f, 0.8f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PlaygroundPanelColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Live progress ${progress.formatFraction()}",
            color = PlaygroundPrimaryTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )

        samples.forEach { sample ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "t=${sample.formatFraction()}",
                    color = PlaygroundSecondaryTextColor,
                    fontSize = 14.sp,
                )
                Text(
                    text = easing.transform(sample).formatFraction(),
                    color = PlaygroundPrimaryTextColor,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

private fun Float.formatFraction(): String = "%.2f".format(this)
