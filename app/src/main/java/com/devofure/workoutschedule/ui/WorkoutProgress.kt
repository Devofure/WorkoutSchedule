package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

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
        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
    }
}
