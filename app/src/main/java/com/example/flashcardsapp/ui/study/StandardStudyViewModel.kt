package com.example.flashcardsapp.ui.study

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.launch

class StandardStudyViewModel(
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _standardUiState = mutableStateOf(StandardUiState())
    val standardUiState: State<StandardUiState> = _standardUiState

    init {
        viewModelScope.launch {
            _standardUiState.value =
                StandardUiState(cards = cardRepository.getDueCardsFromDeck(System.currentTimeMillis(), 4, deckId))
        }
    }

    fun removeCardFromSession(card: Card) {
        val temp = _standardUiState.value.cards.toMutableList()
        temp.remove(card)
        _standardUiState.value = StandardUiState(cards = temp.toList())
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            cardRepository.update(card)
        }
    }
}

data class StandardUiState(val cards: List<Card> = listOf())

enum class RememberQuality(val recallScore: Int) {
    VeryHard(1),
    Hard(2),
    Medium(3),
    Easy(4),
    TooEasy(5)
}