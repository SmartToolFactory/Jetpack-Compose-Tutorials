package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_10_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        StyleableTutorialText(text = "Detect color at touch position on image.", bullets = false)
        TouchOnImageExample()
    }
}

@Composable
private fun TouchOnImageExample() {

    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape6
    )

    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var imageSize by remember { mutableStateOf(Size.Zero) }

    // These are for debugging
    var text by remember { mutableStateOf("") }
    var colorAtTouchPosition by remember { mutableStateOf(Color.Unspecified) }

    val imageModifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        // This is for displaying different ratio, optional
        .aspectRatio(4f / 3)
        .pointerInput(Unit) {
            detectTapGestures { offset: Offset ->
                // Touch coordinates on image
                offsetX = offset.x
                offsetY = offset.y

                // Scale from Image touch coordinates to range in Bitmap
                val scaledX = (bitmapWidth / imageSize.width) * offsetX
                val scaledY = (bitmapHeight / imageSize.height) * offsetY

                try {
                    val pixel: Int =
                        imageBitmap
                            .asAndroidBitmap()
                            .getPixel(scaledX.toInt(), scaledY.toInt())


                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)

                    text = "Image Touch offsetX:$offsetX, offsetY: $offsetY\n" +
                            "Image width: ${imageSize.width}, height: ${imageSize.height}\n" +
                            "Bitmap width: ${bitmapWidth}, height: $bitmapHeight\n" +
                            "scaledX: $scaledX, scaledY: $scaledY\n" +
                            "red: $red, green: $green, blue: $blue\n"

                    colorAtTouchPosition = Color(red, green, blue)
                } catch (e: Exception) {
                    println("Exception e: ${e.message}")
                }
            }
        }
        .onSizeChanged { imageSize = it.toSize() }

    Image(
        bitmap = imageBitmap,
        contentDescription = null,
        modifier = imageModifier.border(2.dp, Color.Red),
        contentScale = ContentScale.Crop
    )
    Text(text = text)
    Box(
        modifier = Modifier
            .then(
                if (colorAtTouchPosition.isUnspecified) {
                    Modifier
                } else {
                    Modifier.background(colorAtTouchPosition)
                }
            )
            .size(100.dp)
    )
}
