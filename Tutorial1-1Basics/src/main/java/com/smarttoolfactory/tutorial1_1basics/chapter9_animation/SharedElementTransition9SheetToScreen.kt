@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R

@Preview
@Composable
private fun SharedElementsample() {

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {

        var state by remember {
            mutableStateOf<Screen>(Screen.List)
        }

        BackHandler(enabled = state != Screen.List) {
            state = Screen.List
        }

        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {

            val sharedTransitionScope = this

            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = state,
                label = "",
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = EnterTransition.None,
                        initialContentExit = ExitTransition.None
                    )
                }
            ) { screenState ->
                val animatedVisibilityScope = this

                if (screenState is Screen.List) {
                    BottomSheetImagePicker(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedVisibilityScope,
                        onClick = {
                            state = Screen.Details(it)
                        },
                        onDismiss = {}
                    )
                } else if (screenState is Screen.Details) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(screenState.item),
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = screenState.item),
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.CenterStart,
                            contentDescription = null
                        )
                    }
                }
            }
        }

//        val navController = rememberNavController()
//        NavHost(
//            navController = navController,
//            startDestination = "home",
//        ) {
//            composable("home") {
//                BottomSheetImagePicker(
//                    sharedTransitionScope = this@SharedTransitionLayout,
//                    animatedContentScope = this@composable,
//                    onClick = {
//                        navController.navigate("details/$it")
//                    },
//                    onDismiss = {}
//                )
//            }
//
//            composable(
//                "details/{item}",
//                arguments = listOf(navArgument("item") { type = NavType.IntType })
//            ) { backStackEntry ->
//                val item = backStackEntry.arguments?.getInt("item") ?: 0
//                Column(
//                    modifier = Modifier.fillMaxSize().background(Color.Black),
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Image(
//                        painter = painterResource(item),
//                        modifier = Modifier
//                            .sharedElement(
//                                state = rememberSharedContentState(key = item),
//                                animatedVisibilityScope = this@composable,
//                            )
//                            .fillMaxWidth(),
//                        contentScale = ContentScale.Crop,
//                        alignment = Alignment.CenterStart,
//                        contentDescription = null
//                    )
//                }
//            }
//        }
    }
}

@Composable
private fun BottomSheetImagePicker(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onDismiss: () -> Unit,
    onClick: (Int) -> Unit,
) {
    val imageUris = remember {
        listOf(
            R.drawable.landscape1,
            R.drawable.landscape2,
            R.drawable.landscape3,
            R.drawable.landscape4,
            R.drawable.landscape5,
            R.drawable.landscape6,
            R.drawable.landscape7,
            R.drawable.landscape8,
            R.drawable.landscape9,
            R.drawable.landscape10
        )
    }

    val drawerState = rememberBottomDrawerState(
        initialValue = BottomDrawerValue.Open
    )

    BottomDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = drawerState,
        content = {
            Box(modifier = Modifier.fillMaxSize())
        },
        drawerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        drawerContent = {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(48.dp, 12.dp)
                        .background(Color.LightGray, RoundedCornerShape(16.dp))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Red, RoundedCornerShape(16.dp))
                    )
                }

                item {

                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(8.dp)
                    ) {
                        ImageItem(
                            sharedTransitionScope,
                            animatedContentScope,
                            imageUris[0],
                            onClick
                        )
                        ImageItem(
                            sharedTransitionScope,
                            animatedContentScope,
                            imageUris[1],
                            onClick
                        )
                    }
                }

                val otherImages = imageUris.drop(2)
                items(otherImages) { uri ->
                    ImageItem(
                        sharedTransitionScope,
                        animatedContentScope,
                        uri,
                        onClick
                    )
                }
            }
        }
    )
}

@Composable
private fun ImageItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    @DrawableRes uri: Int,
    onClick: (Int) -> Unit,
) {
    with(sharedTransitionScope) {
        Image(
            modifier = Modifier.sharedElement(
                state = rememberSharedContentState(key = uri),
                animatedVisibilityScope = animatedContentScope
            ).clickable {
                onClick(uri)
            },
            painter = painterResource(uri),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}