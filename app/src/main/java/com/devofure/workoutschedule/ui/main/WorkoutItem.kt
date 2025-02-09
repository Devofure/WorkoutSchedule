package com.devofure.workoutschedule.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun WorkoutItem(
    workout: Workout,
    expanded: Boolean,
    hideInstruction: Boolean = false,
    onClick: () -> Unit,
    itemMoreMenu: Map<String, () -> Unit> = emptyMap(),
    onWorkoutChecked: ((Int, Boolean) -> Unit)? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    val textAlpha = if (workout.isDone) 0.5f else 1f
    val textColor = if (workout.isDone) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                if (onWorkoutChecked != null)
                    Checkbox(
                        checked = workout.isDone,
                        onCheckedChange = { isChecked -> onWorkoutChecked(workout.id, isChecked) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = workout.exercise.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = textColor.copy(
                                alpha = textAlpha
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-16).dp, y = 0.dp)
                    ) {
                        itemMoreMenu.forEach { (title, menuItemAction) ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    showMenu = false
                                    menuItemAction()
                                }
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                    workout.repsList?.let { repsList ->
                        Text(
                            text = "${repsList.size} sets:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary.copy(
                                    alpha = textAlpha
                                )                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        repsList.forEachIndexed { index, reps ->
                            Text(
                                text = "Set ${index + 1}: ${reps.reps} reps",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = textColor.copy(
                                        alpha = textAlpha
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    if (hideInstruction && workout.repsList?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (hideInstruction.not()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Instructions:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary.copy(
                                    alpha = textAlpha
                                )
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        workout.exercise.instructions?.forEachIndexed { _, instruction ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "\u2022", // Bullet point
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = textColor.copy(
                                            alpha = textAlpha
                                        )
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = instruction,
                                    style = MaterialTheme.typography.bodyMedium.copy(
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
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewWorkoutItem() {
    val exercise = Exercise(
        name = "Dumbbell Bench Press",
        force = "Push",
        level = "Intermediate",
        mechanic = "Compound",
        equipment = "Dumbbell",
        primaryMuscles = listOf("Chest"),
        secondaryMuscles = listOf("Triceps", "Shoulders"),
        category = "Strength",
        instructions = listOf(
            "Lie down on a flat bench.",
            "Hold a dumbbell in each hand.",
            "Push the dumbbells up."
        ),
        rowid = 1
    )
    val workout = Workout(
        id = 1,
        exercise = exercise,
        repsList = listOf(
            SetDetails(reps = 10),
            SetDetails(reps = 10),
            SetDetails(reps = 10)
        ),
        isDone = false,
        position = 1
    )
    MyWorkoutsTheme(primaryColor = Color.Green) {
        WorkoutItem(
            workout = workout,
            expanded = true,
            onClick = {},
            itemMoreMenu = mapOf(
                "Details" to {},
                "Edit" to {},
                "Remove" to {}
            ),
            onWorkoutChecked = { _, _ -> }
        )
    }
}
