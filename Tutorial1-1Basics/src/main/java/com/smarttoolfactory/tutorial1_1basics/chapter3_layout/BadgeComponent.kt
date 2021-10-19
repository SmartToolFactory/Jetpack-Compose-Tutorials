package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//@Composable
//fun BadgeComponent(text: String) {
//    val badgeState = rememberBadgeState(
//        isCircle = ,
//        badgeCornerRatio = ,
//        color = ,
//        contentColor =
//    )
//}
//
//@Composable
//fun Badge(text: String, badgeState: BadgeState, modifier: Modifier = Modifier) {
//    Surface(
//        modifier = modifier,
//        shape = badgeState.shape,
//        elevation = badgeState.elevation,
//        border = badgeState.borderStroke,
//        color = badgeState.color,
//        contentColor = badgeState.contentColor
//    ) {
//        Text(text = text)
//    }
//}
//
//@Composable
//fun rememberBadgeState(
//    isCircle: Boolean,
//    @FloatRange(from = 0.0, to = 1.0) badgeCornerRatio: Float,
//    shape: Shape = CircleShape,
//    color: Color,
//    contentColor: Color,
//    elevation: Dp = 0.dp,
//    borderStroke: BorderStroke? = null
//): BadgeState {
//    return remember {
//        BadgeState(
//            isCircle,
//            badgeCornerRatio,
//            shape,
//            color,
//            contentColor,
//            elevation,
//            borderStroke
//        )
//    }
//}
//
//data class BadgeState(
//    var isCircle: Boolean,
//    @FloatRange(from = 0.0, to = 1.0) var badgeCornerRatio: Float,
//    var shape: Shape = CircleShape,
//    var color: Color,
//    var contentColor: Color,
//    var elevation: Dp = 0.dp,
//    var borderStroke: BorderStroke? = null
//)