package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Tutorial5_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) **detectTransformGestures** listens for centroid, center of touch pointers, " +
                    "zoom, pan and rotate gestures. Using these gestures in each step " +
                    "we add different actions to image to zoom, translate or rotate it."
        )
        TutorialText2(
            text = "Transform scale",
            modifier = Modifier.padding(top = 8.dp)
        )
        TransformGesturesZoomExample()
        TutorialText2(
            text = "Transform scale&translation",
            modifier = Modifier.padding(top = 20.dp)
        )
        TransformGesturesZoomPanExample()
        TutorialText2(
            text = "Transform scale, translation, and rotation",
            modifier = Modifier.padding(top = 20.dp)
        )
        TransformGesturesZoomPanRotateExample()

        TutorialText2(
            text = "Limit pan to image bounds",
            modifier = Modifier.padding(top = 20.dp)
        )
        LimitedPanImageExample()
    }
}

@Composable
private fun TransformGesturesZoomExample() {

    var centroid by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    val decimalFormat = remember { DecimalFormat("0.0") }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom in or out.\n" +
                    "Centroid is position of center of touch pointers"
        )
    }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, _, gestureZoom, _ ->
                    centroid = gestureCentroid
                    val newZoom = zoom * gestureZoom
                    zoom = newZoom.coerceIn(0.5f..5f)

                    transformDetailText = "Zoom: ${decimalFormat.format(zoom)}, centroid: $centroid"
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }

        .graphicsLayer {
            scaleX = zoom
            scaleY = zoom
        }

    ImageBox(boxModifier, imageModifier, R.drawable.landscape1, transformDetailText, Blue400)
}

@Composable
private fun TransformGesturesZoomPanExample() {

    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }

    var transformDetailText by remember {
        mutableStateOf("Use pinch gesture to zoom, and move image with single finger.")
    }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->

                    centroid = gestureCentroid

                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + centroid / oldScale).rotateBy(gestureRotate) -
                            (centroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)

                    transformDetailText = "Zoom: ${decimalFormat.format(zoom)}, centroid: $centroid"
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
        }

    ImageBox(boxModifier, imageModifier, R.drawable.landscape2, transformDetailText, Green400)
}

@Composable
private fun TransformGesturesZoomPanRotateExample() {
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }


    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }
    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = zoom * gestureZoom

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    zoom = newScale.coerceIn(0.5f..5f)
                    angle += gestureRotate

                    centroid = gestureCentroid
                    transformDetailText =
                        "Zoom: ${decimalFormat.format(zoom)}, centroid: $gestureCentroid\n" +
                                "angle: ${decimalFormat.format(angle)}, " +
                                "Rotate: ${decimalFormat.format(gestureRotate)}, pan: $gesturePan"
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(boxModifier, imageModifier, R.drawable.landscape3, transformDetailText)
}

@Composable
private fun LimitedPanImageExample() {

    var zoom by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    val imageModifier = boxModifier
        .pointerInput(Unit) {
            detectTransformGestures { _, panChange, zoomChange, _ ->

                zoom = (zoom * zoomChange).coerceIn(1f, 5f)


                val maxX = (size.width * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)
                val maxY = (size.height * (zoom - 1) / 2f)
                    .coerceAtLeast(0f)

                val newOffset = pan + panChange.times(zoom)

                // This for TransformOrigin(0.5, 0.5f)
                // For TransformOrigin(0f, 0f) it's  coerceIn(0f, 2*Max)
                pan = Offset(
                    newOffset.x.coerceIn(-maxX, maxX),
                    newOffset.y.coerceIn(-maxY, maxY)
                )
            }
        }
        .graphicsLayer {
            this.scaleX = zoom
            this.scaleY = zoom
            this.translationX = pan.x
            this.translationY = pan.y
        }

    Image(
        modifier = imageModifier,
        painter = painterResource(id = R.drawable.landscape5),
        contentScale = ContentScale.FillBounds,
        contentDescription = null
    )
}


@Composable
fun ImageBox(
    modifier: Modifier,
    imageModifier: Modifier,
    imageRes: Int,
    text: String,
    color: Color = Orange400
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(vertical = 2.dp)
                .align(Alignment.BottomStart)
        )
    }
}

val boxModifier = Modifier
    .fillMaxWidth()
    .height(250.dp)
    .clipToBounds()
    .background(Color.LightGray)

/**
 * Rotates the given offset around the origin by the given angle in degrees.
 *
 * A positive angle indicates a counterclockwise rotation around the right-handed 2D Cartesian
 * coordinate system.
 *
 * See: [Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)
 */
fun Offset.rotateBy(angle: Float): Offset {
    val angleInRadians = angle * PI / 180
    return Offset(
        (x * cos(angleInRadians) - y * sin(angleInRadians)).toFloat(),
        (x * sin(angleInRadians) + y * cos(angleInRadians)).toFloat()
    )
}

private const val PI = Math.PI
