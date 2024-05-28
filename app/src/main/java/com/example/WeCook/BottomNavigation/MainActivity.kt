package com.example.WeCook.BottomNavigation

import androidx.compose.runtime.*
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Data.Firebase.SignIn
import com.example.WeCook.Data.Firebase.UserData
import com.example.WeCook.ProfileScreen
import com.example.WeCook.ui.theme._WeCookTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthUiClient = GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
        setContent {
            _WeCookTheme {
                if (googleAuthUiClient.getSignedInUser() != null) {
                    MainScreen(
                        googleAuthUiClient = googleAuthUiClient,
                        onSignOut = {
                            startActivity(Intent(this, SignIn::class.java))
                            finish() // Close MainActivity
                        }
                    )
                } else {
                    startActivity(Intent(this, SignIn::class.java))
                    finish() // Close MainActivity if not signed in
                }
            }
        }
    }
}


@Composable
fun ProfileButton(userData: UserData?, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current // Get screen configuration
    val screenWidth = configuration.screenWidthDp.dp // Get screen width in dp

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .width(screenWidth * 0.25f), // Set width to 25% of screen width
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (userData?.username != null) {
            Text(
                text = userData.username,
                maxLines = if (userData.username.contains(" ")) 2 else 1, // Dynamic maxLines
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}