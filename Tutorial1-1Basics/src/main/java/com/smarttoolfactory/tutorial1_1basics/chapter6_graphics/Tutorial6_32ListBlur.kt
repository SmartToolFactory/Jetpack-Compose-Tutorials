@file:OptIn(ExperimentalFoundationApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial6_32Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val pagerState = rememberPagerState {
        2
    }

    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.height(64.dp)
        ) {
            repeat(2) {
                Tab(
                    selected = pagerState.currentPage == it,
                    content = {
                        val text = if (it == 0) "Bottom Blur" else
                            "Top blur"
                        Text(text)
                    },
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState
        ) { index ->
            val scrollState = rememberScrollState()

            val blurPosition = if (index == 0) {
                BlurPosition.Bottom
            } else {
                BlurPosition.Top
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
                    .drawBlur(
                        scrollState = scrollState,
                        blurDimension = 60.dp,
                        blurPosition = blurPosition
                    )
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
                        Text("Title", fontSize = 26.sp, color = Green400)
                        Text(
                            "Some text for demonstrating list blur",
                            fontSize = 20.sp,
                            color = Red400
                        )
                    }
                }
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

            val ratio = if (blurPosition == BlurPosition.Bottom) {
                (
                        if (max - scrollPos > blurDimensionPx) {
                            0f
                        } else {
                            scrollPos - max + blurDimensionPx
                        }
                        ) / blurDimensionPx
            } else {
                (
                        if (scrollPos > blurDimensionPx) {
                            0f
                        } else {
                            (blurDimensionPx - scrollPos).coerceIn(0f, blurDimensionPx)
                        }
                        ) / blurDimensionPx
            }

            val alphaStart = scale(0f, 1f, ratio, startAlpha, 1f)
            val alphaEnd = scale(0f, 1f, ratio, endAlpha, 1f)

            println("scrollPos: $scrollPos, ratio: $ratio, alphaStart: $alphaStart, alphaEnd: $alphaEnd")

            val blurColor = Color.Transparent

            val startY = if (blurPosition == BlurPosition.Bottom) {
                size.height - blurDimensionPx
            } else {
                0f
            }
            val endY = if (blurPosition == BlurPosition.Bottom) {
                size.height
            } else {
                blurDimensionPx
            }

            val start = if (blurPosition == BlurPosition.Bottom) alphaStart else alphaEnd
            val end = if (blurPosition == BlurPosition.Bottom) alphaEnd else alphaStart

            val brush = Brush.verticalGradient(
                startY = startY,
                endY = endY,
                colors = listOf(
                    blurColor.copy(alpha = start),
                    blurColor.copy(alpha = end)
                )
            )

            onDrawWithContent {
                // Destination
                drawContent()

                // Source
                val topLeftY = if (blurPosition == BlurPosition.Bottom) {
                    size.height - blurDimensionPx
                } else {
                    0f
                }

                drawRect(
                    brush = brush,
                    topLeft = Offset(0f, topLeftY),
                    size = Size(size.width, blurDimensionPx),
                    blendMode = BlendMode.DstIn
                )
            }
        }
)

enum class BlurPosition {
    Top, Bottom
}