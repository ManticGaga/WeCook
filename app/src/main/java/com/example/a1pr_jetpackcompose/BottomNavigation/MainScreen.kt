package com.example.a1pr_jetpackcompose.BottomNavigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.a1pr_jetpackcompose.MVVM.RecipeViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun  MainScreen()
{
    val navController = rememberNavController()
//    val viewModel = viewModel<RecipeViewModel>()
    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    )

    {
        NavGraph(navHostController = navController)
    }
}