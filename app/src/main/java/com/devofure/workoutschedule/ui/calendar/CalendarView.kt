package com.devofure.workoutschedule.ui.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.LogEntity
import com.devofure.workoutschedule.ui.calculateDayOfWeekOffset
import com.devofure.workoutschedule.ui.getDaysInMonth
import com.devofure.workoutschedule.ui.getFirstDayOfMonth
import com.devofure.workoutschedule.ui.getWeekStartDate
import com.devofure.workoutschedule.ui.isSameDay
import com.devofure.workoutschedule.ui.settings.FirstDayOfWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarView(
    selectedDate: Date,
    logs: List<LogEntity>,
    isMonthView: Boolean,
    firstDayOfWeek: FirstDayOfWeek,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfMonth = getFirstDayOfMonth(year, month)

    // Calculate the number of weeks to display
    val dayOfWeekOffset = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
    val totalDays = dayOfWeekOffset + daysInMonth
    val totalWeeks = if (totalDays % 7 == 0) totalDays / 7 else (totalDays / 7) + 1
    val totalCells = if (isMonthView) totalWeeks * 7 else 7

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        MonthNavigation(selectedDate, isMonthView, onDateSelected)
        WeekDayHeaders(firstDayOfWeek)
        CalendarGrid(
            totalCells = totalCells,
            firstDayOfMonth = firstDayOfMonth,
            daysInMonth = daysInMonth,
            calendar = calendar,
            selectedDate = selectedDate,
            logs = logs,
            dateFormat = dateFormat,
            firstDayOfWeek = firstDayOfWeek,
            onDateSelected = onDateSelected,
            isWeekView = !isMonthView
        )
    }
}


@Composable
fun MonthNavigation(selectedDate: Date, isMonthView: Boolean, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (isMonthView) {
                calendar.add(Calendar.MONTH, -1)
            } else {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
            }
            onDateSelected(calendar.time)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = if (isMonthView) "Previous month" else "Previous week"
            )
        }
        Text(
            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            if (isMonthView) {
                calendar.add(Calendar.MONTH, 1)
            } else {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
            onDateSelected(calendar.time)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isMonthView) "Next month" else "Next week"
            )
        }
    }
}

@Composable
fun CalendarGrid(
    totalCells: Int,
    firstDayOfMonth: Int,
    daysInMonth: Int,
    calendar: Calendar,
    selectedDate: Date,
    logs: List<LogEntity>,
    dateFormat: SimpleDateFormat,
    firstDayOfWeek: FirstDayOfWeek,
    onDateSelected: (Date) -> Unit,
    isWeekView: Boolean = false
) {
    if (isWeekView) {
        calendar.time = getWeekStartDate(selectedDate, firstDayOfWeek)
    }
    val dayOfWeekOffset = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
    val weeksToShow = totalCells / 7

    for (week in 0 until weeksToShow) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (dayOffset in 0 until 7) {
                val cellIndex = week * 7 + dayOffset
                val day = if (isWeekView) {
                    calendar.get(Calendar.DAY_OF_MONTH)
                } else {
                    val calculatedDay = cellIndex - dayOfWeekOffset + 1
                    if (calculatedDay in 1..daysInMonth) calculatedDay else null
                }

                println("Week: $week, DayOffset: $dayOffset, CellIndex: $cellIndex, CalculatedDay: $day") // Debugging

                if (day != null) {
                    val logExists = logs.any { log ->
                        val logDate = dateFormat.parse(log.date)
                        val logCalendar = Calendar.getInstance().apply { time = logDate!! }
                        isSameDay(logCalendar, calendar, day)
                    }
                    DayCell(
                        day = day,
                        selectedDate = selectedDate,
                        logExists = logExists,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                    if (isWeekView) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
        if (isWeekView) break // Stop after showing one week
    }
}

@Composable
fun DayCell(
    day: Int,
    selectedDate: Date,
    logExists: Boolean,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val isSelectedDay = calendar.get(Calendar.DAY_OF_MONTH) == day &&
            calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    val backgroundColor by animateColorAsState(
        if (isSelectedDay) MaterialTheme.colors.primary else Color.Transparent,
        label = "backgroundColor"
    )
    val textColor by animateColorAsState(
        if (isSelectedDay) Color.White else Color.Black,
        label = "textColor"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                onDateSelected(calendar.time)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                color = textColor,
                style = MaterialTheme.typography.body1
            )
        }
        if (logExists) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(8.dp)
                    .background(
                        MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
            )
        }
    }
}


@Composable
fun WeekDayHeaders(firstDayOfWeek: FirstDayOfWeek) {
    val days = if (firstDayOfWeek == FirstDayOfWeek.SUNDAY) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    } else {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
