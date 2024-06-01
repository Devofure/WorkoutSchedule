package com.devofure.workoutschedule.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

data class Exercise(
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

    fun loadExercises(): List<Exercise> {
        val jsonString = loadJSONFromAsset("exercises.json")
        val exerciseListType = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(jsonString, exerciseListType)
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
}
