package com.example.flashcardsapp.data

import android.content.Context
import com.example.flashcardsapp.data.repository.CardRepository
import com.example.flashcardsapp.data.repository.DeckRepository

class FlashCardAppContainer(private val context: Context) {
    val deckRepository: DeckRepository by lazy {
        DeckRepository(
            deckDao = FlashCardDatabase.getDatabase(context).deckDao(),
            cardDao = FlashCardDatabase.getDatabase(context).cardDao()
        )
    }

    val cardRepository: CardRepository by lazy {
        CardRepository(FlashCardDatabase.getDatabase(context).cardDao())
    }
}