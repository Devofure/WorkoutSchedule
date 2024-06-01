package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.Workout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val _workouts = MutableStateFlow<Map<String, List<Workout>>>(emptyMap())
    val workouts: StateFlow<Map<String, List<Workout>>> = _workouts

    init {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
            val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

            if (isFirstLaunch) {
                loadWorkoutsFromJson()
                sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            } else {
                loadWorkoutsFromPreferences()
            }
        }
    }

    private fun loadWorkoutsFromJson() {
        val inputStream = context.assets.open("exercises.json")
        val reader = InputStreamReader(inputStream)
        val jsonObject = Gson().fromJson(reader, JsonObject::class.java)
        val workoutType = object : TypeToken<List<Workout>>() {}.type
        val workoutsList: List<Workout> = Gson().fromJson(jsonObject.getAsJsonArray("exercises"), workoutType)

        val workoutsByDay = workoutsList.groupBy { workout ->
            when (workout.name) {
                "Bench Press", "Incline Dumbbell Press", "Chest Flyes", "Tricep Dips", "Tricep Pushdowns" -> "Mon"
                "Pull-Ups", "Bent Over Rows", "Lat Pulldowns", "Bicep Curls", "Hammer Curls" -> "Tue"
                "Squats", "Leg Press", "Leg Curls", "Leg Extensions", "Calf Raises" -> "Wed"
                "Shoulder Press", "Lateral Raises", "Front Raises", "Reverse Flyes", "Plank", "Russian Twists" -> "Thu"
                "Incline Bench Press", "Dumbbell Flyes", "T-Bar Rows", "Single-Arm Rows" -> "Fri"
                "Deadlifts", "Lunges", "Glute Bridges", "Leg Raises", "Bicycle Crunches" -> "Sat"
                "Light Cardio", "Stretching or Yoga" -> "Sun"
                else -> "Other"
            }
        }

        _workouts.value = workoutsByDay
        saveWorkoutsToPreferences(workoutsByDay)
    }

    private fun loadWorkoutsFromPreferences() {
        val sharedPreferences = context.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
        val workoutsJson = sharedPreferences.getString("workouts", null)

        if (workoutsJson != null) {
            val workoutType = object : TypeToken<Map<String, List<Workout>>>() {}.type
            val workoutsByDay: Map<String, List<Workout>> = Gson().fromJson(workoutsJson, workoutType)
            _workouts.value = workoutsByDay
        } else {
            _workouts.value = loadSampleWorkouts()
        }
    }

    private fun saveWorkoutsToPreferences(workouts: Map<String, List<Workout>>) {
        val sharedPreferences = context.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val workoutsJson = Gson().toJson(workouts)
        editor.putString("workouts", workoutsJson)
        editor.apply()
    }

    private fun loadSampleWorkouts(): Map<String, List<Workout>> {
        return sampleWorkouts
    }

    fun workoutsForDay(day: String): StateFlow<List<Workout>> {
        return workouts
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
        saveWorkoutsToPreferences(_workouts.value)
    }

    fun onAllWorkoutsChecked(day: String, isChecked: Boolean) {
        _workouts.value = _workouts.value.mapValues { entry ->
            if (entry.key == day) {
                entry.value.map { it.copy(isDone = isChecked) }
            } else entry.value
        }
        saveWorkoutsToPreferences(_workouts.value)
    }

    fun addWorkout(day: String, workout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            this[day] = this[day]?.toMutableList()?.apply { add(workout) } ?: listOf(workout)
        }
        saveWorkoutsToPreferences(_workouts.value)
    }

    fun removeWorkout(day: String, workout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            this[day] = this[day]?.filter { it.id != workout.id } ?: emptyList()
        }
        saveWorkoutsToPreferences(_workouts.value)
    }
}

val sampleWorkouts = mapOf(
    "Mon" to listOf(
        Workout(id = 1, name = "Bench Press", description = "4 sets of 8-12 reps"),
        Workout(id = 2, name = "Incline Dumbbell Press", description = "3 sets of 8-12 reps"),
        Workout(id = 3, name = "Chest Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 4, name = "Tricep Dips", description = "3 sets of 8-12 reps"),
        Workout(id = 5, name = "Tricep Pushdowns", description = "3 sets of 10-15 reps")
    ),
    "Tue" to listOf(
        Workout(id = 6, name = "Pull-Ups", description = "4 sets of 6-10 reps"),
        Workout(id = 7, name = "Bent Over Rows", description = "4 sets of 8-12 reps"),
        Workout(id = 8, name = "Lat Pulldowns", description = "3 sets of 10-15 reps"),
        Workout(id = 9, name = "Bicep Curls", description = "3 sets of 10-15 reps"),
        Workout(id = 10, name = "Hammer Curls", description = "3 sets of 10-15 reps")
    ),
    "Wed" to listOf(
        Workout(id = 11, name = "Squats", description = "4 sets of 8-12 reps"),
        Workout(id = 12, name = "Leg Press", description = "3 sets of 10-15 reps"),
        Workout(id = 13, name = "Leg Curls", description = "3 sets of 10-15 reps"),
        Workout(id = 14, name = "Leg Extensions", description = "3 sets of 10-15 reps"),
        Workout(id = 15, name = "Calf Raises", description = "4 sets of 15-20 reps")
    ),
    "Thu" to listOf(
        Workout(id = 16, name = "Shoulder Press", description = "4 sets of 8-12 reps"),
        Workout(id = 17, name = "Lateral Raises", description = "3 sets of 10-15 reps"),
        Workout(id = 18, name = "Front Raises", description = "3 sets of 10-15 reps"),
        Workout(id = 19, name = "Reverse Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 20, name = "Plank", description = "3 sets of 1 minute"),
        Workout(id = 21, name = "Russian Twists", description = "3 sets of 20 reps")
    ),
    "Fri" to listOf(
        Workout(id = 22, name = "Incline Bench Press", description = "4 sets of 8-12 reps"),
        Workout(id = 23, name = "Dumbbell Flyes", description = "3 sets of 10-15 reps"),
        Workout(id = 24, name = "T-Bar Rows", description = "4 sets of 8-12 reps"),
        Workout(id = 25, name = "Single-Arm Rows", description = "3 sets of 10-15 reps")
    ),
    "Sat" to listOf(
        Workout(id = 26, name = "Deadlifts", description = "4 sets of 8-12 reps"),
        Workout(id = 27, name = "Lunges", description = "3 sets of 10-15 reps per leg"),
        Workout(id = 28, name = "Glute Bridges", description = "3 sets of 10-15 reps"),
        Workout(id = 29, name = "Leg Raises", description = "3 sets of 15 reps"),
        Workout(id = 30, name = "Bicycle Crunches", description = "3 sets of 20 reps")
    ),
    "Sun" to listOf(
        Workout(id = 31, name = "Light Cardio", description = "30 minutes (walking, jogging, cycling)"),
        Workout(id = 32, name = "Stretching or Yoga", description = "30 minutes")
    )
)
