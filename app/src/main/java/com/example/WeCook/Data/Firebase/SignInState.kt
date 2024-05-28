package com.example.WeCook.Data.Firebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)