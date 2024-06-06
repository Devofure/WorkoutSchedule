package com.devofure.workoutschedule.ui.main

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun EditNicknameDialog(
    editedNickname: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Edit Nickname") },
        text = {
            TextField(
                value = editedNickname,
                onValueChange = onValueChange,
                label = { Text("Nickname") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave() }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
        }
    )
}
