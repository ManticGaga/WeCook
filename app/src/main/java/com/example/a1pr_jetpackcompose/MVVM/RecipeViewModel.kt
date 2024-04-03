package com.example.a1pr_jetpackcompose.MVVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// RecipeViewModel.kt
class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _favoriteRecipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeEntity>> = _favoriteRecipes
    init {
        viewModelScope.launch {
            repository.getAllFavoriteRecipes().collect {
                _favoriteRecipes.value = it
            }
        }
    }

    fun insertFavoriteRecipe(recipe: Recipe) {
        viewModelScope.launch {
        val recipeEntity = RecipeEntity(
            recipeId = recipe.id,
            name = recipe.name,
            difficulty = recipe.difficulty,
            tags = recipe.tags.joinToString(",") // Assuming tags are a list of strings
        );repository.insertFavoriteRecipe(recipeEntity)
        }
    }

    fun deleteFavoriteRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteRecipe(recipeId)
        }
    }


}