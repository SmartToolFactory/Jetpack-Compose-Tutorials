package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor


@Preview
@Composable
fun Tutorial4_2_7Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column {
        StyleableTutorialText(
            text = "Reading unstable types inside lambda causes function " +
                    "to be recomposed.",
            bullets = false
        )
        Spacer(modifier = Modifier.height(20.dp))
        LambdaStabilitySample()
    }
}

@Preview
@Composable
private fun LambdaStabilitySample() {
    var counter by remember {
        mutableStateOf(0)
    }

    val contextWrapper = ContextWrapper(LocalContext.current)
    // 🔥List and Context are not @Stable so when they are read inside lambda
    // It triggers recomposition for the function
    val context = LocalContext.current
//    val list = remember {
//        List(5){
//            it
//        }
//    }

    Column {
        Text(text = "Counter $counter")
        Outer {
            counter++
            contextWrapper.context
//            context
//            list
//            Toast.makeText(contextWrapper.context, "Toast", Toast.LENGTH_SHORT).show()
        }
    }
}

// Removing this annotation causes recomposition to be triggered when read
@Immutable
data class ContextWrapper(val context: Context)

@Composable
private fun Outer(
    onClick: () -> Unit
) {

    SideEffect {
        println("Outer")
    }

    Column(
        modifier = Modifier
            .border(2.dp, getRandomColor())
            .padding(10.dp)
    ) {
        Button(onClick = onClick) {
            Text(text = "Click")
        }
    }
}
