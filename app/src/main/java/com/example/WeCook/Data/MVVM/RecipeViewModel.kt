package com.example.WeCook.Data.MVVM

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
//import com.example.WeCook.Data.Firebase.FirestoreRepository
import com.example.WeCook.Data.Retrofit.recipeList
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allTasks: kotlinx.coroutines.flow.Flow<List<RecipeEntity>>
    private val _firestoreRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val firestoreRecipes = _firestoreRecipes.asStateFlow()
    val db = Firebase.firestore

    init {
        val taskDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(taskDao)
        allTasks = repository.allTasks
        fetchFirestoreRecipes()
    }
    fun insertFavoriteRecipe(recipeId: String) {
        viewModelScope.launch {
            val recipe = recipeList.find { it.id == recipeId } ?: return@launch
            val recipeEntity = recipe.id?.let {
                RecipeEntity(
                    id = it,
                    isFavorite = false
                )
            }
            if (recipeEntity != null) {
                repository.insertFavoriteRecipe(recipeEntity)
            }

        }
    }
    fun deleteFavoriteRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.deleteFavoriteRecipe(recipeId)
        }
    }
    fun getRecipeDetails(recipeId: String): Recipe? {
        return recipeList.find { it.id == recipeId }
    }
    //    val favoriteRecipes: Flow<List<RecipeEntity>> = repository.getFavoriteRecipes()
    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            val recipeEntity = repository.allTasks.first().find { it.id == recipeId }
            if (recipeEntity != null) {
                if (recipeEntity.isFavorite) {
                    repository.deleteFavoriteRecipe(recipeId)
                } else {
                    insertFavoriteRecipe(recipeId)
                }
            }
            else {
                insertFavoriteRecipe(recipeId)
            }
        }
    }
    private fun fetchFirestoreRecipes() {
        viewModelScope.launch {
            try {
                db.collection("recipes")
                    .get()
                    .addOnSuccessListener { result ->
                        val recipes = result.documents.mapNotNull { document ->
                            document.toObject(Recipe::class.java)?.also { recipe ->
                                recipe.id = document.id
                            }
                        }
                        _firestoreRecipes.value = recipes
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }
}