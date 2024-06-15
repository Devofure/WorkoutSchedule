@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.getFullDayName
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDate

@Composable
fun MainScreen(
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    navigate: Navigate
) {
    val initialDaysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val daysOfWeek = remember { mutableStateListOf(*initialDaysOfWeek.toTypedArray()) }
    val nicknames = remember { mutableStateListOf(*Array(initialDaysOfWeek.size) { "" }) }
    val pagerState = rememberPagerState { daysOfWeek.size }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editedNickname by remember { mutableStateOf("") }
    var showEditNicknameDialog by remember { mutableStateOf(false) }
    var showDateConfirmationDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.primary,
        darkIcons = useDarkIcons
    )

    Scaffold(
        topBar = {
            TopBar(
                onSettingsClick = { navigate.to(Route.Settings) },
                onCalendarClick = { navigate.to(Route.Calendar) },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = !showBottomSheet) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Options"
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                PagerIndicator(
                    pagerState = pagerState,
                    pageCount = daysOfWeek.size,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val workouts by workoutViewModel.workoutsForDay(
                        getFullDayName(
                            daysOfWeek[page],
                            nicknames[page]
                        )
                    ).collectAsState()

                    LaunchedEffect(workouts) {
                        selectedWorkouts = workouts
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = getFullDayName(daysOfWeek[page], nicknames[page]),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (workouts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Rest day",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            WorkoutProgress(workouts)
                            LazyColumn(contentPadding = PaddingValues(top = 16.dp)) {
                                items(workouts) { workout ->
                                    WorkoutItem(
                                        workout = workout,
                                        expanded = expandedWorkoutIds.contains(workout.id),
                                        onClick = {
                                            expandedWorkoutIds =
                                                if (expandedWorkoutIds.contains(workout.id)) {
                                                    expandedWorkoutIds - workout.id
                                                } else {
                                                    expandedWorkoutIds + workout.id
                                                }
                                        },
                                        onWorkoutChecked = { workoutId, isChecked ->
                                            workoutViewModel.onWorkoutChecked(
                                                getFullDayName(daysOfWeek[page], nicknames[page]),
                                                workoutId,
                                                isChecked
                                            )
                                        },
                                        onWorkoutRemove = {
                                            workoutViewModel.removeWorkout(
                                                getFullDayName(daysOfWeek[page], nicknames[page]),
                                                workout
                                            )
                                        },
                                        onWorkoutDetail = {
                                            sharedViewModel.selectWorkout(workout)
                                            navigate.to(Route.WorkoutDetail)
                                        },
                                        onWorkoutEdit = {
                                            sharedViewModel.selectWorkout(workout)
                                            navigate.to(
                                                Route.EditWorkout(
                                                    getFullDayName(
                                                        daysOfWeek[page],
                                                        nicknames[page]
                                                    )
                                                )
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            BottomSheetContent(
                daysOfWeek = daysOfWeek,
                pagerState = pagerState,
                nicknames = nicknames,
                workoutViewModel = workoutViewModel,
                onEditNickname = {
                    editedNickname = nicknames[pagerState.currentPage]
                    showEditNicknameDialog = true
                },
                onLogDay = {
                    showDateConfirmationDialog = true
                },
                navigate = navigate
            )
        }
    }

    if (showDateConfirmationDialog) {
        DateConfirmationDialog(
            selectedDate = selectedDate,
            onConfirm = {
                selectedWorkouts.forEach { workout ->
                    workoutViewModel.logWorkout(workout, selectedDate)
                }
                showDateConfirmationDialog = false
                showBottomSheet = false
            },
            onDismiss = { showDateConfirmationDialog = false },
            onPickAnotherDate = { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        ShowDatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
                showDateConfirmationDialog = true
            },
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }

    if (showEditNicknameDialog) {
        EditNicknameDialog(
            editedNickname = editedNickname,
            onNicknameChange = { editedNickname = it },
            onSave = {
                nicknames[pagerState.currentPage] = editedNickname
                showEditNicknameDialog = false
            },
            onDismiss = { showEditNicknameDialog = false }
        )
    }
}
