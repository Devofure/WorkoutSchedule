package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val name: String,
    val description: String,
    val equipment: String = "",
    val force: String = "",
    val instructions: List<String> = emptyList(),
    val level: String = "",
    val mechanic: String? = null,
    val primaryMuscles: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val isDone: Boolean = false
)
