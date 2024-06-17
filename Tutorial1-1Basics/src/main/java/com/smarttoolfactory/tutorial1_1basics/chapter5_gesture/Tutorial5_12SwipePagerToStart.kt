package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial5_12Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Pager Scroll to start and Endless Pager")
        StyleableTutorialText("Scroll from last item to start", bullets = false)
        PagerScrollSample()
        Spacer(Modifier.height(16.dp))
        PagerScrollSample2()
        StyleableTutorialText("Endless Pager", bullets = false)
        InfinitePagerSample()
    }
}

@Preview
@Composable
private fun PagerScrollSample() {
    val pagerState = rememberPagerState {
        5
    }

    var shouldScrollToFirstPage by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(shouldScrollToFirstPage) {
        if (shouldScrollToFirstPage) {
            delay(100)
            pagerState.animateScrollToPage(0, animationSpec = tween(1000))
            shouldScrollToFirstPage = false
        }
    }

    HorizontalPager(
        userScrollEnabled = shouldScrollToFirstPage.not(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    shouldScrollToFirstPage = false

                    do {
                        val event: PointerEvent = awaitPointerEvent(
                            pass = PointerEventPass.Initial
                        )

                        event.changes.forEach {

                            if (pagerState.currentPage == 4 &&
                                pagerState.currentPageOffsetFraction == 0f &&
                                // current position of pointer
                                it.position.x < 200f &&
                                shouldScrollToFirstPage.not()
                            ) {
                                shouldScrollToFirstPage = true
                            }
                        }

                    } while (event.changes.any { it.pressed })

//                         User lifts pointer, you can animate here as well
//                        if (pagerState.currentPage == 4 &&
//                            pagerState.currentPageOffsetFraction == 0f
//                        ) {
//                            shouldScrollToFirstPage = true
//                        }
                }
            },
        state = pagerState,
        pageSpacing = 16.dp,
    ) {

        val context = LocalContext.current

        Box(
            modifier = Modifier
                .clickable {
                    Toast.makeText(context, "Clicked $it", Toast.LENGTH_SHORT).show()
                }
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Page $it",
                fontSize = 28.sp
            )
        }
    }
}

@Preview
@Composable
private fun PagerScrollSample2() {

    val pagerState = rememberPagerState {
        5
    }

    var shouldScrollToFirstPage by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints {

        val pageSpacing = 16.dp
        val pageWidth = maxWidth - pageSpacing - 32.dp

        HorizontalPager(
            userScrollEnabled = shouldScrollToFirstPage.not(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSize = PageSize.Fixed(pageWidth),
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(pass = PointerEventPass.Initial)
                        shouldScrollToFirstPage = false

                        val firstTouchX = down.position.x

                        do {
                            val event: PointerEvent = awaitPointerEvent(
                                pass = PointerEventPass.Initial
                            )

                            event.changes.forEach {

                                val diff = firstTouchX - it.position.x
                                val posX = it.position.x

                                val valid = pagerState.currentPage == 4 &&
                                        pagerState.currentPage == pagerState.settledPage &&
                                        // Scroll if user scrolled 10% from first touch position
                                        // or pointer is at the left of 20% of page
                                        (diff > size.width * .10f ||
                                                it.position.x < size.width * .2f) &&
                                        shouldScrollToFirstPage.not()

                                println(
                                    "Diff $diff, posX: $posX , " +
                                            "current page: ${pagerState.currentPage}, " +
                                            "valid: $valid"
                                )

                                if (valid) {
                                    coroutineScope.launch {
                                        println("🔥 Scrolling...")
                                        shouldScrollToFirstPage = true
                                        delay(50)
                                        pagerState.animateScrollToPage(
                                            0,
                                            animationSpec = tween(500)
                                        )
                                        shouldScrollToFirstPage = false
                                    }
                                }
                            }

                        } while (event.changes.any { it.pressed })
                    }
                },
            state = pagerState,
            pageSpacing = pageSpacing,
        ) {

            val context = LocalContext.current

            Box(
                modifier = Modifier
                    .clickable {
                        Toast.makeText(context, "Clicked $it", Toast.LENGTH_SHORT).show()
                    }
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page $it",
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun InfinitePagerSample() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {

        val items = remember {
            listOf("A", "B", "C")
        }
        val pageCount = items.size * 400

        val pagerState = rememberPagerState(
            initialPage = pageCount / 2,
            pageCount = {
                pageCount
            }
        )

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            beyondViewportPageCount = 1,
            pageSpacing = 16.dp
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page ${items[it % 3]}",
                    fontSize = 28.sp
                )
            }
        }
    }
}
