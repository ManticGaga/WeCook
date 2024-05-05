package com.example.WeCook
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.WeCook.MVVM.RecipeViewModel
@Composable
fun RecipeDetails(viewModel: RecipeViewModel, recipeId: Int) {
    val recipe = viewModel.getRecipeDetails(recipeId)
    var currentStep by remember { mutableStateOf(0) }

    fun nextStep() {
        if (currentStep < recipe!!.stepstotal - 1) {
            currentStep++
        }
    }

    fun previousStep() {
        if (currentStep > 0) {
            currentStep--
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()) // Make the column scrollable
    ) {
        Box {
            // StepImage(recipe!!.receiptdetails_image[currentStep])
            val imageName = recipe!!.receiptdetails_image[currentStep]
            TODO("Лучше всего, если фотографии нет, будет добавлять фотографию предыдущего изображения.")
            TODO("Пока фото не появится.")
            if (imageName == "Пусто") {
                Text(
                    text = "Пусто",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            } else {
                val context = LocalContext.current
                val imageResource = context.resources.getIdentifier(
                    imageName, "drawable", context.packageName
                )
                if (imageResource != 0) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = 0.5f // Adjust gradient start to center
                                )
                            ), // Apply gradient from bottom to center
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Handle case where image is not found (optional)
                }
            }
            Text(
                text = "Step ${currentStep + 1} / ${recipe!!.stepstotal}",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp),
                color = Color.White
            )
        }

        Text(text = recipe!!.receiptdetails_text[currentStep], modifier = Modifier.padding(16.dp))

        if (recipe!!.receiptdetails_info[currentStep] > 0) {
            // Implement Timer using CountDownTimer
        }

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
                Text("Back")
            }
            Button(
                onClick = { nextStep() },
                enabled = currentStep < recipe.stepstotal - 1
            ) {
                Text("Forward")
            }
        }
    }
}