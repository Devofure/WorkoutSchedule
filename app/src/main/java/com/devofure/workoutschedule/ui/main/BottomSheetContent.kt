@file:OptIn(ExperimentalFoundationApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.getFullDayName

@Composable
fun BottomSheetContent(
    daysOfWeek: List<String>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    nicknames: List<String>,
    workoutViewModel: WorkoutViewModel,
    navigate: Navigate,
    onEditNickname: () -> Unit,
    onLogDay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextButton(onClick = onLogDay) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Log day")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log day")
        }
        TextButton(onClick = onEditNickname) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Nickname")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Nickname")
        }
        TextButton(onClick = {
            val dayFullName = getFullDayName(
                daysOfWeek[pagerState.currentPage],
                nicknames[pagerState.currentPage]
            )
            workoutViewModel.onAllWorkoutsChecked(dayFullName, true)
            navigate.back()
        }) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Mark all as done")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mark all as done")
        }
        TextButton(onClick = {
            val dayFullName = getFullDayName(
                daysOfWeek[pagerState.currentPage],
                nicknames[pagerState.currentPage]
            )
            navigate.to(Route.AddExercise(dayFullName))
        }) {
            Icon(Icons.Filled.FitnessCenter, contentDescription = "Add Exercise")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Exercise")
        }
        TextButton(onClick = {
            val dayFullName = getFullDayName(
                daysOfWeek[pagerState.currentPage],
                nicknames[pagerState.currentPage]
            )
            navigate.to(Route.ReorderExercise(dayFullName))
        }) {
            Icon(Icons.Filled.Reorder, contentDescription = "Reorder Exercises")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reorder Exercises")
        }
    }
}
