package com.example.a1pr_jetpackcompose.BottomNavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.a1pr_jetpackcompose.AddRecipeScreen
import com.example.a1pr_jetpackcompose.RecipeList
import com.example.a1pr_jetpackcompose.FavouritesScreen
import com.example.a1pr_jetpackcompose.ui.theme.RecipeDetails


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
            FavouritesScreen()
        }
        composable("RecipeDetails?id={id}") { backStackEntry -> // Note the ?id={id} part
            RecipeDetails(backStackEntry)
        }
    }
}
