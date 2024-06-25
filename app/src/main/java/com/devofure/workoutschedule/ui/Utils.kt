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
    0 to listOf(
        "Push-Up",
        "Dumbbell Bench Press",
        "Chest Flyes with Bands",
        "Close-Grip Push-Up",
        "Triceps Extension with Bands"
    ),
    1 to listOf(
        "Bodyweight Rows",
        "Dumbbell Rows",
        "Band Pull-Aparts",
        "Bicep Curls with Dumbbells",
        "Hammer Curls with Bands"
    ),
    2 to listOf(
        "Bodyweight Squats",
        "Dumbbell Lunges",
        "Goblet Squat with Dumbbell",
        "Band Hamstring Curls",
        "Calf Raises"
    ),
    3 to listOf(
        "Bodyweight Shoulder Press",
        "Dumbbell Shoulder Press",
        "Lateral Raises with Bands",
        "Plank",
        "Russian Twists"
    ),
    4 to listOf(
        "Deadlift with Dumbbells",
        "Kettlebell Dead Clean",
        "Bear Crawl with Bands",
        "Child's Pose",
        "Dynamic Chest Stretch"
    ),
    5 to listOf(
        "Mountain Climbers",
        "Air Bike",
        "Plank",
        "Russian Twist",
        "Flutter Kicks"
    ),
    6 to listOf(
        "Cat Stretch",
        "Quad Stretch",
        "Hamstring Stretch",
        "Butterfly Stretch",
        "Child's Pose"
    )
)

@Preview(
    name = "Landscape Mode",
    showBackground = true,
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 640
)
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PIXEL_4)
annotation class OrientationPreviews