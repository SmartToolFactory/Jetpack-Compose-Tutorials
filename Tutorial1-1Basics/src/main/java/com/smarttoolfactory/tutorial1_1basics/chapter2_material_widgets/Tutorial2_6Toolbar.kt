package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.ActionItemMode
import com.smarttoolfactory.tutorial1_1basics.model.ActionItemSpec
import com.smarttoolfactory.tutorial1_1basics.model.separateIntoActionAndOverflow
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText

@Composable
fun Tutorial2_6Screen(onBack: (() -> Unit)? = null) {
    TutorialContent(onBack)
}

@Composable
private fun TutorialContent(onBack: (() -> Unit)? = null) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffECEFF1)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {

            item {
                TutorialText(
                    text = "1-) TopAppbar with IconButtons as menus in classic Views"
                )
            }
            item {
                ActionTopAppbar(onBack)
            }
            item {
                TutorialText(
                    text = "2-) TopAppbar with Overflow menu"
                )
            }
            item {
                OverflowTopAppBar()
            }

            item {
                OverflowTopAppBar2()
            }

            item {
                TutorialText(
                    text = "3-) Fixed tabs only with text. TabRow is our fixed Row with equal size for each tab that contains tabs."
                )
            }
            item {
                TextTabComponent()
            }

            item {
                TutorialText(
                    text = "4-) Fixed tabs only with icon"
                )
            }
            item {
                IconTabComponent()
            }

            item {
                TutorialText(
                    text = "5-) Fixed tabs with text and icon"
                )
            }
            item {
                CombinedTabComponent()
            }

            item {
                TutorialText(
                    text = "6-) Fixed tabs with horizontally placed text and icon"
                )
            }
            item {
                CombinedTabComponent2()
            }
        })
}

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
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun CombinedTabComponentPreview() {
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
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun CombinedTab2ComponentPreview() {
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
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun CustomTabComponentPreview() {
   ComposeTutorialsTheme {
       CustomTab("Home",Icons.Filled.Home,{})
   }
}

@Composable
fun ActionTopAppbar(onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Text(text = "TopAppBar")
        },
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        navigationIcon = {
            IconButton(onClick = { onBack?.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
            }

            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
            }

            IconButton(
                onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Call, contentDescription = null)
            }

        }
    )
}

@Composable
fun OverflowTopAppBar() {

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Overflow") },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                }
                DropdownMenuItem(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Filled.Call, contentDescription = null)
                }
            }
        }
    )
}


@Composable
fun OverflowTopAppBar2() {
    val items = listOf(
        ActionItemSpec("Call", Icons.Default.Call, ActionItemMode.ALWAYS_SHOW) {},
        ActionItemSpec("Send", Icons.Default.Send, ActionItemMode.IF_ROOM) {},
        ActionItemSpec("Email", Icons.Default.Email, ActionItemMode.IF_ROOM) {},
        ActionItemSpec("Delete", Icons.Default.Delete, ActionItemMode.IF_ROOM) {},
    )
    TopAppBar(
        title = { Text("Overflow2") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, "Menu")
            }
        },
        actions = {
            ActionMenu(items, defaultIconSpace = 3)
        }
    )
}


@Composable
fun ActionMenu(
    items: List<ActionItemSpec>,
    defaultIconSpace: Int = 3, // includes overflow menu
    menuExpanded: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    // decide how many ifRoom icons to show as actions
    val (actionItems, overflowItems) = remember(items, defaultIconSpace) {
        separateIntoActionAndOverflow(items, defaultIconSpace)
    }

    Row {
        for (item in actionItems) {
            IconButton(onClick = item.onClick) {
                Icon(item.icon, item.name)
            }
        }
        if (overflowItems.isNotEmpty()) {
            IconButton(onClick = { menuExpanded.value = true }) {
                Icon(Icons.Default.MoreVert, "More actions")
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false }
            ) {
                for (item in overflowItems) {
                    DropdownMenuItem(onClick = item.onClick) {
                        //Icon(item.icon, item.name) just have text in the overflow menu
                        Text(item.name)
                    }
                }
            }

        }
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun TutorialContentPreview() {
    ComposeTutorialsTheme {
        TutorialContent()
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun ActionTopAppBarReview() {
    ComposeTutorialsTheme {
        ActionTopAppbar()
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun OverflowTopAppBarReview() {
    ComposeTutorialsTheme {
        OverflowTopAppBar()
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun OverflowTopAppBar2Review() {
    ComposeTutorialsTheme {
        OverflowTopAppBar2()
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
fun ActionMenuReview() {
    val items = listOf(
        ActionItemSpec("Call", Icons.Default.Call, ActionItemMode.ALWAYS_SHOW) {},
        ActionItemSpec("Send", Icons.Default.Send, ActionItemMode.IF_ROOM) {},
        ActionItemSpec("Email", Icons.Default.Email, ActionItemMode.IF_ROOM) {},
        ActionItemSpec("Delete", Icons.Default.Delete, ActionItemMode.IF_ROOM) {},
    )

    ComposeTutorialsTheme {
        ActionMenu(items = items, 3)
    }
}