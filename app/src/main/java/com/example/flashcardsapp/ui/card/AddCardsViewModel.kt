package com.example.flashcardsapp.ui.card

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository

class AddCardsViewModel(private val cardRepository: CardRepository) : ViewModel() {
    var cardUiState by mutableStateOf(CardUiState())
        private set

    fun updateUiState(uiState: CardUiState) {
        cardUiState = uiState.copy(questionErrorMessage = null, answerErrorMessage = null)
    }

    private fun validateInput(uiState: CardUiState = cardUiState): Boolean {
        return with(uiState) {
            if (question.trim().isBlank()) {
                cardUiState = copy(questionErrorMessage = QUESTION_NOT_BLANK)
                return false
            }

            if (answer.trim().isBlank()) {
                cardUiState = copy(answerErrorMessage = ANSWER_NOT_BLANK)
                return false
            }

            true
        }
    }

    suspend fun addCard(deckId: Int): Boolean {
        if (!validateInput()) {
            return false
        }

        cardRepository.insert(
            Card(
                id = 0,
                question = cardUiState.question.trim(),
                answer = cardUiState.answer.trim(),
                deckId = deckId
            )
        )

        resetUiState()
        return true
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

    companion object {
        const val QUESTION_NOT_BLANK = "Question cannot be blank!"
        const val ANSWER_NOT_BLANK = "Answer cannot be blank!"
    }
}

data class CardUiState(
    val question: String = "",
    val answer: String = "",
    val questionErrorMessage: String? = null,
    val answerErrorMessage: String? = null,
)