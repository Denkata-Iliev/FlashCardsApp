package com.example.flashcardsapp

import android.app.Application
import com.example.flashcardsapp.data.FlashCardAppContainer

class FlashCardApplication : Application() {
    lateinit var container: FlashCardAppContainer

    override fun onCreate() {
        super.onCreate()
        container = FlashCardAppContainer(this)
    }
}