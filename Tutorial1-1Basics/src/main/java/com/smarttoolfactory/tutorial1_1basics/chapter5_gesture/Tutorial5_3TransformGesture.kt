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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.ZoomableImage
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.rememberZoomableState
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import java.text.DecimalFormat
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

        TutorialText2(
            text = "Default Zoom, Pan, Rotation",
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(20.dp))
        TransformGesturesZoomExample()


        TutorialText2(
            text = "Transform scale, translation, and rotation",
            modifier = Modifier.padding(top = 20.dp)
        )
        Spacer(Modifier.height(20.dp))
        TransformGesturesZoomPanRotateExample()
        Spacer(Modifier.height(20.dp))

        TutorialText2(
            text = "ZoomableImage",
            modifier = Modifier.padding(top = 20.dp)
        )

        ZoomableImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            coroutineScope = rememberCoroutineScope(),
            zoomableState = rememberZoomableState(),
            dragGesturesEnabled = {
                false
            },
            painter = painterResource(id = R.drawable.landscape11)
        )

        Spacer(Modifier.height(20.dp))
        TutorialText2(
            text = "Alternative Zoom",
            modifier = Modifier.padding(top = 20.dp)
        )
        AlternativeTransformExampleExample()
        Spacer(Modifier.height(50.dp))

    }
}

@Composable
private fun TransformGesturesZoomExample() {

    var centroid by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    val decimalFormat = remember { DecimalFormat("0.0") }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

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
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->

                    centroid = gestureCentroid

                    val newZoom = zoom * gestureZoom
                    zoom = newZoom.coerceIn(0.5f..5f)

                    offset += gesturePan.times(zoom)
                    angle += gestureRotate

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
            translationX = offset.x
            translationY = offset.y
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
        }

    ImageBox(boxModifier, imageModifier, R.drawable.landscape11, transformDetailText, Blue400)
}


@Composable
private fun TransformGesturesZoomPanRotateExample() {
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->
                    val oldScale = zoom
                    val newScale = (zoom * gestureZoom).coerceIn(0.5f..5f)

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale).rotateBy(gestureRotate) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    angle += gestureRotate
                    zoom = newScale
                }
            )
        }

        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            rotationZ = angle
            TransformOrigin(0f, 0f).also { transformOrigin = it }
        }

    ImageBox(boxModifier, imageModifier, R.drawable.landscape11, "")
}

@Composable
private fun AlternativeTransformExampleExample() {
    val decimalFormat = remember { DecimalFormat("0.0") }

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var transformOffset by remember { mutableStateOf(Offset.Zero) }
    var difference by remember { mutableStateOf(Offset.Zero) }

    var centroid by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }


    var composableCenter by remember { mutableStateOf(Offset.Zero) }

    var transformDetailText by remember {
        mutableStateOf(
            "Use pinch gesture to zoom, move image with single finger in either x or y coordinates.\n" +
                    "Rotate image using two fingers with twisting gesture."
        )
    }
    val imageModifier = Modifier
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotate ->

                    val rotationChange = gestureRotate
                    val tempOffset = offset + gesturePan

                    centroid = gestureCentroid

                    val x0 = centroid.x - composableCenter.x
                    val y0 = centroid.y - composableCenter.y

                    val hyp0 = sqrt(x0 * x0 + y0 * y0)
                    val hyp1 = zoom * hyp0 * (if (x0 > 0) {
                        1f
                    } else {
                        -1f
                    })

                    val alpha0 = atan(y0 / x0)

                    val alpha1 = alpha0 + (rotationChange * ((2 * kotlin.math.PI) / 360))

                    val x1 = cos(alpha1) * hyp1
                    val y1 = sin(alpha1) * hyp1

                    transformOffset =
                        gestureCentroid - (composableCenter - tempOffset) - Offset(
                            x1.toFloat(),
                            y1.toFloat()
                        )

                    difference = transformOffset - offset

                    zoom *= gestureZoom
                    angle += gestureRotate
                    centroid = gestureCentroid

                    transformDetailText =
                        "gestureCentroid: $gestureCentroid, offset: $offset, tempOffset: $tempOffset\n" +
                                "transformOffset: $transformOffset, difference: $difference\n" +
                                "angle: ${decimalFormat.format(angle)}, pan: $gesturePan"


                    println(
                        "🍎 Composable gestureCentroid: $gestureCentroid, composableCenter: $composableCenter\n" +
                                "Offset: ${offset}, tempOffset: $tempOffset\n" +
                                "transformOffset: $transformOffset, difference: $difference, zoomChange: $zoom"
                    )

                    offset += difference
                }
            )
        }
        .drawWithContent {
            drawContent()
            drawCircle(color = Color.Red, center = centroid, radius = 20f)
            drawCircle(color = Color.Green, center = composableCenter, radius = 10f)
            drawCircle(color = Color.Magenta, center = transformOffset, radius = 10f)
        }


    Text(transformDetailText)
    Box(imageModifier) {

        Box(Modifier
            .clip(RectangleShape)
//            .offset {
//                IntOffset(offset.x.toInt(), offset.y.toInt())
//            }
            .clip(RectangleShape)
            .onGloballyPositioned { coordinates ->
                val localOffset =
                    Offset(
                        coordinates.size.width.toFloat() / 2,
                        coordinates.size.height.toFloat() / 2
                    )
                val windowOffset = coordinates.localToWindow(localOffset)
                composableCenter =
                    coordinates.parentLayoutCoordinates?.windowToLocal(windowOffset)
                        ?: Offset.Zero


//                println("🥮 CENTER: $composableCenter")
            }
            .graphicsLayer {
//            translationX = difference.x
//            translationY = difference.y
                scaleX = zoom
                scaleY = zoom
                rotationZ = angle
            }
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                painter = painterResource(id = R.drawable.landscape11),
                contentDescription = null
            )
        }
    }
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
    .height(350.dp)
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
