package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.main.MainScreen
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
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
            NavHost(
                navController = navController,
                startDestination = if (isFirstLaunch) "welcome" else "main"
            ) {
                composable("welcome") {
                    WelcomeScreen(workoutViewModel) { navController.navigate("main") }
                }
                composable("main") {
                    MainScreen(
                        workoutViewModel = workoutViewModel,
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                composable("settings") {
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
}
