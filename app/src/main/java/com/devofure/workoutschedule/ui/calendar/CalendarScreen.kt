package com.devofure.workoutschedule.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.FirstDayOfWeek
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.main.WorkoutItem
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    navigate: Navigate,
    firstDayOfWeek: FirstDayOfWeek,
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isMonthView by remember { mutableStateOf(true) }
    val logs by calendarViewModel.getLogsForDate(selectedDate).collectAsState(initial = emptyList())
    val currentYearMonth =
        remember { mutableStateOf(Pair(selectedDate.year, selectedDate.monthValue)) }
    val logDatesForMonth by calendarViewModel.getLogDatesForMonth(
        currentYearMonth.value.first,
        currentYearMonth.value.second
    ).collectAsState(initial = emptyList())

    val deleteEvent by calendarViewModel.deleteEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteEvent) {
        deleteEvent?.let { log ->
            val result = snackbarHostState.showSnackbar(
                message = "Log deleted",
                actionLabel = "Undo"
            )
            if (result == SnackbarResult.ActionPerformed) {
                calendarViewModel.undoDeleteLog(log)
            }
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Calendar") },
                navigationIcon = {
                    IconButton(onClick = { navigate.back() }) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedDate = LocalDate.now()
                        currentYearMonth.value = Pair(selectedDate.year, selectedDate.monthValue)
                    }) {
                        Icon(Icons.Filled.Today, contentDescription = "Today")
                    }
                    IconButton(onClick = { isMonthView = !isMonthView }) {
                        Icon(
                            imageVector =
                            if (isMonthView) Icons.Filled.ViewWeek
                            else Icons.Filled.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                CalendarView(selectedDate, logDatesForMonth, isMonthView, firstDayOfWeek) { date ->
                    selectedDate = date
                    currentYearMonth.value = Pair(date.year, date.monthValue)
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
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }
                    LazyColumn {
                        items(logs) { log ->
                            val workout = calendarViewModel.getWorkoutByName(log)
                            workout?.let {
                                WorkoutItem(
                                    workout = workout,
                                    hideInstruction = true,
                                    expanded = expandedWorkoutIds.contains(workout.id),
                                    onClick = {
                                        expandedWorkoutIds =
                                            if (expandedWorkoutIds.contains(workout.id)) {
                                                expandedWorkoutIds - workout.id
                                            } else {
                                                expandedWorkoutIds + workout.id
                                            }
                                    },
                                    itemMoreMenu = mapOf(
                                        "Edit" to {
                                            //navigate.to(Route.EditLog(workout.id))
                                        },
                                        "Delete" to {
                                            calendarViewModel.deleteLog(log)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun CalendarScreenPreview() {
    val navigate = Navigate(rememberNavController())
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        CalendarScreen(
            calendarViewModel = viewModel(),
            navigate = navigate,
            firstDayOfWeek = FirstDayOfWeek.MONDAY
        )
    }
}
