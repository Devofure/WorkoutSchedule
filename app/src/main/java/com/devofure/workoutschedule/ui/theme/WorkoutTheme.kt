package com.devofure.workoutschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.devofure.workoutschedule.ui.settings.ThemeType

private val LightColorPalette = lightColors(
    primary = Gray700,
    primaryVariant = Gray600,
    secondary = Gray500,
    background = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Gray900,
    onSurface = Gray900
)

private val DarkColorPalette = darkColors(
    primary = Gray400,
    primaryVariant = Gray500,
    secondary = Gray300,
    background = Gray900,
    surface = Gray800,
    onPrimary = Gray900,
    onSecondary = Gray900,
    onBackground = White,
    onSurface = White
)

@Composable
fun MyWorkoutsTheme(themeType: ThemeType = ThemeType.SYSTEM, content: @Composable () -> Unit) {
    val colors = when (themeType) {
        ThemeType.LIGHT -> LightColorPalette
        ThemeType.DARK -> DarkColorPalette
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
