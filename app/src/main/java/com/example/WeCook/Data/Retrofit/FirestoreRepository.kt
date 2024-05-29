package com.example.WeCook.Data.Firebase


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class firestoreRepository(private val db: FirebaseFirestore) {
    suspend fun updateRecipeRating(recipeId: String, newRatingTotal: Float, newRatingCount: Int) {
        val recipeRef = db.collection("recipes").document(recipeId)
        recipeRef.update(
            mapOf(
                "rating_total" to newRatingTotal,
                "rating_count" to newRatingCount
            )
        ).await()
    }

    suspend fun updateUserRating(recipeId: String, userRatings: Map<String, Int>) {
        val recipeRef = db.collection("recipes").document(recipeId)
        recipeRef.update(
            mapOf(
                "userRatings" to userRatings // Update the userRatings map
            )
        ).await()
    }
}