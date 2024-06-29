package com.devofure.workoutschedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun GenericItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    imageVector: ImageVector? = null,
    backgroundColor: Color? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val baseModifier = modifier
        .clickable(enabled = enabled) { if (enabled) onClick() }
        .padding(vertical = 4.dp)
    val finalModifier = if (backgroundColor != null) {
        baseModifier.background(backgroundColor)
    } else {
        baseModifier
    }

    val colors = backgroundColor?.let {
        ListItemDefaults.colors(
            containerColor = it
        )
    }

    val textColor = if (enabled) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
    }

    ListItem(
        leadingContent = {
            if (imageVector != null)
                Icon(
                    imageVector,
                    contentDescription = "$headline icon",
                    tint = textColor
                )
        },
        headlineContent = {
            Text(
                text = headline,
                color = textColor
            )
        },
        supportingContent = {
            if (supporting != null)
                Text(
                    text = supporting,
                    color = textColor.copy(alpha = 0.7f)
                )
        },
        modifier = finalModifier,
        colors = colors ?: ListItemDefaults.colors()
    )
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewSettingsItem() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        GenericItem(
            imageVector = Icons.Default.Settings,
            headline = "Workout Reminders",
            supporting = "Set up your workout reminder time",
            onClick = {},
            enabled = true,
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewSettingsItemWithoutIcon() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        GenericItem(
            headline = "Workout Reminders",
            supporting = "Set up your workout reminder time",
            onClick = {},
            enabled = false,
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewSettingsItemOneLine() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        GenericItem(
            imageVector = Icons.Default.Settings,
            headline = "Workout Reminders",
            onClick = {},
            enabled = true,
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewSettingsItemOneLineWithoutIcon() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        GenericItem(
            headline = "Workout Reminders",
            onClick = {},
            enabled = false,
        )
    }
}
