package com.devofure.workoutschedule

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class WorkoutScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "workout_reminder_channel",
            "Workout Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for workout reminders"
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
