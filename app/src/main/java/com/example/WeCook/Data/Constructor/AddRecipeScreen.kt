import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
            onDifficultyChange = { newDifficulty ->
                recipeState = recipeState.copy(difficulty = newDifficulty)
            }, // Pass a lambda function to update recipeState.difficulty
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
    onDifficultyChange: (Int) -> Unit,  // Receive the difficulty change function
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

        DifficultySelector(recipeState.difficulty) { newDifficulty ->
            onDifficultyChange(newDifficulty)
        }

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editing Steps") }
            )
        }
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
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0
                ) {
                    Text("Previous Step")
                }

                Button(
                    onClick = {
                        if (currentStep < recipeState.steps.size - 1) {
                            currentStep++
                        } else {
                            recipeState.steps.add(RecipeStep(currentStep + 2, "", "", 0))
                            currentStep++
                        }
                    }
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

        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
            },
            label = { Text("Step Description") },
            modifier = Modifier.fillMaxWidth()
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
fun DifficultySelector(
    initialDifficulty: Int,
    onDifficultyChange: (Int) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }

    val icons = remember(selectedDifficulty) { // Key on selectedDifficulty
        List(5) { index ->
            IconState(index + 1, index + 1 <= selectedDifficulty)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        icons.forEach { iconState ->
            Icon(
                painter = painterResource(
                    id = if (iconState.isLit) R.drawable.ogon else R.drawable.ogoff
                ),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        selectedDifficulty = iconState.index
                        onDifficultyChange(iconState.index)
                    }
                    .size(48.dp)
            )
        }
    }
}

// A simple data class to hold the state of each icon
data class IconState(val index: Int, val isLit: Boolean)