package com.example.flashcardsapp.data.repository

import com.example.flashcardsapp.data.dao.CardDao
import com.example.flashcardsapp.data.entity.Card

class CardRepository(private val cardDao: CardDao) {
    fun getAllCardsFromDeck(deckId: Int) = cardDao.getAllCardsFromDeck(deckId)

    suspend fun insert(card: Card) = cardDao.insertAll(card)
}