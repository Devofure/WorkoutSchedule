package com.devofure.workoutschedule.ui

import com.devofure.workoutschedule.data.FirstDayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MainUtilsTest {

    // Tests for getDaysInMonth
    @Test
    fun testGetDaysInMonth_january() {
        val date = LocalDate.of(2024, 1, 1)
        val expected = 31
        val result = date.lengthOfMonth()
        assertEquals(expected, result)
    }

    @Test
    fun testGetDaysInMonth_february_nonLeapYear() {
        val date = LocalDate.of(2023, 2, 1)
        val expected = 28
        val result = date.lengthOfMonth()
        assertEquals(expected, result)
    }

    @Test
    fun testGetDaysInMonth_february_leapYear() {
        val date = LocalDate.of(2024, 2, 1)
        val expected = 29
        val result = date.lengthOfMonth()
        assertEquals(expected, result)
    }

    // Tests for getWeekStartDate
    @Test
    fun testGetWeekStartDate_sundayFirstDay() {
        val date = LocalDate.of(2024, 6, 12)
        val expected = LocalDate.of(2024, 6, 9)
        val result = getWeekStartDate(date, FirstDayOfWeek.SUNDAY)
        assertEquals(expected, result)
    }

    @Test
    fun testGetWeekStartDate_mondayFirstDay() {
        val date = LocalDate.of(2024, 6, 12)
        val expected = LocalDate.of(2024, 6, 10)
        val result = getWeekStartDate(date, FirstDayOfWeek.MONDAY)
        assertEquals(expected, result)
    }

    // Tests for getFirstDayOfMonth
    @Test
    fun testGetFirstDayOfMonth_january2024() {
        val date = LocalDate.of(2024, 1, 6)
        val expected = 1 // Monday
        val result = getFirstDayOfMonthWeekIndex(date)
        assertEquals(expected, result)
    }

    @Test
    fun testGetFirstDayOfMonth_february2024() {
        val date = LocalDate.of(2024, 2, 6)
        val expected = 4 // Thursday
        val result = getFirstDayOfMonthWeekIndex(date)
        assertEquals(expected, result)
    }

    @Test
    fun testGetFirstDayOfMonth_march2024() {
        val date = LocalDate.of(2024, 3, 4)
        val expected = 5 // Friday
        val result = getFirstDayOfMonthWeekIndex(date)
        assertEquals(expected, result)
    }

    @Test
    fun testGetFirstDayOfMonth_april2024() {
        val date = LocalDate.of(2024, 4, 5)
        val expected = 1 // Monday
        val result = getFirstDayOfMonthWeekIndex(date)
        assertEquals(expected, result)
    }

    // Tests for calculateDayOfWeekOffset
    @Test
    fun testCalculateDayOfWeekOffset_sundayFirstDay() {
        val firstDayOfMonth = 4 // Thursday
        val firstDayOfWeek = FirstDayOfWeek.SUNDAY
        val expected = 4
        val result = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
        assertEquals(expected, result)
    }

    @Test
    fun testCalculateDayOfWeekOffset_mondayFirstDay() {
        val firstDayOfMonth = 4 // Thursday
        val firstDayOfWeek = FirstDayOfWeek.MONDAY
        val expected = 3
        val result = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
        assertEquals(expected, result)
    }

    @Test
    fun testGetTotalCells_MonthView() {
        val firstDayOfMonth = 4 // Assume Thursday
        val firstDayOfWeek = FirstDayOfWeek.MONDAY
        val daysInMonth = 30 // Assume a 30-day month
        val isMonthView = true

        val expectedTotalCells = 35 // 5 weeks * 7 days
        val result = getTotalCells(firstDayOfMonth, firstDayOfWeek, daysInMonth, isMonthView)
        assertEquals(expectedTotalCells, result)
    }

    @Test
    fun testGetTotalCells_MonthView_SixWeeks() {
        val firstDayOfMonth = 6 // Assume Saturday
        val firstDayOfWeek = FirstDayOfWeek.SUNDAY
        val daysInMonth = 31 // Assume a 31-day month
        val isMonthView = true

        val expectedTotalCells = 42 // 6 weeks * 7 days
        val result = getTotalCells(firstDayOfMonth, firstDayOfWeek, daysInMonth, isMonthView)
        assertEquals(expectedTotalCells, result)
    }

    @Test
    fun testGetTotalCells_WeekView() {
        val firstDayOfMonth = 2 // Assume Tuesday
        val firstDayOfWeek = FirstDayOfWeek.MONDAY
        val daysInMonth = 30 // Assume a 30-day month
        val isMonthView = false

        val expectedTotalCells = 7 // Always 7 for week view
        val result = getTotalCells(firstDayOfMonth, firstDayOfWeek, daysInMonth, isMonthView)
        assertEquals(expectedTotalCells, result)
    }

    @Test
    fun testGetTotalCells_ExactWeeks() {
        val firstDayOfMonth = 3 // Assume Wednesday
        val firstDayOfWeek = FirstDayOfWeek.SUNDAY
        val daysInMonth = 28 // Assume a 28-day month (4 exact weeks)
        val isMonthView = true

        val expectedTotalCells =
            35 // 5 weeks * 7 days (because of the starting day of the week offset)
        val result = getTotalCells(firstDayOfMonth, firstDayOfWeek, daysInMonth, isMonthView)
        assertEquals(expectedTotalCells, result)
    }
}