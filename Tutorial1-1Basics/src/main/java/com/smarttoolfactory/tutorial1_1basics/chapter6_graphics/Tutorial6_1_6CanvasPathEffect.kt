package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import kotlin.math.roundToInt

@Composable
fun Tutorial6_1Screen6() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "dashedPathEffect",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        DashedEffectExample()
        DashPathEffectAnimatedExample()

        Text(
            "cornerPathEffect",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        CornerPathEffectExample()

        Text(
            "chainPathEffect",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        ChainPathEffectExample()
        Text(
            "stompedPathEffect",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        StompedPathEffectExample()
    }
}

@Composable
private fun DashedEffectExample() {

    var onInterval by remember { mutableStateOf(20f) }
    var offInterval by remember { mutableStateOf(20f) }
    var phase by remember { mutableStateOf(10f) }

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(onInterval, offInterval),
        phase = phase
    )

    DrawPathEffect(pathEffect = pathEffect)

    Text(text = "onInterval ${onInterval.roundToInt()}")
    Slider(
        value = onInterval,
        onValueChange = { onInterval = it },
        valueRange = 0f..100f,
    )


    Text(text = "offInterval ${offInterval.roundToInt()}")
    Slider(
        value = offInterval,
        onValueChange = { offInterval = it },
        valueRange = 0f..100f,
    )

    Text(text = "phase ${phase.roundToInt()}")
    Slider(
        value = phase,
        onValueChange = { phase = it },
        valueRange = 0f..100f,
    )
}

@Composable
private fun DashPathEffectAnimatedExample() {

    val transition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 20f),
        phase = phase
    )

    DrawPathEffect(pathEffect = pathEffect)
}

@Composable
private fun CornerPathEffectExample() {

    var cornerRadius by remember { mutableStateOf(20f) }

    val pathEffect = PathEffect.cornerPathEffect(cornerRadius)
    DrawRect(pathEffect)

    Text(text = "cornerRadius ${cornerRadius.roundToInt()}")
    Slider(
        value = cornerRadius,
        onValueChange = { cornerRadius = it },
        valueRange = 0f..100f,
    )
}

@Composable
private fun ChainPathEffectExample() {

    var onInterval1 by remember { mutableStateOf(20f) }
    var offInterval1 by remember { mutableStateOf(20f) }
    var phase1 by remember { mutableStateOf(10f) }

    var cornerRadius by remember { mutableStateOf(20f) }

    val pathEffect1 = PathEffect.dashPathEffect(
        intervals = floatArrayOf(onInterval1, offInterval1),
        phase = phase1
    )

    val pathEffect2 = PathEffect.cornerPathEffect(cornerRadius)
    val pathEffect = PathEffect.chainPathEffect(outer = pathEffect1, inner = pathEffect2)

    DrawRect(pathEffect)

    Text(text = "onInterval1 ${onInterval1.roundToInt()}")
    Slider(
        value = onInterval1,
        onValueChange = { onInterval1 = it },
        valueRange = 0f..100f,
    )


    Text(text = "offInterval1 ${offInterval1.roundToInt()}")
    Slider(
        value = offInterval1,
        onValueChange = { offInterval1 = it },
        valueRange = 0f..100f,
    )

    Text(text = "phase1 ${phase1.roundToInt()}")
    Slider(
        value = phase1,
        onValueChange = { phase1 = it },
        valueRange = 0f..100f,
    )

    Text(text = "cornerRadius ${cornerRadius.roundToInt()}")
    Slider(
        value = cornerRadius,
        onValueChange = { cornerRadius = it },
        valueRange = 0f..100f,
    )
}

@Composable
private fun StompedPathEffectExample() {

    var stompedPathEffectStyle by remember {
        mutableStateOf(StampedPathEffectStyle.Translate)
    }

    var advance by remember { mutableStateOf(20f) }
    var phase by remember { mutableStateOf(20f) }

    val path = remember {
        Path().apply {
            moveTo(10f, 0f)
            lineTo(20f, 10f)
            lineTo(10f, 20f)
            lineTo(0f, 10f)
        }
    }

    val pathEffect = PathEffect.stampedPathEffect(
        shape = path,
        advance = advance,
        phase = phase,
        style = stompedPathEffectStyle
    )

    DrawPathEffect(pathEffect = pathEffect)

    Text(text = "advance ${advance.roundToInt()}")
    Slider(
        value = advance,
        onValueChange = { advance = it },
        valueRange = 0f..100f,
    )


    Text(text = "phase ${phase.roundToInt()}")
    Slider(
        value = phase,
        onValueChange = { phase = it },
        valueRange = 0f..100f,
    )

    ExposedSelectionMenu(title = "StompedEffect Style",
        index = when (stompedPathEffectStyle) {
            StampedPathEffectStyle.Translate -> 0
            StampedPathEffectStyle.Rotate -> 1
            else -> 2
        },
        options = listOf("Translate", "Rotate", "Morph"),
        onSelected = {
            println("STOKE CAP $it")
            stompedPathEffectStyle = when (it) {
                0 -> StampedPathEffectStyle.Translate
                1 -> StampedPathEffectStyle.Rotate
                else -> StampedPathEffectStyle.Morph
            }
        }
    )

}


@Composable
private fun DrawRect(pathEffect: PathEffect) {
    Canvas(modifier = canvasModifier) {
        val horizontalCenter = size.width / 2
        val verticalCenter = size.height / 2
        val radius = size.height / 3
        drawRect(
            Color.Black,
            topLeft = Offset(horizontalCenter - radius, verticalCenter - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = pathEffect

            )
        )
    }
}

@Composable
private fun DrawPathEffect(pathEffect: PathEffect) {
    Canvas(modifier = canvasModifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        val radius = (canvasHeight / 4).coerceAtMost(canvasWidth / 6)
        val space = (canvasWidth - 4 * radius) / 3

        drawRect(
            topLeft = Offset(space, (canvasHeight - 2 * radius) / 2),
            size = Size(radius * 2, radius * 2),
            color = Color.Black,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = pathEffect

            )
        )

        drawCircle(
            Color.Black,
            center = Offset(space * 2 + radius * 3, canvasHeight / 2),
            radius = radius,
            style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect)
        )

        drawLine(
            color = Color.Black,
            start = Offset(50f, canvasHeight - 50f),
            end = Offset(canvasWidth - 50f, canvasHeight - 50f),
            strokeWidth = 2.dp.toPx(),
            pathEffect = pathEffect
        )

    }
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxSize()
    .height(200.dp)