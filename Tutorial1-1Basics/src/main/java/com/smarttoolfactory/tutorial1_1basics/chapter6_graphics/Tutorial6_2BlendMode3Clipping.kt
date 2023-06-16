package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial6_2Screen3() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {

        Text(
            "Blend (PorterDuff) Modes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        StyleableTutorialText(
            text = "Use BlendMode to create partially filled **VectorDrawable**",
            bullets = false
        )
        ClipIconSample()
        StyleableTutorialText(
            text = "Gradient colored VectorDrawable",
            bullets = false
        )
        GradientClipIconSample()
        StyleableTutorialText(
            text = "Fill/empty animation when **VectorDrawable** is touched.",
            bullets = false
        )
        FillIconSample()
        StyleableTutorialText(
            text = "Shimmer effect.",
            bullets = false
        )
        ShimmerIconSample()
    }
}


@Composable
private fun ClipIconSample() {
    val vectorRes2: Painter = painterResource(id = R.drawable.vd_dashboard_active)
    Icon(
        vectorRes2,
        modifier = Modifier
            .drawWithContent {
                val height = size.height

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    // Destination
                    drawContent()

                    // Source
                    drawRect(
                        Color(0xffEC407A),
                        topLeft = Offset(0f, height / 2),
                        size = Size(size.width, size.height / 2),
                        blendMode = BlendMode.SrcIn

                    )

                    restoreToCount(checkPoint)

                }
            }
            .size(100.dp),
        contentDescription = null
    )
}

@Composable
private fun GradientClipIconSample() {
    val vectorRes2: Painter = painterResource(id = R.drawable.vd_dashboard_active)
    Icon(
        vectorRes2,
        modifier = Modifier
            .drawWithContent {
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    // Destination
                    drawContent()

                    // Source
                    drawRect(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Green,
                                Color.Cyan,
                                Color.Red,
                                Color.Blue,
                                Color.Yellow,
                                Color.Magenta,
                            )
                        ),
                        blendMode = BlendMode.SrcIn
                    )

                    restoreToCount(checkPoint)

                }
            }
            .size(100.dp),
        contentDescription = null
    )
}

@Composable
private fun FillIconSample() {
    val vectorRes2: Painter = painterResource(id = R.drawable.vd_dashboard_active)
    var targetValue by remember {
        mutableStateOf(0f)
    }
    val progress by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(1000)
    )
    Icon(
        vectorRes2,
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
            ) {
                targetValue = if (targetValue == 0f) {
                    1f
                } else {
                    0f
                }
            }
            .drawWithContent {
                val height = size.height * progress

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    val totalHeight = size.height
                    val filledHeight = totalHeight * progress

                    // Destination
                    drawContent()

                    // Source
                    drawRect(
                        Color(0xffEC407A),
                        topLeft = Offset(0f, totalHeight - height),
                        size = Size(size.width, height),
                        blendMode = BlendMode.SrcIn

                    )

                    restoreToCount(checkPoint)

                }
            }
            .size(100.dp),
        contentDescription = null
    )
}

@Composable
private fun ShimmerIconSample() {

    val vectorRes2: Painter = painterResource(id = R.drawable.vd_clock_alarm)

    val transition = rememberInfiniteTransition()

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Icon(
        vectorRes2,
        modifier = Modifier
            .drawWithContent {
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)
                    val canvasWidth = size.width

                    val brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xff616161),
                            Color.White,
                            Color(0xff9E9E9E),
                            Color(0xff616161),
                        ),
                        startX = canvasWidth * progress,
                        endX = canvasWidth * progress * 2f
                    )

                    // Destination
                    drawContent()

                    // Source
                    drawRect(
                        brush = brush,
                        blendMode = BlendMode.SrcIn
                    )

                    restoreToCount(checkPoint)

                }
            }
            .size(100.dp),
        contentDescription = null
    )
}

