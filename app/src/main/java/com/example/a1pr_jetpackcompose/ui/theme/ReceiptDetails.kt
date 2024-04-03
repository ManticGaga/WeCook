package com.example.a1pr_jetpackcompose.ui.theme
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry

@Composable
fun RecipeDetails(navBackStackEntry: NavBackStackEntry) {
    val recipeId = navBackStackEntry.arguments?.getString("id")?.toInt() ?: 0

    // Use the recipeId to fetch the recipe details and display them
    // ...
}