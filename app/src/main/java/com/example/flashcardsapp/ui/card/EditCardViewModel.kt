package com.example.flashcardsapp.ui.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository

class EditCardViewModel(private val cardRepository: CardRepository) : ViewModel() {
    var cardUiState by mutableStateOf(CardUiState())
        private set

    fun updateUiState(uiState: CardUiState) {
        cardUiState = uiState.copy(questionErrorMessage = null, answerErrorMessage = null)
    }

    private fun validateInput(uiState: CardUiState = cardUiState): Boolean {
        return with(uiState) {
            if (question.trim().isBlank()) {
                cardUiState = copy(questionErrorMessage = AddCardsViewModel.QUESTION_NOT_BLANK)
                return false
            }

            if (answer.trim().isBlank()) {
                cardUiState = copy(answerErrorMessage = AddCardsViewModel.ANSWER_NOT_BLANK)
                return false
            }

            true
        }
    }

    suspend fun updateCard(id: Int): Boolean {
        if (!validateInput()) {
            return false
        }

        val card = cardRepository.getById(id)
        cardRepository.update(
            Card(
                id = card.id,
                question = cardUiState.question.trim(),
                answer = cardUiState.answer.trim(),
                deckId = card.deckId
            )
        )

        resetUiState()

        return true
    }

    private fun resetUiState() {
        cardUiState = CardUiState()
    }

    suspend fun populateUiState(cardId: Int) {
        val card = cardRepository.getById(cardId)
        cardUiState = CardUiState(
            question = card.question,
            answer = card.answer
        )
    }
}