package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetContent(
    onMarkAllAsDone: () -> Unit,
    onLogDay: () -> Unit,
    onAddExercise: () -> Unit,
    onEditNickname: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextButton(onClick = onMarkAllAsDone) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Mark all as done")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mark all as done")
        }
        TextButton(onClick = onLogDay) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Log day")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log day")
        }
        TextButton(onClick = onAddExercise) {
            Icon(Icons.Filled.FitnessCenter, contentDescription = "Add Exercise")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Exercise")
        }
        TextButton(onClick = onEditNickname) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Nickname")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Nickname")
        }
    }
}
