package com.devofure.workoutschedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    var setsError by remember { mutableStateOf<String?>(null) }
    var repsError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workout") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = setsError != null,
                )
                if (setsError != null) {
                    Text(
                        text = setsError ?: "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = repsError != null,
                )
                if (repsError != null) {
                    Text(
                        text = repsError ?: "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (mins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = durationError != null,
                )
                if (durationError != null) {
                    Text(
                        text = durationError ?: "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                var valid = true

                val setsValue = sets.toIntOrNull()
                if (sets.isNotBlank() && setsValue == null) {
                    setsError = "Invalid number"
                    valid = false
                } else {
                    setsError = null
                }

                val repsValue = reps.toIntOrNull()
                if (reps.isNotBlank() && repsValue == null) {
                    repsError = "Invalid number"
                    valid = false
                } else {
                    repsError = null
                }

                val durationValue = duration.toIntOrNull()
                if (duration.isNotBlank() && durationValue == null) {
                    durationError = "Invalid number"
                    valid = false
                } else {
                    durationError = null
                }

                if (valid) {
                    val updatedWorkout = workout.copy(
                        sets = setsValue,
                        reps = repsValue,
                        duration = durationValue
                    )
                    onSave(updatedWorkout)
                    onDismiss()
                }
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
