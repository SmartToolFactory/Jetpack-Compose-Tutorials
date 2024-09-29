package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R

@Preview
@Composable
fun RotateZoomDragBoxTest() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RotateZoomDragBox()
    }
}

@Composable
private fun RotateZoomDragBox() {

    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var centroid by remember { mutableStateOf(Offset.Zero) }

    val modifier = Modifier
        .border(3.dp, Color.Green)
        .fillMaxWidth()
        .aspectRatio(4 / 3f)
        .graphicsLayer {
            this.translationX = offset.x
            this.translationY = offset.y
            this.scaleX = zoom
            this.scaleY = zoom
            this.rotationZ = rotation
        }
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    rotation += gestureRotate
                    zoom *= gestureZoom
                    offset += gesturePan.rotateBy(rotation) * zoom
                    centroid = gestureCentroid
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Blue, center = centroid, radius = 20f)
        }

    Image(
        modifier = modifier.background(Color.Gray),
        painter = painterResource(id = R.drawable.landscape1),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}