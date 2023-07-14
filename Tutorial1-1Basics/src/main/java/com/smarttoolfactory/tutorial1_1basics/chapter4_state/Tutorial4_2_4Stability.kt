package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor


/*

    In jetpack Compose stability, not triggering recomposition when inputs of a
    function hasn't changed, is achieved by using immutable variables or/and using
    @Immutable or @Stable annotation to make functions skippable
    as can be seen in detail in articles below.

    Skippable — when called during recomposition, compose is able to skip the function
    if all of the parameters are equal with their previous values.

    Restartable — this function serves as a “scope” where recomposition can start
    (In other words, this function can be used as a point of entry for where Compose can
    start re-executing code for recomposition after state changes).

    Types could be immutable or stable:

    Immutable — Indicates a type where the value of any properties will never change after
    the object is constructed, and all methods are referentially transparent.
    All primitive types (String, Int, Float, etc) are considered immutable.

    Stable — Indicates a type that is mutable, but the Compose runtime will be notified if
    and when any public properties or method behavior
    would yield different results from a previous invocation.

    Resources
    https://medium.com/androiddevelopers/jetpack-compose-stability-explained-79c10db270c8
    https://chrisbanes.me/posts/composable-metrics/
 */
@Preview
@Composable
fun Tutorial4_2_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        StyleableTutorialText(
            text = "**Stability**, not triggering recomposition when inputs of a " +
                    "function hasn't changed, is achieved" +
                    " by using immutable variables or/and using" +
                    "    @Immutable or @Stable annotation to make functions skippable.\n" +
                    "In the example below **UnStableDataClass(var)** is not skippable because of " +
                    "**UnstableComposable(data = unstableData)**. It " +
                    "recompose even if the input doesn't change.",
            bullets = false
        )
        ComposeScopeSample()
    }
}

@Preview
@Composable
private fun ComposeScopeSample() {
    var counter by remember {
        mutableStateOf(0)
    }

    val unstableData by remember {
        mutableStateOf(UnStableDataClass(0))
    }

    val stableData by remember {
        mutableStateOf(StableDataClas(0))
    }

    Column {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }

        Spacer(modifier = Modifier.height(20.dp))
        OuterComposable {
            // This scope never gets recomposed because
            // Nothing is read here
            println("Outer SCOPE")
            MiddleComposable {
                // This scope is recomposed because counter is read in this scope
                println("Middle SCOPE")
                // Unstable Functions get recomposed even when their inputs don't change
                UnstableComposable(data = unstableData)
                StableComposable(data = stableData)
                InnerComposable(text = "Counter $counter")
            }
        }
    }
}

// 🔥 A class with mutable param is Unstable
data class UnStableDataClass(var value: Int)

// A class with all of its params immutable is Stable
data class StableDataClas(val value: Int)

@Composable
private fun UnstableComposable(data: UnStableDataClass) {
    SideEffect {
        println("🍎 UnstableComposable")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "UnstableComposable() value: ${data.value}")
    }
}

@Composable
private fun StableComposable(data: StableDataClas) {
    SideEffect {
        println("🍏 StableComposable")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "StableComposable value(): ${data.value}")
    }
}

@Composable
private fun OuterComposable(
    content: @Composable () -> Unit
) {
    SideEffect {
        println("OUTER COMPOSABLE")
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        content()
    }
}


@Composable
private fun MiddleComposable(
    content: @Composable () -> Unit
) {
    SideEffect {
        println("MIDDLE COMPOSABLE")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        content()
    }
}

@Composable
private fun InnerComposable(
    text: String
) {
    SideEffect {
        println("INNER COMPOSABLE")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "Inner Composable: $text")
    }
}
