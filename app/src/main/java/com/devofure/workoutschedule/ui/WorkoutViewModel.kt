// WorkoutViewModel.kt
package com.devofure.workoutschedule.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.WorkoutDataStoreManager
import com.devofure.workoutschedule.data.exercise.ExerciseRepository
import com.devofure.workoutschedule.data.exercise.toExercise
import com.devofure.workoutschedule.data.log.LogEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseRepository: ExerciseRepository
    val equipmentOptions: StateFlow<List<String>>
    val muscleOptions: StateFlow<List<String>>
    val categoryOptions: StateFlow<List<String>>

    private val _workouts = MutableStateFlow<Map<Int, List<Workout>>>(emptyMap())
    private val dataStoreManager = WorkoutDataStoreManager(application.applicationContext)
    private val database by lazy { AppDatabase.getDatabase(application) }
    private val logDao by lazy { database.logDao() }
    private val exerciseDao by lazy { database.exerciseDao() }
    private val gson = Gson()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    private var nextWorkoutId = 1

    private val _filteredExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    val filterQuery = MutableStateFlow("")
    val selectedFilters = MutableStateFlow<List<Pair<String, String>>>(emptyList())

    private val _deleteEvent = MutableStateFlow<Pair<Workout, Int>?>(null)
    val deleteEvent: StateFlow<Pair<Workout, Int>?> = _deleteEvent

    init {
        exerciseRepository =
            ExerciseRepository(application.applicationContext, exerciseDao, database)
        equipmentOptions = exerciseRepository.equipmentOptions
        muscleOptions = exerciseRepository.primaryMusclesOptions
        categoryOptions = exerciseRepository.categoryOptions

        viewModelScope.launch {
            dataStoreManager.isFirstLaunch.collectLatest { isFirstLaunchString ->
                _isFirstLaunch.value = isFirstLaunchString?.toBoolean() ?: true
                if (!_isFirstLaunch.value) {
                    loadUserSchedule()
                }
            }
        }
        setupFilter()
    }

    private fun setupFilter() {
        exerciseRepository.exercises.onEach {
            _isLoading.value = true
            _filteredExercises.value = filterExercises(filterQuery.value, selectedFilters.value)
            _isLoading.value = false
        }.launchIn(viewModelScope)

        filterQuery.onEach { query ->
            _isLoading.value = true
            _filteredExercises.value = filterExercises(query, selectedFilters.value)
            _isLoading.value = false
        }.launchIn(viewModelScope)

        selectedFilters.onEach { filters ->
            _isLoading.value = true
            _filteredExercises.value = filterExercises(filterQuery.value, filters)
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    private suspend fun filterExercises(
        query: String,
        filters: List<Pair<String, String>>
    ): List<Exercise> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        val ftsResults =
            if (trimmedQuery.isBlank()) exerciseRepository.exercises.value
            else exerciseRepository.searchExercises(trimmedQuery).map { it.toExercise() }

        return@withContext ftsResults.filter { exercise ->
            val matchesFilters = filters.isEmpty() || filters.any { (attribute, value) ->
                when (attribute) {
                    "Equipment" -> exercise.equipment == value
                    "Primary Muscles" -> exercise.primaryMuscles.contains(value)
                    "Secondary Muscles" -> exercise.secondaryMuscles.contains(value)
                    "Category" -> exercise.category == value
                    else -> false
                }
            }

            matchesFilters
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
                duration = workout.durationInSeconds,
            )
            logDao.insertLog(logEntity)
        }
    }

    fun generateSampleSchedule() {
        viewModelScope.launch {
            val sampleWorkouts = loadWorkoutsFromExercises()
            _workouts.value = sampleWorkouts.mapValues { (dayIndex, workouts) ->
                workouts.mapIndexed { index, workout ->
                    workout.copy(position = index)
                }
            }
            saveUserSchedule(sampleWorkouts)
            dataStoreManager.setIsFirstLaunch(false)
            _isFirstLaunch.value = false
        }
    }

    fun declineSampleSchedule() {
        viewModelScope.launch {
            dataStoreManager.setIsFirstLaunch(false)
            _isFirstLaunch.value = false
        }
    }

    private fun loadWorkoutsFromExercises(): Map<Int, List<Workout>> {
        var index = 0
        val workoutsByDay = SAMPLE_EXERCISE_SCHEDULE.mapValues { (_, exercises) ->
            exercises.mapNotNull { exerciseName ->
                exerciseRepository.getExerciseByName(exerciseName)?.let { exercise ->
                    Workout(
                        id = getNextWorkoutId(),
                        exercise = exercise,
                        position = index++,
                    )
                }
            }
        }
        return workoutsByDay
    }

    fun getCheckedWorkoutsForDay(dayIndex: Int): List<Workout> {
        return _workouts.value[dayIndex]?.filter { it.isDone } ?: emptyList()
    }

    private fun saveUserSchedule(workouts: Map<Int, List<Workout>>) {
        viewModelScope.launch {
            val workoutsJson = gson.toJson(workouts.mapValues { (_, workouts) ->
                workouts.sortedBy { it.position }
            })
            dataStoreManager.setUserSchedule(workoutsJson)
        }
    }

    private fun loadUserSchedule() {
        viewModelScope.launch {
            dataStoreManager.userSchedule.collectLatest { workoutsJson ->
                if (!workoutsJson.isNullOrEmpty()) {
                    val workoutType = object : TypeToken<Map<Int, List<Workout>>>() {}.type
                    val loadedWorkouts: Map<Int, List<Workout>> =
                        gson.fromJson<Map<Int, List<Workout>>>(workoutsJson, workoutType)
                            .mapValues { (_, workouts) ->
                                workouts.sortedBy { it.position }
                            }
                    _workouts.value = loadedWorkouts
                    nextWorkoutId = loadedWorkouts.values.flatten().maxOfOrNull { it.id + 1 } ?: 1
                }
            }
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
                        ),
                        position = existingWorkouts.size // Assign position based on current size
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
                // Update positions of remaining workouts
                this[dayIndex] = existingWorkouts.mapIndexed { index, existingWorkout ->
                    existingWorkout.copy(position = index)
                }
                _deleteEvent.value = workout to dayIndex
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
            this[dayIndex] = updatedList.mapIndexed { index, workout ->
                workout.copy(position = index)
            }
        }
        saveUserSchedule(_workouts.value)
    }

    fun saveNicknames(dayIndex: Int, dayName: String) {
        viewModelScope.launch {
            dataStoreManager.setNickname(dayIndex, dayName)
        }
    }

    fun getNicknameFlow(dayIndex: Int): StateFlow<String?> {
        return dataStoreManager.getNickname(dayIndex)
            .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    }

    fun addExercise(it: Exercise) {
        viewModelScope.launch {
            exerciseRepository.insertExercise(it)
        }
    }

    fun undoRemoveWorkout(workout: Workout, dayIndex: Int) {
        _workouts.value = _workouts.value.toMutableMap().apply {
            val existingWorkouts = this[dayIndex]?.toMutableList() ?: mutableListOf()
            existingWorkouts.add(workout.copy(position = existingWorkouts.size))
            this[dayIndex] = existingWorkouts
        }
        saveUserSchedule(_workouts.value)
        _deleteEvent.value = null
    }
}
