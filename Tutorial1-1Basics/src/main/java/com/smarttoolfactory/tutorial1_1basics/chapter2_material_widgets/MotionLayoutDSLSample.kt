package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.OnSwipe
import androidx.constraintlayout.compose.SwipeDirection
import androidx.constraintlayout.compose.SwipeMode
import androidx.constraintlayout.compose.SwipeSide
import androidx.constraintlayout.compose.SwipeTouchUp

@Preview(showBackground = true)
@Composable
private fun Test() {
    MotionLayout(
        MotionScene { // this: MotionSceneScope
            val textRef = createRefFor("text")
            defaultTransition(
                from = constraintSet { // this: ConstraintSetScope
                    constrain(textRef) { // this: ConstrainScope
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                },
                to = constraintSet { // this: ConstraintSetScope
                    constrain(textRef) { // this: ConstrainScope
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                }
            ) { // this: TransitionScope
                onSwipe = OnSwipe(
                    anchor = textRef,
                    side = SwipeSide.End,
                    direction = SwipeDirection.End,
                    mode = SwipeMode.Spring,
                    onTouchUp = SwipeTouchUp.AutoComplete
                )
            }
        },
        progress = 0f,
        Modifier.fillMaxSize()
    ) {
        Text("Hello, World", Modifier.layoutId("text"))
    }
}