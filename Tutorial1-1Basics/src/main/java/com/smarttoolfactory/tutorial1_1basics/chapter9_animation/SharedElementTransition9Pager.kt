@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Preview
@Composable
fun SharedElement_Pager() {

    val scrollState = rememberLazyListState()

    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {

            composable("home") {
                HomeScreen(
                    scrollState = scrollState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                ) {
                    navController.navigate(it)
                }
            }

            composable(
                "details/{item}",
                arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item")
                val snack = listSnacks[id!!]
                DetailsScreen(
                    snack,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                ) {
                    navController.navigate(it)
                }
            }
        }
    }
}

@Composable
private fun DetailsScreen(
    snack: SnackItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (String) -> Unit,
) {

    var selectedIndex by remember {
        mutableStateOf(listSnacks.indexOf(snack).coerceAtLeast(0))
    }
    with(sharedTransitionScope) {


        val pagerState = rememberPagerState(
            initialPage = selectedIndex,
            pageCount = {
                listSnacks.size
            }
        )

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp
        ) { page ->

            selectedIndex = page
            val currentSnack = listSnacks[page]

            Column(
                Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {
                        onClick("home")
                    }
            ) {
                Image(
                    painterResource(id = currentSnack.image),
                    contentDescription = currentSnack.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-$selectedIndex"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .aspectRatio(1f)
                        .fillMaxWidth()
                )
                Text(
                    currentSnack.name, fontSize = 38.sp,
                    modifier =
                    Modifier
                        .sharedBounds(
                            sharedTransitionScope.rememberSharedContentState(key = "text-$selectedIndex"),
                            animatedVisibilityScope = animatedContentScope,
                        )
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(

    scrollState: LazyListState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (String) -> Unit,
) {

    var isDetailSelected by remember {
        mutableStateOf(false)
    }

    with(sharedTransitionScope) {
        Column {

            TopAppBar(
                title = {
                    Text("Title")
                },
                backgroundColor = Color.Black,
                contentColor = Color.White,
                modifier = if (isDetailSelected) Modifier
                else Modifier.renderInSharedTransitionScopeOverlay()
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(listSnacks) { index, item ->
                    Row(
                        Modifier.clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {
                            isDetailSelected = true
                            onClick("details/$index")
                        }
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painterResource(id = item.image),
                            contentDescription = item.description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    sharedTransitionScope.rememberSharedContentState(key = "image-$index"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .size(120.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            item.name, fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .sharedBounds(
                                    sharedTransitionScope.rememberSharedContentState(key = "text-$index"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                        )
                    }
                }
            }
        }
    }

}


@Preview
@Composable
fun Test() {
    val count = remember { mutableIntStateOf(0) }
    val incrementCounter = { count.intValue++ }

    SideEffect {
        println("Recomposed with ${count.value}")
    }
    Box(
        Modifier
            .size(300.dp)
            .background(Color.Red)
    ) {
        // Box1
        Box(Modifier
            .clipToBounds()
            .size(300.dp)
            .drawWithCache {
                onDrawBehind {
                    println("DrawBehind")
                }
            }
            .pointerInput(1, 2) {
                detectTapGestures { offset ->
                    incrementCounter()
                }
            }) {}


        MyBox {
            // Box2
            Box(
                Modifier
                    .size(300.dp)
            ) {
                repeat(count.intValue) {
                    Text("$it")
                }
            }
        }
    }
}

@Composable
fun MyBox(
    content: @Composable () -> Unit,
) {
    Box {
        content()
    }
}