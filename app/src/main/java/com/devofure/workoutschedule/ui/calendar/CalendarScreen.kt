package com.devofure.workoutschedule.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.main.WorkoutItem
import com.devofure.workoutschedule.ui.settings.SettingsViewModel
import java.time.LocalDate

@Composable
fun CalendarScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    settingsViewModel: SettingsViewModel
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isMonthView by remember { mutableStateOf(true) }
    val logs by workoutViewModel.getLogsForDate(selectedDate).collectAsState(initial = emptyList())
    val firstDayOfWeek by settingsViewModel.firstDayOfWeek.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { selectedDate = LocalDate.now() }) {
                        Icon(Icons.Filled.Today, contentDescription = "Today")
                    }
                    IconButton(onClick = { isMonthView = !isMonthView }) {
                        Icon(
                            imageVector = if (isMonthView) Icons.Filled.ViewWeek else Icons.Filled.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .fillMaxWidth()
            ) {
                CalendarView(selectedDate, logs, isMonthView, firstDayOfWeek) { date ->
                    selectedDate = date
                }
                if (logs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No logs for this day",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    LazyColumn {
                        items(logs) { log ->
                            val workout = workoutViewModel.getWorkoutByName(log)
                            workout?.let {
                                WorkoutItem(
                                    workout = workout,
                                    expanded = false,
                                    onClick = {},
                                    onWorkoutRemove = {},
                                    onWorkoutDetail = {},
                                    onWorkoutEdit = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
