package com.example.WeCook.BottomNavigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.WeCook.Data.Constructor.AddRecipeScreen
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Presenter.FavoritesScreen
import com.example.WeCook.Presenter.ProfileScreen
import com.example.WeCook.Presenter.RecipeDetails
import com.example.WeCook.Presenter.RecipeList

@Composable
fun NavGraph(navHostController: NavHostController, googleAuthUiClient: GoogleAuthUiClient, onSignOut: () -> Unit) {
    NavHost(navController = navHostController, startDestination = "RecipeList") {
        composable("RecipeList") {
            RecipeList(navHostController)
        }
        composable("Add") {
            AddRecipeScreen(navHostController, googleAuthUiClient)
        }
        composable("Favourites") {
            FavoritesScreen(navHostController)
        }
        composable(
            route = "RecipeDetails/{recipeId}", // Correct route pattern
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: "" // Get recipeId
            RecipeDetails(viewModel = viewModel(), recipeId = recipeId) // Pass recipeId
        }
        composable("Profile") {
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = onSignOut,
                onClose = { navHostController.popBackStack() } // Close the profile screen
            )
        }
    }
}