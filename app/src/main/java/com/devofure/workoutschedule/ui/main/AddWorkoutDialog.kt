// AddWorkoutScreen.kt
package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.clickable
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
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Exercise

@Composable
fun AddWorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    onAddWorkout: (List<Exercise>) -> Unit,
    onBack: () -> Unit
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        workoutViewModel.searchExercises(searchQuery)
    }

    val filteredExercises by workoutViewModel.filteredExercises.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Exercises") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onAddWorkout(selectedExercises) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
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
    }
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
