package com.example.WeCook.Data.MVVM

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.example.WeCook.Data.Firebase.firestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allTasks: kotlinx.coroutines.flow.Flow<List<RecipeEntity>>
    private val _firestoreRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    var firestoreRecipes = _firestoreRecipes.asStateFlow()
    private lateinit var firestoreRepository: firestoreRepository

    init {
        val taskDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(taskDao)
        firestoreRepository = firestoreRepository(FirebaseFirestore.getInstance()) // Initialize firestoreRepository
        allTasks = repository.allTasks
        fetchFirestoreRecipes()
    }

    private fun fetchFirestoreRecipes() {
        viewModelScope.launch {
            try {
                val recipes = FirebaseFirestore.getInstance()
                    .collection("recipes")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { document ->
                        document.toObject(Recipe::class.java)?.also { recipe ->
                            recipe.id = document.id
                        }
                    }
                _firestoreRecipes.value = recipes
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching recipes", e)
            }
        }
    }

    fun updateRating(recipeId: String, personalRating: Int) {
        viewModelScope.launch {
            val recipe = firestoreRecipes.value?.find { it.id == recipeId }
            if (recipe != null) {
                val newRatingCount = recipe.rating_count + 1
                val newRatingTotal = ((recipe.rating_total * recipe.rating_count) + personalRating) / newRatingCount
                firestoreRepository.updateRecipeRating(recipeId, newRatingTotal, newRatingCount)
                firestoreRecipes = _firestoreRecipes.asStateFlow()
            }
        }
    }

    fun insertFavoriteRecipe(recipeId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { // Use withContext for background thread
                val recipeEntity = RecipeEntity(
                    id = recipeId,
                    isFavorite = true
                )
                repository.insertFavoriteRecipe(recipeEntity)
            }
        }
    }

    fun getRecipeDetails(recipeId: String): Recipe? {
        return firestoreRecipes.value.find { it.id == recipeId }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            Log.d("RecipeViewModel", "Attempting to toggle favorite for recipe ID: $recipeId")
            val recipeEntity = repository.allTasks.first().find { it.id == recipeId }

            if (recipeEntity != null) {
                Log.d("RecipeViewModel", "Recipe found, isFavorite = ${recipeEntity.isFavorite}")
                if (recipeEntity.isFavorite) {
                    repository.deleteFavoriteRecipe(recipeId)
                } else {
                    insertFavoriteRecipe(recipeId)
                }
            } else {
                Log.d("RecipeViewModel", "Recipe not found in database, inserting...")
                insertFavoriteRecipe(recipeId)
            }
        }
    }
    val favoriteRecipes: Flow<List<RecipeEntity>> = repository.getFavoriteRecipes()
}