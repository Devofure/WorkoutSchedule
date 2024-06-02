package com.devofure.workoutschedule.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.Exercise
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

@Composable
fun WorkoutItem(
    workout: Workout,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onWorkoutChecked: (Int, Boolean) -> Unit,
    onWorkoutRemove: () -> Unit,
    onWorkoutDetail: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onExpandToggle() },
        elevation = 4.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = workout.isDone,
                    onCheckedChange = { isChecked -> onWorkoutChecked(workout.id, isChecked) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = workout.exercise.name, style = MaterialTheme.typography.h6)
                    Text(
                        text = when {
                            workout.sets != null && workout.reps != null -> "${workout.sets} sets of ${workout.reps} reps"
                            workout.duration != null -> "Duration: ${workout.duration} mins"
                            else -> ""
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-16).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(onClick = {
                            showMenu = false
                            onWorkoutDetail()
                        }) {
                            Text("Details")
                        }
                        DropdownMenuItem(onClick = {
                            showMenu = false
                            onWorkoutRemove()
                        }) {
                            Text("Remove")
                        }
                    }
                }
            }
            if (expanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = workout.exercise.instructions.joinToString(" "),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
fun ShowWorkoutDetailDialog(workout: Workout, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = workout.exercise.name) },
        text = {
            Column {
                Text(text = workout.exercise.name)
                Text(text = "Sets: ${workout.sets}")
                Text(text = "Reps: ${workout.reps}")
                Text(text = "Equipment: ${workout.exercise.equipment}")
                Text(text = "Primary Muscles: ${workout.exercise.primaryMuscles.joinToString(", ")}")
                Text(text = "Secondary Muscles: ${workout.exercise.secondaryMuscles.joinToString(", ")}")
                Text(text = "Instructions: ${workout.exercise.instructions.joinToString(" ")}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun WorkoutProgress(workouts: List<Workout>) {
    val completedWorkouts = workouts.count { it.isDone }
    val totalWorkouts = workouts.size
    val progress = if (totalWorkouts > 0) {
        completedWorkouts / totalWorkouts.toFloat()
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Progress: $completedWorkouts / $totalWorkouts",
            style = MaterialTheme.typography.body1
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun AddWorkoutDialog(
    allExercises: List<Exercise>,
    onAddWorkout: (List<Exercise>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercises") },
        text = {
            LazyColumn {
                items(allExercises) { exercise ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                selectedExercises = if (selectedExercises.contains(exercise)) {
                                    selectedExercises - exercise
                                } else {
                                    selectedExercises + exercise
                                }
                            }
                    ) {
                        Checkbox(
                            checked = selectedExercises.contains(exercise),
                            onCheckedChange = {
                                selectedExercises = if (it) {
                                    selectedExercises + exercise
                                } else {
                                    selectedExercises - exercise
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(exercise.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddWorkout(selectedExercises) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
