package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

/*
    This tutorial uses references from this medium article. Check it out.
    https://medium.com/mobile-app-development-publication/android-jetpack-compose-recomposition-made-easy-527ecc9bcbaf
 */

/*
    Buttons
 */
var update1 by mutableStateOf(0)
var update2 by mutableStateOf(0)
var update3 by mutableStateOf(0)


/*
    Flat design
 */
var recomposeFlatCombined1 = 0
var recomposeFlatCombined2 = 0
var recomposeFlatCombined3 = 0

var recomposeFlatTop = 0
var recomposeFlatMiddle = 0
var recomposeFlatBottom = 0

/*
    Hierarchical design
 */
var recomposeHierarchical1 = 0
var recomposeHierarchical2 = 0
var recomposeHierarchical3 = 0

var recomposeHierarchicalOuter = 0
var recomposeHierarchicalCenter = 0
var recomposeHierarchicalInner = 0

@Composable
fun Tutorial4_2_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        UpdateButtons()

        StyleableTutorialText(
            text = "**FlatCombined:** Composables are siblings. **Every Composable** gets recomposed.",
            bullets = false
        )
        FlatCombined()
        StyleableTutorialText(
            text = "**FlatSeparated:** Composables are sibling. **Only suitable Composable** gets recomposed.",
            bullets = false
        )
        FlatSeparated()
        StyleableTutorialText(
            text = "**HierarchicalCombined:** Composables are parent and children. **Every Composable** gets recomposed.",
            bullets = false
        )
        HierarchicalCombined()
        StyleableTutorialText(
            text = "**HierarchicalSeparated:**  Composables are parent and children. **Only suitable Composable** gets recomposed.",
            bullets = false
        )
        HierarchicalSeparated()
    }
}

@Composable
private fun FlatSeparated() {
    Column(Modifier.padding(horizontal = 8.dp)) {
        FlatTopLayer()
        FlatMiddleLayer()
        FlatBottomLayer()
    }
}

@Composable
private fun FlatTopLayer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = orange400)
    ) {

        recomposeFlatTop++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeFlatTop; Update: $update1",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FlatMiddleLayer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = blue400)
    ) {

        recomposeFlatMiddle++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeFlatMiddle; Update: $update2",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FlatBottomLayer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = pink400)
    ) {

        recomposeFlatBottom++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeFlatBottom; Update: $update3",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FlatCombined() {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {

        Column(Modifier.background(color = orange400)) {
            recomposeFlatCombined1++
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Recompose: $recomposeFlatCombined1; Update: $update1",
                textAlign = TextAlign.Center
            )
        }

        Column {
            recomposeFlatCombined2++
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = blue400),
                text = "Recompose: $recomposeFlatCombined2; Update: $update2",
                textAlign = TextAlign.Center
            )
        }

        Column {
            recomposeFlatCombined3++
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = pink400),
                text = "Recompose: $recomposeFlatCombined3; Update: $update3",
                textAlign = TextAlign.Center
            )
        }
    }
}

/*
    Hierarchical Structure
 */
@Composable
private fun HierarchicalCombined() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
            .background(color = orange400)


    ) {

        recomposeHierarchical1++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeHierarchical1; Update: $update1",
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
                .background(color = blue400)
        ) {

            recomposeHierarchical2++
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Recompose: $recomposeHierarchical2; Update: $update2",
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
                    .background(color = pink400)



            ) {

                recomposeHierarchical3++
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Recompose: $recomposeHierarchical3; Update: $update3",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HierarchicalSeparated() {
    HierarchicalOuterLayer()
}

@Composable
private fun HierarchicalOuterLayer() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
            .background(color = orange400)
    ) {

        recomposeHierarchicalOuter++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeHierarchicalOuter; Update: $update1",
            textAlign = TextAlign.Center
        )

        HierarchicalCenterLayer()
    }
}

@Composable
private fun HierarchicalCenterLayer() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
            .background(color = blue400)

    ) {

        recomposeHierarchicalCenter++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeHierarchicalCenter; Update: $update2",
            textAlign = TextAlign.Center
        )

        HierarchicalInnerLayer()
    }
}

@Composable
private fun HierarchicalInnerLayer() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp), clip = true)
            .background(color = pink400)
    ) {

        recomposeHierarchicalInner++
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Recompose: $recomposeHierarchicalInner; Update: $update3",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UpdateButtons() {
    Column {

        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = orange400),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp)
                .fillMaxWidth(),
            onClick = { update1++ },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Update: $update1",
                textAlign = TextAlign.Center
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = blue400),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 2.dp)
                .fillMaxWidth(),
            onClick = { update2++ },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Update: $update2",
                textAlign = TextAlign.Center
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = pink400),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 2.dp)
                .fillMaxWidth(),
            onClick = { update3++ },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Update: $update3",
                textAlign = TextAlign.Center
            )
        }
    }
}
