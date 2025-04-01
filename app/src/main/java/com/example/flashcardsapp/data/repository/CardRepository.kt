package com.example.flashcardsapp.data.repository

import com.example.flashcardsapp.data.dao.CardDao
import com.example.flashcardsapp.data.entity.Card

class CardRepository(private val cardDao: CardDao) {
    fun getAllCardsFromDeck(deckId: Int) = cardDao.getAllCardsFromDeck(deckId)
    suspend fun getDueCardsFromDeck(currentTime: Long, limit: Int, deckId: Int) = cardDao.getDueCards(currentTime, limit, deckId)

    suspend fun insert(card: Card) = cardDao.insertAll(card)
    suspend fun getById(id: Int) = cardDao.getById(id)
    suspend fun update(card: Card) = cardDao.update(card)
    suspend fun delete(vararg cards: Card) = cardDao.deleteAll(*cards)
}