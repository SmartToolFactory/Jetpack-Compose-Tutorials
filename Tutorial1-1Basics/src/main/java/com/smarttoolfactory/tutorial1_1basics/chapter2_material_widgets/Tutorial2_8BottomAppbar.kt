package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

@Composable
fun Tutorial2_8Screen(onBack: (() -> Unit)? = null) {
    TutorialContent(onBack)
}

@Composable
private fun TutorialContent(onBack: (() -> Unit)? = null) {
    Scaffold(

        modifier = Modifier.background(Color(0xffECEFF1)),
        topBar = {
            TopAppBarWithTabComponent(onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                backgroundColor = Color(0xffFFA000)
            ) {
                Icon(
                    Icons.Filled.Add, tint = Color.White,
                    contentDescription = null
                )
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBarComponent(onBack)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {

        }
    }
}

@Composable
private fun BottomAppBarComponent(onBack: (() -> Unit)? = null) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp,
        cutoutShape = CircleShape
    ) {

        // Leading icons should typically have a high content alpha
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            IconButton(
                onClick = { onBack?.invoke() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }

        }
        // The actions should be at the end of the BottomAppBar. They use the default medium
        // content alpha provided by BottomAppBar
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { }) {
            Icon(Icons.Filled.Favorite, contentDescription = null)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Search, contentDescription = null)
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun BottomAppBarComponentPreview() {
    ComposeTutorialsTheme {
        BottomAppBarComponent()
    }
}
