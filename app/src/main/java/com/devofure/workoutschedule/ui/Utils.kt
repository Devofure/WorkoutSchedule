package com.devofure.workoutschedule.ui

import com.devofure.workoutschedule.ui.settings.FirstDayOfWeek
import java.time.LocalDate

fun getFullDayName(day: String, nickname: String): String =
    if (nickname.isEmpty()) day else "$day ($nickname)"

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
