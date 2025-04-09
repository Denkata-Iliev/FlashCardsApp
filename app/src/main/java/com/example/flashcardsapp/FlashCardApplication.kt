package com.example.flashcardsapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.flashcardsapp.data.FlashCardAppContainer

class FlashCardApplication : Application() {
    lateinit var container: FlashCardAppContainer

    override fun onCreate() {
        super.onCreate()
        container = FlashCardAppContainer(this)

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "MemoraNotification"
        const val NOTIFICATION_CHANNEL_NAME = "MemoraNotification"
        const val OPEN_APP_REQUEST_CODE = 15
    }
}