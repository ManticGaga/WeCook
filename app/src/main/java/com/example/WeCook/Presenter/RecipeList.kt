package com.example.WeCook.Presenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.WeCook.Data.MVVM.Recipe
import com.example.WeCook.Data.MVVM.RecipeViewModel
import com.example.WeCook.R
import com.example.WeCook.ui.theme._WeCookTheme


class RecipeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _WeCookTheme {
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
fun RecipeList(navController: NavController, viewModel: RecipeViewModel = viewModel()) {
    val firestoreRecipes by viewModel.firestoreRecipes.collectAsState()

    LazyColumn {
        items(firestoreRecipes) { recipe ->
            RecipeCard(recipe, navController, viewModel)
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, navController: NavController, viewModel: RecipeViewModel) {
    val recipeEntity = viewModel.allTasks.collectAsState(emptyList()).value.find { it.id == recipe.id }
    val isFavorite = recipeEntity?.isFavorite ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("RecipeDetails/${recipe.id}")
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
                RecipeImage(recipe.image)
                IconButton(
                    onClick = { viewModel.toggleFavorite(recipe.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp) // Add padding for spacing
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = recipe.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Рейтинг:",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${recipe.rating_total} (${recipe.rating_count})",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Автор:",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = recipe.author,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Тэги:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                recipe.tags.forEach { tag ->
                    TagItem(tag) // Use the TagItem composable for each tag
                }


            }
        }
    }
}
@Composable
fun RecipeImage(imageName: String) {
    val context = LocalContext.current
    val imageResource = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    Image(
        painter = painterResource(id = imageResource),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TagItem(tag: String) {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp) // Rounded corners
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp) // Rounded corners
            )
            .padding(8.dp) // Padding inside the tag
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )
    }
}