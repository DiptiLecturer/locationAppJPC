package org.freedu.locatiosharingappjpc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.freedu.locatiosharingappjpc.repository.UserRepository
import org.freedu.locatiosharingappjpc.ui.presentation.FriendListScreen
import org.freedu.locatiosharingappjpc.ui.presentation.LoginScreen
import org.freedu.locatiosharingappjpc.ui.presentation.MapScreen
import org.freedu.locatiosharingappjpc.ui.presentation.ProfileScreen
import org.freedu.locatiosharingappjpc.ui.presentation.SignUpScreen
import org.freedu.locatiosharingappjpc.ui.presentation.SplashScreen
import org.freedu.locatiosharingappjpc.ui.viewModel.AuthState
import org.freedu.locatiosharingappjpc.ui.viewModel.AuthViewModel
import org.freedu.locatiosharingappjpc.ui.viewModel.FriendListViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object FriendList : Screen("friendList")
    object Profile : Screen("profile")

    object MapAll : Screen("map_all")
    object MapSingle : Screen("map_single/{lat}/{lng}") {
        fun createRoute(lat: Double, lng: Double) = "map_single/$lat/$lng"
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Initialize Location Client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    if (authState is AuthState.Authenticated) {
                        navController.navigate(Screen.FriendList.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.FriendList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.FriendList.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // --- ONLY ONE FRIENDLIST COMPOSABLE ---
        composable(Screen.FriendList.route) {
            val viewModel: FriendListViewModel = viewModel(
                factory = FriendListViewModelFactory(UserRepository(), fusedLocationClient)
            )

            FriendListScreen(
                viewModel = viewModel,
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.FriendList.route) { inclusive = true }
                    }
                }
            )
        }

        // --- MAP ROUTES (Sharing the same logic) ---
        composable(Screen.MapSingle.route) { backStackEntry ->
            // Use getString because arguments in routes are strings by default
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            val viewModel: FriendListViewModel = viewModel(
                factory = FriendListViewModelFactory(UserRepository(), fusedLocationClient)
            )
            MapScreen(lat = lat, lng = lng, showAll = false, viewModel = viewModel)
        }

        composable(Screen.MapAll.route) {
            val viewModel: FriendListViewModel = viewModel(
                factory = FriendListViewModelFactory(UserRepository(), fusedLocationClient)
            )
            MapScreen(showAll = true, viewModel = viewModel)
        }
        // Inside NavHost
        composable(Screen.Profile.route) {
            // Reuse the same ViewModel instance or a new one
            val viewModel: FriendListViewModel = viewModel(
                factory = FriendListViewModelFactory(UserRepository(), fusedLocationClient)
            )

            ProfileScreen(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(Screen.FriendList.route) {
                        popUpTo(Screen.FriendList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

class FriendListViewModelFactory(
    private val repository: UserRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendListViewModel(repository, fusedLocationClient) as T
    }
}
