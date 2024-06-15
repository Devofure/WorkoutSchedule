@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.getFullDayName
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MainScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    onSettingsClick: () -> Unit
) {
    val initialDaysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val daysOfWeek = remember { mutableStateListOf(*initialDaysOfWeek.toTypedArray()) }
    val nicknames = remember { mutableStateListOf(*Array(initialDaysOfWeek.size) { "" }) }
    val pagerState = rememberPagerState { daysOfWeek.size }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
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

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                BottomSheetContent(
                    daysOfWeek = daysOfWeek,
                    pagerState = pagerState,
                    nicknames = nicknames,
                    workoutViewModel = workoutViewModel,
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
            topBar = {
                TopBar(
                    onSettingsClick = onSettingsClick,
                    onCalendarClick = {
                        navController.navigate("calendar")
                    }
                )
            }
        ) { paddingValues ->
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
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val isExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded

        AnimatedVisibility(
            visible = !isExpanded,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (!isExpanded) {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
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
    }

    if (showDateConfirmationDialog) {
        DateConfirmationDialog(
            selectedDate = selectedDate,
            onConfirm = {
                selectedWorkouts.forEach { workout ->
                    workoutViewModel.logWorkout(workout, selectedDate)
                }
                showDateConfirmationDialog = false
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Workout logged!")
                }
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
