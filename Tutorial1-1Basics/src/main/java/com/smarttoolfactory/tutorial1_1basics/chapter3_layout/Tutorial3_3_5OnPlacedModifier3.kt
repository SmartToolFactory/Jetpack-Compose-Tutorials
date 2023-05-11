package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun Tutorial3_3Screen5() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    var alignment by remember {
        mutableStateOf(Alignment.Center)
    }

    var alignmentValue by remember {
        mutableStateOf(0f)
    }

    alignment = when (alignmentValue.roundToInt()) {
        0 -> Alignment.TopStart
        1 -> Alignment.TopCenter
        2 -> Alignment.TopEnd
        3 -> Alignment.CenterStart
        4 -> Alignment.Center
        5 -> Alignment.CenterEnd
        6 -> Alignment.BottomStart
        7 -> Alignment.BottomCenter
        else -> Alignment.BottomEnd
    }

    val text = when (alignmentValue.roundToInt()) {
        0 -> "Alignment.TopStart"
        1 -> "Alignment.TopCenter"
        2 -> "Alignment.TopEnd"
        3 -> "Alignment.CenterStart"
        4 -> "Alignment.Center"
        5 -> "Alignment.CenterEnd"
        6 -> "Alignment.BottomStart"
        7 -> "Alignment.BottomCenter"
        else -> "Alignment.BottomEnd"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(10.dp)
    ) {
        Text(text = "Alignment: $text", fontSize = 20.sp)
        Slider(
            value = alignmentValue,
            onValueChange = {
                alignmentValue = it
            },
            valueRange = 0f..8f,
            steps = 7
        )
        AnimatedChildAlignment(alignment = alignment)
    }
}

@Composable
fun AnimatedChildAlignment(alignment: Alignment) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(4.dp)
            .border(2.dp, Green400)
    ) {
        Box(
            modifier = Modifier
                .animatePlacement()
                .align(alignment)
                .size(100.dp)
                .background(Red400)
        )
    }
}

fun Modifier.animatePlacement(): Modifier = composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        // 🔥 onPlaced should be before offset Modifier
        .onPlaced {
            // Calculate the position in the parent layout
            targetOffset = it
                .positionInParent()
                .round()

            println("🚙 Modifier.onPlaced{} targetOffset: $targetOffset")
        }
        .offset {
            // Animate to the new target offset when alignment changes.
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also {
                    animatable = it
                }

            println(
                "🚗 Modifier.offset{} targetOffset: $targetOffset, " +
                        "anim.targetValue: ${anim.targetValue}"
            )

            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, spring(stiffness = Spring.StiffnessMediumLow))
                }
            }
            // Offset the child in the opposite direction to the targetOffset, and slowly catch
            // up to zero offset via an animation to achieve an overall animated movement.
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}