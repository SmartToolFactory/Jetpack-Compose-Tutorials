@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun SharedElement_ManualVisibleControl() {

    var selectFirst by remember { mutableStateOf(true) }
    val key = remember { Any() }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable {
                selectFirst = !selectFirst
            }
    ) {

        /*
            🔥 Important: The shared element remains in the UI tree even when visible == false.
            The shared element starts a transition whenever its size or position changes as it
            has an active match. Therefore it is recommended to remove the shared element
            with visible == false from the tree once the transition is finished, by observing
            SharedTransitionScope.isTransitionActive.
         */

        println("isTransitionActive: $isTransitionActive")
        Box(
            Modifier
                .sharedElementWithCallerManagedVisibility(
                    rememberSharedContentState(key = key),
                    !selectFirst
                )
                .background(Color.Red)
                .size(100.dp)
        ) {
            Text("$selectFirst", color = Color.White)
        }
        Box(
            Modifier
                .offset(180.dp, 180.dp)
                .sharedElementWithCallerManagedVisibility(
                    rememberSharedContentState(
                        key = key,
                    ),
                    selectFirst
                )
                .alpha(0.5f)
                .background(Color.Blue)
                .size(180.dp)
        ) {
            Text("$selectFirst", color = Color.White)
        }
    }
}

@Preview
@Composable
fun SharedElementWithCallerManagedVisibility() {
    var selectFirst by remember { mutableStateOf(true) }
    val key = remember { Any() }
    SharedTransitionLayout(
        Modifier.fillMaxSize().padding(10.dp).clickable { selectFirst = !selectFirst }
    ) {
        Box(
            Modifier.sharedElementWithCallerManagedVisibility(
                rememberSharedContentState(key = key),
                !selectFirst,
                boundsTransform = boundsTransform
            )
                .background(Color.Red)
                .size(100.dp)
        ) {
            Text(if (!selectFirst) "false" else "true", color = Color.White)
        }

        // TODO: Check isTransitionActive is false in the end. Why are the shared bounds not
        // two separate entities when transition is finished?
        Box(
            Modifier.offset(180.dp, 180.dp)
                .sharedElementWithCallerManagedVisibility(
                    rememberSharedContentState(
                        key = key,
                    ),
                    selectFirst,
                    boundsTransform = boundsTransform
                )
                .alpha(0.5f)
                .background(Color.Blue)
                .size(180.dp)
        ) {
            Text(if (selectFirst) "false" else "true", color = Color.White)
        }
    }
}

private val boundsTransform = BoundsTransform { initial, target ->
    // Move vertically first then horizontally
    keyframes {
        durationMillis = 500
        initial at 0
        Rect(initial.left, target.top, initial.left + target.width, target.bottom) at 300
    }
}