// MainActivity.kt
package com.devofure.workoutschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import com.devofure.workoutschedule.data.WEEK
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.addexercise.AddExerciseScreen
import com.devofure.workoutschedule.ui.calendar.CalendarScreen
import com.devofure.workoutschedule.ui.calendar.CalendarViewModel
import com.devofure.workoutschedule.ui.createexercise.CreateExerciseScreen
import com.devofure.workoutschedule.ui.editworkout.EditWorkoutScreen
import com.devofure.workoutschedule.ui.main.MainScreen
import com.devofure.workoutschedule.ui.reorderexercise.ReorderExerciseScreen
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import com.devofure.workoutschedule.ui.workoutdetails.WorkoutDetailScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // User is not signed in; launch LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

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
        val dayNamingPreference by settingsViewModel.dayNamingPreference.collectAsState()

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
                        navigate = navigate,
                        dayNamingPreference = dayNamingPreference,
                    )
                }
                composable(Route.AddExercise.route) { backStackEntry ->
                    val dayIndexParam =
                        backStackEntry.arguments?.getString(Route.AddExercise.parameterName)?.toInt()
                            ?: return@composable
                    AddExerciseScreen(
                        dayIndex = dayIndexParam,
                        subTitle = WEEK[dayIndexParam].getFullName(dayNamingPreference),
                        workoutViewModel = workoutViewModel,
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
                composable(Route.CreateExercise.route) {
                    CreateExerciseScreen(
                        navigate = navigate,
                        onAddExercise = { workoutViewModel.addExercise(it) }
                    )
                }
                composable(Route.EditWorkout.route) { backStackEntry ->
                    val dayIndex =
                        backStackEntry.arguments?.getString(Route.EditWorkout.parameterName)?.toInt()
                            ?: return@composable
                    sharedViewModel.selectedWorkout.collectAsState().value?.let { workout ->
                        EditWorkoutScreen(
                            dayIndex = dayIndex,
                            navigate = navigate,
                            workout = workout,
                            updateWorkout = { day, exercises ->
                                workoutViewModel.updateWorkout(day, exercises)
                            },
                        )
                    }
                }
                composable(Route.ReorderExercise.route) { backStackEntry ->
                    val dayIndex =
                        backStackEntry.arguments?.getString(Route.ReorderExercise.parameterName)?.toInt()
                            ?: return@composable
                    val dayOfWeek = WEEK[dayIndex]
                    val workouts by workoutViewModel.workoutsForDay(dayOfWeek).collectAsState()
                    ReorderExerciseScreen(
                        navigate = navigate,
                        dayOfWeek = dayOfWeek,
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
