package com.example.a1pr_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a1pr_jetpackcompose.MVVM.Recipe
import com.example.a1pr_jetpackcompose.MVVM.RecipeViewModel
import com.example.a1pr_jetpackcompose.ui.theme._1pr_jetpackcomposeTheme

class RecipeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _1pr_jetpackcomposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeList()
                }
            }
        }
    }
}

@Composable
fun RecipeList(navController: NavController) {
    val recipeList = listOf(
        Recipe(
            id = 1,
            name = "Салат Цезарь",
            difficulty = 2,
            tags = listOf("Салаты", "Здоровье", "Легкие"),
            rating = 4.0f,
            image = painterResource(id = R.drawable.grecheskiy_salat) // Уникальное изображение для каждого рецепта)
        ),
        Recipe(
            id = 2,
            name = "Паста с грибами",
            difficulty = 4,
            tags = listOf("Паста", "Грибы", "Вегетарианское"),
            rating = 5.0f,
            image = painterResource(id = R.drawable.pasta_s_gribami) // Уникальное изображение для каждого рецепта
        ),
        Recipe(
            id = 3,
            name = "Яичница",
            difficulty = 1,
            tags = listOf("Завтрак", "Легкие"),
            rating = 5.0f,
            image = painterResource(id = R.drawable.yaichinca) // Уникальное изображение для каждого рецепта
        ),
    )
    LazyColumn {
        items(recipeList) { recipe ->
            RecipeCard(recipe, navController)
        }
    }
}



@Composable
fun RecipeCard(recipe: Recipe, navController: NavController) {

//    val isFavorite = viewModel.favoriteRecipes.collectAsState(emptyList()).value.any { it.recipeId == recipe.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("RecipeDetails?id=${recipe.id}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Image(
                    painter = recipe.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(), // Fill the entire Box
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = recipe.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Сложность приготовления",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )

                for (i in 1..5) {
                    val filled = i <= recipe.difficulty
                    val icon = if (filled) {
                        painterResource(id = R.drawable.ogon)
                    } else {
                        painterResource(id = R.drawable.ogoff)
                    }
                    Image(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                    )

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Тэги:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                recipe.tags.forEach { tag ->
                    Text(
                        text = " $tag",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

/*               IconButton(onClick = {
                   if (isFavorite) {
                       viewModel.deleteFavoriteRecipe(recipe.id)
                   } else {
                       viewModel.insertFavoriteRecipe(recipe) // Pass the Recipe object
                   }
               }) {
                   Icon(
                       imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                       contentDescription = "Favorite"
                   )
               }*/
            }
        }
    }

    fun GoToRecipe(
        id: Int,
        name: String,
        difficulty: String,
        tags: String,
        rating: Float,
        image: String
    ) {


    }

}
