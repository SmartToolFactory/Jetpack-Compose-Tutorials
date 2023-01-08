package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun Tutorial5_11Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        ZoomableList(snacks)
    }
}

@Composable
private fun ZoomableList(snacks: List<Snack>) {

    var size by remember { mutableStateOf(IntSize.Zero) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),

        ) {
        itemsIndexed(items = snacks) { index, item ->

            Image(
                painter = rememberAsyncImagePainter(model = item.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .fillMaxWidth(.8f)
                    .aspectRatio(1f)
                    .clipToBounds()
                    .graphicsLayer {
                        val zoom = item.zoom.value
                        val offset = item.offset.value
//                        translationX = -offset.x * zoom
//                        translationY = -offset.y * zoom
                        scaleX = zoom
                        scaleY = zoom
                    }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Wait for at least one pointer to press down
                            awaitFirstDown()
                            do {

                                val event = awaitPointerEvent()
                                // Calculate gestures and consume pointerInputChange
                                // only size of pointers down is 2
                                if (event.changes.size == 2) {
                                    var zoom = item.zoom.value
                                    zoom *= event.calculateZoom()
                                    // Limit zoom between 100% and 300%
                                    zoom = zoom.coerceIn(1f, 3f)
                                    item.zoom.value = zoom

                                    val offset = event.calculatePan()
                                    val currentOffset = if (zoom == 1f) {
                                        Offset.Zero
                                    } else {
                                        val temp = item.offset.value + offset
                                        println("🔥Temp $temp, zoom: $zoom, size: $size")

                                        val scaledWidth = size.width / zoom
                                        val scaledHeight = size.height / zoom
                                        Offset(
                                            x = temp.x.coerceIn(
                                                -scaledWidth / 2,
                                                scaledWidth / 2
                                            ),
                                            y = temp.y.coerceIn(
                                                -scaledHeight / 2,
                                                scaledHeight / 2
                                            )
                                        )
                                    }

                                    item.offset.value = currentOffset
                                    /*
                                            Consumes position change if there is any
                                            This stops scrolling if there is one set to any parent Composable
                                         */
                                    event.changes.forEach { pointerInputChange: PointerInputChange ->
                                        pointerInputChange.consume()
                                    }
                                }
                            } while (event.changes.any { it.pressed })
                        }
                    }
                    .onSizeChanged {
                        size = it
                    }
            )
        }
    }
}


private val snacks = listOf(
    Snack(
        imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
    ),
    Snack(

        imageUrl = "https://source.unsplash.com/AHF_ZktTL6Q",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/rqFm0IgMVYY",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/qRE_OpbVPR8",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/33fWPnyN6tU",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/aX_ljOOyWJY",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/m741tj4Cz7M",
    ),
    Snack(

        imageUrl = "https://source.unsplash.com/iuwMdNq0-s4",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/qgWWQU1SzqM",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/9MzCd76xLGk",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/1d9xXWMtQzQ",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/wZxpOw84QTU",
    ),
    Snack(

        imageUrl = "https://source.unsplash.com/okzeRxm_GPo",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/l7imGdupuhU",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/bkXzABDt08Q",
    ),
    Snack(

        imageUrl = "https://source.unsplash.com/y2MeW00BdBo",
    ),
    Snack(
        imageUrl = "https://source.unsplash.com/1oMGgHn-M8k",
    ),
    Snack(

        imageUrl = "https://source.unsplash.com/TIGDsyy0TK4",
    )
)

private class Snack(
    val imageUrl: String
) {
    var zoom = mutableStateOf(1f)
    var offset = mutableStateOf(Offset.Zero)
}