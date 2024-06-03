package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
    onWorkoutDetail: () -> Unit,
    onWorkoutEdit: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    val textAlpha = if (workout.isDone) 0.5f else 1f
    val textColor = if (workout.isDone) Color.Gray else MaterialTheme.typography.body2.color

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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = workout.exercise.name,
                        style = MaterialTheme.typography.h6.copy(color = textColor.copy(alpha = textAlpha)),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
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
                            onWorkoutEdit()
                        }) {
                            Text("Edit")
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
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                    workout.sets?.let {
                        Text(
                            text = "${workout.sets} sets",
                            style = MaterialTheme.typography.body2.copy(color = textColor.copy(alpha = textAlpha)),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        workout.repsList?.forEachIndexed { index, reps ->
                            Text(
                                text = "Set ${index + 1}: $reps reps",
                                style = MaterialTheme.typography.body2.copy(
                                    color = textColor.copy(
                                        alpha = textAlpha
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.primary),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    workout.exercise.instructions.forEachIndexed { _, instruction ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "\u2022", // Bullet point
                                style = MaterialTheme.typography.body2.copy(
                                    color = textColor.copy(
                                        alpha = textAlpha
                                    )
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.body2.copy(
                                    color = textColor.copy(
                                        alpha = textAlpha
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
    }
}
