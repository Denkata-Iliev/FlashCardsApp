package com.example.flashcardsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.data.entity.DeckCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert
    suspend fun insertAll(vararg decks: Deck): List<Long>

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun deleteAll(vararg decks: Deck)

    @Query("SELECT * FROM decks")
    fun getAll(): Flow<List<Deck>>

    @Query("SELECT * FROM decks")
    suspend fun getAllSuspend(): List<Deck>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getById(id: Int): Deck

    @Query("SELECT COUNT(id) FROM decks WHERE LOWER(name) = LOWER(:name)")
    suspend fun countByName(name: String): Int

    @Query("SELECT * FROM decks WHERE id IN (:ids)")
    suspend fun getDecksWithCardsById(ids: List<Int>): List<DeckCards>

}