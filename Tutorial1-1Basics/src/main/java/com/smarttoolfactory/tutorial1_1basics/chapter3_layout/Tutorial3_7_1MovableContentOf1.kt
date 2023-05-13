package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Composable
fun Tutorial3_7Screen1() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    var showAsRow by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        StyleableTutorialText(
            text = "With **movableContentOf** convert a lambda into " +
                    "one that moves the remembered state and nodes created in a previous call " +
                    "to the new location it is called.\n" +
                    "Tracking compositions can be used to produce a composable that moves its " +
                    "content between a row and a column based on a parameter",
            bullets = false
        )

        val movableContent = remember {
            movableContentOf {
                CustomTextField()
                CustomCheckBox()
                Counter()
            }
        }

        val contentList = remember {
            listOf<@Composable () -> Unit> {
                CustomTextField()
                CustomCheckBox()
                Counter()
            }
        }

        if (showAsRow) {
            Row {
                movableContent()
            }
        } else {
            movableContent()
        }

        StyleableTutorialText(
            text = "When content enters composition content is reset by default.",
            bullets = false
        )

        if (showAsRow) {
            Row {
                contentList.forEach {
                    it.invoke()
                }
            }
        } else {
            contentList.forEach {
                it.invoke()
            }
        }

        // Both are same with contentList
//        if (showAsRow) {
//            Row {
//                CustomTextField()
//                CustomSwitch()
//                Counter()
//            }
//        } else {
//            CustomTextField()
//            CustomSwitch()
//            Counter()
//        }
        
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showAsRow = showAsRow.not()
            }
        ) {
            Text(text = "Show as Row: $showAsRow")
        }
    }
}

@Composable
private fun CustomTextField() {
    var text by remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier.width(180.dp),
        value = text,
        onValueChange = { text = it }
    )
}

@Composable
private fun CustomCheckBox() {
    var checked by remember {
        mutableStateOf(false)
    }

    Checkbox(checked = checked, onCheckedChange = { checked = it })
}

@Composable
private fun Counter() {
    // 🔥🔥 There is a bug with counter text, it doesn't recompose text while
    // everything else does unless counter is increased or decreased until 10 🤯

    var counter by remember {
        mutableStateOf(0)
    }

    SideEffect {
        println("Recomposing counter: $counter")
    }

    Row(
        modifier = Modifier.border(2.dp, getRandomColor()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                counter++
                println("Increase: $counter")
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "+")
        }
        Text(text = "$counter")
        IconButton(
            onClick = {
                if (counter > 0) counter--
            }
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "-")
        }
    }
}