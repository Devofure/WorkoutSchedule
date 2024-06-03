// MainActivity.kt
package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.main.WorkoutApp
import com.devofure.workoutschedule.ui.main.WorkoutViewModel
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.settings.ThemeType
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {
        val workoutViewModel: WorkoutViewModel = viewModel()
        val settingsViewModel: SettingsViewModel = viewModel()
        val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()
        val currentTheme by settingsViewModel.theme.collectAsState()
        val navController = rememberNavController()
        val systemUiController = rememberSystemUiController()

        MyWorkoutsTheme(themeType = currentTheme) {
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    if (isFirstLaunch) {
                        systemUiController.setSystemBarsColor(
                            color = MaterialTheme.colors.background,
                            darkIcons = currentTheme != ThemeType.DARK
                        )
                        AskUserToGenerateSampleSchedule(workoutViewModel)
                    } else {
                        systemUiController.setSystemBarsColor(
                            color = MaterialTheme.colors.primary,
                            darkIcons = MaterialTheme.colors.isLight
                        )
                        WorkoutApp(
                            workoutViewModel = workoutViewModel,
                            onSettingsClick = { navController.navigate("settings") }
                        )
                    }
                }
                composable("settings") {
                    systemUiController.setSystemBarsColor(
                        color = MaterialTheme.colors.background,
                        darkIcons = currentTheme != ThemeType.DARK
                    )
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        onBack = { navController.popBackStack() },
                        currentTheme = currentTheme,
                        onThemeChange = { settingsViewModel.setTheme(it) }
                    )
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
