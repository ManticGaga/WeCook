package com.example.WeCook.Data.Constructor
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import java.io.ByteArrayOutputStream
import java.util.UUID

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
    var showImageUploadDialog by remember { mutableStateOf(false) }
    var imageUploadProgress by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                uploadImageToFirebase(
                    uri = uri,
                    recipeName = name,
                    context = context,
                    onProgress = { progress -> imageUploadProgress = progress },
                    onSuccess = { downloadUrl ->
                        imageUrl = downloadUrl
                        showImageUploadDialog = false // Hide the dialog
                    },
                    onFailure = { exception ->
                        Log.e("Firebase", "Image upload failed: ${exception.message}")
                        showImageUploadDialog = false // Hide the dialog
                    }
                )
                showImageUploadDialog = true // Show the dialog
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
        Spacer(modifier = Modifier.height(8.dp))

        if (imageUrl != "placeholder") {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Receipt Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                launcher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (imageUrl == "placeholder") "Choose Receipt Image" else "Replace Image")
        }

        if (showImageUploadDialog) {
            ImageUploadDialog(
                imageUploadProgress = imageUploadProgress,
                onDismiss = { showImageUploadDialog = false }
            )
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

    if (recipeState.steps.isEmpty()) {
        recipeState.steps.add(RecipeStep(1, "", "", 0))
    }
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
            if (recipeState.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = recipeState.imageUrl),
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Current step editing
            StepEditing(
                currentStep = currentStep,
                recipeStep = recipeState.steps[currentStep], // Pass the correct step
                recipeState = recipeState
            ) { updatedStep ->
                // Update the step in the list
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
                    onClick = { previousStep() },
                    enabled = currentStep > 0
                ) {
                    Text("Previous Step")
                }

                Button(
                    onClick = { nextStep() },
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

@Composable
fun StepEditing(
    currentStep: Int,
    recipeStep: RecipeStep,
    recipeState: RecipeCreationState,
    onStepChange: (RecipeStep) -> Unit
) {
    var imageUploadProgress by remember { mutableStateOf(0f) }
    var imageUrl by remember { mutableStateOf(recipeStep.imageUrl) }
    var showImageUploadDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    var text by remember { mutableStateOf(recipeStep.text) }
    var info by remember { mutableStateOf(recipeStep.info.toString()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Upload the selected image to Firebase Storage
                showImageUploadDialog = true
                val imageName = UUID.randomUUID().toString()
                Log.d("ImageUpload", "Image name: $imageName") // Added logging
                val imageRef = storageReference.child("recipe_images/${recipeState.name}/${imageName}")
                val uploadTask = imageRef.putFile(uri)
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                    imageUploadProgress = progress
                }.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        onStepChange(recipeStep.copy(imageUrl = uri.toString()))
                        showImageUploadDialog = false // Hide the dialog after successful upload
                    }
                }.addOnFailureListener { exception ->
                    Log.e("Firebase", "Image upload failed: ${exception.message}")
                    showImageUploadDialog = false // Hide the dialog if upload fails
                }
            }
        }
    }

    Column {
        Text("Editing Step ${currentStep + 1}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                launcher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Receipt Image")
        }
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
        if (showImageUploadDialog) {
            ImageUploadDialog(
                imageUploadProgress = imageUploadProgress,
                onDismiss = { showImageUploadDialog = false }
            )
        }
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

@Composable
fun ImageUploadDialog(
    imageUploadProgress: Float,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Uploading Image") },
        text = {
            Column {
                LinearProgressIndicator(progress = imageUploadProgress / 100f)
                Text("Upload Progress: ${String.format("%.1f", imageUploadProgress)}%")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
fun uploadImageToFirebase(
    uri: Uri,
    recipeName: String,
    context: Context, // Add Context parameter
    onProgress: (Float) -> Unit,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("recipe_images/$recipeName/${UUID.randomUUID()}")

    // 1. Get the image as Bitmap (adjust based on your image loading library)
    val bitmap = try {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } catch (e: Exception) {
        onFailure(e)
        return
    }

    // 2. Resize and/or Crop
    val resizedBitmap = resizeBitmap(bitmap, 800) // Resize to max 800px (adjust as needed)
    // val croppedBitmap = cropToSquare(resizedBitmap) // Optional: Crop to a square

    // 3. Compress the bitmap (optional, for further reducing size)
    val byteArrayOutputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val compressedImageData = byteArrayOutputStream.toByteArray()

    // 4. Upload
    val uploadTask = imageRef.putBytes(compressedImageData)
    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
        onProgress(progress)
    }.addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onSuccess(downloadUri.toString())
        }
    }.addOnFailureListener(onFailure)
}

// Function to resize a Bitmap (keeping aspect ratio)
private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scale = maxSize.toFloat() / maxOf(width, height)
    return Bitmap.createScaledBitmap(bitmap, (width * scale).toInt(), (height * scale).toInt(), true)
}

// Function to crop a Bitmap to a square (optional)
private fun cropToSquare(bitmap: Bitmap): Bitmap {
    val minDimension = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - minDimension) / 2
    val y = (bitmap.height - minDimension) / 2
    return Bitmap.createBitmap(bitmap, x, y, minDimension, minDimension)
}