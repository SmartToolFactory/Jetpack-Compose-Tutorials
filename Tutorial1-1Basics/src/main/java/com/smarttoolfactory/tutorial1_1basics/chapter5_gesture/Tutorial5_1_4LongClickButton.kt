package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Preview
@Composable
private fun ButtonLongClickSample() {

    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }

    val viewConfiguration = LocalViewConfiguration.current


    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    Toast.makeText(context, "Long click", Toast.LENGTH_SHORT).show()
                }

                is PressInteraction.Release -> {
                    if (isLongClick.not()) {
                        Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Spacer(Modifier.weight(1f))
        FloatingActionButton(
            onClick = {},
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
