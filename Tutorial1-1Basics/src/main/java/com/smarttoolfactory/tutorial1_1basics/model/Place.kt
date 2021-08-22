package com.smarttoolfactory.tutorial1_1basics.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.smarttoolfactory.tutorial1_1basics.R
import kotlin.random.Random

@Immutable
data class Place(
    val id: Long,
    val description: String,
    @DrawableRes val imgRes: Int,
    val rating: Double = Random.nextDouble(0.0, 10.0),
    val price: Int = Random.nextInt(500)
)

val places = listOf(
    Place(1, "Place1", R.drawable.landscape1),
    Place(2, "Place2", R.drawable.landscape2),
    Place(3, "Place3", R.drawable.landscape3),
    Place(4, "Place4", R.drawable.landscape4),
    Place(5, "Place5", R.drawable.landscape5),
    Place(6, "Place6", R.drawable.landscape6),
    Place(7, "Place7", R.drawable.landscape7),
    Place(8, "Place8", R.drawable.landscape8),
    Place(9, "Place9", R.drawable.landscape9),
    Place(10, "Place10", R.drawable.landscape10)
)