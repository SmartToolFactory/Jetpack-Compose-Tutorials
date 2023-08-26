@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch


// Creates a custom modifier to animate the local position of the layout within the
// given LookaheadScope, whenever the relative position changes.
@SuppressLint("UnrememberedMutableState")
fun Modifier.animatePlacementInScope(lookaheadScope: LookaheadScope) = composed {
    // Creates an offset animation
    var offsetAnimation: Animatable<IntOffset, AnimationVector2D>? by mutableStateOf(
        null
    )
    var targetOffset: IntOffset? by mutableStateOf(null)

    this.intermediateLayout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            // Converts coordinates of the current layout to LookaheadCoordinates
            val coordinates = coordinates
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                val target = with(lookaheadScope) {
                    lookaheadScopeCoordinates
                        .localLookaheadPositionOf(coordinates)
                        .round().also { targetOffset = it }
                }

                // Uses the target offset to start an offset animation
                if (target != offsetAnimation?.targetValue) {
                    offsetAnimation?.run {
                        launch { animateTo(target) }
                    } ?: Animatable(target, IntOffset.VectorConverter).let {
                        offsetAnimation = it
                    }
                }
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset =
                    lookaheadScopeCoordinates.localPositionOf(
                        coordinates,
                        Offset.Zero
                    ).round()
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val (x, y) = requireNotNull(offsetAnimation).run { value - placementOffset }
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

val colors = listOf(
    Color(0xffff6f69), Color(0xffffcc5c), Color(0xff264653), Color(0xff2a9d84)
)



@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun Test() {

    var isInColumn by remember { mutableStateOf(true) }
    LookaheadScope {
        // Creates movable content containing 4 boxes. They will be put either in a [Row] or in a
        // [Column] depending on the state
        val items = remember {
            movableContentOf {
                colors.forEach { color ->
                    Box(
                        Modifier
                            .padding(15.dp)
                            .size(100.dp, 80.dp)
                            .animatePlacementInScope(this)
                            .background(color, RoundedCornerShape(20))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isInColumn = !isInColumn }
        ) {
            // As the items get moved between Column and Row, their positions in LookaheadScope
            // will change. The `animatePlacementInScope` modifier created above will
            // observe that final position change via `localLookaheadPositionOf`, and create
            // a position animation.
            if (isInColumn) {
                Column(Modifier.fillMaxSize()) {
                    items()
                }
            } else {
                Row { items() }
            }
        }
    }
}