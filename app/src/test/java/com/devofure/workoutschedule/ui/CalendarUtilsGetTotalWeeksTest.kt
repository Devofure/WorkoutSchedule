package com.devofure.workoutschedule.ui

import com.devofure.workoutschedule.ui.settings.FirstDayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarUtilsTest {

    @Test
    fun testGetTotalWeeks_MonthView_FourWeeks() {
        val isMonthView = true
        val firstDayOfMonth = 0 // Sunday
        val daysInMonth = 28 // February in a non-leap year
        val expected = 5 // 5 weeks in February 2023 when Sunday is the first day of the week
        val result = getTotalWeeks(isMonthView, firstDayOfMonth, daysInMonth, FirstDayOfWeek.SUNDAY)
        assertEquals(expected, result)
    }

    @Test
    fun testGetTotalWeeks_MonthView_FiveWeeks() {
        val isMonthView = true
        val firstDayOfMonth = 1 // Monday
        val daysInMonth = 31 // January
        val expected = 5
        val result = getTotalWeeks(isMonthView, firstDayOfMonth, daysInMonth, FirstDayOfWeek.MONDAY)
        assertEquals(expected, result)
    }

    @Test
    fun testGetTotalWeeks_MonthView_SixWeeks() {
        val isMonthView = true
        val firstDayOfMonth = 5 // Friday
        val daysInMonth = 31 // July
        val expected = 6
        val result = getTotalWeeks(isMonthView, firstDayOfMonth, daysInMonth, FirstDayOfWeek.SUNDAY)
        assertEquals(expected, result)
    }

    @Test
    fun testGetTotalWeeks_WeekView() {
        val isMonthView = false
        val firstDayOfMonth = 0 // This value doesn't matter in week view
        val daysInMonth = 0 // This value doesn't matter in week view
        val expected = 1
        val result = getTotalWeeks(isMonthView, firstDayOfMonth, daysInMonth, FirstDayOfWeek.SUNDAY)
        assertEquals(expected, result)
    }
}
