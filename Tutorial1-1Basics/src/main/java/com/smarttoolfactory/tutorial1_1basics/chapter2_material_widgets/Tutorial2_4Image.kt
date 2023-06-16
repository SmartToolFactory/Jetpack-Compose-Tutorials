package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.FullWidthColumn
import com.smarttoolfactory.tutorial1_1basics.ui.components.FullWidthRow
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
@Composable
fun Tutorial2_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TutorialHeader(text = "Image")

            StyleableTutorialText(
                text = "1-) Image component lays out and draws a given  ImageBitmap, ImageVector," +
                        "or Painter."
            )

            ImageFromPainterExample()
            ImageFromVectorDrawableExample()
            ImageFromImageBitmapExample()

            StyleableTutorialText(
                text = "2-) With Canvas we can draw on a ImageBitmap and set ImageBitmap to an Image."
            )

            DrawOverImageBitmapExample()
            DrawOverImageBitmapExample2()

            StyleableTutorialText(
                text = """3-) With androidx.compose.ui.graphics.Canvas 
                    we can add a watermark on ImageBitmap and use this ImageBitmap for Image
                    or save it into a file.
                    """
            )
            DrawOnImageBitmapExample()

            StyleableTutorialText(
                text = "4-) Set shape or/and filter for the Image."
            )
            ImageShapeAndFilterExample()

            StyleableTutorialText(
                text = "5-) **graphicLayer** modifier to apply effects to content, such as scaling (scaleX, scaleY), rotation (rotationX, rotationY, rotationZ), opacity (alpha), shadow (shadowElevation, shape), and clipping (clip, shape)."
            )
            ImageGraphicLayer()

            StyleableTutorialText(
                text = "6-) **graphicLayer** alpha set to .99 and **blendMode** set on **drawImage** can be used to " +
                        "apply Porter Duff Modes to src and dst images"
            )
            ImageFromBlendMode()


            StyleableTutorialText(
                text = "7) Use Glide library to fetch an image resource from network and " +
                        "set it to Image component."
            )

            ImageDownloadWithGlideExample()

            StyleableTutorialText(
                text = "8) Use Coil library to fetch an image resource from network and " +
                        "set it to Image component."
            )
            ImageDownloadWithCoilExample()


            StyleableTutorialText(
                text = "9-) ContentScale represents a rule to apply to scale a source " +
                        "rectangle to be inscribed into a destination."
            )
            ImageContentScaleExample()
        }
    }
}

@Composable
private fun ImageFromPainterExample() {
    TutorialText2(text = "Image from painterResource")
    val painter: Painter = painterResource(id = R.drawable.landscape1)
    Image(painter, contentDescription = null)
}

@Composable
private fun ImageFromVectorDrawableExample() {
    TutorialText2(text = "Image from vector Drawable")
    FullWidthRow(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Painters from vector drawables
        val vectorRes1: Painter = painterResource(id = R.drawable.vd_clock_alarm)
        Image(vectorRes1, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes2: Painter = painterResource(id = R.drawable.vd_dashboard_active)
        Image(vectorRes2, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes3: Painter = painterResource(id = R.drawable.vd_home_active)
        Image(vectorRes3, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes4: Painter = painterResource(id = R.drawable.vd_notification_active)
        Image(vectorRes4, modifier = Modifier.size(60.dp), contentDescription = null)
    }
}

@Composable
fun ImageFromImageBitmapExample() {

    TutorialText2(text = "Image from ImageBitmap")

    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape2
    )

    Image(bitmap = imageBitmap, contentDescription = null)
}

@Composable
fun DrawOverImageBitmapExample() {

    TutorialText2(text = "Draw over ImageBitmap with Painter")

    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape3
    )

    val customPainter: Painter = object : Painter() {

        override val intrinsicSize: Size
            get() = Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

        override fun DrawScope.onDraw() {
            drawImage(imageBitmap)
            drawLine(
                color = Color.Red,
                start = Offset(0f, 0f),
                end = Offset(imageBitmap.width.toFloat(), imageBitmap.height.toFloat()),
                strokeWidth = 5f
            )
        }
    }

    Image(painter = customPainter, contentDescription = null)

}

@Composable
fun DrawOverImageBitmapExample2() {

    TutorialText2(text = "Draw over ImageBitmap with Canvas")

    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape3
    )

    val drawLambda: DrawScope.() -> Unit = {
        drawImage(imageBitmap)

        drawRoundRect(
            color = Color.Yellow,
            topLeft = Offset(imageBitmap.width / 4f, imageBitmap.height / 4f),
            style = Stroke(width = 5f),
            size = Size(imageBitmap.width / 2f, imageBitmap.height / 2f),
            cornerRadius = CornerRadius(5f)
        )

        val paint = android.graphics.Paint().apply {
            textSize = 50f
            color = Color.Red.toArgb()
        }

        // 🔥🔥 There is not a built-in function as of 1.0.0
        // for drawing text so we get the native canvas to draw text and use a Paint object

        drawContext.canvas.nativeCanvas.drawText(
            "Android",
            center.x,
            center.y,
            paint
        )
    }


    // 🔥 We get the exact Dp values using density for width and height of image which is in pixels
    val (widthInDp, heightInDp) =
        LocalDensity.current.run { Pair(imageBitmap.width.toDp(), imageBitmap.height.toDp()) }

    // 🔥 Used Stroke, instead of Fill for DrawStyle
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .background(Color.Green)
            .width(widthInDp)
            .height(heightInDp),
        onDraw = drawLambda
    )
}

@Composable
private fun DrawOnImageBitmapExample() {

    TutorialText2(text = "Draw on ImageBitmap and return it")

    val option = BitmapFactory.Options()
    option.apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
        inMutable = true
    }

    val imageBitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.landscape3,
        option
    ).asImageBitmap()

    // 🔥 This is a function that returns Canvas which can be used to draw on an
    // ImageBitmap that was sent as param. ImageBitmap that returned can be
    // be used to display on Image or can be saved to a physical file.

    val canvas: androidx.compose.ui.graphics.Canvas = Canvas(imageBitmap)

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 10f
            color = Color(0xff29B6F6)

        }
    }

    canvas.drawRect(0f, 0f, 200f, 200f, paint = paint)
    canvas.drawCircle(
        Offset(
            imageBitmap.width / 2 - 75f,
            imageBitmap.height / 2 + 75f
        ), 150.0f, paint
    )

    Image(bitmap = imageBitmap, contentDescription = null)

}

@Composable
private fun ImageShapeAndFilterExample() {

    val avatarBitmap1: Painter = painterResource(id = R.drawable.avatar_1_raster)
    val avatarBitmap2 = painterResource(id = R.drawable.avatar_2_raster)
    val avatarBitmap3 = painterResource(id = R.drawable.avatar_3_raster)
    val avatarBitmap4 = painterResource(id = R.drawable.avatar_4_raster)

    TutorialText2(text = "Shape")

    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp)),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(4.dp, CircleShape)
                .clip(CircleShape),
            painter = avatarBitmap2,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(5.dp, CutCornerShape(10.dp))
                .clip(CutCornerShape(10.dp)),
            painter = avatarBitmap3,
            contentDescription = null
        )

        // 🔥 Adding clip = true flag adds shadow and clips to shape
        Image(
            modifier = Modifier
                .shadow(2.dp, diamondShape, clip = true),
            painter = avatarBitmap4,
            contentDescription = null
        )
    }

    TutorialText2(text = "Color Filter")
    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Darken),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Lighten),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Difference),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(
                color = Color(0xffEEEEEE),
                blendMode = BlendMode.Saturation
            ),
            contentDescription = null
        )
    }
}

@Composable
private fun ImageFromBlendMode() {
    val imageBitmapSrc = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.composite_src
    )
    val imageBitmapDst = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.composite_dst
    )


    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            // Provide a slight opacity to for compositing into an
            // offscreen buffer to ensure blend modes are applied to empty pixel information
            // By default any alpha != 1.0f will use a compositing layer by default
            .graphicsLayer(alpha = 0.99f)
    ) {

        val dimension = (size.height.coerceAtMost(size.width) * .9f).toInt()

        // Images on left
        drawImage(
            image = imageBitmapDst,
            dstSize = IntSize(dimension, dimension)
        )
        drawImage(
            image = imageBitmapSrc,
            dstSize = IntSize(dimension, dimension),
            blendMode = BlendMode.SrcOut
        )

        // Images on right
        drawImage(
            image = imageBitmapDst,
            dstOffset = IntOffset((size.width / 2f).toInt(), 0),
            dstSize = IntSize(dimension, dimension)
        )
        drawImage(
            image = imageBitmapSrc,
            dstOffset = IntOffset((size.width / 2f).toInt(), 0),
            dstSize = IntSize(dimension, dimension),
            blendMode = BlendMode.DstOut
        )
    }
}

@Composable
private fun ImageGraphicLayer() {

    val avatarBitmap1: Painter = painterResource(id = R.drawable.avatar_1_raster)

    TutorialText2(text = "Rotate")

    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            modifier = Modifier
                .graphicsLayer(
                    rotationX = 45f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .graphicsLayer(
                    rotationY = 45f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = 45f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )

        Image(
            modifier = Modifier
                .graphicsLayer {
                    rotationX = 45f
                    rotationY = 45f
                    rotationZ = 45f
                },
            painter = avatarBitmap1,
            contentDescription = null
        )
    }

    TutorialText2(text = "Scale, Translate, Camera Distance")

    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = .8f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .graphicsLayer(
                    scaleY = .8f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .graphicsLayer(
                    translationX = 18f,
                    translationY = 18f
                ),
            painter = avatarBitmap1,
            contentDescription = null
        )

        Image(
            modifier = Modifier
                .graphicsLayer {
                    cameraDistance = .4f
                },
            painter = avatarBitmap1,
            contentDescription = null
        )
    }
}

@Composable
fun ImageDownloadWithGlideExample() {
    val url =
        "https://avatars3.githubusercontent.com/u/35650605?s=400&u=058086fd5c263f50f2fbe98ed24b5fbb7d437a4e&v=4"

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val sizeModifier = Modifier
        .fillMaxWidth()
    val context = LocalContext.current

    val glide = Glide.with(context)


    val target = object : CustomTarget<Bitmap>() {
        override fun onLoadCleared(placeholder: Drawable?) {
            imageBitmap = null
        }

        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
            imageBitmap = bitmap.asImageBitmap()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
        }

        override fun onLoadStarted(placeholder: Drawable?) {
            super.onLoadStarted(placeholder)
        }
    }

    glide
        .asBitmap()
        .load(url)
        .into(target)

    Column(
        modifier = sizeModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageBitmap?.let { imgBitmap ->
            // Image is a pre-defined composable that lays out and draws a given [ImageBitmap].
            Image(bitmap = imgBitmap, contentDescription = null)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ImageDownloadWithCoilExample() {

    val sizeModifier = Modifier
        .fillMaxWidth()

    val url =
        "https://avatars3.githubusercontent.com/u/35650605?s=400&u=058086fd5c263f50f2fbe98ed24b5fbb7d437a4e&v=4"

    Column(
        modifier = sizeModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier
                .height(180.dp),
            painter = rememberAsyncImagePainter(
                model = url
            ),
            contentDescription = null
        )

    }
}


@Composable
private fun ImageContentScaleExample() {

    val imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(4 / 3f)
        .background(Color.LightGray)


    val imageModifier2 = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .background(Color.LightGray)

    FullWidthColumn {

        val painter = painterResource(id = R.drawable.landscape10)

        TutorialText2(text = "Original")

        Image(painter = painter, contentDescription = null)

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )


        val painter2: Painter = painterResource(id = R.drawable.landscape5)

        TutorialText2(text = "Original")

        Image(
            painter = painter2, contentDescription = null
        )

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )
    }
}

private val diamondShape = GenericShape { size: Size, layoutDirection: LayoutDirection ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}


private val triangleShape = GenericShape { size: Size, layoutDirection: LayoutDirection ->
    val path = Path()
    path.apply {

        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(0f, size.height)
        lineTo(0f, 0f)
    }

    addPath(path = path)
}

