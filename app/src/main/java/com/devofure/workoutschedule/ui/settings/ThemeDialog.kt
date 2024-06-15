package com.devofure.workoutschedule.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemeDialog(
    currentTheme: ThemeType,
    onDismiss: () -> Unit,
    onThemeChange: (ThemeType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme Settings") },
        text = {
            Column {
                RadioButtonWithText(
                    text = "Light",
                    selected = currentTheme == ThemeType.LIGHT,
                    onClick = { onThemeChange(ThemeType.LIGHT) }
                )
                RadioButtonWithText(
                    text = "Dark",
                    selected = currentTheme == ThemeType.DARK,
                    onClick = { onThemeChange(ThemeType.DARK) }
                )
                RadioButtonWithText(
                    text = "System Default",
                    selected = currentTheme == ThemeType.SYSTEM,
                    onClick = { onThemeChange(ThemeType.SYSTEM) }
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
