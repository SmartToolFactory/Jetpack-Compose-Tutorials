package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.components.*

@Composable
fun Tutorial2_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(Modifier.fillMaxSize()) {

        item {

            val paddingModifier = Modifier.padding(8.dp)

            TutorialHeader(text = "Button")

            TutorialText2(text = "Button")
            ButtonExample(paddingModifier)
            DisabledButtonExample(paddingModifier)
            ButtonWithIconExample(paddingModifier)
            ButtonBackgroundExample(paddingModifier)
            GradientButtonExample(paddingModifier)

            TutorialText2(text = "Icon Button")
            IconButtonExample(paddingModifier)

            TutorialHeader(text = "Floating Action Button")
            FloatingActionButtonExample(paddingModifier)

            TutorialHeader(text = "Chip")
            ChipExample(paddingModifier)
        }
    }
}

@Composable
private fun ButtonExample(modifier: Modifier) {
    FullWidthRow {

        Button(onClick = {}, modifier = modifier) {
            Text(text = "Button")
        }

        TextButton(onClick = {}, modifier = modifier) {
            Text(text = "TextButton")
        }

        OutlinedButton(
            onClick = {},
            modifier = modifier,
        ) {
            Text(text = "Outlined")
        }
    }

    FullWidthRow {

        Button(
            onClick = {},
            modifier = modifier,
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text = "Rounded")
        }

        Button(
            onClick = {},
            modifier = modifier,
            shape = RoundedCornerShape(
                topStartPercent = 30,
                topEndPercent = 0,
                bottomStartPercent = 0,
                bottomEndPercent = 0
            )
        ) {
            Text(text = "Rounded")
        }

        Button(
            onClick = { },
            modifier = modifier,
            shape = CutCornerShape(20)
        ) {
            Text(text = "CutCorner")
        }
    }
}

@Composable
private fun DisabledButtonExample(modifier: Modifier) {
    FullWidthRow {
        Button(
            onClick = {},
            modifier = modifier,
            enabled = false
        ) {
            Text(text = "Button")
        }

        TextButton(
            onClick = {},
            modifier = modifier,
            enabled = false
        ) {
            Text(text = "TextButton")
        }

        OutlinedButton(
            onClick = {},
            modifier = modifier,
            enabled = false
        ) {
            Text(text = "Outlined")
        }
    }
}

@Composable
private fun ButtonWithIconExample(modifier: Modifier) {
    FullWidthRow {
        Button(
            onClick = {},
            modifier = modifier
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    modifier = Modifier.padding(end = 4.dp),
                    contentDescription = null
                )
                Text(text = "Icon+Text")
            }
        }

        Button(
            onClick = {},
            modifier = modifier
        ) {
            Text(text = "Text+Icon")
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                modifier = Modifier.padding(start = 4.dp),
                contentDescription = null
            )
        }

        Button(
            onClick = {},
            shape = RoundedCornerShape(20),
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null
            )
        }
    }

    FullWidthRow {
        OutlinedButton(
            onClick = {},
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                contentDescription = null
            )
            Text(text = "Icon+Text+Icon")
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                modifier = Modifier.padding(start = 4.dp),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ButtonBackgroundExample(modifier: Modifier) {
    FullWidthRow {

        Button(
            onClick = {},
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xffF57C00),
                contentColor = Color(0xffB2EBF2)
            )
        ) {
            Text(text = "Button")
        }

        TextButton(
            onClick = {},
            modifier = modifier,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xff8BC34A)
            )
        ) {
            Text(text = "TextButton")
        }

        OutlinedButton(
            onClick = {},
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xff795548)
            )
        ) {
            Text(text = "Outlined")
        }
    }
}

@Composable
private fun GradientButtonExample(modifier: Modifier) {

    val horizontalGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xffF57F17),
            Color(0xffFFEE58),
            Color(0xffFFF9C4)
        )
    )

    val verticalGradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xff4E342E),
            Color(0xff8D6E63),
            Color(0xffD7CCC8)
        )
    )

    FullWidthRow {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(20))
                .background(brush = horizontalGradientBrush)
                .clickable(onClick = { })
                .then(modifier)
        ) {
            Text(text = "Horizontal Gradient")
        }

        Column(
            modifier = modifier
                .clip(RoundedCornerShape(20))
                .background(brush = verticalGradientBrush)
                .clickable(onClick = { })
                .then(modifier)
        ) {
            Text(text = "Vertical Gradient")
        }

    }

    FullWidthRow {
        Button(contentPadding = PaddingValues(0.dp), onClick = {}) {
            Text(
                text = "Vertical Gradient",
                modifier = modifier
                    .preferredHeight(ButtonDefaults.MinHeight)
                    .align(Alignment.CenterVertically)
                    .background(brush = verticalGradientBrush)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun IconButtonExample(modifier: Modifier) {

    FullWidthRow {
        IconButton(onClick = {}, modifier = modifier) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = null
            )
        }

        var checked by remember { mutableStateOf(false) }

        IconToggleButton(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = modifier
        ) {
            val tint = animate(if (checked) Color(0xffE91E63) else Color(0xffB0BEC5))
            Icon(
                Icons.Filled.Favorite, tint = tint,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun FloatingActionButtonExample(modifier: Modifier) {

    FullWidthRow {

        FloatingActionButton(
            onClick = {},
            modifier = modifier,
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = null
            )
        }

        FloatingActionButton(
            onClick = {},
            modifier = modifier,
            backgroundColor = Color(0xffFFA000)
        ) {
            Icon(
                Icons.Filled.Done, tint = Color.White,
                contentDescription = null
            )
        }

        ExtendedFloatingActionButton(
            text = { Text("Extended") },
            onClick = {},
            modifier = modifier
        )

        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite, tint = Color.White,
                    contentDescription = null
                )
            },
            text = { Text("Like", color = Color.White) },
            backgroundColor = Color(0xffEC407A),
            onClick = {},
            modifier = modifier
        )
    }
}

@Composable
fun ChipExample(modifier: Modifier) {

    // Chips are not available natively as of Compose 1.0.0-alpha09 but can be implemented
    // with ease with Cards, Surfaces or Buttons.

    FullWidthRow {
        TutorialChip(modifier = modifier, text = "Chip")
        Chip(
            modifier = modifier,
            text = "Chip",
            drawableRes = R.drawable.avatar_1_raster,
            closable = true
        )
        OutlinedChip(
            modifier = modifier,
            text = "Outlined",
            drawableRes = R.drawable.avatar_2_raster,
            closable = true
        )
    }
}

