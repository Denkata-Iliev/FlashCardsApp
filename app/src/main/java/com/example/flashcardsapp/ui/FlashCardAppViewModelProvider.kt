package com.example.flashcardsapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcardsapp.FlashCardApplication
import com.example.flashcardsapp.ui.card.AddCardsViewModel
import com.example.flashcardsapp.ui.card.CardListViewModel
import com.example.flashcardsapp.ui.card.EditCardViewModel
import com.example.flashcardsapp.ui.deck.DeckListViewModel

object FlashCardAppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeckListViewModel(
                flashCardApplication().container.deckRepository
            )
        }

        initializer {
            CardListViewModel(
                flashCardApplication().container.cardRepository
            )
        }

        initializer {
            AddCardsViewModel(
                flashCardApplication().container.cardRepository
            )
        }

        initializer {
            EditCardViewModel(
                flashCardApplication().container.cardRepository
            )
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)