package com.smarttoolfactory.tutorial1_1basics.chapter8_semantics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun UndoRedoTest() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        val redoState = remember {
            RedoState()
        }

        EditableTextField(redoState)

        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    redoState.undoText()
                }
            ) {
                Text("Undo")
            }

            Spacer(Modifier.width(16.dp))

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    redoState.redoText()
                }
            ) {
                Text("Redo")
            }
        }
    }
}

@Composable
fun EditableTextField(
    redoState: RedoState = remember {
        RedoState()
    }
) {

    val textFieldValue = redoState.textFieldValue

    TextField(
        value = textFieldValue,
        onValueChange = {
            redoState.update(it)
        }
    )
}

class RedoState {

    private val defaultValue = TextFieldValue()

    private val textFieldValues = mutableListOf<TextFieldValue>()
    private val undoneTextFieldValues = mutableListOf<TextFieldValue>()

    var textFieldValue: TextFieldValue by mutableStateOf(defaultValue)

    fun redoText() {
        if (undoneTextFieldValues.isNotEmpty()) {
            val lastItem = undoneTextFieldValues.last()
            textFieldValue = undoneTextFieldValues.removeLast()
            textFieldValues.add(lastItem)
        }
    }

    fun undoText() {
        if (textFieldValues.isNotEmpty()) {
            val lastItem = textFieldValues.last()
            textFieldValues.removeLast()

            textFieldValues.lastOrNull()?.let {
                textFieldValue = it
            } ?: run {
                textFieldValue = defaultValue
            }
            undoneTextFieldValues.add(lastItem)
        }
    }

    fun update(newValue: TextFieldValue) {
        if (textFieldValues.isEmpty() || (textFieldValue.text != newValue.text)
        ) {
            textFieldValues.add(newValue)
        }
        textFieldValue = newValue

    }
}