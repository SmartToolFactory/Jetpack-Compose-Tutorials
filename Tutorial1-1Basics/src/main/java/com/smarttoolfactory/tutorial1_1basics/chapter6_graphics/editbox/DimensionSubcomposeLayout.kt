package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.editbox

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize

/**
 * [SubcomposeLayout] layouts children like [Box] and returns [IntSize] of this Composable.
 * It doesn't layout mainContent because our [mainContent] and [dependentContent] are
 * combined as one Composable. We just measure exact size of initial dimensions
 * for a Composable to use it inside for setting size of Rectangles for initial bounds
 * for instance.
 */
@Composable
internal fun DimensionSubcomposeLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        // Get max width and height of main component
        val maxSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        layout(maxSize.width, maxSize.height) {

            // Get List<Measurable> from subcompose function then get List<Placeable> and place them
            subcompose(SlotsEnum.Dependent) {
                dependentContent(maxSize)
            }.forEach {
                it.measure(constraints).placeRelative(0, 0)
            }
        }
    }
}

enum class SlotsEnum { Main, Dependent }