package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial2_5Screen8() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

        StyleableTutorialText(
            text = "The object of LazyListLayoutInfo calculated during the last layout pass. " +
                    "For example, you can use it to calculate what items are currently visible.\n" +
                    "Note that this property is observable and is updated after every scroll" +
                    "or remeasure. If you use it in the composable function it will be recomposed " +
                    "on every change causing potential performance issues including infinity " +
                    "recomposition loop. " +
                    "Therefore, avoid using it in the composition.\n" +
                    "If you want to run some side effects like sending an analytics event " +
                    "or updating a state based on this value consider using **snapshotFlow**",
            bullets = false
        )

        Spacer(modifier = Modifier.height(30.dp))

        val state = rememberLazyListState()

        var contentPaddingStart by remember {
            mutableStateOf(0f)
        }

        var contentPaddingEnd by remember {
            mutableStateOf(0f)
        }

        Text("Content Padding Start ${contentPaddingStart}dp")
        Slider(
            value = contentPaddingStart,
            valueRange = 0f..50f,
            onValueChange = { contentPaddingStart = it }
        )

        Text("Content Padding End ${contentPaddingEnd}dp")
        Slider(
            value = contentPaddingEnd,
            valueRange = 0f..50f,
            onValueChange = { contentPaddingEnd = it }
        )

        val text by remember {
            derivedStateOf {
                val lazyLayoutInfo = state.layoutInfo
                val visibleItemsInfo = lazyLayoutInfo.visibleItemsInfo
                val viewportSize = lazyLayoutInfo.viewportSize
                val viewportStartOffset = lazyLayoutInfo.viewportStartOffset
                val viewportEndOffset = lazyLayoutInfo.viewportEndOffset
                val beforeContentPadding = lazyLayoutInfo.beforeContentPadding
                val afterContentPadding = lazyLayoutInfo.afterContentPadding

                var tempText = "firstVisibleItemIndex: ${state.firstVisibleItemIndex}\n" +
                        "firstVisibleItemScrollOffset: ${state.firstVisibleItemScrollOffset}\n" +
                        "viewportSize: $viewportSize\n" +
                        "viewportStartOffset: $viewportStartOffset\n" +
                        "viewportEndOffset: $viewportEndOffset\n" +
                        "beforeContentPadding: $beforeContentPadding\n" +
                        "afterContentPadding: $afterContentPadding\n"

                val visibleItemSize = visibleItemsInfo.size

                tempText += "visibleItemsInfo size: $visibleItemSize\n"

                visibleItemsInfo.forEach { lazyListItemInfo: LazyListItemInfo ->

                    val index = lazyListItemInfo.index
                    val offset = lazyListItemInfo.offset
                    val size = lazyListItemInfo.size

                    tempText += "index: $index, offset: $offset, size: $size\n"
                }

                tempText
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            contentPadding = PaddingValues(
                start = contentPaddingStart.toInt().dp,
                end = contentPaddingEnd.toInt().dp,
                top = 0.dp,
                bottom = 0.dp
            )
        ) {
            items(10) {
                Text(
                    "ROW $it",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .width(100.dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    color = Color.White
                )
            }
        }

        Text(text)
    }
}