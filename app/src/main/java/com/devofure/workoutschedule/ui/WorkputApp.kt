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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Show dialog to add a workout */ }) {
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

            val workouts by workoutViewModel.workoutsForDay(daysOfWeek[selectedTabIndex]).collectAsState()

            WorkoutProgress(workouts)

            LazyColumn {
                items(workouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        onWorkoutChecked = { isChecked ->
                            workoutViewModel.onWorkoutChecked(daysOfWeek[selectedTabIndex], workout, isChecked)
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
                        workoutViewModel.onAllWorkoutsChecked(daysOfWeek[selectedTabIndex], isChecked)
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
            showWorkoutDetailDialog(selectedWorkout!!) {
                showDialog = false
            }
        }
    }
}

@Composable
fun WorkoutItem(
    workout: Workout,
    onWorkoutChecked: (Boolean) -> Unit,
    onWorkoutRemove: () -> Unit,
    onWorkoutDetail: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded },
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
                    onCheckedChange = onWorkoutChecked
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = workout.name, style = MaterialTheme.typography.h6)
                    Text(text = workout.description, style = MaterialTheme.typography.body2)
                    if (expanded) {
                        Text(text = "Instructions: ${workout.instructions.joinToString(" ")}")
                    }
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
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
    }
}

@Composable
fun showWorkoutDetailDialog(workout: Workout, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = workout.name) },
        text = {
            Column {
                Text(text = workout.description)
                Text(text = "Equipment: ${workout.equipment}")
                Text(text = "Primary Muscles: ${workout.primaryMuscles.joinToString(", ")}")
                Text(text = "Secondary Muscles: ${workout.secondaryMuscles.joinToString(", ")}")
                Text(text = "Instructions: ${workout.instructions.joinToString(" ")}")
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
        Text(text = "Progress: $completedWorkouts / $totalWorkouts", style = MaterialTheme.typography.body1)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colors.primary
        )
    }
}
