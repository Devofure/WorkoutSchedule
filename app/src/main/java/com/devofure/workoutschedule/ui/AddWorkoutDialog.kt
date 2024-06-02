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
    workoutViewModel: WorkoutViewModel,
    onAddWorkout: (List<Exercise>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        workoutViewModel.searchExercises(searchQuery)
    }

    val filteredExercises by workoutViewModel.filteredExercises.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercises") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Exercises") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(filteredExercises) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            isSelected = selectedExercises.contains(exercise),
                            onSelected = {
                                selectedExercises = if (selectedExercises.contains(exercise)) {
                                    selectedExercises - exercise
                                } else {
                                    selectedExercises + exercise
                                }
                            }
                        )
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

@Composable
fun ExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelected() }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelected() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = exercise.name, style = MaterialTheme.typography.subtitle1)
            Text(text = "Equipment: ${exercise.equipment}", style = MaterialTheme.typography.body2)
            Text(text = "Primary Muscles: ${exercise.primaryMuscles.joinToString(", ")}", style = MaterialTheme.typography.body2)
            Text(text = "Secondary Muscles: ${exercise.secondaryMuscles.joinToString(", ")}", style = MaterialTheme.typography.body2)
        }
    }
}
