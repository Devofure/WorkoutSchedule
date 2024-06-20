package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.DayOfWeek
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseRepository = ExerciseRepository(application.applicationContext)
    val equipmentOptions: StateFlow<List<String>> = exerciseRepository.equipmentOptions
    val primaryMusclesOptions: StateFlow<List<String>> = exerciseRepository.primaryMusclesOptions
    val secondaryMusclesOptions: StateFlow<List<String>> =
        exerciseRepository.secondaryMusclesOptions
    val categoryOptions: StateFlow<List<String>> = exerciseRepository.categoryOptions

    private val _workouts = MutableStateFlow<Map<Int, List<Workout>>>(emptyMap())
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
    val filterQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
            _isFirstLaunch.value = isFirstLaunch
            if (!isFirstLaunch) {
                loadUserSchedule()
            }
        }
        setupFilter()
    }

    private fun setupFilter() {
        exerciseRepository.exercises.onEach {
            _isLoading.value = true
            _filteredExercises.value = exerciseRepository.filterExercises(filterQuery.value)
            _isLoading.value = false
        }.launchIn(viewModelScope)

        filterQuery.onEach { query ->
            _isLoading.value = true
            _filteredExercises.value = exerciseRepository.filterExercises(query)
            _isLoading.value = false
        }.launchIn(viewModelScope)
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

    private fun loadWorkoutsFromExercises(): Map<Int, List<Workout>> {
        val workoutsByDay = SAMPLE_EXERCISE_SCHEDULE.mapValues { (_, exercises) ->
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

    private fun saveUserSchedule(workouts: Map<Int, List<Workout>>) {
        val editor = sharedPreferences.edit()
        val workoutsJson = gson.toJson(workouts)
        editor.putString("userSchedule", workoutsJson)
        editor.apply()
    }

    private fun loadUserSchedule() {
        val workoutsJson = sharedPreferences.getString("userSchedule", null)
        if (!workoutsJson.isNullOrEmpty()) {
            val workoutType = object : TypeToken<Map<Int, List<Workout>>>() {}.type
            val loadedWorkouts: Map<Int, List<Workout>> =
                gson.fromJson(workoutsJson, workoutType)
            _workouts.value = loadedWorkouts
            nextWorkoutId = loadedWorkouts.values.flatten().maxOfOrNull { it.id + 1 } ?: 1
        }
    }

    fun workoutsForDay(dayOfWeek: DayOfWeek): StateFlow<List<Workout>> {
        return _workouts
            .map { it[dayOfWeek.dayIndex] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun onWorkoutChecked(dayIndex: Int, workoutId: Int, isChecked: Boolean) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[dayIndex]?.map { workout ->
                if (workout.id == workoutId) workout.copy(isDone = isChecked) else workout
            }
            if (updatedWorkouts != null) {
                this[dayIndex] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun onAllWorkoutsChecked(dayIndex: Int) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[dayIndex]?.map { workout ->
                workout.copy(isDone = true)
            }
            if (updatedWorkouts != null) {
                this[dayIndex] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun addWorkouts(dayIndex: Int, exercises: List<Exercise>) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[dayIndex]?.toMutableList() ?: mutableListOf()
            exercises.forEach { exercise ->
                existingWorkouts.add(
                    Workout(
                        id = getNextWorkoutId(),
                        exercise = exercise,
                        repsList = listOf(
                            SetDetails(reps = 1),
                            SetDetails(reps = 1)
                        )
                    )
                )
            }
            this[dayIndex] = existingWorkouts
        }
        saveUserSchedule(_workouts.value)
    }

    fun removeWorkout(dayIndex: Int, workout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[dayIndex]?.toMutableList()
            existingWorkouts?.remove(workout)
            if (existingWorkouts != null) {
                this[dayIndex] = existingWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun updateWorkout(dayIndex: Int, updatedWorkout: Workout) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val updatedWorkouts = this[dayIndex]?.map {
                if (it.id == updatedWorkout.id) updatedWorkout else it
            }
            if (updatedWorkouts != null) {
                this[dayIndex] = updatedWorkouts
            }
        }
        saveUserSchedule(_workouts.value)
    }

    private fun getNextWorkoutId(): Int {
        return nextWorkoutId++
    }

    fun updateWorkoutOrder(dayIndex: Int, updatedList: List<Workout>) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            this[dayIndex] = updatedList
        }
        saveUserSchedule(_workouts.value)
    }

    fun saveNicknames(dayIndex: Int, dayName: String) {
        sharedPreferences.edit().putString("nicknames_$dayIndex", dayName).apply()
    }

    fun getNickname(dayIndex: Int): String {
        return sharedPreferences.getString("nicknames_$dayIndex", "") ?: ""
    }
}
