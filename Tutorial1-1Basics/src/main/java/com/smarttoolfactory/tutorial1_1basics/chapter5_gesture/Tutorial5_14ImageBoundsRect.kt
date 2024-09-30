@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.launch
import java.util.UUID

@Preview
@Composable
fun Tutorial5_14Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val tabList = remember {
            listOf(
                "Markers",
                "Draw Lines",
                "Detect Color"
            )
        }
        val pagerState: PagerState = rememberPagerState {
            tabList.size
        }

        val coroutineScope = rememberCoroutineScope()

        TabRow(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        ) {
            // Add tabs for all of our pages
            tabList.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState
        ) { page: Int ->

            when (page) {
                0 -> ImageWithMarkersSample()
                1 -> ImageBoundsDrawSample()
                else -> TouchOnImageExample()
            }
        }
    }
}

@Preview
@Composable
fun ImageWithMarkersSample() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(R.drawable.landscape1)

    val markerList = rememberMarkerList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {

        Text("ContentScale: ContentScale.Fit, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            contentScale = ContentScale.Fit,
            markerList = markerList,
            imageBitmap = imageBitmap
        ) {}

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.FillBounds, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 2f),
            contentScale = ContentScale.FillBounds,
            markerList = markerList,
            imageBitmap = imageBitmap
        ) {}

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Fit, alignment: BottomEnd")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(5 / 3f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            markerList = markerList,
            imageBitmap = imageBitmap
        ) {}

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopCenter")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            markerList = markerList,
            imageBitmap = imageBitmap
        ) {}

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopStart")
        ImageWithMarkers(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart,
            markerList = markerList,
            imageBitmap = imageBitmap
        ) {}
    }
}

@Composable
private fun rememberMarkerList(): SnapshotStateList<Marker> {
    val markerList = remember {
        mutableStateListOf<Marker>()
            .apply {
                add(
                    Marker(
                        uid = "45224",
                        coordinateX = 10f,
                        coordinateY = 10f,
                        note = "Sample text 1"
                    )
                )
                add(
                    Marker(
                        uid = "6454",
                        coordinateX = 50f,
                        coordinateY = 50f,
                        note = "Sample text 2"
                    )
                )
                add(
                    Marker(
                        uid = "211111",
                        coordinateX = 200f,
                        coordinateY = 90f,
                        note = "Sample text 3"
                    )
                )
                add(
                    Marker(
                        uid = "21555",
                        coordinateX = 32f,
                        coordinateY = 93f,
                        note = "Sample text 4"
                    )
                )
            }
    }
    return markerList
}

@Composable
private fun ImageWithMarkers(
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    alignment: Alignment = Alignment.Center,
    markerList: SnapshotStateList<Marker>,
    imageBitmap: ImageBitmap,
    onClick: (Marker) -> Unit,
) {

    var imageProperties by remember {
        mutableStateOf(ImageProperties.Zero)
    }

    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {

        Image(
            modifier = modifier
                .drawWithContent {
                    drawContent()
                    val drawAreaRect = imageProperties.drawAreaRect

                    // This is for displaying area that bitmap is drawn in Image Composable
                    drawRect(
                        color = Color.Green,
                        topLeft = drawAreaRect.topLeft,
                        size = drawAreaRect.size,
                        style = Stroke(
                            4.dp.toPx(),
                        )
                    )
                }
                .pointerInput(contentScale) {
                    detectTapGestures { offset: Offset ->

                        val drawAreaRect = imageProperties.drawAreaRect
                        val isTouchInImage = drawAreaRect.contains(offset)

                        if (isTouchInImage) {
                            val bitmapRect = imageProperties.bitmapRect

                            val offsetOnBitmap = scaleFromScreenToBitmapPosition(
                                offsetScreen = offset,
                                bitmapRect = bitmapRect,
                                drawAreaRect = drawAreaRect,
                                scaleFactor = imageProperties.scaleFactor
                            )

                            markerList.add(
                                Marker(
                                    UUID.randomUUID().toString(),
                                    offsetOnBitmap.x,
                                    offsetOnBitmap.y
                                )
                            )
                        }
                    }
                }
                .onSizeChanged {
                    val imageSize = it
                    val dstSize: Size = imageSize.toSize()
                    val srcSize =
                        Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

                    imageProperties = calculateImageDrawProperties(
                        srcSize = srcSize,
                        dstSize = dstSize,
                        contentScale = contentScale,
                        alignment = alignment
                    )
                },
            bitmap = imageBitmap,
            contentScale = contentScale,
            alignment = alignment,
            contentDescription = null
        )

        ShapesOnImage(
            list = markerList,
            imageProperties = imageProperties,
            onClick = onClick
        )
    }
}

@Composable
fun ShapesOnImage(
    list: List<Marker>,
    imageProperties: ImageProperties,
    onClick: (Marker) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    if (imageProperties != ImageProperties.Zero) {
        list.forEachIndexed { index, marker ->

            val tooltipState = rememberTooltipState(isPersistent = true)

            val provider = rememberPlainTooltipPositionProvider(
                spacingBetweenTooltipAndAnchor = 10.dp
            )

            val offsetOnScreen = scaleFromBitmapToScreenPosition(
                offsetBitmap = Offset(
                    marker.coordinateX,
                    marker.coordinateY
                ),
                drawAreaRect = imageProperties.drawAreaRect,
                bitmapRect = imageProperties.bitmapRect,
                scaleFactor = imageProperties.scaleFactor
            )

            Box(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        val width = placeable.width
                        val height = placeable.height
                        val xOnScreen = offsetOnScreen.x
                        val yOnScreen = offsetOnScreen.y

                        val xPos = (xOnScreen - width / 2).toInt()
                        val yPos = (yOnScreen - height / 2).toInt()

                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(xPos, yPos)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {

                TooltipBox(
                    state = tooltipState,
                    positionProvider = provider,
                    tooltip = {
                        PlainTooltip(
                            caretProperties = CaretProperties(
                                caretWidth = 8.dp,
                                caretHeight = 8.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            containerColor = Color.Red
                        ) {
                            Text(
                                text = " On bitmap at " +
                                        "x: ${marker.coordinateX.toInt()}, " +
                                        "y: ${marker.coordinateY.toInt()}",
                                color = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    },
                    content = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(20.dp)
                                .background(Color.Red, CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        val isVisible = tooltipState.isVisible
                                        if (isVisible) {
                                            tooltipState.dismiss()
                                        } else {
                                            tooltipState.show()
                                        }
                                    }

                                    onClick(marker)
                                }
                        ) {
                            Text("${index + 1}", color = Color.White)
                        }
                    }
                )

            }
        }
    }
}

data class Marker(
    val uid: String,
    val coordinateX: Float,
    val coordinateY: Float,
    val note: String = "",
)

@Preview
@Composable
fun ContentScaleComputeFactorTest() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val painter = painterResource(R.drawable.landscape6)


        val contentScale = ContentScale.Fit

        var dstSize by remember {
            mutableStateOf(IntSize.Zero)
        }

        var text by remember {
            mutableStateOf("")
        }

        var scaleFactor by remember {
            mutableStateOf(ScaleFactor(1f, 1f))
        }
        Text(text = text)

        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(1f)
                .onSizeChanged {
                    dstSize = it
                    scaleFactor = contentScale.computeScaleFactor(
                        srcSize = painter.intrinsicSize,
                        dstSize = dstSize.toSize()
                    )

                    text = "Src size: ${painter.intrinsicSize}\n" +
                            "dstSize: $dstSize\n" +
                            "scaleFactor: $scaleFactor"

                },
            painter = painter,
            contentScale = contentScale,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun ImageBoundsDrawSample() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {

        val pointsOnBitmap = remember {
            mutableStateListOf<Offset>()
        }

        val painter = painterResource(R.drawable.landscape10)

        Text("ContentScale: ContentScale.Fit, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            contentScale = ContentScale.Fit,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.FillBounds, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 2f),
            contentScale = ContentScale.FillBounds,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Fit, alignment: BottomEnd")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(5 / 3f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopCenter")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("ContentScale: ContentScale.Crop, alignment: TopStart")
        ImageWithBounds(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(3 / 4f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart,
            pointsOnBitmap = pointsOnBitmap,
            painter = painter
        ) { pointOnBitmap: Offset ->
            pointsOnBitmap.add(pointOnBitmap)
        }
    }
}


@Composable
private fun ImageWithBounds(
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    alignment: Alignment = Alignment.Center,
    painter: Painter,
    pointsOnBitmap: List<Offset>,
    onClick: (positionOnBitmap: Offset) -> Unit,
) {

    var imageProperties by remember {
        mutableStateOf(ImageProperties.Zero)
    }


    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {

        Image(
            modifier = modifier
                .drawWithContent {
                    drawContent()

                    val drawAreaRect = imageProperties.drawAreaRect
                    val bitmapRect = imageProperties.bitmapRect
                    val scaleFactor = imageProperties.scaleFactor

                    // This is for displaying area that bitmap is drawn in Image Composable
                    drawRect(
                        color = Color.Green,
                        topLeft = drawAreaRect.topLeft,
                        size = drawAreaRect.size,
                        style = Stroke(
                            4.dp.toPx(),
                        )
                    )

                    val size = pointsOnBitmap.size
                    pointsOnBitmap.forEachIndexed { index: Int, offset: Offset ->

                        val scaledCurrentOffset = scaleFromBitmapToScreenPosition(
                            offsetBitmap = offset,
                            drawAreaRect = drawAreaRect,
                            bitmapRect = bitmapRect,
                            scaleFactor = scaleFactor
                        )
                        if (size > 1 && index > 0) {

                            val scaledPreviousOffset = scaleFromBitmapToScreenPosition(
                                offsetBitmap = pointsOnBitmap[index - 1],
                                drawAreaRect = drawAreaRect,
                                bitmapRect = bitmapRect,
                                scaleFactor = scaleFactor
                            )

                            drawLine(
                                color = Color.Blue,
                                start = scaledPreviousOffset,
                                end = scaledCurrentOffset,
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        drawCircle(
                            color = Color.Red,
                            center = scaledCurrentOffset,
                            radius = 6.dp.toPx()
                        )
                    }
                }
                .pointerInput(contentScale) {
                    detectTapGestures { offset: Offset ->

                        val bitmapRect = imageProperties.bitmapRect
                        val drawAreaRect = imageProperties.drawAreaRect
                        val isTouchInImage = drawAreaRect.contains(offset)

                        if (isTouchInImage) {

                            val offsetOnBitmap = scaleFromScreenToBitmapPosition(
                                offsetScreen = offset,
                                bitmapRect = bitmapRect,
                                drawAreaRect = drawAreaRect,
                                scaleFactor = imageProperties.scaleFactor
                            )

                            onClick(offsetOnBitmap)
                        }
                    }
                }
                .onSizeChanged {
                    val imageSize = it
                    val dstSize: Size = imageSize.toSize()

                    val srcSize = painter.intrinsicSize

                    imageProperties = calculateImageDrawProperties(
                        srcSize = srcSize,
                        dstSize = dstSize,
                        contentScale = contentScale,
                        alignment = alignment
                    )
                },
            painter = painter,
            contentScale = contentScale,
            alignment = alignment,
            contentDescription = null
        )

    }
}