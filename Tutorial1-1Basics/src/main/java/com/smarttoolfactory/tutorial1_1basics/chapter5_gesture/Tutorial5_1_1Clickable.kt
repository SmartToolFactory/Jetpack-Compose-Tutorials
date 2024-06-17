@file:OptIn(ExperimentalMaterialApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalUseFallbackRippleImplementation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Tutorial5_1Screen1() {
    CompositionLocalProvider(LocalUseFallbackRippleImplementation provides true) {
        TutorialContent()
    }
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        StyleableTutorialText(
//            text = "1-) **Clickable** modifier to the element to make it clickable within its " +
//                    "bounds and show an indication as specified in indication parameter."
//        )
//        TutorialText2(text = "Position of clickable")
//        ClickableOrderExample()
//        TutorialText2(text = "Custom ripple and bound")
//        CustomRippleExample()
//        TutorialText2(text = "Custom ripple theme")
//        CustomRippleThemeExample()
//        TutorialText2(text = "Custom Indication")
//        CustomIndicationExample()
    }
}

private val color = Color(0xffBDBDBD)

internal val modifierWithClip = Modifier
    .fillMaxWidth()
    .height(40.dp)
    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = true)
    .background(color)

internal val modifierNoClip = Modifier
    .fillMaxWidth()
    .height(40.dp)
    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
    .background(color, shape = RoundedCornerShape(16.dp))

//@Composable
//private fun ClickableOrderExample() {
//    // This one clips ripple to have correct form
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = modifierWithClip
//                .clickable {}
//        ) {
//            Text(
//                text = "Clip before clickable",
//                color = Color.White,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//
//        // This ripple is not bound correctly to shape
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .clickable {}
//                .then(modifierWithClip)
//        ) {
//            Text(
//                text = "Clip after clickable",
//                color = Color.White,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}
//
//@Composable
//fun CustomRippleExample() {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = modifierWithClip
//                .clickable(
//                    interactionSource = MutableInteractionSource(),
//                    indication = ripple(
//                        bounded = true,
//                        radius = 250.dp,
//                        color = Color.Green
//                    ),
//                    onClick = {}
//                )
//        ) {
//            Text(
//                text = "ripple() bounded",
//                color = Color.White
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Box(
//            contentAlignment = Alignment.Center,
//            // 🔥 Modifier.clip also bounds ripple
//            modifier = modifierNoClip
//                .clickable(
//                    interactionSource = MutableInteractionSource(),
//                    indication = ripple(
//                        bounded = false,
//                        radius = 250.dp,
//                        color = Color.Green
//                    ),
//                    onClick = {}
//                )
//        ) {
//            Text(
//                text = "ripple() unbounded",
//                color = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun CustomRippleThemeExample() {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//
//        CompositionLocalProvider(LocalRippleTheme provides CustomRippleTheme(Color.Cyan)) {
//            Box(
//                modifier = modifierWithClip.clickable {},
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Custom Ripple Theme",
//                    color = Color.White
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        CompositionLocalProvider(LocalRippleTheme provides CustomRippleTheme(Color.Magenta)) {
//            Box(
//                modifier = modifierWithClip.clickable {},
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Custom Ripple Theme",
//                    color = Color.White
//                )
//            }
//        }
//    }
//}
//
//
//@Composable
//fun CustomIndicationExample() {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//
//
//        val indication1: CustomIndication = CustomIndication(
//            pressColor = Color.Cyan,
//            cornerRadius = CornerRadius(30f, 30f),
//            alpha = .7f
//        )
//
//        val indication2: CustomIndication = CustomIndication(
//            pressColor = Color.Red,
//            cornerRadius = CornerRadius(16f, 16f),
//            alpha = .5f
//        )
//
//        val indication3: CustomIndication = CustomIndication(
//            pressColor = Color(0xffFFEB3B),
//            alpha = .4f,
//            drawRoundedShape = false,
//        )
//
//        Box(
//            modifierWithClip
//                .clickable(
//                    interactionSource = MutableInteractionSource(),
//                    indication = indication1,
//                    onClick = {}
//                ),
//            contentAlignment = Alignment.Center) {
//            Text(
//                text = "Custom Indication",
//                color = Color.White
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Box(
//            modifierWithClip
//                .clickable(
//                    interactionSource = MutableInteractionSource(),
//                    indication = indication2,
//                    onClick = {}
//                ),
//            contentAlignment = Alignment.Center) {
//            Text(
//                text = "Custom Indication",
//                color = Color.White
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Box(
//            Modifier.fillMaxWidth()
//                .height(200.dp)
//                .clickable(
//                    interactionSource = MutableInteractionSource(),
//                    indication = indication3,
//                    onClick = {}
//                ),
//            contentAlignment = Alignment.Center) {
//            Text(
//                text = "Custom Indication with Circle Shape",
//                color = Color.White
//            )
//        }
//    }
//}
//
//private class CustomRippleTheme(val color: Color = Color.Black) : RippleTheme {
//    @Composable
//    override fun defaultColor(): Color = color
//
//    @Composable
//    override fun rippleAlpha(): RippleAlpha =
//        RippleTheme.defaultRippleAlpha(color, lightTheme = !isSystemInDarkTheme())
//}
//
//private class CustomIndication(
//    val pressColor: Color = Color.Red,
//    val cornerRadius: CornerRadius = CornerRadius(16f, 16f),
//    val alpha: Float = 0.5f,
//    val drawRoundedShape: Boolean = true
//) : Indication {
//
//    private inner class DefaultIndicationInstance(
//        private val isPressed: State<Boolean>,
//    ) : IndicationInstance {
//
//        override fun ContentDrawScope.drawIndication() {
//
//            drawContent()
//            when {
//                isPressed.value -> {
//                    if (drawRoundedShape) {
//                        drawRoundRect(
//                            cornerRadius = cornerRadius,
//                            color = pressColor.copy(
//                                alpha = alpha
//                            ), size = size
//                        )
//                    } else {
//
//                        drawCircle(
//                            radius = size.width,
//                            color = pressColor.copy(
//                                alpha = alpha
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    @Composable
//    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
//        val isPressed = interactionSource.collectIsPressedAsState()
//        return remember(interactionSource) {
//            DefaultIndicationInstance(isPressed)
//        }
//    }
//}