// MyWorkoutsTheme.kt
package com.devofure.workoutschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.devofure.workoutschedule.ui.settings.ThemeType

private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6)
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6)
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
