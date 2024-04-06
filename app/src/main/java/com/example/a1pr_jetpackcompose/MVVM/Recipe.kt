package com.example.a1pr_jetpackcompose.MVVM

import android.os.Parcelable
import androidx.compose.ui.graphics.painter.Painter

// Recipe.kt
data class Recipe(
    val id: Int = 0,
    val name: String,
    val difficulty: Int,
    val tags: List<String>,
    val rating: Float,
    val image: String
)