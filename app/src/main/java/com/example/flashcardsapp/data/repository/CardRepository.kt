package com.example.flashcardsapp.data.repository

import com.example.flashcardsapp.data.dao.CardDao

class CardRepository(private val cardDao: CardDao) {
    fun getAllCardsFromDeck(deckId: Int) = cardDao.getAllCardsFromDeck(deckId)
}