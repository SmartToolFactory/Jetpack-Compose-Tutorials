package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.ChatAppbar
import com.smarttoolfactory.tutorial1_1basics.ui.QuotedMessageColor
import com.smarttoolfactory.tutorial1_1basics.ui.SentMessageColor
import com.smarttoolfactory.tutorial1_1basics.ui.SentQuoteColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun Tutorial3_6Screen2() {

    val systemUiController = rememberSystemUiController()

    // Check out Tutorial4_5_1 for DisposableEffect
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

    val description = "Flexible chat rows that position message and status " +
            "based on text width, line, last width line and parent width.\n" +
            "Create messages with varying in size and line numbers to see how they are displayed."
    val context = LocalContext.current

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val sdf = remember { SimpleDateFormat("hh:mm", Locale.ROOT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffFBE9E7))
    ) {

        ChatAppbar(
            title = "Flexible ChatRows",
            description = description,
            onClick = {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
            }
        )
        val scrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages) { message: ChatMessage ->

                // Remember random stats icon to not create at each recomposition
                val messageStatus = remember { MessageStatus.values()[Random.nextInt(3)] }

                // Toggle between sent and received message
                if (message.id.toInt() % 2 == 1) {
                    SentMessageRow(
                        text = message.message,
                        messageTime = sdf.format(message.date),
                        messageStatus = messageStatus
                    )
                } else {
                    ReceivedMessageRow(
                        text = message.message,
                        messageTime = sdf.format(message.date)
                    )
                }
            }
        }

        ChatInput(
            onMessageChange = { messageContent ->
                messages.add(
                    ChatMessage(
                        (messages.size + 1).toLong(),
                        messageContent,
                        System.currentTimeMillis()
                    )
                )

                coroutineScope.launch {
                    scrollState.animateScrollToItem(messages.size - 1)
                }

            }
        )
    }
}

@Composable
private fun SentMessageRow(
    text: String,
    messageTime: String,
    messageStatus: MessageStatus
) {
    // Whole column that contains chat bubble and padding on start or end
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 2.dp, bottom = 2.dp)
//            .background(Color.LightGray)
            .padding(start = 60.dp, end = 8.dp)

    ) {


        // This is chat bubble
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(SentMessageColor)
                .clickable { },

            mainContent = {
                // 💬 Quoted message
                QuotedMessage(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        // 🔥 This is required to set Surface height before text is set
                        .height(IntrinsicSize.Min)
                        .background(SentQuoteColor, shape = RoundedCornerShape(8.dp))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable {

                        },
                    quotedMessage = "Quoted long message"
                )
            }
        ) {

            println(
                "📝 SentMessageRow() in dependent()"
                        + " IntSize: $it"
            )

            ChatFlexBoxLayout(
                modifier = Modifier
                    .padding(start = 2.dp, top = 2.dp, end = 4.dp, bottom = 2.dp),
                text = text,
                messageStat = {
                    MessageTimeText(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(end = 6.dp),
                        messageTime = messageTime,
                        messageStatus = messageStatus
                    )
                }
            )
        }
    }
}

@Composable
private fun ReceivedMessageRow(text: String, messageTime: String) {
    // Whole column that contains chat bubble and padding on start or end
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 2.dp, bottom = 2.dp)
//            .background(Color.LightGray)
            .padding(start = 8.dp, end = 60.dp)

    ) {

        // This is chat bubble
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { },

            mainContent = {
                // 💬 Quoted message
                QuotedMessage(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        // 🔥 This is required to set Surface height before text is set
                        .height(IntrinsicSize.Min)
                        .background(QuotedMessageColor, shape = RoundedCornerShape(8.dp))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable {

                        },
                    quotedImage = R.drawable.landscape1
                )
            }
        ) {

            println("📝 ReceivedMessageRow() in dependent() IntSize: $it")

            ChatFlexBoxLayout(
                modifier = Modifier
                    .padding(start = 2.dp, top = 2.dp, end = 4.dp, bottom = 2.dp),
                text = text,
                messageStat = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            modifier = Modifier.padding(top = 1.dp, bottom = 1.dp, end = 4.dp),
                            text = messageTime,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }
    }
}