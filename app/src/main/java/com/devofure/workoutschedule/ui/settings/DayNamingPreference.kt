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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.DayOfWeek

@Composable
fun DayNamingPreferenceDialog(
    currentPreference: DayOfWeek.DayNamingPreference,
    onDismiss: () -> Unit,
    onPreferenceChange: (DayOfWeek.DayNamingPreference) -> Unit
) {
    val selectedPreference = remember { mutableStateOf(currentPreference) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onPreferenceChange(selectedPreference.value)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        title = { Text("Choose Day Naming Preference") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedPreference.value = DayOfWeek.DayNamingPreference.DAY_NAMES }
                ) {
                    RadioButton(
                        selected = selectedPreference.value == DayOfWeek.DayNamingPreference.DAY_NAMES,
                        onClick = { selectedPreference.value = DayOfWeek.DayNamingPreference.DAY_NAMES }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Week Day\n(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedPreference.value = DayOfWeek.DayNamingPreference.DAY_NUMBERS }
                ) {
                    RadioButton(
                        selected = selectedPreference.value == DayOfWeek.DayNamingPreference.DAY_NUMBERS,
                        onClick = { selectedPreference.value = DayOfWeek.DayNamingPreference.DAY_NUMBERS }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Numeric Day\n(Day 1, Day 2, Day 3, Day 4, Day 5, Day 6, Day 7)")
                }
            }
        }
    )
}
