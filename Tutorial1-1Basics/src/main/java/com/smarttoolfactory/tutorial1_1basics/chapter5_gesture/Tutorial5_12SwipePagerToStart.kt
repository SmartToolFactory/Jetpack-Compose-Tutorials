package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
        TutorialHeader(text = "Scroll to start and Endless Pager")
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
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    shouldScrollToFirstPage = false

                    val firstTouchX = down.position.x

                    do {
                        val event: PointerEvent = awaitPointerEvent(
                            pass = PointerEventPass.Initial
                        )

                        event.changes.forEach {

                            val isValid = pagerState.currentPage == 4 &&
                                    pagerState.currentPage == pagerState.settledPage &&
                                    shouldScrollToFirstPage.not()
                            val movedLeft = it.position.x - firstTouchX < -40f

                            if (isValid && movedLeft) {
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
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    Toast.makeText(context, "Clicked $it", Toast.LENGTH_SHORT).show()
                }
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
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

                                val diff = it.position.x - firstTouchX
                                val posX = it.position.x

                                val valid = pagerState.currentPage == 4 &&
                                        pagerState.currentPage == pagerState.settledPage &&
                                        shouldScrollToFirstPage.not()

                                println(
                                    "Diff $diff, posX: $posX , " +
                                            "current page: ${pagerState.currentPage}, " +
                                            "currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}" +
                                            "valid: $valid"
                                )

                                if (valid && diff < -40f) {
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
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        Toast.makeText(context, "Clicked $it", Toast.LENGTH_SHORT).show()
                    }
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray),
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


@Preview
@Composable
fun PagerScrollCancelBackwardScroll() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState {
            25
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Current page: ${pagerState.currentPage}\n" +
                    "settled Page: ${pagerState.settledPage}\n" +
                    "target Page: ${pagerState.targetPage}\n" +
                    "currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}\n" +
                    "isScrollInProgress: ${pagerState.isScrollInProgress}\n" +
                    "canScrollForward: ${pagerState.canScrollForward}\n" +
                    "canScrollBackward: ${pagerState.canScrollBackward}\n" +
                    "lastScrolledForward: ${pagerState.lastScrolledForward}\n" +
                    "lastScrolledBackward: ${pagerState.lastScrolledBackward}\n",
            fontSize = 18.sp
        )

        val scrollEnabled by remember {
            derivedStateOf {
                pagerState.currentPageOffsetFraction >= 0
            }
        }

        HorizontalPager(
            userScrollEnabled = scrollEnabled,
            state = pagerState,
            pageSpacing = 16.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
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


// Cancel scroll in backwards
@Preview
@Composable
fun PagerScrollCancelBackwardNoScrollEffect() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState {
            20
        }

        HorizontalPager(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)

                        do {
                            val event: PointerEvent = awaitPointerEvent(
                                pass = PointerEventPass.Initial
                            )

                            event.changes.forEach {
                                val diffX = it.position.x - it.previousPosition.x

                                if (diffX > 0) {
                                    it.consume()
                                }
                            }

                        } while (event.changes.any { it.pressed })
                    }
                },
            state = pagerState,
            pageSpacing = 16.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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

// Cancel scroll in backwards
@Preview
@Composable
fun PagerScrollCancelBackwardScrollableContent() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState {
            20
        }

        var userScrollEnabled by remember {
            mutableStateOf(true)
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Current page: ${pagerState.currentPage}\n" +
                    "settled Page: ${pagerState.settledPage}\n" +
                    "target Page: ${pagerState.targetPage}\n" +
                    "userScrollEnabled: ${userScrollEnabled}\n" +
                    "currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}\n" +
                    "isScrollInProgress: ${pagerState.isScrollInProgress}\n" +
                    "canScrollForward: ${pagerState.canScrollForward}\n" +
                    "canScrollBackward: ${pagerState.canScrollBackward}\n" +
                    "lastScrolledForward: ${pagerState.lastScrolledForward}\n" +
                    "lastScrolledBackward: ${pagerState.lastScrolledBackward}\n",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            userScrollEnabled = false,
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(pass = PointerEventPass.Initial)

                        do {
                            val event: PointerEvent = awaitPointerEvent(
                                pass = PointerEventPass.Initial
                            )

                            event.changes.forEach {

                                val diffX = it.previousPosition.x - it.position.x
                                val downPos = down.position.x

                                println("Current: ${it.position.x}, Previous: ${it.previousPosition.x}, downPos: $downPos, diffX: $diffX")

                                pagerState.dispatchRawDelta(diffX.coerceAtLeast(0f))

                                val currentPageOffsetFraction = pagerState.currentPageOffsetFraction
                                userScrollEnabled = !(diffX > 0 || currentPageOffsetFraction < 0)
                            }

                        } while (event.changes.any { it.pressed })
                    }
                },
            state = pagerState,
            pageSpacing = 16.dp,
        ) { page ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(30)
                {
                    Text(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxWidth().padding(16.dp),
                        text = "Page $page, item: $it",
                        fontSize = 28.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PagerScrollCancelBackwardScrollNestedScrollConnection() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState {
            20
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Current page: ${pagerState.currentPage}\n" +
                    "settled Page: ${pagerState.settledPage}\n" +
                    "target Page: ${pagerState.targetPage}\n" +
                    "currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}\n" +
                    "isScrollInProgress: ${pagerState.isScrollInProgress}\n" +
                    "canScrollForward: ${pagerState.canScrollForward}\n" +
                    "canScrollBackward: ${pagerState.canScrollBackward}\n" +
                    "lastScrolledForward: ${pagerState.lastScrolledForward}\n" +
                    "lastScrolledBackward: ${pagerState.lastScrolledBackward}\n",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    println("onPreScroll available: $available")

                    val availableX = available.x

                    val consumed = if (availableX > 0) availableX else 0f
                    return Offset(consumed, 0f)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {

                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.settledPage)
                    }
                    return super.onPostScroll(consumed, available, source)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = 16.dp,
            ) { page ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(30)
                    {
                        Text(
                            modifier = Modifier
                                .background(Color.Black)
                                .fillMaxWidth().padding(16.dp),
                            text = "Page $page, item: $it",
                            fontSize = 28.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}