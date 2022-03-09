package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.Tutorial6_5Screen

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MainScreen() {
    Scaffold { paddingValues: PaddingValues ->
//        TutorialNavGraph(modifier = Modifier.padding(paddingValues))
        Tutorial6_5Screen()
    }
}