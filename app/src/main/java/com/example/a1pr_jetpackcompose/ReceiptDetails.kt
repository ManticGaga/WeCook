package com.example.a1pr_jetpackcompose
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.example.a1pr_jetpackcompose.MVVM.RecipeViewModel

@Composable
fun RecipeDetails(navBackStackEntry: NavBackStackEntry) {

}

//    val recipeId = navBackStackEntry.arguments?.getString("id")?.toInt() ?: 0
//    val recipeEntity by viewModel.allTasks.collectAsState(emptyList()).value.find { it.recipeId == recipeId }
//
//    // Extract attributes to local variables
//    val name = recipeEntity?.name ?: ""
//    val image = recipeEntity?.image ?: ""
//    val difficulty = recipeEntity?.difficulty ?: 0
//    val tagsString = recipeEntity?.tags ?: ""
//    val tags = tagsString.split(",") // Convert tags string to list
//
//    // Use the extracted variables to display details
//    Column(modifier = Modifier.padding(16.dp)) {
//        RecipeImage_fav(image, LocalContext.current) // Use RecipeImage_fav for consistency
//        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//        // ... display other details using local variables ...
//    }
//}

