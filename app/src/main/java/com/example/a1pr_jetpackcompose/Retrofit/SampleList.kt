package com.example.a1pr_jetpackcompose.Retrofit

import com.example.a1pr_jetpackcompose.MVVM.Recipe

val recipeList = listOf(
    Recipe(
        id = 1,
        name = "Салат Цезарь",
        difficulty = 2,
        tags = listOf("Салаты", "Здоровье", "Легкие"),
        rating = 4.0f,
        image = "grecheskiy_salat" // Уникальное изображение для каждого рецепта)
    ),
    Recipe(
        id = 2,
        name = "Паста с грибами",
        difficulty = 4,
        tags = listOf("Паста", "Грибы", "Вегетарианское"),
        rating = 5.0f,
        image = "pasta_s_gribami" // Уникальное изображение для каждого рецепта
    ),
    Recipe(
        id = 3,
        name = "Яичница",
        difficulty = 1,
        tags = listOf("Завтрак", "Легкие"),
        rating = 5.0f,
        image = "yaichinca" // Уникальное изображение для каждого рецепта
    ),
)