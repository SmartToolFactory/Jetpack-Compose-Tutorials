@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// TODO There is a bug with Popup that clips content that exceed 16dp height
// out of bounds. Increasing caret height causes it to be clipped
@Preview
@Composable
fun TooltipBoxSample() {
    Column(
        modifier = Modifier.fillMaxSize().border(2.dp, Color.Blue)
    ) {
        val state = androidx.compose.material3.rememberTooltipState(
            isPersistent = true
        )
        val coroutineScope = rememberCoroutineScope()

        Spacer(modifier = Modifier.height(0.dp))

        Row(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.width(0.dp))

            TooltipBox(
                positionProvider = rememberPlainTooltipPositionProvider(
                    spacingBetweenTooltipAndAnchor = 16.dp
                ),
                modifier = Modifier.border(2.dp, Color.Red),
                focusable = true,
                state = state,
                tooltip = {

                    PlainTooltip(
                        caretProperties = CaretProperties(
                            caretWidth = 16.dp,
                            caretHeight = 16.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.Red
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
}
