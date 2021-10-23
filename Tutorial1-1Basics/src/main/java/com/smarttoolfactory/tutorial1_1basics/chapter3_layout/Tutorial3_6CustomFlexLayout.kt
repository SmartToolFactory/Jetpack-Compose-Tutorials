package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Tutorial3_6Screen() {

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

    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val sdf = remember { SimpleDateFormat("hh:mm a", Locale.ROOT) }

    println("🎃 Tutorial3_6Screen messages: $messages, sdf: $sdf")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffFBE9E7))
    ) {

        TopAppBar(
            title = {

                Row(
//                    modifier = Modifier.background(Color.Red)
                ) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { },
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

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = "TopAppBar", modifier = Modifier.padding(2.dp))
                    }

                }

            },
            elevation = 4.dp,
            backgroundColor = Color(0xff00897B),
            contentColor = Color.White,
//            navigationIcon = {
//                Row(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(percent = 40))
//                        .clickable {  }
//                        .padding(vertical = 4.dp, horizontal = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = null
//                    )
//
//                    Surface(
//                        shape = CircleShape,
//                        color = Color.LightGray
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Groups,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .background(Color.LightGray)
//                                .padding(8.dp)
//                                .fillMaxHeight()
//                                .aspectRatio(1f)
//                        )
//                    }
//                }
//            },
            actions = {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Videocam,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Call,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages) { message: ChatMessage ->
                MessageRow(
                    text = message.message,
                    messageTime = sdf.format(message.date),
                    messageStatus = MessageStatus.RECEIVED
                )
            }
        }

        ChatInput(onMessageChange = { messageContent ->
            messages.add(
                ChatMessage(
                    (messages.size + 1).toLong(),
                    messageContent,
                    System.currentTimeMillis()
                )
            )
        })
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ChatInput(modifier: Modifier = Modifier, onMessageChange: (String) -> Unit) {

    var input by remember { mutableStateOf(TextFieldValue("")) }
    val textEmpty: Boolean by derivedStateOf { input.text.isEmpty() }

    Row(
        modifier = modifier
            .padding(8.dp)
//            .navigationBarsWithImePadding()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colors.surface,
            elevation = 1.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Mood, contentDescription = "emoji")
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textStyle = TextStyle(
                                fontSize = 16.sp
                            ),
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            cursorBrush = SolidColor(Color(0xff00897B)),
                            decorationBox = { innerTextField ->
                                if (textEmpty) {
                                    Text("Message")
                                }
                                innerTextField()
                            }
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            modifier = Modifier.rotate(-45f),
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "attach"
                        )
                    }
                    AnimatedVisibility(visible = textEmpty) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "camera"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        FloatingActionButton(
            modifier = Modifier.size(50.dp),
            backgroundColor = Color(0xff00897B),
            onClick = {
                if (!textEmpty) {
                    onMessageChange(input.text)
                    input = TextFieldValue("")
                }
            }
        ) {
            Icon(
                tint = Color.White,
                imageVector = if (textEmpty) Icons.Filled.Mic else Icons.Filled.Send,
                contentDescription = null
            )
        }
    }
}

@Composable
fun MessageRow(
    text: String,
    messageTime: String,
    messageStatus: MessageStatus
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 1.dp, bottom = 1.dp)
            .background(Color.LightGray)
    ) {
        ChatFlexBoxLayout(

            modifier = Modifier
                .padding(start = 60.dp, end = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xffDCF8C6)),
            text,
            messageTime,
            messageStatus
        )
    }
}


@Composable
private fun ChatFlexBoxLayout(
    modifier: Modifier = Modifier,
    text: String,
    messageTime: String,
    messageStatus: MessageStatus
) {

    var lineCount = 1

    val content = @Composable {
        Message(text) { textLayoutResult ->
            lineCount = textLayoutResult.lineCount
        }
        MessageTimeText(messageTime = messageTime, messageStatus = messageStatus)
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        if (measurables.size != 2)
            throw IllegalArgumentException("There should be 2 components for this layout")

        val parentWidth = constraints.maxWidth

        val placeables: List<Placeable> = measurables.map { measurable ->
            // Measure each child
            measurable.measure(constraints)
        }

        val totalSize = placeables.fold(IntSize.Zero) { totalSize: IntSize, placeable: Placeable ->

            val intSize = IntSize(
                width = totalSize.width + placeable.width,
                height = totalSize.height + placeable.height
            )

            println(
                "🍏 ChatFlexBoxLayout placeable " +
                        "width: ${placeable.width}, " +
                        "height: ${placeable.height}, " +
                        "intSize: $intSize"
            )

            intSize
        }

        val width: Int
        val height: Int
        var selection = 0

        // Message + time layout is smaller than component so layout them side by side
        if (parentWidth > totalSize.width) {
            width = totalSize.width
            height = placeables.maxOf { it.height }
            selection = 0
        } else {
            width = parentWidth
            height = totalSize.height
            selection = 1
        }

        println("🍒 ChatFlexBoxLayout parentWidth: $parentWidth, maxSize: $totalSize, lineCount: $lineCount, selection: $selection, width: $width, height: $height")

        layout(width = width, height = height) {

            if (selection == 0) {
                placeables.first().placeRelative(0, 0)
                placeables.last()
                    .placeRelative(placeables.first().width, height - placeables.last().height)
            } else {
                placeables.first().placeRelative(0, 0)
                placeables.last()
                    .placeRelative(width - placeables.last().width, placeables.first().height)
            }
        }
    }
}

@Composable
private fun Message(text: String, onTextLayout: (TextLayoutResult) -> Unit) {
    Text(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
        fontSize = 16.sp,
        lineHeight = 18.sp,
        text = text,
        onTextLayout = onTextLayout
    )
}

@Composable
private fun MessageTimeText(
    modifier: Modifier = Modifier,
    messageTime: String,
    messageStatus: MessageStatus
) {

    Row(
        modifier = modifier
            .padding(end = 6.dp)
//            .background(Color.Red)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                modifier = Modifier
                    .padding(top = 1.dp, bottom = 1.dp),
                text = messageTime,
                fontSize = 12.sp
            )
        }

        Icon(
            modifier = Modifier
                .size(16.dp, 12.dp)
                .padding(start = 4.dp),
            imageVector = if (messageStatus == MessageStatus.RECEIVED) {
                Icons.Default.Done
            } else Icons.Default.DoneAll,
            tint = if (messageStatus == MessageStatus.RECEIVED) Color(0xff424242)
            else Color(
                0xff0288D1
            ),
            contentDescription = "state"
        )

    }
}

enum class MessageStatus {
    RECEIVED, READ
}

data class ChatMessage(val id: Long, var message: String, var date: Long)
