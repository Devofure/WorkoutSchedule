package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.ExerciseRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val exerciseRepository = ExerciseRepository(context)
    private val _workouts = MutableStateFlow<Map<String, List<Workout>>>(emptyMap())
    val workouts: StateFlow<Map<String, List<Workout>>> = _workouts
    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.exercises
    private val sharedPreferences = context.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    init {
        viewModelScope.launch {
            val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
            _isFirstLaunch.value = isFirstLaunch
            if (!isFirstLaunch) {
                loadUserSchedule()
            }
        }
    }

    fun generateSampleSchedule() {
        viewModelScope.launch {
            val sampleWorkouts = loadWorkoutsFromExercises()
            _workouts.value = sampleWorkouts
            saveUserSchedule(sampleWorkouts)
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            _isFirstLaunch.value = false
        }
    }

    private fun loadWorkoutsFromExercises(): Map<String, List<Workout>> {
        val sampleExercises = mapOf(
            "Mon" to listOf("3/4 Sit-Up", "90/90 Hamstring"),
            "Tue" to listOf("Ab Crunch Machine", "Ab Roller"),
            "Wed" to listOf("Adductor", "Adductor/Groin"),
            "Thu" to listOf("Advanced Kettlebell Windmill", "Air Bike"),
            "Fri" to listOf("All Fours Quad Stretch", "Alternate Hammer Curl"),
            "Sat" to listOf("Alternate Heel Touchers", "Alternate Incline Dumbbell Curl"),
            "Sun" to listOf("Alternate Leg Diagonal Bound", "Alternating Cable Shoulder Press")
        )

        val workoutsByDay = sampleExercises.mapValues { (day, exercises) ->
            exercises.mapNotNull { exerciseName ->
                exerciseRepository.getExerciseByName(exerciseName)?.let { exercise ->
                    Workout(id = exercise.id, exercise = exercise, sets = 3, reps = 10)
                }
            }
        }
        return workoutsByDay
    }

    private fun saveUserSchedule(workouts: Map<String, List<Workout>>) {
        val editor = sharedPreferences.edit()
        val workoutsJson = gson.toJson(workouts)
        editor.putString("userSchedule", workoutsJson)
        editor.apply()
    }

    private fun loadUserSchedule() {
        val workoutsJson = sharedPreferences.getString("userSchedule", null)
        if (!workoutsJson.isNullOrEmpty()) {
            val workoutType = object : TypeToken<Map<String, List<Workout>>>() {}.type
            val loadedWorkouts: Map<String, List<Workout>> = gson.fromJson(workoutsJson, workoutType)
            _workouts.value = loadedWorkouts
        }
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
        saveUserSchedule(_workouts.value)
    }

    fun onAllWorkoutsChecked(day: String, isChecked: Boolean) {
        _workouts.value = _workouts.value.mapValues { entry ->
            if (entry.key == day) {
                entry.value.map { it.copy(isDone = isChecked) }
            } else entry.value
        }
        saveUserSchedule(_workouts.value)
    }

    fun addWorkouts(day: String, newWorkouts: List<Workout>) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[day]?.toMutableList() ?: mutableListOf()
            existingWorkouts.addAll(newWorkouts)
            this[day] = existingWorkouts
        }
        saveUserSchedule(_workouts.value)
    }

    fun removeWorkout(day: String, workout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[day]?.toMutableList()
            existingWorkouts?.remove(workout)
            if (existingWorkouts != null) {
                this[day] = existingWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }
}
