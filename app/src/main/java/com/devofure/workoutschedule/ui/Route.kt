package com.devofure.workoutschedule.ui

import androidx.navigation.NavController

class Navigate(private val navController: NavController) {
    fun to(route: Route) {
        navController.navigate(route.route)
    }

    fun back() {
        navController.popBackStack()
    }
}

sealed class Route(val route: String) {
    data object Main : Route("main")
    data class AddExercise(val dayIndex: Int) :
        Route("add_exercise/${dayIndex}") {
        companion object {
            const val parameterName = "dayIndex"
            const val route = "add_exercise/{$parameterName}"
        }
    }

    data class EditWorkout(val dayIndex: Int) : Route("edit_workout/$dayIndex") {
        companion object {
            const val parameterName = "dayIndex"
            const val route = "edit_workout/{$parameterName}"
        }
    }

    data class ReorderExercise(val dayIndex: Int) :
        Route("reorder_exercise/$dayIndex") {
        companion object {
            const val parameterName = "dayIndex"
            const val route = "reorder_exercise/{$parameterName}"
        }
    }

    data object CreateExercise : Route("createExercise")
    data object Calendar : Route("calendar")
    data object Settings : Route("settings")
    data object WorkoutDetail : Route("workout_detail")
    data object FilterExercise : Route("filterExercise")
}
