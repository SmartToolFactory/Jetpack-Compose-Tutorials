package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.delay

@Composable
fun Tutorial4_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        StyleableTutorialText(
            text = "Custom rememberable, **rememberBadgeState**, is used to set properties of" +
                    " custom badges. Badge component uses **BadgeState** to " +
                    "draw different types of badges with various properties.",
            bullets = false
        )

        val badge1 = rememberBadgeState(
            elevation = 1.dp
        )
        Badge(badgeState = badge1)
        Spacer(modifier = Modifier.height(4.dp))

        val badge2 = rememberBadgeState(
            fontSize = 12.sp,
            elevation = 4.dp
        )
        Badge(badgeState = badge2)
        Spacer(modifier = Modifier.height(4.dp))

        val badge3 = rememberBadgeState(
            fontSize = 14.sp,
            circleShapeThreshold = 2,
            textColor = Color(0xffB2DFDB),
            backgroundColor = Color(0xff00897B)
        )
        Badge(badgeState = badge3)
        Spacer(modifier = Modifier.height(4.dp))

        val badge4 = rememberBadgeState(
            fontSize = 18.sp,
            borderStroke = BorderStroke(2.dp, Color(0xff29B6F6)),
            elevation = (.4).dp
        )
        Badge(badgeState = badge4)
        Spacer(modifier = Modifier.height(4.dp))

        val badge5 = rememberBadgeState(
            backgroundColor = Color.Transparent,
            textColor = Color(0xffF57C00),
            borderStroke = BorderStroke(2.dp, Color(0xffF57C00))

        )
        Badge(badgeState = badge5)
        Spacer(modifier = Modifier.height(4.dp))

        val badge6 = rememberBadgeState(

        )
        Badge(badgeState = badge6)
        Spacer(modifier = Modifier.height(4.dp))

        val badge7 = rememberBadgeState(
            fontSize = 40.sp,
            textColor = Color.White,
            backgroundColor = Color(0xff795548)
        )
        Badge(badgeState = badge7)
        Spacer(modifier = Modifier.height(4.dp))

        val badge8 = rememberBadgeState(
            fontSize = 40.sp,
            horizontalPadding = 24.dp,
            textColor = Color.White,
            backgroundColor = Color(0xff3F51B5)
        )
        Badge(badgeState = badge8)
        Spacer(modifier = Modifier.height(4.dp))

        val badge9 = rememberBadgeState(
            fontSize = 64.sp,
            textColor = Color.White,
            backgroundColor = Color(0xffD81B60),
            roundedRadiusPercent = 20,
            elevation = 5.dp,
            shadowColor = Color(0xffD32F2F)
        )
        Badge(badgeState = badge9)
        Spacer(modifier = Modifier.height(6.dp))

        val badge10 = rememberBadgeState(
            maxNumber = 49,
            circleShapeThreshold = 2,
            fontSize = 64.sp,
            textColor = Color.White,
            backgroundColor = Color(0xff607D8B),
            roundedRadiusPercent = 70
        )
        Badge(badgeState = badge10)

        val badgeStateList = listOf(
            badge1,
            badge2,
            badge3,
            badge4,
            badge5,
            badge6,
            badge7,
            badge8,
            badge9,
            badge10
        )

        LaunchedEffect(Unit) {
            repeat(101) {
                delay(100)
                badgeStateList.forEach { badgeState ->
                    badgeState.setBadgeCount(it)
                }
            }
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialContentPreview() {
    TutorialContent()
}