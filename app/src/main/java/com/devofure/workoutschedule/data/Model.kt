// Model.kt
package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val exercise: Exercise,
    val repsList: List<SetDetails>? = null,
    val duration: Int? = null,
    val isDone: Boolean = false
)

data class SetDetails(
    val reps: Int,
    val weight: Float? = null,
    val duration: Int? = null
)
