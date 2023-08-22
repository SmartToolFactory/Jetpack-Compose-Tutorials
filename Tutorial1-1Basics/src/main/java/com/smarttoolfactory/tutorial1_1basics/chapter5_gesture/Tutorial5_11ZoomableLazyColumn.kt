package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Preview
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
        ZoomableList(ZoomableSnacks)
    }
}

@Composable
private fun ZoomableList(snackList: List<ZoomableSnack>) {

    var size by remember { mutableStateOf(IntSize.Zero) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),

        ) {
        itemsIndexed(items = snackList) { index: Int, item: ZoomableSnack ->

            Image(
                painter = rememberAsyncImagePainter(model = item.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .fillMaxWidth(.9f)
                    .aspectRatio(1f)
//                    .clipToBounds()
                    .graphicsLayer {
                        val zoom = item.zoom.value
                        val offset = item.offset.value
                        translationX = offset.x
                        translationY = offset.y
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
                                var zoom = item.zoom.value
                                zoom *= event.calculateZoom()
                                // Limit zoom between 100% and 300%
                                zoom = zoom.coerceIn(1f, 3f)

                                item.zoom.value = zoom

                                val pan = event.calculatePan()

                                val currentOffset = if (zoom == 1f) {
                                    Offset.Zero
                                } else {

                                    // This is for limiting pan inside Image bounds
                                    val temp = item.offset.value + pan.times(zoom)
                                    val maxX = (size.width * (zoom - 1) / 2f)
                                    val maxY = (size.height * (zoom - 1) / 2f)

                                    Offset(
                                        temp.x.coerceIn(-maxX, maxX),
                                        temp.y.coerceIn(-maxY, maxY)
                                    )
                                }

                                item.offset.value = currentOffset

                                // When image is zoomed consume event and prevent scrolling
                                if (zoom > 1f) {
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


private val ZoomableSnacks = listOf(
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
    ),
    ZoomableSnack(

        imageUrl = "https://source.unsplash.com/AHF_ZktTL6Q",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/rqFm0IgMVYY",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/qRE_OpbVPR8",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/33fWPnyN6tU",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/aX_ljOOyWJY",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/m741tj4Cz7M",
    ),
    ZoomableSnack(

        imageUrl = "https://source.unsplash.com/iuwMdNq0-s4",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/qgWWQU1SzqM",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/9MzCd76xLGk",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/1d9xXWMtQzQ",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/wZxpOw84QTU",
    ),
    ZoomableSnack(

        imageUrl = "https://source.unsplash.com/okzeRxm_GPo",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/l7imGdupuhU",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/bkXzABDt08Q",
    ),
    ZoomableSnack(

        imageUrl = "https://source.unsplash.com/y2MeW00BdBo",
    ),
    ZoomableSnack(
        imageUrl = "https://source.unsplash.com/1oMGgHn-M8k",
    ),
    ZoomableSnack(

        imageUrl = "https://source.unsplash.com/TIGDsyy0TK4",
    )
)

private class ZoomableSnack(
    val imageUrl: String
) {
    var zoom = mutableStateOf(1f)
    var offset = mutableStateOf(Offset.Zero)
}