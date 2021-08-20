package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.*

@Composable
fun Tutorial2_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TutorialHeader(text = "Image")

            TutorialText(
                text = "1-) Image component lays out and draws a given  ImageBitmap, ImageVector," +
                        "or Painter."
            )

            ImageFromPainterExample()
            ImageFromVectorDrawableExample()
            ImageFromImageBitmapExample()

            TutorialText(
                text = "2-) With Canvas we can draw on a ImageBitmap and set ImageBitmap to an Image."
            )

            DrawOverImageBitmapExample()
            DrawOverImageBitmapExample2()

            TutorialText(
                text = """3-) With androidx.compose.ui.graphics.Canvas 
                    we can add a watermark on ImageBitmap and use this ImageBitmap for Image
                    or save it into a file.
                    """
            )
            DrawOnImageBitmapExample()

            TutorialText(
                text = "4-) Set shape or/and filter for the Image."
            )
            ImageShapeAndFilterExample()

            TutorialText(
                text = "5-) graphicLayer modifier to apply effects to content, such as scaling (scaleX, scaleY), rotation (rotationX, rotationY, rotationZ), opacity (alpha), shadow (shadowElevation, shape), and clipping (clip, shape)."
            )

            ImageGraphicLayer()

            TutorialText(
                text = "6) Use Glide library to fetch an image resource from network and " +
                        "set it to Image component."
            )

            ImageDownloadWithGlideExample()

            TutorialText(
                text = "6) Use Coil library to fetch an image resource from network and " +
                        "set it to Image component."
            )
            ImageDownloadWithCoilExample()


            TutorialText(
                text = "7-) ContentScale represents a rule to apply to scale a source " +
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
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.DstOver),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Color),
            contentDescription = null
        )
    }

    // TODO PorterDuff mode is not working properly, or i couldn't figure it out yet
    // Check out https://stackoverflow.com/questions/65653560/jetpack-compose-applying-porterduffmode-to-image
//    val imageBitmapSrc: ImageBitmap = imageFromResource(
//        LocalContext.current.resources,
//        R.drawable.composite_src
//    )
//    val imageBitmapDst: ImageBitmap = imageFromResource(
//        LocalContext.current.resources,
//        R.drawable.composite_dst
//    )
//
//    val size = Size(imageBitmapSrc.width.toFloat() * 2, imageBitmapSrc.height.toFloat() * 2)
//
//    val blendPainter = remember {
//        object : Painter() {
//
//            override val intrinsicSize: Size
//                get() = size
//
//            override fun DrawScope.onDraw() {
//
//                drawRect(color = Color.Green, blendMode = BlendMode.Clear)
//                drawImage(image = imageBitmapDst)
//                drawImage(image =imageBitmapDst, blendMode = BlendMode.Lighten)
//            }
//        }
//    }
//
//    Image(blendPainter, contentDescription = null)
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
            painter = rememberImagePainter(
                data = url
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

