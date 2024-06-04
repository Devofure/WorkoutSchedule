@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    onSettingsClick: () -> Unit
) {
    val initialDaysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var daysOfWeek by remember { mutableStateOf(initialDaysOfWeek.toMutableList()) }
    var nicknames by remember { mutableStateOf(MutableList(initialDaysOfWeek.size) { "" }) }
    val pagerState = rememberPagerState { daysOfWeek.size }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editedNickname by remember { mutableStateOf("") }
    var showEditNicknameDialog by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colors.primary,
        darkIcons = useDarkIcons
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(
                onMarkAllAsDone = {
                    val dayFullName = getFullDayName(
                        daysOfWeek[pagerState.currentPage],
                        nicknames[pagerState.currentPage]
                    )
                    workoutViewModel.onAllWorkoutsChecked(dayFullName, true)
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("All workouts completed!")
                    }
                },
                onLogDay = { showDatePicker = true },
                onAddExercise = {
                    val dayFullName = getFullDayName(
                        daysOfWeek[pagerState.currentPage],
                        nicknames[pagerState.currentPage]
                    )
                    navController.navigate("add_exercise/$dayFullName")
                },
                onEditNickname = {
                    editedNickname = nicknames[pagerState.currentPage]
                    showEditNicknameDialog = true
                }
            )
        },
        sheetPeekHeight = 0.dp,
        floatingActionButton = {
            val isExpanded = scaffoldState.bottomSheetState.isExpanded
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    if (isExpanded) {
                        scaffoldState.bottomSheetState.collapse()
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }) {
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 0f else 0f,
                    animationSpec = tween(durationMillis = 150),
                    label = "",
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.MoreVert,
                    contentDescription = if (isExpanded) "Close Options" else "More Options",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
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
                )
                    .collectAsState()

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
                            style = MaterialTheme.typography.h6
                        )
                    }

                    if (workouts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Rest day",
                                style = MaterialTheme.typography.h4,
                                color = Color.Gray
                            )
                        }
                    } else {
                        WorkoutProgress(workouts)
                        LazyColumn {
                            items(workouts) { workout ->
                                WorkoutItem(
                                    workout = workout,
                                    expanded = expandedWorkoutIds.contains(workout.id),
                                    onExpandToggle = {
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
                                        navController.navigate("workout_detail")
                                    },
                                    onWorkoutEdit = {
                                        sharedViewModel.selectWorkout(workout)
                                        navController.navigate(
                                            "edit_workout/${
                                                getFullDayName(
                                                    daysOfWeek[page],
                                                    nicknames[page]
                                                )
                                            }"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        ShowDatePickerDialog(
            onDateSelected = { date ->
                // Handle the selected date here, e.g., log the day
                showDatePicker = false
            },
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }

    if (showEditNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNicknameDialog = false },
            title = { Text("Edit Nickname") },
            text = {
                TextField(
                    value = editedNickname,
                    onValueChange = { editedNickname = it },
                    label = { Text("Nickname") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        nicknames[pagerState.currentPage] = editedNickname
                        showEditNicknameDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNicknameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
