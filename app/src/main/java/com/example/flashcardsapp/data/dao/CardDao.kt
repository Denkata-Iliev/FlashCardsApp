package com.example.flashcardsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.DeckCards
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insertAll(vararg cards: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun deleteAll(vararg cards: Card)

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getAllCardsFromDeck(deckId: Int): Flow<DeckCards>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Int): Card
}