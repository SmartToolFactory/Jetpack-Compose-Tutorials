package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Composable
fun Tutorial3_1Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        StyleableTutorialText(
            text = "1-) Modifier.Element that makes content draw into a draw layer. " +
                    "The draw layer can be\n" +
                    " invalidated separately from parents. A **graphicsLayer** should be " +
                    "used when the content\n" +
                    " updates independently from anything above it to minimize the " +
                    "invalidated content.\n\n" +
                    " **graphicsLayer** can be used to apply effects to content, " +
                    "such as scaling, rotation, opacity,\n" +
                    "  shadow, and clipping."
        )
        TutorialText2(text = "Offset and Translate")
        OffsetAndTranslationExample()
        StyleableTutorialText(
            text = "2-) Changing scale via **Modifier.graphicsLayer{}** doesn't change " +
                    "the bounds of Composable."
        )
        GraphicsLayerExample()
        Spacer(modifier = Modifier.height(20.dp))
        StyleableTutorialText(
            text = "3-) When width is changed Composable's bounds also change."
        )
        WidthChangeExample()
        StyleableTutorialText(
            text = "4-) Scale changes are originated from center. To have same appearance as " +
                    "width change we can translate and make changes look originated start or " +
                    "end of a Composable."
        )
        TutorialText2(text = "Scale in end direction of Composable")
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            ScaledAndTranslateEndExample()
        }
        TutorialText2(text = "Scale in start direction of Composable")
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            ScaledAndTranslateStartExample()
        }
    }
}

@Composable
private fun OffsetAndTranslationExample() {
    val context = LocalContext.current

    var value by remember { mutableStateOf(0f) }

    Text("Offset/Translation: ${value.round2Digits()}")
    Slider(
        value = value,
        onValueChange = {
            value = it
        },
        valueRange = 0f..1000f
    )


    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(value.toInt(), 0)
                }
                .clickable {
                    Toast
                        .makeText(context, "One with offset is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                .zIndex(2f)
                .shadow(2.dp)
                .border(2.dp, Color.Green)
                .background(Orange400)
                .size(120.dp)
        )
        Box(
            modifier = Modifier
                .zIndex(1f)
                .background(Blue400)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Static composable is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
        )
    }

    Spacer(modifier = Modifier.height(40.dp))

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = value
                }
                .clickable {
                    Toast
                        .makeText(context, "One with offset is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                .zIndex(2f)
                .shadow(2.dp)
                .border(2.dp, Color.Green)
                .background(Orange400)
                .size(120.dp)
        )
        Box(
            modifier = Modifier
                .zIndex(1f)
                .background(Blue400)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Static composable is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
        )
    }
}

@Composable
private fun GraphicsLayerExample() {
    val context = LocalContext.current

    var offsetX by remember { mutableStateOf(0f) }

    Text("translationX: ${offsetX.round2Digits()}")
    Slider(
        value = offsetX,
        onValueChange = {
            offsetX = it
        },
        valueRange = 0f..1000f
    )

    var sclX by remember { mutableStateOf(1f) }

    Text("scaleX: ${sclX.round2Digits()}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0.3f..3f
    )

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = offsetX
                    scaleX = sclX
                    cameraDistance
                }
                .border(2.dp, Color.Green)
                .zIndex(2f)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Image is clicked", Toast.LENGTH_SHORT)
                        .show()
                },
            painter = painterResource(id = R.drawable.landscape1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .zIndex(1f)
                .background(Blue400)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Static composable is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
        )
    }
}

@Composable
private fun WidthChangeExample() {
    val context = LocalContext.current

    var offsetX by remember { mutableStateOf(0f) }

    Text("translationX: ${offsetX.round2Digits()}")
    Slider(
        value = offsetX,
        onValueChange = {
            offsetX = it
        },
        valueRange = 0f..1000f
    )

    var width by remember { mutableStateOf(100f) }

    Text("width: ${width.round2Digits()}dp")
    Slider(
        value = width,
        onValueChange = {
            width = it
        },
        valueRange = 0f..500f
    )

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .width(width.dp)
                .graphicsLayer {
                    translationX = offsetX
                }
                .border(2.dp, Color.Green)
                .zIndex(2f)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Image is clicked", Toast.LENGTH_SHORT)
                        .show()
                },
            painter = painterResource(id = R.drawable.landscape1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .zIndex(1f)
                .background(Blue400)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Static composable is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
        )
    }
}

@Composable
private fun ScaledAndTranslateEndExample() {
    val context = LocalContext.current

    var sclX by remember { mutableStateOf(1f) }
    var width by remember { mutableStateOf(0f) }

    Text("End Scale: ${(sclX.round2Digits())}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0f..10f
    )

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = (width * sclX - width) / 2
                    scaleX = sclX
                }
                .onSizeChanged {
                    width = it.width.toFloat()
                }
                .zIndex(2f)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Image is clicked", Toast.LENGTH_SHORT)
                        .show()
                },
            painter = painterResource(id = R.drawable.landscape1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun ScaledAndTranslateStartExample() {
    val context = LocalContext.current

    var sclX by remember { mutableStateOf(1f) }
    var width by remember { mutableStateOf(0f) }

    Text("Start Scale: ${(sclX.round2Digits())}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0f..10f
    )

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = (width - width * sclX) / 2
                    scaleX = sclX
                }
                .onSizeChanged {
                    width = it.width.toFloat()
                }
                .zIndex(2f)
                .size(120.dp)
                .clickable {
                    Toast
                        .makeText(context, "Image is clicked", Toast.LENGTH_SHORT)
                        .show()
                },
            painter = painterResource(id = R.drawable.landscape1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

private fun Float.round2Digits() = (this * 100).roundToInt() /100f

