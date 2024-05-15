package com.example.WeCook

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.WeCook.MVVM.RecipeEntity
import com.example.WeCook.MVVM.RecipeViewModel
import com.example.WeCook.ui.theme.NormalTextComponent
import com.example.WeCook.R

@Composable
fun FavouritesScreen(navController: NavController, viewModel: RecipeViewModel = viewModel()) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(top = 10.dp).padding(bottom = 15.dp)
    ) {

        NormalTextComponent(value = stringResource(id = R.string.hell5))

    }

    val favoriteList by viewModel.allTasks.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp) // Установите отступ, чтобы избежать перекрытия
    ) {
        items(favoriteList) { Recipe ->
            RecipeCard_Fav(Recipe, navController, viewModel)
        }
    }
}

@Composable
fun RecipeCard_Fav(recipe: RecipeEntity, navController: NavController, viewModel: RecipeViewModel) {

    val recipeEntity = viewModel.allTasks.collectAsState(emptyList()).value.find { it.recipeId == recipe.id }
    val isFavorite = recipeEntity?.isFavorite ?: false
    val context = LocalContext.current

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
            )
            {
                RecipeImage_fav(recipe.image, context)
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

                IconButton(onClick = { viewModel.deleteFavoriteRecipe(recipe.id) }) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}
@Composable
fun RecipeImage_fav(imageName: String, context: Context) {
    val context = LocalContext.current
    val imageResource = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    Image(
        painter = painterResource(id = imageResource),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
