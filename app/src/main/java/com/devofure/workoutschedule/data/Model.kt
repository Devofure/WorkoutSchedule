// Model.kt
package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val exercise: Exercise,
    val sets: Int? = null,
    val repsList: List<Int>? = null,
    val duration: Int? = null,
    val isDone: Boolean = false
)
