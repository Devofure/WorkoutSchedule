package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
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
    val reminderTime by settingsViewModel.reminderTime.collectAsState()

    // Define a common background color for both the TopAppBar and the content
    val backgroundColor = MaterialTheme.colors.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = backgroundColor,
                elevation = 0.dp // Set elevation to 0 to make it level with the background
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
            }
        }
    )
}
