// MainActivity.kt
package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.devofure.workoutschedule.ui.WorkoutApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            WorkoutApp()
        }
    }

    private fun createNotificationChannel() {
        val name = "Workout Reminder Channel"
        val descriptionText = "Channel for workout reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("workout_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}