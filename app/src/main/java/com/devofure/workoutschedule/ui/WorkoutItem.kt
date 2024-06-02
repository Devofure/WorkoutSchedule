package com.devofure.workoutschedule.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

@Composable
fun WorkoutItem(
    workout: Workout,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onWorkoutChecked: (Int, Boolean) -> Unit,
    onWorkoutRemove: () -> Unit,
    onWorkoutDetail: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onExpandToggle() },
        elevation = 4.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = workout.isDone,
                    onCheckedChange = { isChecked -> onWorkoutChecked(workout.id, isChecked) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = workout.exercise.name, style = MaterialTheme.typography.h6)
                    Text(
                        text = when {
                            workout.sets != null && workout.reps != null -> "${workout.sets} sets of ${workout.reps} reps"
                            workout.duration != null -> "Duration: ${workout.duration} mins"
                            else -> ""
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-16).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(onClick = {
                            showMenu = false
                            onWorkoutDetail()
                        }) {
                            Text("Details")
                        }
                        DropdownMenuItem(onClick = {
                            showMenu = false
                            onWorkoutRemove()
                        }) {
                            Text("Remove")
                        }
                    }
                }
            }
            if (expanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = workout.exercise.instructions.joinToString(" "),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}
