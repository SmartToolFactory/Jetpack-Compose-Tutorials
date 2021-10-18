package com.smarttoolfactory.tutorial1_1basics.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.model.SuggestionModel

@Composable
fun TutorialChip(modifier: Modifier = Modifier, text: String, color:Color = Color(0xff00BCD4)) {
    Card(
        elevation = 0.dp,
        modifier = modifier,
        backgroundColor = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp, 8.dp)
                    .clip(CircleShape)
                    .background(color = color)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes drawableRes: Int = -1,
    cancelable: Boolean = false
) {

    Surface(
        elevation = 0.dp,
        modifier = modifier,
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (cancelable) {
                CircleCloseButton(Modifier.padding(end = 8.dp))
            }
        }
    }
}

@Composable
fun CancelableChip(
    modifier: Modifier = Modifier,
    suggestion: SuggestionModel,
    @DrawableRes drawableRes: Int = -1,
    onClick: ((SuggestionModel) -> Unit)? = null,
    onCancel: ((SuggestionModel) -> Unit)? = null
) {

    Surface(
        elevation = 0.dp,
        modifier = modifier,
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClick?.run {
                        invoke(suggestion)
                    }
                }
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ) {

            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }

            Text(
                text = suggestion.tag,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(end = 8.dp)
            )

            Surface(color = Color.DarkGray, modifier = Modifier, shape = CircleShape) {
                IconButton(
                    onClick = {
                        onCancel?.run {
                            invoke(suggestion)
                        }
                    },
                    modifier = Modifier
                        .size(16.dp)
                        .padding(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        tint = Color(0xFFE0E0E0),
                        contentDescription = null
                    )
                }
            }
        }
    }
}


@Composable
fun OutlinedChip(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes drawableRes: Int = -1,
    closable: Boolean = false
) {
    Surface(
        elevation = 0.dp,
        modifier = modifier,
        border = BorderStroke(width = 1.dp, Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (closable) {
                CircleCloseButton(Modifier.padding(end = 8.dp))
            }
        }
    }
}

@Composable
fun CircleCloseButton(modifier: Modifier = Modifier) {
    Surface(color = Color.DarkGray, modifier = modifier, shape = CircleShape) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(16.dp)
                .padding(1.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                tint = Color(0xFFE0E0E0),
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
private fun TutorialChipReview() {
    TutorialChip(text = "Tutorial Chip")
}

@Composable
@Preview
private fun ChipPreview() {
    Chip(text = "Chip", drawableRes = R.drawable.avatar_1_raster, cancelable = true)
}

@Composable
@Preview
private fun SuggestionChipReview() {
    CancelableChip(suggestion = SuggestionModel("Suggestion"))
}

@Composable
@Preview
private fun SuggestionChipWithIconReview() {
    CancelableChip(
        suggestion = SuggestionModel("Suggestion"),
        drawableRes = R.drawable.avatar_2_raster
    )
}

@Composable
@Preview
private fun OutlinedChipPreview() {
    OutlinedChip(text = "Outlined", drawableRes = R.drawable.avatar_2_raster, closable = true)

}
