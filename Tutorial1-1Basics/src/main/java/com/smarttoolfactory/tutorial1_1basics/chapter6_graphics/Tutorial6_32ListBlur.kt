package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor

@Preview
@Composable
fun Tutorial6_32Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 8.dp)
            .drawBlur(scrollState, 100.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        repeat(14) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Title", fontSize = 28.sp)
                Text("Some text for demonstrating list blur", fontSize = 20.sp)
            }
        }
    }
}

fun Modifier.drawBlur(
    scrollState: ScrollState,
    blurDimension: Dp,
    startAlpha: Float = 1f,
    endAlpha: Float = 0f,
    blurPosition: BlurPosition = BlurPosition.Bottom
) = this.then(
    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
        .drawWithCache {

            val blurDimensionPx = blurDimension.toPx()
            val scrollPos = scrollState.value
            val max = scrollState.maxValue

            val ratio = (
                    if (max - scrollPos > blurDimensionPx) {
                        0f
                    } else {
                        scrollPos - max + blurDimensionPx
                    }
                    ) / blurDimensionPx


            val alphaStart = scale(0f, 1f, ratio, startAlpha, 1f)
            val alphaEnd = scale(0f, 1f, ratio, endAlpha, 1f)

            println("ratio: $ratio, alphaStart: $alphaStart, alphaEnd: $alphaEnd")
            val blurColor = Color.Transparent

            val brush = Brush.verticalGradient(
                startY = size.height - blurDimensionPx,
                endY = size.height,
                colors = listOf(
                    blurColor.copy(alpha = alphaStart),
                    blurColor.copy(alpha = alphaEnd)
                )
            )

            onDrawWithContent {
                // Destination
                drawContent()

                // Source
                drawRect(
                    brush = brush,
                    topLeft = Offset(0f, size.height - blurDimensionPx),
                    size = Size(size.width, blurDimensionPx),
                    blendMode = BlendMode.DstIn
                )
            }
        }
)

enum class BlurPosition {
    Start, End, Top, Bottom
}