@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Purple400
import kotlinx.coroutines.launch

// TODO There is a bug with Popup that clips content that exceed 16dp height
// out of bounds. Increasing caret height causes it to be clipped
@Preview
@Composable
fun TooltipBoxTest() {
    Box(
        modifier = Modifier.fillMaxSize().border(2.dp, Color.Blue)
    ) {

        var paddingStart by remember {
            mutableFloatStateOf(200f)
        }

        var paddingTop by remember {
            mutableFloatStateOf(0f)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text("paddingStart: ${paddingStart}.dp")
            Slider(
                value = paddingStart,
                onValueChange = {
                    paddingStart = it
                },
                valueRange = 0f..350f
            )

            Text("paddingTop: ${paddingTop}.dp")
            Slider(
                value = paddingTop,
                onValueChange = {
                    paddingTop = it
                },
                valueRange = 0f..750f
            )
        }

        TooltipBoxSample(
            modifier = Modifier.padding(
                start = paddingStart.dp,
                top = paddingTop.dp
            )
        )
    }
}

@Composable
private fun TooltipBoxSample(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val state = androidx.compose.material3.rememberTooltipState(
            isPersistent = true
        )
        val coroutineScope = rememberCoroutineScope()

        androidx.compose.material3.TooltipBox(
            positionProvider = rememberPlainTooltipPositionProvider(
                spacingBetweenTooltipAndAnchor = 16.dp
            ),
            focusable = true,
            enableUserInput = false,
            state = state,
            tooltip = {

                PlainTooltip(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    caretProperties = CaretProperties(
                        caretWidth = 24.dp,
                        caretHeight = 16.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Purple400
                ) {
                    Text(
                        text = "Tooltip Content for testing...",
                        modifier = Modifier.padding(16.dp)
                    )
                }

            },
            content = {
                Icon(
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                state.show()
                            }
                        },
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        )
    }
}
