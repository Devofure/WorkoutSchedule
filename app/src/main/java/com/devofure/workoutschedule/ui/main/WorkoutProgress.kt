package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

@Composable
fun WorkoutProgress(workouts: List<Workout>) {
    val completedWorkouts = workouts.count { it.isDone }
    val totalWorkouts = workouts.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Progress: $completedWorkouts / $totalWorkouts",
            style = MaterialTheme.typography.bodyLarge
        )
        LinearProgressIndicator(
            progress = {
                if (totalWorkouts > 0) {
                    completedWorkouts / totalWorkouts.toFloat()
                } else {
                    0f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.background,
            strokeCap = StrokeCap.Round
        )
    }
}
