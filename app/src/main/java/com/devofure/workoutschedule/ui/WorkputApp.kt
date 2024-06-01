package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.data.Workout

@Composable
fun WorkoutApp(workoutViewModel: WorkoutViewModel = viewModel()) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Workout Schedule",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            daysOfWeek.forEachIndexed { index, day ->
                Tab(
                    text = { Text(day) },
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
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Checkbox(
                checked = allChecked,
                onCheckedChange = { isChecked ->
                    workoutViewModel.onAllWorkoutsChecked(daysOfWeek[selectedTabIndex], isChecked)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Mark all as done")
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout, onWorkoutChecked: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
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
