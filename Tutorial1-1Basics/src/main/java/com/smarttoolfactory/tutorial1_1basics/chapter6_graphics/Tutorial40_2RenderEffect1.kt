package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.CanvasHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntSize
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import org.intellij.lang.annotations.Language
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial6_40Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.padding(8.dp)) {
        TutorialHeader(text = "RenderEffect")
        StyleableTutorialText(
            text = "Blue Composables using **RenderEffect.createBlurEffect** with varying values " +
                    "and with different edgeTreatments.",
            bullets = false
        )

        RenderEffectBlurSample()
    }
}

@Preview
@Composable
private fun RenderEffectBlurSample() {

    var blurRadiusX by remember {
        mutableFloatStateOf(10f)
    }

    var blurRadiusY by remember {
        mutableFloatStateOf(10f)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            TutorialText2(
                text = "Shader.TileMode.MIRROR",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.MIRROR
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.DECAL",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.DECAL
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.CLAMP",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.REPEAT",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.REPEAT
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )


            Text("Blur radiusX: ${blurRadiusX.roundToInt()}")

            Slider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = blurRadiusX,
                onValueChange = {
                    blurRadiusX = it
                },
                valueRange = 0.01f..25f
            )

            Text("Blur radiusY: ${blurRadiusY.roundToInt()}")

            Slider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = blurRadiusY,
                onValueChange = {
                    blurRadiusY = it
                },
                valueRange = 0.01f..25f
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun GradientShaderBrushSample() {

    val COLOR_SHADER_SRC =
        """uniform float2 iResolution;
   half4 main(float2 fragCoord) {
      float2 scaled = fragCoord/iResolution.xy;
      return half4(scaled, 0, 1);
   }"""

    Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(4 / 3f).border(2.dp, Color.Red)) {
        val colorShader = RuntimeShader(COLOR_SHADER_SRC)

        colorShader.setFloatUniform("iResolution", size.width, size.height)
        val shaderBrush = ShaderBrush(colorShader)
        drawCircle(brush = shaderBrush)
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun ImageRenderEffectSample() {
    val hueShader = remember {
        RuntimeShader(
            """
    uniform float2 iResolution;       // Viewport resolution (pixels)
    uniform float2 iImageResolution;  // iImage1 resolution (pixels)
    uniform float iRadian;            // radian to rotate things around
    uniform shader iImage1;           // An input image
    half4 main(float2 fragCoord) {
    float cosR = cos(iRadian);
    float sinR = sin(iRadian);
        mat4 hueRotation =
        mat4 (
                0.299 + 0.701 * cosR + 0.168 * sinR, //0
                0.587 - 0.587 * cosR + 0.330 * sinR, //1
                0.114 - 0.114 * cosR - 0.497 * sinR, //2
                0.0,                                 //3
                0.299 - 0.299 * cosR - 0.328 * sinR, //4
                0.587 + 0.413 * cosR + 0.035 * sinR, //5
                0.114 - 0.114 * cosR + 0.292 * sinR, //6
                0.0,                                 //7
                0.299 - 0.300 * cosR + 1.25 * sinR,  //8
                0.587 - 0.588 * cosR - 1.05 * sinR,  //9
                0.114 + 0.886 * cosR - 0.203 * sinR, //10
                0.0,                                 //11
                0.0, 0.0, 0.0, 1.0 );                //12,13,14,15
        float2 scale = iImageResolution.xy / iResolution.xy;
        return iImage1.eval(fragCoord * scale)*hueRotation;
    }
"""
        )
    }

    val bitmap = ImageBitmap.imageResource(R.drawable.avatar_1_raster)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        hueShader.setFloatUniform(
            "iImageResolution", bitmap.width.toFloat(),
            bitmap.height.toFloat()
        )
        hueShader.setFloatUniform(
            "iResolution", bitmap.width.toFloat(),
            bitmap.height.toFloat()
        )
        hueShader.setFloatUniform("iRadian", 20f)
        hueShader.setInputShader(
            "iImage1", BitmapShader(
                bitmap.asAndroidBitmap(), Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR
            )
        )
        Canvas(
            modifier = Modifier
                .border(2.dp, Color.Green)
                .clipToBounds()
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .graphicsLayer {
                    renderEffect = RenderEffect.createShaderEffect(hueShader)
                        .asComposeRenderEffect()
                }
        ) {
            drawImage(image = bitmap)
        }

    }
}

//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Language("GLSL")
//val FROSTED_GLASS_SHADER = RuntimeShader(
//    """
//    uniform shader inputShader;
//    uniform float height;
//    uniform float width;
//
//    vec4 main(vec2 coords) {
//        vec4 currValue = inputShader.eval(coords);
//        float top = height - 100;
//        if (coords.y < top) {
//            return currValue;
//        } else {
//            // Avoid blurring edges
//            if (coords.x > 1 && coords.y > 1 &&
//                    coords.x < (width - 1) &&
//                    coords.y < (height - 1)) {
//                // simple box blur - average 5x5 grid around pixel
//                vec4 boxSum =
//                    inputShader.eval(coords + vec2(-2, -2)) +
//                    // ...
//                    currValue +
//                    // ...
//                    inputShader.eval(coords + vec2(2, 2));
//                currValue = boxSum / 25;
//            }
//
//            const vec4 white = vec4(1);
//            // top-left corner of label area
//            vec2 lefttop = vec2(0, top);
//            float lightenFactor = min(1.0, .6 *
//                    length(coords - lefttop) /
//                    (0.85 * length(vec2(width, 100))));
//            // White in upper-left, blended increasingly
//            // toward lower-right
//            return mix(currValue, white, 1 - lightenFactor);
//        }
//    }
//"""
//)
//
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Preview
//@Composable
//fun FrostedGlassSample() {
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Image(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(4 / 3f),
//            painter = painterResource(R.drawable.landscape11),
//            contentDescription = null,
//            contentScale = ContentScale.FillBounds
//        )
//
//        Image(
//            modifier = Modifier
//                .fillMaxWidth()
//                .border(2.dp, Color.Red)
//                .aspectRatio(4 / 3f)
//                .graphicsLayer {
//                    val width = size.width
//                    val height = size.height
//
//                    FROSTED_GLASS_SHADER.setFloatUniform("width", width)
//                    FROSTED_GLASS_SHADER.setFloatUniform("height", height)
//
//                    val blur = RenderEffect.createBlurEffect(5f, 5f, Shader.TileMode.CLAMP)
//
//                    val effect = RenderEffect.createRuntimeShaderEffect(
//                        FROSTED_GLASS_SHADER, "inputShader"
//                    )
//
//                    renderEffect =
//                        RenderEffect.createChainEffect(blur, effect).asComposeRenderEffect()
//                },
//            painter = painterResource(R.drawable.landscape11),
//            contentDescription = null,
//            contentScale = ContentScale.FillBounds
//        )
//
//    }
//}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun GlassEffectSample() {

    val density = LocalDensity.current.density

    Canvas(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(1.0f)
            .graphicsLayer {
                val shader = RuntimeShader(compositeSksl)
                shader.setFloatUniform(
                    "rectangle",
                    85.0f * density, 110.0f * density, 405.0f * density, 290.0f * density
                )
                shader.setFloatUniform("radius", 20.0f * density)
                // What should i set for content and blur for shaders?

            }
    ) {
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF7A26D9), Color(0xFFE444E1)),
                start = Offset(450.dp.toPx(), 60.dp.toPx()),
                end = Offset(290.dp.toPx(), 190.dp.toPx()),
                tileMode = TileMode.Clamp
            ),
            center = Offset(375.dp.toPx(), 125.dp.toPx()),
            radius = 100.dp.toPx()
        )
        drawCircle(
            color = Color(0xFFEA357C),
            center = Offset(100.dp.toPx(), 265.dp.toPx()),
            radius = 55.dp.toPx()
        )
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFEA334C), Color(0xFFEC6051)),
                start = Offset(180.dp.toPx(), 125.dp.toPx()),
                end = Offset(230.dp.toPx(), 125.dp.toPx()),
                tileMode = TileMode.Clamp
            ),
            center = Offset(205.dp.toPx(), 125.dp.toPx()),
            radius = 25.dp.toPx()
        )
    }
}

// Recreate visuals from https://uxmisfit.com/2021/01/13/how-to-create-glassmorphic-card-ui-design/
@Language("GLSL")
val compositeSksl = """
    uniform shader content;
    uniform shader blur;

    uniform vec4 rectangle;
    uniform float radius;

    // Simplified version of SDF (signed distance function) for a rounded box
    // from https://www.iquilezles.org/www/articles/distfunctions2d/distfunctions2d.htm
    float roundedRectangleSDF(vec2 position, vec2 box, float radius) {
        vec2 q = abs(position) - box + vec2(radius);
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;
    }

    vec4 main(vec2 coord) {
        vec2 shiftRect = (rectangle.zw - rectangle.xy) / 2.0;
        vec2 shiftCoord = coord - rectangle.xy;
        float distanceToClosestEdge = roundedRectangleSDF(
            shiftCoord - shiftRect, shiftRect, radius);

        vec4 c = content.eval(coord);
        if (distanceToClosestEdge > 0.0) {
            // We're outside of the filtered area
            return c;
        }

        vec4 b = blur.eval(coord);
        return b;
    }
"""

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun RenderNodeSample() {

    val contentNode = remember {
        RenderNode("contentNode")
    }

    val topAppbarHeight = 180.dp
    val canvasHolder: CanvasHolder = remember {
        CanvasHolder()
    }

    val graphicsLayer = rememberGraphicsLayer()

    val state = rememberLazyListState()

    Box {
        LazyColumn(
            state = state,
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
                .drawWithContent {

                    clipRect(
                        top = topAppbarHeight.toPx()
                    ) {
                        this@drawWithContent.drawContent()
                    }

                    graphicsLayer.record(
                        size = IntSize(size.width.toInt(), topAppbarHeight.roundToPx())
                    ) {
                        this@drawWithContent.drawContent()
                    }
                },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(topAppbarHeight))
            }

            items(100) {

                if (it == 5) {
                    Image(
                        modifier = Modifier.fillMaxWidth().aspectRatio(2f),
                        painter = painterResource(R.drawable.landscape11),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("Row $it", fontSize = 22.sp)
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()

        ) {
            Canvas(
                modifier = Modifier
                    .drawWithContent {
                        drawContent()
                        drawRect(color = Color.White.copy(alpha = .5f))
                    }
                    .fillMaxWidth()
                    .height(topAppbarHeight)
            ) {
                contentNode.setPosition(0, 0, size.width.toInt(), size.height.toInt())
                contentNode.setRenderEffect(
                    RenderEffect.createBlurEffect(
                        15f, 15f,
                        Shader.TileMode.CLAMP
                    )
                )

                drawIntoCanvas { canvas: Canvas ->
                    val recordingCanvas = contentNode.beginRecording()
                    canvasHolder.drawInto(recordingCanvas) {
                        drawContext.also {
                            it.layoutDirection = layoutDirection
                            it.size = size
                            it.canvas = this
                        }
                        drawLayer(graphicsLayer)
                    }

                    contentNode.endRecording()

                    canvas.nativeCanvas.drawRenderNode(contentNode)
                }
            }

            Text(
                "Glass Blur",
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                fontSize = 26.sp
            )
        }
    }
}

fun Modifier.frostedGlass(height: Dp) = this.then(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.drawWithCache {
            val topBarHeightPx = height.toPx()

            val blurLayer = obtainGraphicsLayer().apply {
                renderEffect = RenderEffect
                    .createBlurEffect(16f, 16f, Shader.TileMode.DECAL)
                    .asComposeRenderEffect()
            }

            onDrawWithContent {
                blurLayer.record(size.copy(height = topBarHeightPx).roundToIntSize()) {
                    this@onDrawWithContent.drawContent()
                }

                drawLayer(blurLayer)
                clipRect(top = topBarHeightPx) {
                    this@onDrawWithContent.drawContent()
                }
            }
        }
    } else {
        Modifier
    }
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun PartialBlurTest() {

    val scrollState = rememberLazyListState()
    val topBarHeight = 180.dp

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .frostedGlass(height = topBarHeight),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(topBarHeight))
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth().aspectRatio(2f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(5) {
                    Image(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .aspectRatio(2f),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(R.drawable.landscape11), contentDescription = null
                    )
                }
            }
        }
        items(100) {
            if (it == 5) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(.5f),
                    painter = painterResource(R.drawable.landscape11),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .clickable {

                        }
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Row $it", fontSize = 22.sp)
                }
            }
        }
    }
}


@Language("GLSL")
val CheapFrostedGlassTopBarAGSL =
    """
    const vec4 white = vec4(1.0);
    
    uniform shader inputShader;
    uniform float barHeight;
    
    vec4 main(vec2 coord) {
        if (coord.y > barHeight) {
            return inputShader.eval(coord);
        } else {
            vec2 factor = vec2(3.0);
            
            vec4 color = vec4(0.0);
            color += inputShader.eval(coord - 3.0 * factor) * 0.0540540541;
            color += inputShader.eval(coord - 2.0 * factor) * 0.1216216216;
            color += inputShader.eval(coord - factor) * 0.1945945946;
            color += inputShader.eval(coord) * 0.2270270270;
            color += inputShader.eval(coord + factor) * 0.1945945946;
            color += inputShader.eval(coord + 2.0 * factor) * 0.1216216216;
            color += inputShader.eval(coord + 3.0 * factor) * 0.0540540541;
            
            return mix(color, white, 0.7);
        }
    }
    """.trimIndent()

val TopAppBarHeight = 180.dp

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun FrostedGlassTopBarTest() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val shader = RuntimeShader(CheapFrostedGlassTopBarAGSL)
                shader.setFloatUniform("barHeight", TopAppBarHeight.toPx())
                val androidRenderEffect = android.graphics.RenderEffect
                    .createRuntimeShaderEffect(shader, "inputShader")
                renderEffect = androidRenderEffect.asComposeRenderEffect()
            }
    ) {
        items(100) {
            if (it == 5) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f),
                    painter = painterResource(R.drawable.landscape11),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Row $it", fontSize = 22.sp)
                }
            }
        }
    }
}