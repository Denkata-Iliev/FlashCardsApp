package com.example.flashcardsapp.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.DeckCards
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CardListViewModel(private val cardRepository: CardRepository) : ViewModel() {
    fun cardListUiState(deckId: Int): StateFlow<CardListUiState> =
        cardRepository.getAllCardsFromDeck(deckId)
        .map { CardListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT),
            initialValue = CardListUiState(DeckCards())
        )



    companion object {
        const val TIMEOUT = 5000L
    }
}

data class CardListUiState(val deckWithCards: DeckCards)