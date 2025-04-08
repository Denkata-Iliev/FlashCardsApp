package com.example.flashcardsapp.data.repository

import androidx.room.Transaction
import com.example.flashcardsapp.data.dao.CardDao
import com.example.flashcardsapp.data.dao.DeckDao
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.ui.deck.ExportableDeck
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao, private val cardDao: CardDao) {
    suspend fun insertAll(vararg decks: Deck) = deckDao.insertAll(*decks)

    suspend fun deleteAll(vararg decks: Deck) = deckDao.deleteAll(*decks)

    suspend fun update(deck: Deck) = deckDao.update(deck)

    fun getAll(): Flow<List<Deck>> = deckDao.getAll()

    suspend fun getById(id: Int): Deck = deckDao.getById(id)

    suspend fun existsByName(name: String) = deckDao.countByName(name) >= 1

    suspend fun getDecksWithCardsById(ids: List<Int>) = deckDao.getDecksWithCardsById(ids)

    @Transaction
    suspend fun insertDecksWithCards(decks: List<ExportableDeck>) {
        // get all decks' names so later I can generate a unique name
        // for imported decks with conflicting names
        val existingDecks = getAllSuspend()
        val existingNames = existingDecks.map { it.name }.toMutableSet()

        val allCards = mutableListOf<Card>()
        val newDecks = mutableListOf<ExportableDeck>()
        val newDecksDb = mutableListOf<Deck>()

        decks.forEach { exportableDeck ->
            // generate a unique name for each deck with a conflicting name,
            // then add it to the set of existing names
            val uniqueName = generateUniqueName(exportableDeck.name, existingNames)
            existingNames.add(uniqueName)

            val newDeck = Deck(id = 0, name = uniqueName)

            // add the deck with now unique name
            // to the decks that will be added to the db
            newDecks.add(exportableDeck.copy(name = newDeck.name))
            newDecksDb.add(newDeck)
        }

        // get a list of ids of the inserted decks
        val insertedDeckIds = deckDao.insertAll(*newDecksDb.toTypedArray())

        // pair the deck ids with the actual decks (and their cards)
        // and map from ExportableCard to Card entity
        insertedDeckIds.zip(newDecks) { deckId, deck ->
            val cards = deck.cards.map {
                Card(
                    id = 0,
                    question = it.question,
                    answer = it.answer,
                    deckId = deckId.toInt()
                )
            }

            allCards.addAll(cards)
        }

        cardDao.insertAll(*allCards.toTypedArray())
    }

    private fun generateUniqueName(baseName: String, existingNames: Set<String>): String {
        if (baseName !in existingNames) {
            return baseName
        }

        var i = 1
        var newName: String
        do {
            newName = "$baseName ($i)"
            i++
        } while (newName in existingNames)

        return newName
    }

    private suspend fun getAllSuspend(): List<Deck> = deckDao.getAllSuspend()
}