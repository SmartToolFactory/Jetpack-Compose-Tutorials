package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
private fun ComposeScopeSample() {
    var counter by remember {
        mutableStateOf(0)
    }

    val list = remember {
        listOf(1, 2, 3)
    }

    val unstableData by remember {
        mutableStateOf(UnStableDataClass(0))
    }

    val stableData by remember {
        mutableStateOf(StableDataClas(0))
    }

    Column {
        Button(onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }

        OuterComposable {
            // This scope never gets recomposed because
            // Nothing is read here
            println("Outer SCOPE")
            MiddleComposable {

                println("Middle SCOPE")
                ListParamFunction(list = list)
                UnstableFunction(data = unstableData)
                StableFunction(data = stableData)
                InnerComposable(text = "Counter $counter")
            }
        }
    }
}

data class UnStableDataClass(var value: Int)
data class StableDataClas(val value: Int)

@Composable
private fun UnstableFunction(data: UnStableDataClass) {
    SideEffect {
        println("🍎 UnstableFunction")
    }
    Text(text = "Value: ${data.value}")
}

@Composable
private fun StableFunction(data: StableDataClas) {
    SideEffect {
        println("🍏 StableFunction")
    }
    Text(text = "Value: ${data.value}")
}


@Composable
private fun ListParamFunction(list: List<Int>) {
    SideEffect {
        println("🔥 ListParamFunction")
    }
    Text(text = "List size: ${list.size}")
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
private fun InnerComposable(
    text: String
) {
    SideEffect {
        println("INNER COMPOSABLE")
    }
    Text(text = text)
}


@Composable
private fun SomeComposable() {
    SideEffect {
        println("SomeComposable()")
    }

    Text(text = "Some Composable")
}

@Preview
@Composable
private fun NeverEqualPolicySample() {
    var singleEventData by remember {
        mutableStateOf(
            value = SingleEventData(message = "neverEqualPolicy message"),
            policy = referentialEqualityPolicy()
        )
    }

    var counter by remember {
        mutableStateOf(0)
    }

    Column {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                singleEventData =
                    singleEventData.copy(message = "neverEqualPolicy message")

            }
        ) {
            Text(text = "Click to create Event")
        }

        Button(
            onClick = {
                counter++
            }
        ) {
            Text(text = "Counter: $counter")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Counter: $counter")
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .background(Blue400)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier
                    .border(2.dp, getRandomColor())
                    .fillMaxWidth()
                    .padding(10.dp),
                text = singleEventData.message,
                color = Color.White,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        InnerEventFunction(singleEventData)
    }
}

@Composable
private fun InnerEventFunction(singleEventData: SingleEventData) {
    Column(
        modifier = Modifier
            .border(2.dp, getRandomColor())
            .fillMaxWidth()
    ) {
        Text(text = "Inner Composable: ${singleEventData.message}")
    }
}

private data class SingleEventData(val message: String)
