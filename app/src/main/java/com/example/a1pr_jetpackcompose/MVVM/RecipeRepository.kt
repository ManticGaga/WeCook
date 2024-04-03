package com.example.a1pr_jetpackcompose.MVVM

import kotlinx.coroutines.flow.Flow

// RecipeRepository.kt
class RecipeRepository(private val recipeDao: RecipeDao) {
    suspend fun insertFavoriteRecipe(recipeEntity: RecipeEntity) {
        recipeDao.insertFavoriteRecipe(recipeEntity)
    }

    fun getAllFavoriteRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.getAllFavoriteRecipes()
    }

    suspend fun deleteFavoriteRecipe(recipeId: Int) {
        recipeDao.deleteFavoriteRecipe(recipeId)
    }
}