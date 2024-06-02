package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

@Composable
fun EditWorkoutDialog(
    workout: Workout,
    onSave: (Workout) -> Unit,
    onDismiss: () -> Unit
) {
    var sets by remember { mutableStateOf(workout.sets?.toString() ?: "") }
    var reps by remember { mutableStateOf(workout.reps?.toString() ?: "") }
    var duration by remember { mutableStateOf(workout.duration?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workout") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (mins)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedWorkout = workout.copy(
                    sets = sets.toIntOrNull(),
                    reps = reps.toIntOrNull(),
                    duration = duration.toIntOrNull()
                )
                onSave(updatedWorkout)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
