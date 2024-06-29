package com.devofure.workoutschedule.ui.editworkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun TimePickerDialog(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    initialSecond: Int = 0,
    onTimeSelected: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val hour = remember { mutableStateOf(initialHour) }
    val minute = remember { mutableStateOf(initialMinute) }
    val second = remember { mutableStateOf(initialSecond) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Time") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimePickerField("Hour", hour.value) { newHour ->
                        hour.value = newHour.coerceIn(0, 23)
                    }
                    TimePickerField("Minute", minute.value) { newMinute ->
                        minute.value = newMinute.coerceIn(0, 59)
                    }
                    TimePickerField("Second", second.value) { newSecond ->
                        second.value = newSecond.coerceIn(0, 59)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(hour.value, minute.value, second.value)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimePickerField(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = value.toString().padStart(2, '0'),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let(onValueChange)
            },
            modifier = Modifier.width(60.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimePickerDialog() {
    TimePickerDialog(
        initialHour = 0,
        initialMinute = 0,
        initialSecond = 0,
        onTimeSelected = { hour, minute, second ->
            // Handle time selection
        },
        onDismiss = {
            // Handle dialog dismiss
        }
    )
}
