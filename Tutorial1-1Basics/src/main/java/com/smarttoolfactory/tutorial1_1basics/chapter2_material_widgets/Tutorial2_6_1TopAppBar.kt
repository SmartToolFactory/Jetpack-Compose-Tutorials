package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.ActionItemMode
import com.smarttoolfactory.tutorial1_1basics.model.ActionItemSpec
import com.smarttoolfactory.tutorial1_1basics.model.separateIntoActionAndOverflow
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.IndicatingIconButton
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial2_6Screen(onBack: (() -> Unit)? = null) {
    TutorialContent(onBack)
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialContentPreview() {
    ComposeTutorialsTheme {
        TutorialContent()
    }
}

@Composable
private fun TutorialContent(onBack: (() -> Unit)? = null) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {

            item {
                StyleableTutorialText(
                    text = "1-) TopAppbar with IconButtons as Toolbar menus in classic Views"
                )
            }
            item {
                ActionTopAppbar(onBack, elevation = 8.dp)
            }
            item {
                StyleableTutorialText(
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
                StyleableTutorialText(
                    text = "3-) Fixed tabs only with text. **TabRow** is our fixed Row with equal size for each tab that contains tabs."
                )
            }
            item {
                TextTabComponent()
            }

            item {
                StyleableTutorialText(
                    text = "4-) Fixed tabs only with icon"
                )
            }
            item {
                IconTabComponent()
            }

            item {
                StyleableTutorialText(
                    text = "5-) Fixed tabs with text and icon"
                )
            }
            item {
                CombinedTabComponent()
            }

            item {
                StyleableTutorialText(
                    text = "6-) Fixed tabs with horizontally placed text and icon"
                )
            }
            item {
                CombinedTabComponent2()
            }
            item {
                StyleableTutorialText(
                    text = "7-) Scrollable tabs"
                )
            }
            item {
                ScrollableTextTabComponent()
            }
            item {
                StyleableTutorialText(
                    text = "8-) Custom tabs"
                )
            }
            item {
                CustomTabs()
            }

            item {
                StyleableTutorialText(
                    text = "9-) TopAppBar and Tabs"
                )
            }
            item {
                TopAppBarWithTabComponent(onBack)
            }

            item {
                StyleableTutorialText(
                    text = "10-) Whatsapp TopAppbar"
                )
            }
            item {
                ChatAppbar(
                    title = "Custom Chat",
                    description = "Alice, Brook, Jack, Jason, Brad, No Name User",
                    onBack = onBack
                )
            }
        })
}

@Composable
fun ActionTopAppbar(onBack: (() -> Unit)? = null, elevation: Dp) {
    TopAppBar(
        title = {
            Text(text = "TopAppBar")
        },
        elevation = elevation,
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

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun ActionTopAppBarReview() {
    ComposeTutorialsTheme {
        ActionTopAppbar(elevation = 8.dp)
    }
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

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun OverflowTopAppBarReview() {
    ComposeTutorialsTheme {
        OverflowTopAppBar()
    }
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

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun OverflowTopAppBar2Review() {
    ComposeTutorialsTheme {
        OverflowTopAppBar2()
    }
}

@Composable
fun TopAppBarWithTabComponent(onBack: (() -> Unit)? = null) {
    Surface(elevation = 1.dp) {
        Column {
            ActionTopAppbar(onBack, elevation = 0.dp)
            CombinedTabComponent2()
        }
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TopAppBarWithTabComponentReview() {
    ComposeTutorialsTheme {
        TopAppBarWithTabComponent()
    }
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
private fun ActionMenuReview() {
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

@Composable
fun ChatAppbar(
    title: String = "Title",
    description: String = "Description",
    onClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        elevation = 4.dp,
        backgroundColor = Color(0xff00897B),
        contentColor = Color.White
    )
    {

        Row(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable {
                        onBack?.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )

                Surface(
                    modifier = Modifier.padding(6.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        modifier = Modifier
                            .background(Color.LightGray)
                            .padding(4.dp)
                            .fillMaxHeight()
                            .aspectRatio(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onClick?.invoke() }
                    .padding(2.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        ChatAppbarActions()
    }
}

@Preview
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun ChatAppbarPreview() {
    ChatAppbar()
}

@Composable
private fun ChatAppbarActions(
    onCamClick: (() -> Unit)? = null,
    onCallClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicatingIconButton(
            onClick = { /* doSomething() */ },
            indication = rememberRipple(bounded = false, radius = 22.dp),
            modifier = Modifier.then(Modifier.size(44.dp))
        ) {
            Icon(
                imageVector = Icons.Rounded.Videocam,
                contentDescription = null,
                tint = Color.White
            )
        }

        IndicatingIconButton(
            onClick = { /* doSomething() */ },
            indication = rememberRipple(bounded = false, radius = 22.dp),
            modifier = Modifier.then(Modifier.size(44.dp))
        ) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = null,
                tint = Color.White
            )
        }

        IndicatingIconButton(
            onClick = { /* doSomething() */ },
            indication = rememberRipple(bounded = false, radius = 22.dp),
            modifier = Modifier.then(Modifier.size(44.dp))
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

