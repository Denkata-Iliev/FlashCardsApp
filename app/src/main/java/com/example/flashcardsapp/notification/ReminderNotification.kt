package com.example.flashcardsapp.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import com.example.flashcardsapp.FlashCardApplication
import com.example.flashcardsapp.MainActivity
import com.example.flashcardsapp.R
import java.time.LocalTime
import java.util.Calendar

class ReminderNotification(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showNotification() {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            FlashCardApplication.OPEN_APP_REQUEST_CODE,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, FlashCardApplication.NOTIFICATION_CHANNEL_ID)
            .setContentText(context.getString(R.string.app_name))
            .setContentTitle("Daily Reminder")
            .setSmallIcon(R.drawable.app_icon)
            .setLargeIcon(Icon.createWithResource(context, R.drawable.app_icon))
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Don't forget to review your cards!")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun scheduleNotificationDefaultTime() {
        val pendingIntent = NotificationUtils.getNotificationPendingIntent(context)

        val alarmManager = NotificationUtils.getAlarmManager(context)

        val defaultTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            defaultTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleNotification(time: LocalTime) {
        val pendingIntent = NotificationUtils.getNotificationPendingIntent(context)

        val alarmManager = NotificationUtils.getAlarmManager(context)

        val scheduleTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            scheduleTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    companion object {
        private const val NOTIFICATION_ID = 10
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val scheduleNotificationService = context?.let { ReminderNotification(it) }
        scheduleNotificationService?.showNotification()
    }
}