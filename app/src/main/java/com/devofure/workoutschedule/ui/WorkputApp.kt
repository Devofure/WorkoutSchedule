package com.devofure.workoutschedule.ui

import android.app.Application
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.viewModelFactory
import com.devofure.workoutschedule.data.Workout
import kotlinx.coroutines.launch

@Composable
fun WorkoutApp(workoutViewModel: WorkoutViewModel = viewModel()) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle add workout */ }) {
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
                    ExpandableWorkoutItem(workout = workout, onWorkoutChecked = { isChecked ->
                        workoutViewModel.onWorkoutChecked(daysOfWeek[selectedTabIndex], workout, isChecked)
                    })
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
    }
}

@Composable
fun ExpandableWorkoutItem(workout: Workout, onWorkoutChecked: (Boolean) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = workout.isDone,
                    onCheckedChange = onWorkoutChecked
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = workout.name, style = MaterialTheme.typography.h6)
            }
            if (expanded) {
                Text(text = workout.description, style = MaterialTheme.typography.body2)
                // Add more detailed information here
                Text(text = "Force: ${workout.force}", style = MaterialTheme.typography.body2)
                Text(text = "Level: ${workout.level}", style = MaterialTheme.typography.body2)
                Text(text = "Mechanic: ${workout.mechanic}", style = MaterialTheme.typography.body2)
                Text(text = "Equipment: ${workout.equipment}", style = MaterialTheme.typography.body2)
                Text(text = "Primary Muscles: ${workout.primaryMuscles.joinToString()}", style = MaterialTheme.typography.body2)
                if (workout.secondaryMuscles.isNotEmpty()) {
                    Text(text = "Secondary Muscles: ${workout.secondaryMuscles.joinToString()}", style = MaterialTheme.typography.body2)
                }
                workout.instructions.forEachIndexed { index, instruction ->
                    Text(text = "${index + 1}. $instruction", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
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
