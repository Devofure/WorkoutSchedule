package com.devofure.workoutschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import com.devofure.workoutschedule.data.Workout

class WorkoutViewModel : ViewModel() {
    private val _workouts = MutableStateFlow(sampleWorkouts)
    val workouts: StateFlow<Map<String, List<Workout>>> get() = _workouts

    fun workoutsForDay(day: String): StateFlow<List<Workout>> {
        return _workouts
            .map { it[day] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun onWorkoutChecked(day: String, workout: Workout, isChecked: Boolean) {
        _workouts.value = _workouts.value.mapValues { entry ->
            if (entry.key == day) {
                entry.value.map {
                    if (it.id == workout.id) it.copy(isDone = isChecked) else it
                }
            } else entry.value
        }
    }

    fun onAllWorkoutsChecked(day: String, isChecked: Boolean) {
        _workouts.value = _workouts.value.mapValues { entry ->
            if (entry.key == day) {
                entry.value.map { it.copy(isDone = isChecked) }
            } else entry.value
        }
    }
}

val sampleWorkouts = mapOf(
    "Monday" to listOf(
        Workout(id = 1, name = "Bench Press", description = "4 sets of 8-12 reps"),
        Workout(id = 2, name = "Incline Dumbbell Press", description = "3 sets of 8-12 reps"),
        Workout(id = 3, name = "Chest Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 4, name = "Tricep Dips", description = "3 sets of 8-12 reps"),
        Workout(id = 5, name = "Tricep Pushdowns", description = "3 sets of 10-15 reps")
    ),
    "Tuesday" to listOf(
        Workout(id = 6, name = "Pull-Ups", description = "4 sets of 6-10 reps"),
        Workout(id = 7, name = "Bent Over Rows", description = "4 sets of 8-12 reps"),
        Workout(id = 8, name = "Lat Pulldowns", description = "3 sets of 10-15 reps"),
        Workout(id = 9, name = "Bicep Curls", description = "3 sets of 10-15 reps"),
        Workout(id = 10, name = "Hammer Curls", description = "3 sets of 10-15 reps")
    ),
    "Wednesday" to listOf(
        Workout(id = 11, name = "Squats", description = "4 sets of 8-12 reps"),
        Workout(id = 12, name = "Leg Press", description = "3 sets of 10-15 reps"),
        Workout(id = 13, name = "Leg Curls", description = "3 sets of 10-15 reps"),
        Workout(id = 14, name = "Leg Extensions", description = "3 sets of 10-15 reps"),
        Workout(id = 15, name = "Calf Raises", description = "4 sets of 15-20 reps")
    ),
    "Thursday" to listOf(
        Workout(id = 16, name = "Shoulder Press", description = "4 sets of 8-12 reps"),
        Workout(id = 17, name = "Lateral Raises", description = "3 sets of 10-15 reps"),
        Workout(id = 18, name = "Front Raises", description = "3 sets of 10-15 reps"),
        Workout(id = 19, name = "Reverse Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 20, name = "Plank", description = "3 sets of 1 minute"),
        Workout(id = 21, name = "Russian Twists", description = "3 sets of 20 reps")
    ),
    "Friday" to listOf(
        Workout(id = 22, name = "Incline Bench Press", description = "4 sets of 8-12 reps"),
        Workout(id = 23, name = "Dumbbell Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 24, name = "T-Bar Rows", description = "4 sets of 8-12 reps"),
        Workout(id = 25, name = "Single-Arm Rows", description = "3 sets of 10-15 reps")
    ),
    "Saturday" to listOf(
        Workout(id = 26, name = "Deadlifts", description = "4 sets of 8-12 reps"),
        Workout(id = 27, name = "Lunges", description = "3 sets of 10-15 reps per leg"),
        Workout(id = 28, name = "Glute Bridges", description = "3 sets of 10-15 reps"),
        Workout(id = 29, name = "Leg Raises", description = "3 sets of 15 reps"),
        Workout(id = 30, name = "Bicycle Crunches", description = "3 sets of 20 reps")
    ),
    "Sunday" to listOf(
        Workout(id = 31, name = "Light Cardio", description = "30 minutes (walking, jogging, cycling)"),
        Workout(id = 32, name = "Stretching or Yoga", description = "30 minutes")
    )
)
