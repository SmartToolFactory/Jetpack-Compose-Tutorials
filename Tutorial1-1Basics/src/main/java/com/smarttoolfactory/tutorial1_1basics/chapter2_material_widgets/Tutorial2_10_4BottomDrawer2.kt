package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.DrawerButton
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun Tutorial2_10Screen4() {
    TutorialContent()
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {

    val coroutineScope = rememberCoroutineScope()
    val drawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val openDrawer: () -> Unit = { coroutineScope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }
    var selectedBottomDrawerIndex by remember { mutableStateOf(0) }

    val toggleDrawer: () -> Unit = {
        if (drawerState.isOpen) {
            closeDrawer()
        } else {
            openDrawer()
        }
    }
    Scaffold(
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBarComponent(toggleDrawer)
        }
    ) {

        val bottomAppBarPadding = it.calculateBottomPadding()

        BottomDrawerComponent(
            bottomAppBarPadding,
            drawerState,
            openDrawer,
            selectedIndex = selectedBottomDrawerIndex,
            onSelected = {
                selectedBottomDrawerIndex = it
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun BottomDrawerComponent(
    bottomAppBarPadding: Dp,
    drawerState: BottomDrawerState,
    openDrawer: () -> Unit,
    selectedIndex: Int, onSelected: (Int) -> Unit
) {

    var selectedUser by remember { mutableStateOf(userList.first()) }
    val onUserSelected: (String) -> Unit = {
        selectedUser = it
    }

    BottomDrawer(
        gesturesEnabled = drawerState.isOpen,
        drawerState = drawerState,
        drawerContent = {
            DrawerContentBottom(
                selectedUser,
                selectedIndex = selectedIndex,
                onSelected = onSelected
            )
        },
        content = {
            // Select user from list in main screen and send it to BottomDrawer via this lambda
            DrawerContent(bottomAppBarPadding, onUserSelected, openDrawer)
        }
    )
}

/**
 * Main content for [BottomDrawer]
 */
@ExperimentalMaterialApi
@Composable
private fun DrawerContent(
    bottomAppBarPadding: Dp,
    onUserSelected: (String) -> Unit,
    openDrawer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.background(backgroundColor),
        contentPadding = PaddingValues(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = bottomAppBarPadding + 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(userList) { item: String ->
            Card(shape = RoundedCornerShape(8.dp)) {
                ListItem(
                    modifier = Modifier.clickable {
                        onUserSelected(item)
                        openDrawer()
                    },
                    icon = {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            painter = painterResource(id = R.drawable.avatar_1_raster),
                            contentDescription = null
                        )
                    },
                    secondaryText = {
                        Text(text = "Secondary text")
                    }
                ) {
                    Text(text = item, fontSize = 18.sp)
                }
            }
        }
    }
}

/**
 * Drawer content for [BottomDrawer]
 */
@Composable
fun DrawerContentBottom(
    selectedUser: String,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            Text("Mail", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "$selectedUser@abc.com")

            }
        }

        bottomDrawerList.forEachIndexed { index, pair ->
            val label = pair.first
            val imageVector = pair.second
            DrawerButton(
                icon = imageVector,
                label = label,
                isSelected = selectedIndex == index,
                action = {
                    onSelected(index)
                }
            )
        }
    }
}

@Composable
private fun BottomAppBarComponent(toggleDrawer: () -> Unit) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 2.dp,
        cutoutShape = CircleShape
    ) {

        // Leading icons should typically have a high content alpha
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            IconButton(
                onClick = toggleDrawer
            ) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }

        }
        // The actions should be at the end of the BottomAppBar. They use the default medium
        // content alpha provided by BottomAppBar
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { }) {
            Icon(Icons.Filled.Search, contentDescription = null)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
        }
    }
}

val bottomDrawerList = listOf(
    Pair("Inbox", Icons.Filled.Inbox),
    Pair("Outbox", Icons.Filled.Outbox),
    Pair("Favorites", Icons.Filled.Favorite),
    Pair("Archive", Icons.Filled.Archive),
    Pair("Trash", Icons.Filled.Delete),
)
