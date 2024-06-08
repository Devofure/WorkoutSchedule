package com.devofure.workoutschedule.ui.calendar

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.height
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
import com.devofure.workoutschedule.ui.main.getDaysInMonth
import com.devofure.workoutschedule.ui.main.getFirstDayOfMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarView(selectedDate: Date, logs: List<LogEntity>, isMonthView: Boolean, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfMonth = getFirstDayOfMonth(year, month)
    val totalCells = daysInMonth + firstDayOfMonth
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        MonthNavigation(calendar, onDateSelected, isMonthView)
        Spacer(modifier = Modifier.height(8.dp))
        WeekDayHeaders()
        CalendarGrid(totalCells, firstDayOfMonth, daysInMonth, calendar, selectedDate, logs, dateFormat, onDateSelected)
    }
}

@Composable
fun WeekView(selectedDate: Date, logs: List<LogEntity>, isMonthView: Boolean, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))

    val daysInWeek = 7
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        MonthNavigation(calendar, onDateSelected, isMonthView)
        Spacer(modifier = Modifier.height(8.dp))
        WeekDayHeaders()
        CalendarGrid(daysInWeek, 0, daysInWeek, calendar, selectedDate, logs, dateFormat, onDateSelected, isWeekView = true)
    }
}

@Composable
fun MonthNavigation(calendar: Calendar, onDateSelected: (Date) -> Unit, isMonthView: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isMonthView) {
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, -1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
        }
        Text(
            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        if (isMonthView) {
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, 1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }
    }
}

@Composable
fun WeekDayHeaders() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
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
    onDateSelected: (Date) -> Unit,
    isWeekView: Boolean = false
) {
    for (i in 0 until totalCells step 7) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (j in i until i + 7) {
                val day = if (isWeekView) {
                    calendar.get(Calendar.DAY_OF_MONTH) + (j - i)
                } else {
                    j - firstDayOfMonth + 1
                }
                val dayOfMonth = if (isWeekView) calendar.get(Calendar.DAY_OF_MONTH) else day
                if ((j >= firstDayOfMonth && day <= daysInMonth) || isWeekView) {
                    val logExists = logs.any { log ->
                        val logDate = dateFormat.parse(log.date)
                        val logCalendar = Calendar.getInstance().apply { time = logDate!! }
                        logCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth &&
                                logCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                                logCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    }
                    DayCell(dayOfMonth, selectedDate, logExists, onDateSelected, Modifier.weight(1f))
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
    val isSelectedDay = isSameDay(calendar.apply { time = selectedDate }, day)
    val backgroundColor by animateColorAsState(if (isSelectedDay) MaterialTheme.colors.primary else Color.Transparent)
    val textColor by animateColorAsState(if (isSelectedDay) Color.White else Color.Black)

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

private fun isSameDay(calendar: Calendar, day: Int): Boolean {
    return calendar.get(Calendar.DAY_OF_MONTH) == day
}
