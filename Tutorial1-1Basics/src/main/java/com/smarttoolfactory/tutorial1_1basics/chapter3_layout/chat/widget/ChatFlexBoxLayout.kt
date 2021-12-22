package com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Layout that contains message and message status. [messageStat] is positioned based on
 * how many lines [text] has and with of message composable and [messageStat] or parent of
 * this composable. [messageStat] can be position right side or bottom or top of last line
 * of the message.
 *
 * Since [TextLayoutResult] is required for text properties composable contains message but
 * [messageStat] is a parameter of this function which can be created in lambda block.
 *
 * @param text This is the context of the message as [AnnotatedString].
 * @param fontSize of message [Text].
 * @param fontStyle of message [Text].
 * @param fontWeight of message [Text].
 * @param fontFamily of message [Text].
 * @param letterSpacing of message [Text].
 * @param textDecoration of message [Text].
 * @param textAlign of message [Text].
 * @param lineHeight of message [Text].
 * @param overflow of message [Text].
 * @param softWrap of message [Text].
 * @param maxLines of message [Text].
 * @param messageStat composable that might contain message date and message receive status.
 * @param onMeasure returns results from measuring and positioning chat components.
 */
@Composable
fun ChatFlexBoxLayout(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    messageStat: @Composable () -> Unit,
    onMeasure: ((ChatRowData) -> Unit)? = null
) {
    ChatFlexBoxLayout(
        modifier= modifier,
        text = text.text,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        messageStat = messageStat,
        onMeasure = onMeasure
    )
}


/**
 * Layout that contains message and message status. [messageStat] is positioned based on
 * how many lines [text] has and with of message composable and [messageStat] or parent of
 * this composable. [messageStat] can be position right side or bottom or top of last line
 * of the message.
 *
 * Since [TextLayoutResult] is required for text properties composable contains message but
 * [messageStat] is a parameter of this function which can be created in lambda block.
 *
 * @param text This is the context of the message.
 * @param fontSize of message [Text].
 * @param fontStyle of message [Text].
 * @param fontWeight of message [Text].
 * @param fontFamily of message [Text].
 * @param letterSpacing of message [Text].
 * @param textDecoration of message [Text].
 * @param textAlign of message [Text].
 * @param lineHeight of message [Text].
 * @param overflow of message [Text].
 * @param softWrap of message [Text].
 * @param maxLines of message [Text].
 * @param messageStat composable that might contain message date and message receive status.
 * @param onMeasure returns results from measuring and positioning chat components.
 */
@Composable
fun ChatFlexBoxLayout(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    messageStat: @Composable () -> Unit,
    onMeasure: ((ChatRowData) -> Unit)? = null
) {
    val chatRowData = remember { ChatRowData() }
    val content = @Composable {

        Message(
            modifier = modifier
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .wrapContentSize(),
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                // maxWidth of text constraint returns parent maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth =
                    textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            }
        )

        messageStat()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        if (measurables.size != 2)
            throw IllegalArgumentException("There should be 2 components for this layout")

//        println("⚠️ CHAT constraints: $constraints")

        val placeables: List<Placeable> = measurables.map { measurable ->
            // Measure each child maximum constraints since message can cover all of the available
            // space by parent
            measurable.measure(Constraints(0, constraints.maxWidth))
        }

        val message = placeables.first()
        val status = placeables.last()

        // calculate chat row dimensions are not  based on message and status positions
        if ((chatRowData.rowWidth == 0 || chatRowData.rowHeight == 0) || chatRowData.text != text) {
            // Constrain with max width instead of longest sibling
            // since this composable can be longest of siblings after calculation
            chatRowData.parentWidth = constraints.maxWidth
            calculateChatWidthAndHeight(text, chatRowData, message, status)
            // Parent width of this chat row is either result of width calculation
            // or quote or other sibling width if they are longer than calculated width.
            // minWidth of Constraint equals (text width + horizontal padding)
            chatRowData.parentWidth =
                chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)
        }

//        println("⚠️⚠️ CHAT after calculation-> CHAT_ROW_DATA: $chatRowData")

        // Send measurement results if requested by Composable
        onMeasure?.invoke(chatRowData)

        layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {

            println(
                "⚠️⚠️⚠️ CHAT layout() status x: ${chatRowData.parentWidth - status.width}, " +
                        "y: ${chatRowData.rowHeight - status.height}"
            )

            message.placeRelative(0, 0)
            // set left of status relative to parent because other elements could result this row
            // to be long as longest composable
            status.placeRelative(
                chatRowData.parentWidth - status.width,
                chatRowData.rowHeight - status.height
            )
        }
    }
}

private fun calculateChatWidthAndHeight(
    text: String,
    chatRowData: ChatRowData,
    message: Placeable,
    status: Placeable?,
) {

    if (status != null) {

        val lineCount = chatRowData.lineCount
        val lastLineWidth = chatRowData.lastLineWidth
        val parentWidth = chatRowData.parentWidth

        val padding = (message.measuredWidth - chatRowData.textWidth) / 2
//        println(
//            "🌽 CHAT INIT calculate() text: $text\n" +
//                    "lineCount: $lineCount, parentWidth: $parentWidth, lastLineWidth: $lastLineWidth\n" +
//                    "MESSAGE width: ${message.width}, measured: ${message.measuredWidth}," +
//                    " textWidth: ${chatRowData.textWidth} padding: $padding\n" +
//                    "STATUS width: ${status.width}, measured: ${status.measuredWidth}, " +
//                    "(stat +last): ${lastLineWidth + status.measuredWidth}\n"
//        )

        // Multiple lines and last line and status is longer than text size and right padding
        if (lineCount > 1 && lastLineWidth + status.measuredWidth >= chatRowData.textWidth + padding) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 0
//            println("🤔 CHAT calculate() 0 for ${chatRowData.textWidth + padding}")
        } else if (lineCount > 1 && lastLineWidth + status.measuredWidth < chatRowData.textWidth + padding) {
            // Multiple lines and last line and status is shorter than text size and right padding
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 1
//            println("🔥 CHAT calculate() 1 for ${message.measuredWidth - padding}")
        } else if (lineCount == 1 && message.width + status.measuredWidth >= parentWidth) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 2
//            println("🎃 CHAT calculate() 2")
        } else {
            chatRowData.rowWidth = message.measuredWidth + status.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 3
//            println("🚀 CHAT calculate() 3")
        }
    } else {
        chatRowData.rowWidth = message.width
        chatRowData.rowHeight = message.height
    }
}

@Composable
private fun Message(
    modifier: Modifier = Modifier,
    text: String,
    onTextLayout: (TextLayoutResult) -> Unit,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = text,
        onTextLayout = onTextLayout,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
}

data class ChatRowData(
    var text: String = "",
    // Width of the text without padding
    var textWidth: Int = 0,
    var lastLineWidth: Float = 0f,
    var lineCount: Int = 0,
    var rowWidth: Int = 0,
    var rowHeight: Int = 0,
    var parentWidth: Int = 0,
    var measuredType: Int = 0,
) {

    override fun toString(): String {
        return "ChatRowData text: $text, " +
                "lastLineWidth: $lastLineWidth, lineCount: $lineCount, " +
                "textWidth: ${textWidth}, rowWidth: $rowWidth, height: $rowHeight, " +
                "parentWidth: $parentWidth, measuredType: $measuredType"
    }
}

