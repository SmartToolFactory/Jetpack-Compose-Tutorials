@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu

val listSnacks = listOf(
    SnackItem("Cupcake", "", R.drawable.cupcake),
    SnackItem("Donut", "", R.drawable.donut),
    SnackItem("Eclair", "", R.drawable.eclair),
    SnackItem("Froyo", "", R.drawable.froyo),
    SnackItem("Gingerbread", "", R.drawable.gingerbread),
    SnackItem("Honeycomb", "", R.drawable.honeycomb),
)

data class SnackItem(
    val name: String,
    val description: String,
    @DrawableRes val image: Int,
)

@Preview
@Composable
fun SharedElement_PlaceholderSize() {
    var state by remember {
        mutableStateOf<Screen>(Screen.List)
    }

    val listScrollState = rememberLazyListState()

    var placeHolderIndex by remember {
        mutableIntStateOf(0)
    }

    var placeHolderSize by remember {
        mutableStateOf(
            SharedTransitionScope.PlaceHolderSize.contentSize
        )
    }


    Column {

        ExposedSelectionMenu(
            title = "ResizeMode",
            index = placeHolderIndex,
            options = listOf(
                "contentSize", "animatedSize"
            ),
            onSelected = {
                placeHolderIndex = it

                placeHolderSize = if (placeHolderIndex == 0) {
                    SharedTransitionScope.PlaceHolderSize.contentSize
                } else {
                    SharedTransitionScope.PlaceHolderSize.animatedSize
                }
            }
        )


        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {


            AnimatedContent(
                targetState = state,
                label = "",
                contentKey = { it.javaClass },
                transitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                }
            ) {
                when (it) {
                    Screen.List -> {
                        Column {
                            LazyRow(
                                state = listScrollState,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(items = listSnacks) { index, item: SnackItem ->
                                    Row(modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            state = Screen.Details(index)
                                        }

                                    ) {
                                        Image(
                                            painter = painterResource(item.image),
                                            modifier = Modifier
                                                .sharedElement(
                                                    rememberSharedContentState(
                                                        key = "item-image$index"
                                                    ),
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    // 🔥 Changing placeHolderSize effects how other
                                                    // items will react during animation
                                                    placeHolderSize = placeHolderSize,
                                                    boundsTransform = imageBoundsTransform,
                                                    clipInOverlayDuringTransition = OverlayClip(
                                                        shapeForShared
                                                    )
                                                )
                                                .width(100.dp)
                                                .aspectRatio(3 / 4f)
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = null
                                        )

                                    }
                                }
                            }

                            Image(
                                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(16.dp)),
                                painter = painterResource(R.drawable.eclair),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = null
                            )
                        }
                    }

                    is Screen.Details -> {
                        val index = it.item
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                state = Screen.List
                            }) {

                            Image(
                                painter = painterResource(listSnacks[index].image),
                                modifier = Modifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "item-image$index"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        // 🔥 Changing placeHolderSize effects how other
                                        // items will react during animation
                                        placeHolderSize = placeHolderSize,
                                        boundsTransform = imageBoundsTransform,
                                        clipInOverlayDuringTransition = OverlayClip(shapeForShared)
                                    )
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}


private val imageBoundsTransform: BoundsTransform = BoundsTransform { _: Rect, _: Rect ->
    tween(durationMillis = 1000, easing = FastOutSlowInEasing)
}