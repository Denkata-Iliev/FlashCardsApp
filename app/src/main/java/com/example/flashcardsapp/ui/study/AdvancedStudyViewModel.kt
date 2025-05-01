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

class AdvancedStudyViewModel(
    private val applicationContext: Context,
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _advancedUiState = mutableStateOf(AdvancedUiState())
    val advancedUiState: State<AdvancedUiState> = _advancedUiState

    init {
        viewModelScope.launch {
            val limit = applicationContext.dataStore.data.map {
                it[SettingsKeys.advancedLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
            }.first()

            _advancedUiState.value = AdvancedUiState(
                cards = cardRepository.getRandomCards(limit = limit, deckId = deckId)
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