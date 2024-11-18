// Model.kt
package com.devofure.workoutschedule.data

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@IgnoreExtraProperties
data class Workout(
    var id: Int = 0,
    var exercise: Exercise = Exercise(),
    var repsList: List<SetDetails>? = null,
    var durationInSeconds: Int? = null,
    var position: Int? = null,
    var dayIndex: Int = 0,
    var isDone: Boolean = false
)

@IgnoreExtraProperties
data class SetDetails(
    var reps: Int = 0,
    var weight: Float? = 0f,
    val duration: Int? = null,
)

@IgnoreExtraProperties
data class Exercise(
    var rowid: Int = 0,
    var name: String = "",
    var force: String? = null,
    var level: String? = null,
    var mechanic: String? = null,
    var equipment: String? = null,
    var primaryMuscles: List<String> = emptyList(),
    var secondaryMuscles: List<String> = emptyList(),
    var instructions: List<String>? = null,
    var category: String? = null,
    var muscleCategory: String? = null
)

val WEEK = listOf(
    DayOfWeek.Monday,
    DayOfWeek.Tuesday,
    DayOfWeek.Wednesday,
    DayOfWeek.Thursday,
    DayOfWeek.Friday,
    DayOfWeek.Saturday,
    DayOfWeek.Sunday,
)

sealed class DayOfWeek(val dayIndex: Int, private val fullName: String) {
    enum class DayNamingPreference { DAY_NAMES, DAY_NUMBERS, }

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

enum class FirstDayOfWeek { SUNDAY, MONDAY, }