package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.ExerciseRepository
import com.devofure.workoutschedule.data.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseRepository = ExerciseRepository(application.applicationContext)
    private val _workouts = MutableStateFlow<Map<String, List<Workout>>>(emptyMap())
    private val workouts: StateFlow<Map<String, List<Workout>>> = _workouts
    private val sharedPreferences =
        application.applicationContext.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    private var nextWorkoutId = 1

    private val _filteredExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

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

    fun declineSampleSchedule() {
        viewModelScope.launch {
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
                    Workout(id = getNextWorkoutId(), exercise = exercise)
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
            val loadedWorkouts: Map<String, List<Workout>> =
                gson.fromJson(workoutsJson, workoutType)
            _workouts.value = loadedWorkouts
            nextWorkoutId = loadedWorkouts.values.flatten().maxOfOrNull { it.id + 1 } ?: 1
        }
    }

    fun workoutsForDay(day: String): StateFlow<List<Workout>> {
        return workouts
            .map { it[day] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun onWorkoutChecked(day: String, workoutId: Int, isChecked: Boolean) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[day]?.map {
                if (it.id == workoutId) it.copy(isDone = isChecked) else it
            }
            if (updatedWorkouts != null) {
                this[day] = updatedWorkouts
            }
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

    fun addWorkouts(day: String, exercises: List<Exercise>) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[day]?.toMutableList() ?: mutableListOf()
            exercises.forEach { exercise ->
                existingWorkouts.add(
                    Workout(
                        id = getNextWorkoutId(),
                        exercise = exercise
                    )
                )
            }
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

    fun updateWorkout(day: String, updatedWorkout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[day]?.map {
                if (it.id == updatedWorkout.id) updatedWorkout else it
            }
            if (updatedWorkouts != null) {
                this[day] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    private fun getNextWorkoutId(): Int {
        return nextWorkoutId++
    }

    fun searchExercises(query: String) {
        viewModelScope.launch {
            _filteredExercises.value = exerciseRepository.searchExercises(query)
        }
    }
}
