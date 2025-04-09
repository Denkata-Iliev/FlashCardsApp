package com.example.flashcardsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.flashcardsapp.notification.ReminderNotification
import com.example.flashcardsapp.ui.theme.FlashCardsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // temporarily schedule notifications here. will be moved when settings screen is done
        val reminderNotification = ReminderNotification(applicationContext)
        reminderNotification.scheduleNotification()

        setContent {
            FlashCardsAppTheme {
                FlashCardApp()
            }
        }
    }
}