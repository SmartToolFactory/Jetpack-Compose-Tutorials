package com.smarttoolfactory.tutorial3_1navigation

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute

/*
    Steps to add deeplink to Profile Screen
    1- Add deeplink scheme and host and intent filter to manifest
    2- Add url to composable as
    composable<Profile>(
        deepLinks = listOf(
            navDeepLink<Profile>(basePath = "$uri/profile")
        )
    3- Test with adb by calling snipped below in terminal
    adb shell am start -W -a android.intent.action.VIEW -d "test://www.example.com/profile/2"
 */

@Preview
@Composable
fun Tutorial4_1Screen() {
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

            if (hasNotificationPermission) {
                showNotification(context)
            }
        }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> { navBackStackEntry: NavBackStackEntry ->
            SplashScreen {
                navController.navigate(Home) {
                    popUpTo<Splash> {
                        inclusive = true
                    }
                }
            }
        }

        navigation<HomeGraph>(
            startDestination = Home
        ) {
            composable<Home> { navBackStackEntry: NavBackStackEntry ->
                HomeScreen(
                    onClick = { profile: Profile ->
                        navController.navigate(profile)
                    }
                ) { _: Profile ->
                    if (hasNotificationPermission) {
                        showNotification(context)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            composable<Profile>(
                deepLinks = listOf(
                    navDeepLink<Profile>(basePath = "$uri/profile")
                )
            ) { navBackStackEntry: NavBackStackEntry ->
                val profile: Profile = navBackStackEntry.toRoute<Profile>()
                Screen(profile.toString(), navController)
            }
        }
    }
}

@Composable
fun SplashScreen(
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Splash Screen", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) {
            Text(text = "Go to Home")
        }
    }
}

private fun showNotification(context: Context) {
    val id = "exampleId"

    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        "$uri/profile/$id".toUri(),
        context,
        MainActivity::class.java
    )

    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder
        .create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


    val notification: Notification = NotificationCompat.Builder(
        context,
        "channelId"
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Go to app")
        .setContentText("Click to open Profile")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setAutoCancel(true)
        .setContentIntent(deepLinkPendingIntent)
        .build()

    val notificationManager = context.getSystemService() as NotificationManager?

    notificationManager?.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createNotificationChannel(
                NotificationChannel("channelId", "name", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        this.notify(1, notification)
    }

}

@Composable
private fun HomeScreen(
    onClick: (Profile) -> Unit,
    onOpenDeeplink: (Profile) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val list = remember {
            List(20) {
                Profile(it.toString())
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(list) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .clickable {
                            onClick(it)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile ${it.id}",
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            onOpenDeeplink(it)
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
        }
    }
}
