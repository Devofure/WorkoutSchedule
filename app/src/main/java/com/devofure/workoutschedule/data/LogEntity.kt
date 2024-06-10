package com.devofure.workoutschedule.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_workouts")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val workoutId: Int,
    val exerciseName: String,
    val dayOfWeek: String,
    val repsList: List<SetDetails>? = null,
    val duration: Int? = null,
)