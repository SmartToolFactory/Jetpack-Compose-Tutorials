package com.smarttoolfactory.tutorial1_1basics.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(InternalLayoutApi::class)
@Composable
fun FullWidthRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        content()
    }
}


@Composable
fun FullWidthColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {

    Column(modifier = modifier.fillMaxWidth()) {
        content()
    }
}