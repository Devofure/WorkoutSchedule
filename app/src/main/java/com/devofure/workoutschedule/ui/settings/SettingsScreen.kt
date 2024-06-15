package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    currentTheme: ThemeType,
    onThemeChange: (ThemeType) -> Unit
) {
    var showReminderSetup by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFirstDayDialog by remember { mutableStateOf(false) }
    val reminderTime by settingsViewModel.reminderTime.collectAsState()
    val firstDayOfWeek by settingsViewModel.firstDayOfWeek.collectAsState()

    // Define a common background color for both the TopAppBar and the content
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(0.dp) // Set elevation to 0 to make it level with the background
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                ListItem(
                    headlineContent = { Text("Workout Reminders") },
                    supportingContent = { Text("Set up your workout reminder time") },
                    modifier = Modifier.clickable { showReminderSetup = true }
                )
                ListItem(
                    headlineContent = { Text("Delete Schedule") },
                    supportingContent = { Text("Delete your entire workout schedule") },
                    modifier = Modifier.clickable { showDeleteConfirmation = true }
                )
                ListItem(
                    headlineContent = { Text("Theme Settings") },
                    supportingContent = { Text("Switch between light and dark modes") },
                    modifier = Modifier.clickable { showThemeDialog = true }
                )
                ListItem(
                    headlineContent = { Text("First Day of the Week") },
                    supportingContent = { Text("Choose the first day of the week") },
                    modifier = Modifier.clickable { showFirstDayDialog = true }
                )

                if (showReminderSetup) {
                    ReminderSetupDialog(
                        currentReminderTime = reminderTime,
                        onDismiss = { showReminderSetup = false },
                        onSave = { newReminderTime ->
                            settingsViewModel.setReminder(newReminderTime)
                            showReminderSetup = false
                        }
                    )
                }

                if (showDeleteConfirmation) {
                    DeleteConfirmationDialog(
                        onDismiss = { showDeleteConfirmation = false },
                        onConfirm = {
                            settingsViewModel.deleteAllWorkouts()
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
                        }
                    )
                }

                if (showFirstDayDialog) {
                    FirstDayOfWeekDialog(
                        currentFirstDay = firstDayOfWeek,
                        onDismiss = { showFirstDayDialog = false },
                        onFirstDayChange = { newFirstDay ->
                            settingsViewModel.setFirstDayOfWeek(newFirstDay)
                            showFirstDayDialog = false
                        }
                    )
                }
            }
        }
    )
}
