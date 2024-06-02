package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                ValidatedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = "Sets",
                    error = setsError,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                ValidatedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = "Reps",
                    error = repsError,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                ValidatedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = "Duration (mins)",
                    error = durationError,
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val validationResult = validateWorkoutInput(sets, reps, duration)
                setsError = validationResult.setsError
                repsError = validationResult.repsError
                durationError = validationResult.durationError

                if (validationResult.isValid) {
                    val updatedWorkout = workout.copy(
                        sets = sets.toIntOrNull(),
                        reps = reps.toIntOrNull(),
                        duration = duration.toIntOrNull()
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

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

fun validateWorkoutInput(sets: String, reps: String, duration: String): ValidationResult {
    var isValid = true
    var setsError: String? = null
    var repsError: String? = null
    var durationError: String? = null

    if (sets.isNotBlank() && sets.toIntOrNull() == null) {
        setsError = "Invalid number"
        isValid = false
    }

    if (reps.isNotBlank() && reps.toIntOrNull() == null) {
        repsError = "Invalid number"
        isValid = false
    }

    if (duration.isNotBlank() && duration.toIntOrNull() == null) {
        durationError = "Invalid number"
        isValid = false
    }

    return ValidationResult(isValid, setsError, repsError, durationError)
}

data class ValidationResult(
    val isValid: Boolean,
    val setsError: String?,
    val repsError: String?,
    val durationError: String?
)

