package com.example.WeCook.Data.MVVM

data class Recipe(
    var id: String = "",
    var author: String = "",
    var name: String = "",
    var difficulty: Int = 1, // Default to 1 if invalid
    var tags: List<String> = emptyList(),
    var rating: Float = 0f,
    var image: String = "",
    var stepstotal: Int = 0,
    var receiptdetails_image: List<String> = emptyList(),
    var receiptdetails_text: List<String> = emptyList(),
    var receiptdetails_info: List<Int> = emptyList()
) {
    init {
        // This will only be triggered if the value is invalid
        difficulty = difficulty.coerceAtLeast(1).coerceAtMost(5) // Clamp the value to 1-5
        require(rating in 0f..5f) { "Rating must be between 0 and 5" }
        // Add more validation rules as needed
    }
}