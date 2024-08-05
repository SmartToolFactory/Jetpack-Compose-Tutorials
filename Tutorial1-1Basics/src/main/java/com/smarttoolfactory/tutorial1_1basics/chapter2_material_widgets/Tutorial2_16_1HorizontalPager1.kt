package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial2_16Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TutorialHeader(text = "HorizontalPager")

        val pagerState = rememberPagerState {
            10
        }

        // Check swipe event alternative 1
        var userScrolled by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(pagerState.isScrollInProgress) {
            if (pagerState.isScrollInProgress) {
                userScrolled = true
            }
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }.collect {
                if (pagerState.targetPage == pagerState.settledPage && userScrolled) {
                    println("Swiped to ${pagerState.currentPage}")
                }
            }
        }


        // Check swipe event alternative 2
        // This one considers swiping more than half of the screen as a swipe
        // even before movement is completed or even when swiped back
//        LaunchedEffect(pagerState) {
//            snapshotFlow { pagerState.currentPage }.collect {
//                if (pagerState.currentPage != pagerState.settledPage) {
//                    println("Swiped to ${pagerState.currentPage}")
//                }
//            }
//        }

        val coroutineScope = rememberCoroutineScope()
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

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = 9,
                        animationSpec = tween(3000)
                    )
                }
            }
        ) {
            Text("Animate to last item")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = 0,
                        animationSpec = tween(3000)
                    )
                }
            }
        ) {
            Text("Animate to first item")
        }
        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            beyondViewportPageCount = 1,
            contentPadding = PaddingValues(horizontal = 16.dp),
            snapPosition = SnapPosition.Start
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
                    .background(Color.LightGray)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = "Page $it",
                        fontSize = 28.sp
                    )

                }
            }
        }
    }

}