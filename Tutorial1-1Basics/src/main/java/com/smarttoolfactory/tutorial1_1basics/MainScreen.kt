package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MainScreen() {
    Scaffold { paddingValues: PaddingValues ->
//        println("🤔 MainScreen() paddingValues: $paddingValues")
        TutorialNavGraph(modifier = Modifier.padding(paddingValues))
    }

//    val state  =  rememberBadgeState(
//        fontSize = 64.sp,
//        backgroundColor = Color.Red,
//        circleShapeThreshold = 2,
//        horizontalPadding = 12.dp
//    )
//
//    Badge(
//       badgeState = state
//    )

//    LaunchedEffect(Unit) {
//        delay(1500)
//        state.setBadgeCount(24)
//    }

//    LaunchedEffect(Unit) {
//        repeat(101) {
//            delay(200)
//            state.setBadgeCount(it)
//        }
//    }
}