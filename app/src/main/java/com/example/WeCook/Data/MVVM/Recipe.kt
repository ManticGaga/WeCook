package com.example.WeCook.Data.MVVM

data class Recipe(
    var id: String = "", // Let Firestore generate this
    var author: String = "",
    var name: String = "",
    var difficulty: Int = 1,
    var tags: List<String> = emptyList(),
    var rating_total: Float = 0f,
    var rating_count: Int = 0,
    var image: String = "",
    var stepstotal: Int = 0,
    var receiptdetails_image: List<String> = emptyList(),
    var receiptdetails_text: List<String> = emptyList(),
    var userRatings: Map<String, Int> = emptyMap() // Map of usernames to ratings
)