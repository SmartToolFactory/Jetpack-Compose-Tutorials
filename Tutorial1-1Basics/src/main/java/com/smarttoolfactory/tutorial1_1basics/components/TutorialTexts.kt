package com.smarttoolfactory.tutorial1_1basics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TutorialHeader(text: String, modifier: Modifier = Modifier) {

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        text = text
    )
}

/**
 * [Text] for describing tutorial contents for specific section
 */
@Composable
fun TutorialText(text: String, modifier: Modifier = Modifier, addBullet: Boolean = true) {

    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(style = SpanStyle(fontWeight = FontWeight.Bold), start = 0, end = 3)
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        fontSize = 16.sp,
        text = annotatedString
    )
}

@Composable
fun TutorialText2(text: String, modifier: Modifier = Modifier) {

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {

    Text(
        text = text,
        style = MaterialTheme.typography.body2,
        color = Color.Gray,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
    )
    }
}

@Preview
@Composable
fun TutorialTextPreview() {
    TutorialText("Sample text for demonstrating this text")
}