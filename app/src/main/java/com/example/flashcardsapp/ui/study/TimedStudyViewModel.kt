package com.example.flashcardsapp.ui.study

import androidx.lifecycle.ViewModel
import com.example.flashcardsapp.data.repository.CardRepository

class TimedStudyViewModel(
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
}