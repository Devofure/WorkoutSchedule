package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.data.Workout
import kotlinx.coroutines.launch

@Composable
fun WorkoutApp(workoutViewModel: WorkoutViewModel = viewModel()) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showAddWorkoutDialog by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddWorkoutDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Workout")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Tab(
                        text = { Text(day, modifier = Modifier.padding(8.dp)) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            val workouts by workoutViewModel.workoutsForDay(daysOfWeek[selectedTabIndex])
                .collectAsState()

            WorkoutProgress(workouts)

            LazyColumn {
                items(workouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        expanded = expandedWorkoutIds.contains(workout.id),
                        onExpandToggle = {
                            expandedWorkoutIds = if (expandedWorkoutIds.contains(workout.id)) {
                                expandedWorkoutIds - workout.id
                            } else {
                                expandedWorkoutIds + workout.id
                            }
                        },
                        onWorkoutChecked = { workoutId, isChecked ->
                            workoutViewModel.onWorkoutChecked(
                                daysOfWeek[selectedTabIndex],
                                workoutId,
                                isChecked
                            )
                        },
                        onWorkoutRemove = {
                            workoutViewModel.removeWorkout(daysOfWeek[selectedTabIndex], workout)
                        },
                        onWorkoutDetail = {
                            selectedWorkout = workout
                            showDialog = true
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
                            daysOfWeek[selectedTabIndex],
                            isChecked
                        )
                        if (isChecked) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("All workouts completed!")
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mark all as done")
            }
        }

        if (showDialog && selectedWorkout != null) {
            ShowWorkoutDetailDialog(selectedWorkout!!) {
                showDialog = false
            }
        }

        if (showAddWorkoutDialog) {
            AddWorkoutDialog(
                allExercises = workoutViewModel.allExercises.collectAsState().value,
                onAddWorkout = { selectedExercises ->
                    workoutViewModel.addWorkouts(daysOfWeek[selectedTabIndex], selectedExercises)
                    showAddWorkoutDialog = false
                },
                onDismiss = { showAddWorkoutDialog = false }
            )
        }
    }
}
