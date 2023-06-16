package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

@Preview
@Composable
fun TextTabComponent() {
    var selectedIndex by remember { mutableStateOf(0) }

    val list = listOf("Home", "Map", "Settings")

    TabRow(selectedTabIndex = selectedIndex,
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .height(4.dp)
                    .background(color = Color.Red)
            ) {}
        }) {
        list.forEachIndexed { index, text ->
            Tab(selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = { Text(text = text) }
            )
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TextTabComponentPreview() {
    ComposeTutorialsTheme {
        TextTabComponent()
    }
}

@Composable
fun IconTabComponent() {

    var selectedIndex by remember { mutableStateOf(0) }
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Map, Icons.Filled.Settings)
    TabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = Color(0xff546E7A),
        contentColor = Color(0xffF06292),
//            divider = {
//                TabRowDefaults.Divider(
//                    thickness = 5.dp,
//                    color = Color(0xffE0E0E0)
//                )
//            },
        indicator = { tabPositions: List<TabPosition> ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                height = 5.dp,
                color = Color(0xff8BC34A)
            )
        }
    ) {
        icons.forEachIndexed { index, imageVector ->
            Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = {
                    Icon(imageVector = imageVector, contentDescription = null)
                })
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun IconTabComponentPreview() {
    ComposeTutorialsTheme {
        IconTabComponent()
    }
}

@Composable
fun CombinedTabComponent() {

    var selectedIndex by remember { mutableStateOf(0) }
    val tabContents = listOf(
        "Home" to Icons.Filled.Home,
        "Map" to Icons.Filled.Map,
        "Settings" to Icons.Filled.Settings
    )

    TabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        tabContents.forEachIndexed { index, pair: Pair<String, ImageVector> ->
            Tab(selected = selectedIndex == index, onClick = { selectedIndex = index },
                text = { Text(text = pair.first) },
                icon = { Icon(imageVector = pair.second, contentDescription = null) }
            )
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun CombinedTabComponentPreview() {
    ComposeTutorialsTheme {
        CombinedTabComponent()
    }
}

@Composable
fun CombinedTabComponent2() {

    var selectedIndex by remember { mutableStateOf(0) }
    val tabContents = listOf(
        "Home" to Icons.Filled.Home,
        "Map" to Icons.Filled.Map,
        "Settings" to Icons.Filled.Settings
    )

    TabRow(
        divider = {

        },
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
    ) {


        tabContents.forEachIndexed { index, pair: Pair<String, ImageVector> ->
            CustomTab(pair.first, pair.second, {
                selectedIndex = index
            })
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun CombinedTab2ComponentPreview() {
    ComposeTutorialsTheme {
        CombinedTabComponent2()
    }
}


@Composable
private fun CustomTab(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun CustomTabComponentPreview() {
    ComposeTutorialsTheme {
        CustomTab("Home", Icons.Filled.Home, {})
    }
}

@Composable
fun ScrollableTextTabComponent() {
    var selectedIndex by remember { mutableStateOf(0) }

    val list = listOf("Home", "Map", "Dashboard", "Favorites", "Explore", "Settings")

    ScrollableTabRow(
        edgePadding = 8.dp,
        selectedTabIndex = selectedIndex
    ) {
        list.forEachIndexed { index, text ->
            Tab(selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = { Text(text = text) }
            )
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun ScrollableTextTabComponentPreview() {
    ComposeTutorialsTheme {
        ScrollableTextTabComponent()
    }
}

@Composable
fun CustomTabs() {
    var selectedIndex by remember { mutableStateOf(0) }

    val list = listOf("Active", "Completed")

    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color(0xff1E76DA),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xff1E76DA))
            .padding(1.dp),
        indicator = { tabPositions: List<TabPosition> ->
            Box {}
        }
    ) {
        list.forEachIndexed { index, text ->
            val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                else Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xff1E76DA)),
                selected = selected,
                onClick = { selectedIndex = index },
                text = { Text(text = text, color = Color(0xff6FAAEE)) }
            )
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun CustomTabsbComponentPreview() {
    ComposeTutorialsTheme {
        CustomTabs()
    }
}