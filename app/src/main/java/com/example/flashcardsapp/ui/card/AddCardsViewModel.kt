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
            if (question.trim().isBlank() || question.trim().length > QA_LENGTH_LIMIT) {
                cardUiState = copy(questionErrorMessage = QUESTION_ERROR_MESSAGE)
                return false
            }

            if (answer.trim().isBlank() || answer.trim().length > QA_LENGTH_LIMIT) {
                cardUiState = copy(answerErrorMessage = ANSWER_ERROR_MESSAGE)
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
        const val QA_LENGTH_LIMIT = 200
        const val QUESTION_ERROR_MESSAGE = "Question cannot be blank and must be less than $QA_LENGTH_LIMIT characters long!"
        const val ANSWER_ERROR_MESSAGE = "Answer cannot be blank and must be less than $QA_LENGTH_LIMIT characters long!"
    }
}

data class CardUiState(
    val question: String = "",
    val answer: String = "",
    val questionErrorMessage: String? = null,
    val answerErrorMessage: String? = null,
)