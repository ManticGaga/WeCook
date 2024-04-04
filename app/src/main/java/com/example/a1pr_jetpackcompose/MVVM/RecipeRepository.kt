package com.example.a1pr_jetpackcompose.MVVM

import kotlinx.coroutines.flow.Flow

// RecipeRepository.kt
class RecipeRepository(private val recipeDao: RecipeDao) {
    val allTasks: Flow<List<RecipeEntity>> = recipeDao.getAllFavorite()

    suspend fun insertFavoriteRecipe(recipeEntity: RecipeEntity) {
        recipeEntity.isFavorite = true // Set isFavorite to true
        recipeDao.insertFavoriteRecipe(recipeEntity)
    }
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    suspend fun deleteFavoriteRecipe(recipeId: Int) {
        recipeDao.deleteFavoriteRecipe(recipeId)
    }
}