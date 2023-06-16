package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.editbox.ScaleEditBox

@Preview
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
        // In this example we use a Composable that scales, translates its scope
        EditScaleDemo()
    }
}


@Composable
private fun EditScaleDemo() {
    Column(
        modifier = Modifier
            .background(Color(0xff424242))
            .fillMaxSize()
            .padding(8.dp)
    ) {


        Spacer(modifier = Modifier.height(40.dp))


        val density = LocalDensity.current
        val size = (1000 / density.density).dp

        ScaleEditBox(
            modifier = Modifier.size(size),
            enabled = true
        ) {
            Image(
                modifier=Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.landscape3),
                contentScale = ContentScale.FillBounds,
                contentDescription = "",
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        ScaleEditBox(
            handleRadius = 20.dp,
            enabled = true
        ) {
            Image(
                painter = painterResource(id = R.drawable.landscape2),
                contentScale = ContentScale.FillBounds,
                contentDescription = ""
            )
        }


    }
}