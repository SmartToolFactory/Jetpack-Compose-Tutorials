package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.rememberFlingNestedScrollConnection
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
private fun LazyRowSnapAndDeleteAnimation() {

    val viewModel = remember {
        MyViewModel()
    }

    val lazyListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 16.dp)
    ) {

        val list = viewModel.list

        TutorialHeader("Animate deletion in LazyRow as Pager")

        StyleableTutorialText(
            text = "In this example **LazyRow** is transformed into **HorizontalPager** to " +
                    "be able to use **Modifier.animateItem** to animate deleting items.",
            bullets = false
        )

        LazyRow(
            modifier = Modifier.fillMaxSize()
                // Slow fling speed to match Pager
                .nestedScroll(rememberFlingNestedScrollConnection()),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            // Snap to item as Pager when fling ends
            flingBehavior = rememberSnapFlingBehavior(
                lazyListState = lazyListState,
                snapPosition = SnapPosition.Start
            ),
            state = lazyListState
        ) {

            itemsIndexed(
                items = list,
                // 🔥🔥Without unique keys animations do not work
                key = { _, item ->
                    item.id
                }
            ) { page, item ->
                Column(
                    modifier = Modifier
                        .animateItem(
                            fadeOutSpec = tween(1000),
                            placementSpec = tween(1000)
                        )
                        .fillParentMaxWidth()
                        .height(160.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(32.dp)
                ) {

                    SideEffect {
                        println("Recomposing item: $page")
                    }
                    Text("Item ${item.value}", fontSize = 26.sp)

                    Spacer(Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.removeItem(page)
                        }
                    ) {
                        Text("Remove ${item.value}")
                    }
                }
            }
        }
    }
}