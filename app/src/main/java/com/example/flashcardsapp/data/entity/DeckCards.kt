package com.example.flashcardsapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DeckCards(
    @Embedded val deck: Deck,
    @Relation(
        entity = Deck::class,
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<Card>
)
