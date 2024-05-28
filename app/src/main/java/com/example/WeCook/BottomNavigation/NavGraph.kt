package com.example.WeCook.BottomNavigation

import AddRecipeScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Presenter.FavoritesScreen
import com.example.WeCook.Presenter.RecipeList
import com.example.WeCook.Presenter.RecipeDetails

@Composable
fun NavGraph(navHostController: NavHostController, googleAuthUiClient: GoogleAuthUiClient) {
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
    }
}