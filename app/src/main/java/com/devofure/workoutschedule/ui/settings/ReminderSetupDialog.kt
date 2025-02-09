// ReminderSetupDialog.kt
package com.devofure.workoutschedule.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.devofure.workoutschedule.data.ReminderTime

@Composable
fun ReminderSetupDialog(
    currentReminderTime: ReminderTime,
    onDismiss: () -> Unit,
    onSave: (ReminderTime) -> Unit
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
                OutlinedButton(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showTimePicker = true
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }) {
                    Text("Pick Time")
                }
                if (reminderTime.format().isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Selected Time: ${reminderTime.format()}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(reminderTime) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showTimePicker) {
        val initialTime = Pair(reminderTime.hour, reminderTime.minute)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                reminderTime = ReminderTime(hourOfDay, minute)
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
