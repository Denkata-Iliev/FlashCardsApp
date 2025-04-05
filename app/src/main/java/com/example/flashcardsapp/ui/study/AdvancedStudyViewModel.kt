package com.example.flashcardsapp.ui.study

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.launch

class AdvancedStudyViewModel(
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _advancedUiState = mutableStateOf(AdvancedUiState())
    val advancedUiState: State<AdvancedUiState> = _advancedUiState

    init {
        viewModelScope.launch {
            _advancedUiState.value = AdvancedUiState(
                cards = cardRepository.getRandomCards(limit = 10, deckId = deckId)
            )
        }
    }

    fun removeCardFromSession(card: Card) {
        val temp = _advancedUiState.value.cards.toMutableList()
        temp.remove(card)
        _advancedUiState.value = AdvancedUiState(temp.toList())
    }
}

data class AdvancedUiState(val cards: List<Card> = listOf())