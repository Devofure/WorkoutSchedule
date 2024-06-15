package com.devofure.workoutschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.devofure.workoutschedule.ui.settings.ThemeType

private val LightColorScheme = lightColorScheme(
    primary = Gray700,
    onPrimary = White,
    primaryContainer = Gray600,
    onPrimaryContainer = Gray900,
    secondary = Gray500,
    onSecondary = White,
    secondaryContainer = Gray300,
    onSecondaryContainer = Gray900,
    background = White,
    onBackground = Gray900,
    surface = Gray300,
    onSurface = Gray900
)

private val DarkColorScheme = darkColorScheme(
    primary = Gray400,
    onPrimary = Gray900,
    primaryContainer = Gray500,
    onPrimaryContainer = White,
    secondary = Gray300,
    onSecondary = Gray900,
    secondaryContainer = Gray500,
    onSecondaryContainer = White,
    background = Gray900,
    onBackground = White,
    surface = Gray800,
    onSurface = White
)

@Composable
fun MyWorkoutsTheme(themeType: ThemeType = ThemeType.SYSTEM, content: @Composable () -> Unit) {
    val colorScheme = when (themeType) {
        ThemeType.LIGHT -> LightColorScheme
        ThemeType.DARK -> DarkColorScheme
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
