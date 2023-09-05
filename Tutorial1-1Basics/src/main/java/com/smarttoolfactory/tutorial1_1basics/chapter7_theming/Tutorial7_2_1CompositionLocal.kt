package com.smarttoolfactory.tutorial1_1basics.chapter7_theming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor


data class Elevations(val card: Dp = 0.dp, val default: Dp = 0.dp)

// Define a CompositionLocal global object with a default
// This instance can be accessed by all composables in the app
val LocalElevations = compositionLocalOf { Elevations() }

@Preview
@Composable
private fun Test() {

    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        // Default icon
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(10.dp))

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Green
        )
        Spacer(modifier = Modifier.height(10.dp))

        Icon(
            modifier = Modifier.alpha(.3f),
            imageVector = Icons.Default.Favorite,
            tint = Color.Green,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Icon uses tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        // changing it also changes tint
        CompositionLocalProvider(LocalContentColor provides Color.Green) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        CompositionLocalProvider(LocalContentAlpha provides .3f) {
            CompositionLocalProvider(LocalContentColor provides Color.Green) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun SomeComposable() {
    // Access the globally defined LocalElevations variable to get the
    // current Elevations in this part of the Composition
    Card(
        elevation = LocalElevations.current.card,
        backgroundColor = Color.White
    ) {
        Text(text = "Hello World", fontSize = 20.sp)
    }
}

@Preview
@Composable
fun CompositionLocalExample() {
    MaterialTheme { // MaterialTheme sets ContentAlpha.high as default
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Text("Uses MaterialTheme's provided alpha")
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("Medium value provided for LocalContentAlpha")
                Text("This Text also uses the medium value")
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    DescendantExample()
                }
            }
        }
    }
}

@Composable
fun DescendantExample() {
    // CompositionLocalProviders also work across composable functions
    Text("This Text uses the disabled alpha now")
}