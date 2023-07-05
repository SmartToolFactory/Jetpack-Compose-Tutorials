package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.widget.Toast
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
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial4_1Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(8.dp)
    ) {

        StyleableTutorialText(
            text =
            "**SnapshotMutationPolicy** determines whether setting value of **MutableState** " +
                    "with same primitive values should trigger recomposition.\n" +
                    "Change policies or implement custom one to determine when recomposition " +
                    "should be triggered.",
            bullets = false
        )
        SnapshotPolicySample()

        StyleableTutorialText(
            text = "SnapshotMutationPolicy controls how changes are handled in mutable " +
                    "snapshots. **structuralEqualityPolicy**(default) policy check " +
                    "if equals function of a class returns true.\n" +
                    "**copy function** of **data class** with same message returns equals so " +
                    "no recomposition is triggered. In first example recomposition is " +
                    "not trigger when we copy a new object since data class checks primary " +
                    "constructor values in **equals** functions",
            bullets = false
        )

        StructuralEqualitySample()
        StyleableTutorialText(
            text =
            "**referentialEqualityPolicy** policy checks if both instances are the same" +
                    " to trigger recomposition.\n" +
                    "Since new instance is created with copy each time button is clicked " +
                    "new one time event is fired. Border of Text changes on each recomposition " +
                    "with a random color to show recomposition visually.",
            bullets = false
        )
        ReferentialEqualitySample()

    }
}


@Preview
@Composable
private fun SnapshotPolicySample() {
    var counter by remember {
        mutableStateOf(
            value = 0,
            // 🔥Setting policy changes whether recomposition should
            // be triggered when same value is set in this example
//            policy = referentialEqualityPolicy(),
//            policy = structuralEqualityPolicy(),
            policy = neverEqualPolicy()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            // Depending on which policy is used setting same value will trigger recomposition
            onClick = { counter = 1 }) {
            SideEffect {
                println("Recomposing with $counter")
            }
            Text(text = "Click to set MutableState value")
        }

        Text(
            modifier = Modifier
                .border(2.dp, getRandomColor())
                .fillMaxWidth()
                .padding(10.dp),
            text = "counter: $counter",
            fontSize = 16.sp
        )
    }
}

@Composable
private fun StructuralEqualitySample() {

    val context = LocalContext.current

    var oneTimeEventData by remember {
        mutableStateOf(
            value = OneTimeEventData(message = "structuralEqualityPolicy message"),
            // 🔥 For recomposition to be triggered we need to assign an object with
            // different message since data class checks primary constructor values
            // for equals function
            policy = structuralEqualityPolicy()
        )
    }

    // This is for showing toast message only on each recomposition
    Toast.makeText(context, oneTimeEventData.message, Toast.LENGTH_SHORT).show()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                oneTimeEventData =
                    oneTimeEventData.copy(message = "structuralEqualityPolicy message")

            }
        ) {
            Text(text = "Click to create Event")
        }

        Text(
            modifier = Modifier
                .border(2.dp, getRandomColor())
                .fillMaxWidth()
                .padding(10.dp),
            text = oneTimeEventData.message,
            fontSize = 16.sp
        )
    }

}

@Composable
private fun ReferentialEqualitySample() {

    val context = LocalContext.current


    var oneTimeEventData by remember {
        mutableStateOf(
            value = OneTimeEventData(message = "referentialEqualityPolicy message"),
            policy = referentialEqualityPolicy()
        )
    }

    Toast.makeText(context, oneTimeEventData.message, Toast.LENGTH_SHORT).show()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                oneTimeEventData =
                    oneTimeEventData.copy(message = "referentialEqualityPolicy message")

            }
        ) {
            Text(text = "Click to create Event")
        }

        Text(
            modifier = Modifier
                .border(2.dp, getRandomColor())
                .fillMaxWidth()
                .padding(10.dp),
            text = oneTimeEventData.message,
            fontSize = 16.sp
        )
    }
}

private data class OneTimeEventData(val message: String)
