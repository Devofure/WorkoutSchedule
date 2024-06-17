package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import com.devofure.workoutschedule.ui.GenericItem
import com.devofure.workoutschedule.ui.Navigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navigate: Navigate,
    currentTheme: ThemeType,
    onThemeChange: (ThemeType) -> Unit,
    onPrimaryColorChange: (Color) -> Unit
) {
    var showReminderSetup by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPrimaryColorDialog by remember { mutableStateOf(false) }
    var showFirstDayDialog by remember { mutableStateOf(false) }
    var showDayNamingPreferenceDialog by remember { mutableStateOf(false) }

    val reminderTime by settingsViewModel.reminderTime.collectAsState()
    val firstDayOfWeek by settingsViewModel.firstDayOfWeek.collectAsState()
    val dayNamingPreference by settingsViewModel.dayNamingPreference.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navigate.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                GenericItem(
                    headline = "Workout Reminders",
                    supporting = "Set up your workout reminder time",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showReminderSetup = true },
                )
                GenericItem(
                    headline = "Delete Schedule",
                    supporting = "Delete your entire workout schedule",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showDeleteConfirmation = true },
                )
                GenericItem(
                    headline = "Theme Mode",
                    supporting = "Switch between light and dark modes",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showThemeDialog = true },
                )
                GenericItem(
                    headline = "Theme Light color",
                    supporting = "Choose your theme light color",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showPrimaryColorDialog = true },
                )
                GenericItem(
                    headline = "First Day of the Week",
                    supporting = "Choose the first day of the week",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showFirstDayDialog = true },
                )
                GenericItem(
                    headline = "Schedule Day Naming",
                    supporting = "Choose between week day and numeric naming",
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onClick = { showDayNamingPreferenceDialog = true },
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

                if (showPrimaryColorDialog) {
                    PrimaryColorDialog(
                        onDismiss = { showPrimaryColorDialog = false },
                        onPrimaryColorChange = { newColor ->
                            onPrimaryColorChange(newColor)
                            showPrimaryColorDialog = false
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

                if (showDayNamingPreferenceDialog) {
                    DayNamingPreferenceDialog(
                        currentPreference = dayNamingPreference,
                        onDismiss = { showDayNamingPreferenceDialog = false },
                        onPreferenceChange = { newPreference ->
                            settingsViewModel.setDayNamingPreference(newPreference)
                            showDayNamingPreferenceDialog = false
                        }
                    )
                }
            }
        }
    )
}
