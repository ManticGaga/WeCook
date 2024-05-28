package com.example.WeCook.Data.MVVM

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class RecipeEntity(
    @PrimaryKey val id: String = "", // Use String ID here as well
    var isFavorite: Boolean = false,
    var personalRating: Int = -1 // -1 can indicate "not rated"
)