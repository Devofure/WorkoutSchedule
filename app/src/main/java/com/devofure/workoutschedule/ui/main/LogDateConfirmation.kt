package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateConfirmationDialog(
    selectedDate: LocalDate,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onPickAnotherDate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Log Date") },
        text = {
            Column {
                Text(
                    "Do you want to log the workout for ${
                        selectedDate.format(
                            DateTimeFormatter.ofPattern(
                                "EEEE, MMMM d",
                                Locale.getDefault()
                            )
                        )
                    }?"
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onPickAnotherDate) {
                    Text("Pick another date")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}