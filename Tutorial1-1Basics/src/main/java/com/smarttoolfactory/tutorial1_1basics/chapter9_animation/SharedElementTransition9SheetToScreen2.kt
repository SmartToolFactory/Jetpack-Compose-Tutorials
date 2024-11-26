@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.smarttoolfactory.tutorial1_1basics.R

@Preview
@Composable
private fun SharedElementsample2() {

    BottomSheetImagePicker(
        onDismiss = {},
        onClick = {}
    )
}

@Composable
fun BottomSheetImagePicker(
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

    var state by remember {
        mutableStateOf<Screen>(Screen.List)
    }

    val sheetState = rememberModalBottomSheetState()

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {

        val sharedTransitionScope = this

        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = state,
            label = "",
            contentKey = { it.javaClass },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = EnterTransition.None,
                    initialContentExit = ExitTransition.None
                )
            }
        ) { screenState ->

            val animatedVisibilityScope = this

            ModalBottomSheet(
                modifier = Modifier
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        alpha = if (state is Screen.List) 1f else 0f
                    },
                onDismissRequest = onDismiss,
                sheetState = sheetState,
            ) {
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ImageItem(
                                uri = imageUris[0],
                                onClick = {
                                    state = Screen.Details(it)
                                }
                            )
                            ImageItem(
                                uri = imageUris[1],
                                onClick = {
                                    state = Screen.Details(it)
                                }
                            )
                        }
                    }

                    val otherImages = imageUris.drop(2)
                    items(otherImages) { uri ->
                        ImageItem(
                            uri = uri,
                            onClick = {
                                state = Screen.Details(it)
                            }
                        )
                    }
                }

                if (screenState is Screen.Details) {
                    BasicAlertDialog(
                        onDismissRequest = {
                            state = Screen.List
                        },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().background(Color.Black),
                        ) {
                            Image(
                                painter = painterResource(screenState.item),
                                modifier = Modifier
//                                    .sharedElement(
//                                        state = rememberSharedContentState(key = screenState.item),
//                                        animatedVisibilityScope = this@AnimatedContent,
//                                    )
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
    }
}

@Composable
fun ImageItem(
    modifier: Modifier = Modifier,
    @DrawableRes uri: Int,
    onClick: (Int) -> Unit,
) {
    Image(
        modifier = modifier
            .clickable {
                onClick(uri)
            },
        painter = painterResource(uri),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}