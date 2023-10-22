package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.math.sqrt

@Preview
@Composable
fun Tutorial6_21Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column {
        TutorialHeader(text = "Constant Velocity Animation", modifier = Modifier.padding(8.dp))

        StyleableTutorialText(
            text = "Red circles animate with constant time, " +
                    "green circles animate with constant velocity.",
            bullets = false
        )
        AnimationVelocityTest()
    }
}

@Preview
@Composable
private fun AnimationVelocityTest() {

    var isClicked by remember {
        mutableStateOf(false)
    }

    val target1 = if (isClicked.not()) Offset.Zero
    else Offset(500f, 500f)

    val target2 = if (isClicked.not()) Offset.Zero
    else Offset(1000f, 1000f)


    val offset1 by animateOffsetAsState(
        targetValue = target1,
        animationSpec = tween(4000, easing = LinearEasing),
        label = ""
    )

    val offset2 by animateOffsetAsState(
        targetValue = target2,
        animationSpec = tween(4000, easing = LinearEasing),
        label = ""
    )

    val offset3 by animateConstantVelocityOffsetAsState(
        targetValue = target1,
        velocity = 250f,
    )

    val offset4 by animateConstantVelocityOffsetAsState(
        targetValue = target2,
        velocity = 250f,
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
            .padding(20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isClicked = isClicked.not()
            }
    ) {
        drawCircle(
            color = Color.Red,
            radius = 50f,
            center = offset1
        )

        translate(top = 100f) {
            drawCircle(
                color = Color.Red,
                radius = 50f,
                style = Stroke(4.dp.toPx()),
                center = offset2
            )
        }

        translate(top = 200f) {
            drawCircle(
                color = Color.Green,
                radius = 50f,
                center = offset3
            )
        }

        translate(top = 300f) {
            drawCircle(
                color = Color.Green,
                radius = 50f,
                style = Stroke(4.dp.toPx()),
                center = offset4
            )
        }
    }
}

@Preview
@Composable
private fun AnimationVelocityTest2() {
    var isClicked by remember {
        mutableStateOf(false)
    }

    var dynamicTarget by remember {
        mutableStateOf(Offset.Zero)
    }

    LaunchedEffect(isClicked) {
        if (isClicked) {
            dynamicTarget = dynamicTarget.plus(Offset(100f, 100f))
        }
    }

    val offset by animateConstantVelocityOffsetAsState(
        targetValue = dynamicTarget,
        velocity = 100f,
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
            .padding(20.dp)
            .clickable {
                isClicked = isClicked.not()
            }
    ) {

        drawRect(
            color = Color.Magenta,
            size = Size(100f, 100f),
            style = Stroke(4.dp.toPx()),
            topLeft = offset
        )
    }
}

@Composable
fun animateConstantVelocityOffsetAsState(
    initialOffset: Offset = Offset.Zero,
    targetValue: Offset,
    velocity: Float,
    label: String = "OffsetAnimation",
    finishedListener: ((Offset) -> Unit)? = null
): State<Offset> {

    require(velocity > 0f)

    var previousOffset by remember {
        mutableStateOf(initialOffset)
    }

    val durationMillis by remember(targetValue) {
        mutableIntStateOf(
            calculateDuration(targetValue.minus(previousOffset), velocity)
        )
    }

    previousOffset = targetValue

    val animationSpec = tween<Offset>(
        durationMillis = durationMillis,
        easing = LinearEasing
    )
    return animateValueAsState(
        targetValue,
        Offset.VectorConverter,
        animationSpec,
        label = label,
        finishedListener = {
            previousOffset = targetValue
            finishedListener?.invoke(it)
        }
    )
}

private fun calculateDuration(
    targetValue: Offset,
    velocity: Float
): Int {
    val xPos = targetValue.x
    val yPos = targetValue.y

    val distance = sqrt(xPos * xPos + yPos + yPos)
    return (distance / velocity * 1000).toInt()
}
