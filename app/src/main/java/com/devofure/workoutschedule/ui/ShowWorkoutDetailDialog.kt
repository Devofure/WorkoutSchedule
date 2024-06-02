package com.devofure.workoutschedule.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import com.devofure.workoutschedule.data.Workout

@Composable
fun ShowWorkoutDetailDialog(workout: Workout, onEdit: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = workout.exercise.name, style = MaterialTheme.typography.h6) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                workout.sets?.let {
                    DetailItem(
                        icon = Icons.Default.FormatListNumbered,
                        label = "Sets",
                        value = it.toString()
                    )
                }
                workout.reps?.let {
                    DetailItem(
                        icon = Icons.Default.FormatListNumbered,
                        label = "Reps",
                        value = it.toString()
                    )
                }
                workout.duration?.let {
                    DetailItem(
                        icon = Icons.Default.Timer,
                        label = "Duration",
                        value = "$it mins"
                    )
                }
                if (workout.exercise.equipment.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.Build,
                        label = "Equipment",
                        value = workout.exercise.equipment
                    )
                }
                if (workout.exercise.primaryMuscles.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Primary Muscles",
                        value = workout.exercise.primaryMuscles.joinToString(", ")
                    )
                }
                if (workout.exercise.secondaryMuscles.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Secondary Muscles",
                        value = workout.exercise.secondaryMuscles.joinToString(", ")
                    )
                }
                if (workout.exercise.instructions.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.Description,
                        label = "Instructions",
                        value = workout.exercise.instructions.joinToString(" ")
                    )
                }
            }
        },
        confirmButton = {
            Row {
                Button(onClick = onEdit) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.body2, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.body1)
        }
    }
}
