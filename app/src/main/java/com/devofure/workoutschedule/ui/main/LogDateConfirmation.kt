package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogDateConfirmationDialog(
    selectedDate: Date,
    onConfirm: () -> Unit,
    onPickAnotherDate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirm Log Date") },
        text = {
            Column {
                Text(
                    "Do you want to log the workout for ${
                        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(selectedDate)
                    }?"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onPickAnotherDate() }
                ) {
                    Text("Pick another date")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
