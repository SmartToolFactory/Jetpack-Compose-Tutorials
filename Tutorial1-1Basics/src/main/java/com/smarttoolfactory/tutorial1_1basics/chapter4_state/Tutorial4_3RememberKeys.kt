package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial4_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp)) {

        StyleableTutorialText(text = "1-) Remember  produce and remember a new value " +
                "by calling calculation when key is not equal to previous key.")
        RememberKeyExample()
        StyleableTutorialText(text = "2-) Remember the value returned by calculation " +
                "if key1 and key2 are equal to the previous\n" +
                " composition, otherwise produce and remember a new value by calling calculation.")
        RememberMultipleKeysExample()
    }
}

@Composable
private fun RememberKeyExample() {
    var operation by remember { mutableStateOf(MathOperation.INCREASE) }
    var counter by remember(operation) { mutableStateOf(0) }


    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            operation = if (operation == MathOperation.INCREASE) {
                MathOperation.DECREASE
            } else {

                MathOperation.INCREASE
            }
        }) {
        val text = if (operation == MathOperation.INCREASE) {
            "Increase"
        } else {
            "Decrease"
        }
        Text("Current operation $text")
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (operation == MathOperation.INCREASE) {
                counter++
            } else {
                counter--
            }
        }
    ) {
        Text(text = "Counter $counter")
    }
}

enum class MathOperation {
    INCREASE, DECREASE
}

@Composable
private fun RememberMultipleKeysExample() {

    var key1Text by remember { mutableStateOf("key1") }
    var key2Text by remember { mutableStateOf("key2") }

    var counter by remember(key1 = key1Text, key2 = key2Text) { mutableStateOf(0) }

    Column {

        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = key1Text,
                onValueChange = {
                    key1Text = it
                },
                placeholder = {
                    Text(text = "key1")
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = key2Text,
                onValueChange = {
                    key2Text = it
                },
                placeholder = {
                    Text(text = "key2")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Counter: $counter", modifier = Modifier.weight(1f))
            IconButton(onClick = { counter++ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { counter-- }) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "remove")
            }

        }
    }
}

