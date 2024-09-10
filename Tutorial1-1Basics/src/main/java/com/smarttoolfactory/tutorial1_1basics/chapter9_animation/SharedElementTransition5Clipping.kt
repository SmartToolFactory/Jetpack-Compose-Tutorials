@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R

val shapeForShared = RoundedCornerShape(16.dp)

@Preview
@Composable
private fun SharedElement_Clipping() {
    var showDetails by remember {
        mutableStateOf(false)
    }

    val durationMillis = 2000

    val boundsTransform = remember {
        { initialBounds: Rect, targetBounds: Rect ->
            tween<Rect>(durationMillis)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {

        SharedTransitionLayout {
            AnimatedContent(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        showDetails = showDetails.not()
                    }
                ),
                targetState = showDetails,
                label = "basic_transition"
            ) { targetState ->
                if (!targetState) {
                    Row(
                        modifier = Modifier
                            // [START android_compose_animations_shared_element_clipping]
                            .sharedBounds(
                                rememberSharedContentState(key = "bounds"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = boundsTransform,
                                // 🔥 ParentClip shows non-rounded bounds
                                clipInOverlayDuringTransition = OverlayClip(shapeForShared),
                                enter = fadeIn(
                                    tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutSlowInEasing
                                    )
                                ),
                                exit = fadeOut(
                                    tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutSlowInEasing
                                    )
                                ),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(
                                    contentScale = ContentScale.FillBounds
                                )
                            )
                            .background(Color.Green.copy(alpha = 0.5f), shapeForShared)
                            .clip(shapeForShared)
                            // [END android_compose_animations_shared_element_clipping]
                            .padding(8.dp)
                    ) {
                        // [START android_compose_animations_shared_element_clipping]
                        Image(
                            painter = painterResource(id = R.drawable.avatar_1_raster),
                            contentDescription = "Cupcake",
                            modifier = Modifier
                                .size(100.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = "image"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                                .clip(shapeForShared),
                            contentScale = ContentScale.Crop
                        )
                        // [END android_compose_animations_shared_element_clipping]
                        Text(
                            "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = "title"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            // [START android_compose_animations_shared_element_clipping]
                            .sharedBounds(
                                rememberSharedContentState(key = "bounds"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = boundsTransform,
                                // 🔥 ParentClip shows non-rounded bounds
                                clipInOverlayDuringTransition = OverlayClip(shapeForShared),
                                enter = fadeIn(
                                    tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutSlowInEasing
                                    )
                                ),
                                exit = fadeOut(
                                    tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutSlowInEasing
                                    )
                                ),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(
                                    contentScale = ContentScale.FillBounds
                                )
                            )
                            .background(Color.Blue.copy(alpha = 0.3f), shapeForShared)
                            .clip(shapeForShared)
                            // [END android_compose_animations_shared_element_clipping]
                            .padding(top = 200.dp, start = 16.dp, end = 16.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_1_raster),
                            contentDescription = "Cupcake",
                            modifier = Modifier
                                .size(200.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = "image"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                                .clip(shapeForShared),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "title"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = boundsTransform
                            )
                        )
                        Text(
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                                    " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                                    "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                                    "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus"
                        )
                    }
                }
            }
        }
    }
}
