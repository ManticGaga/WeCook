package com.example.WeCook.Data.Constructor
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.WeCook.Data.Constructor.RecipeCreationState
import com.example.WeCook.Data.Constructor.RecipeStep
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Data.MVVM.Recipe
import com.example.WeCook.R
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun AddRecipeScreen(
    navController: NavController,
    googleAuthUiClient: GoogleAuthUiClient,
    viewModel: RecipeViewModel = viewModel()
) {
    val authenticatedUser = googleAuthUiClient.getSignedInUser()
    var recipeState by remember {
        mutableStateOf(
            RecipeCreationState(author = authenticatedUser?.username ?: "")
        )
    }
    var showStepsEditing by remember { mutableStateOf(false) }

    if (!showStepsEditing) {
        RecipeInfoScreen(
            recipeState = recipeState,
            onNextClick = { updatedState ->
                recipeState = updatedState
                showStepsEditing = true
            },
            viewModel = viewModel
        )
    } else {
        StepsEditingScreen(
            navController = navController,
            initialRecipeState = recipeState
        )
    }
}

@Composable
fun RecipeInfoScreen(
    recipeState: RecipeCreationState,
    onNextClick: (RecipeCreationState) -> Unit,
    viewModel: RecipeViewModel
) {
    var name by remember { mutableStateOf(recipeState.name) }
    var tags by remember { mutableStateOf(recipeState.tags.joinToString(", ")) }
    var imageUrl by remember { mutableStateOf(recipeState.imageUrl) }
    var difficulty by remember { mutableStateOf(recipeState.difficulty) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                uploadImageToFirebaseStorage(uri) { downloadUrl ->
                    imageUrl = downloadUrl ?: ""
                }
            }
        }
    }

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

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                launcher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Recipe Image")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

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
                recipeState.steps[currentStep] = updatedStep;
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
                ) {
                    Text("Next Step")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
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
private fun uploadImageToFirebaseStorage(uri: Uri, onComplete: (String?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageName = UUID.randomUUID().toString()
    val imageRef = storageRef.child("recipe_images/$imageName")

    val uploadTask = imageRef.putFile(uri)

    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        Log.d(TAG, "Upload is $progress% done")
    }.addOnSuccessListener {
        // Get download URL
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onComplete(downloadUri.toString())
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to get download URL: ${exception.message}")
            onComplete(null)
        }
    }.addOnFailureListener { exception ->
        Log.e(TAG, "Image upload failed: ${exception.message}")
        onComplete(null)
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
    var info by remember { mutableStateOf(recipeStep.info.toString()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                uploadImageToFirebaseStorage(uri) { downloadUrl ->
                    imageUrl = downloadUrl ?: ""
                    onStepChange(recipeStep.copy(imageUrl = downloadUrl ?: ""))
                }
            }
        }
    }

    Column {
        Text("Editing Step ${currentStep + 1}")
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                launcher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Image for this Step")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display the uploaded image
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Step Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                onStepChange(recipeStep.copy(text = newValue))
            },
            label = { Text("Step Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .verticalScroll(rememberScrollState())
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = info,
            onValueChange = { newValue ->
                info = newValue
                onStepChange(recipeStep.copy(info = newValue.toIntOrNull() ?: 0))
            },
            label = { Text("Timer Value (seconds)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun ImageUploadDialog(
    imageUrl: String,
    onImageSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    imageUploadProgress: Float
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Receipt Image") },
        text = {
            Column {
                if (imageUrl.isNotEmpty()) {
                    Text("Current Image URL: $imageUrl")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (imageUploadProgress > 0f) {
                    LinearProgressIndicator(progress = imageUploadProgress / 100f)
                    Text("Upload Progress: ${imageUploadProgress.toInt()}%")
                }
            }
        },
        confirmButton = {
        }
    )
}