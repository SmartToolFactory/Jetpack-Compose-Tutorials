package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.DebugFlags
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.smarttoolfactory.tutorial1_1basics.R

@Preview(showBackground = true)
@Composable
fun MotionLayoutSample1() {

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
                start.linkTo(button.end)
                top.linkTo(parent.top)
            }
        }
    }

    var show by remember {
        mutableStateOf(false)
    }

    val progress by animateFloatAsState(if (show) 1f else 0f, label = "")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {


        MotionLayout(
            modifier = Modifier.fillMaxWidth()
                .background(Color.Yellow)
                .animateContentSize()
                .then(
                    if (show) Modifier.height(300.dp) else Modifier.height(150.dp)
                ),
            start = constraintSet1,
            end = constraintSet2,
            progress = progress,
            debugFlags = DebugFlags.All
        ) {


            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId(buttonId)
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId(textId))
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

// on below line we are creating
// a motion layout button method.

@Preview(showBackground = true)
@Composable
private fun MotionLayoutButtonTest() {
    // on below line we are creating a box
    Box(
        // in this box we are specifying a modifier
        // and specifying a max size
        modifier = Modifier
            .fillMaxSize(),

        // on below line we are specifying center alignment
        contentAlignment = Alignment.Center,
    ) {
        // on below line we are calling
        // motion layout button method.
        MotionLayoutButton()
    }
}

@Composable
fun MotionLayoutButton() {
    // on below line we are specifying animate button method.
    var animateButton by remember { mutableStateOf(false) }
    // on below line we are specifying button animation progress
    val buttonAnimationProgress by animateFloatAsState(

        // specifying target value on below line.
        targetValue = if (animateButton) 1f else 0f,

        // on below line we are specifying
        // animation specific duration's 1 sec
        animationSpec = tween(1000),
        label = ""
    )

    // on below line we are creating a motion layout.
    MotionLayout(
        // in motion layout we are specifying 2 constraint
        // set for two different positions of buttons.
        // in first constraint set we are specifying width,
        // height start, end and top position of buttons.
        ConstraintSet(
            """ {
                // on below line we are specifying width,height and margin 
                // from start, top and end for button1 
                button1: { 
                  width: "spread",
                  height: 120,
                  start: ['parent', 'start', 16],
                  end: ['parent', 'end', 16],
                  top: ['parent', 'top', 0]
                },
                // on below line we are specifying width,height 
                // and margin from start, top and end for button2
                button2: { 
                  width: "spread",
                  height: 120,
                  start: ['parent', 'start', 16],
                  end: ['parent', 'end', 16],
                  top: ['button1', 'bottom', 16]
                }
            } """
        ),

        // in second constraint set we are specifying width,
        // height start, end and top position of buttons.
        ConstraintSet(
            """ {
                // on below line we are specifying width,height and margin
                // from start, top and end for button1
                button1: { 
                  width: 150,
                  height: 120,
                  start: ['parent', 'start', 30],
                  end: ['button2', 'start', 10]
                },
                // on below line we are specifying width,height
                // and margin from start, top and end for button2
                button2: { 
                  width: 150,
                  height: 120,
                  start: ['button1', 'end', 10],
                  end: ['parent', 'end', 30]
                }
            } """
        ),
        // on below line we are specifying
        // progress for button animation
        progress = buttonAnimationProgress,
        // on below line we are specifying modifier
        // for filling max width and content height.
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // on below line we are creating  a button.
        Button(
            // on below line we are adding on click.
            onClick = {
                // inside on click we are animating button
                // by simply changing animateButton variable
                animateButton = !animateButton
            },
            // on below line we are
            // specifying id for our button 1
            modifier = Modifier.layoutId("button1")
        ) {
            // on below line we are adding content
            // inside our button in the form of column.
            Column(
                // in this column we are specifying a
                // modifier with padding from all sides.
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                // on below line we are specifying vertical
                // and horizontal arrangement for our column
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // on the below line we are specifying an image inside our column
                Image(
                    // on below line we are specifying
                    // the drawable image for our image.
                    painter = painterResource(id = R.drawable.avatar_1_raster),

                    // on below line we are specifying
                    // content description for our image
                    contentDescription = "Python",

                    // on below line we are setting
                    // height and width for our image.
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
                // on below line we are adding spacer/
                Spacer(modifier = Modifier.height(5.dp))

                // below spacer we are adding a
                // simple text for displaying a text
                Text(
                    text = "Python",
                    color = Color.White,
                    fontSize = TextUnit(value = 18F, type = TextUnitType.Sp)
                )
            }
        }

        // on the below line we are creating a button.
        Button(
            onClick = {
                // inside on click we are animating button
                // by simply changing animateButton variable
                animateButton = !animateButton
            },
            // on below line we are specifying id for our button 2
            modifier = Modifier.layoutId("button2")
        ) {
            Column(
                // in this column we are specifying
                // a modifier with padding from all sides.
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                // on below line we are specifying vertical
                // and horizontal arrangement for our column
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // on below line we are specifying image inside our column
                Image(
                    // on below line we are specifying
                    // the drawable image for our image.
                    painter = painterResource(id = R.drawable.avatar_2_raster),

                    // on below line we are specifying
                    // content description for our image
                    contentDescription = "Javascript",

                    // on below line we are setting
                    // height and width for our image.
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
                // on below line we are adding spacer/
                Spacer(modifier = Modifier.height(5.dp))

                // below spacer we are adding a
                // simple text for displaying a text
                Text(
                    text = "JavaScript",
                    color = Color.White,
                    fontSize = TextUnit(value = 18F, type = TextUnitType.Sp)
                )

            }
        }
    }
}


@Preview(group = "motion8", showBackground = true)
@Composable
fun AttributesRotationXY() {

    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000), label = ""
    )

    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene(
                """{
                ConstraintSets: {   // all ConstraintSets
                  start: {          // constraint set id = "start"
                    a: {            // Constraint for widget id='a'
                      width: 40,
                      height: 40,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {         // constraint set id = "start"
                    a: {
                      width: 40,
                      height: 40,
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {            // All Transitions in here 
                  default: {              // transition named 'default'
                    from: 'start',        // go from Transition "start"
                    to: 'end',            // go to Transition "end"
                    pathMotionArc: 'startHorizontal',  // move in arc 
                    KeyFrames: {          // All keyframes go here
                      KeyAttributes: [    // keyAttributes key frames go here
                        {
                          target: ['a'],              // This keyAttributes group target id "a"
                          frames: [25,50,75],         // 3 points on progress 25% , 50% and 75%
                          rotationX: [0, 45, 60],     // the rotationX angles are a spline from 0,0,45,60,0
                          rotationY: [60, 45, 0],     // the rotationX angles are a spline from 0,60,45,0,0
                        }
                      ]
                    }
                  }
                }
            }"""
            ),
            debugFlags = DebugFlags.All,
            progress = progress
        ) {
            Box(
                modifier = Modifier
                    .layoutId("a")
                    .background(Color.Red)
            )
        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}