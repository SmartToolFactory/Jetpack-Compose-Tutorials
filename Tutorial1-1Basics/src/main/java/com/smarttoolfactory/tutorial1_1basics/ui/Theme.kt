package com.smarttoolfactory.tutorial1_1basics.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = purple200,
    secondary = teal200,
    primaryVariant = purple700,
    error = Color(0xFFB3261E),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF201F24),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onError = Color.White
)

private val LightColorPalette = lightColors(
    primary = purple500,
    secondary = teal200,
    primaryVariant = purple700,
    error = Color(0xFFF2B8B5),
    surface = Color(0xFFE6E1E5),
    background = Color(0xFFE6E1E5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF201F24),
    onSurface = Color(0xFF201F24),
    onError = Color(0xFF601410)
)

@Composable
fun ComposeTutorialsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val sysUiController = rememberSystemUiController()
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = colors.surface.copy(.5f)
        )
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}