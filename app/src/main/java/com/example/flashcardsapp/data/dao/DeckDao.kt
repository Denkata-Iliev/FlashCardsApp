package com.example.flashcardsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flashcardsapp.data.entity.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert
    suspend fun insertAll(vararg decks: Deck)

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun deleteAll(vararg decks: Deck)

    @Query("SELECT * FROM decks")
    fun getAll(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id")
    fun getById(id: Int): Flow<Deck>

}