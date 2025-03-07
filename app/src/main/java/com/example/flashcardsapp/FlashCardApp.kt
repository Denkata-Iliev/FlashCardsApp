package com.example.flashcardsapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.flashcardsapp.ui.navigation.FlashCardNavHost

@Composable
fun FlashCardApp(navController: NavHostController = rememberNavController()) {
    FlashCardNavHost(navController = navController)
}