package com.example.a1pr_jetpackcompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FavouritesScreen() {
    Column {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            text = "Кушать хочу",
            textAlign = TextAlign.Center
        )
    }
}
