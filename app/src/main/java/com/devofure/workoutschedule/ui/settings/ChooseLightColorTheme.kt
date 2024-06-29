// PrimaryColorDialog.kt
@file:OptIn(ExperimentalLayoutApi::class)

package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun PrimaryColorDialog(
    onDismiss: () -> Unit,
    onPrimaryColorChange: (Color) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Light Theme Color") },
        text = {
            FlowRow {
                ColorButton(color = Colors.BlueAccent, onPrimaryColorChange)
                ColorButton(color = Colors.RedAccent, onPrimaryColorChange)
                ColorButton(color = Colors.GreenAccent, onPrimaryColorChange)
                ColorButton(color = Colors.YellowAccent, onPrimaryColorChange)
                ColorButton(color = Colors.PurpleAccent, onPrimaryColorChange)
                ColorButton(color = Colors.OrangeAccent, onPrimaryColorChange)
                ColorButton(color = Colors.Gray, onPrimaryColorChange)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ColorButton(color: Color, onPrimaryColorChange: (Color) -> Unit) {
    Button(
        onClick = { onPrimaryColorChange(color) },
        modifier = Modifier
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(contentColor = color, containerColor = color),
        content = { Text("Color") },
    )
}

@PreviewLightDark
@Composable
fun PreviewPrimaryColorDialog() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        PrimaryColorDialog(
            onDismiss = {},
            onPrimaryColorChange = { /* Handle color change */ }
        )
    }
}