@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400

/*
Resize mode
When animating between two shared bounds, you can set the resizeMode parameter to either
RemeasureToBounds or ScaleToBounds. This parameter determines how the shared element transitions
between the two states. ScaleToBounds first measures the child layout with
the lookahead (or target) constraints. Then the child's stable layout is scaled to fit in the
shared bounds. ScaleToBounds can be thought of as a "graphical scale" between the states.

Whereas RemeasureToBounds re-measures and re-layouts the child layout of sharedBounds with
animated fixed constraints based on the target size. The re-measurement is triggered by
the bounds size change, which could potentially be every frame.

For Text composables, ScaleToBounds is recommended as it'll avoid relayout and reflowing of text
onto different lines. For bounds that are different aspect ratios, and if you'd like fluid
continuity between the two shared elements, RemeasureToBounds is recommended.
 */

@Preview
@Composable
fun SharedElementApp_ResizeModeExample() {
    var showDetails by remember {
        mutableStateOf(false)
    }

    var resizeModeIndex by remember {
        mutableIntStateOf(0)
    }

    var resizeMode by remember {
        mutableStateOf(
            ScaleToBounds(
                // Changing ContentScale and Alignment changes how bounds will change
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.Center
            )
        )
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ExposedSelectionMenu(
            title = "ResizeMode",
            index = resizeModeIndex,
            options = listOf(
                "ScaleToBounds", "RemeasureToBounds"
            ),
            onSelected = {
                resizeModeIndex = it

                resizeMode = if (resizeModeIndex == 0) {
                    ScaleToBounds(
                        contentScale = ContentScale.FillWidth,
                        alignment = Alignment.Center
                    )
                } else {
                    RemeasureToBounds
                }
            }
        )

        SharedTransitionLayout {
            AnimatedContent(
                showDetails,
                label = "basic_transition"
            ) { targetState ->
                if (!targetState) {
                    MainContent(
                        onShowDetails = {
                            showDetails = true
                        },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        resizeMode = resizeMode
                    )
                } else {
                    DetailsContent(
                        onBack = {
                            showDetails = false
                        },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        resizeMode = resizeMode
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
private fun MainContent(
    onShowDetails: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    resizeMode: SharedTransitionScope.ResizeMode
) {
    with(sharedTransitionScope) {
        Box(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = fadeIn(
                            tween(
                                boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(
                            tween(
                                boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        boundsTransform = boundsTransform,
                        resizeMode = resizeMode
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(Blue400, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onShowDetails()
                    }
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_1_raster),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                val textBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
                    keyframes {
                        durationMillis = boundsAnimationDurationMillis
                        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                        targetBounds at boundsAnimationDurationMillis
                    }
                }
                Text(
                    "Cupcake", fontSize = 21.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = textBoundsTransform
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    resizeMode: SharedTransitionScope.ResizeMode
) {
    with(sharedTransitionScope) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = fadeIn(
                            tween(
                                durationMillis = boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(
                            tween(
                                durationMillis = boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        boundsTransform = boundsTransform,
                        resizeMode = resizeMode
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(Pink400, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onBack()
                    }
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_1_raster),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // [START android_compose_shared_element_text_bounds_transform]
                val textBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
                    keyframes {
                        durationMillis = boundsAnimationDurationMillis
                        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                        targetBounds at boundsAnimationDurationMillis
                    }
                }
                Text(
                    "Cupcake", fontSize = 28.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = textBoundsTransform
                    )
                )
                // [END android_compose_shared_element_text_bounds_transform]
                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                            " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                            "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                            "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus",
                    modifier = Modifier.skipToLookaheadSize()
                )
            }
        }
    }
}

private val boundsTransform = BoundsTransform { _: Rect, _: Rect ->
    tween(durationMillis = boundsAnimationDurationMillis, easing = FastOutSlowInEasing)
}
private const val boundsAnimationDurationMillis = 2000