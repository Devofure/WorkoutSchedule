package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.theme.Colors.BlueAccent
import com.devofure.workoutschedule.ui.theme.Colors.RedAccent

@Composable
fun PrimaryColorDialog(
    onDismiss: () -> Unit,
    onPrimaryColorChange: (Color) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Light Theme Color") },
        text = {
            Column {
                Button(
                    onClick = { onPrimaryColorChange(BlueAccent) },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Blue")
                }
                Button(
                    onClick = { onPrimaryColorChange(RedAccent) },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Red")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
