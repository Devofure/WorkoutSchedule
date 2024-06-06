package com.devofure.workoutschedule.ui.main

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable

@Composable
fun TopBar(onSettingsClick: () -> Unit, onCalendarClick: () -> Unit) {
    TopAppBar(
        title = { Text("Workout Schedule") },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(onClick = onCalendarClick) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Calendar")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}
