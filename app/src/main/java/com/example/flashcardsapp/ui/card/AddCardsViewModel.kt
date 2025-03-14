package com.example.flashcardsapp.ui.card

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.launch

class AddCardsViewModel(private val cardRepository: CardRepository) : ViewModel() {
    var cardUiState by mutableStateOf(CardUiState())
        private set

    fun updateUiState(uiState: CardUiState) {
        cardUiState = uiState.copy()
    }

    private fun validateInput(uiState: CardUiState = cardUiState): Boolean {
        return with(uiState) {
            if (question.trim().isBlank()) {
                cardUiState = copy(questionErrorMessage = "Question cannot be blank!")
                return false
            }

            if (answer.trim().isBlank()) {
                cardUiState = copy(answerErrorMessage = "Answer cannot be blank!")
                return false
            }

            true
        }
    }

    fun addCard(deckId: Int) {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            cardRepository.insert(
                Card(
                    id = 0,
                    question = cardUiState.question.trim(),
                    answer = cardUiState.answer.trim(),
                    deckId = deckId
                )
            )

            resetUiState()
        }
    }

    fun requestFocus(focusRequester: FocusRequester) {
        focusRequester.requestFocus()
    }

    suspend fun displaySnackbar(snackbarHostState: SnackbarHostState) {
        snackbarHostState.showSnackbar("Card added")
    }

    private fun resetUiState() {
        cardUiState = CardUiState()
    }
}

data class CardUiState(
    val question: String = "",
    val answer: String = "",
    val questionErrorMessage: String? = null,
    val answerErrorMessage: String? = null,
)