package com.example.WeCook.Data.Constructor
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import com.example.WeCook.Data.MVVM.RecipeViewModel
import androidx.compose.material3.SnackbarHostState
import com.google.firebase.firestore.firestore
import androidx.compose.material3.OutlinedTextField // Use Material3 OutlinedTextField
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.WeCook.Data.Constructor.RecipeCreationState
import com.example.WeCook.Data.Constructor.RecipeStep
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Data.MVVM.Recipe
import com.example.WeCook.R
import com.google.firebase.Firebase

@Composable
fun AddRecipeScreen(
    navController: NavController, // Add NavController parameter
    googleAuthUiClient: GoogleAuthUiClient
) {
    val authenticatedUser = googleAuthUiClient.getSignedInUser()
    var recipeState by remember { mutableStateOf(
        RecipeCreationState(author = authenticatedUser?.username ?: "")
    ) }
    var showStepsEditing by remember { mutableStateOf(false) }

    if (!showStepsEditing) {
        // Show Recipe Info Screen
        RecipeInfoScreen(
            recipeState = recipeState,
            onNextClick = { updatedState ->
                recipeState = updatedState
                showStepsEditing = true
            }
        )
    } else {
        // Show Steps Editing Screen
        StepsEditingScreen(
            navController = navController,
            initialRecipeState = recipeState
        )
    }
}

@Composable
fun RecipeInfoScreen(
    recipeState: RecipeCreationState,
    onNextClick: (RecipeCreationState) -> Unit
) {
    var name by remember { mutableStateOf(recipeState.name) }
    var tags by remember { mutableStateOf(recipeState.tags.joinToString(", ")) }
    var imageUrl by remember { mutableStateOf(recipeState.imageUrl) }
    var difficulty by remember { mutableStateOf(recipeState.difficulty) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Recipe Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        DifficultySlider(difficulty) { newDifficulty ->
            difficulty = newDifficulty
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onNextClick(
                    recipeState.copy(
                        name = name,
                        difficulty = difficulty,
                        tags = tags.split(",").map { it.trim() },
                        imageUrl = imageUrl
                    )
                )
            }
        ) {
            Text("Next: Add Steps")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsEditingScreen(
    navController: NavController,
    initialRecipeState: RecipeCreationState
) {
    var recipeState by remember { mutableStateOf(initialRecipeState) }
    var currentStep by remember { mutableStateOf(0) }

    val db = Firebase.firestore
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    fun previousStep() {
        if (currentStep > 0) {
            currentStep--
        }
    }

    fun nextStep() {
        if (currentStep < recipeState.steps.size - 1) {
            currentStep++
        } else {
            recipeState.steps.add(RecipeStep(currentStep + 2, "", "", 0))
            currentStep++
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Current step editing
            StepEditing(
                currentStep = currentStep,
                recipeStep = recipeState.steps[currentStep]
            ) { updatedStep ->
                recipeState.steps[currentStep] = updatedStep
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { previousStep() }, // Call previousStep here
                    enabled = currentStep > 0
                ) {
                    Text("Previous Step")
                }

                Button(
                    onClick = { nextStep() }, // Call nextStep here
                    enabled = currentStep < recipeState.steps.size - 1
                ) {
                    Text("Next Step")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Create the Recipe object
                val newRecipe = Recipe(
                    author = recipeState.author,
                    name = recipeState.name,
                    difficulty = recipeState.difficulty,
                    tags = recipeState.tags,
                    image = recipeState.imageUrl,
                    stepstotal = recipeState.steps.size,
                    receiptdetails_image = recipeState.steps.map { it.imageUrl },
                    receiptdetails_text = recipeState.steps.map { it.text },
                    receiptdetails_info = recipeState.steps.map { it.info }
                )

                // Save to Firestore
                db.collection("recipes")
                    .add(newRecipe)
                    .addOnSuccessListener {
                        navController.navigate("RecipeList")
                    }
            }) {
                Text("Finish Recipe Editing")
            }
        }
    }
}

@Composable
fun StepEditing(
    currentStep: Int,
    recipeStep: RecipeStep,
    onStepChange: (RecipeStep) -> Unit
) {
    var imageUrl by remember { mutableStateOf(recipeStep.imageUrl) }
    var text by remember { mutableStateOf(recipeStep.text) }
    var info by remember { mutableStateOf(recipeStep.info.toString()) } // Store as String

    Column {
        Text("Editing Step ${currentStep + 1}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { newValue ->
                imageUrl = newValue
            },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Make the text field scrollable
        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
            },
            label = { Text("Step Description") },
            modifier = Modifier.fillMaxWidth()
                .height(100.dp) // Set a fixed height for the text field
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = info,
            onValueChange = { newValue ->
                info = newValue
            },
            label = { Text("Timer Value (seconds)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    LaunchedEffect(imageUrl, text, info) {
        onStepChange(recipeStep.copy(imageUrl = imageUrl, text = text, info = info.toIntOrNull() ?: 0))
    }
}

@Composable
fun DifficultySlider(
    initialDifficulty: Int,
    onDifficultyChange: (Int) -> Unit
) {
    var difficulty by remember { mutableStateOf(initialDifficulty) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = difficulty.toFloat(),
            onValueChange = { newValue ->
                difficulty = newValue.toInt()
                onDifficultyChange(difficulty)
            },
            valueRange = 1f..5f,
            steps = 4, // Allow for 4 steps between 1 and 5
            modifier = Modifier.weight(1f) // Makes the slider take up the remaining space
        )
        Spacer(modifier = Modifier.width(8.dp)) // Add some spacing
        Text(text = difficulty.toString()) // Display the value
    }
}

