@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
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
import com.devofure.workoutschedule.ui.WorkoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(
    workoutViewModel: WorkoutViewModel,
    scaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextButton(onClick = { /* Handle log day */ }) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Log day")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log day")
        }
        TextButton(onClick = { /* Handle edit nickname */ }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Nickname")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Nickname")
        }
        TextButton(onClick = {
            coroutineScope.launch {
                workoutViewModel.markAllWorkoutsAsDone()
                scaffoldState.snackbarHostState.showSnackbar("All workouts completed!")
            }
        }) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Mark all as done")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mark all as done")
        }
        TextButton(onClick = { /* Handle add exercise */ }) {
            Icon(Icons.Filled.FitnessCenter, contentDescription = "Add Exercise")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Exercise")
        }
    }
}
