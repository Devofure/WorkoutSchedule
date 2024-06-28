package com.devofure.workoutschedule.ui

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.data.FirstDayOfWeek
import java.time.LocalDate

fun getDayName(
    day: DayOfWeek,
    dayNamingPreference: DayOfWeek.DayNamingPreference,
    dayNickname: String?
): String {
    val separator = when (dayNamingPreference) {
        DayOfWeek.DayNamingPreference.DAY_NAMES -> " - "
        DayOfWeek.DayNamingPreference.DAY_NUMBERS -> ". "
    }
    return if (dayNickname.isNullOrEmpty()) {
        day.getFullName(dayNamingPreference)
    } else {
        "${day.getShortName(dayNamingPreference)}$separator$dayNickname"
    }
}

fun getWeekStartDate(date: LocalDate, firstDayOfWeek: FirstDayOfWeek): LocalDate {
    val dayOfWeek = date.dayOfWeek.value
    val offset = if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) {
        dayOfWeek % 7 // Sunday as 0
    } else {
        if (dayOfWeek == 7) 6 else dayOfWeek - 1 // Monday as 0
    }
    return date.minusDays(offset.toLong())
}

fun getFirstDayOfMonthWeekIndex(date: LocalDate): Int =
    date.withDayOfMonth(1).dayOfWeek.value % 7

fun calculateDayOfWeekOffset(firstDayOfMonth: Int, firstDayOfWeek: FirstDayOfWeek): Int =
    if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) firstDayOfMonth
    else (firstDayOfMonth + 6) % 7 // Adjust for Monday as the first day of the week

fun isSameDay(calendarMonth: LocalDate, selectedDate: LocalDate, day: Int): Boolean =
    calendarMonth.withDayOfMonth(day) == selectedDate

fun getTotalCells(
    firstDayOfMonth: Int,
    firstDayOfWeek: FirstDayOfWeek,
    daysInMonth: Int,
    isMonthView: Boolean
): Int {
    if (!isMonthView) return 7
    val dayOfWeekOffset = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
    val totalDays = dayOfWeekOffset + daysInMonth
    val totalWeeks =
        (totalDays + 6) / 7 // Same as Math.ceil(totalDays / 7.0) but without using floating-point arithmetic
    return totalWeeks * 7
}

val SAMPLE_EXERCISE_SCHEDULE = mapOf(
    // Day 0: Upper body with curls and presses.
    0 to listOf("Alternate Hammer Curl", "Alternate Incline Dumbbell Curl", "Alternating Cable Shoulder Press", "Alternating Deltoid Raise"),

    // Day 1: Core day with sit-ups and roller exercises.
    1 to listOf("3/4 Sit-Up", "Ab Crunch Machine", "Ab Roller", "Advanced Kettlebell Windmill"),

    // Day 2: Lower body focus with hamstring and quad exercises.
    2 to listOf("90/90 Hamstring", "Adductor", "Adductor/Groin", "All Fours Quad Stretch"),

    // Day 3: Rest day for recovery.
    3 to listOf(),

    // Day 4: Full body mix with presses and cleans.
    4 to listOf("Alternating Floor Press", "Alternating Kettlebell Press", "Air Bike", "Alternate Heel Touchers", "Alternate Leg Diagonal Bound", "Alternating Hang Clean"),

    // Day 5: Additional core and lower body exercises.
    5 to listOf("Barbell Side Bend", "Atlas Stone Trainer", "Band Good Morning"),

    // Day 6: Flexibility and recovery exercises.
    6 to listOf("Adductor", "Ankle Circles", "All Fours Quad Stretch")
)

@Preview(
    name = "Landscape Mode",
    showBackground = true,
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 640
)
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PIXEL_4)
annotation class OrientationPreviews