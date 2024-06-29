// Model.kt
package com.devofure.workoutschedule.data

import com.devofure.workoutschedule.data.exercise.Exercise
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Workout(
    val id: Int,
    val exercise: Exercise,
    val repsList: List<SetDetails>? = null,
    val durationInSeconds: Int? = null,
    val isDone: Boolean = false
)

data class SetDetails(
    val reps: Int = 1,
    val weight: Float? = null,
    val duration: Int? = null
)

val WEEK = listOf(
    DayOfWeek.Monday,
    DayOfWeek.Tuesday,
    DayOfWeek.Wednesday,
    DayOfWeek.Thursday,
    DayOfWeek.Friday,
    DayOfWeek.Saturday,
    DayOfWeek.Sunday
)

sealed class DayOfWeek(val dayIndex: Int, private val fullName: String) {
    enum class DayNamingPreference {
        DAY_NAMES, DAY_NUMBERS
    }

    fun getFullName(dayNamingPreference: DayNamingPreference): String {
        return when (dayNamingPreference) {
            DayNamingPreference.DAY_NAMES -> fullName
            DayNamingPreference.DAY_NUMBERS -> "Day ${dayIndex + 1}"
        }
    }

    fun getShortName(dayNamingPreference: DayNamingPreference): String {
        return when (dayNamingPreference) {
            DayNamingPreference.DAY_NAMES -> fullName.substring(0, 3)
            DayNamingPreference.DAY_NUMBERS -> "${dayIndex + 1}"
        }
    }

    data object Monday : DayOfWeek(0, "Monday")
    data object Tuesday : DayOfWeek(1, "Tuesday")
    data object Wednesday : DayOfWeek(2, "Wednesday")
    data object Thursday : DayOfWeek(3, "Thursday")
    data object Friday : DayOfWeek(4, "Friday")
    data object Saturday : DayOfWeek(5, "Saturday")
    data object Sunday : DayOfWeek(6, "Sunday")
}

data class ReminderTime(
    val hour: Int,
    val minute: Int
) {
    fun format(): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}

enum class FirstDayOfWeek {
    SUNDAY, MONDAY
}