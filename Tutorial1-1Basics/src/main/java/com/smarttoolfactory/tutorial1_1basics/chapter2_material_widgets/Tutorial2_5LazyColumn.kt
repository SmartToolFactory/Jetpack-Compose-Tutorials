package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.runtime.Composable
import com.smarttoolfactory.tutorial1_1basics.components.TutorialHeader

@Composable
fun Tutorial2_5Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    TutorialHeader(text = "Lazy Column")


}