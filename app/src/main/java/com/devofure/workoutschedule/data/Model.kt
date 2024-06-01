package com.devofure.workoutschedule.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Workout(
    val id: Int,
    val name: String,
    val description: String,
    val isDone: Boolean = false
)