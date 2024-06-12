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

@Composable
fun RecipeDetails(viewModel: RecipeViewModel, recipeId: String) {
    val recipe = viewModel.getRecipeDetails(recipeId)
    var currentStep by remember { mutableStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5) } // Default rating

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
            if (offsetX >= 1) {
                previousStep()
            } else if (offsetX <= -1) {
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

            Text(
                text = recipe.receiptdetails_text[currentStep],
                modifier = Modifier.padding(16.dp)
            )

            if (recipe.receiptdetails_info[currentStep] > 0) {
                // Implement Timer using CountDownTimer
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep == recipe.stepstotal - 1) {
                    Button(
                        onClick = { showRatingDialog = true },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Rate this Recipe")
                    }
                }
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