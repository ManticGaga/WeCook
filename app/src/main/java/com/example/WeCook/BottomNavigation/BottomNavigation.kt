package com.example.WeCook.BottomNavigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(navController: NavController) {
    val listItems = listOf(
        BottomItem.RecipeList,
        BottomItem.Add,
        BottomItem.Favourites
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        listItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = "Icon",
                        tint = if (currentRoute == item.route) Color(0xFF26962C) else Color.Gray
                    )
                },
                label = {
                    // Apply style to the Text composable
                    Text(
                        text = item.title,
                        fontSize = 9.sp,
                        style = TextStyle(
                            color = if (currentRoute == item.route) Color(0xFF26962C) else Color.Gray
                        )
                    )
                }
            )
        }
    }
}