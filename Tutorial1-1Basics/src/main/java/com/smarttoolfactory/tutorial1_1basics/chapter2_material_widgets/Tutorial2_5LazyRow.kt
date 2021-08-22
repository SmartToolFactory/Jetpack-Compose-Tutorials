package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.Place
import com.smarttoolfactory.tutorial1_1basics.model.Snack
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.model.snacks
import com.smarttoolfactory.tutorial1_1basics.ui.components.*

@Composable
fun Tutorial2_5Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scrollState = rememberScrollState()

    LazyColumn(content = {

        item {
            Text(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                color = Color(0xffE53935),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                text ="Snacks"
            )
        }

        item {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                content = {
                    items(snacks) { snack: Snack ->
                        HorizontalSnackCard(snack = snack)
                    }
                }
            )
        }

        item {
            Text(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                color = Color(0xff4CAF50),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                text ="Places"
            )
        }


        item {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                content = {
                    items(places) { place: Place ->
                        PlaceCard(place = place)
                    }
                }
            )
        }


        item {
            Text(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                color = Color(0xff4CAF50),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                text ="Places to Stay"
            )
        }


        item {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                content = {
                    items(places) { place: Place ->
                        PlacesToBookComponent(place = place)
                    }
                }
            )
        }

    })
}