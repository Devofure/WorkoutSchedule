package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.ExerciseRepository
import com.devofure.workoutschedule.data.LogEntity
import com.devofure.workoutschedule.data.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
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
        initializeState()
        observeSearchQuery()
    }

    private fun initializeState() {
        viewModelScope.launch {
            _isFirstLaunch.value = sharedPreferences.getBoolean("isFirstLaunch", true)
            if (!_isFirstLaunch.value) loadUserSchedule()
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery.collect { query ->
                searchExercises(query)
            }
        }
    }

    fun getLogsForDate(date: Date): Flow<List<LogEntity>> {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        return logDao.getLogsForDate(formattedDate)
    }

    fun logWorkout(workout: Workout, date: Date) {
        viewModelScope.launch {
            val logEntity = LogEntity(
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
                workoutId = workout.id,
                dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
            )
            logDao.insertLog(logEntity)
        }
    }

    fun generateSampleSchedule() {
        viewModelScope.launch {
            val sampleWorkouts = loadSampleWorkouts()
            _workouts.value = sampleWorkouts
            saveUserSchedule(sampleWorkouts)
            setFirstLaunchCompleted()
        }
    }

    fun declineSampleSchedule() {
        viewModelScope.launch {
            setFirstLaunchCompleted()
        }
    }

    private fun loadSampleWorkouts(): Map<String, List<Workout>> {
        val sampleExercises = mapOf(
            "Monday" to listOf("3/4 Sit-Up", "90/90 Hamstring"),
            "Tuesday" to listOf("Ab Crunch Machine", "Ab Roller"),
            "Wednesday" to listOf("Adductor", "Adductor/Groin"),
            "Thursday" to listOf("Advanced Kettlebell Windmill", "Air Bike"),
            "Friday" to listOf("All Fours Quad Stretch", "Alternate Hammer Curl"),
            "Saturday" to listOf("Alternate Heel Touchers", "Alternate Incline Dumbbell Curl"),
            "Sunday" to listOf("Alternate Leg Diagonal Bound", "Alternating Cable Shoulder Press")
        )

        return sampleExercises.mapValues { (_, exercises) ->
            exercises.mapNotNull { exerciseName ->
                exerciseRepository.getExerciseByName(exerciseName)?.let { exercise ->
                    Workout(id = getNextWorkoutId(), exercise = exercise)
                }
            }
        }
    }

    private fun saveUserSchedule(workouts: Map<String, List<Workout>>) {
        val workoutsJson = gson.toJson(workouts)
        sharedPreferences.edit().putString("userSchedule", workoutsJson).apply()
    }

    private fun loadUserSchedule() {
        sharedPreferences.getString("userSchedule", null)?.let { workoutsJson ->
            val workoutType = object : TypeToken<Map<String, List<Workout>>>() {}.type
            val loadedWorkouts: Map<String, List<Workout>> =
                gson.fromJson(workoutsJson, workoutType)
            _workouts.value = loadedWorkouts
            nextWorkoutId = loadedWorkouts.values.flatten().maxOfOrNull { it.id }?.plus(1) ?: 1
        }
    }

    fun workoutsForDay(day: String): StateFlow<List<Workout>> {
        return workouts.map { it[normalizeDayKey(day)] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    private fun normalizeDayKey(day: String): String {
        return day.substringBefore(" (")
    }

    fun onWorkoutChecked(day: String, workoutId: Int, isChecked: Boolean) {
        updateWorkoutState(day) { workouts ->
            workouts.map { if (it.id == workoutId) it.copy(isDone = isChecked) else it }
        }
    }

    fun onAllWorkoutsChecked(day: String, isChecked: Boolean) {
        updateWorkoutState(day) { workouts ->
            workouts.map { it.copy(isDone = isChecked) }
        }
    }

    fun addWorkouts(day: String, exercises: List<Exercise>) {
        updateWorkoutState(day) { workouts ->
            workouts + exercises.map { Workout(id = getNextWorkoutId(), exercise = it) }
        }
    }

    fun removeWorkout(day: String, workout: Workout) {
        updateWorkoutState(day) { workouts ->
            workouts.filterNot { it.id == workout.id }
        }
    }

    fun updateWorkout(day: String, updatedWorkout: Workout) {
        updateWorkoutState(day) { workouts ->
            workouts.map { if (it.id == updatedWorkout.id) updatedWorkout else it }
        }
    }

    private fun updateWorkoutState(day: String, update: (List<Workout>) -> List<Workout>) {
        val normalizedDay = normalizeDayKey(day)
        _workouts.value = _workouts.value.toMutableMap().apply {
            this[normalizedDay] = update(this[normalizedDay] ?: emptyList())
        }
        saveUserSchedule(_workouts.value)
    }

    private fun getNextWorkoutId(): Int {
        return nextWorkoutId++
    }

    private suspend fun searchExercises(query: String) {
        _isLoading.value = true
        _filteredExercises.value = exerciseRepository.searchExercises(query)
        _isLoading.value = false
    }

    private fun setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        _isFirstLaunch.value = false
    }
}
