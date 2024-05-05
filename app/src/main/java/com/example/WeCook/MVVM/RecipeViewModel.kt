package com.example.WeCook.MVVM

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WeCook.Retrofit.recipeList
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
    fun insertFavoriteRecipe(recipeId: Int) {
        viewModelScope.launch {
            val recipe = recipeList.find { it.id == recipeId } ?: return@launch
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
    fun getRecipeDetails(recipeId: Int): Recipe? {
        return recipeList.find { it.id == recipeId }
    }
        //    val favoriteRecipes: Flow<List<RecipeEntity>> = repository.getFavoriteRecipes()
    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val recipeEntity = repository.allTasks.first().find { it.recipeId == recipeId }
            if (recipeEntity != null) {
                if (recipeEntity.isFavorite) {
                    repository.deleteFavoriteRecipe(recipeId)
                } else {
                    insertFavoriteRecipe(recipeId)
                }
            }
            else {
                insertFavoriteRecipe(recipeId)
            }
        }
    }
}