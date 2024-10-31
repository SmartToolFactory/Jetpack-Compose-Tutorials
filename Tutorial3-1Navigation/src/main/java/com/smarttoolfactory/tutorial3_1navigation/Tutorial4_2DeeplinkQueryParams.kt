package com.smarttoolfactory.tutorial3_1navigation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute

/*

   Test with adb by calling snipped below in terminal
    adb shell am start -W -a android.intent.action.VIEW -d "test://www.example.com/profile/2"
    or test Product with uri param id and query params count and type
    adb shell am start -W -a android.intent.action.VIEW -d "test://www.example.com/product/type=2?count=3&type=someType"
 */

@Preview
@Composable
fun Tutorial4_2Screen() {
    MainContainer()
}

@Composable
private fun MainContainer() {
    val navController = rememberNavController()

    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionRequest =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result ->
            hasNotificationPermission = result
        }

    val intent: Intent? = (LocalContext.current as? MainActivity)?.intent
    val deeplink: Uri? = intent?.data

    println("Deeplink: $deeplink")

    deeplink?.let {
        val deeplinkRequest: NavDeepLinkRequest = NavDeepLinkRequest.Builder
            .fromUri(it)
            .build()

        println(
            "uri ${deeplinkRequest.uri}, " +
                    "action: ${deeplinkRequest.action}, " +
                    "mimeType: ${deeplinkRequest.mimeType}"
        )
    }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = HomeGraph
    ) {
        addNavGraph(navController, hasNotificationPermission, context, permissionRequest)
    }
}

private fun NavGraphBuilder.addNavGraph(
    navController: NavHostController,
    hasNotificationPermission: Boolean,
    context: Context,
    permissionRequest: ManagedActivityResultLauncher<String, Boolean>,
) {

    navigation<HomeGraph>(
        startDestination = Home
    ) {
        composable<Home> { navBackStackEntry: NavBackStackEntry ->
            HomeScreen(
                onProfileClick = { profile: Profile ->
                    navController.navigate(profile)
                },
                onShowProfileDeeplinkNotification = { profile: Profile ->
                    if (hasNotificationPermission) {
                        showNotification(context, "$uri/profile/${profile.id}")
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                onProductClick = { product: Product ->
                    navController.navigate(product)
                },
                onShowProductDeeplinkNotification = { product: Product ->
                    if (hasNotificationPermission) {
                        showNotification(
                            context = context,
                            uriString = "$uri/product/${product.id}?count=${product.count}&type=${product.type}"
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }

        composable<Profile>(
            deepLinks = listOf(
                navDeepLink<Profile>(basePath = "$uri/profile")
            )
        ) { navBackStackEntry: NavBackStackEntry ->
            val profile: Profile = navBackStackEntry.toRoute<Profile>()
            Screen(profile.toString(), navController)
        }

        composable<Product>(
            deepLinks = listOf(
                navDeepLink<Product>(basePath = "$uri/product")
            )
        ) { navBackStackEntry: NavBackStackEntry ->
            val product: Product = navBackStackEntry.toRoute<Product>()
            Screen(
                text = "id: ${product.id}, " +
                        "count: ${product.count}" +
                        "type: ${product.type}, ",
                navController = navController
            )
        }
    }
}

@Composable
private fun HomeScreen(
    onProfileClick: (Profile) -> Unit,
    onShowProfileDeeplinkNotification: (Profile) -> Unit,
    onProductClick: (Product) -> Unit,
    onShowProductDeeplinkNotification: (Product) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val profileList = remember {
            List(20) {
                Profile(it.toString())
            }
        }

        val productList = remember {
            List(20) {
                Product(
                    id = "$it", count = it, type = if (it % 2 == 0) "even" else "odd"
                )
            }
        }

        val pagerState = rememberPagerState {
            2
        }

        HorizontalPager(
            state = pagerState
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {

                if (page == 0) {
                    items(profileList) {
                        ListRow(
                            text = "Profile ${it.id}",
                            onClick = {
                                onProfileClick(it)
                            },
                            onImageCLick = {
                                onShowProfileDeeplinkNotification(it)
                            }
                        )

                    }
                } else {
                    items(productList) {
                        ListRow(
                            text = "Product ${it.id}",
                            onClick = {
                                onProductClick(it)
                            },
                            onImageCLick = {
                                onShowProductDeeplinkNotification(it)
                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
private fun ListRow(
    text: String,
    onClick: () -> Unit,
    onImageCLick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.White)
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                onImageCLick()
            }
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.tertiary,
                imageVector = Icons.Default.Notifications,
                contentDescription = null
            )
        }
    }
}