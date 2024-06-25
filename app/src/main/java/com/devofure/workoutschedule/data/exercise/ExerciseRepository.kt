package com.devofure.workoutschedule.data.exercise

import android.content.Context
import androidx.annotation.Keep
import androidx.room.withTransaction
import com.devofure.workoutschedule.data.AppDatabase
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

@Keep
data class Exercise(
    val rowid: Int,
    val name: String,
    val level: String? = null,
    val mechanic: String? = null,
    val equipment: String? = null,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val force: String? = null,
    val instructions: List<String>? = null,
    val category: String,
)

class ExerciseRepository(
    private val context: Context,
    private val exerciseDao: ExerciseDao,
    private val appDatabase: AppDatabase,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _equipmentOptions = MutableStateFlow<List<String>>(emptyList())
    val equipmentOptions: StateFlow<List<String>> = _equipmentOptions.asStateFlow()

    private val _muscleOptions = MutableStateFlow<List<String>>(emptyList())
    val primaryMusclesOptions: StateFlow<List<String>> = _muscleOptions.asStateFlow()

    private val _categoryOptions = MutableStateFlow<List<String>>(emptyList())
    val categoryOptions: StateFlow<List<String>> = _categoryOptions.asStateFlow()

    init {
        coroutineScope.launch {
            prepareData()
        }
    }

    private suspend fun prepareData() {
        loadExercises()
        preloadOptions()
    }

    private suspend fun loadExercises() {
        appDatabase.withTransaction {
            if (exerciseDao.getExerciseCount() == 0) {
                val jsonString = loadJSONFromAsset("exercises.json")
                val exerciseListType = object : TypeToken<ExerciseWrapper>() {}.type
                val exerciseWrapper: ExerciseWrapper = Gson().fromJson(jsonString, exerciseListType)
                exerciseDao.insertAll(exerciseWrapper.exercises.map { it.toExerciseEntity() })
            }
            val exercisesFromDb = exerciseDao.getAllExercises().map { it.toExercise() }
            _exercises.update { exercisesFromDb }
        }
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

    fun searchExercises(query: String): List<ExerciseFtsEntity> {
        val normalizedQuery = normalizeQuery(query)
        val list = exerciseDao.searchExercises(normalizedQuery)
        return list
    }

    private fun normalizeQuery(query: String): String {
        // Add wildcards for partial matching
        val parts = query.toLowerCase().split(" ").map { it + "*" }
        return parts.joinToString(" ")
    }

    private fun preloadOptions() {
        val exercises = _exercises.value
        _equipmentOptions.value = exercises.mapNotNull { it.equipment }.distinct()
        _categoryOptions.value = exercises.map { it.category }.distinct()
        val primaryMuscleOptions = exercises.flatMap { it.primaryMuscles }
        val secondaryMuscleOptions = exercises.flatMap { it.secondaryMuscles }
        val distinctMuscleOptions = (primaryMuscleOptions + secondaryMuscleOptions).distinct()
        _muscleOptions.value = distinctMuscleOptions
    }

    @Keep
    private data class ExerciseWrapper(
        val exercises: List<Exercise>
    )
}

fun Exercise.toExerciseEntity(): ExerciseEntity {
    return ExerciseEntity(
        rowid = this.rowid,
        name = this.name,
        force = this.force,
        mechanic = this.mechanic,
        equipment = this.equipment,
        primaryMuscles = this.primaryMuscles.joinToString(","),
        secondaryMuscles = this.secondaryMuscles.joinToString(","),
        instructions = this.instructions?.joinToString(","),
        category = this.category,
        level = this.level,
    )
}

fun ExerciseEntity.toExercise(): Exercise {
    return Exercise(
        rowid = this.rowid,
        name = this.name,
        force = this.force,
        level = this.level,
        mechanic = this.mechanic,
        equipment = this.equipment,
        primaryMuscles = this.primaryMuscles.split(","),
        secondaryMuscles = this.secondaryMuscles.split(","),
        instructions = this.instructions?.split(","),
        category = this.category
    )
}
