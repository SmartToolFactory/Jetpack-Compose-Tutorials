package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

/**
 * Column that resizes its children to width of the longest child
 */
@Composable
fun SubcomposeColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        var recompositionIndex = 0

        var placeables: List<Placeable> = subcompose(recompositionIndex++, content).map {
            it.measure(constraints)
        }

        val maxSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }

        // Remeasure every element using width of longest item as minWidth of Constraint
        if (!placeables.isNullOrEmpty() && placeables.size > 1) {
            placeables = subcompose(recompositionIndex, content).map { measurable: Measurable ->
                measurable.measure(Constraints(maxSize.width, constraints.maxWidth))
            }
        }

        layout(maxSize.width, maxSize.height) {
            var yPos = 0
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, yPos)
                yPos += placeable.height
            }

        }
    }
}

/**
 * Column that resizes its children to width of the longest child
 */
@Composable
fun SubcomposeColumn(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit = {},
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        var recompositionIndex = 0

        var mainPlaceables: List<Placeable> = subcompose(recompositionIndex++, mainContent).map {
            it.measure(constraints)
        }

        var maxSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }

        val dependentMeasurables: List<Measurable> = subcompose(recompositionIndex++) {
            // 🔥🔥 Send maxSize of mainComponent to
            // dependent composable in case it might be used
            dependentContent(maxSize)
        }

        val dependentPlaceables: List<Placeable> = dependentMeasurables
            .map { measurable: Measurable ->
                // dependent components width should be at least width of main one
                measurable.measure(Constraints(maxSize.width, constraints.maxWidth))
            }

        // Get maximum width of dependent composable
        val maxWidth = if (!dependentPlaceables.isNullOrEmpty()) {
            dependentPlaceables.maxOf { it.width }
        } else maxSize.width

        println("✏️ SubcomposeColumn() maxSize " +
                "width: ${maxSize.width}, height: ${maxSize.height}, maxWidth: $maxWidth")

        // If width of dependent composable is longer than main one, remeasure main one
        // with dependent composable's width using it as minWidth of Constraint
        if (!mainPlaceables.isNullOrEmpty() && maxWidth > maxSize.width) {

            println(
                "✏️✏️ SubcomposeColumn() REMEASURE MAIN COMPONENT " +
                        "maxWidth: $maxWidth, maxSize width: ${maxSize.width}"
            )
            mainPlaceables = subcompose(recompositionIndex, mainContent).map {
                it.measure(Constraints(maxWidth, constraints.maxWidth))
            }
        }

        // Our final maxSize is longest width and total height of main and dependent composables
        if (!dependentPlaceables.isNullOrEmpty()) {
            maxSize = IntSize(
                maxSize.width.coerceAtLeast(maxWidth),
                maxSize.height + dependentPlaceables.sumOf { it.height }
            )
        }

        layout(maxSize.width, maxSize.height) {

            println(
                "✏️✏️✏️️ SubcomposeColumn() layout()-> " +
                        "maxSize width: ${maxSize.width}, height: ${maxSize.height}, " +
                        "maxWidth: $maxWidth"
            )

            var posY = 0

            // Place layouts
            if (!mainPlaceables.isNullOrEmpty()) {
                mainPlaceables.forEach {
                    it.placeRelative(0, posY)
                    posY += it.height
                }

            }
            if (!dependentPlaceables.isNullOrEmpty()) {
                dependentPlaceables.forEach {
                    it.placeRelative(0, posY)
                    posY += it.height
                }
            }
        }
    }
}