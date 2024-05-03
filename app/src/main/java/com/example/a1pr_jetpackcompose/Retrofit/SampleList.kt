package com.example.a1pr_jetpackcompose.Retrofit

import com.example.a1pr_jetpackcompose.MVVM.Recipe

val recipeList = listOf(
    Recipe(
        id = 1,
        author = "Максим",
        name = "Салат Цезарь",
        difficulty = 2,
        tags = listOf("Салаты", "Здоровье", "Легкие"),
        rating = 4.0f,
        image = "grecheskiy_salat",
        stepstotal = 4,
        receiptdetails_image = listOf("grecheskiy_salat","Пусто","yaichinca","Пусто"),
        receiptdetails_text = listOf("grecheskiy_salat","Пусто","yaichinca", "ЕЕЕЕЕЕЕ Макарена"),
        receiptdetails_info = listOf (0,90,60,0)
    ),
    Recipe(
        id = 2,
        author = "Максим",
        name = "Паста с грибами",
        difficulty = 4,
        tags = listOf("Паста", "Грибы", "Вегетарианское"),
        rating = 5.0f,
        image = "pasta_s_gribami",
        stepstotal = 4,
        receiptdetails_image = listOf("grecheskiy_salat","Пусто","yaichinca","Пусто"),
        receiptdetails_text = listOf("grecheskiy_salat","Пусто","yaichinca", "ЕЕЕЕЕЕЕ Макарена"),
        receiptdetails_info = listOf (0,90,60,0)
    ),
    Recipe(
        id = 3,
        author = "Максим",
        name = "Яичница",
        difficulty = 1,
        tags = listOf("Завтрак", "Легкие"),
        rating = 5.0f,
        image = "yaichinca",
        stepstotal = 4,
        receiptdetails_image = listOf("grecheskiy_salat","Пусто","yaichinca","Пусто"),
        receiptdetails_text = listOf("grecheskiy_salat","Пусто","yaichinca", "ЕЕЕЕЕЕЕ Макарена"),
        receiptdetails_info = listOf (0,90,60,0)
    ),
)