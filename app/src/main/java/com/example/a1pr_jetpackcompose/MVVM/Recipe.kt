package com.example.a1pr_jetpackcompose.MVVM

// Recipe.kt
data class Recipe(
    val id: Int = 0,
    val author: String,
    val name: String,
    val difficulty: Int,
    val tags: List<String>,
    val rating: Float,
    val image: String,
    val stepstotal: Int,
    val receiptdetails_image: List<String>,
    val receiptdetails_text: List<String>,
    val receiptdetails_info: List<Int>
)