package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.delay

@Preview
@Composable
fun Tutorial4_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppbar()
        TutorialPage()
    }
}

@Composable
private fun TutorialPage() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
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
            shadow = MaterialShadow()
        )

        Badge(badgeState = badge1)
        Spacer(modifier = Modifier.height(4.dp))

        val badge2 = rememberBadgeState(
            fontSize = 12.sp,
            shadow = MaterialShadow(elevation = 4.dp)
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
            shadow = MaterialShadow(elevation = 4.dp)
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
            shadow = MaterialShadow(
                elevation = 5.dp,
                color = Color(0xffD32F2F)
            )
        )
        Badge(badgeState = badge9)
        Spacer(modifier = Modifier.height(10.dp))

        val badge10 = rememberBadgeState(
            maxNumber = 49,
            circleShapeThreshold = 2,
            fontSize = 64.sp,
            textColor = Color.White,
            backgroundColor = Color(0xff607D8B),
            roundedRadiusPercent = 70
        )
        Badge(badgeState = badge10)

        Spacer(modifier = Modifier.height(10.dp))

        val badge11 = rememberBadgeState(
            fontSize = 40.sp,
            textColor = Color.White,
            horizontalPadding = 12.dp,
            fontStyle = FontStyle.Italic,
            shadow = MaterialShadow(
                Color.DarkGray,
                alpha = 0.8f,
                dX = 6.dp,
                dY = 3.dp,
                shadowRadius = 9.dp
            )
        )
        Badge(badgeState = badge11)
        Spacer(modifier = Modifier.height(10.dp))

        val badge12 = rememberBadgeState(
            fontSize = 40.sp,
            textColor = Color.White,
            horizontalPadding = 12.dp,
            fontWeight = FontWeight.ExtraBold,
            shadow = MaterialShadow(
                Color.DarkGray,
                alpha = 0.6f,
                dX = 5.dp,
                dY = 5.dp,
                shadowRadius = 1.dp
            )
        )
        Badge(badgeState = badge12)
        Spacer(modifier = Modifier.height(10.dp))

        val badge13 = rememberBadgeState(
            fontSize = 40.sp,
            textColor = Color.White,
            horizontalPadding = 12.dp,
            textDecoration = TextDecoration.Underline,
            shadow = MaterialShadow(
                Color.DarkGray,
                alpha = 0.9f,
                dX = 2.dp,
                dY = 2.dp,
                shadowRadius = 6.dp
            )
        )
        Badge(badgeState = badge13)
        Spacer(modifier = Modifier.height(10.dp))

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
            badge10,
            badge11,
            badge12,
            badge13
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

@Composable
private fun TopAppbar() {
    Surface(elevation = 4.dp) {
        Column {
            var selectedIndex by remember { mutableStateOf(0) }

            TopAppBar(
                title = {
                    Text(text = "Custom Remember", color = Color.White)
                },
                backgroundColor = Color(0xff00897B),
                elevation = 8.dp
            )

            val list = listOf(
                "CHATS",
                "STATUS",
                "CALLS"
            )
            val badgeChats = rememberBadgeState(
                backgroundColor = Color.White,
                textColor = Color(0xff00897B),
                fontSize = 12.sp,
                horizontalPadding = 2.dp,
            )
            badgeChats.setBadgeCount(9)

            val badgeStatus = rememberBadgeState(
                backgroundColor = Color.White,
                textColor = Color(0xff00897B),
                fontSize = 12.sp,
                horizontalPadding = 2.dp
            )
            badgeStatus.setBadgeCount(80)

            val badgeCalls = rememberBadgeState(
                backgroundColor = Color.White,
                textColor = Color(0xff00897B),
                fontSize = 12.sp,
                horizontalPadding = 2.dp,
                showBadgeThreshold = 1
            )

            val badgeStates = listOf(
                badgeChats,
                badgeStatus,
                badgeCalls,
            )

            LaunchedEffect(key1 = Unit) {
                delay(2000)
                badgeStatus.setBadgeCount(100)
                delay(1000)
                badgeCalls.setBadgeCount(5)
                delay(1000)
                badgeCalls.setBadgeCount(42)
            }

            TabRow(
                selectedTabIndex = selectedIndex,
                backgroundColor = Color(0xff00897B),
                indicator = { tabPositions: List<TabPosition> ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        height = 3.dp,
                        color = Color.White
                    )
                }
            ) {
                list.forEachIndexed { index, text ->
                    Tab(selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color(0xff77a9a0),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = text,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge(badgeState = badgeStates[index])
                            }
                        }
                    )
                }
            }

            val systemUiController = rememberSystemUiController()

            DisposableEffect(key1 = true, effect = {

                systemUiController.setStatusBarColor(
                    color = Color(0xff00897B)
                )
                onDispose {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent
                    )
                }
            })
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