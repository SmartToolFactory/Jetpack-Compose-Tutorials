package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.detectDragGesture
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor


@Preview
@Composable
fun DragPagerTest() {

    val images = remember {
        listOf(
            R.drawable.avatar_1_raster,
            R.drawable.avatar_2_raster,
            R.drawable.avatar_3_raster,
            R.drawable.avatar_4_raster,
            R.drawable.avatar_5_raster,
            R.drawable.avatar_6_raster,
        )
    }

    val pagerState = rememberPagerState {
        5
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                println("onPreScroll() available: $available")
                return super.onPreScroll(available, source)
            }
        }
    }

    Column(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)

    ) {

        var text by remember {
            mutableStateOf("")
        }

        Box {
            Column(
                modifier = Modifier
                    // This is default detectDragGestures
//                    .pointerInput(Unit) {
//                        detectDragGestures(
//                            onDragStart = {
//                                text = "detectDragGestures() onDragStart..."
//                            },
//                            onDrag = { change, dragAmount ->
//                                text = "onDrag() dragAmount: $dragAmount"
//                            },
//                            onDragCancel = {
//                                text = "onDragCancel"
//                            },
//                            onDragEnd = {
//                                text = "onDragEnd"
//                            }
//                        )
//                    }

                    .pointerInput(Unit) {
                        // This one takes shouldAwaitTouchSlop param to not wait slope, or threshold
                        // has requireUnconsumed param to invoke awaitTouchSlope or drag even
                        // if scroll consumed them before
                        // pass to change propagation direction, with initial
                        // it can receive gestures from any gesture with other passes
                        // if there are any with initial pass it should be above them
                        // or in parent to be invoked first
                        detectDragGesture(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial,
                            onDragStart = { _, _ ->
                                text = "onDragStart..."
                            },
                            onDrag = { change, dragAmount ->
                                text = "onDrag...$dragAmount"
                                println("onDrag $dragAmount")
                                change.consume()
                            },
                            onDragCancel = {
                                text = "onDragCancel"
                            },
                            onDragEnd = {
                                text = "onDragEnd"
                            }
                        )
                    }

            ) {

                Column(
                    modifier = Modifier.fillMaxSize()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                ) {
                    HorizontalPager(
                        modifier = Modifier,
                        state = pagerState
                    ) { page ->

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(3 / 4f),
                            painter = painterResource(images[page % images.size]),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = null
                        )
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Spacer(Modifier.height(32.dp))
                        Text("Title", fontSize = 24.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(text, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DragTest() {

    var textDefaultDrag by remember {
        mutableStateOf("")
    }

    var textCustomDrag by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text("Default drag")
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .background(Color.Red)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        textDefaultDrag = "onDragStart()"
                    },
                    onDrag = { change, dragAmount ->
                        textDefaultDrag = "onDrag() ${change.position}, dragAmount: $dragAmount"
                    },
                    onDragEnd = {
                        textDefaultDrag = "onDragEnd"
                    },
                    onDragCancel = {
                        textDefaultDrag = "onDragEnd"
                    }
                )
            }
        ) {
            Text(textDefaultDrag)
        }


        Text("Custom Drag")

        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .background(Color.Yellow)
            .pointerInput(Unit) {
                // This one takes shouldAwaitTouchSlop param to not wait slope, or threshold
                // has requireUnconsumed param to invoke awaitTouchSlope or drag even
                // if scroll consumed them before
                // pass to change propagation direction, with initial
                // it can receive gestures from any gesture with other passes
                // if there are any with initial pass it should be above them
                // or in parent to be invoked first
                detectDragGesture(
                    requireUnconsumed = false,
                    pass = PointerEventPass.Initial,
                    onDragStart = { _, _ ->
                        textCustomDrag = "onDragStart()"
                    },
                    onDrag = { change, dragAmount ->
                        textCustomDrag = "onDrag() ${change.position}, dragAmount: $dragAmount"
                    },
                    onDragEnd = {
                        textCustomDrag = "onDragEnd"
                    },
                    onDragCancel = {
                        textCustomDrag = "onDragEnd"
                    }
                )
            }
        ) {
            Text(textCustomDrag)
        }
    }
}
