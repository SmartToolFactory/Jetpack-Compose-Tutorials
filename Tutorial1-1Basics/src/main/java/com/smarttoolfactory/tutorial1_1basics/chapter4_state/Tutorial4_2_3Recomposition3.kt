package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Composable
fun Tutorial4_2_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {

        StyleableTutorialText(
            text = "Since we are using separate composables for Button, Column that have modifiers " +
                    "compositions are smart compositions, not all of the tree is recomposed, " +
                    "only **Text** are recomposed with updated values.",
            bullets = false
        )
        Sample1()
        Spacer(modifier = Modifier.height(12.dp))
        Sample2()
        Spacer(modifier = Modifier.height(12.dp))
        Sample3()
        Spacer(modifier = Modifier.height(12.dp))
        StyleableTutorialText(
            text = "",
            bullets = false
        )
        Sample4()
        Spacer(modifier = Modifier.height(12.dp))
        Sample5()
    }
}

@Composable
private fun Sample1() {
    RandomColorColumn {
        var counter by remember { mutableStateOf(0) }

        Text("Sample1", color = getRandomColor())
        RandomColorButton(onClick = { counter++ }) {
            RandomColorText("Counter: $counter")
        }
    }
}


@Composable
private fun Sample2() {
    RandomColorColumn {
        var update1 by remember { mutableStateOf(0) }
        var update2 by remember { mutableStateOf(0) }

        println("ROOT")
        RandomColorText("Sample2")

        RandomColorButton(onClick = { update1++ }) {
            println("🔥 Button 1")
            RandomColorText(text = "Update1: $update1")
        }

        RandomColorButton(onClick = { update2++ }) {
            println("🍏 Button 2")
            RandomColorText(text = "Update2: $update2")
        }

        RandomColorColumn {

            println("🚀 Inner Column")
            var update3 by remember { mutableStateOf(0) }

            RandomColorButton(onClick = { update3++ }) {
                println("✅ Button 3")
                RandomColorText(text = "Update2: $update2, Update3: $update3")
            }

            RandomColorColumn {
                println("☕️ Bottom Column")
                RandomColorText("Sample2")
            }
        }
    }
}


@Composable
private fun Sample3() {
    RandomColorColumn {
        var update1 by remember { mutableStateOf(0) }
        var update2 by remember { mutableStateOf(0) }

        println("ROOT")
        RandomColorText("Sample3")

        RandomColorButton(onClick = { update1++ }) {
            println("🔥 Button 1")
            RandomColorText(text = "Update1: $update1")
        }

        RandomColorButton(onClick = { update2++ }) {
            println("🍏 Button 2")
            RandomColorText(text = "Update2: $update2")
        }

        RandomColorColumn {
            println("🚀 Inner Column")
            var update3 by remember { mutableStateOf(0) }
            RandomColorButton(onClick = { update3++ }) {

                println("✅ Button 3")
                RandomColorText(text = "Update2: $update2, Update3: $update3")
            }
            RandomColorColumn {
                println("☕️ Bottom Column")
                RandomColorText(text = "Update1: $update1")
            }
        }
        RandomColorText(text = "Sample3")
        // 🔥🔥 Since it's a separate function it does not recomposed without updating it with an argument
        SomeComposable()
    }
}

@Composable
private fun Sample4() {
    RandomColorColumn {
        var update1 by remember { mutableStateOf(0) }
        var update2 by remember { mutableStateOf(0) }
        println("ROOT")
        RandomColorText("Sample4")
        RandomColorButton(onClick = { update1++ }) {
            println("🔥 Button 1")
            RandomColorText(text = "Update1: $update1")
        }

        RandomColorButton(
            onClick = { update2++ }
        ) {
            println("🍏 Button 2")
            RandomColorText(text = "Update2: $update2")
        }

        RandomColorColumn {
            println("🚀 Inner Column")
            var update3 by remember { mutableStateOf(0) }

            RandomColorButton(onClick = { update3++ }) {
                println("✅ Button 3")
                RandomColorText(text = "Update2: $update2, Update3: $update3")
            }
            RandomColorColumn {
                println("☕️ Bottom Column")
                RandomColorText(text = "Update1: $update1")
                // 🔥🔥 Since it's a separate function it does not recomposed without updating it with an argument
                SomeComposable()
            }
        }

        // 🔥🔥 Since it's a separate function it does not recomposed without updating it with an argument
        SomeComposable()
        Text(
            "⚠️ SomeComposable below that observes update2 causes entire composable " +
                    "to be recomposed because it's at same level. Wrap it with RandomColorColumn to prevent this",
            color = getRandomColor()
        )
        // 🔥🔥 It's updated since we sent an argument to it
        // 🔥🔥⚠️ This causes whole composable to RECOMPOSED but not RandomColumn or RandomButton
        SomeComposable(update2)
    }
}


@Composable
private fun Sample5() {
    RandomColorColumn {
        var update1 by remember { mutableStateOf(0) }
        var update2 by remember { mutableStateOf(0) }

        println("ROOT")
        RandomColorText("Sample5")

        RandomColorButton(onClick = { update1++ }) {
            println("🔥 Button 1")
            RandomColorText(text = "Update1: $update1")
        }

        RandomColorButton(onClick = { update2++ }) {
            println("🍏 Button 2")
            RandomColorText(text = "Update2: $update2")
        }

        RandomColorColumn {
            println("🚀 Inner Column")
            RandomColorColumn {
                println("☕️ Bottom Column")
                RandomColorText(text = "Update1: $update1")
            }
            // 🔥🔥 It's updated since we sent an argument to it
            SomeComposable(update2)
        }

        // 🔥🔥 Since it's a separate function it does not recomposed without updating it with an argument
        SomeComposable()
        Text(
            "⚠️ AnotherComposable below that observes update2 causes entire composable " +
                    "to be recomposed because it's at same level. Wrap it with RandomColorColumn to prevent this",
            color = getRandomColor()
        )
        // 🔥🔥 It's updated since we sent an argument to it
        // 🔥🔥⚠️ This causes whole composable to RECOMPOSED but not RandomColumn or RandomButton
        AnotherComposable(update2)
    }
}

@Composable
private fun SomeComposable(update: Int = 0) {

    println("🚀 SomeComposable() Composable")

    val text = if (update == 0) "no args" else "update: $update"

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(getRandomColor()),
        text = "SomeComposable $text",
        textAlign = TextAlign.Center,
        color = getRandomColor()
    )
}


@Composable
private fun AnotherComposable(update: Int) {

    println(" AnotherComposable() First Column")
    val text = if (update == 0) "no args" else "update: $update"

    RandomColorColumn {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "AnotherComposable $text",
            textAlign = TextAlign.Center,
            color = getRandomColor()
        )
        RandomColorColumn {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "AnotherComposable bottom inner text",
                textAlign = TextAlign.Center,
                color = getRandomColor()
            )
        }
    }
}

@Composable
fun RandomColorText(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = getRandomColor()
    )
}

@Composable
fun RandomColorColumn(content: @Composable () -> Unit) {

    println("📌 RandomColumn COMPOSABLE: $content")
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .padding(4.dp)
    ) {
        content()
    }
}

@Composable
private fun RandomColorButton(onClick: () -> Unit, content: @Composable () -> Unit) {

    println("💬 RandomColorButton COMPOSABLE: $content")

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = getRandomColor()),
        onClick = onClick,
        shape = RoundedCornerShape(5.dp)
    ) {
        content()
    }
}