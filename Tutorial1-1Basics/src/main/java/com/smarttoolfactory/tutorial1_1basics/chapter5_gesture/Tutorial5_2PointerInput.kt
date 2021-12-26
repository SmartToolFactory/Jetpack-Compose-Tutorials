package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.blue400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.green400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.orange400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StyleableTutorialText(
            text = "1-) Detects tap, double-tap, and long press gestures and calls onTap, " +
                    "onDoubleTap, and onLongPress, respectively, when detected. "
        )
        DetectTapGesturesExample()
    }

}

private val modifier = Modifier
    .padding(8.dp)
    .fillMaxWidth()
    .height(40.dp)
    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = true)

@Composable
private fun DetectTapGesturesExample() {

    var gestureText by remember { mutableStateOf("Idle") }
    var gestureColor by remember { mutableStateOf(Color(0xffBDBDBD)) }

    modifier
        .background(gestureColor, shape = RoundedCornerShape(16.dp))
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    gestureText = "onPress"
                    gestureColor = orange400
                },
                onTap = {
                    gestureText = "onTap offset: $it"
                    gestureColor = pink400
                },
                onDoubleTap = {
                    gestureText = "onDoubleTap offset: $it"
                    gestureColor = blue400

                },
                onLongPress = {
                    gestureText = "onLongPress offset: $it"
                    gestureColor = green400
                }
            )
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = gestureText,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}