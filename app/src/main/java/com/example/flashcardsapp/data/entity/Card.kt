package com.example.flashcardsapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val question: String,
    val answer: String,
    var lastReviewed: Long = 0L,
    var interval: Int = 1,
    var repCount: Int = 0,
    var easeFactor: Double = 2.5,
    val deckId: Int
)
