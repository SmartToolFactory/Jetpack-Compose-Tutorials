package com.smarttoolfactory.tutorial1_1basics.chapter7_custom_components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity

@Composable
fun CustomComponent(modifier: Modifier = Modifier) {

   val density = LocalDensity.current

    Canvas(modifier = modifier.aspectRatio(1f), onDraw = {
        
    })
}