package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor


@Preview
@Composable
fun Tutorial4_2_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        StyleableTutorialText(
            text = "Recomposition happens in closest scope that reads any **State** change.\n" +
                    "A scope is non-inline function that returns Unit.",
            bullets = false
        )
        ScopedRecompositionSample()

        StyleableTutorialText(
            text = "Recomposition happens in closest scope that reads any **State** change. In this" +
                    " example after composition on each recomposition only scopes that read a " +
                    "value are recomposed skipping recomposition of Composables between.\n" +
                    "Check logs to see which Composables get recomposed and how many times they do.",
            bullets = false
        )
        MyComponent()
    }
}

/*
    This sample uses this article as reference to show how scoping effect which section of
    any composable should be recomposed

    https://dev.to/zachklipp/scoped-recomposition-jetpack-compose-what-happens-when-state-changes-l78
 */
@Preview
@Composable
private fun ScopedRecompositionSample() {
    var counter by remember {
        mutableStateOf(0)
    }

    var counter2 by remember {
        mutableStateOf(0)
    }

    Column {
        println("Root")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }
        ) {
            println("🍏 Button Scope")
            Text(text = "Counter: $counter")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter2++ }
        ) {
            println("🍎 Button Scope2")
            Text(text = "Counter2: $counter2")
        }

        // 🔥🔥 Reading this value triggers recomposition
        // for whole Column and both Button scopes
        Text(text = "Counter: $counter")
    }
}

@Preview
@Composable
private fun ComposeScopeSample() {
    var counter by remember {
        mutableStateOf(0)
    }

    /*
        Prints
        I  Middle SCOPE
        I  INNER COMPOSABLE
    */
    Column {
        Button(onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }
        
        OuterComposable {
            // This scope never gets recomposed because
            // Nothing
            println("Outer SCOPE")
            MiddleComposable {
                // Everything inside this scope
                // is recomposed when counter changes
                println("Middle SCOPE")
                InnerComposable(text = "Counter $counter")
            }
        }
    }
}

@Composable
private fun OuterComposable(
    content: @Composable () -> Unit
) {
    SideEffect {
        println("OUTER COMPOSABLE")
    }
    content()
}


@Composable
private fun MiddleComposable(
    content: @Composable () -> Unit
) {
    SideEffect {
        println("MIDDLE COMPOSABLE")
    }
    SomeComposable()
    content()
}

@Composable
private fun SomeComposable() {
    SideEffect {
        println("SomeComposable()")
    }
    
    Text(text = "Some Composable")
}

@Composable
private fun InnerComposable(
    text: String
) {
    SideEffect {
        println("INNER COMPOSABLE")
    }
    SomeInnerComposable()
    TextComposable(text = text)
}

@Composable
private fun TextComposable(text: String){
    Text(text = text)
}

@Composable
private fun SomeInnerComposable() {
    SideEffect {
        println("SomeInnerComposable()")
    }
}

/*
    This sample uses this article as reference to show how scoping effect which section of
    any composable should be recomposed

    https://www.jetpackcompose.app/articles/donut-hole-skipping-in-jetpack-compose
 */
@Preview
@Composable
fun MyComponent() {
    var counter by remember { mutableStateOf(0) }

    LogCompositions("🔥 MyComposable function")

    CustomButton(onClick = { counter++ }) {
        LogCompositions("🍉 CustomButton scope")
        CustomTextWrapper(text = "Counter: $counter")
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
fun CustomTextWrapper(text: String) {
    LogCompositions("🎃 CustomTextWrapper function")
    AnotherComposable()
    CustomText(text)
}

@Composable
private fun AnotherComposable() {
    LogCompositions("🍋 AnotherComposable function")
}

@Composable
fun CustomText(text: String) {
    LogCompositions("🍎 CustomText function")

    Text(
        text = text,
        modifier = Modifier
            .border(3.dp, getRandomColor())
            .padding(32.dp),
        fontSize = 20.sp
    )
}

