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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor


@Preview
@Composable
fun Tutorial4_2_8Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column {
        StyleableTutorialText(
            text = "**@NonRestartableComposable** annotation function is nor " +
                    "skippable neither restartable.\n" +
                    "When a function is not skippable even if " +
                    "its params change it gets recomposed.\n" +
                    "When a composable is not restartable it doesn't create a scope which means " +
                    "recomposition happens inside that function might trigger recomposition in " +
                    "parent function too.",
            bullets = false
        )
        Spacer(modifier = Modifier.height(20.dp))
        NonRestartableComposableSample()
    }
}

@Preview
@Composable
private fun NonRestartableComposableSample() {
    var counter1 by remember {
        mutableIntStateOf(0)
    }

    var counter2 by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(4.dp)
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter1++ }) {
            Text(text = "Counter: $counter1")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter2++ }) {
            Text(text = "Counter: $counter2")
        }

        Text(text = "Counter1: $counter1")
        Text(text = "Counter2: $counter2")

        // 🔥When this Composable has @NonRestartableComposable
        // 1- It's not restartable which means when value changes it
        // this scope gets recomposed too.
        // 2- It's not skippable which means despite it doesn't read counter1 it recomposes
        // when it changes,not skipping while it's input counter doesn't change
        Counter(counter = counter2)
    }
}

/*
    This annotation can be applied to Composable functions in order to prevent code from being
    generated which allow this function's execution to be skipped or restarted.his annotation
    makes this Composable.
 */
// Comment out this annotation to see the difference
@NonRestartableComposable
@Composable
private fun Counter(counter: Int) {

    var value by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .fillMaxWidth()
            .background(getRandomColor())
            .padding(4.dp)
    ) {
        Text(text = "Counter: $counter")
        Text(text = "Value: $value")

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { value++ }) {
            Text(text = "Value: $value")
        }
    }
}
