package com.example.flashcardsapp.ui.study

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import com.example.flashcardsapp.ui.settings.SettingsDefaults
import com.example.flashcardsapp.ui.settings.SettingsKeys
import com.example.flashcardsapp.ui.settings.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.max

class StandardStudyViewModel(
    private val applicationContext: Context,
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _standardUiState = mutableStateOf(StandardUiState())
    val standardUiState: State<StandardUiState> = _standardUiState

    init {
        viewModelScope.launch {
            val limit = applicationContext.dataStore.data.map {
                it[SettingsKeys.standardLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
            }.first()

            _standardUiState.value = StandardUiState(
                cards = cardRepository.getDueCardsFromDeck(
                    currentTime = System.currentTimeMillis(),
                    limit = limit,
                    deckId = deckId
                )
            )
        }
    }

    fun removeCardFromSession(card: Card) {
        val temp = _standardUiState.value.cards.toMutableList()
        temp.remove(card)
        _standardUiState.value = StandardUiState(cards = temp.toList())
    }

    private fun updateCardDb(card: Card) {
        viewModelScope.launch {
            cardRepository.update(card)
        }
    }

    fun updateCard(card: Card, recallScore: Int): Card {
        // all of the magic numbers are based on the official implementation of
        // the spaced repetition algorithm of SM-2: https://github.com/thyagoluciano/sm2

        // modification: instead of interval going from 1 to 6 days on the first and second review,
        // I've decided it'll be better if the cards appear a bit more often,
        // going from 12 to 24 to 48 hours on the first, second and third review respectively,
        // after which, the interval is based on the ease factor
        if (recallScore >= 3) {
            when (card.repCount) {
                0, 1 -> card.interval = 12.0
                2 -> card.interval = 24.0
                3 -> card.interval = 48.0
                else -> card.interval *= card.easeFactor
            }

            // Update the repetition count
            card.repCount++

            // Update ease factor based on recall score
            card.easeFactor += (0.1 - (5 - recallScore) * (0.08 + (5 - recallScore) * 0.02))
            card.easeFactor = max(1.3, card.easeFactor) // Prevent ease factor from going below 1.3
        } else {
            card.repCount = 0
            card.interval = 12.0
        }

        // Update the last reviewed timestamp
        card.lastReviewed = System.currentTimeMillis()

        updateCardDb(card)

        return card
    }
}

data class StandardUiState(val cards: List<Card> = listOf())