@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.data.WEEK
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.getDayName
import java.time.LocalDate

@Composable
fun MainScreen(
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    navigate: Navigate,
    dayNamingPreference: DayOfWeek.DayNamingPreference,
) {
    val pagerState = rememberPagerState { WEEK.size }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showEditNicknameDialog by remember { mutableStateOf(false) }
    var showDateConfirmationDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var editedNickname by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val deleteEvent by workoutViewModel.deleteEvent.collectAsState()

    LaunchedEffect(deleteEvent) {
        deleteEvent?.let { (workout, dayIndex) ->
            val result = snackbarHostState.showSnackbar(
                message = "Workout removed",
                actionLabel = "Undo"
            )
            if (result == SnackbarResult.ActionPerformed) {
                workoutViewModel.undoRemoveWorkout(workout, dayIndex)
            }
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                PagerIndicator(
                    pagerState = pagerState,
                    pageCount = WEEK.size,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val dayOfWeek = WEEK[page]
                    val workouts by workoutViewModel.workoutsForDay(dayOfWeek).collectAsState()
                    val dayNickname = workoutViewModel.getNickname(dayOfWeek.dayIndex)
                    val dayName = getDayName(
                        dayOfWeek,
                        dayNamingPreference,
                        dayNickname
                    )
                    LaunchedEffect(workouts) {
                        editedNickname = dayNickname
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
                                text = dayName,
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
                                                dayOfWeek.dayIndex,
                                                workoutId,
                                                isChecked
                                            )
                                        },
                                        itemMoreMenu = mapOf(
                                            "Details" to {
                                                sharedViewModel.selectWorkout(workout)
                                                navigate.to(Route.WorkoutDetail)
                                            },
                                            "Edit" to {
                                                sharedViewModel.selectWorkout(workout)
                                                navigate.to(Route.EditWorkout(dayName))
                                            },
                                            "Remove" to {
                                                workoutViewModel.removeWorkout(
                                                    dayOfWeek.dayIndex,
                                                    workout,
                                                )
                                            }
                                        ),
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
            val workouts = workoutViewModel.workoutsForDay(WEEK[pagerState.currentPage]).collectAsState().value
            val hasWorkouts = workouts.isNotEmpty()
            val hasFinishedWorkouts = workouts.any { it.isDone }

            BottomSheetContent(
                showEditDialogDayNickname = { showEditNicknameDialog = true },
                showLogWorkoutDay = { showDateConfirmationDialog = true },
                navigate = navigate,
                dayOfWeek = WEEK[pagerState.currentPage],
                checkAllWorkouts = workoutViewModel::onAllWorkoutsChecked,
                hasFinishedWorkouts = hasFinishedWorkouts,
                hasWorkouts = hasWorkouts
            )
        }
    }

    if (showDateConfirmationDialog) {
        DateConfirmationDialog(
            selectedDate = selectedDate,
            onConfirm = {
                val checkedWorkouts =
                    workoutViewModel.getCheckedWorkoutsForDay(WEEK[pagerState.currentPage].dayIndex)
                checkedWorkouts.forEach { workout ->
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
        val day = WEEK[pagerState.currentPage]
        EditNicknameDialog(
            editedNickname = editedNickname,
            onNicknameChange = { editedNickname = it },
            dayNamingPreference = dayNamingPreference,
            dayOfWeek = day,
            save = { dayOfWeek ->
                workoutViewModel.saveNicknames(dayOfWeek.dayIndex, editedNickname)
                showEditNicknameDialog = false
            },
            onDismiss = {
                editedNickname = ""
                showEditNicknameDialog = false
            }
        )
    }
}