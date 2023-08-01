package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
@Composable
fun Tutorial3_2Screen9() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        /**
         * Modifier.wrapContent allow the content to measure at its desired size without
         * regard for the incoming measurement
         * [minimum width][Constraints.minWidth]
         * or [minimum height][Constraints.minHeight] constraints,
         * and, if [unbounded] is true, also without regard for the incoming maximum constraints.
         *
         *  If the content's measured size is smaller than the minimum size constraint, [align] it
         * within that minimum sized space.
         *
         * If the content's measured size is larger than the maximum
         * size constraint (only possible when [unbounded] is true),
         * [align] within the maximum space.
         *
         */
        StyleableTutorialText(
            text = "1-) **Modifier.wrapContentSize** can be used to override minimum constraints" +
                    " coming from parent. In this example **MinimumConstrainedLayout** forces " +
                    "min width and height 500px. In second example **Modifier.wrapContentSize** " +
                    "forces measurement with size comes from child Composable."
        )

        WrapContentSizeSample()

        StyleableTutorialText(
            text = "2-) Surface forces minimum Constraints to direct descendant. With " +
                    "**Modifier.wrapContentSize** minimum constraints can be used"
        )

        WrapWidthInsideSurfaceSample()
        StyleableTutorialText(
            text = "3-) **Modifier.wrapContentSize(unBounded = true)** forces maximum constraints " +
                    "from child Composable. In second example **Image** is measured with its own " +
                    "max width constraint."
        )
        // 🔥 Unbounded content doesn't change position or dimensions of parent. This can
        // lead to wrong placement when other sibling composables are to be considered
        // Parent Composable is placed based on its constraints not Unbounded content constraints
        UnboundedWrapContentSample()
        StyleableTutorialText(
            text = "4-) **Modifier.wrapContentSize(unBounded = true)** can be used to draw images " +
                    "to fit inside a content that is smaller than image bounds while not scaling" +
                    " image down to fit content"
        )

        // 🔥 Unbounded content doesn't change position or dimensions of parent. This can
        // lead to wrong placement when other sibling composables are to be considered
        // Parent Composable is placed based on its constraints not Unbounded content constraints
        UnBoundedWrapContentImageSample()

    }
}

@Composable
private fun WrapContentSizeSample() {

    TutorialText2(text = "No wrap Modifier")

    MinimumConstrainedLayout(
        Modifier.border(2.dp, Color.Green)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Red)
        ) {
            Box(modifier = Modifier.size(50.dp))
        }
    }

    Spacer(modifier = Modifier.width(20.dp))

    TutorialText2(text = "Modifier.wrapContentSize()")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MinimumConstrainedLayout(
            Modifier.border(2.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .background(Color.Red)
            ) {
                Box(modifier = Modifier.size(50.dp))
            }
        }

        MinimumConstrainedLayout(
            Modifier.border(2.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier

                    .wrapContentSize(align = Alignment.BottomStart)
                    .background(Color.Red)
            ) {
                Box(modifier = Modifier.size(50.dp))
            }
        }

        MinimumConstrainedLayout(
            Modifier.border(2.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier

                    .wrapContentSize(align = Alignment.BottomEnd)
                    .background(Color.Red)
            ) {
                Box(modifier = Modifier.size(50.dp))
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WrapWidthInsideSurfaceSample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Surface(modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Yellow), onClick = {}) {
            Column(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red, RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Green, RoundedCornerShape(6.dp))
                )

            }
        }
        Surface(modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Yellow), onClick = {}) {
            Column(
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .background(Color.Red, RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Green, RoundedCornerShape(6.dp))
                )

            }
        }


        Surface(modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Yellow), onClick = {}) {
            Column(
                modifier = Modifier
                    .wrapContentHeight(Alignment.Top)
                    .background(Color.Red, RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Green, RoundedCornerShape(6.dp))
                )

            }
        }
    }
}

@Composable
private fun UnboundedWrapContentSample() {
    TutorialText2(text = "Modifier.wrapContentSize(unbounded = false)")
    Box(
        modifier = Modifier
            .size(80.dp)
            .border(2.dp, Color.Green)
    ) {
        Column(
            modifier = Modifier
                .border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                .wrapContentSize(unbounded = false)
                .background(Color.Cyan)
                .size(150.dp),
        ) {
            Text(
                text = "Hello world text",
                modifier = Modifier.background(Pink400),
                color = Color.White
            )        }
    }
    TutorialText2(text = "Modifier\n" +
            ".wrapContentSize(unbounded = true)\n" +
            ".size(150.dp)"
    )
    Box(
        modifier = Modifier
            .size(80.dp)
            .border(2.dp, Color.Green)
    ) {
        Column(
            modifier = Modifier
                .border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                .wrapContentSize(unbounded = true)
                .background(Color.Cyan)
                .size(150.dp),
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    modifier = Modifier.background(Pink400),
                    color = Color.White
                )
            }
        }
    }

    TutorialText2(text = "Modifier\n" +
            ".size(150.dp)\n" +
            ".wrapContentSize(unbounded = true)")
    Box(
        modifier = Modifier
            .size(80.dp)
            .border(2.dp, Color.Green)
    ) {
        Column(
            modifier = Modifier
                .border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                .size(150.dp)
                .background(Color.Cyan)
                .wrapContentSize(unbounded = true)
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    modifier = Modifier.background(Pink400),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun UnBoundedWrapContentImageSample() {

    TutorialText2(text = "Modifier.wrapContentSize(unbounded = false)")
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Green)
    ) {
        Image(
            modifier = Modifier
                .wrapContentSize(unbounded = false)
                .size(150.dp),
            painter = painterResource(id = R.drawable.landscape6),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
    }
    TutorialText2(text = "Modifier\n" +
            ".wrapContentSize(unbounded = true)\n" +
            ".size(250.dp)")
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Green)
    ) {
        Image(
            modifier = Modifier
                .border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                .wrapContentSize(unbounded = true)
                .border(2.dp, Color.Cyan)
                .size(250.dp),
            painter = painterResource(id = R.drawable.landscape6),
            contentDescription = null
        )
    }

    TutorialText2(text = "Modifier\n" +
            ".size(250.dp)\n" +
            ".wrapContentSize(unbounded = true)")
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Green)
    ) {
        Image(
            modifier = Modifier
                .size(250.dp)
                .border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                .wrapContentSize(unbounded = true)
                .border(2.dp, Color.Cyan),
            painter = painterResource(id = R.drawable.landscape6),
            contentDescription = null
        )
    }
}

@Composable
private fun MinimumConstrainedLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val measurePolicy = MeasurePolicy { measurables, constraints ->
        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = 300,
                    minHeight = 300
                )
            )
        }

        val hasBoundedWidth = constraints.hasBoundedWidth
        val hasFixedWidth = constraints.hasFixedWidth

        val width = if (hasBoundedWidth && hasFixedWidth) constraints.maxWidth
        else placeables.maxOf { it.width }.coerceIn(constraints.minWidth, constraints.maxWidth)

        val height = placeables.sumOf { it.height }

        var yPos = 0

        layout(width, height) {
            placeables.forEach {
                it.placeRelative(0, yPos)
                yPos += it.height
            }
        }
    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy
    )
}
