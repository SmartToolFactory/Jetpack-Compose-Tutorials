package com.smarttoolfactory.tutorial1_1basics.chapter7_theming

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

val LocalColors: ProvidableCompositionLocal<Colors> = staticCompositionLocalOf {
    lightColors()
}

val LocalStaticCounter: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { 0 }
val LocalCounter: ProvidableCompositionLocal<Int> = compositionLocalOf { 0 }


@Preview
@Composable
private fun StaticCompositionLocalOfTest() {
    var isLightTheme by remember {
        mutableStateOf(true)
    }
    CompositionLocalProvider(
        LocalColors provides if (isLightTheme) lightColors(
        ) else darkColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColors.current.background)
                .padding(16.dp)
        ) {
            Text("Selected theme: $isLightTheme")
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    isLightTheme = isLightTheme.not()
                }
            ) {
                Text("Change Theme")
            }
        }
    }
}

@Preview
@Composable
fun LocalVsStaticCompositionLocalTest() {

    var counter by remember {
        mutableIntStateOf(0)
    }

    var counterStatic by remember {
        mutableIntStateOf(0)
    }
    CompositionLocalProvider(LocalCounter provides counter) {
        CompositionLocalProvider(LocalStaticCounter provides counterStatic) {
            Content(
                onCLickLocal = {
                    counter++
                },
                onCLickStaticLocal = {
                    counterStatic++
                }
            )
        }
    }
}

@Composable
private fun Content(
    onCLickLocal: () -> Unit,
    onCLickStaticLocal: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(4.dp, getRandomColor())
            .padding(16.dp)
    ) {
        StyleableTutorialText(
            text = "**staticCompositionLocalOf** composes entire tree inside its content " +
                    "while **compositionLocalOf** only composes where value **Local** is rea"
        )
        LocalCounterText()
        LocalStaticCounterText()

        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onCLickLocal()
            }
        ) {
            LocalCounterText()
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onCLickStaticLocal()
            }
        ) {
            LocalStaticCounterText()
        }
    }
}

@Composable
private fun LocalCounterText() {
    val counter = LocalCounter.current
    Text(
        text = "LocalCounter $counter",
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth().border(2.dp, getRandomColor())
    )
}

@Composable
private fun LocalStaticCounterText() {
    val counter = LocalStaticCounter.current
    Text(
        text = "LocalStaticCounter $counter",
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth().border(2.dp, getRandomColor())
    )
}