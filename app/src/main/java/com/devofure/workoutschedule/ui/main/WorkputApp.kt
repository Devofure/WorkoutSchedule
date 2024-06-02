// WorkoutApp.kt
package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.data.Workout
import kotlinx.coroutines.launch

@Composable
fun WorkoutApp(
    workoutViewModel: WorkoutViewModel = viewModel(),
    onSettingsClick: () -> Unit
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showEditWorkoutDialog by remember { mutableStateOf(false) }
    var showAddWorkoutDialog by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
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
                        },
                        onWorkoutEdit = {
                            selectedWorkout = workout
                            showEditWorkoutDialog = true
                        } // Pass the edit function
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
                    },
                    modifier = Modifier.testTag("markAllAsDoneCheckbox")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mark all as done")
            }
        }

        if (showDialog && selectedWorkout != null) {
            ShowWorkoutDetailDialog(
                workout = selectedWorkout!!,
                onEdit = {
                    showDialog = false
                    showEditWorkoutDialog = true
                },
                onDismiss = { showDialog = false }
            )
        }

        if (showEditWorkoutDialog && selectedWorkout != null) {
            EditWorkoutDialog(
                workout = selectedWorkout!!,
                onSave = { updatedWorkout ->
                    workoutViewModel.updateWorkout(daysOfWeek[selectedTabIndex], updatedWorkout)
                    showEditWorkoutDialog = false
                },
                onDismiss = { showEditWorkoutDialog = false }
            )
        }

        if (showAddWorkoutDialog) {
            AddWorkoutDialog(
                workoutViewModel = workoutViewModel,
                onAddWorkout = { selectedExercises ->
                    workoutViewModel.addWorkouts(daysOfWeek[selectedTabIndex], selectedExercises)
                    showAddWorkoutDialog = false
                },
                onDismiss = { showAddWorkoutDialog = false }
            )
        }
    }
}
