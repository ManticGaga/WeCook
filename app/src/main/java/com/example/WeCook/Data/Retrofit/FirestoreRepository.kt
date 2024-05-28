//package com.example.WeCook.Data.Firebase
//
//import android.content.ContentValues.TAG
//import android.util.Log
//import com.example.WeCook.Data.MVVM.Recipe
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.firestore.toObject
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.tasks.await
//
//class FirestoreRepository {
//
//    val db = Firebase.firestore
//    private val recipesCollection = db.collection("recipes")
//
//    suspend fun getRecipesFromFirestore(): List<Recipe> {
//        return try {
//            val snapshot = recipesCollection.get().await()
//            snapshot.documents.mapNotNull { document ->
//                document.toObject<Recipe>() // Map to Recipe directly
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting recipes", e) // Log the error
//            emptyList()
//        }
//    }
//}