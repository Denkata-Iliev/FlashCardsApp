package com.example.flashcardsapp.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "decks",
    indices = [Index("name", unique = true)]
)
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)
