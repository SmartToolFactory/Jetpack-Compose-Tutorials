package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import javax.inject.Inject


@Composable
fun Tutorial5_1Screen() {
    MainContainer()
}

@Composable
private fun MainContainer() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = Splash

    ) {
        addNavGraph(navController)
    }
}


private fun NavGraphBuilder.addNavGraph(
    navController: NavHostController,
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
                // 🔥 hiltViewModel() creates ViewModel scoped this navBackStackEntry
                // NavBackStackEntry implements LifecycleOwner,
                // ViewModelStoreOwner, SavedStateRegistryOwner
                homeViewModel = hiltViewModel()
            )
        }

        composable<UserProfile> { navBackStackEntry: NavBackStackEntry ->
            ProfileScreen(
                // 🔥 hiltViewModel() creates ViewModel scoped this navBackStackEntry
                profileViewModel = hiltViewModel()
            )
        }
    }
}

@Composable
private fun SplashScreen(
    onNavigateHome: () -> Unit,
) {
    val updatedLambda = rememberUpdatedState(onNavigateHome)
    LaunchedEffect(Unit) {
        delay(1000)
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
private fun HomeScreen(
    homeViewModel: HomeViewModel,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Home Screen")
    }
}

@Composable
private fun ProfileScreen(
    profileViewModel: ProfileViewModel,
) {

    val profile: UserProfile? by profileViewModel.profileFlow.collectAsStateWithLifecycle()

    profile?.let {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text("name: ${profile?.name}, surname: ${profile?.surname}")
        }
    }

}

@HiltViewModel
class HomeViewModel @Inject constructor(
//    savedStateHandle: SavedStateHandle,
) : ViewModel()

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _profileFlow = MutableStateFlow<UserProfile?>(null)
    val profileFlow = _profileFlow.asStateFlow()

    init {
        println("This MyViewModel: $this")
        _profileFlow.value = savedStateHandle.toRoute<UserProfile>()
    }
}

@Serializable
data class UserProfile(val id: String, val name: String, val surname: String)
