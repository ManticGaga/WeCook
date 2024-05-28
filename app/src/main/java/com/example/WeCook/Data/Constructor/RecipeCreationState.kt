package com.example.WeCook.Data.Constructor

data class RecipeCreationState(
    var author: String = "",
    var name: String = "",
    var difficulty: Int = 1,
    var tags: List<String> = emptyList(),
    var imageUrl: String = "placeholder", // Using "imageUrl" for clarity
    var steps: MutableList<RecipeStep> = mutableListOf(RecipeStep(1, "", "", 0))
)

data class RecipeStep(
    val stepNumber: Int,
    var imageUrl: String = "",
    var text: String = "",
    var info: Int = 0 // Assuming this is a timer value in seconds
)

data class Recipe(
    var id: String = "", // Add ID
    var author: String = "",
    var name: String = "",
    var difficulty: Int = 1,
    var tags: List<String> = emptyList(),
    var rating: Float = 0f,
    var imageUrl: String = "",
    var steps: List<RecipeStep> = emptyList(),
)