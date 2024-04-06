package com.example.a1pr_jetpackcompose.MVVM

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allTasks: kotlinx.coroutines.flow.Flow<List<RecipeEntity>>

    init {
        val taskDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(taskDao)
        allTasks = repository.allTasks
    }
    fun insertFavoriteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val recipeEntity = RecipeEntity(
                recipeId = recipe.id,
                name = recipe.name,
                difficulty = recipe.difficulty,
                tags = recipe.tags.joinToString(","),
                image = recipe.image
            )
            repository.insertFavoriteRecipe(recipeEntity)
        }
    }
    fun deleteFavoriteRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteRecipe(recipeId)
        }
    }
    val favoriteRecipes: Flow<List<RecipeEntity>> = repository.getFavoriteRecipes()
    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val recipeEntity = repository.allTasks.first().find { it.recipeId == recipeId }
            if (recipeEntity != null) {
                val newIsFavorite = !recipeEntity.isFavorite
                repository.updateFavoriteStatus(recipeId, newIsFavorite)
            } else {
            }
        }
    }
}