package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
                    WorkoutItem(workout = workout, onWorkoutChecked = { isChecked ->
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
fun WorkoutItem(workout: Workout, onWorkoutChecked: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
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
            Column {
                Text(text = workout.name, style = MaterialTheme.typography.h6)
                Text(text = workout.description, style = MaterialTheme.typography.body2)
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
