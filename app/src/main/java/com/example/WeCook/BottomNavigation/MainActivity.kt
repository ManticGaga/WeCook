package com.example.WeCook.BottomNavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.WeCook.ui.theme._1pr_jetpackcomposeTheme

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