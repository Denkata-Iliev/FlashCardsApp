package com.example.flashcardsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.flashcardsapp.ui.card.AddCardsScreen
import com.example.flashcardsapp.ui.card.CardListScreen
import com.example.flashcardsapp.ui.card.EditCardScreen
import com.example.flashcardsapp.ui.deck.DeckListScreen
import com.example.flashcardsapp.ui.settings.SettingsScreen
import com.example.flashcardsapp.ui.study.AdvancedStudyScreen
import com.example.flashcardsapp.ui.study.StandardStudyScreen
import com.example.flashcardsapp.ui.study.TimedStudyScreen

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
            DeckListScreen(
                navigateToDeck = { deckId ->
                    navController.navigate(CardListDestination(deckId))
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsDestination)
                }
            )
        }

        composable<CardListDestination> {
            val cardListDest: CardListDestination = it.toRoute()
            CardListScreen(
                deckId = cardListDest.deckId,
                onNavigateBackUp = {
                    navController.navigateUp()
                },
                onNavigateToAddCards = {
                    navController.navigate(AddCardsDestination(deckId = cardListDest.deckId))
                },
                onNavigateToStandardStudy = {
                    navController.navigate(StandardStudyDestination(deckId = cardListDest.deckId))
                },
                onNavigateToTimedStudy = {
                    navController.navigate(TimedStudyDestination(deckId = cardListDest.deckId))
                },
                onNavigateToAdvancedStudy = {
                    navController.navigate(AdvancedStudyDestination(deckId = cardListDest.deckId))
                },
                onNavigateToCard = { cardId ->
                    navController.navigate(EditCardDestination(cardId = cardId))
                }
            )
        }

        composable<AddCardsDestination> {
            val destination: AddCardsDestination = it.toRoute()
            AddCardsScreen(
                deckId = destination.deckId,
                onNavigateBackUp = { navController.navigateUp() }
            )
        }

        composable<EditCardDestination> {
            val destination: EditCardDestination = it.toRoute()
            EditCardScreen(
                cardId = destination.cardId,
                onNavigateBackUp = { navController.navigateUp() }
            )
        }

        composable<StandardStudyDestination> {
            val destination: StandardStudyDestination = it.toRoute()
            StandardStudyScreen(
                deckId = destination.deckId,
                onNavigateBackUp = { navController.navigateUp() }
            )
        }

        composable<TimedStudyDestination> {
            val destination: TimedStudyDestination = it.toRoute()
            TimedStudyScreen(
                deckId = destination.deckId,
                onNavigateBackUp = { navController.navigateUp() }
            )
        }

        composable<AdvancedStudyDestination> {
            val destination: AdvancedStudyDestination = it.toRoute()
            AdvancedStudyScreen(
                deckId = destination.deckId,
                onNavigateBackUp = { navController.navigateUp() }
            )
        }

        composable<SettingsDestination> {
            SettingsScreen(onNavigateBackUp = { navController.navigateUp() })
        }
    }
}