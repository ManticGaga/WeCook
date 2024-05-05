package com.example.WeCook.Firebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)