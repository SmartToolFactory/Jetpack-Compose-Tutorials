package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        val text = if (it == 0) "Bottom Fade" else
                            "Top Fade"
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

            val fadeEdge = if (index == 0) {
                FadeEdge.Bottom
            } else {
                FadeEdge.Top
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
                    .drawFadeEffect(
                        scrollState = scrollState,
                        fadeSize = 80.dp,
                        startAlpha = .7f,
                        endAlpha = 0f,
                        fadeEdge = fadeEdge
                    )
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = sampleText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

fun Modifier.drawFadeEffect(
    scrollState: ScrollState,
    fadeSize: Dp,
    startAlpha: Float = 1f,
    endAlpha: Float = 0f,
    fadeEdge: FadeEdge = FadeEdge.Bottom
) = this.then(
    Modifier.graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
        .drawWithCache {

            val fadeDimensionPx = fadeSize.toPx()
            val scrollPos = scrollState.value
            val max = scrollState.maxValue

            val ratio = if (fadeEdge == FadeEdge.Bottom) {
                (
                        if (max - scrollPos > fadeDimensionPx) {
                            0f
                        } else {
                            scrollPos - max + fadeDimensionPx
                        }
                        ) / fadeDimensionPx
            } else {
                (
                        if (scrollPos > fadeDimensionPx) {
                            0f
                        } else {
                            (fadeDimensionPx - scrollPos).coerceIn(0f, fadeDimensionPx)
                        }
                        ) / fadeDimensionPx
            }

            val alphaStart = scale(0f, 1f, ratio, startAlpha, 1f)
            val alphaEnd = scale(0f, 1f, ratio, endAlpha, 1f)

            println("scrollPos: $scrollPos, ratio: $ratio, alphaStart: $alphaStart, alphaEnd: $alphaEnd")

            val fadeColor = Color.Transparent

            val startY = if (fadeEdge == FadeEdge.Bottom) {
                size.height - fadeDimensionPx
            } else {
                0f
            }
            val endY = if (fadeEdge == FadeEdge.Bottom) {
                size.height
            } else {
                fadeDimensionPx
            }

            val start = if (fadeEdge == FadeEdge.Bottom) alphaStart else alphaEnd
            val end = if (fadeEdge == FadeEdge.Bottom) alphaEnd else alphaStart

            val brush = Brush.verticalGradient(
                startY = startY,
                endY = endY,
                colors = listOf(
                    fadeColor.copy(alpha = start),
                    fadeColor.copy(alpha = end)
                )

                // Also can use stops for better pixel erase look
//                colorStops = arrayOf(
//                    Pair(.0f, fadeColor.copy(alpha = start)),
//                    Pair(.1f, fadeColor.copy(alpha = (start + end) / 2)),
//                    Pair(1f, fadeColor.copy(alpha = end)),
//                )
            )

            onDrawWithContent {
                // Destination
                drawContent()

                // Source
                val topLeftY = if (fadeEdge == FadeEdge.Bottom) {
                    size.height - fadeDimensionPx
                } else {
                    0f
                }

                drawRect(
                    brush = brush,
                    topLeft = Offset(0f, topLeftY),
                    size = Size(size.width, fadeDimensionPx),
                    blendMode = BlendMode.DstIn
                )

                // This is of seeing bounds of blend mode applied area
//                drawRect(
//                    color = Color.Red,
//                    topLeft = Offset(0f, topLeftY),
//                    size = Size(size.width, fadeDimensionPx),
//                    style = Stroke(2.dp.toPx())
//                )

            }
        }
)

// TODO
fun Modifier.drawFadeEffect(
    scrollState: LazyListState,
    fadeDimension: Dp,
    startAlpha: Float = 1f,
    endAlpha: Float = 0f,
    fadeEdge: FadeEdge = FadeEdge.Bottom
) = composed {

    val itemPos: Int by remember {
        derivedStateOf {
            val totalItems = scrollState.layoutInfo.totalItemsCount
            val visibleItemsInfo: List<LazyListItemInfo> = scrollState.layoutInfo.visibleItemsInfo

            if (fadeEdge == FadeEdge.Bottom) {
                val lastItemVisible: LazyListItemInfo? = visibleItemsInfo.firstOrNull {
                    it.index == totalItems - 1
                }

                lastItemVisible?.offset?.let {
                    (scrollState.layoutInfo.viewportEndOffset - it).coerceIn(
                        0,
                        lastItemVisible.size
                    )
                } ?: run {
                    0
                }
            } else {
                val firstItemVisible: LazyListItemInfo? = visibleItemsInfo.firstOrNull {
                    it.index == 0
                }
                firstItemVisible?.offset?.coerceIn(
                    0,
                    firstItemVisible.size
                ) ?: run {
                    0
                }
            }
        }
    }

    println("itemPos: $itemPos")

    Modifier
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .drawWithCache {
            val fadeDimensionPx = fadeDimension.toPx()

            val ratio = (
                    if (fadeEdge == FadeEdge.Bottom) {
                        itemPos / fadeDimensionPx
                    } else {
                        itemPos / fadeDimensionPx
                    }
                    ).coerceIn(0f, 1f)

            val alphaStart = scale(0f, 1f, ratio, startAlpha, 1f)
            val alphaEnd = scale(0f, 1f, ratio, endAlpha, 1f)


            val fadeColor = Color.Transparent

            val startY = if (fadeEdge == FadeEdge.Bottom) {
                size.height - fadeDimensionPx
            } else {
                0f
            }
            val endY = if (fadeEdge == FadeEdge.Bottom) {
                size.height
            } else {
                fadeDimensionPx
            }

            val start = if (fadeEdge == FadeEdge.Bottom) alphaStart else alphaEnd
            val end = if (fadeEdge == FadeEdge.Bottom) alphaEnd else alphaStart

            val brush = Brush.verticalGradient(
                startY = startY,
                endY = endY,
                colors = listOf(
                    fadeColor.copy(alpha = start),
                    fadeColor.copy(alpha = end)
                )
            )

            onDrawWithContent {
                // Destination
                drawContent()

                // Source
                val topLeftY = if (fadeEdge == FadeEdge.Bottom) {
                    size.height - fadeDimensionPx
                } else {
                    0f
                }

                drawRect(
                    brush = brush,
                    topLeft = Offset(0f, topLeftY),
                    size = Size(size.width, fadeDimensionPx),
                    blendMode = BlendMode.DstIn
                )
            }
        }
}

enum class FadeEdge {
    Top, Bottom
}

private val sampleText = """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque. Curabitur sed fermentum lectus, ac suscipit est. Praesent nisi lorem, viverra a feugiat eu, pretium ac odio. Curabitur dapibus justo at nisi sagittis mattis. Sed molestie orci nec quam consequat, consequat sagittis elit lacinia.

    1. Lorem Ipsum
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque.

    2. Lorem Ipsum
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque.

    3. Lorem Ipsum
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque.

    4. Lorem Ipsum
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque.

    5. Lorem Ipsum
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ac erat vitae tortor ultrices vestibulum id quis mi. Aliquam erat volutpat. Praesent ullamcorper eros at sapien commodo lobortis. Mauris consequat elit quis eros mollis vulputate. Ut egestas porta tincidunt. Pellentesque suscipit pulvinar mauris a fringilla. Fusce volutpat sem non est facilisis, non aliquet ligula finibus. Sed id quam sit amet massa iaculis fermentum. Nullam luctus justo ultricies condimentum scelerisque.
""".trimIndent()
