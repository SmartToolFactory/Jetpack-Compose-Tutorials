package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.MessageStatus
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.MessageTimeText
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.QuotedMessageAlt
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.RecipientName
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.isRecipientRegistered
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.recipientOriginalName
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.recipientRegisteredName
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget.ChatFlexBoxLayout
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget.SubcomposeColumn
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.ReceivedQuoteColor
import com.smarttoolfactory.tutorial1_1basics.ui.SentMessageColor
import com.smarttoolfactory.tutorial1_1basics.ui.SentQuoteColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial5_1Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StyleableTutorialText(
            text = "**InteractionSource** represents a stream of **Interaction**s " +
                    "corresponding to events emitted by a\n" +
                    " component. These [Interaction]s can be used to change how components " +
                    "appear in different\n" +
                    " states, such as when a component is pressed or dragged. " +
                    "Setting same **InteractionSource** for different" +
                    " composables we can have synchronous click effects.",
            bullets = false
        )
        TutorialText2(text = "Composables with same InteractionSource")
        SharedInteractionSourceExample()
        TutorialText2(text = "InteractionSource that updates state of other")
        DiscreteInteractionSourceExample()
        TutorialText2(text = "Collect Interaction  and update other InteractionSource")
        DiscreteInteractionSourceFLowExample()

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SharedInteractionSourceExample() {
    // 🔥 This interaction is shared by Row and Card
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = true)
            .background(Color(0xff64B5F6))
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {
                    Toast
                        .makeText(context, "Row is clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Card(
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                .size(50.dp),
            backgroundColor = Color(0xff2196F3),
            shape = CircleShape,
            interactionSource = interactionSource,
            onClick = {
                Toast.makeText(context, "Icon is clicked", Toast.LENGTH_SHORT).show()
            },
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Icon",
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                    tint = Color(0xffBBDEFB)
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = "Title",
            style = TextStyle(fontSize = 16.sp),
        )
    }
}


@Composable
private fun DiscreteInteractionSourceExample() {
    val context = LocalContext.current

    // 🔥 This interaction source is set by outer composable to trigger ripple on inner one
    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
                .background(color = Orange400)
                .clickable {

                    coroutineScope.launch {

                        val press = PressInteraction.Press(Offset.Zero)
                        interactionSource.emit(
                            press
                        )
                        delay(300)
                        interactionSource.emit(
                            PressInteraction.Release(press)
                        )
                    }

                    Toast
                        .makeText(context, "Outer one is clicked", Toast.LENGTH_SHORT)
                        .show()

                }
                .padding(8.dp)

        ) {

            Text("Outer Composable", color = Color.White)

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
                    .background(color = Blue400)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = {
                            Toast
                                .makeText(context, "🔥 Inner one is clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                    .padding(8.dp)
            ) {
                Text("Inner Composable", color = Color.White, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}


@Composable
private fun DiscreteInteractionSourceFLowExample() {

    // 🔥 This interaction source is for the row we will collect interactions on this one
    // and send it to quote to mimic whatsapp message click that highlight also quote
    val interactionSourceSentRow = MutableInteractionSource()
    val interactionSourceSentQuote = MutableInteractionSource()

    LaunchedEffect(key1 = Unit,
        block = {
            interactionSourceSentRow.interactions
                .onEach { interaction: Interaction ->
                    // Quote emits same interaction row had when row is clicked or released
                    interactionSourceSentQuote.emit(interaction)
                }
                .launchIn(this)
        }
    )

    Column(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(SentMessageColor)
                .clickable(
                    interactionSource = interactionSourceSentRow,
                    indication = rememberRipple(),
                    onClick = {}
                ),

            content = {
                // 💬 Quoted message
                QuotedMessageAlt(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        // 🔥 This is required to set Surface height before text is set
                        .height(IntrinsicSize.Min)
                        .background(SentQuoteColor, shape = RoundedCornerShape(8.dp))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = interactionSourceSentQuote,
                            indication = rememberRipple(),
                            onClick = {}
                        ),
                    quotedMessage = "quotedMessage",
                )

                ChatFlexBoxLayout(
                    modifier = Modifier.padding(
                        start = 2.dp,
                        top = 2.dp,
                        end = 4.dp,
                        bottom = 2.dp
                    ),
                    text = "Message",
                    messageStat = {
                        MessageTimeText(
                            modifier = Modifier.wrapContentSize(),
                            messageTime = "12:00 AM",
                            messageStatus = MessageStatus.RECEIVED
                        )
                    }
                )
            }
        )
    }

    // 🔥 This interaction source is for the row we will collect interactions on this one
    // and send it to quote to mimic whatsapp message click that highlight also quote
    val interactionSourceReceivedRow = MutableInteractionSource()
    val interactionSourceReceivedQuote = MutableInteractionSource()

    LaunchedEffect(key1 = Unit,
        block = {
            interactionSourceReceivedRow.interactions
                .onEach { interaction: Interaction ->
                    interactionSourceReceivedQuote.emit(interaction)
                }
                .launchIn(this)
        }
    )

    Column(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // This is chat bubble
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(
                    interactionSource = interactionSourceReceivedRow,
                    indication = rememberRipple(),
                    onClick = {}
                ),
            content = {
                RecipientName(
                    name = recipientRegisteredName,
                    isName = isRecipientRegistered,
                    altName = recipientOriginalName
                )

                // 💬 Quoted message
                QuotedMessageAlt(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        // 🔥 This is required to set Surface height before text is set
                        .height(IntrinsicSize.Min)
                        .background(ReceivedQuoteColor, shape = RoundedCornerShape(8.dp))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = interactionSourceReceivedQuote,
                            indication = rememberRipple(),
                            onClick = {}
                        ),
                    quotedMessage = "quotedMessage",
                )

                ChatFlexBoxLayout(
                    modifier = Modifier.padding(start = 2.dp, end = 4.dp),
                    text = "Message",
                    messageStat = {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                modifier = Modifier.padding(top = 1.dp, bottom = 1.dp, end = 4.dp),
                                text = "12:00 AM",
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        )
    }
}