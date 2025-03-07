package com.example.flashcardsapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flashcardsapp.data.dao.CardDao
import com.example.flashcardsapp.data.dao.DeckDao
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.Deck

@Database(
    entities = [Deck::class, Card::class],
    version = 1,
    exportSchema = false
)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context): FlashCardDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlashCardDatabase::class.java, "flash_card_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}