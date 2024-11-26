@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, ExperimentalAnimationSpecApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.launch

@Preview
@Composable
private fun SharedElementsample() {

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable("home") {
                BottomSheetImagePicker(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    onClick = {
                        navController.navigate("details/$it")
                    },
                    onDismiss = {}
                )
            }

            composable(
                "details/{item}",
                arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val item = backStackEntry.arguments?.getInt("item") ?: 0
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(item),
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = item),
                                animatedVisibilityScope = this@composable,
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

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 400.dp,
        content = {

            val bottomState = scaffoldState.bottomSheetState
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {

                Spacer(Modifier.weight(1f))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            bottomState.partialExpand()
                        }
                    }
                ) {
                    Text("Expand")
                }
            }
            if (bottomState.isVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = .3f))
                )

            }
        },
        sheetContent = {
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
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = gridBoundsTransform
            ).clickable {
                onClick(uri)
            },
            painter = painterResource(uri),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

val gridBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = 500
        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
        targetBounds at 500
    }
}
