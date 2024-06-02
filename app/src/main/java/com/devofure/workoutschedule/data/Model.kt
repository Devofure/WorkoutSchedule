package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val exercise: Exercise,
    val sets: Int? = null,
    val reps: Int? = null,
    val duration: Int? = null, // for time-based exercises
    val isDone: Boolean = false
)
