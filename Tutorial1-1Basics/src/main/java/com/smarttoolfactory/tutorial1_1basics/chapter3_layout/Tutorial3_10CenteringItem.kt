package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.launch


@Preview
@Composable
fun Tutorial3_10Screen() {
    Column {
        TutorialContent()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun TutorialContent() {
    BoxWithConstraints {
        val screenHeight = maxHeight
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.drawer_bg2),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }


            Column(modifier = Modifier.height(screenHeight)) {
                val tabList = listOf("Tab1", "Tab2")
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f
                ) {
                    tabList.size
                }
                val coroutineScope = rememberCoroutineScope()

                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    selectedTabIndex = pagerState.currentPage
                ) {
                    tabList.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(
                            remember {
                                object : NestedScrollConnection {
                                    override fun onPreScroll(
                                        available: Offset,
                                        source: NestedScrollSource,
                                    ): Offset {
                                        return if (available.y > 0) {
                                            Offset.Zero
                                        } else {
                                            Offset(
                                                x = 0f,
                                                y = -scrollState.dispatchRawDelta(-available.y),
                                            )
                                        }
                                    }
                                }
                            },
                        ),
                    state = pagerState,
                    pageSpacing = 0.dp,
                    userScrollEnabled = true,
                    reverseLayout = false,
                    contentPadding = PaddingValues(0.dp),
                    beyondBoundsPageCount = 0,
                    pageSize = PageSize.Fill,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                    key = null,
                    pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                        pagerState,
                        Orientation.Horizontal
                    ),
                    pageContent = { page ->
                        when (page) {
                            0 -> ListLazyColumn(0)
                            1 -> ListFlowRow(5)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ListLazyColumn(itemsCount: Int) {

    var offsetY by remember {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (itemsCount > 0) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itemsCount) { index ->
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Button $index")
                    }
                }
            }
        } else {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .onPlaced { layoutCoordinates: LayoutCoordinates ->
                        val contentHeight = layoutCoordinates.size.height

                        // This is Box inside ListLazyColumn
                        val parent = layoutCoordinates.parentLayoutCoordinates
                        // This is outer Column
                        val root = parent?.parentLayoutCoordinates?.parentLayoutCoordinates
                        if (parent != null && root != null) {

                            val rootHeight = root.size.height
                            val parentHeight = parent.size.height
                            val parentPosition = parent.positionInRoot().y

                            val tabHeight = rootHeight - parentHeight

                            offsetY =
                                ((parentHeight - rootHeight - tabHeight - parentPosition + contentHeight) / 2).toInt()

                            println(
                                "parent height: ${parentHeight}, " +
                                        "parent pos: $parentPosition " +
                                        "root height: ${rootHeight}, " +
                                        "contentHeight: $contentHeight, " +
                                        "tabHeight: $tabHeight, " +
                                        "offsetY: $offsetY"
                            )
                        }

                    }
                    .offset {
                        IntOffset(0, offsetY)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "empty state",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.Blue)
                )
                Text(
                    text = "Centering Empty State",
                    style = TextStyle(
                        fontSize = 18.sp,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
fun ListFlowRow(items: Int) {
    FlowRow(modifier = Modifier.fillMaxSize()) {
        repeat(items) { index ->
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Button $index")
            }
        }
    }
}