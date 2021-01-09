package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientTextStyle
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.HtmlCompat
import com.smarttoolfactory.tutorial1_1basics.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText2

// TODO Fix HyperLink
@Composable
fun Tutorial2_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    ScrollableColumn(Modifier.fillMaxSize()) {

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

        Spacer(modifier = Modifier.padding(bottom = 32.dp))
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
                .background(Color(0xff00BCD4), shape = CutCornerShape(topLeftPercent = 25))
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
                    shape = CutCornerShape(topLeftPercent = 25)
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

    val annotatedLinkString: AnnotatedString = buildAnnotatedString {

        val str = "Click this link to go to web site"
        val startIndex = str.indexOf("link")
        val endIndex = startIndex + 4
        append(str)
        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )

        // attach a string annotation that stores a URL to the text "Jetpack Compose".
        addStringAnnotation(
            tag = "URL",
            annotation = "http://www.github.com",
            start = startIndex,
            end = endIndex
        )

    }

  val str =  AnnotatedString.Builder().apply {
        append("link: Jetpack Compose")
        // attach a string annotation that stores a URL to the text "Jetpack Compose".
        addStringAnnotation(
            tag = "URL",
            annotation = "https://developer.android.com/jetpack/compose",
            start = 6,
            end = 21
        )
    }.toAnnotatedString()

  val linkText =  with(AnnotatedString.Builder()) {
        append("link: Jetpack Compose")
        // attach a string annotation that stores a URL to the text "Jetpack Compose".
        addStringAnnotation(
            tag = "URL",
            annotation = "https://developer.android.com/jetpack/compose",
            start = 6,
            end = 21
        )

        toAnnotatedString()
    }

    Text(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = linkText
    )
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
    style: TextStyle = AmbientTextStyle.current
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