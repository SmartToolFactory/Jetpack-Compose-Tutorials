package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BadgedBox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Tutorial4_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    BadgeComponent()
//        var size = IntSize(0,0)
//        Surface(
//            shape = CircleShape,
//            contentColor = Color.White,
//            color = Color.Red,
//            elevation = 12.dp,
//            modifier = Modifier.then(
//                if (size.width > 0 || size.height > 0) {
//
//                    val density: Density = LocalDensity.current
//
//
//                val newSize =    with(density) {
//                        size.width.coerceAtLeast(size.height).toDp()
//                    }
//
//                    Modifier.size(newSize)
//                }else {
//                    Modifier
//                }
//            )
//        ) {
//            Text(
//                text = "0",
//                fontSize = 34.sp,
//                onTextLayout = {textLayoutResult ->
//                   println("🔥 textLayoutResult: $textLayoutResult")
//                    size = textLayoutResult.size
//                },
//                modifier = Modifier.padding(4.dp),
//                textAlign = TextAlign.Center
//            )
//        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialContentPreview() {
    TutorialContent()
}