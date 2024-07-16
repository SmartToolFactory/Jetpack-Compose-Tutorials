package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateRect
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


@Preview
@Composable
fun AnimatedVisibilityTransitionSample() {
    val visibleState = remember { MutableTransitionState(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                visibleState.targetState = visibleState.targetState.not()
            }
        ) {

            Text(
                "State currentState: ${visibleState.currentState}\n" +
                        "targetState: ${visibleState.targetState}\n" +
                        "isIdle:  ${visibleState.isIdle}"
            )
        }

        if (visibleState.targetState || visibleState.currentState) {
            Popup(
                properties = PopupProperties(focusable = true),
                offset = IntOffset(200, 1000),
                onDismissRequest = {
                    visibleState.targetState = false
                }
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(
                        animationSpec = tween(2000)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(2000)
                    )
                ) {
                    Box(modifier = Modifier.background(Color.Red).padding(16.dp)) {
                        Text("Test Composable...")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AnimatedVisibilityCloseTest() {
    val visibleState = remember { MutableTransitionState(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(
            onClick = {
                visibleState.targetState = visibleState.targetState.not()
            }
        ) {
            Text(
                "State currentState: ${visibleState.currentState}\n" +
                        "targetState: ${visibleState.targetState}\n" +
                        "isIdle:  ${visibleState.isIdle}"
            )
        }

        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(
                animationSpec = tween(5000)
            ),
            exit = fadeOut(
                tween(5000)
            )
        ) {
            Box(modifier = Modifier.background(Color.Red).padding(16.dp)) {
                Text("Test Composable...")
            }
        }
    }

}


@Preview
@Composable
fun PoppingInCardPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PoppingInCard()
    }
}

@Preview
@Composable
fun MutableTransitionStatePreview() {
    val visibleState = remember { MutableTransitionState(false) }
    val transition = rememberTransition(visibleState)


    val size by transition.animateDp(
        label = "",
        transitionSpec = {
            tween(3000)
        }
    ) { visible ->
        if (visible) 200.dp else 100.dp
    }
    Column {
        Button(
            onClick = {
                visibleState.targetState = visibleState.targetState.not()
            }
        ) {

            Text(
                "State currentState: ${visibleState.currentState}, " +
                        "targetState: ${visibleState.targetState},isIdle:  ${visibleState.isIdle}"
            )
        }


        Box(
            modifier = Modifier.size(size).background(Color.Red)
        )
    }
}


// This composable enters the composition with a custom enter transition. This is achieved by
// defining a different initialState than the first target state using `MutableTransitionState`
@Composable
fun PoppingInCard() {
    // Creates a transition state with an initial state where visible = false
    val visibleState = remember { MutableTransitionState(false) }
    // Sets the target state of the transition state to true. As it's different than the initial
    // state, a transition from not visible to visible will be triggered.
    visibleState.targetState = true

    // Creates a transition with the transition state created above.
    val transition: Transition<Boolean> = rememberTransition(visibleState)
    // Adds a scale animation to the transition to scale the card up when transitioning in.
    val scale by transition.animateFloat(
        // Uses a custom spring for the transition.
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) }, label = ""
    ) { visible ->
        if (visible) 1f else 0.8f
    }

    // Adds an elevation animation that animates the dp value of the animation.
    val elevation by transition.animateDp(
        // Uses a tween animation
        transitionSpec = {
            // Uses different animations for when animating from visible to not visible, and
            // the other way around
            if (false isTransitioningTo true) {
                tween(1000)
            } else {
                spring()
            }
        }, label = ""
    ) { visible ->
        if (visible) 10.dp else 0.dp
    }

    transition.animateRect(label = "") { visible ->
        if (visible) {
            Rect.Zero
        } else {
            Rect(
                offset = Offset.Zero,
                size = Size(100f, 100f)
            )
        }
    }
    Card(
        Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .size(200.dp, 100.dp)
            .fillMaxWidth(),
        elevation = elevation
    ) {}
}