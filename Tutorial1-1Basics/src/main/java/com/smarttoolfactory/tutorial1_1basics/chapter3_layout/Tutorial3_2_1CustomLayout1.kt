package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.graphics.Point
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.random.Random

@Preview
@Composable
fun Tutorial3_2Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .border(3.dp, Color.Red)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "Custom Layouts 1")

        ChipStaggeredGrid(
            modifier = Modifier
//                .fillMaxWidth()
                .width(250.dp)
                .border(3.dp, Color.Green)
        ) {
            for (topic in topics) {
                Chip(
                    modifier = Modifier
                        .border(3.dp, Color.Cyan)
                        .padding(8.dp),
                    text = topic
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        ChipStaggeredGrid(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Color.Green)
        ) {
            for (topic in topics) {
                Chip(
                    modifier = Modifier
                        .border(3.dp, Color.Cyan)
                        .padding(8.dp),
                    text = topic
                )
            }
        }
    }
}

@Composable
private fun CustomColumn(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // 🔥 We need to set minWidth to zero to wrap only placeable width
        // If we use Default constrains each Composable gets measured with current minWidth
        // which is equal to maxWidth when we set fillMaxWidth/Size
        val looseConstraints = constraints.copy(minWidth = 0)

        //
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(looseConstraints)
        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0

        val totalHeight: Int = placeables.sumOf {
            it.height
        }


        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, totalHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}


@Composable
private fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {

        val size = Random.nextInt(10, 40)

        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

/**
 * This layout is a staggered grid which aligns the chip in next row based on maximumh
 * height of Chip on previous row
 */
@Composable
fun ChipStaggeredGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val constraintMaxWidth = constraints.maxWidth

        var maxRowWidth = 0

        var currentWidthOfRow = 0
        var totalHeightOfRows = 0

        var xPos: Int
        var yPos: Int

        val placeableMap = linkedMapOf<Int, Point>()
        val rowHeights = mutableListOf<Int>()

        var maxPlaceableHeight = 0
        var lastRowHeight = 0

        val placeables: List<Placeable> = measurables.mapIndexed { index, measurable ->

            // Measure each child
            val placeable =
                measurable.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity))

            val placeableWidth = placeable.width
            val placeableHeight = placeable.height

            // It's the same row as previous Composable if sum of current width of row and width of
            // this placeable is smaller then constraintMaxWidth(Parent width)
            val isSameRow = (currentWidthOfRow + placeableWidth <= constraintMaxWidth)

            if (isSameRow) {

                xPos = currentWidthOfRow
                yPos = totalHeightOfRows

                // Current width or row is now existing length and new item's length
                currentWidthOfRow += placeableWidth

                // Get the maximum item height in each row
                maxPlaceableHeight = maxPlaceableHeight.coerceAtLeast(placeableHeight)

                // After adding each item check if it's the longest row
                maxRowWidth = maxRowWidth.coerceAtLeast(currentWidthOfRow)

                lastRowHeight = maxPlaceableHeight


            } else {

                currentWidthOfRow = placeableWidth
                maxPlaceableHeight = maxPlaceableHeight.coerceAtLeast(placeableHeight)

                totalHeightOfRows += maxPlaceableHeight

                xPos = 0
                yPos = totalHeightOfRows

                rowHeights.add(maxPlaceableHeight)

                lastRowHeight = maxPlaceableHeight
                maxPlaceableHeight = placeableHeight
            }

            placeableMap[index] = Point(xPos, yPos)
            placeable
        }

        val finalHeight = (rowHeights.sumOf { it } + lastRowHeight)
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Constraints can be bigger or smaller than max width
        // Limit max width of layout in Constraints' min-max range
        if (constraints.hasFixedWidth && constraints.hasBoundedWidth) {
            maxRowWidth =
                maxRowWidth.coerceIn(
                    minimumValue = constraints.minWidth,
                    maximumValue = constraints.maxWidth
                )
        }

        // Set the size of the layout as big as it can
        layout(maxRowWidth, finalHeight) {
            // Place children in the parent layout
            placeables.forEachIndexed { index, placeable ->
                // Position item on the screen

                val point = placeableMap[index]
                point?.let {
                    placeable.placeRelative(x = point.x, y = point.y)
                }
            }
        }
    }
}

private val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)
