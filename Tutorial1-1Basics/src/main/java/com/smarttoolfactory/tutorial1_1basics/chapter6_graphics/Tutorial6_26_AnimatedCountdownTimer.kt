package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlinx.coroutines.launch


@Preview
@Composable
fun Tutorial6_26Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.padding(8.dp)) {
        TutorialHeader(text = "Animated Countdown Timer")
        AnimatedCountdownTimerSample()
    }
}


@Preview
@Composable
private fun AnimatedCountdownTimerSample() {


    var timer by remember {
        mutableStateOf(5)
    }

    val coroutineScope = rememberCoroutineScope()

    val animatedCountdownTimer = remember {
        AnimatedCountdownTimer()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.graphicsLayer {
                scaleX = animatedCountdownTimer.scale
                scaleY = animatedCountdownTimer.scale
                alpha = animatedCountdownTimer.alpha
            },
            text = "$timer",
            fontSize = 140.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(Modifier.height(40.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    animatedCountdownTimer.start(5, 0) {
                        timer = it
                    }
                }
            }
        ) {
            Text("Start Timer")
        }
    }
}

class AnimatedCountdownTimer {

    private val animatableScale = Animatable(1f)
    private val animatableAlpha = Animatable(1f)

    val scale: Float
        get() = animatableScale.value

    val alpha: Float
        get() = animatableAlpha.value

    suspend fun start(initialValue: Int, endValue: Int, onChange: (Int) -> Unit) {

        var value = initialValue

        while (value > endValue - 1) {
            onChange(value)
            animatableScale.snapTo(1f)
            animatableAlpha.snapTo(1f)
            animatableScale.animateTo(2f, animationSpec = tween(750))
            if (value > endValue) {
                animatableAlpha.animateTo(0f, animationSpec = tween(250))
            }
            value--
        }

    }
}