// MainActivity.kt
package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.addexercise.AddExerciseScreen
import com.devofure.workoutschedule.ui.calendar.CalendarScreen
import com.devofure.workoutschedule.ui.calendar.CalendarViewModel
import com.devofure.workoutschedule.ui.editworkout.EditWorkoutScreen
import com.devofure.workoutschedule.ui.main.MainScreen
import com.devofure.workoutschedule.ui.reorderworkout.ReorderExerciseScreen
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import com.devofure.workoutschedule.ui.workoutdetails.WorkoutDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        createNotificationChannel()
        setContent {
            WorkoutApp()
        }
        enableEdgeToEdge()
    }

    private fun enableEdgeToEdge() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
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
    fun WorkoutApp() {
        val settingsViewModel: SettingsViewModel = viewModel()
        val workoutViewModel: WorkoutViewModel = viewModel()
        val currentTheme by settingsViewModel.theme.collectAsState()
        val currentPrimaryColor by settingsViewModel.primaryColor.collectAsState()
        val navController = rememberNavController()
        val navigate = Navigate(navController)
        val sharedViewModel: SharedViewModel = viewModel()
        val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()

        if (isFirstLaunch) {
            AskUserToGenerateSampleSchedule(
                generateSampleSchedule = { workoutViewModel.generateSampleSchedule() },
                declineSampleSchedule = { workoutViewModel.declineSampleSchedule() },
            )
        }
        MyWorkoutsTheme(themeType = currentTheme, primaryColor = currentPrimaryColor) {
            NavHost(navController = navController, startDestination = Route.Main.route) {
                composable(Route.Main.route) {
                    MainScreen(
                        workoutViewModel = workoutViewModel,
                        sharedViewModel = sharedViewModel,
                        navigate = navigate
                    )
                }
                composable(Route.AddExercise.route) { backStackEntry ->
                    val dayFullName =
                        backStackEntry.arguments?.getString(Route.AddExercise.parameterName)
                            ?: return@composable
                    val isLoading by workoutViewModel.isLoading.collectAsState()
                    val filteredExercises by workoutViewModel.filteredExercises.collectAsState()
                    val searchQuery by workoutViewModel.searchQuery.collectAsState()

                    AddExerciseScreen(
                        day = dayFullName,
                        searchQuery = searchQuery,
                        filteredExercises = filteredExercises,
                        isLoading = isLoading,
                        onSearchQueryChange = { workoutViewModel.searchQuery.value = it },
                        onAddWorkouts = { day, exercises ->
                            workoutViewModel.addWorkouts(day, exercises)
                        },
                        navigate = navigate,
                    )
                }
                composable(Route.Calendar.route) {
                    val calendarViewModel: CalendarViewModel = viewModel()
                    val firstDayOfWeek by settingsViewModel.firstDayOfWeek.collectAsState()
                    CalendarScreen(
                        calendarViewModel = calendarViewModel,
                        firstDayOfWeek = firstDayOfWeek,
                        navigate = navigate
                    )
                }
                composable(Route.Settings.route) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        navigate = navigate,
                        currentTheme = currentTheme,
                        onThemeChange = { theme ->
                            settingsViewModel.setTheme(theme)
                        },
                        onPrimaryColorChange = { color ->
                            settingsViewModel.setPrimaryColor(color)
                        }
                    )
                }
                composable(Route.WorkoutDetail.route) {
                    sharedViewModel.selectedWorkout.collectAsState().value?.let { workout ->
                        WorkoutDetailScreen(
                            workout = workout,
                            navigate = navigate,
                        )
                    }
                }
                composable(Route.EditWorkout.route) { backStackEntry ->
                    val dayFullName =
                        backStackEntry.arguments?.getString("dayFullName") ?: return@composable
                    sharedViewModel.selectedWorkout.collectAsState().value?.let { workout ->
                        EditWorkoutScreen(
                            day = dayFullName,
                            navigate = navigate,
                            workout = workout,
                            updateWorkout = { day, exercises ->
                                workoutViewModel.updateWorkout(day, exercises)
                            },
                        )
                    }
                }
                composable(Route.ReorderExercise.route) { backStackEntry ->
                    val dayFullName =
                        backStackEntry.arguments?.getString(Route.ReorderExercise.parameterName)
                            ?: return@composable
                    val workouts by workoutViewModel.workoutsForDay(dayFullName).collectAsState()
                    ReorderExerciseScreen(
                        navigate = navigate,
                        day = dayFullName,
                        workouts = workouts,
                        updateWorkoutOrder = workoutViewModel::updateWorkoutOrder
                    )
                }
            }
        }
    }

    @Composable
    fun AskUserToGenerateSampleSchedule(
        generateSampleSchedule: () -> Unit,
        declineSampleSchedule: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Generate Sample Schedule") },
            text = { Text("Would you like to generate a sample workout schedule?") },
            confirmButton = {
                TextButton(onClick = generateSampleSchedule) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = declineSampleSchedule) {
                    Text("No")
                }
            }
        )
    }
}
