package com.example.WeCook.Data.MVVM

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.WeCook.Data.Constructor.RecipeStep

@Entity(tableName = "favorite_recipes")
data class RecipeEntity(
    @PrimaryKey val id: String = "", // Use String ID here as well
    var isFavorite: Boolean = false,
)
@Entity(tableName = "recipe_drafts")
data class RecipeDraft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var author: String = "",
    var name: String = "",
    var difficulty: Int = 1,
    var tags: List<String> = emptyList(),
    var imageUrl: String = "placeholder",
    var steps: List<RecipeStep> = emptyList() // Store steps as a list
)