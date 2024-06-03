package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

@Composable
fun ShowWorkoutDetailScreen(workout: Workout, onEdit: () -> Unit, onDismiss: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = workout.exercise.name, style = MaterialTheme.typography.h6) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                workout.repsList?.let {
                    Column {
                        Text(
                            text = "Reps per Set",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.primary
                        )
                        it.forEachIndexed { index, reps ->
                            Text(
                                text = "Set ${index + 1}: $reps reps",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
                workout.duration?.let {
                    DetailItem(label = "Duration", value = "$it mins")
                }
                if (workout.exercise.equipment?.isNotEmpty() == true) {
                    DetailItem(label = "Equipment", value = workout.exercise.equipment)
                }
                if (workout.exercise.primaryMuscles.isNotEmpty()) {
                    DetailItem(
                        label = "Primary Muscles",
                        value = workout.exercise.primaryMuscles.joinToString(", ")
                    )
                }
                if (workout.exercise.secondaryMuscles.isNotEmpty()) {
                    DetailItem(
                        label = "Secondary Muscles",
                        value = workout.exercise.secondaryMuscles.joinToString(", ")
                    )
                }
                if (workout.exercise.instructions.isNotEmpty()) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        workout.exercise.instructions.forEachIndexed { _, instruction ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "\u2022", // Bullet point
                                    style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = instruction,
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.body2, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.body1)
        }
    }
}
