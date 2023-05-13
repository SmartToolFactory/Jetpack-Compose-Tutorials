package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Composable
fun Tutorial3_3Screen2() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "Modifier.layoutId")
        StyleableTutorialText(
            text = "1-) **Modifier.layoutId**  Creates a tag associated with a " +
                    "composable " +
                    "A measurable of a composable with MeasureLayout " +
                    "it can be measured by getting " +
                    "it via **measurable.layoutId** and the dimensions of **Placeable** can be " +
                    "used to constrain another measurable."
        )

        MyLayout(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Cyan)
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .layoutId("image"),
                painter = painterResource(id = R.drawable.landscape1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Text(
                modifier = Modifier
                    .layoutId("text")
                    .border(2.dp, Color.Red),
                text = "Hello world some very long text that will be scaled"
            )
        }

        MyLayout(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Cyan)
        ) {

            Text(
                modifier = Modifier
                    .layoutId("text")
                    .border(2.dp, Color.Red),
                text = "Hello world some very long text that will be scaled"
            )

            Image(
                modifier = Modifier
                    .size(100.dp)
                    .layoutId("image"),
                painter = painterResource(id = R.drawable.landscape1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

        }

        StyleableTutorialText(
            text = "2-) A Composable with **Modifier.layoutId** can have properties that other" +
                    " child Composables don't have by selecting it in layout phase and placing it " +
                    "with **Placeable.placeWithLayer**."
        )

        LayoutIdWithPlaceWithLayerSample()
    }
}

@Composable
private fun LayoutIdWithPlaceWithLayerSample() {

    PlaceWithLayerLayout(
        modifier = Modifier.drawChecker(),
        alpha = .4f
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .size(80.dp)
                .padding(10.dp)
        )

        Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .size(80.dp)
                .padding(10.dp)
        )

        Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .layoutId("full_alpha")
                .background(Color.Red, CircleShape)
                .size(80.dp)
                .padding(10.dp)
        )

        Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .size(80.dp)
                .padding(10.dp)
        )
    }
}


@Composable
private fun MyLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val imageMeasurable: Measurable = measurables.find { it.layoutId == "image" }!!
        val textMeasurable: Measurable = measurables.find { it.layoutId == "text" }!!

        // If min width is not 0 it's measured with Modifier.fillMaxWidth's constraint
        // which is minWidth = 1080, maxWidth = 1080
        val imagePlaceable = imageMeasurable.measure(constraints.copy(minWidth = 0, minHeight = 0))

        // Limit text width to image width by setting min and max width at image width
        val textPlaceable = textMeasurable.measure(
            constraints.copy(
                minWidth = imagePlaceable.width,
                maxWidth = imagePlaceable.width
            )
        )

        val width = imagePlaceable.width
        val imagePlaceableHeight = imagePlaceable.height
        val height = imagePlaceableHeight + textPlaceable.height
        // Width and height of parent composable depends on requirements
        layout(width, height) {
            imagePlaceable.placeRelative(0, 0)
            textPlaceable.placeRelative(0, imagePlaceableHeight)
        }
    }
}

@Composable
fun PlaceWithLayerLayout(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    content: @Composable () -> Unit
) {
    val measurePolicy = MeasurePolicy { measurables, constraints ->

        val fullAlphaIndex = measurables.indexOfFirst {
            it.layoutId == "full_alpha"
        }
        val placeablesWidth = measurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = Constraints.Infinity,
                    minHeight = 0,
                    maxHeight = Constraints.Infinity
                )
            )
        }

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth

        val hasBoundedHeight = constraints.hasBoundedHeight
        val hasFixedHeight = constraints.hasFixedHeight

        val width =
            if (hasBoundedWidth && hasFixedWidth) constraints.maxWidth
            else placeablesWidth.sumOf { it.width }.coerceAtMost(constraints.maxWidth)

        val height =
            if (hasBoundedHeight && hasFixedHeight) constraints.maxHeight
            else placeablesWidth.maxOf { it.height }.coerceAtMost(constraints.maxHeight)


        var posX = 0

        layout(width, height) {
            placeablesWidth.forEachIndexed { index, placeable ->
                placeable.placeRelativeWithLayer(posX, 0) {
                    if (index == fullAlphaIndex) {
                        this.alpha = 1f
                    } else {
                        this.alpha = alpha
                    }
                }

                posX += placeable.width
            }
        }

    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy
    )
}

fun Modifier.drawChecker() = this.then(
    drawBehind {
        val width = this.size.width
        val height = this.size.height

        val checkerWidth = 10.dp.toPx()
        val checkerHeight = 10.dp.toPx()

        val horizontalSteps = (width / checkerWidth).toInt()
        val verticalSteps = (height / checkerHeight).toInt()

        for (y in 0..verticalSteps) {
            for (x in 0..horizontalSteps) {
                val isGrayTile = ((x + y) % 2 == 1)
                drawRect(
                    color = if (isGrayTile) Color.LightGray else Color.White,
                    topLeft = Offset(x * checkerWidth, y * checkerHeight),
                    size = Size(checkerWidth, checkerHeight)
                )
            }
        }
    }
)
