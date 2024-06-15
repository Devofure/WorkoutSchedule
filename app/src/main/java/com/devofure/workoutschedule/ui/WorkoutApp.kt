package com.devofure.workoutschedule.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.addexercise.AddExerciseScreen
import com.devofure.workoutschedule.ui.calendar.CalendarScreen
import com.devofure.workoutschedule.ui.calendar.CalendarViewModel
import com.devofure.workoutschedule.ui.editworkout.EditWorkoutScreen
import com.devofure.workoutschedule.ui.main.MainScreen
import com.devofure.workoutschedule.ui.reorderworkout.ReorderExerciseScreen
import com.devofure.workoutschedule.ui.settings.SettingsScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.settings.ThemeType
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import com.devofure.workoutschedule.ui.workoutdetails.WorkoutDetailScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun WorkoutApp() {
    val settingsViewModel: SettingsViewModel = viewModel()
    val workoutViewModel: WorkoutViewModel = viewModel()
    val currentTheme by settingsViewModel.theme.collectAsState()
    val systemUiController = rememberSystemUiController()
    val navController = rememberNavController()
    val navigate = Navigate(navController)
    val sharedViewModel: SharedViewModel = viewModel()
    val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()

    if (isFirstLaunch) {
        AskUserToGenerateSampleSchedule(workoutViewModel)
    }
    MyWorkoutsTheme(themeType = currentTheme) {

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
                CalendarScreen(
                    calendarViewModel = calendarViewModel,
                    settingsViewModel = settingsViewModel,
                    navigate = navigate
                )
            }
            composable(Route.Settings.route) {
                systemUiController.setSystemBarsColor(
                    color = MaterialTheme.colorScheme.background,
                    darkIcons = currentTheme != ThemeType.DARK
                )
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    currentTheme = currentTheme,
                    onThemeChange = { settingsViewModel.setTheme(it) }
                )
            }
            composable(Route.WorkoutDetail.route) {
                WorkoutDetailScreen(
                    sharedViewModel = sharedViewModel,
                    navigate = navigate,
                )
            }
            composable(Route.EditWorkout.route) { backStackEntry ->
                val dayFullName =
                    backStackEntry.arguments?.getString("dayFullName") ?: return@composable
                EditWorkoutScreen(
                    sharedViewModel = sharedViewModel,
                    workoutViewModel = workoutViewModel,
                    day = dayFullName,
                    navigate = navigate,
                )
            }
            // Add the new route for reordering exercises
            composable(Route.ReorderExercise.route) { backStackEntry ->
                val dayFullName =
                    backStackEntry.arguments?.getString(Route.ReorderExercise.parameterName)
                        ?: return@composable
                ReorderExerciseScreen(
                    day = dayFullName,
                    workoutViewModel = workoutViewModel,
                    navigate = navigate,
                )
            }
        }
    }
}

@Composable
fun AskUserToGenerateSampleSchedule(workoutViewModel: WorkoutViewModel) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Generate Sample Schedule") },
        text = { Text("Would you like to generate a sample workout schedule?") },
        confirmButton = {
            TextButton(
                onClick = {
                    workoutViewModel.generateSampleSchedule()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    workoutViewModel.declineSampleSchedule()
                }
            ) {
                Text("No")
            }
        }
    )
}
