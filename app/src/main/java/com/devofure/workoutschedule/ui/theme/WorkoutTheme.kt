package com.devofure.workoutschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devofure.workoutschedule.ui.settings.ThemeType
import com.devofure.workoutschedule.ui.theme.Colors.Gray100
import com.devofure.workoutschedule.ui.theme.Colors.Gray300
import com.devofure.workoutschedule.ui.theme.Colors.Gray400
import com.devofure.workoutschedule.ui.theme.Colors.Gray500
import com.devofure.workoutschedule.ui.theme.Colors.Gray700
import com.devofure.workoutschedule.ui.theme.Colors.Gray800
import com.devofure.workoutschedule.ui.theme.Colors.Gray900
import com.devofure.workoutschedule.ui.theme.Colors.White

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)


object Colors {
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFEEEEEE)
    val Gray300 = Color(0xFFE0E0E0)
    val Gray400 = Color(0xFFBDBDBD)
    val Gray500 = Color(0xFF9E9E9E)
    val Gray600 = Color(0xFF757575)
    val Gray700 = Color(0xFF616161)
    val Gray800 = Color(0xFF424242)
    val Gray900 = Color(0xFF212121)
    val White = Color(0xFFFFFFFF)
    val BlueAccent = Color(0xFF009688)
    val BlueAccentLight = Color(0xFFBBDEFB)
    val RedAccent = Color(0xFFE53935)
    val RedAccentLight = Color(0xFFFFCDD2)
}

private fun customLightColorScheme(primaryColor: Color) = lightColorScheme(
    primary = primaryColor,
    onPrimary = White,
    primaryContainer = primaryColor.copy(alpha = 0.1f),
    onPrimaryContainer = Gray900,
    secondary = Gray700,
    onSecondary = White,
    secondaryContainer = Gray400,
    onSecondaryContainer = Gray900,
    background = Gray100,
    onBackground = Gray900,
    surface = White,
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
fun MyWorkoutsTheme(
    themeType: ThemeType = ThemeType.SYSTEM,
    primaryColor: Color,
    content: @Composable () -> Unit
) {
    val colors = when (themeType) {
        ThemeType.LIGHT -> customLightColorScheme(primaryColor)
        ThemeType.DARK -> DarkColorScheme
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) DarkColorScheme else customLightColorScheme(
            primaryColor
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}