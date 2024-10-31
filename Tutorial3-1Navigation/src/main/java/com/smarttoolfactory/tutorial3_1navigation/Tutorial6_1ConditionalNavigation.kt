@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.random.Random

@Preview
@Composable
fun Tutorial6_1Screen() {

    // In this example RegisterViewModel is shared by
    // RegisterGraph and BottomNavigationRoute.DashboardRoute
    // with HomeGraph NavBackEntry
    // When user is not logged in RegisterScreen is shown by navigating from
    // BottomNavigationRoute.DashboardRoute

    // Also if user enters app via deeplink from terminal
    // or Notification startDestination is changed from Splash to ProfileGraph
    // to have conditional startDestination too.

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
                showNotification(
                    context = context,
                    uriString = "$uri/profile/deeplinkId"
                )
            }
        }

    val deeplink: Uri? = (LocalContext.current as? MainActivity)?.intent?.data
    val isDeeplink = deeplink != null

    println("Deeplink: $deeplink")

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = if (isDeeplink) ProfileGraph else Splash,
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

        composable<Splash> { navBackStackEntry: NavBackStackEntry ->
            SplashScreen {
                navController.navigate(HomeGraph) {
                    popUpTo<Splash> {
                        inclusive = true
                    }
                }
            }
        }

        navigation<HomeGraph>(
            startDestination = BottomNavigationRoute.DashboardRoute
        ) {

            navigation<RegisterGraph>(
                // 🔥🔥 Start destination should be class
                // or throws error for Serialized for Companion
                startDestination = SessionModel("", "")
            ) {
                composable<SessionModel> { navBackStackEntry: NavBackStackEntry ->
                    val parentBackStackEntry: NavBackStackEntry =
                        remember(navBackStackEntry) {
                            navController.getBackStackEntry(HomeGraph)
                        }

                    // 🔥This ViewModel is shared between HomeGraph and SessionModel
                    val registerViewModel = hiltViewModel<RegisterViewModel>(parentBackStackEntry)

                    RegisterScreen(
                        registerViewModel = registerViewModel
                    ) {
                        navController.navigate(BottomNavigationRoute.DashboardRoute) {
                            popUpTo<HomeGraph>()
                        }
                    }
                }
            }

            composable<BottomNavigationRoute.DashboardRoute> { navBackStackEntry: NavBackStackEntry ->

                val parentBackStackEntry: NavBackStackEntry =
                    remember(navBackStackEntry) {
                        navController.getBackStackEntry(HomeGraph)
                    }

                // 🔥This ViewModel is shared between HomeGraph and RegisterGraph
                val registerViewModel = hiltViewModel<RegisterViewModel>(parentBackStackEntry)

                LaunchedEffect(registerViewModel.loggedIn) {
                    if (registerViewModel.loggedIn.not()) {
                        navController.navigate(RegisterGraph) {
                            popUpTo<HomeGraph>()
                        }
                    }
                }

                // If registered open Home Screen
                if (registerViewModel.loggedIn) {
                    MainContainer(
                        onShowDeeplinkNotification = {
                            if (hasNotificationPermission) {
                                showNotification(
                                    context = context,
                                    uriString = "$uri/profile/deeplinkId"
                                )
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    ) { route: Any, navBackStackEntry: NavBackStackEntry ->
                        // Navigate only when life cycle is resumed for current screen
                        if (navBackStackEntry.lifecycleIsResumed()) {
                            navController.navigate(route = route)
                        }
                    }
                }
            }
        }

        navigation<ProfileGraph>(
            startDestination = Profile("")
        ) {
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

@SuppressLint("RestrictedApi")
@Composable
private fun MainContainer(
    onShowDeeplinkNotification: () -> Unit,
    onGoToProfileScreen: (
        route: Any,
        navBackStackEntry: NavBackStackEntry,
    ) -> Unit,
) {
    val items = remember {
        bottomNestedRouteDataList2()
    }

    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("TopAppbar")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {

            NavigationBar(
                modifier = Modifier.height(56.dp),
                tonalElevation = 4.dp
            ) {
                items.forEachIndexed { index, item: BottomRouteData ->

                    val selected =
                        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            nestedNavController.navigate(route = item.route) {
                                launchSingleTop = true

                                // 🔥 If restoreState = true and saveState = true are commented
                                // routes other than Home1 are not saved
                                restoreState = true

                                // Pop up backstack to the first destination and save state.
                                // This makes going back
                                // to the start destination when pressing back in any other bottom tab.
                                popUpTo(findStartDestination(nestedNavController.graph).id) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues: PaddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = nestedNavController,
            startDestination = BottomNavigationRoute.HomeGraph
        ) {
            addBottomNavigationGraph(
                nestedNavController = nestedNavController,
                onShowDeeplinkNotification = onShowDeeplinkNotification,
                onGoToProfileScreen = { route, navBackStackEntry ->
                    onGoToProfileScreen(route, navBackStackEntry)
                },
                onBottomScreenClick = { route, navBackStackEntry ->
                    nestedNavController.navigate(route)
                }
            )
        }
    }
}

/**
 * @param onGoToProfileScreen lambda for navigating Profile screen from current screen with top NavHostController
 * @param onBottomScreenClick lambda for navigating with [nestedNavController] in BottomNavigation
 */
private fun NavGraphBuilder.addBottomNavigationGraph(
    nestedNavController: NavHostController,
    onShowDeeplinkNotification: () -> Unit,
    onGoToProfileScreen: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
    onBottomScreenClick: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    navigation<BottomNavigationRoute.HomeGraph>(
        startDestination = BottomNavigationRoute.HomeRoute1
    ) {
        composable<BottomNavigationRoute.HomeRoute1> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen1",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.HomeRoute2, from)
                }
            )
        }

        composable<BottomNavigationRoute.HomeRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen2",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.HomeRoute3, from)
                }
            )
        }

        composable<BottomNavigationRoute.HomeRoute3> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen3",
                navController = nestedNavController
            )
        }
    }

    navigation<BottomNavigationRoute.SettingsGraph>(
        startDestination = BottomNavigationRoute.SettingsRoute1
    ) {
        composable<BottomNavigationRoute.SettingsRoute1> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.SettingsRoute2, from)
                }
            )
        }

        composable<BottomNavigationRoute.SettingsRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen2",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.SettingsRoute3, from)
                }
            )
        }

        composable<BottomNavigationRoute.SettingsRoute3> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen3",
                navController = nestedNavController
            )
        }
    }

    composable<BottomNavigationRoute.FavoritesRoute> { from: NavBackStackEntry ->
        UsersScreen(
            homeViewModel = hiltViewModel(),
            onShowDeeplinkNotification = onShowDeeplinkNotification,
            onProfileClick = { userProfile ->
                onGoToProfileScreen(
                    Profile("Name: ${userProfile.name}"),
                    from
                )
            }
        )
    }

    composable<BottomNavigationRoute.NotificationRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Notifications Screen",
            navController = nestedNavController,
            onClick = {
                onGoToProfileScreen(
                    Profile("Notifications"),
                    from
                )
            }
        )
    }
}

@Composable
private fun SplashScreen(
    onNavigateHome: () -> Unit,
) {
    val updatedLambda = rememberUpdatedState(onNavigateHome)

    LaunchedEffect(Unit) {
        delay(500)
        updatedLambda.value.invoke()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Splash Screen", fontSize = 34.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UsersScreen(
    homeViewModel: UsersVieModel,
    onProfileClick: (UserProfile) -> Unit,
    onShowDeeplinkNotification: () -> Unit,
) {

    val userList by homeViewModel.profileFlow.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {

        items(items = userList) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .clickable {
                        onProfileClick(it)
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
                        onShowDeeplinkNotification()
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

@HiltViewModel
class UsersVieModel @Inject constructor() : ViewModel() {

    private val _profileFlow = MutableStateFlow<List<UserProfile>>(listOf())
    val profileFlow = _profileFlow.asStateFlow()

    init {
        _profileFlow.value = List(Random.nextInt(15, 50)) {
            UserProfile(id = "$it", name = "User $it")
        }
    }
}

@Composable
private fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    goToHomeScreen: () -> Unit,
) {

    var email by remember {
        mutableStateOf("")
    }

    LaunchedEffect(registerViewModel.loggedIn) {
        if (registerViewModel.loggedIn) {
            goToHomeScreen()
        }
    }

    var password by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            label = {
                Text("Email")
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = {
                Text("Password")
            }
        )

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                registerViewModel.login(email, password)
            }
        ) {
            Text("Sign in")
        }
    }
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {

    var loggedIn: Boolean by mutableStateOf(false)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            sessionManager
                .login(email, password)
                .collect { user: SessionModel? ->
                    loggedIn = user != null
                }
        }
    }
}

class SessionManager @Inject constructor() {

    var loggedIn: Boolean = false

    fun login(email: String, password: String): Flow<SessionModel?> {
        return flow<SessionModel?> {
            delay(200)
            emit(SessionModel(email, password))
        }
            .map {
                loggedIn = it != null
                it
            }
    }
}

@Serializable
data class SessionModel(val email: String, val password: String)
