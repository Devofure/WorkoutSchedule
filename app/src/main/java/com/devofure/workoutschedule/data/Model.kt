package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val exercise: Exercise,
    val sets: Int,
    val reps: Int,
    val duration: Int? = null, // for time-based exercises
    val isDone: Boolean = false
)
