package com.example.quotevault.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quotevault.ui.screens.auth.ForgotPasswordScreen
import com.example.quotevault.ui.screens.auth.LoginScreen
import com.example.quotevault.ui.screens.auth.ResetPasswordScreen
import com.example.quotevault.ui.screens.auth.SignUpScreen
import com.example.quotevault.ui.screens.browse.BrowseScreen
import com.example.quotevault.ui.screens.collections.CollectionDetailScreen
import com.example.quotevault.ui.screens.collections.CollectionsScreen
import com.example.quotevault.ui.screens.favorites.FavoritesScreen
import com.example.quotevault.ui.screens.home.HomeScreen
import com.example.quotevault.ui.screens.profile.ProfileScreen
import com.example.quotevault.ui.screens.settings.SettingsScreen


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password")

    object Home : Screen("home")
    object Browse : Screen("browse")
    object Favorites : Screen("favorites")
    object Collections : Screen("collections")
    object CollectionDetail : Screen("collection_detail/{collectionId}") {
        fun createRoute(collectionId: String) = "collection_detail/$collectionId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

@Composable
fun QuoteVaultNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onPasswordResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToBrowse = { navController.navigate(Screen.Browse.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToCollections = { navController.navigate(Screen.Collections.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Browse.route) {
            BrowseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Collections.route) {
            CollectionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCollectionDetail = { collectionId ->
                    navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
                }
            )
        }

        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            CollectionDetailScreen(
                collectionId = collectionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}