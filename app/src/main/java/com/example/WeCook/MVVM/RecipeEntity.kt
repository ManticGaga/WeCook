package com.example.WeCook.MVVM

import androidx.room.Entity
import androidx.room.PrimaryKey

// RecipeEntity.kt
@Entity(tableName = "favorite_recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val difficulty: Int,
    val tags: String,
    var isFavorite: Boolean = false,
    val image: String
)