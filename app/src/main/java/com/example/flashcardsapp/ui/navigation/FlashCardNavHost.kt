package com.example.flashcardsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flashcardsapp.ui.deck.DeckListScreen

@Composable
fun FlashCardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = StartDestination,
        modifier = modifier
    ) {
        composable<StartDestination> {
            DeckListScreen()
        }
    }
}