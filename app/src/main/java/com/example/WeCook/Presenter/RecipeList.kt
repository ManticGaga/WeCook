package com.example.WeCook.Presenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.WeCook.Data.MVVM.Recipe
import com.example.WeCook.Data.MVVM.RecipeViewModel
import com.example.WeCook.R
import com.example.WeCook.ui.theme._WeCookTheme
import com.google.firebase.auth.FirebaseAuth


class RecipeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _WeCookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    val navController = rememberNavController()
                    val viewModel: RecipeViewModel = viewModel()
                    RecipeList(navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun RecipeList(navController: NavController, viewModel: RecipeViewModel = viewModel()) {
    val firestoreRecipes by viewModel.firestoreRecipes.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    LazyColumn {
        items(firestoreRecipes) { recipe ->
            RecipeCard(recipe, navController, viewModel, currentUser?.displayName ?: "")
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, navController: NavController, viewModel: RecipeViewModel, currentUserId: String) {
    val recipeEntity = viewModel.allTasks.collectAsState(emptyList()).value.find { it.id == recipe.id }
    val isFavorite = recipeEntity?.isFavorite ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
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
                if (recipe.image == "placeholder") {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = R.drawable.placeholder
                        ),
                        contentDescription = "Recipe Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = recipe.image,
                            placeholder = painterResource(id = R.drawable.placeholder)
                        ),
                        contentDescription = "Recipe Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                IconButton(
                    onClick = { viewModel.toggleFavorite(recipe.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
                if (recipe.author == currentUserId) {
                    IconButton(
                        onClick = {
                            viewModel.deleteRecipe(recipe.id)
                        },

                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_circle_24),
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
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
                            text = String.format("%.2f", recipe.rating_total / recipe.rating_count) + " (${recipe.rating_count})",
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
fun TagItem(tag: String) {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .background(
                color = Color(0xFF509654),
                shape = RoundedCornerShape(8.dp) // Rounded corners
            )
            .border(
                width = 1.dp,
                color = Color(0xFF4D754F),
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