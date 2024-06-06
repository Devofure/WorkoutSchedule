package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout

@Composable
fun WorkoutPage(
    dayFullName: String,
    workouts: List<Workout>,
    expandedWorkoutIds: Set<Int>,
    onExpandToggle: (Int) -> Unit,
    onWorkoutChecked: (Int, Boolean) -> Unit,
    onWorkoutRemove: (Workout) -> Unit,
    onWorkoutDetail: (Workout) -> Unit,
    onWorkoutEdit: (Workout) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = dayFullName,
                style = MaterialTheme.typography.h6
            )
        }

        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rest day",
                    style = MaterialTheme.typography.h4,
                    color = Color.Gray
                )
            }
        } else {
            WorkoutProgress(workouts)
            LazyColumn {
                items(workouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        expanded = expandedWorkoutIds.contains(workout.id),
                        onExpandToggle = {
                            onExpandToggle(workout.id)
                        },
                        onWorkoutChecked = onWorkoutChecked,
                        onWorkoutRemove = {
                            onWorkoutRemove(workout)
                        },
                        onWorkoutDetail = {
                            onWorkoutDetail(workout)
                        },
                        onWorkoutEdit = {
                            onWorkoutEdit(workout)
                        }
                    )
                }
            }
        }
    }
}
