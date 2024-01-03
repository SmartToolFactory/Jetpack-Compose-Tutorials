package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId


@Preview
@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        val constraints = if (minWidth < 600.dp) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button) {
            top.linkTo(parent.top, margin = margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }
    }
}

@Preview
@Composable
private fun ConstraintLayoutGuidlineSample() {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize().border(2.dp, Color.Red)
    ) {
        // Create guideline from the start of the parent at 10% the width of the Composable
        val startGuideline = createGuidelineFromStart(0.4f)
        // Create guideline from the end of the parent at 10% the width of the Composable
        val endGuideline = createGuidelineFromEnd(0.1f)
        //  Create guideline from 16 dp from the top of the parent
        val topGuideline = createGuidelineFromTop(16.dp)
        //  Create guideline from 16 dp from the bottom of the parent
        val bottomGuideline = createGuidelineFromBottom(16.dp)

        val button = createRef()
        val text = createRef()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier
                .constrainAs(button) {
                    start.linkTo(startGuideline)
                }
        ) {
            Text("Button")
        }

        Text(
            text = "Text",
            modifier = Modifier
                .background(Color.Yellow)
                .constrainAs(text) {
                    start.linkTo(button.end, 10.dp)
                }
        )
    }
}

@Preview
@Composable
fun ConstraintLayoutDemo() {
    ConstraintLayout(modifier = Modifier.size(200.dp)) {

        val (redBox, blueBox, yellowBox, text) = createRefs()

        Box(modifier = Modifier
            .size(50.dp)
            .background(Color.Red)
            .constrainAs(redBox) {})

        Box(modifier = Modifier
            .size(50.dp)
            .background(Color.Blue)
            .constrainAs(blueBox) {
                top.linkTo(redBox.bottom)
                start.linkTo(redBox.end)
            })

        Box(modifier = Modifier
            .size(50.dp)
            .background(Color.Yellow)
            .constrainAs(yellowBox) {
                bottom.linkTo(blueBox.bottom)
                start.linkTo(blueBox.end, 20.dp)
            }
        )

        Text("Hello World", modifier = Modifier
            .constrainAs(text) {
                top.linkTo(parent.top)
                start.linkTo(yellowBox.start)
            }
        )

    }
}

@Preview
@Composable
private fun ConstraintLayoutAnimationTest() {

    /*
        height = Dimension.value(10.dp)
        width = Dimension.ratio("4:1")    // The width will be 40dp
        width = Dimension.wrapContent
        height = Dimension.ratio("1:0.25")   // The height will be a fourth of the resulting wrapContent width
     */

    val buttonId = "Button"
    val textId = "Text"

    val constraintSet1 = remember {
        ConstraintSet {
            val button = createRefFor(buttonId)
            val text = createRefFor(textId)

            constrain(button) {
                top.linkTo(parent.top)
            }
            constrain(text) {
                top.linkTo(button.bottom)
            }
        }
    }

    val constraintSet2 = remember {
        ConstraintSet {
            val button = createRefFor(buttonId)
            val text = createRefFor(textId)

            constrain(button) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
            constrain(text) {
                // change width
                width = Dimension.value(100.dp)
                start.linkTo(button.end)
                top.linkTo(parent.top)
            }
        }
    }

    var show by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
                .background(Color.Yellow)
                .animateContentSize()
                .then(
                    if (show) Modifier.height(300.dp) else Modifier.height(150.dp)
                ),
            constraintSet = if (show) constraintSet2 else constraintSet1,
            animateChanges = true
        ) {

            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId(buttonId)
            ) {
                Text("Button")
            }

            Text(
                text = "Text", Modifier.layoutId(textId).background(Color.Red)
            )
        }

        Button(
            onClick = {
                show = show.not()
            }
        ) {
            Text("Show $show")
        }
    }
}