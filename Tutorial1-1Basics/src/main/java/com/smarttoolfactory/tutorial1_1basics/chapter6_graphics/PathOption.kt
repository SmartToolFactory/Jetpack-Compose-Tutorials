package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

@Composable
fun rememberPathOption(
    strokeWidth: Float = 10f,
    color: Color = Color.Red,
    strokeCap: StrokeCap = StrokeCap.Round,
    strokeJoin: StrokeJoin = StrokeJoin.Round
): PathOption {
    return remember{
        PathOption(strokeWidth, color, strokeCap, strokeJoin)
    }
}

class PathOption(
    strokeWidth: Float,
    color: Color,
    strokeCap: StrokeCap,
    strokeJoin: StrokeJoin
) {
    var strokeWidth by mutableStateOf(strokeWidth)
    var color by mutableStateOf(color)
    var strokeCap by mutableStateOf(strokeCap)
    var strokeJoin by mutableStateOf(strokeJoin)
    var eraseMode = false
}
