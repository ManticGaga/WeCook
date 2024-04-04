package com.example.a1pr_jetpackcompose.MVVM

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// RecipeViewModel.kt

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allTasks: kotlinx.coroutines.flow.Flow<List<RecipeEntity>>

    init {
        val taskDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun insertFavoriteRecipe(fav: RecipeEntity) = viewModelScope.launch {
        repository.insertFavoriteRecipe(fav)
    }

    fun deleteFavoriteRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteRecipe(recipeId)
        }
    }
    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val recipe = repository.allTasks.first().find { it.recipeId == recipeId }
            if (recipe != null) {
                repository.updateFavoriteStatus(recipeId, !recipe.isFavorite)
            } else {
                // Handle the case where the recipe is not found in the database
            }
        }
    }
}