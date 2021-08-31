package com.smarttoolfactory.tutorial1_1basics.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.model.Place
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import java.text.DecimalFormat

val decimalFormat = DecimalFormat("0.0")

@Composable
fun PlaceCard(place: Place) {

    val painter = painterResource(id = place.imgRes)
    Box(contentAlignment = Alignment.BottomStart) {
        Surface(
            elevation = 4.dp,
            color = Color.LightGray,
            shape = RoundedCornerShape(8.dp),

            ) {
            Image(
                painter = painter,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .clickable { }
                    .width(150.dp)
                    .height(200.dp),
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = place.description,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun PlaceCardPreview() {
    ComposeTutorialsTheme {
        PlaceCard(places.first())
    }
}

@Composable
fun PlacesToBookComponent(place: Place) {
    Card(
        modifier = Modifier
            .wrapContentWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.clickable { }) {
            PlaceContent(place, Modifier.width(140.dp))
            ImageContent(place)
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun PlacesToBookComponentPreview() {
    ComposeTutorialsTheme {
        PlacesToBookComponent(places.first())
    }
}

@Composable
fun PlacesToBookVerticalComponent(place: Place) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.clickable { }) {
            PlaceContent(place, Modifier.weight(1f))
            ImageContent(place)
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun PlacesToBookVerticalComponentPreview() {
    ComposeTutorialsTheme {
        PlacesToBookVerticalComponent(places.first())
    }
}


@Composable
private fun ImageContent(place: Place) {
    Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.size(140.dp)) {

        val painter = painterResource(id = place.imgRes)

        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            painter = painter,
            contentDescription = null
        )

        Surface(
            shape = CircleShape,
            modifier = Modifier
                .padding(6.dp)
                .size(32.dp),
            color = Color(0x77000000)
        ) {
            FavoriteButton(modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun ImageContentPreview() {
    ComposeTutorialsTheme {
        ImageContent(places.first())
    }
}

@Composable
private fun PlaceContent(place: Place, modifier: Modifier = Modifier) {

    val rating = place.rating

    val triple = getPlaceProperties(rating)

    val color = triple.first
    val text = triple.second
    val startCount = triple.third

    Column(
        modifier = modifier
            .height(140.dp)
            .padding(8.dp)
    ) {

        // 🔥 This weight pushes price text to bottom
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.description,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                repeat(startCount) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_star_12),
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color

                ) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = decimalFormat.format(rating),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = text, color = color, fontSize = 12.sp)
            }
        }

        Row {
            // 🔥 This Spacer with weight pushes price tag to most right of this context
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${place.price}",
                color = Color(0xff4CAF50)
            )
        }
    }
}

@Composable
private fun getPlaceProperties(rating: Double): Triple<Color, String, Int> {
    val triple = if (rating > 8) {
        Triple(Color(0xff4CAF50), "Very good", 5)
    } else if (rating <= 8 && rating > 6) {
        Triple(Color(0xff2196F3), "Good", 4)
    } else if (rating > 4 && rating <= 6) {
        Triple(Color(0xffFFEB3B), "Mediocre", 3)

    } else if (rating > 2 && rating <= 4) {
        Triple(Color(0xffFF9800), "Bad", 2)
    } else {
        Triple(Color(0xffF44336), "Very bad", 1)
    }
    return triple
}

@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun PlaceContentPreview() {
    ComposeTutorialsTheme {
        PlaceContent(places.first())
    }
}