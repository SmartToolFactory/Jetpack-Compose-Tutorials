package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.editbox.ScaleEditBox

@Composable
fun Tutorial6_6Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(Color(0xff424242))
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Column(modifier = Modifier.fillMaxSize()) {

            var text by remember { mutableStateOf("") }

            val density = LocalDensity.current
            val size = (1000 / density.density).dp

            ScaleEditBox(
                modifier = Modifier
                    .size(size),
                onTextChange = {
                    text = it
                }
            ) {
                Image(
                    modifier = Modifier
                        .size(size),
                    painter = painterResource(id = R.drawable.landscape1),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = ""
                )
            }

            Text(text, color = Color.White, fontSize = 12.sp)
        }
    }
}
