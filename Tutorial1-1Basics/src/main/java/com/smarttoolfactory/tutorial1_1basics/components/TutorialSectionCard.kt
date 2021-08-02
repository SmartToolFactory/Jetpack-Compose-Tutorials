package com.smarttoolfactory.tutorial1_1basics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

@Composable
fun TutorialSectionCard(
    model: TutorialSectionModel,
    onClick: ((TutorialSectionModel) -> Unit)? = null
) {

    Card(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
            .background(Color.White)
            .clickable(
                onClick = { onClick?.invoke(model) }
            ),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)

    ) {

        Column(Modifier.padding(16.dp)) {

            Text(
                text = model.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )

            // Vertical spacing
            Spacer(Modifier.height(8.dp))

            // Description text
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(model.description, style = MaterialTheme.typography.body2)
            }

            // Vertical spacing
            Spacer(Modifier.height(16.dp))

            // Horizontal list for tags
            LazyRow(content = {

                items(model.tags) { tag ->
                    TutorialChip(text = tag)
                    Spacer(Modifier.width(8.dp))
                }
            })
        }
    }
}

@Preview
@Composable
fun TutorialSectionCardPreview() {

    val model = TutorialSectionModel(
        title = "1-1 Column/Row Basics",
        description = "Create Rows that adds elements in horizontal order, and Columns that adds elements in vertical order",
        tags = listOf("Jetpack", "Compose", "Rows", "Columns", "Layouts", "Text", "Modifier")
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(HexToJetpackColor.getColor("EEEEEE"))
                .fillMaxHeight()
                .padding(top = 16.dp)

        ) {
            TutorialSectionCard(model)
            TutorialSectionCard(model)
            TutorialSectionCard(model)
        }
    }
}

object HexToJetpackColor {

    @JvmStatic
    fun getColor(colorString: String): Color {
        return Color(android.graphics.Color.parseColor("#$colorString"))
    }

}