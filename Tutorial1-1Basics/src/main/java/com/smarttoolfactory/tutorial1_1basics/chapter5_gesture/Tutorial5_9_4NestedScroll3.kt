package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Brown400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400

@Preview
@Composable
fun Tutorial5_9Screen4() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        NestedScrollSample()
    }
}

@Preview
@Composable
private fun NestedScrollSample() {

    var offsetPreScroll by remember {
        mutableFloatStateOf(0f)
    }

    var offsetPostScroll by remember {
        mutableFloatStateOf(0f)
    }

    var textPreScroll by remember {
        mutableStateOf("")
    }

    var textPostScroll by remember {
        mutableStateOf("")
    }

    val scrollState = rememberScrollState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                textPreScroll = "onPreScroll() available: ${available.y}"
                offsetPreScroll =
                    (offsetPreScroll - available.y).coerceIn(0f, scrollState.maxValue.toFloat())
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                offsetPostScroll =
                    (offsetPostScroll - consumed.y).coerceIn(0f, scrollState.maxValue.toFloat())
                textPostScroll = "onPostScroll() consumed: ${consumed.y}, available: ${available.y}"
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    Column {
        Text(text = textPreScroll)
        Text(text = textPostScroll)
        Text(
            text = "offsetPreScroll: $offsetPreScroll, " +
                    "offsetPostScroll: $offsetPostScroll"
        )

        Column(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState())

            ) {
                repeat(20) {

                    val color = if (it % 2 == 0) Red400 else Brown400
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color)
                            .padding(16.dp),
                        text = "Row $it",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
            repeat(50) {

                val color = if (it % 2 == 0) Green400 else Pink400
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .padding(16.dp),
                    text = "Row $it",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
    }
}