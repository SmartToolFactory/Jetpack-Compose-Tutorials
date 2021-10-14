package com.smarttoolfactory.tutorial1_1basics.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.max

@Composable
fun FullWidthRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        content()
    }
}


@Composable
fun FullWidthColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier.fillMaxWidth()) {
        content()
    }
}

//@Composable
//fun StaggeredGridLayout(
//    modifier: Modifier = Modifier,
//    rows: Int = 3,
//    content: @Composable () -> Unit
//) {
//
//    Layout(modifier = modifier, content = content) { measurables, constraints ->
//
//        val rowWidths = IntArray(rows) { 0 }
//        val rowHeights = IntArray(rows) { 0 }
//
//        val placeables = measurables.mapIndexed { index, measurable ->
//            val placeable = measurable.measure(constraints)
//
//            val row = index % rows
//            rowWidths[row] += placeable.width
//            rowHeights[row] = max(rowHeights[row], placeable.height)
//            placeable
//        }
//
//        // Grid's width is the widest row
//        val width = rowWidths.maxOrNull()
//            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth
//
//        // Grid's height is the sum of the tallest element of each row
//        // coerced to the height constraints
//        val height = rowHeights.sumBy { it }
//            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))
//
//        // Y of each row, based on the height accumulation of previous rows
//        val rowY = IntArray(rows) { 0 }
//        for (i in 1 until rows) {
//            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
//        }
//
//        // Set the size of the parent layout
//        layout(width, height) {
//            // x co-ord we have placed up to, per row
//            val rowX = IntArray(rows) { 0 }
//
//            placeables.forEachIndexed { index, placeable ->
//                val row = index % rows
//                placeable.placeRelative(
//                    x = rowX[row],
//                    y = rowY[row]
//                )
//                rowX[row] += placeable.width
//            }
//        }
//    }
//}