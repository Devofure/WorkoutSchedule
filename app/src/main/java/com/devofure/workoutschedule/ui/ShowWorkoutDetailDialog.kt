package com.devofure.workoutschedule.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import com.devofure.workoutschedule.data.Workout

@Composable
fun ShowWorkoutDetailDialog(workout: Workout, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = workout.exercise.name) },
        text = {
            Column {
                Text(text = workout.exercise.name)
                Text(text = "Sets: ${workout.sets ?: "N/A"}")
                Text(text = "Reps: ${workout.reps ?: "N/A"}")
                Text(text = "Duration: ${workout.duration ?: "N/A"} mins")
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
