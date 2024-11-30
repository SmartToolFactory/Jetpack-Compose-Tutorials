package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.UUID


data class MyData(val id: String = UUID.randomUUID().toString(), val value: Int, val isAlive: Boolean = true)

class MyViewModel : ViewModel() {
    val list =
        mutableStateListOf<MyData>().apply {
            repeat(6) {
                add(
                    MyData(value = it)
                )
            }
        }

    fun updateStatus(index: Int) {
        val newItem = list[index].copy(isAlive = false)
        list[index] = newItem
        println("Update Status: $index")
    }

    fun removeItem(index: Int) {
        println("🔥 Remove item: $index")
        list.removeAt(index)
    }
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        TutorialHeader("Horizontal Pager delete animation")
        Spacer(Modifier.height(16.dp))
        PagerRemoveAnimationSample()

    }
}

@Composable
private fun PagerRemoveAnimationSample() {

    val viewModel = remember {
        MyViewModel()
    }

    val list = viewModel.list

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
    ) {

        val pagerState = rememberPagerState {
            list.size
        }

        var userGestureEnabled by remember {
            mutableStateOf(true)
        }

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(start = 16.dp, end = 12.dp),
            userScrollEnabled = userGestureEnabled,
            key = {
                list[it].id
            }
        ) { page: Int ->
            list.getOrNull(page)?.let { item ->

                val animate = item.isAlive.not()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .animatePagerItem(
                            animate = animate,
                            page = page,
                            list = list,
                            pagerState = pagerState,
                            onStart = {
                                userGestureEnabled = false
                            },
                            onFinish = {
                                userGestureEnabled = true
                                viewModel.removeItem(it)
                            }
                        )
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(32.dp)
                ) {
                    Text(
                        "value: ${item.value}\n" +
                                "isScrollInProgress: ${pagerState.isScrollInProgress}\n" +
                                "fraction: ${pagerState.currentPageOffsetFraction}\n" +
                                "currentPage: ${pagerState.currentPage}\n" +
                                "settledPage: ${pagerState.settledPage}"
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (userGestureEnabled) {
                                viewModel.updateStatus(page)
                            }
                        }
                    ) {
                        Text("Remove ${item.value}")
                    }
                }
            }
        }
    }
}

fun <T> Modifier.animatePagerItem(
    animate: Boolean,
    page: Int,
    list: List<T>,
    pagerState: PagerState,
    onStart: (page: Int) -> Unit,
    onFinish: (page: Int) -> Unit,
) = composed {

    val animatable = remember {
        Animatable(1f)
    }

    LaunchedEffect(animate) {
        if (animate) {
            val animationSpec = tween<Float>(1000)

            onStart(page)
            launch {
                try {
                    animatable.animateTo(
                        targetValue = 0f,
                        animationSpec = animationSpec,
                    )
                    onFinish(page)
                } catch (e: CancellationException) {
                    println("CANCELED $page, ${e.message}")
                    onFinish(page)
                }
            }

            if (list.size > 1 && page != list.lastIndex) {
                launch {
                    pagerState.animateScrollToPage(
                        page = page + 1,
                        animationSpec = animationSpec
                    )
                }
            } else if (page == list.lastIndex) {
                pagerState.animateScrollToPage(
                    page = page - 1,
                    animationSpec = animationSpec
                )
            }
        }
    }

    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        val difference = -constraints.maxWidth * (1 - animatable.value)

        layout(placeable.width, placeable.height) {
            placeable.placeRelativeWithLayer(0, 0) {

                transformOrigin = TransformOrigin(0f, .5f)
                translationX = if (list.size > 1 && page != list.lastIndex) {
                    -difference
                } else if (list.size > 1 && page == list.lastIndex) {
                    difference
                } else 0f

                translationY = -300f * (1 - animatable.value)
                alpha = animatable.value

                // Other animations
//                scaleY = (animatable.value).coerceAtLeast(.8f)
                cameraDistance = (1 - animatable.value) * 100
//                rotationY = -30f * (1 - animatable.value)
//                scaleX = animatable.value

            }
        }
    }
}