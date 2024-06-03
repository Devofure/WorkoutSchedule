@file:OptIn(ExperimentalFoundationApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.data.Workout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun WorkoutApp(
    workoutViewModel: WorkoutViewModel = viewModel(),
    onSettingsClick: () -> Unit
) {
    val daysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val pagerState = rememberPagerState { daysOfWeek.size }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    var showEditWorkoutScreen by remember { mutableStateOf(false) }
    var showAddWorkoutScreen by remember { mutableStateOf(false) }
    var showWorkoutDetailScreen by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colors.primary,
        darkIcons = useDarkIcons
    )

    when {
        showAddWorkoutScreen -> {
            AddWorkoutScreen(
                workoutViewModel = workoutViewModel,
                day = daysOfWeek[pagerState.currentPage],
                onAddWorkout = { selectedExercises ->
                    workoutViewModel.addWorkouts(
                        daysOfWeek[pagerState.currentPage],
                        selectedExercises
                    )
                    showAddWorkoutScreen = false
                },
                onBack = { showAddWorkoutScreen = false }
            )
        }

        showWorkoutDetailScreen && selectedWorkout != null -> {
            ShowWorkoutDetailScreen(
                workout = selectedWorkout!!,
                onDismiss = { showWorkoutDetailScreen = false }
            )
        }

        showEditWorkoutScreen && selectedWorkout != null -> {
            EditWorkoutScreen(
                workout = selectedWorkout!!,
                onSave = { updatedWorkout ->
                    workoutViewModel.updateWorkout(
                        daysOfWeek[pagerState.currentPage],
                        updatedWorkout
                    )
                    showEditWorkoutScreen = false
                },
                onDismiss = { showEditWorkoutScreen = false }
            )
        }

        else -> {
            Scaffold(
                scaffoldState = scaffoldState,
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddWorkoutScreen = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Workout")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    Column {
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
                            val workouts by workoutViewModel.workoutsForDay(daysOfWeek[page])
                                .collectAsState()
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(daysOfWeek[page], style = MaterialTheme.typography.h6)

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
                                                        daysOfWeek[page],
                                                        workoutId,
                                                        isChecked
                                                    )
                                                },
                                                onWorkoutRemove = {
                                                    workoutViewModel.removeWorkout(
                                                        daysOfWeek[page],
                                                        workout
                                                    )
                                                },
                                                onWorkoutDetail = {
                                                    selectedWorkout = workout
                                                    showWorkoutDetailScreen = true
                                                },
                                                onWorkoutEdit = {
                                                    selectedWorkout = workout
                                                    showEditWorkoutScreen = true
                                                }
                                            )
                                        }
                                    }
                                    val allChecked = workouts.all { it.isDone }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(vertical = 16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Checkbox(
                                            checked = allChecked,
                                            onCheckedChange = { isChecked ->
                                                workoutViewModel.onAllWorkoutsChecked(
                                                    daysOfWeek[page],
                                                    isChecked
                                                )
                                                if (isChecked) {
                                                    coroutineScope.launch {
                                                        scaffoldState.snackbarHostState.showSnackbar(
                                                            "All workouts completed!"
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier.testTag("markAllAsDoneCheckbox")
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Mark all as done")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            }
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}
