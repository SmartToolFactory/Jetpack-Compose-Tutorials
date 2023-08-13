package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2


@Preview
@Composable
fun Tutorial3_1Screen6() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Chaining Size Modifiers")

        // In this example only width modifiers are used for demonstration since
        // vertical scroll changes maxHeight to Constraints.Infinity
        StyleableTutorialText(
            text = "1-) Size Modifiers return Constraints which contain min-max ranges " +
                    "for measuring Composables. When chaining Modifiers you can narrow " +
                    "range but it's not allowed to widen it. If first Modifier returns " +
                    "Modifier.width(50.dp) which return min=50.dp, max=50.dp and because of " +
                    "that second **Modifier.size** does not effect resulting Constraints."
        )

        TutorialText2(text = "❌fillMaxWidth().width(50.dp)")

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .width(50.dp)
                .border(2.dp, Color.Red)
        ) {
            Text(text = "minWidth: $minWidth, maxWidth: $maxWidth")
        }

        TutorialText2(text = "❌width(200).width(50.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .width(200.dp)
                .width(50.dp)
                .border(2.dp, Color.Red)
        ) {
            Text(text = "minWidth: $minWidth, maxWidth: $maxWidth")
        }

        StyleableTutorialText(
            text = "2-) **Modifier.width/height/sizeIn** describes a range " +
                    "between min and max values. It's allowed to narrow range but " +
                    "not allowed to widen it as can be seen examples below."
        )

        TutorialText2(text = "✅widthIn(min = 100.dp, max = 200.dp).width(150.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 200.dp)
                .width(150.dp)
                .border(2.dp, Color.Green)
        ) {
            Text(text = "minWidth: $minWidth, maxWidth: $maxWidth")
        }

        TutorialText2(text = "❌widthIn(min = 100.dp, max = 200.dp).width(50.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 200.dp)
                .width(50.dp)
                .border(2.dp, Color.Red)
        ) {
            Text(text = "minWidth: $minWidth, maxWidth: $maxWidth")
        }

        TutorialText2(text = "❌widthIn(min = 100.dp, max = 200.dp).width(250.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 200.dp)
                .width(250.dp)
                .border(2.dp, Color.Red)
        ) {
            Text(text = "minWidth: $minWidth, maxWidth: $maxWidth")
        }
    }
}
