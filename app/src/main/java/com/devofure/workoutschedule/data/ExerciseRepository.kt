package com.devofure.workoutschedule.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

data class Exercise(
    val name: String,
    val force: String,
    val level: String,
    val mechanic: String?,
    val equipment: String?,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val category: String,
)

class ExerciseRepository(
    private val context: Context,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    // Preloaded data
    private val _equipmentOptions = MutableStateFlow<List<String>>(emptyList())
    val equipmentOptions: StateFlow<List<String>> = _equipmentOptions.asStateFlow()

    private val _primaryMusclesOptions = MutableStateFlow<List<String>>(emptyList())
    val primaryMusclesOptions: StateFlow<List<String>> = _primaryMusclesOptions.asStateFlow()

    private val _secondaryMusclesOptions = MutableStateFlow<List<String>>(emptyList())
    val secondaryMusclesOptions: StateFlow<List<String>> = _secondaryMusclesOptions.asStateFlow()

    private val _categoryOptions = MutableStateFlow<List<String>>(emptyList())
    val categoryOptions: StateFlow<List<String>> = _categoryOptions.asStateFlow()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            prepareData()
        }
    }

    private fun prepareData() {
        loadExercises()
        preloadOptions()
    }

    private fun loadExercises() {
        val jsonString = loadJSONFromAsset("exercises.json")
        val exerciseListType = object : TypeToken<ExerciseWrapper>() {}.type
        val exerciseWrapper: ExerciseWrapper = Gson().fromJson(jsonString, exerciseListType)
        _exercises.update { exerciseWrapper.exercises }
    }

    private fun loadJSONFromAsset(fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            Timber.e(e)
            null
        }
    }

    fun getExerciseByName(name: String): Exercise? {
        return exercises.value.find { it.name == name }
    }

    fun filterExercises(query: String): List<Exercise> {
        return exercises.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.equipment?.contains(query, ignoreCase = true) == true ||
                    it.primaryMuscles.any { muscle -> muscle.contains(query, ignoreCase = true) } ||
                    it.secondaryMuscles.any { muscle -> muscle.contains(query, ignoreCase = true) }
        }
    }

    private fun preloadOptions() {
        val exercises = _exercises.value
        _equipmentOptions.value = exercises.mapNotNull { it.equipment }.distinct()
        _primaryMusclesOptions.value = exercises.flatMap { it.primaryMuscles }.distinct()
        _secondaryMusclesOptions.value = exercises.flatMap { it.secondaryMuscles }.distinct()
        _categoryOptions.value = exercises.map { it.category }.distinct()
    }

    private data class ExerciseWrapper(
        val exercises: List<Exercise>
    )
}
