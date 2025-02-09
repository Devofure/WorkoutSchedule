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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.FirstDayOfWeek
import com.devofure.workoutschedule.ui.calculateDayOfWeekOffset
import com.devofure.workoutschedule.ui.getFirstDayOfMonthWeekIndex
import com.devofure.workoutschedule.ui.getTotalCells
import com.devofure.workoutschedule.ui.getWeekStartDate
import com.devofure.workoutschedule.ui.isSameDay
import com.devofure.workoutschedule.ui.theme.Colors.DefaultThemeColor
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarView(
    selectedDate: LocalDate,
    logDates: List<LocalDate>,
    isMonthView: Boolean,
    firstDayOfWeek: FirstDayOfWeek,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = getFirstDayOfMonthWeekIndex(selectedDate)
    val totalCells =
        getTotalCells(firstDayOfMonth, firstDayOfWeek, selectedDate.lengthOfMonth(), isMonthView)

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
            selectedDate = selectedDate,
            logDates = logDates,
            firstDayOfWeek = firstDayOfWeek,
            onDateSelected = onDateSelected,
            isWeekView = !isMonthView
        )
    }
}

@Composable
fun MonthNavigation(
    selectedDate: LocalDate,
    isMonthView: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = {
                val newDate = if (isMonthView) {
                    selectedDate.minusMonths(1)
                } else {
                    selectedDate.minusWeeks(1)
                }
                onDateSelected(newDate)
            }) {
            Text(text = "Previous")
        }
        Text(
            text = selectedDate.format(
                DateTimeFormatter.ofPattern(
                    "MMMM yyyy",
                    Locale.getDefault()
                )
            ),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        TextButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = {
                val newDate = if (isMonthView) {
                    selectedDate.plusMonths(1)
                } else {
                    selectedDate.plusWeeks(1)
                }
                onDateSelected(newDate)
            }) {
            Text(text = "Next")
        }
    }
}

@Composable
fun CalendarGrid(
    totalCells: Int,
    firstDayOfMonth: Int,
    selectedDate: LocalDate,
    logDates: List<LocalDate>,
    firstDayOfWeek: FirstDayOfWeek,
    onDateSelected: (LocalDate) -> Unit,
    isWeekView: Boolean = false
) {
    val dayOfWeekOffset = calculateDayOfWeekOffset(firstDayOfMonth, firstDayOfWeek)
    val weeksToShow = totalCells / 7

    var calendarDate = if (isWeekView) getWeekStartDate(
        selectedDate,
        firstDayOfWeek
    ) else selectedDate.withDayOfMonth(1)

    val daysInMonth = calendarDate.lengthOfMonth()
    for (week in 0 until weeksToShow) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (dayOffset in 0 until 7) {
                val cellIndex = week * 7 + dayOffset
                val day = if (isWeekView) {
                    calendarDate.dayOfMonth
                } else {
                    val calculatedDay = cellIndex - dayOfWeekOffset + 1
                    if (calculatedDay in 1..daysInMonth) calculatedDay else null
                }

                if (day != null) {
                    val isSelectedDay = isSameDay(calendarDate, selectedDate, day)
                    val logExists = logDates.contains(calendarDate.withDayOfMonth(day))
                    DayCell(
                        day = day,
                        isSelectedDay = isSelectedDay,
                        logExists = logExists,
                        onDateSelected = {
                            calendarDate = calendarDate.withDayOfMonth(day)
                            onDateSelected(calendarDate)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    if (isWeekView) {
                        calendarDate = calendarDate.plusDays(1)
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
    isSelectedDay: Boolean,
    logExists: Boolean,
    onDateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (isSelectedDay) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "backgroundColor"
    )
    val textColor by animateColorAsState(
        if (isSelectedDay) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
        label = "textColor"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onDateSelected() },
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
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (logExists) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarViewPreview() {
    MyWorkoutsTheme(primaryColor = DefaultThemeColor) {
        CalendarView(
            selectedDate = LocalDate.now(),
            logDates = listOf(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)),
            isMonthView = true,
            firstDayOfWeek = FirstDayOfWeek.MONDAY,
            onDateSelected = {}
        )
    }
}
