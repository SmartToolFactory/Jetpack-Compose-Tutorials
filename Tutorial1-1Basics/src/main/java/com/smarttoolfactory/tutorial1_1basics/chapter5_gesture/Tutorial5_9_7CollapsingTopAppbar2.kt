package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial5_9Screen7() {
    val systemUiController = rememberSystemUiController()

    // Check out Tutorial4_5_1 for DisposableEffect
    DisposableEffect(key1 = true, effect = {

        systemUiController.setStatusBarColor(
            color = Blue400
        )
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.Transparent
            )
        }
    })
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    ComposeTutorialsTheme {
        /*
            This sample removes vertical scroll from Column,
            LazyVerticalGrid scroll updates
         */
        TwitterCollapsingToolbar3()
        /*
            This TopAppbar scrolls Column with vertical scroll
            when LazyVerticalGrid is scrolled to have parallax effect
         */
//        TwitterCollapsingToolbar2()
        /*
          This TopAppbar disables LazVerticalGrid user scroll
          and calls lazyGridState.scrollBy(delta) inside NestedScrollConnection
         */
//        TwitterCollapsingToolbar1()
    }
}

@Composable
private fun TwitterCollapsingToolbar3() {

    val scrollState = rememberScrollState()
    val titleHeight = remember { mutableStateOf(0f) }
    val collapseRangePx = with(LocalDensity.current) { collapseRange.toPx() }
    val avatarSizePx = with(LocalDensity.current) { avatarSize.toPx() }
    val profileNameTopPaddingPx = with(LocalDensity.current) { paddingSmall.toPx() }
    val paddingMediumPx = with(LocalDensity.current) { paddingMedium.toPx() }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp

    val lazyGridState = rememberLazyGridState()


    val collapseRangeReached = remember {
        derivedStateOf {
            scrollState.value >= (collapseRangePx)
        }
    }

    val avatarZIndex = remember {
        derivedStateOf {
            if (collapseRangeReached.value)
                0f
            else
                2f
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = -available.y
                coroutineScope.launch {
                    if (scrollState.isScrollInProgress.not()) {
                        scrollState.scrollBy(delta)
                    }
                }
                return Offset.Zero
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize()

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .zIndex(0f)
                    .background(Color.Black)
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.height(screenHeightDp),
                ) {

                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                // This layout is to match size to parent because
                                // inside LazyVerticalGrid we are constraint with horizontal
                                // padding
                                .layout { measurable: Measurable, constraints: Constraints ->
                                    val placeable = measurable.measure(
                                        constraints.copy(
                                            minWidth = screenWidthDp.roundToPx(),
                                            maxWidth = screenWidthDp.roundToPx()
                                        )
                                    )
                                    layout(placeable.width, placeable.height) {
                                        placeable.placeRelative(0, 0)
                                    }
                                }
                        ) {
                            Spacer(Modifier.height(headerHeight - 16.dp))
                            Text(
                                text = "Edit Profile",
                                color = Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .border(1.dp, Gray, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text(
                            text = "Title",
                            modifier = Modifier
                                .padding(top = paddingSmall, start = paddingMedium)
                                .onGloballyPositioned {
                                    titleHeight.value = it.size.height.toFloat()
                                }
                        )
                    }

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text("Items")
                    }

                    items(20) { index ->
                        ItemListItem(index)
                    }
                }
            }

            Header(scrollState, collapseRangePx, Modifier.zIndex(1f), collapseRangeReached)
            Avatar(
                scrollState,
                collapseRangePx,
                paddingMediumPx,
                collapseRangeReached,
                avatarZIndex,
                R.drawable.avatar_3_raster
            )
            Toolbar(
                scrollState,
                collapseRangePx,
                titleHeight,
                avatarSizePx,
                profileNameTopPaddingPx,
                collapseRangeReached,
                Modifier
                    .zIndex(3f)
                    .statusBarsPadding()
            )
            ToolbarActions(
                Modifier
                    .zIndex(4f)
                    .statusBarsPadding()
            )
        }
    }
}

@Composable
private fun TwitterCollapsingToolbar2() {
    val scrollState = rememberScrollState()
    val titleHeight = remember { mutableStateOf(0f) }
    val collapseRangePx = with(LocalDensity.current) { collapseRange.toPx() }
    val avatarSizePx = with(LocalDensity.current) { avatarSize.toPx() }
    val profileNameTopPaddingPx = with(LocalDensity.current) { paddingSmall.toPx() }
    val paddingMediumPx = with(LocalDensity.current) { paddingMedium.toPx() }

    val collapseRangeReached = remember {
        mutableStateOf(false)
    }

    val lazyGridState = rememberLazyGridState()

    val avatarZIndex = remember {
        mutableStateOf(2f)
    }

    var offset by remember {
        mutableStateOf(0f)
    }

    val coroutineScope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = -available.y

                offset = (offset - available.y).coerceIn(0f, scrollState.maxValue.toFloat())
                coroutineScope.launch {
                    if (scrollState.isScrollInProgress.not()) {
                        scrollState.scrollBy(delta)
                    }
                }
                collapseRangeReached.value = offset >= collapseRangePx
                avatarZIndex.value = if (collapseRangeReached.value)
                    0f
                else
                    2f
                return Offset.Zero
            }

        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .zIndex(0f)
                    .background(Color.Black)
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(Modifier.height(headerHeight))
                    Text(
                        text = "Edit Profile",
                        color = Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .border(1.dp, Gray, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.heightIn(max = 1000.dp),
//                    modifier = Modifier.heightIn(max = 2000.dp),
                ) {

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text(
                            text = "Title",
                            modifier = Modifier
                                .padding(top = paddingSmall, start = paddingMedium)
                                .onGloballyPositioned {
                                    titleHeight.value = it.size.height.toFloat()
                                }
                        )
                    }

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text("Items")
                    }

                    items(20) { index ->
                        ItemListItem(index)
                    }
                }
            }

            Header(scrollState, collapseRangePx, Modifier.zIndex(1f), collapseRangeReached)
            Avatar(
                scrollState,
                collapseRangePx,
                paddingMediumPx,
                collapseRangeReached,
                avatarZIndex,
                R.drawable.avatar_2_raster
            )
            Toolbar(
                scrollState,
                collapseRangePx,
                titleHeight,
                avatarSizePx,
                profileNameTopPaddingPx,
                collapseRangeReached,
                Modifier
                    .zIndex(3f)
                    .statusBarsPadding()
            )
            ToolbarActions(
                Modifier
                    .zIndex(4f)
                    .statusBarsPadding()
            )
        }
    }
}

@Composable
private fun TwitterCollapsingToolbar1() {
    val scrollState = rememberScrollState()
    val titleHeight = remember { mutableStateOf(0f) }
    val collapseRangePx = with(LocalDensity.current) { collapseRange.toPx() }
    val avatarSizePx = with(LocalDensity.current) { avatarSize.toPx() }
    val profileNameTopPaddingPx = with(LocalDensity.current) { paddingSmall.toPx() }
    val paddingMediumPx = with(LocalDensity.current) { paddingMedium.toPx() }

    val collapseRangeReached = remember {
        derivedStateOf {
            scrollState.value >= (collapseRangePx)
        }
    }

    val avatarZIndex = remember {
        derivedStateOf {
            if (collapseRangeReached.value)
                0f
            else
                2f
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val lazyGridState = rememberLazyGridState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = -available.y
                coroutineScope.launch {
                    if (lazyGridState.isScrollInProgress.not()) {
                        lazyGridState.scrollBy(delta)
                    }
                }
                return Offset.Zero
            }

        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .zIndex(0f)
                    .background(Color.Black)
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(headerHeight))
                Text(
                    text = "Edit Profile",
                    color = Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .border(1.dp, Gray, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.height(1000.dp),
                    // Setting this modifier let's LazyVerticalGrid to
                    // be measured between 0.dp - 2000.dp
                    // which composes all of the items initially
//                    modifier = Modifier.heightIn(2000.dp),
                ) {

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text(
                            text = "Title",
                            modifier = Modifier
                                .padding(top = paddingSmall, start = paddingMedium)
                                .onGloballyPositioned {
                                    titleHeight.value = it.size.height.toFloat()
                                }
                        )
                    }

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text("Items")
                    }

                    items(20) { index ->
                        ItemListItem(index)
                    }
                }
            }

            Header(scrollState, collapseRangePx, Modifier.zIndex(1f), collapseRangeReached)
            Avatar(
                scrollState,
                collapseRangePx,
                paddingMediumPx,
                collapseRangeReached,
                avatarZIndex,
                R.drawable.avatar_1_raster
            )
            Toolbar(
                scrollState,
                collapseRangePx,
                titleHeight,
                avatarSizePx,
                profileNameTopPaddingPx,
                collapseRangeReached,
                Modifier
                    .zIndex(3f)
                    .statusBarsPadding()
            )
            ToolbarActions(
                Modifier
                    .zIndex(4f)
                    .statusBarsPadding()
            )
        }
    }
}
