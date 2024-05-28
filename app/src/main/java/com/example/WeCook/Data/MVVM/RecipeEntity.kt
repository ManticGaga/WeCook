package com.example.WeCook.Data.MVVM

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class RecipeEntity(
    @PrimaryKey val id: String = "0",
    var isFavorite: Boolean = false
)
