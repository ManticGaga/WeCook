package com.example.WeCook

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.WeCook.Data.MVVM.Recipe
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import com.example.WeCook.Data.MVVM.RecipeViewModel
import androidx.compose.material3.SnackbarHostState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch // Import for coroutineScope


@Composable
fun AddRecipeScreen(viewModel: RecipeViewModel = viewModel()) {
    val db = Firebase.firestore
    // Create a new user with a first and last name
    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815
    )

// Add a new document with a generated ID
      var author by remember { mutableStateOf("") }
      var name by remember { mutableStateOf("") }
      var difficulty by remember { mutableStateOf("1") }
      var tags by remember { mutableStateOf("") }
      var rating by remember { mutableStateOf("0") }
      var image by remember { mutableStateOf("placeholder") }
      var stepstotal by remember { mutableStateOf("1") }
      var receiptdetails_image by remember { mutableStateOf("") }
      var receiptdetails_text by remember { mutableStateOf("") }
      var receiptdetails_info by remember { mutableStateOf("") }
      val context = LocalContext.current
        val snackbarHostState = SnackbarHostState()
      Scaffold(
            snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
      ) { innerPadding ->
          Column(
              modifier = Modifier
                  .fillMaxSize()
                  .padding(16.dp)
                  .padding(innerPadding),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
          ) {
              TextField(
                  value = author,
                  onValueChange = { author = it },
                  label = { Text("Author") }
              )
              TextField(
                  value = name,
                  onValueChange = { name = it },
                  label = { Text("Name") }
              )
              TextField(
                  value = difficulty,
                  onValueChange = { difficulty = it },
                  label = { Text("Difficulty (1-5)") }
              )
              TextField(
                  value = tags,
                  onValueChange = { tags = it },
                  label = { Text("Tags (comma-separated)") }
              )
              // ... Add similar TextFields for other fields (rating, image, etc.)

              Button(
                  onClick = {
                      val newRecipe = Recipe(
                          author = author,
                          name = name,
                          difficulty = difficulty.toIntOrNull() ?: 1, // Handle invalid input
                          tags = tags.split(",").map { it.trim() },
                          rating = rating.toFloatOrNull() ?: 0f,
                          image = image,
                          stepstotal = stepstotal.toIntOrNull() ?: 1,
                          receiptdetails_image = receiptdetails_image.split(",").map { it.trim() },
                          receiptdetails_text = receiptdetails_text.split(",").map { it.trim() },
                          receiptdetails_info = receiptdetails_info.split(",").map { it.trim().toIntOrNull() ?: 0 }
                      )
                      db.collection("recipes")
                          .add(newRecipe)
                          .addOnSuccessListener { documentReference ->
                              Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                          }
                          .addOnFailureListener { e ->
                              Log.w(TAG, "Error adding document", e)
                          }
                  }
              ) {
                  Text("Add Recipe")
              }
          }
      }
}