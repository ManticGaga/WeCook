package com.example.a1pr_jetpackcompose.MVVM

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// RecipeDao.kt
@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(recipeEntity: RecipeEntity)
    @Query("UPDATE favorite_recipes SET isFavorite = :isFavorite WHERE recipeId = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean)
    @Query("SELECT * FROM favorite_recipes")
    fun getAllFavorite(): Flow<List<RecipeEntity>>

    @Query("DELETE FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun deleteFavoriteRecipe(recipeId: Int)
}