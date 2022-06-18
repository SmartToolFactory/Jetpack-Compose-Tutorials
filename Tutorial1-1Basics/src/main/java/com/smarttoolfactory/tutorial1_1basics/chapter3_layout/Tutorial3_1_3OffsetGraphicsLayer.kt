package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
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
        Spacer(modifier = Modifier.height(40.dp))

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
                    "size and bounds of a Composable but it's **position in parent** " +
                    "might change based on where it's translated with scale."
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

        StyleableTutorialText(
            text = "5-) rotation variables of **Modifier.graphicsLayer{}** can be used to rotate" +
                    "a composable in respective axis."
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotationExample()
        }

        StyleableTutorialText(
            text = "6-) **TransformOrigin** determines which position transformation events" +
                    "such as scale or translate should be applied at."
        )
        TransformOriginExample()
    }
}

@Composable
private fun OffsetAndTranslationExample() {
    val context = LocalContext.current

    var value by remember { mutableStateOf(0f) }


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

    Spacer(modifier = Modifier.height(5.dp))
    Text("Offset/Translation: ${value.round2Digits()}")
    Slider(
        value = value,
        onValueChange = {
            value = it
        },
        valueRange = 0f..1000f
    )
}

@Composable
private fun GraphicsLayerExample() {
    val context = LocalContext.current

    var offsetX by remember { mutableStateOf(0f) }
    var sclX by remember { mutableStateOf(1f) }

    var textSize by remember { mutableStateOf("") }
    var textPosition by remember { mutableStateOf("") }

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = offsetX
                    scaleX = sclX
                }
                .border(2.dp, Color.Green)
                .zIndex(2f)
                .size(120.dp)
                // Order of clickable respect to graphicsLayer matters, in which
                // position clickable is and how it's dimensions are calculated
                // no matter what scale is touch position is corresponds to same value
                // end of image always returns same value as offset for instance
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Toast
                                .makeText(context, "Clicked position: $it", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
                .onSizeChanged {
                    textSize = "Size: $it\n"
                }
                .onGloballyPositioned {
                    textPosition =
                        "positionInParent: ${it.positionInParent()}\n" +
                                "positionInRoot: ${it.positionInRoot()}\n"
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

    Text(textSize + textPosition)

    Spacer(modifier = Modifier.height(5.dp))
    Text("translationX: ${offsetX.round2Digits()}")
    Slider(
        value = offsetX,
        onValueChange = {
            offsetX = it
        },
        valueRange = 0f..1000f
    )


    Text("scaleX: ${sclX.round2Digits()}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0.3f..3f
    )
}

@Composable
private fun WidthChangeExample() {
    val context = LocalContext.current

    var offsetX by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(100f) }

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

    Spacer(modifier = Modifier.height(5.dp))
    Text("translationX: ${offsetX.round2Digits()}")
    Slider(
        value = offsetX,
        onValueChange = {
            offsetX = it
        },
        valueRange = 0f..1000f
    )


    Text("width: ${width.round2Digits()}dp")
    Slider(
        value = width,
        onValueChange = {
            width = it
        },
        valueRange = 0f..500f
    )
}

@Composable
private fun ScaledAndTranslateEndExample() {
    val context = LocalContext.current

    var sclX by remember { mutableStateOf(1f) }
    var width by remember { mutableStateOf(0f) }

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

    Spacer(modifier = Modifier.height(5.dp))
    Text("End Scale: ${(sclX.round2Digits())}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0f..10f
    )
}

@Composable
private fun ScaledAndTranslateStartExample() {
    val context = LocalContext.current

    var sclX by remember { mutableStateOf(1f) }
    var width by remember { mutableStateOf(0f) }



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

    Spacer(modifier = Modifier.height(5.dp))
    Text("Start Scale: ${(sclX.round2Digits())}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0f..10f
    )
}

@Composable
private fun RotationExample() {
    val context = LocalContext.current
    var angleX by remember { mutableStateOf(0f) }
    var angleY by remember { mutableStateOf(0f) }
    var angleZ by remember { mutableStateOf(0f) }

    var camDistance by remember { mutableStateOf(5f) }


    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    rotationX = angleX
                    rotationY = angleY
                    rotationZ = angleZ
                    cameraDistance = camDistance
                }

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

    Spacer(modifier = Modifier.height(5.dp))
    Text("angleX: ${angleX.round2Digits()}")
    Slider(
        value = angleX,
        onValueChange = {
            angleX = it
        },
        valueRange = 0f..360f
    )

    Text("angleY: ${angleY.round2Digits()}")
    Slider(
        value = angleY,
        onValueChange = {
            angleY = it
        },
        valueRange = 0f..360f
    )

    Text("angleZ: ${angleZ.round2Digits()}")
    Slider(
        value = angleZ,
        onValueChange = {
            angleZ = it
        },
        valueRange = 0f..360f
    )

    Text("camDistance: ${camDistance.round2Digits()}")
    Slider(
        value = camDistance,
        onValueChange = {
            camDistance = it
        },
        valueRange = 0f..10f
    )
}

@Composable
private fun TransformOriginExample() {
    val context = LocalContext.current

    var sclX by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var angleX by remember { mutableStateOf(0f) }
    var angleY by remember { mutableStateOf(0f) }
    var angleZ by remember { mutableStateOf(0f) }

    var pivotFractionX by remember { mutableStateOf(0.5f) }

    Row(modifier = Modifier.border(2.dp, Color.Red)) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = offsetX
                    scaleX = sclX
                    rotationX = angleX
                    rotationY = angleY
                    rotationZ = angleZ
                    transformOrigin =
                        TransformOrigin(pivotFractionX = pivotFractionX, pivotFractionY = 0.5f)
                }
                .size(120.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Toast
                                .makeText(context, "Clicked position: $it", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                },
            painter = painterResource(id = R.drawable.landscape1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

    Spacer(modifier = Modifier.height(5.dp))
    Text("translationX: ${offsetX.round2Digits()}")
    Slider(
        value = offsetX,
        onValueChange = {
            offsetX = it
        },
        valueRange = 0f..1000f
    )

    Text("scaleX: ${sclX.round2Digits()}")
    Slider(
        value = sclX,
        onValueChange = {
            sclX = it
        },
        valueRange = 0.3f..3f
    )

    Text("angleX: ${angleX.round2Digits()}")
    Slider(
        value = angleX,
        onValueChange = {
            angleX = it
        },
        valueRange = 0f..360f
    )

    Text("angleY: ${angleY.round2Digits()}")
    Slider(
        value = angleY,
        onValueChange = {
            angleY = it
        },
        valueRange = 0f..360f
    )

    Text("angleZ: ${angleZ.round2Digits()}")
    Slider(
        value = angleZ,
        onValueChange = {
            angleZ = it
        },
        valueRange = 0f..360f
    )

    Text("pivotFractionX: ${(pivotFractionX.round2Digits())}")
    Slider(
        value = pivotFractionX,
        onValueChange = {
            pivotFractionX = it
        }
    )
}

private fun Float.round2Digits() = (this * 100).roundToInt() / 100f

