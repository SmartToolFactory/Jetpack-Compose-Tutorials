package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.collection.forEach
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Preview
@Composable
fun Tutorial8_1Screen() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = Main,
        enterTransition = {
            slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(700)
            )
        }
    ) {

        composable<Main> {
            MainScren {
                navController.navigate(
                    // 🔥Navigate with Search route
                    Search(
                        parameters = SearchParameters(
                            searchQuery = "query",
                            filters = listOf("Shopping", "Travel", "Home", "Garden")
                        )
                    )
                )
            }
        }

        // Now use this in your destination
        composable<Search>(
            // 🔥Add how SearchParameters should be serialized
            typeMap = mapOf(typeOf<SearchParameters>() to SearchParametersType)
        ) { backStackEntry ->

            // Get Search parameter via toRoute<Search>()
            val searchParameters = backStackEntry.toRoute<Search>().parameters

            SearchScreen(
                searchParameters = searchParameters,
                navController = navController
            )
        }
    }
}

@Composable
private fun MainScren(
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
            Text("Go to Search")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("RestrictedApi")
@Composable
private fun SearchScreen(
    searchParameters: SearchParameters,
    navController: NavController,
) {

    val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()
    val packageName = LocalContext.current.packageName

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val query = searchParameters.searchQuery
        val filters = searchParameters.filters

        Text("Search Screen", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White)
                .fillMaxWidth()
                .padding(16.dp),
            text = "Query: $query",
            fontSize = 20.sp
        )

        Text("Filters")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach {
                Text(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(8.dp),
                    text = it,
                    fontSize = 16.sp
                )
            }
        }


        val pagerState = rememberPagerState {
            2
        }
        HorizontalPager(state = pagerState) { page ->

            val headerText = if (page == 0) "Current Back stack(reversed)" else "Current hierarchy"
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = headerText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                val destinations = if (page == 0) {
                    currentBackStack.reversed().map { it.destination }
                } else {
                    navController.currentDestination?.hierarchy?.toList() ?: listOf()
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(items = destinations) { destination: NavDestination ->

                        if (destination is NavGraph) {
                            MainText(destination, packageName)
                            destination.nodes.forEach { _, value ->
                                SubItemText(value, packageName)
                            }

                        } else {
                            MainText(destination, packageName)
                        }
                    }
                }

            }
        }
    }
}

@Serializable
object Main

// The Search screen requires more complicated parameters
@Serializable
private data class Search(val parameters: SearchParameters)

@Serializable
@Parcelize
data class SearchParameters(
    val searchQuery: String,
    val filters: List<String>,
) : Parcelable


val SearchParametersType = object : NavType<SearchParameters>(
    isNullableAllowed = false
) {
    override fun put(bundle: Bundle, key: String, value: SearchParameters) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): SearchParameters? {
        println("get() key: $key")
        return bundle.getParcelable(key) as SearchParameters?
    }

    override fun serializeAsValue(value: SearchParameters): String {
        // Serialized values must always be Uri encoded
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): SearchParameters {
        println("parseValue(): $value")
        // Navigation takes care of decoding the string
        // before passing it to parseValue()
        return Json.decodeFromString<SearchParameters>(value)
    }
}

inline fun <reified T : Parcelable> getNavType(
    isNullableAllowed: Boolean = false,
) = object : NavType<T>(
    isNullableAllowed = isNullableAllowed
) {
    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): T? {
        println("get() key: $key")
        return bundle.getParcelable(key) as T?
    }

    override fun serializeAsValue(value: T): String {
        // Serialized values must always be Uri encoded
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): T {
        println("parseValue(): $value")
        // Navigation takes care of decoding the string
        // before passing it to parseValue()
        return Json.decodeFromString<T>(value)
    }
}

// FIXME Serializable NavType crashes. Fix this solution
//val SearchParametersType = object : NavType<SearchParameters>(
//    isNullableAllowed = false
//) {
//
//    override fun put(bundle: Bundle, key: String, value: SearchParameters) {
//        bundle.putSerializable(key, value::class.java)
//    }
//
//    override fun get(bundle: Bundle, key: String): SearchParameters? {
//        return bundle.getSerializable(key) as SearchParameters?
//    }
//
//    override fun serializeAsValue(value: SearchParameters): String {
//        return Uri.encode(
//            Json.encodeToString(
//                serializer = SearchParameters.serializer(),
//                value = value
//            )
//        )
//    }
//
//    override fun parseValue(value: String): SearchParameters {
//        return Json.decodeFromString<SearchParameters>(value)
//    }
//}