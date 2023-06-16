package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
@Composable
fun Tutorial2_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(Modifier.fillMaxSize()) {

        item {
            TutorialHeader(text = "Text")

            TutorialText2(text = "Font Color")
            TextSampleRow {
                CustomText(text = "Red 700", color = Color(0xffd32f2f))
                CustomText(text = "Purple 700", color = Color(0xff7B1FA2))
                CustomText(text = "Green 700", color = Color(0xff1976D2))
                CustomText(text = "Teal 700", color = Color(0xff00796B))
            }

            // Examples about font related properties
            TextFontExample()

            TutorialText2(text = "Letter Spacing")
            TextSampleRow {
                CustomText(text = " LS:0.4sp", letterSpacing = 0.4.sp)
                CustomText(text = "LS:1sp", letterSpacing = 1.sp)
                CustomText(text = "LS:2sp", letterSpacing = 2.sp)
                CustomText(text = "LS:4sp", letterSpacing = 4.sp)
            }

            TutorialText2(text = "Text Decoration")
            TextSampleRow {
                CustomText(text = "Underline", textDecoration = TextDecoration.Underline)
                CustomText(text = "LineThrough", textDecoration = TextDecoration.LineThrough)
                CustomText(
                    text = "Underline+LineThrough", textDecoration = TextDecoration.combine(
                        listOf(
                            TextDecoration.Underline,
                            TextDecoration.LineThrough
                        )
                    )
                )
            }

            TutorialText2(text = "Line Height")
            CustomText(
                text = "This text has line height of 15 sp. Line height for the Paragraph in TextUnit unit, e.g. SP or EM.",
                lineHeight = 15.sp
            )

            Divider(modifier = Modifier.padding(4.dp))
            CustomText(
                text = "This text has line height of 20 sp. Line height for the Paragraph in TextUnit unit, e.g. SP or EM.",
                lineHeight = 20.sp
            )
            Divider(modifier = Modifier.padding(4.dp))
            CustomText(
                text = "This text has line height of 25 sp. Line height for the Paragraph in TextUnit unit, e.g. SP or EM.",
                lineHeight = 25.sp
            )
            Divider(modifier = Modifier.padding(4.dp))
            CustomText(
                text = "This text has line height of 30 sp. Line height for the Paragraph in TextUnit unit, e.g. SP or EM.",
                lineHeight = 30.sp
            )
            Divider(modifier = Modifier.padding(4.dp))

            TutorialText2(text = "Overflow")
            CustomText(
                text = "Clip the overflowing text to fix its container. " +
                        "If the text exceeds the given number of lines, it will be truncated according to " +
                        " overflow and softWrap.",
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            CustomText(
                text = "Use an ellipsis to indicate that the text has overflowed. " +
                        "If the text exceeds the given number of lines, it will be truncated according to " +
                        " overflow and softWrap.",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            // Draw background and border examples
            TextBackgroundAndBorderExample()

            // Draw text shadow
            TextShadowExample()

            // Draw Spannable example
            SpannableTextExample()

            // Draw Subscript and SuperScript text
            SubscriptSuperscriptTextExample()

            SelectableTextExample()
            Spacer(modifier = Modifier.padding(bottom = 32.dp))


        }
    }
}

@Composable
private fun TextFontExample() {
    TutorialText2(text = "Font Size")
    TextSampleRow {
        CustomText(text = "14sp", fontSize = 14.sp)
        CustomText(text = "18sp", fontSize = 18.sp)
        CustomText(text = "30sp", fontSize = 30.sp)
        CustomText(text = "40sp", fontSize = 40.sp)
    }

    TutorialText2(text = "Font Style")
    TextSampleRow {
        CustomText(text = "Normal", fontStyle = FontStyle.Normal)
        CustomText(text = "Italic", fontStyle = FontStyle.Italic)
    }

    TutorialText2(text = "Font Weight")
    TextSampleRow {
        CustomText(text = "Thin", fontWeight = FontWeight.Thin)
        CustomText(text = "ExtraLight", fontWeight = FontWeight.ExtraLight)
        CustomText(text = "Light", fontWeight = FontWeight.Light)
        CustomText(text = "Normal", fontWeight = FontWeight.Normal)
        CustomText(text = "Medium", fontWeight = FontWeight.Medium)
    }
    TextSampleRow {
        CustomText(text = "SemiBold", fontWeight = FontWeight.SemiBold)
        CustomText(text = "Bold", fontWeight = FontWeight.Bold)
        CustomText(text = "ExtraBold", fontWeight = FontWeight.ExtraBold)
        CustomText(text = "Black", fontWeight = FontWeight.Black)
    }

    TutorialText2(text = "Font Family")
    TextSampleRow {
        CustomText(text = "Default", fontFamily = FontFamily.Default)
        CustomText(text = "Cursive", fontFamily = FontFamily.Cursive)
        CustomText(text = "Monospace", fontFamily = FontFamily.Monospace)
        CustomText(text = "SansSerif", fontFamily = FontFamily.SansSerif)
        CustomText(text = "Serif", fontFamily = FontFamily.Serif)
    }
}

/**
 * Draw [Text]s with background or border with solid or gradient colors
 */
@Composable
private fun TextBackgroundAndBorderExample() {
    TutorialText2(text = "Background")
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val horizontalGradientBrush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xffF57F17),
                Color(0xffFFEE58),
                Color(0xffFFF9C4)
            )
        )

        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            text = "Orange 500",
            modifier = Modifier
                .background(Color(0xffFF9800), shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        )
        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            text = "Cyan 500",
            modifier = Modifier
                .background(Color(0xff00BCD4), shape = CutCornerShape(topStartPercent = 25))
                .padding(20.dp)
        )
        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = "Gradient",
            modifier = Modifier
                .background(brush = horizontalGradientBrush)
                .padding(20.dp)
        )
    }

    TutorialText2(text = "Border")
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val verticalGradientBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xff4E342E),
                Color(0xff8D6E63),
                Color(0xffD7CCC8)
            )
        )

        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = "Orange 500",
            modifier = Modifier
                .border(width = 4.dp, Color(0xffFF9800), shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        )
        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = "Cyan 500",
            modifier = Modifier
                .border(
                    width = 4.dp,
                    Color(0xff00BCD4),
                    shape = CutCornerShape(topStartPercent = 25)
                )
                .padding(20.dp)
        )
        CustomText(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = "Gradient",
            modifier = Modifier
                .border(width = 4.dp, brush = verticalGradientBrush, shape = RectangleShape)
                .padding(20.dp)
        )
    }
}

@Composable
private fun TextShadowExample() {

    TutorialText2(text = "Shadow")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            text = "Shadow1",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Gray, blurRadius = 5f,
                    offset = Offset(5f, 5f)
                )
            )
        )
        Text(
            text = "Shadow2",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black, blurRadius = 10f,
                    offset = Offset(-5f, -5f)
                )
            )
        )
        Text(
            text = "Shadow3",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Red, blurRadius = 20f,
                    offset = Offset(5f, 5f)
                )
            )
        )
    }
}

@Composable
private fun SpannableTextExample(modifier: Modifier = Modifier) {

    TutorialText2(text = "Spannable Text")

    val annotatedColorString = buildAnnotatedString {
        append("RedGreenBlue")
        addStyle(style = SpanStyle(color = Color.Red, fontSize = 24.sp), start = 0, end = 3)
        addStyle(
            style = SpanStyle(
                color = Color.Green,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ), start = 3, end = 8
        )
        addStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontSize = 26.sp,
                textDecoration = TextDecoration.Underline
            ),
            start = 8,
            end = this.length
        )
    }

    Text(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = annotatedColorString
    )

    TutorialText2(text = "ClickableText")

    val annotatedLinkString: AnnotatedString = buildAnnotatedString {

        val str = "Click this link to go to web site"
        val startIndex = str.indexOf("link")
        val endIndex = startIndex + 4
        append(str)

        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                fontSize = 18.sp,
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )

        // attach a string annotation that stores a URL to the text "Jetpack Compose".
        addStringAnnotation(
            tag = "URL",
            annotation = "https://github.com/SmartToolFactory",
            start = startIndex,
            end = endIndex
        )

    }

    // UriHandler parse and opens URI inside AnnotatedString Item in Browse
    val uriHandler: UriHandler = LocalUriHandler.current

    // 🔥 Clickable text returns position of text that is clicked in onClick callback
    ClickableText(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = annotatedLinkString,
        onClick = {
            annotatedLinkString
                .getStringAnnotations(it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    println("🔥 Clicked: $it, item: ${stringAnnotation.item}")
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

@Composable
private fun SubscriptSuperscriptTextExample() {

    TutorialText2(text = "Subscript and Superscript")
    // create a variable superScript
    // enter the baselineShift to
    // BaselineShift.Superscript for superscript
    val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 14.sp, // font size of superscript
        color = Color.Red // color
    )

    // create a variable subScript
    // enter the baselineShift to
    // BaselineShift.Subscript for subscript
    val subscript = SpanStyle(
        baselineShift = BaselineShift.Subscript,
        fontSize = 14.sp, // font size of subscript
        color = Color.Blue // color
    )

    // create first text
    Text(
        modifier = Modifier.padding(16.dp),
        fontSize = 20.sp,
        text = buildAnnotatedString {
            // instead of directly passing
            // string value to text
            // use append
            append("E = mc")
            withStyle(superscript) {
                append("2")
            }
        }
    )

    // Create second text
    Text(
        modifier = Modifier.padding(16.dp),
        fontSize = 20.sp,
        text = buildAnnotatedString {
            // instead of directly passing
            // string value to text
            // use append
            append("CH")
            withStyle(subscript) {
                append("4")
            }
            append(" + H")
            withStyle(subscript) {
                append("2")
            }
            append("O = CO + 3H")
            withStyle(subscript) {
                append("2")
            }
        }
    )
}

@Composable
private fun SelectableTextExample() {
    TutorialText2(text = "Selectable Text")
    SelectionContainer {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                    "when an unknown printer took a galley of type and scrambled " +
                    "it to make a type specimen book."
        )
    }
}


@Composable
private fun CustomText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
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
    style: TextStyle = LocalTextStyle.current
) {

    Text(
        text = text,
        modifier = modifier,
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
        style = style
    )
}

/**
 * Sample Row to display [Text] components with different features side by side
 */
@Composable
fun TextSampleRow(content: @Composable () -> Unit) {

    val rowModifier = Modifier
        .fillMaxWidth()
        .background(Color.LightGray)

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        content()
    }
}