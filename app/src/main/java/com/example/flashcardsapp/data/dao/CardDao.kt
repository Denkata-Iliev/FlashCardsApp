package com.example.flashcardsapp.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.DeckCards
import kotlinx.coroutines.flow.Flow

interface CardDao {
    @Insert
    suspend fun insertAll(vararg cards: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun deleteAll(vararg cards: Card)

    @Transaction
    @Query("SELECT * FROM decks")
    suspend fun getAllCardsFromDeck(): Flow<List<DeckCards>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Int): Flow<Card>
}