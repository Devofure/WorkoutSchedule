package com.devofure.workoutschedule.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
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
fun CalendarView(selectedDate: Date, logs: List<LogEntity>, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfMonth = getFirstDayOfMonth(year, month)
    val totalCells = daysInMonth + firstDayOfMonth

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, -1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                style = MaterialTheme.typography.h6
            )
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, 1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        for (i in 0 until totalCells step 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in i until i + 7) {
                    val day = j - firstDayOfMonth + 1
                    if (j >= firstDayOfMonth && day <= daysInMonth) {
                        val logExists = logs.any { log ->
                            val logDate = dateFormat.parse(log.date)
                            val logCalendar = Calendar.getInstance()
                            logCalendar.time = logDate!!
                            logCalendar.get(Calendar.DAY_OF_MONTH) == day &&
                                    logCalendar.get(Calendar.MONTH) == month &&
                                    logCalendar.get(Calendar.YEAR) == year
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .background(
                                    if (isSameDay(
                                            calendar,
                                            day
                                        )
                                    ) MaterialTheme.colors.primary else Color.Transparent
                                )
                                .clickable {
                                    calendar.set(Calendar.DAY_OF_MONTH, day)
                                    onDateSelected(calendar.time)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isSameDay(calendar, day)) Color.White else Color.Black,
                                style = MaterialTheme.typography.body1
                            )
                            if (logExists) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .size(8.dp)
                                        .background(
                                            MaterialTheme.colors.secondary,
                                            MaterialTheme.shapes.small
                                        )
                                )
                            }
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
}

private fun isSameDay(calendar: Calendar, day: Int): Boolean {
    return calendar.get(Calendar.DAY_OF_MONTH) == day
}
