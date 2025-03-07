package com.example.flashcardsapp.data

import android.content.Context
import com.example.flashcardsapp.data.repository.DeckRepository

class FlashCardAppContainer(private val context: Context) {
    val deckRepository: DeckRepository by lazy {
        DeckRepository(FlashCardDatabase.getDatabase(context).deckDao())
    }
}