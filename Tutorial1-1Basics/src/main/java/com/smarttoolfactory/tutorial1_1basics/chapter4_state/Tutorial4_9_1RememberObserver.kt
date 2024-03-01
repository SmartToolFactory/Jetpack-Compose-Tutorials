package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Tutorial4_9_1Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    var showSample by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Button(onClick = { showSample = showSample.not() }) {
            Text(text = "Show Composable")
        }
        if (showSample) {
            RememberObserverSample()
        }
    }
}

@Composable
private fun RememberObserverSample() {
    val sampleUiState = remember {
        SampleUiState()
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { sampleUiState.counter++ }
    ) {
        Text(text = "Increase Counter")
    }

    Text(text = "Counter: ${sampleUiState.counter}")
}


private class SampleUiState : RememberObserver {

    var counter by mutableIntStateOf(0)


    override fun onAbandoned() {
        println("🔥 onAbandoned")
    }

    override fun onForgotten() {
        println("🏈 onForgotten")
    }

    override fun onRemembered() {
        println("🔥 onRemembered")
    }

}