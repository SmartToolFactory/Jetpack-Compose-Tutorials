package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.model.Place
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.ui.components.PlaceCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun Tutorial4_5_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        SideEffectSample()
        ProduceStateSampleButton()
        DerivedStateOfSample()
        DerivedStateOfSample2()
    }
}

/**
 * SideEffect function is triggered every time a recomposition occurs.
 *
 * SideEffect can be used to apply side effects to objects managed by the composition
 * that are not backed by snapshots so as not to leave those objects
 * in an inconsistent state if the current composition operation fails.
 */
@Composable
private fun SideEffectSample() {

    val context = LocalContext.current

    // Updates composable that listens changes in value of this State
    var counterOuter by remember { mutableStateOf(0) }
    // Updates composable that listens changes in value of this State
    var counterInner by remember { mutableStateOf(0) }

    // only runs first time SideEffectSample is called
    SideEffect {
        Toast.makeText(context, "SideEffectSample()", Toast.LENGTH_SHORT).show()
    }
    Column(
        Modifier
            .background(orange400)
            .padding(8.dp)
    ) {

        // only runs first time SideEffectSample is called
        SideEffect {
            Toast.makeText(
                context,
                "SideEffectSample() OUTER",
                Toast.LENGTH_SHORT
            ).show()
        }

        Button(onClick = { counterOuter++ }, modifier = Modifier.fillMaxWidth()) {
            SideEffect {
                Toast.makeText(
                    context,
                    "SideEffectSample() Button OUTER",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            Text(text = "Outer Composable: $counterOuter")
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(blue400)
                .padding(8.dp)
        ) {

            SideEffect {
                Toast.makeText(
                    context,
                    "SideEffectSample() INNER",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            Button(onClick = { counterInner++ }, modifier = Modifier.fillMaxWidth()) {

                SideEffect {
                    Toast.makeText(
                        context,
                        "SideEffectSample() Button INNER",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Text(text = "Inner Composable: $counterInner")
            }

        }
    }
}

@Composable
private fun ProduceStateSampleButton() {

    var loadImage by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display a load image button when image is not loading
        OutlinedButton(
            onClick = {
                loadImage = !loadImage
            }
        ) {
            Text(text = "Click to load image with produceState $loadImage")
        }

        if (loadImage) {
            ProduceStateSample()
        }
    }
}

@Composable
private fun ProduceStateSample() {
    val context = LocalContext.current

    val url = "www.example.com"
    val imageRepository = remember { ImageRepository() }

    val imageState = loadNetworkImage(url = url, imageRepository)

    when (imageState.value) {
        is Result.Loading -> {
            println("🔥 ProduceStateSample() Result.Loading")
            Toast.makeText(context, "🔥 ProduceStateSample() Result.Loading", Toast.LENGTH_SHORT)
                .show()
            CircularProgressIndicator()
        }

        is Result.Error -> {
            println("❌ ProduceStateSample() Result.Error")
            Toast.makeText(context, "❌ ProduceStateSample() Result.Error", Toast.LENGTH_SHORT)
                .show()

            Image(imageVector = Icons.Default.Error, contentDescription = null)
        }

        is Result.Success -> {
            println("✅ ProduceStateSample() Result.Success")
            Toast.makeText(context, "✅ ProduceStateSample() Result.Success", Toast.LENGTH_SHORT)
                .show()

            val image = (imageState!!.value as Result.Success).image

            Image(
                painterResource(id = image.imageIdRes),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun loadNetworkImage(
    url: String,
    imageRepository: ImageRepository
): State<Result> {

    // Creates a State<T> with Result.Loading as initial value
    // If either `url` or `imageRepository` changes, the running producer
    // will cancel and will be re-launched with the new inputs.
    return produceState<Result>(initialValue = Result.Loading, url, imageRepository) {

        // In a coroutine, can make suspend calls
        val image = imageRepository.load(url)

        // Update State with either an Error or Success result.
        // This will trigger a recomposition where this State is read
        value = if (image == null) {
            Result.Error
        } else {
            Result.Success(image)
        }
    }
}

sealed class Result {
    object Loading : Result()
    object Error : Result()
    class Success(val image: ImageRes) : Result()
}

class ImageRes(val imageIdRes: Int)

class ImageRepository() {

    /**
     * Returns a drawable resource or null to simulate Result with Success or Error states
     */
    suspend fun load(url: String): ImageRes? {
        delay(2000)

        // Random is added to return null if get a random number that is zero.
        // Possibility of getting null is 1/4
        return if (Random.nextInt(until = 4) > 0) {

            val images = listOf(
                R.drawable.avatar_1_raster,
                R.drawable.avatar_2_raster,
                R.drawable.avatar_3_raster,
                R.drawable.avatar_4_raster,
                R.drawable.avatar_5_raster,
                R.drawable.avatar_6_raster,
            )

            // Load a random id each time load function is called
            ImageRes(images[Random.nextInt(images.size)])
        } else {
            null
        }
    }
}

@Composable
private fun DerivedStateOfSample() {

    var numberOfItems by remember {
        mutableStateOf(0)
    }

    // Use derivedStateOf when a certain state is calculated or derived from other state objects.
    // Using this function guarantees that the calculation will only occur whenever one
    // of the states used in the calculation changes.
    val derivedStateMax by remember {
        derivedStateOf {
            numberOfItems > 5
        }
    }
    val derivedStateMin by remember {
        derivedStateOf {
            numberOfItems > 0
        }
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Amount to buy: $numberOfItems", modifier = Modifier.weight(1f))
            IconButton(onClick = { numberOfItems++ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { if (derivedStateMin) numberOfItems-- }) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "remove")
            }
        }

        println("🤔 COMPOSING..." +
                "numberOfItems: $numberOfItems, " +
                "derivedStateMax: $derivedStateMax, " +
                "derivedStateMin: $derivedStateMin"
        )
        if (derivedStateMax) {
            Text("You cannot buy more than 5 items", color = Color(0xffE53935))
        }
    }
}

@Composable
private fun DerivedStateOfSample2() {

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val firstItemVisible by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex != 0 }
    }

    Box {
        LazyRow(
            state = scrollState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(places) { place: Place ->
                    PlaceCard(place = place)
                }
            }
        )

        if (firstItemVisible) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd),
                backgroundColor = Color(0x99000000)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}