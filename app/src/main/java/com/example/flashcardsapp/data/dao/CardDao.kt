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

    @Query("""
        SELECT * FROM cards
        WHERE :currentTime >= lastReviewed + (interval * 60 * 60 * 1000) AND deckId = :deckId
        ORDER BY lastReviewed ASC
        LIMIT :limit
        """)
    suspend fun getDueCards(currentTime: Long, limit: Int, deckId: Int): List<Card>

    @Query("""
        SELECT * FROM cards
        WHERE deckId = :deckId
        ORDER BY RANDOM()
        LIMIT :limit
        """)
    suspend fun getRandomCards(limit: Int, deckId: Int): List<Card>
}