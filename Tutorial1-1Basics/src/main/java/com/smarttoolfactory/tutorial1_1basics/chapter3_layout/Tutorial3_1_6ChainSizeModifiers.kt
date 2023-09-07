package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
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
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
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

        ChainSizeModifiersSample()
        StyleableTutorialText(
            text = "3-) required modifiers can modify min or/and max Constraints coming from " +
                    "top or Parent, they can widen the measurement range unlike size Modifiers. " +
                    "If the content chooses a size that does not satisfy the " +
                    "incoming Constraints, the parent layout will be reported a size coerced " +
                    "in the Constraints, and the position of the content will be automatically " +
                    "offset to be centered on the space assigned to the child by the parent " +
                    "layout under the assumption that Constraints were respected."
        )
        ChainRequiredSizeModifierSample()
    }
}

@Composable
private fun ChainSizeModifiersSample() {
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

@Composable
private fun ChainRequiredSizeModifierSample() {
    // In these examples requiredWidth constraints do not match the one comes from Modifier.size
    // Because of that parent attempts to place them in center. When required is bigger
    // it's placed at (parent.max-max) at left side. When required is smaller content is
    // place at right side(centered in parent).
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(2.dp, Blue400)
    ) {
        TutorialText2(text = "❌size(100.dp).requiredWidth(140.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .size(100.dp)
                .requiredWidth(140.dp)
        ) {
            Text(
                text = "minWidth: $minWidth, maxWidth: $maxWidth",
                modifier = Modifier.border(3.dp, Color.Green)
            )
        }

        TutorialText2(text = "❌size(100.dp).requiredWidth(80.dp)")
        BoxWithConstraints(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .size(100.dp)
                .requiredWidth(80.dp)
        ) {
            Text(
                text = "minWidth: $minWidth, maxWidth: $maxWidth",
                modifier = Modifier.border(3.dp, Color.Green)
            )
        }
    }
}