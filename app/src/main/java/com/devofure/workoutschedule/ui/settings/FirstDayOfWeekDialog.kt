package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.FirstDayOfWeek

@Composable
fun FirstDayOfWeekDialog(
    currentFirstDay: FirstDayOfWeek,
    onDismiss: () -> Unit,
    onFirstDayChange: (FirstDayOfWeek) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("First Day of the Week") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFirstDayChange(FirstDayOfWeek.SUNDAY) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentFirstDay == FirstDayOfWeek.SUNDAY,
                        onClick = { onFirstDayChange(FirstDayOfWeek.SUNDAY) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sunday")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFirstDayChange(FirstDayOfWeek.MONDAY) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentFirstDay == FirstDayOfWeek.MONDAY,
                        onClick = { onFirstDayChange(FirstDayOfWeek.MONDAY) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Monday")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
