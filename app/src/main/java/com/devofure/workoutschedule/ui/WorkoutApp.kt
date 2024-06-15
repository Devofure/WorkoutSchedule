package com.devofure.workoutschedule.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.ui.addexercise.AddExerciseScreen
import com.devofure.workoutschedule.ui.calendar.CalendarScreen
import com.devofure.workoutschedule.ui.calendar.CalendarViewModel
import com.devofure.workoutschedule.ui.editworkout.EditWorkoutScreen
import com.devofure.workoutschedule.ui.main.MainScreen
import com.devofure.workoutschedule.ui.reorderworkout.ReorderExerciseScreen
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import com.devofure.workoutschedule.ui.workoutdetails.WorkoutDetailScreen

@Composable
fun WorkoutApp(
    searchQuery: String,
    filteredExercises: List<Exercise>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onAddWorkouts: (String, List<Exercise>) -> Unit,
    onSettingsClick: () -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    sharedViewModel: SharedViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                navController,
                workoutViewModel,
                sharedViewModel,
                onSettingsClick
            )
        }
        composable("add_exercise/{dayFullName}") { backStackEntry ->
            val dayFullName = backStackEntry.arguments?.getString("dayFullName") ?: return@composable
            AddExerciseScreen(
                navController = navController,
                day = dayFullName,
                searchQuery = searchQuery,
                filteredExercises = filteredExercises,
                isLoading = isLoading,
                onSearchQueryChange = onSearchQueryChange,
                onAddWorkouts = onAddWorkouts
            )
        }
        composable("calendar") {
            CalendarScreen(
                navController = navController,
                calendarViewModel = calendarViewModel,
                settingsViewModel = settingsViewModel
            )
        }
        composable("workout_detail") { WorkoutDetailScreen(navController, sharedViewModel) }
        composable("edit_workout/{dayFullName}") { backStackEntry ->
            val dayFullName = backStackEntry.arguments?.getString("dayFullName") ?: return@composable
            EditWorkoutScreen(navController, sharedViewModel, workoutViewModel, dayFullName)
        }
        // Add the new route for reordering exercises
        composable("reorder_exercise/{dayFullName}") { backStackEntry ->
            val dayFullName = backStackEntry.arguments?.getString("dayFullName") ?: return@composable
            ReorderExerciseScreen(
                navController = navController,
                day = dayFullName,
                workoutViewModel = workoutViewModel
            )
        }
    }
}
