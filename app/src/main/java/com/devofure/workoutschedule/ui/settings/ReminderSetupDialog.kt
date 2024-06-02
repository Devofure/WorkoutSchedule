package com.devofure.workoutschedule.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ReminderSetupDialog(
    currentReminderTime: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var reminderTime by remember { mutableStateOf(currentReminderTime) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showTimePicker = true
        } else {
            // Handle permission denial
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Up Workout Reminders") },
        text = {
            Column {
                Text("When would you like to be reminded about your workouts?")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showTimePicker = true
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
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
        val initialTime = if (currentReminderTime.isNotEmpty()) {
            val timeParts = currentReminderTime.split(" ")
            val time = timeParts[0].split(":")
            Pair(time[0].toInt(), time[1].toInt())
        } else {
            Pair(
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE)
            )
        }

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                reminderTime = timeFormat.format(calendar.time)
                showTimePicker = false
            },
            initialTime.first,
            initialTime.second,
            false
        ).apply {
            setOnDismissListener { showTimePicker = false }
            show()
        }
    }
}
