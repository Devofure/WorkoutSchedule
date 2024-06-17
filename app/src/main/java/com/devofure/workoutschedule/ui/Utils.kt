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
        "Dumbbell Bench Press",
        "Close-Grip Push-Up off of a Dumbbell",
        "Dumbbell Flyes",
        "Cable Chest Press",
        "Triceps Pushdown - Rope Attachment"
    ),
    1 to listOf(
        "Bent Over Barbell Row",
        "One-Arm Dumbbell Row",
        "Alternating Kettlebell Row",
        "Hammer Curls",
        "Dumbbell Bicep Curl"
    ),
    2 to listOf(
        "Barbell Squat",
        "Dumbbell Lunges",
        "Goblet Squat",
        "Hamstring Stretch",
        "Calf Raises - With Bands"
    ),
    3 to listOf(
        "Arnold Dumbbell Press",
        "Dumbbell Shoulder Press",
        "Side Lateral Raise",
        "3/4 Sit-Up",
        "Cable Russian Twists"
    ),
    4 to listOf(
        "Deadlift",
        "Kettlebell Dead Clean",
        "Bear Crawl Sled Drags",
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
        "Butterfly",
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