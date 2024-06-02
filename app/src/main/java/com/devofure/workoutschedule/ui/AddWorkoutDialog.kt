package com.devofure.workoutschedule.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Exercise

@Composable
fun AddWorkoutDialog(
    allExercises: List<Exercise>,
    onAddWorkout: (List<Exercise>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercises") },
        text = {
            LazyColumn {
                items(allExercises) { exercise ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                selectedExercises = if (selectedExercises.contains(exercise)) {
                                    selectedExercises - exercise
                                } else {
                                    selectedExercises + exercise
                                }
                            }
                    ) {
                        Checkbox(
                            checked = selectedExercises.contains(exercise),
                            onCheckedChange = {
                                selectedExercises = if (it) {
                                    selectedExercises + exercise
                                } else {
                                    selectedExercises - exercise
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(exercise.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddWorkout(selectedExercises) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
