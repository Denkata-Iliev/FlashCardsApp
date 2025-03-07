package com.example.flashcardsapp.data.repository

import com.example.flashcardsapp.data.dao.DeckDao
import com.example.flashcardsapp.data.entity.Deck
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {
    suspend fun insertAll(vararg decks: Deck) = deckDao.insertAll(*decks)

    suspend fun deleteAll(vararg decks: Deck) = deckDao.deleteAll(*decks)

    suspend fun update(deck: Deck) = deckDao.update(deck)

    fun getAll(): Flow<List<Deck>> = deckDao.getAll()

    fun getById(id: Int): Flow<Deck> = deckDao.getById(id)

    suspend fun existsByName(name: String) = deckDao.countByName(name) >= 1
}