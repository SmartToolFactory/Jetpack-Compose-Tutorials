package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Inspired from https://fvilarino.medium.com/recreating-google-podcasts-speed-selector-in-jetpack-compose-7623203a009d
 * Motivation -  https://docs.flutter.dev/cookbook/effects/photo-filter-carousel
 */
private val colors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Magenta,
    Color.Yellow,
    Color.Cyan,
)

@Preview
@Composable
fun Tutorial6_19Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column {
        InstagramCarousel(
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            currentValueLabel = { value ->
                Text(modifier = Modifier.padding(16.dp),
                    color = Color.Red,
                    text = "$value",
                    style = MaterialTheme.typography.h6
                )
            }
        )
    }
}

@Stable
interface CarouselState {
    val currentValue: Float
    val range: ClosedRange<Int>

    suspend fun snapTo(value: Float)
    suspend fun scrollTo(value: Int)
    suspend fun decayTo(velocity: Float, value: Float)
    suspend fun stop()
}

class CarouselStateImpl(
    currentValue: Float,
    override val range: ClosedRange<Int>,
) : CarouselState {
    private val floatRange = range.start.toFloat()..range.endInclusive.toFloat()
    private val animatable = Animatable(currentValue)
    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )
    override val currentValue: Float
        get() = animatable.value

    override suspend fun stop() {
        animatable.stop()
    }

    // snap to value
    override suspend fun snapTo(value: Float) {
        animatable.snapTo(value.coerceIn(floatRange))
    }

    // scroll to index
    override suspend fun scrollTo(value: Int) {
        animatable.snapTo(value.toFloat().coerceIn(floatRange))
    }

    // decay animation when velocity decreases as items scrolls
    override suspend fun decayTo(velocity: Float, value: Float) {
        val target = value.roundToInt().coerceIn(range).toFloat()
        animatable.animateTo(
            targetValue = target,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarouselStateImpl

        if (range != other.range) return false
        if (floatRange != other.floatRange) return false
        if (animatable != other.animatable) return false
        if (decayAnimationSpec != other.decayAnimationSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = range.hashCode()
        result = 31 * result + floatRange.hashCode()
        result = 31 * result + animatable.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }

    companion object {
        val Saver = Saver<CarouselStateImpl, List<Any>>(
            save = { listOf(it.currentValue, it.range.start, it.range.endInclusive) },
            restore = {
                CarouselStateImpl(
                    currentValue = it[0] as Float,
                    range = (it[1] as Int)..(it[2] as Int)
                )
            }
        )
    }
}

// save state during configuration change
@Composable
fun rememberCarouselState(
    currentValue: Float = 0f,
    range: ClosedRange<Int> = 0..40,
): CarouselState {
    val state = rememberSaveable(saver = CarouselStateImpl.Saver) {
        CarouselStateImpl(currentValue, range)
    }
    LaunchedEffect(key1 = Unit) {
        state.snapTo(state.currentValue.roundToInt().toFloat())
    }
    return state
}

@Composable
fun InstagramCarousel(
    modifier: Modifier = Modifier,
    state: CarouselState = rememberCarouselState(),
    numSegments: Int = 5,

    currentValueLabel: @Composable (Int) -> Unit = { value -> Text(value.toString()) }
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        currentValueLabel(state.currentValue.roundToInt())
        val scope = rememberCoroutineScope()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .drag(state, numSegments),
            contentAlignment = Alignment.Center,
        ) {
            CenterCircle(
                modifier = Modifier.align(Alignment.Center),
                fillColor = Color(android.graphics.Color.parseColor("#4DB6AC")),
                strokeWidth = 5.dp,
            )
            // divide maxWidth by num of segments. here its 5
            val segmentWidth = maxWidth / numSegments
            // calculate each segment width
            val segmentWidthPx = constraints.maxWidth.toFloat() / numSegments.toFloat()
            // half os segment width cause we need to place the first item at the center
            val halfSegments = (numSegments + 1) / 2

            val start = (state.currentValue - halfSegments).toInt()
                .coerceAtLeast(state.range.start)
            val end = (state.currentValue + halfSegments).toInt()
                .coerceAtMost(state.range.endInclusive)
            val maxOffset = constraints.maxWidth / 2f
            for (i in start..end) {
                // offset of the item. i is the index/position. initially state.currentValue is 0
                // initially offset is 0 for the first item
                val offsetX = (i - state.currentValue) * segmentWidthPx
                // alpha
                val deltaFromCenter = (offsetX)
                // if its 1 then items has no alpha.
                // based on the distance the item has moved from center the alpha changes accordingly
                val percentFromCenter = 1.0f - abs(deltaFromCenter) / maxOffset
                val alpha = 0.25f + (percentFromCenter * 0.75f)
                // scale
                val deltaFromCenterScale = (offsetX)
                val percentFromCenterScale = 1.0f - abs(deltaFromCenterScale) / maxOffset
                val scale = 0.5f + (percentFromCenterScale * 0.5f)

                Column(
                    modifier = Modifier
                        .width(segmentWidth)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .graphicsLayer(
                            // place item at respective offset's on x axis
                            translationX = offsetX,
                        ),
                    // center the component
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .height(55.dp)
                            .graphicsLayer(
                                // setting alpha ans scale for each item
                                alpha = alpha,
                                scaleY = scale,
                                scaleX = scale
                            )
                            .clip(CircleShape) // circle shape for each item
                            .background(colors[i % colors.size])
                            .clickable {
                                // scroll to position on click
                                scope.launch {
                                    state.scrollTo(i)
                                }
                                Toast
                                    .makeText(context, "$i", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    )
                }
            }
        }
    }
}

// Custom gesture to track horizontal drag offset and also the velocity of the fling
// these values are used to the change the state of the items
@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
private fun Modifier.drag(
    state: CarouselState,
    numSegments: Int,
) = pointerInput(Unit) {
    val decay = splineBasedDecay<Float>(this)
    val segmentWidthPx = size.width / numSegments
    coroutineScope {
        while (true) {
            val pointerId =
                awaitPointerEventScope {
                    awaitFirstDown(true).id
                }
            state.stop()
            val tracker = VelocityTracker()
            awaitPointerEventScope {
                horizontalDrag(pointerId) { change ->
                    val horizontalDragOffset =
                        state.currentValue - change.positionChange().x / segmentWidthPx
                    launch {
                        state.snapTo(horizontalDragOffset)
                    }
                    tracker.addPosition(change.uptimeMillis, change.position)
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
            }
            val velocity = tracker.calculateVelocity().x / numSegments
            val targetValue = decay.calculateTargetValue(state.currentValue, -velocity)
            launch {
                state.decayTo(velocity, targetValue)
            }
        }
    }
}


// Custom circle with stroke
@Composable
fun CenterCircle(
    modifier: Modifier = Modifier,
    fillColor: Color,
    strokeWidth: Dp
) {
    Canvas(
        modifier = modifier
            .size(75.dp)
    ) {

        drawArc(
            color = fillColor,
            0f,
            360f,
            true,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }

}