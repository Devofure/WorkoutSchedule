package com.devofure.workoutschedule.ui

import com.devofure.workoutschedule.ui.settings.FirstDayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class MainUtilsTest {

    @Test
    fun testGetFullDayName_withNickname() {
        val day = "Monday"
        val nickname = "Mon"
        val expected = "Monday (Mon)"
        val result = getFullDayName(day, nickname)
        assertEquals(expected, result)
    }

    @Test
    fun testGetFullDayName_withoutNickname() {
        val day = "Monday"
        val nickname = ""
        val expected = day
        val result = getFullDayName(day, nickname)
        assertEquals(expected, result)
    }

    @Test
    fun testGetDaysInMonth_january() {
        val year = 2024
        val month = Calendar.JANUARY
        val expected = 31
        val result = getDaysInMonth(year, month)
        assertEquals(expected, result)
    }

    @Test
    fun testGetDaysInMonth_february_nonLeapYear() {
        val year = 2023
        val month = Calendar.FEBRUARY
        val expected = 28
        val result = getDaysInMonth(year, month)
        assertEquals(expected, result)
    }

    @Test
    fun testGetDaysInMonth_february_leapYear() {
        val year = 2024
        val month = Calendar.FEBRUARY
        val expected = 29
        val result = getDaysInMonth(year, month)
        assertEquals(expected, result)
    }

    @Test
    fun testGetWeekStartDate_sundayFirstDay() {
        val date = GregorianCalendar(2024, Calendar.JUNE, 12).time
        val expected = GregorianCalendar(2024, Calendar.JUNE, 9).time
        val result = getWeekStartDate(date, FirstDayOfWeek.SUNDAY)
        assertEquals(expected, result)
    }

    @Test
    fun testGetWeekStartDate_mondayFirstDay() {
        val date = GregorianCalendar(2024, Calendar.JUNE, 12).time
        val expected = GregorianCalendar(2024, Calendar.JUNE, 10).time
        val result = getWeekStartDate(date, FirstDayOfWeek.MONDAY)
        assertEquals(expected, result)
    }
    @Test
    fun testGetFirstDayOfMonth_january2024() {
        val year = 2024
        val month = Calendar.JANUARY
        val expected = 0 // Monday
        val result = getFirstDayOfMonth(year, month)
        assertEquals(expected, result)
    }

    @Test
    fun testGetFirstDayOfMonth_february2024() {
        val year = 2024
        val month = Calendar.FEBRUARY
        val expected = 3 // Thursday
        val result = getFirstDayOfMonth(year, month)
        assertEquals(expected, result)
    }

    @Test
    fun testCalculateDayOfWeekOffset_sundayFirstDay() {
        val firstDayOfMonth = 3 // Thursday
        val firstDayOfWeek = FirstDayOfWeek.SUNDAY
        val expected = 3
        val result = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
        assertEquals(expected, result)
    }

    @Test
    fun testCalculateDayOfWeekOffset_mondayFirstDay() {
        val firstDayOfMonth = 3 // Thursday
        val firstDayOfWeek = FirstDayOfWeek.MONDAY
        val expected = 2
        val result = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
        assertEquals(expected, result)
    }

    @Test
    fun testIsSameDay_sameDay() {
        val logCalendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 9)
        }
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 9)
        }
        val day = 9
        val result = isSameDay(logCalendar, calendar, day)
        assertEquals(true, result)
    }

    @Test
    fun testIsSameDay_differentDay() {
        val logCalendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 9)
        }
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 10)
        }
        val day = 9
        val result = isSameDay(logCalendar, calendar, day)
        assertEquals(false, result)
    }
}
