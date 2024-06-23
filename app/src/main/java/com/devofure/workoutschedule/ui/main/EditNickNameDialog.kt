@file:OptIn(ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.ui.getDayName

@Composable
fun EditNicknameDialog(
    editedNickname: String,
    dayOfWeek: DayOfWeek,
    dayNamingPreference: DayOfWeek.DayNamingPreference,
    onNicknameChange: (String) -> Unit,
    save: (DayOfWeek) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            val dayName = getDayName(dayOfWeek, dayNamingPreference, editedNickname)
            Text(dayName)
        },
        text = {
            TextField(
                value = editedNickname,
                onValueChange = onNicknameChange,
                label = { Text("Day Name") },
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { save(dayOfWeek) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
