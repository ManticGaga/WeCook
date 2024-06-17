package com.example.WeCook.Presenter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import com.example.WeCook.Data.MVVM.RecipeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipeDetails(viewModel: RecipeViewModel, recipeId: String) {
    val recipe = viewModel.getRecipeDetails(recipeId)
    var currentStep by remember { mutableStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5) } // Default rating
    // Timer state
    var timerDuration by remember { mutableStateOf(0L) } // Seconds
    var timerRunning by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(0L) }
    var timerJob: Job? = null
    // Create a CoroutineScope within RecipeDetails
    val coroutineScope = rememberCoroutineScope()
    fun nextStep() {
        if (recipe != null) {
            if (currentStep < recipe.stepstotal - 1) {
                currentStep++
            }
        }
    }
    fun previousStep() {
        if (currentStep > 0) {
            currentStep--
        }
    }
    fun startTimer() {
        if (!timerRunning) {
            timerJob = coroutineScope.launch {
                while (timeRemaining > 0) {
                    delay(1000L) // Delay for 1 second
                    timeRemaining -= 1000L
                }
                // Timer finished
                timerRunning = false
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerRunning = false
    }

    fun resetTimer() {
        stopTimer()
        timeRemaining = timerDuration
    }

    if (recipe != null) {
        // State for handling swipe animation
        var offsetX by remember { mutableStateOf(0f) }
        var isSwiping by remember { mutableStateOf(false) }

        // Function to handle swipe animation
        fun onSwipe(dragAmount: Float) {
            offsetX = dragAmount
            isSwiping = true
        }

        fun onSwipeEnd() {
            if (offsetX > 0) {
                previousStep()
            } else if (offsetX < 0) {
                nextStep()
            }
            offsetX = 0f
            isSwiping = false
        }

        // Functions to move to the next/previous step




        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { onSwipe(0f) },
                        onHorizontalDrag = {_ , dragAmount -> onSwipe(dragAmount) },
                        onDragEnd = { onSwipeEnd() }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .offset { IntOffset(offsetX.toInt(), 0) } // Apply offset for swipe animation
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(recipe.receiptdetails_image[currentStep]),
                    contentDescription = "Recipe Step Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Step ${currentStep + 1} / ${recipe.stepstotal}",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(8.dp),
                    color = Color.White
                )
            }

            if (isSwiping) {
                // Show a visual indication during swipe (optional)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                        .align(Alignment.Start)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(16.dp) // Add internal padding
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Fixed height
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = recipe.receiptdetails_text[currentStep],
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (recipe.receiptdetails_info[currentStep] > 0) {
                timerDuration = recipe.receiptdetails_info[currentStep] * 1000L // Convert to milliseconds
                timeRemaining = timerDuration
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Timer: ${formatTime(timeRemaining)}",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!timerRunning) {
                            Button(onClick = { startTimer() }) {
                                Text("Start")
                            }
                        } else {
                            Button(onClick = { stopTimer() }) {
                                Text("Stop")
                            }
                        }
                        Button(onClick = { resetTimer() }) {
                            Text("Reset")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showRatingDialog) {
                    AlertDialog(
                        onDismissRequest = { showRatingDialog = false },
                        title = { Text("Rate the Recipe") },
                        text = {
                            Column {
                                Slider(
                                    value = userRating.toFloat(),
                                    onValueChange = { userRating = it.toInt() },
                                    valueRange = 1f..10f, // Rating from 1 to 10
                                    steps = 9,
                                )
                                Text("Your Rating: $userRating")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.updateRating(recipeId, userRating)
                                showRatingDialog = false
                            }) {
                                Text("Submit Rating")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showRatingDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    } else {
        Text("Recipe not found!")
    }
}
fun formatTime(timeInMillis: Long): String {
    val seconds = (timeInMillis / 1000) % 60
    val minutes = timeInMillis / (1000 * 60) % 60
    return String.format("%02d:%02d", minutes, seconds)
}