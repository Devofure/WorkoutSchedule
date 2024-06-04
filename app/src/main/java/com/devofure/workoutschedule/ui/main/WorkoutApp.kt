@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.addexercise.AddExerciseScreen
import com.devofure.workoutschedule.ui.editworkout.EditWorkoutScreen
import com.devofure.workoutschedule.ui.workoutdetails.WorkoutDetailScreen

@Composable
fun WorkoutApp(
    workoutViewModel: WorkoutViewModel = viewModel(),
    sharedViewModel: SharedViewModel = viewModel(),
    onSettingsClick: () -> Unit
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
            val dayFullName =
                backStackEntry.arguments?.getString("dayFullName") ?: return@composable
            AddExerciseScreen(navController, sharedViewModel, workoutViewModel, dayFullName)
        }
        composable("workout_detail") { WorkoutDetailScreen(navController, sharedViewModel) }
        composable("edit_workout/{dayFullName}") { backStackEntry ->
            val dayFullName =
                backStackEntry.arguments?.getString("dayFullName") ?: return@composable
            EditWorkoutScreen(navController, sharedViewModel, workoutViewModel, dayFullName)
        }
    }
}