package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.ThemeType
import com.devofure.workoutschedule.ui.WorkoutViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    workoutViewModel: WorkoutViewModel,
    onBack: () -> Unit,
    currentTheme: ThemeType,
    onThemeChange: (ThemeType) -> Unit
) {
    var showReminderSetup by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            ListItem(
                text = { Text("Workout Reminders") },
                secondaryText = { Text("Set up your workout reminder time") },
                modifier = Modifier.clickable { showReminderSetup = true }
            )
            ListItem(
                text = { Text("Delete Schedule") },
                secondaryText = { Text("Delete your entire workout schedule") },
                modifier = Modifier.clickable { showDeleteConfirmation = true }
            )
            ListItem(
                text = { Text("Theme Settings") },
                secondaryText = { Text("Switch between light and dark modes") },
                modifier = Modifier.clickable { showThemeDialog = true }
            )
            // Add more settings options here...

            if (showReminderSetup) {
                ReminderSetupDialog(
                    onDismiss = { showReminderSetup = false },
                    onSave = { reminderTime ->
                        workoutViewModel.setReminder(reminderTime)
                        showReminderSetup = false
                    }
                )
            }

            if (showDeleteConfirmation) {
                DeleteConfirmationDialog(
                    onDismiss = { showDeleteConfirmation = false },
                    onConfirm = {
                        workoutViewModel.deleteAllWorkouts()
                        showDeleteConfirmation = false
                    }
                )
            }

            if (showThemeDialog) {
                ThemeDialog(
                    currentTheme = currentTheme,
                    onDismiss = { showThemeDialog = false },
                    onThemeChange = { newTheme ->
                        onThemeChange(newTheme)
                        showThemeDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReminderSetupDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var reminderTime by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Up Workout Reminders") },
        text = {
            Column {
                Text("When would you like to be reminded about your workouts?")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showTimePicker = true }) {
                    Text("Pick Time")
                }
                if (reminderTime.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Selected Time: $reminderTime")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(reminderTime) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showTimePicker) {
        TimePickerDialog(
            context = context,
            initialTime = reminderTime,
            onTimeSelected = { selectedTime ->
                reminderTime = selectedTime
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Schedule") },
        text = { Text("Are you sure you want to delete your entire workout schedule? This action cannot be undone.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ThemeDialog(currentTheme: ThemeType, onDismiss: () -> Unit, onThemeChange: (ThemeType) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme Settings") },
        text = {
            Column {
                RadioButtonWithText(
                    text = "Light",
                    selected = currentTheme == ThemeType.LIGHT,
                    onClick = { onThemeChange(ThemeType.LIGHT) }
                )
                RadioButtonWithText(
                    text = "Dark",
                    selected = currentTheme == ThemeType.DARK,
                    onClick = { onThemeChange(ThemeType.DARK) }
                )
                RadioButtonWithText(
                    text = "System Default",
                    selected = currentTheme == ThemeType.SYSTEM,
                    onClick = { onThemeChange(ThemeType.SYSTEM) }
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
