package com.example.WeCook.BottomNavigation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.WeCook.Firebase.GoogleAuthUiClient
import com.example.WeCook.Firebase.SignIn
import com.example.WeCook.ui.theme._WeCookTheme
import com.google.android.gms.auth.api.identity.Identity

class MainActivity : ComponentActivity() {

    private lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize googleAuthUiClient
        googleAuthUiClient = GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )

        setContent {
            _WeCookTheme {
                if (googleAuthUiClient.getSignedInUser() != null) {
                    MainScreen()
                } else {
                    // Start SignIn activity
                    startActivity(Intent(this, SignIn::class.java))
                }
            }
        }
    }
}