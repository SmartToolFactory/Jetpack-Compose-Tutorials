package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial4_11Screen6() {
    TutorialContent()
}


@Preview
@Composable
private fun TutorialContent() {

    val directionalLazyListState = rememberDirectionalLazyListState(
        rememberLazyListState(),
        rememberCoroutineScope()
    )

    val lazyListState: LazyListState = directionalLazyListState.lazyListState

    val text by remember {
        derivedStateOf {
            "isScrollInProgress: ${lazyListState.isScrollInProgress}\n" +
                    "firstVisibleItemIndex: ${lazyListState.firstVisibleItemIndex}\n" +
                    "firstVisibleItemScrollOffset: ${lazyListState.firstVisibleItemScrollOffset}"
        }
    }


    val color = when (directionalLazyListState.scrollDirection) {
        ScrollDirection.Up -> Color.Green
        ScrollDirection.Down -> Color.Blue
        else -> Color.Black
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(8.dp)
    ) {

        Text(text, fontSize = 16.sp)
        Text(
            "Direction: ${directionalLazyListState.scrollDirection}",
            fontSize = 24.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(50) {
                Row(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Row $it",
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}

enum class ScrollDirection {
    Up, Down, None
}

@Composable
fun rememberDirectionalLazyListState(
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope
): DirectionalLazyListState {
    return remember {
        DirectionalLazyListState(lazyListState, coroutineScope)
    }
}

@Stable
class DirectionalLazyListState(
    val lazyListState: LazyListState,
    coroutineScope: CoroutineScope
) {
    private var firstItemOffset = lazyListState.firstVisibleItemScrollOffset
    private var firstItemIndex = lazyListState.firstVisibleItemIndex

    private var currentTime = System.currentTimeMillis()
    var scrollDirection by mutableStateOf(ScrollDirection.None)

    init {

        coroutineScope.launch {
            while (isActive) {
                delay(240)
                if (System.currentTimeMillis() - currentTime > 240) {
                    scrollDirection = ScrollDirection.None
                }
            }
        }

        snapshotFlow {
            val scrollInt = if (lazyListState.isScrollInProgress) 20000 else 10000
            val visibleItemInt = lazyListState.firstVisibleItemIndex * 10
            scrollInt + visibleItemInt + lazyListState.firstVisibleItemScrollOffset
        }
            .onEach {
                if (lazyListState.isScrollInProgress.not()) {
                    scrollDirection = ScrollDirection.None
                } else {

                    currentTime = System.currentTimeMillis()

                    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                    val firstVisibleItemScrollOffset =
                        lazyListState.firstVisibleItemScrollOffset

                    // We are scrolling while first visible item hasn't changed yet
                    if (firstVisibleItemIndex == firstItemIndex) {
                        val direction = if (firstVisibleItemScrollOffset > firstItemOffset) {
                            ScrollDirection.Down
                        } else {
                            ScrollDirection.Up
                        }
                        firstItemOffset = firstVisibleItemScrollOffset

                        scrollDirection = direction
                    } else {

                        val direction = if (firstVisibleItemIndex > firstItemIndex) {
                            ScrollDirection.Down
                        } else {
                            ScrollDirection.Up
                        }
                        // When next item is on screen first offset
                        firstItemOffset = firstVisibleItemScrollOffset
                        firstItemIndex = firstVisibleItemIndex
                        scrollDirection = direction
                    }
                }
            }
            .launchIn(coroutineScope)
    }

// 🔥 This is another alternative if user not moving pointer when pressed is not
// to be taken into consideration

//    val scrollDirection by derivedStateOf {
//        if (lazyListState.isScrollInProgress.not()) {
//            ScrollDirection.None
//        } else {
//            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
//            val firstVisibleItemScrollOffset =
//                lazyListState.firstVisibleItemScrollOffset
//
//            // We are scrolling while first visible item hasn't changed yet
//            if (firstVisibleItemIndex == visibleItem) {
//                val direction = if (firstVisibleItemScrollOffset > positionY) {
//                    ScrollDirection.Down
//                } else {
//                    ScrollDirection.Up
//                }
//                positionY = firstVisibleItemScrollOffset
//
//                direction
//            } else {
//
//                val direction = if (firstVisibleItemIndex > visibleItem) {
//                    ScrollDirection.Down
//                } else {
//                    ScrollDirection.Up
//                }
//                positionY = firstVisibleItemScrollOffset
//                visibleItem = firstVisibleItemIndex
//                direction
//            }
//        }
//    }
}