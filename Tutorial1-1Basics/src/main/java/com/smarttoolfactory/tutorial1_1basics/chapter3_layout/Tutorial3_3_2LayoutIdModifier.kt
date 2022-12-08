package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Composable
fun Tutorial3_3Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "Modifier.layoutId")
        StyleableTutorialText(
            text = "1-) **Modifier.layoutId**  Creates a tag associated to a " +
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