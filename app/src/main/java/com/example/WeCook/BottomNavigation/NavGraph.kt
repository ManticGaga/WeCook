package com.example.WeCook.BottomNavigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.WeCook.AddRecipeScreen
import com.example.WeCook.FavouritesScreen
import com.example.WeCook.RecipeDetails
import com.example.WeCook.RecipeList

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "RecipeList") {
        composable("RecipeList") {
            RecipeList(navHostController)
        }
        composable("Add") {
            AddRecipeScreen()
        }
        composable("Favourites") {
            FavouritesScreen(navHostController)
        }
        composable(
            "RecipeDetails/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: 0
            RecipeDetails(viewModel = viewModel(), recipeId = recipeId.toString())
        }
    }
}