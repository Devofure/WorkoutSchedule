package com.devofure.workoutschedule.ui.calendar

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.data.LogEntity
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.main.getDaysInMonth
import com.devofure.workoutschedule.ui.main.getFirstDayOfMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(navController: NavHostController, workoutViewModel: WorkoutViewModel) {
    var selectedDate by remember { mutableStateOf(Date()) }
    val logs by workoutViewModel.getLogsForDate(selectedDate).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                CalendarView(selectedDate) { date ->
                    selectedDate = date
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (logs.isEmpty()) {
                    Text("No logs for this day", style = MaterialTheme.typography.h6)
                } else {
                    LazyColumn {
                        items(logs) { log ->
                            LogItem(log)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CalendarView(selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfMonth = getFirstDayOfMonth(year, month)
    val totalCells = daysInMonth + firstDayOfMonth

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, -1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
            Text(text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time))
            IconButton(onClick = {
                calendar.add(Calendar.MONTH, 1)
                onDateSelected(calendar.time)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
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
                                color = if (isSameDay(calendar, day)) Color.White else Color.Black
                            )
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

@Composable
fun LogItem(log: LogEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Workout ID: ${log.workoutId}")
            Text("Date: ${log.date}")
            Text("Day of Week: ${log.dayOfWeek}")
        }
    }
}

private fun isSameDay(calendar: Calendar, day: Int): Boolean {
    return calendar.get(Calendar.DAY_OF_MONTH) == day
}
