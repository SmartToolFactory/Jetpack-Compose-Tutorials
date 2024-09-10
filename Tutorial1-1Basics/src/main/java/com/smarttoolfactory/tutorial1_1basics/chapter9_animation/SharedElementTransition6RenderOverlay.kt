@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R

@SuppressLint("PrimitiveInCollection")
@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun SharedElementRenderInSharedTransitionScopeOverlay() {
    var state by remember {
        mutableStateOf<Screen>(Screen.List)
    }

    val listScrollState = rememberLazyListState()

    val images = remember {
        listOf(
            R.drawable.avatar_1_raster,
            R.drawable.avatar_2_raster,
            R.drawable.avatar_3_raster
        )
    }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state,
            label = "",
            contentKey = { it.javaClass },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) {
            when (it) {
                Screen.List -> {
                    Box(
                        contentAlignment = Alignment.BottomStart
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 56.dp),
                            state = listScrollState
                        ) {
                            items(50) { item ->
                                Row(modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        state = Screen.Details(item)
                                    }
                                    .fillMaxWidth()) {
                                    Image(
                                        painter = painterResource(images[item % 3]),
                                        modifier = Modifier
                                            .size(100.dp)
                                            .sharedElement(
                                                rememberSharedContentState(
                                                    key = "item-image$item"
                                                ),
                                                animatedVisibilityScope = this@AnimatedContent,
                                            ),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.size(15.dp))
                                    Text("Item $item")
                                }
                            }
                        }

                        BottomBar(
                            /*
                            Renders the content in the SharedTransitionScope's overlay, where
                            shared content (i. e. shared elements and shared bounds) is rendered
                            by default. This is useful for rendering content that is not shared on
                            top of shared content to preserve a specific spatial relationship.
                             */
                            // 🔥 Uncomment this to see effect
                            // while clicking an item partially visible at the bottom
                            modifier = Modifier
                                .renderInSharedTransitionScopeOverlay(
                                    zIndexInOverlay = 1f
                                )
                                .animateEnterExit(
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                )
                        )
                    }
                }

                is Screen.Details -> {
                    val item = it.item
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            state = Screen.List
                        }) {

                        Text("Some Text")
                        Image(
                            painter = painterResource(images[item % 3]),
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = "item-image$item"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                        Text(
                            "Item $item",
                            fontSize = 23.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(modifier: Modifier = Modifier) {
    BottomAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.onSurface,
        contentColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {

        // Leading icons should typically have a high content alpha
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            IconButton(
                onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }

        }
        // The actions should be at the end of the BottomAppBar. They use the default medium
        // content alpha provided by BottomAppBar
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { }) {
            Icon(Icons.Filled.Search, contentDescription = null)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
        }
    }
}