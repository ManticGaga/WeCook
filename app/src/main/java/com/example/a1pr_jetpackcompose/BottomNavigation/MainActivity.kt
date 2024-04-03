package com.example.a1pr_jetpackcompose.BottomNavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.a1pr_jetpackcompose.ui.theme._1pr_jetpackcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            _1pr_jetpackcomposeTheme {
                MainScreen()
            }
        }
    }
}