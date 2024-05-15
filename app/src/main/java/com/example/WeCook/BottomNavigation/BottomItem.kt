package com.example.WeCook.BottomNavigation

import com.example.WeCook.R


sealed class BottomItem(val title: String, val iconId: Int, val route: String) {
    object RecipeList : BottomItem("Recipes", R.drawable.baseline_manage_accounts_24, "RecipeList")
    object Add : BottomItem("Add", R.drawable.baseline_add_circle_24, "Add")
    object Favourites : BottomItem("Favorites", R.drawable.baseline_dining_24, "Favourites")
}