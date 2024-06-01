package com.devofure.workoutschedule.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

data class Exercise(
    val id: Int,
    val name: String,
    val force: String,
    val level: String,
    val mechanic: String?,
    val equipment: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val category: String
)

class ExerciseRepository(private val context: Context) {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    init {
        loadExercises()
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
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun getExerciseByName(name: String): Exercise? {
        return exercises.value.find { it.name == name }
    }

    private data class ExerciseWrapper(
        val exercises: List<Exercise>
    )
}
