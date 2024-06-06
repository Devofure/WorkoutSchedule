package com.devofure.workoutschedule.ui.main

import java.util.Calendar

fun getFullDayName(day: String, nickname: String): String {
    return if (nickname.isEmpty()) day else "$day ($nickname)"
}

fun getDaysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getFirstDayOfMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}