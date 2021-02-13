package com.smarttoolfactory.tutorial1_1basics.chapter1_basics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText2


/**
 * Tutorial about [Column]s, [Row]s, [Box] and [Modifier]s.
 *
 * * [Column] contains it's children in vertical order
 * * [Row] contains it's children in horizontal order.
 * * [Box] stacks it's children on top of each other.
 *
 * * [Modifier] is used to set properties such as dimensions, padding, background color,
 * click action, ***padding***, and more.
 *
 * ## Note
 * Order of modifiers matter. Depending on which order **padding** is added
 * it makes UI component(Compose) to have either margin or padding.
 */
@Composable
fun Tutorial1_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(Modifier.fillMaxSize()) {

        item {
            TutorialHeader(text = "Row")
            TutorialText(text = "1-) Row is a layout composable that places its children in a horizontal sequence.")
            RowExample()

            TutorialHeader(text = "Column")
            TutorialText(text = "2-) Column is a layout composable that places its children in a vertical sequence.")
            ColumnExample()

            TutorialText(
                text = "3-) Padding order determines whether it's padding or margin for that component."
                        + "In example below check out paddings."
            )
            ColumnsAndRowPaddingsExample()

            TutorialText(text = "4-) Shadow can be applied to Column or Row.")
            ShadowExample()

            TutorialHeader(text = "Box")
            TutorialText(
                text = "5-) Box aligns children on top of each other like a Stack. " +
                        "The one declared last is on top"
            )
            BoxExample()

            TutorialText(
                text = "6-) Elements in Box can be aligned with different alignments."
            )
            BoxShadowAndAlignmentExample()

            TutorialHeader(text = "Weight and Spacer")
            TutorialText(
                text = "7-) Weight determines based on total weight how much of the parents " +
                        "dimensions a child should occupy. Spacer to create horizontal or vertical " +
                        "space between components."
            )
            WeightAndSpacerExample()
        }

    }

}

@Composable
fun RowExample() {

    TutorialText2(text = "Arrangement.Start")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        RowTexts()
    }

    TutorialText2(text = "Arrangement.End")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        RowTexts()
    }

    TutorialText2(text = "Arrangement.Center")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        RowTexts()
    }

    TutorialText2(text = "Arrangement.SpaceEvenly")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        RowTexts()
    }

    TutorialText2(text = "Arrangement.SpaceAround")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        RowTexts()
    }

    TutorialText2(text = "Arrangement.SpaceBetween")

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        RowTexts()
    }
}

@Composable
fun ColumnExample() {
    val modifier = Modifier.padding(8.dp)
        .fillMaxWidth()
        .height(160.dp)
        .background(Color.LightGray)

    TutorialText2(text = "Arrangement.Top")
    Column(modifier = modifier, verticalArrangement = Arrangement.Top) {
        ColumnTexts()
    }

    TutorialText2(text = "Arrangement.Bottom")
    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        ColumnTexts()
    }

    TutorialText2(text = "Arrangement.Center")
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        ColumnTexts()
    }

    TutorialText2(text = "Arrangement.SpaceEvenly")
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        ColumnTexts()
    }

    TutorialText2(text = "Arrangement.SpaceAround")
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceAround) {
        ColumnTexts()
    }

    TutorialText2(text = "Arrangement.SpaceBetween")
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        ColumnTexts()
    }
}


@Composable
fun RowTexts() {
    Text(text = "Row1", modifier = Modifier.background(Color(0xFFFF9800)).padding(4.dp))
    Text(text = "Row2", modifier = Modifier.background(Color(0xFFFFA726)).padding(4.dp))
    Text(text = "Row3", modifier = Modifier.background(Color(0xFFFFB74D)).padding(4.dp))
}

@Composable
fun ColumnTexts() {
    Text(text = "Column1", modifier = Modifier.background(Color(0xFF8BC34A)).padding(4.dp))
    Text(text = "Column2", modifier = Modifier.background(Color(0xFF9CCC65)).padding(4.dp))
    Text(text = "Column3", modifier = Modifier.background(Color(0xFFAED581)).padding(4.dp))
}


/**
 * [Column] and [Row] example with padding, background, and fill and wrap content
 * to determine dimensions of contents.
 */
@Composable
fun ColumnsAndRowPaddingsExample() {

    val rowModifier = Modifier
        .background(Color(0xFFF06292))
        .fillMaxWidth()
        .wrapContentHeight()

    // 🔥 Padding after Yellow background leaves space inside container
    val modifierA = Modifier
        .background(Color(0xFFFFEB3B))
        .padding(15.dp)

    // 🔥 Padding(10dp) before cyan color acts as margin while padding end leaves
    // space(padding) for the content inside the container
    val modifierB = Modifier
        .padding(10.dp)
        .background(Color(0xFF80DEEA))
        .padding(end = 15.dp)


    val modifierC = Modifier
        .background(Color(0xFF607D8B))
        .padding(15.dp)

    Row(modifier = rowModifier, horizontalArrangement = Arrangement.SpaceEvenly) {

        Column(modifier = modifierA.background(Color(0xFFFFFFFF)).padding(8.dp)) {
            Text(text = "Text A1")
            Text(text = "Text A2")
            Text(text = "Text A3")
        }

        Column(
            modifier = modifierB.background(Color(0xFF9575CD)).padding(top = 12.dp, bottom = 22.dp)
        ) {
            Text(text = "Text B1")
            Text(text = "Text B2")
            Text(text = "Text B3")
        }

        Column(modifier = modifierC.background(Color(0xFFB2FF59))) {
            Text(text = "Text C1")
            Text(text = "Text C2")
            Text(text = "Text C3")
        }
    }
}

@Composable
fun ShadowExample() {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        RowTexts()
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        ColumnTexts()
    }
}

@Composable
fun BoxExample() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.LightGray)

    ) {

        // This is the one at the bottom
        Text(
            text = "First",
            modifier = Modifier
                .background(Color(0xFF1976D2))
                .size(200.dp),
            color = Color.White,
        )

        // This is the one in the middle
        Text(
            text = "Second",
            modifier = Modifier
                .background(Color(0xFF2196F3))
                .size(150.dp),
            color = Color.White
        )

        // This is the one on top
        Text(
            text = "Third ",
            modifier = Modifier
                .background(Color(0xFF64B5F6))
                .size(100.dp),
            color = Color.White
        )
    }
}

@Composable
fun BoxShadowAndAlignmentExample() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.LightGray)
            .padding(8.dp)
    ) {

        Box(
            modifier = Modifier.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            // This is the one at the bottom
            Text(
                text = "First",
                modifier = Modifier
                    .background(Color(0xFFFFA000))
                    .size(200.dp),
                color = Color.White
            )
        }

        Box(
            modifier = Modifier.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
                .align(Alignment.TopEnd)

        ) {
            // This is the one in the middle
            Text(
                text = "Second",
                modifier = Modifier
                    .background(Color(0xFFFFC107))
                    .size(150.dp),
                color = Color.White
            )
        }

        Box(
            modifier = Modifier.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
                .align(Alignment.BottomStart)

        ) {
            // This is the one on top
            Text(
                text = "Third ",
                modifier = Modifier
                    .background(Color(0xFFFFD54F))
                    .size(100.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun WeightAndSpacerExample() {

    // This is parent modifier
    val modifier = Modifier.fillMaxWidth()
        .height(60.dp)
        .background(Color.LightGray)

    val rowModifier = Modifier
        .fillMaxHeight()
        .background(Color(0xFFA1887F))
        .padding(4.dp)

    Row(modifier = modifier) {

        Text(
            fontSize = 12.sp,
            text = "Weight 2",
            modifier = rowModifier.weight(2f)
        )

        // Spacer creates a space with given modifier width or height based on which scope(row/column) it exists
        Spacer(modifier = modifier.weight(1f))

        Text(
            fontSize = 12.sp,
            text = "Weight 3",
            modifier = rowModifier.weight(3f)
        )

        Spacer(modifier = modifier.weight(1f))

        Text(
            fontSize = 12.sp,
            text = "Weight 4",
            modifier = rowModifier.weight(4f)
        )
    }

    // This spacer is for column which behaves as padding below this component
    Spacer(modifier = Modifier.preferredHeight(16.dp))
}

@Preview
@Composable
fun Tutorial1_1Preview() {
    TutorialContent()
}