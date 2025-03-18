package com.example.flashcardsapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DeckCards(
    @Embedded val deck: Deck = Deck(0, ""),
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<Card> = emptyList()
)
