package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.ReceivedQuoteColor
import com.smarttoolfactory.tutorial1_1basics.ui.SentMessageColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview(showBackground = true)
@Composable
fun Tutorial3_2Screen10() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TutorialHeader(text = "Constraints and Layout")

        val density = LocalDensity.current
        val containerWidth = with(density) {
            800f.toDp()
        }

        val arrowWidth = with(density) {
            50f.toDp()
        }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .width(containerWidth)
                .fillMaxHeight()
                .background(Color(0xffFBE9E7))

        ) {

            var message by remember { mutableStateOf("Type to monitor overflow") }
            StyleableTutorialText(
                text = "In this example **Constraints.offset**, " +
                        "**Constraints.constrainWidth/Height**, and **Modifier.layout** is used " +
                        "to create bubbles with arrows in different positions. " +
                        "Comment, offset, constrain to see effects with or without these functions",
                bullets = false
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = message,
                label = { Text("Message") },
                placeholder = { Text("Set text to change main width") },
                onValueChange = { newValue: String ->
                    message = newValue
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .drawBubble(
                        arrowWidth = 16.dp,
                        arrowHeight = 16.dp,
                        arrowOffset = 5.dp,
                        arrowDirection = ArrowDirection.Left,
                        elevation = 2.dp,
                        color = SentMessageColor
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .drawBubble(
                        arrowWidth = 16.dp,
                        arrowHeight = 16.dp,
                        arrowOffset = 5.dp,
                        arrowDirection = ArrowDirection.Right,
                        elevation = 2.dp,
                        color = ReceivedQuoteColor
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .drawBubble(
                        arrowWidth = 24.dp,
                        arrowHeight = 16.dp,
                        arrowOffset = 10.dp,
                        arrowDirection = ArrowDirection.Top,
                        elevation = 2.dp,
                        color = Color.Red
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .drawBubble(
                        arrowWidth = 24.dp,
                        arrowHeight = 16.dp,
                        arrowOffset = 10.dp,
                        arrowDirection = ArrowDirection.Bottom,
                        elevation = 2.dp,
                        color = Color.Green
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .drawBubble(
                        arrowWidth = arrowWidth,
                        arrowHeight = 16.dp,
                        arrowOffset = 5.dp,
                        arrowDirection = ArrowDirection.Left,
                        elevation = 2.dp,
                        color = SentMessageColor
                    )
            ) {
                Text(
                    text = "Constraints: $constraints",
                    fontSize = 16.sp
                )
            }
        }
    }
}
