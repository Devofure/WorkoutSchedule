package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.ui.main.WorkoutApp
import com.devofure.workoutschedule.ui.main.WorkoutViewModel
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            val workoutViewModel: WorkoutViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()
            var showSettings by remember { mutableStateOf(false) }

            val currentTheme by settingsViewModel.theme.collectAsState() // Collect theme state

            MyWorkoutsTheme(themeType = currentTheme) { // Pass theme state to MyWorkoutsTheme
                if (isFirstLaunch) {
                    AskUserToGenerateSampleSchedule(workoutViewModel)
                } else {
                    if (showSettings) {
                        SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            onBack = { showSettings = false },
                            currentTheme = currentTheme, // Pass collected theme
                            onThemeChange = { settingsViewModel.setTheme(it) }
                        )
                    } else {
                        WorkoutApp(
                            workoutViewModel = workoutViewModel,
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
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

    @Composable
    fun AskUserToGenerateSampleSchedule(workoutViewModel: WorkoutViewModel) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Generate Sample Schedule") },
            text = { Text("Would you like to generate a sample workout schedule?") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutViewModel.generateSampleSchedule()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        workoutViewModel.declineSampleSchedule()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}
