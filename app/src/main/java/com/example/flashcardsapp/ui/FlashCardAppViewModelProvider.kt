package com.example.flashcardsapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcardsapp.FlashCardApplication
import com.example.flashcardsapp.ui.card.AddCardsViewModel
import com.example.flashcardsapp.ui.card.CardListViewModel
import com.example.flashcardsapp.ui.card.EditCardViewModel
import com.example.flashcardsapp.ui.deck.DeckListViewModel
import com.example.flashcardsapp.ui.settings.SettingsViewModel
import com.example.flashcardsapp.ui.study.AdvancedStudyViewModel
import com.example.flashcardsapp.ui.study.StandardStudyViewModel
import com.example.flashcardsapp.ui.study.TimedStudyViewModel

class CustomFactories {
    companion object {
        fun cardListFactory(deckId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CardListViewModel(
                    deckId = deckId,
                    cardRepository = flashCardApplication().container.cardRepository
                )
            }
        }

        fun standardStudyFactory(deckId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StandardStudyViewModel(
                    deckId = deckId,
                    cardRepository = flashCardApplication().container.cardRepository
                )
            }
        }

        fun timedStudyFactory(deckId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TimedStudyViewModel(
                    deckId = deckId,
                    cardRepository = flashCardApplication().container.cardRepository
                )
            }
        }

        fun advancedStudyFactory(deckId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AdvancedStudyViewModel(
                    deckId = deckId,
                    cardRepository = flashCardApplication().container.cardRepository
                )
            }
        }
    }
}

object FlashCardAppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeckListViewModel(
                flashCardApplication().container.deckRepository
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

        initializer {
            SettingsViewModel(flashCardApplication().applicationContext)
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)