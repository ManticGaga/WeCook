package com.example.WeCook.BottomNavigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Presenter.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(googleAuthUiClient: GoogleAuthUiClient, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val showProfile = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Create CoroutineScope
    Scaffold(
        modifier = Modifier.padding(top = 16.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("appTitle") },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        ProfileButton(
                            userData = googleAuthUiClient.getSignedInUser(),
                            onClick = { showProfile.value = true }
                        )
                    }
                }
            )
        },

        bottomBar = { BottomNavigation(navController = navController) }
        )
        { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navHostController = navController,
                googleAuthUiClient = googleAuthUiClient
            )
        if (showProfile.value) {
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthUiClient.signOut()
                        showProfile.value = false
                        onSignOut() // Call onSignOut from MainActivity
                    }
                },
                onClose = { showProfile.value = false }
            )
        }
    }
  }
}
