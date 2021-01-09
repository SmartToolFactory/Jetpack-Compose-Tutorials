package com.smarttoolfactory.tutorial1_1basics.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FullWidthRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {

    Row(modifier = modifier.fillMaxWidth()) {
        content()
    }
}


@Composable
fun FullWidthColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {

    Column(modifier = modifier.fillMaxWidth()) {
        content()
    }
}