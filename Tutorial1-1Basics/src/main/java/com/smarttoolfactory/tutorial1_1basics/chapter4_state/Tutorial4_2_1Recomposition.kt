package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

/*
    This tutorial uses this article as reference to show how scoping effect which section of
    any composable should be recomposed

    https://www.jetpackcompose.app/articles/donut-hole-skipping-in-jetpack-compose
 */

@Composable
fun Tutorial4_2_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(20.dp))
        StyleableTutorialText(
            text = "Recomposition happens in closest scope that reads any **State** change. In this" +
                    "example after composition on each recomposition only scopes that read a " +
                    "value are recomposed skipping recomposition of Composables between.",
            bullets = false
        )
        Spacer(modifier = Modifier.height(20.dp))
        MyComponent()
    }
}

@Composable
fun MyComponent() {
    var counter by remember { mutableStateOf(0) }

    LogCompositions("🔥 MyComposable function")

    CustomButton(onClick = { counter++ }) {
        LogCompositions("🍉 CustomButton scope")
        CustomTextWrapper(modifier = Modifier, text = "Counter: $counter")
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    LogCompositions("🌽 CustomButton function")
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(4.dp, getRandomColor())
            .padding(16.dp)
    ) {
        LogCompositions("🍏 Button function")
        content()
    }
}

@Composable
fun CustomTextWrapper(
    text: String,
    modifier: Modifier = Modifier,
) {
    LogCompositions("🎃 CustomTextWrapper function")
    AnotherComposable()
    CustomText(text, modifier)
}

@Composable
private fun AnotherComposable(){
    LogCompositions("🍋 AnotherComposable function")
}

@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
) {
    LogCompositions("🍎 CustomText function")

    Text(
        text = text,
        modifier = modifier
            .border(3.dp, getRandomColor())
            .padding(32.dp),
        fontSize = 20.sp
    )
}

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(msg: String) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    println("$msg ${ref.value}")
}