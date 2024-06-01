package com.devofure.workoutschedule.data

data class Workout(
    val id: Int,
    val name: String,
    val description: String,
    val isDone: Boolean = false
)