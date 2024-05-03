package com.example.a1pr_jetpackcompose.BottomNavigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.a1pr_jetpackcompose.AddRecipeScreen
import com.example.a1pr_jetpackcompose.RecipeList
import com.example.a1pr_jetpackcompose.FavouritesScreen
import com.example.a1pr_jetpackcompose.RecipeDetails


@Composable
fun NavGraph( navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "Add") {
        composable("RecipeList") {
            RecipeList(navHostController) // Pass viewModel to RecipeList
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
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            RecipeDetails(viewModel = viewModel(), recipeId = recipeId)
        }
    }
}
