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
                    SectionHeader(title = "Reps per Set")
                    it.forEachIndexed { index, reps ->
                        Text(
                            text = "Set ${index + 1}: $reps reps",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                    SectionHeader(title = "Instructions")
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
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row {
        Column {
            SectionHeader(title = label)
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
