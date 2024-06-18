package com.example.WeCook.Data.MVVM

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(private val recipeDao: RecipeDao) {
    val allTasks: Flow<List<RecipeEntity>> = recipeDao.getAllFavorite()

    suspend fun insertFavoriteRecipe(recipeEntity: RecipeEntity) {
        recipeEntity.isFavorite = true // Set isFavorite to true
        Log.d("RecipeRepository", "Inserting recipe: $recipeEntity")
        recipeDao.insertFavoriteRecipe(recipeEntity)
    }

    suspend fun deleteFavoriteRecipe(recipeId: String) {
        Log.d("RecipeRepository", "Deleting recipe with ID: $recipeId")
        recipeDao.deleteFavoriteRecipe(recipeId)
    }
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.getAllFavorite().map { recipes ->
            recipes.filter { it.isFavorite }
        }
    }
}