package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun Tutorial2_9Screen2() {
    TutorialContent()
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {
    ModalDrawerComponent()
}

@ExperimentalMaterialApi
@Composable
private fun ModalDrawerComponent() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }
    var selectedIndex by remember { mutableStateOf(0) }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerContentHeader()
            Divider()
            ModelDrawerContentBody(
                selectedIndex,
                onSelected = {
                    selectedIndex = it
                },
                closeDrawer = closeDrawer
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                ModalDrawerTopbar(openDrawer)
                ModalContent(openDrawer)
            }
        }
    )
}

@Composable
fun ModalDrawerTopbar(openDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Text("ModalDrawer")
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {}
    )
}


@Composable
fun ModalDrawerContentHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(20.dp)
    ) {

        Image(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.avatar_1_raster),
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Smart Tool Factory", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "smarttoolfactory@icloud.com")
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
    }
}


@Composable
fun ModelDrawerContentBody(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        list.forEachIndexed { index, pair ->

            val label = pair.first
            val imageVector = pair.second
            DrawerButton(
                icon = imageVector,
                label = label,
                isSelected = selectedIndex == index,
                action = {
                    onSelected(index)
                    closeDrawer()
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ModalContent(openDrawer: () -> Unit) {
    LazyColumn {

        items(userList) { item: String ->
            ListItem(
                modifier = Modifier.clickable {
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

val list = listOf(
    Pair("My Files", Icons.Filled.Folder),
    Pair("Shared with Me", Icons.Filled.People),
    Pair("Starred", Icons.Filled.Star),
    Pair("Recent", Icons.Filled.AccessTime),
    Pair("Offline", Icons.Filled.OfflineShare),
    Pair("Uploads", Icons.Filled.Upload),
    Pair("Backups", Icons.Filled.CloudUpload),
)