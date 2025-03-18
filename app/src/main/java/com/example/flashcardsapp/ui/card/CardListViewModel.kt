package com.example.flashcardsapp.ui.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.DeckCards
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CardListViewModel(deckId: Int, private val cardRepository: CardRepository) : ViewModel() {

    private var _inSelectionMode = MutableStateFlow(false)
    val inSelectionMode = _inSelectionMode.asStateFlow()

    var showDeleteConfirm by mutableStateOf(false)
        private set

    private var _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    val cardListUiState: StateFlow<CardListUiState> =
        cardRepository.getAllCardsFromDeck(deckId)
            .map { CardListUiState(it, false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT),
                initialValue = CardListUiState(DeckCards(), false)
            )

    suspend fun delete(selectedIds: Set<Int>) {
        val cards = selectedIds.map { cardRepository.getById(it) }.toTypedArray()
        cardRepository.delete(*cards)
    }

    fun selectCard(cardId: Int) {
        _selectedIds.value += cardId
    }

    fun deselectCard(cardId: Int) {
        _selectedIds.value -= cardId
    }

    fun openDeleteConfirm() {
        showDeleteConfirm = true
    }

    fun closeDeleteConfirm() {
        showDeleteConfirm = false
    }

    fun selectAll(cards: List<Card>) {
        _selectedIds.value = cards.map { it.id }.toSet()
    }

    fun deselectAll() {
        _selectedIds.value = emptySet()
    }

    fun enterSelectionMode() {
        updateSelectionMode(true)
    }

    fun exitSelectionMode() {
        updateSelectionMode(false)
        deselectAll()
    }

    private fun updateSelectionMode(value: Boolean) {
        _inSelectionMode.value = value
        _inSelectionMode.value = !_inSelectionMode.value
        _inSelectionMode.value = value
    }

    companion object {
        const val TIMEOUT = 5000L
    }
}

data class CardListUiState(val deckWithCards: DeckCards, val isLoading: Boolean)