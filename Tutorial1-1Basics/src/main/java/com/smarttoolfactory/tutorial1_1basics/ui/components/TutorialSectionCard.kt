package com.smarttoolfactory.tutorial1_1basics.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

@ExperimentalAnimationApi
@Composable
fun TutorialSectionCard(
    model: TutorialSectionModel,
    onClick: ((TutorialSectionModel) -> Unit)? = null,
    onExpandClicked: () -> Unit,
    expanded: Boolean
) {
    Card(
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart
        ) {
            TutorialContentComponent(onClick, model, onExpandClicked, expanded)
            TutorialTagsComponent(model)
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun TutorialContentComponent(
    onClick: ((TutorialSectionModel) -> Unit)?,
    model: TutorialSectionModel,
    onExpandClicked: () -> Unit,
    expanded: Boolean
) {

    Column(Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clickable(
            onClick = { onClick?.invoke(model) }
        )
        .padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = model.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onExpandClicked) {
                // Change vector drawable to expand more or less based on state of expanded
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess
                    else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(expanded) {
            // Description text
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(model.description, style = MaterialTheme.typography.body2)
            }
        }
        // Vertical spacing
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TutorialTagsComponent(model: TutorialSectionModel) {
    Column(Modifier.padding(12.dp)) {

        // Horizontal list for tags
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {

                items(model.tags) { tag ->
                    TutorialChip(text = tag, color = model.tagColor)
                }
            })
    }
}

@ExperimentalAnimationApi
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialSectionCardPreview() {

    val model = TutorialSectionModel(
        title = "1-1 Column/Row Basics",
        description = "Create Rows that adds elements in horizontal order, and Columns that adds elements in vertical order",
        tags = listOf("Jetpack", "Compose", "Rows", "Columns", "Layouts", "Text", "Modifier")
    )

    MaterialTheme {
        TutorialSectionCard(model, onExpandClicked = {}, expanded = true)
    }
}

object HexToJetpackColor {
    @JvmStatic
    fun getColor(colorString: String): Color {
        return Color(android.graphics.Color.parseColor("#$colorString"))
    }
}