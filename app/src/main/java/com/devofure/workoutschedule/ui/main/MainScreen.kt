@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    onSettingsClick: () -> Unit
) {
    val initialDaysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val daysOfWeek by remember { mutableStateOf(initialDaysOfWeek.toMutableList()) }
    val nicknames by remember { mutableStateOf(MutableList(initialDaysOfWeek.size) { "" }) }
    val pagerState = rememberPagerState { daysOfWeek.size }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editedNickname by remember { mutableStateOf("") }
    var showEditNicknameDialog by remember { mutableStateOf(false) }
    var showDateConfirmationDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }

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
                daysOfWeek = daysOfWeek,
                pagerState = pagerState,
                nicknames = nicknames,
                workoutViewModel = workoutViewModel,
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
                navController = navController,
                onEditNickname = {
                    editedNickname = nicknames[pagerState.currentPage]
                    showEditNicknameDialog = true
                },
                onLogDay = {
                    showDateConfirmationDialog = true
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
            TopBar(
                onSettingsClick = onSettingsClick,
                onCalendarClick = {
                    navController.navigate("calendar")
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

    if (showDateConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDateConfirmationDialog = false },
            title = { Text("Confirm Log Date") },
            text = {
                Column {
                    Text(
                        "Do you want to log the workout for ${
                            SimpleDateFormat(
                                "EEEE, MMMM d",
                                Locale.getDefault()
                            ).format(selectedDate)
                        }?"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        Text("Pick another date")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Log the workout
                        selectedWorkouts.forEach { workout ->
                            workoutViewModel.logWorkout(workout, selectedDate)
                        }
                        showDateConfirmationDialog = false
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Workout logged!")
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
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
