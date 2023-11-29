package com.smarttoolfactory.tutorial1_1basics.chapter8_semantics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial8Screen2() {
    // 🔥 You need to enable talkback on your emulator to observe
    // semantics nodes in Composables or check out Ui test tutorials
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        TutorialHeader("Custom Merging")
        StyleableTutorialText(
            // This removes Text semantics to not announce anything
            modifier = Modifier.clearAndSetSemantics { },
            text = "1-) **Modifier.semantics(mergeDescendants = true) {}** " +
                    "merges individual nodes to single node"
        )
        RowItem()

        Spacer(modifier = Modifier.height(16.dp))
        MergedRowItem()

        StyleableTutorialText(
            // This removes Text semantics to not announce anything
            modifier = Modifier.clearAndSetSemantics { },
            text = "2-) ** Modifier.clearAndSetSemantics { }** " +
                    "in existing node and calling " +
                    "**Modifier.semantics(mergeDescendants = true){contentDescription}** turns " +
                    "column into single item in second example and sets contentDescription."
        )

        Column {
            Spacer(modifier = Modifier.height(4.dp))
            MergedRowItem()
            Text("Details about item", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = "Item with details"
            }
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            MergedRowItem(
                modifier = Modifier.clearAndSetSemantics { }
            )
            Text(
                modifier = Modifier.clearAndSetSemantics { },
                text = "Details about item", fontSize = 14.sp
            )
        }

    }
}

@Composable
private fun MergedRowItem(
    modifier: Modifier = Modifier
) {
    RowItem(
        modifier = modifier.semantics(mergeDescendants = true) {}
    )
}

@Composable
private fun RowItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = "Title",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    text = "Subtitle"
                )
            }
        }
    }
}