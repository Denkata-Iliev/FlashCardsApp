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
import kotlinx.coroutines.launch

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

    suspend fun deleteByIds(ids: Set<Int>) {
        val decks = ids.map { deckRepository.getById(it) }.toTypedArray()

        deckRepository.deleteAll(*decks)
    }

    fun createDeck(onSuccess: () -> Unit) {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            if (deckRepository.existsByName(createDeckUiState.deckName)) {
                createDeckUiState = createDeckUiState.copy(errorMessage = "Deck name already exists!")
                return@launch
            }

            deckRepository.insertAll(Deck(0, createDeckUiState.deckName))
            createDeckUiState = CreateDeckUiState()
            onSuccess()
        }
    }

    private fun validateInput(createUiState: CreateDeckUiState = createDeckUiState): Boolean {
        return with(createUiState) {
            if (deckName.isBlank()) {
                createDeckUiState = createUiState.copy(errorMessage = "Deck name cannot be blank!")
                return false
            }

            true
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}

data class DeckListUiState(val decks: List<Deck> = listOf())

data class CreateDeckUiState(
    val deckName: String = "",
    val errorMessage: String? = null
)