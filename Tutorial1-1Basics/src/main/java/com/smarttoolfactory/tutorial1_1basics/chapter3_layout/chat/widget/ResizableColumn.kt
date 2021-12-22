package com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget

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

        val columnSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }

//        println(
//            "✏️ SubcomposeColumn() columnSize " +
//                    "width: ${columnSize.width}, height: ${columnSize.height}, " +
//                    "placeable size: ${placeables.size} CONSTRAINTS: $constraints"
//        )

        // Remeasure every element using width of longest item as minWidth of Constraint
        if (!placeables.isNullOrEmpty() && placeables.size > 1) {

//            println(
//                "✏️✏️ SubcomposeColumn() REMEASURE MAIN COMPONENT " +
//                        "columnSize columnSize"
//            )

            placeables = subcompose(recompositionIndex, content).map { measurable: Measurable ->
                measurable.measure(Constraints(columnSize.width, constraints.maxWidth))
            }
        }

        layout(columnSize.width, columnSize.height) {

//            println(
//                "✏️✏️✏️️ SubcomposeColumn() layout()-> " +
//                        "columnSize width: ${columnSize.width}, height: ${columnSize.height}, " +
//                        "CONSTRAINTS: $constraints"
//            )

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

        var columnSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }

        val dependentMeasurables: List<Measurable> = subcompose(recompositionIndex++) {
            // 🔥🔥 Send columnSize of mainComponent to
            // dependent composable in case it might be used
            dependentContent(columnSize)
        }

        val dependentPlaceables: List<Placeable> = dependentMeasurables
            .map { measurable: Measurable ->
                // dependent components width should be at least width of main one
                measurable.measure(Constraints(columnSize.width, constraints.maxWidth))
            }

        // Get maximum width of dependent composable
        val maxWidth = if (!dependentPlaceables.isNullOrEmpty()) {
            dependentPlaceables.maxOf { it.width }
        } else columnSize.width

//        println(
//            "✏️ SubcomposeColumn() columnSize " +
//                    "width: ${columnSize.width}, height: ${columnSize.height}, maxWidth: $maxWidth, " +
//                    "CONSTRAINTS: $constraints"
//        )

        // If width of dependent composable is longer than main one, remeasure main one
        // with dependent composable's width using it as minWidth of Constraint
        if (!mainPlaceables.isNullOrEmpty() && maxWidth > columnSize.width) {

//            println(
//                "✏️✏️ SubcomposeColumn() REMEASURE MAIN COMPONENT " +
//                        "maxWidth: $maxWidth, columnSize width: ${columnSize.width}"
//            )
            mainPlaceables = subcompose(recompositionIndex, mainContent).map {
                it.measure(Constraints(maxWidth, constraints.maxWidth))
            }
        }

        // Our final columnSize is longest width and total height of main and dependent composables
        if (!dependentPlaceables.isNullOrEmpty()) {
            columnSize = IntSize(
                columnSize.width.coerceAtLeast(maxWidth),
                columnSize.height + dependentPlaceables.sumOf { it.height }
            )
        }

        layout(columnSize.width, columnSize.height) {

//            println(
//                "✏️✏️✏️️ SubcomposeColumn() layout()-> " +
//                        "columnSize width: ${columnSize.width}, height: ${columnSize.height}, " +
//                        "maxWidth: $maxWidth"
//            )

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