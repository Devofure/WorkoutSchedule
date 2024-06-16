package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.ExerciseRepository
import com.devofure.workoutschedule.data.LogEntity
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseRepository = ExerciseRepository(application.applicationContext)
    private val _workouts = MutableStateFlow<Map<String, List<Workout>>>(emptyMap())
    val workouts: StateFlow<Map<String, List<Workout>>> = _workouts
    private val sharedPreferences =
        application.applicationContext.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
    private val logDao by lazy { AppDatabase.getDatabase(application).logDao() }
    private val gson = Gson()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    private var nextWorkoutId = 1

    private val _filteredExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
            _isFirstLaunch.value = isFirstLaunch
            if (!isFirstLaunch) {
                loadUserSchedule()
            }
        }

        viewModelScope.launch {
            searchQuery.collect { query ->
                searchExercises(query)
            }
        }
    }

    fun logWorkout(workout: Workout, date: LocalDate) {
        viewModelScope.launch {
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val logEntity = LogEntity(
                date = date.format(dateFormat),
                workoutId = workout.id,
                exerciseName = workout.exercise.name,
                dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())),
                repsList = workout.repsList,
                duration = workout.duration,
            )
            logDao.insertLog(logEntity)
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
            "Monday" to listOf(
                "Dumbbell Bench Press",
                "Close-Grip Push-Up off of a Dumbbell",
                "Dumbbell Flyes",
                "Cable Chest Press",
                "Triceps Pushdown - Rope Attachment"
            ),
            "Tuesday" to listOf(
                "Bent Over Barbell Row",
                "One-Arm Dumbbell Row",
                "Alternating Kettlebell Row",
                "Hammer Curls",
                "Dumbbell Bicep Curl"
            ),
            "Wednesday" to listOf(
                "Barbell Squat",
                "Dumbbell Lunges",
                "Goblet Squat",
                "Hamstring Stretch",
                "Calf Raises - With Bands"
            ),
            "Thursday" to listOf(
                "Arnold Dumbbell Press",
                "Dumbbell Shoulder Press",
                "Side Lateral Raise",
                "3/4 Sit-Up",
                "Cable Russian Twists"
            ),
            "Friday" to listOf(
                "Deadlift",
                "Kettlebell Dead Clean",
                "Bear Crawl Sled Drags",
                "Child's Pose",
                "Dynamic Chest Stretch"
            ),
            "Saturday" to listOf(
                "Mountain Climbers",
                "Air Bike",
                "Plank",
                "Russian Twist",
                "Flutter Kicks"
            ),
            "Sunday" to listOf(
                "Cat Stretch",
                "Quad Stretch",
                "Hamstring Stretch",
                "Butterfly",
                "Child's Pose"
            )
        )

        val workoutsByDay = sampleExercises.mapValues { (_, exercises) ->
            exercises.mapNotNull { exerciseName ->
                exerciseRepository.getExerciseByName(exerciseName)?.let { exercise ->
                    Workout(
                        id = getNextWorkoutId(),
                        exercise = exercise,
                    )
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

    fun loadUserSchedule() {
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
        val normalizedDay = normalizeDayKey(day)
        return workouts
            .map { it[normalizedDay] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    private fun normalizeDayKey(day: String): String {
        return day.substringBefore(" (")
    }

    fun onWorkoutChecked(day: String, workoutId: Int, isChecked: Boolean) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[normalizedDay]?.map {
                if (it.id == workoutId) it.copy(isDone = isChecked) else it
            }
            if (updatedWorkouts != null) {
                this[normalizedDay] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun onAllWorkoutsChecked(day: String) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.mapValues { entry ->
            if (normalizeDayKey(entry.key) == normalizedDay) {
                entry.value.map { it.copy(isDone = true) }
            } else entry.value
        }
        saveUserSchedule(_workouts.value)
    }

    fun addWorkouts(day: String, exercises: List<Exercise>) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[normalizedDay]?.toMutableList() ?: mutableListOf()
            exercises.forEach { exercise ->
                existingWorkouts.add(
                    Workout(
                        id = getNextWorkoutId(),
                        exercise = exercise,
                        repsList = listOf(
                            SetDetails(reps = 10, weight = null, duration = null),
                            SetDetails(reps = 10, weight = null, duration = null)
                        )
                    )
                )
            }
            this[normalizedDay] = existingWorkouts
        }
        saveUserSchedule(_workouts.value)
    }

    fun removeWorkout(day: String, workout: Workout) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[normalizedDay]?.toMutableList()
            existingWorkouts?.remove(workout)
            if (existingWorkouts != null) {
                this[normalizedDay] = existingWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun updateWorkout(day: String, updatedWorkout: Workout) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[normalizedDay]?.map {
                if (it.id == updatedWorkout.id) updatedWorkout else it
            }
            if (updatedWorkouts != null) {
                this[normalizedDay] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    private fun getNextWorkoutId(): Int {
        return nextWorkoutId++
    }

    private fun searchExercises(query: String) {
        _isLoading.value = true
        _filteredExercises.value = exerciseRepository.searchExercises(query)
        _isLoading.value = false
    }

    fun updateWorkoutOrder(day: String, updatedList: List<Workout>) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            this[normalizedDay] = updatedList
        }
        saveUserSchedule(_workouts.value)
    }
}