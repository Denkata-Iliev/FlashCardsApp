package com.example.flashcardsapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcardsapp.FlashCardApplication
import com.example.flashcardsapp.ui.deck.DeckListViewModel

object FlashCardAppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeckListViewModel(
                flashCardApplication().container.deckRepository
            )
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)