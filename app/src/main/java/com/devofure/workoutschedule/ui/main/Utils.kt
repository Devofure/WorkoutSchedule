package com.devofure.workoutschedule.ui.main

fun getFullDayName(day: String, nickname: String): String {
    return if (nickname.isEmpty()) day else "$day ($nickname)"
}
