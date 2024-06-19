package com.example.WeCook.BottomNavigation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.WeCook.Data.Firebase.GoogleAuthUiClient
import com.example.WeCook.Presenter.ProfileScreen
import com.example.WeCook.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(googleAuthUiClient: GoogleAuthUiClient, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val showProfile = remember { mutableStateOf(false) }
    val showSupportDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Create CoroutineScope
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            contentAlignment = Alignment.TopStart
                        ){
                            SupportButton(onClick = { showSupportDialog.value = true })
                        }
                        Box(
                            contentAlignment = Alignment.TopCenter
                        ){
                            Text("WeCook")
                        }
                        Box(
                            contentAlignment = Alignment.TopEnd
                        ) {
                            ProfileButton(
                                userData = googleAuthUiClient.getSignedInUser(),
                                navController = navController, // Pass the NavController
                                onClick = { navController.navigate("Profile") } // Navigate to Profile
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                }
            )
        },

        bottomBar = { BottomNavigation(navController = navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navHostController = navController,
                googleAuthUiClient = googleAuthUiClient,
                onSignOut = onSignOut
            )
            if (showProfile.value) {
                ProfileScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        onSignOut()
                    },
                    onClose = { showProfile.value = false }
                )
            }
            if (showSupportDialog.value) {
                SupportDialog(
                    onDismiss = { showSupportDialog.value = false },
                    onSend = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:ManticGaga@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "WeCook Support Request")
                            putExtra(Intent.EXTRA_TEXT, "Please describe your technical issue here:")
                        }
                        if (context.packageManager.resolveActivity(emailIntent, 0) != null) {
                            context.startActivity(emailIntent)
                        } else {
                            // Handle the case where there is no email app available
                            // You could show a message or use another method
                        }
                        showSupportDialog.value = false
                    }
                )
            }
        }
    }
}
@Composable
fun SupportButton(onClick: () -> Unit) {
    val triangleIcon = painterResource(id = R.drawable.baseline_handyman_24)
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = triangleIcon,
                    contentDescription = "Support",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
fun SupportDialog(onDismiss: () -> Unit, onSend: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tech Support") },
        text = { Text("Please describe your technical issue or ask for assistance.") },
        confirmButton = {
            Button(onClick = onSend) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}