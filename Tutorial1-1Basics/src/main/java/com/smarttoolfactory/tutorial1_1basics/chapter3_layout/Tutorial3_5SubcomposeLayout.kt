package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun Tutorial3_5Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val density = LocalDensity.current.density

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SubComponent(
            modifier = Modifier.background(Color.LightGray),
            mainContent = {

                println("🤘 SubComponent-> MainContent {}")
                Text(
                    "MainContent",
                    modifier = Modifier
                        .background(Color(0xffF44336))
                        .height(60.dp),
                    color = Color.White
                )
            },
            dependentContent = { size: IntSize ->

                println("🤘 SubComponent-> DependentContent {} Dependent size: $size")

                val paddingTop = with(density) {
                    size.height/this
                }.dp

                Column(modifier = Modifier.padding(top = paddingTop)) {

                    Text(
                        "Dependent Content",
                        modifier = Modifier
                            .background(Color(0xff9C27B0)),
                        color = Color.White
                    )
                }
            }
        )
    }
}

@Composable
private fun SubComponent(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        val maxSize = mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
            IntSize(
                width = maxOf(currentMax.width, placeable.width),
                height = maxOf(currentMax.height, placeable.height)
            )
        }

        layout(maxSize.width, maxSize.height) {

            println("🔥 SubcomposeLayout-> layout() maxSize width: ${maxSize.width}, height: ${maxSize.height}")
            mainPlaceables.forEach { it.placeRelative(0, 0) }

            subcompose(SlotsEnum.Dependent) {
                dependentContent(maxSize)
            }.forEach {
                it.measure(constraints).placeRelative(0, 0)
            }

        }
    }
}

enum class SlotsEnum { Main, Dependent }

