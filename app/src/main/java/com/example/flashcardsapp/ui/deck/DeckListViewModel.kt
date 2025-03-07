package com.example.flashcardsapp.ui.deck

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.data.repository.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DeckListViewModel(private val deckRepository: DeckRepository) : ViewModel() {
    val deckListUiState: StateFlow<DeckListUiState> =
        deckRepository.getAll().map { DeckListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT),
                initialValue = DeckListUiState()
            )

    var createDeckUiState by mutableStateOf(CreateDeckUiState())
        private set

    fun updateCreateUiState(deckName: String) {
        createDeckUiState = CreateDeckUiState(deckName)
    }

    suspend fun createDeck() {
        if (validateInput()) {
            deckRepository.insertAll(Deck(0, createDeckUiState.deckName))
        }
    }

    private fun validateInput(createUiState: CreateDeckUiState = createDeckUiState): Boolean {
        return with(createUiState) {
            deckName.isNotBlank()
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}

data class DeckListUiState(val decks: List<Deck> = listOf())

data class CreateDeckUiState(val deckName: String = "")