package com.smarttoolfactory.tutorial1_1basics.chapter8_semantics

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SnapshowFlowTest() {

    val query = remember {
        mutableStateOf(mutableStateOf(""))
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            query.value
        }
            .collect {
                println("State: $it, value: ${it.value}")
            }
    }

    Column {
        TextField(
            value = query.value.value,
            onValueChange = {
                query.value.value = it
            }
        )

        Button(
            onClick = {
                query.value = mutableStateOf("Default Value")
            }
        ) {
            Text("Set new MutableState")
        }
    }
}