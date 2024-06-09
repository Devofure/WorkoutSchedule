package com.devofure.workoutschedule.ui

import com.devofure.workoutschedule.ui.settings.FirstDayOfWeek
import java.util.Calendar
import java.util.Date

fun getFullDayName(day: String, nickname: String): String {
    return if (nickname.isEmpty()) day else "$day ($nickname)"
}

fun getDaysInMonth(year: Int, month: Int): Int {
    return Calendar.getInstance().apply {
        set(year, month, 1)
    }.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getWeekStartDate(date: Date, firstDayOfWeek: FirstDayOfWeek): Date {
    val calendar = Calendar.getInstance().apply { time = date }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val offset = if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) {
        dayOfWeek - Calendar.SUNDAY
    } else {
        if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
    }
    calendar.add(Calendar.DAY_OF_MONTH, -offset)
    return calendar.time
}

fun getFirstDayOfMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance().apply {
        set(year, month, 1)
    }
    val firstDay = calendar.get(Calendar.DAY_OF_WEEK)
    return firstDay - 1 // Calendar.SUNDAY is 1, so subtract 1 to make it 0-based
}

fun getTotalWeeks(
    isMonthView: Boolean,
    firstDayOfMonth: Int,
    daysInMonth: Int,
    firstDayOfWeek: FirstDayOfWeek
): Int {
    val totalDays = firstDayOfMonth + daysInMonth
    val firstDayOffset = if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) 0 else 1
    val additionalWeek = if ((totalDays + firstDayOffset) % 7 > 0) 1 else 0
    return if (isMonthView) (totalDays + firstDayOffset) / 7 + additionalWeek else 1
}


fun calculateDayOfWeekOffset(firstDayOfMonth: Int, firstDayOfWeek: FirstDayOfWeek): Int {
    return if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) {
        firstDayOfMonth
    } else {
        (firstDayOfMonth - 1 + 7) % 7 // Adjust for Monday as the first day of the week
    }
}

fun isSameDay(calendar: Calendar, date: Date, day: Int): Boolean {
    val testCalendar = Calendar.getInstance().apply { time = date }
    return calendar.get(Calendar.YEAR) == testCalendar.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == testCalendar.get(Calendar.MONTH) &&
            day == testCalendar.get(Calendar.DAY_OF_MONTH)
}