package com.devofure.workoutschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.ui.ThemeType
import com.devofure.workoutschedule.ui.WorkoutApp
import com.devofure.workoutschedule.ui.WorkoutViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val workoutViewModel: WorkoutViewModel = viewModel()
            val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()
            val currentTheme by workoutViewModel.theme.collectAsState()

            MyWorkoutsTheme(theme = currentTheme) {
                if (isFirstLaunch) {
                    AskUserToGenerateSampleSchedule(workoutViewModel)
                } else {
                    WorkoutApp(workoutViewModel)
                }
            }
        }
    }

    @Composable
    fun AskUserToGenerateSampleSchedule(workoutViewModel: WorkoutViewModel) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Generate Sample Schedule") },
            text = { Text("Would you like to generate a sample workout schedule?") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutViewModel.generateSampleSchedule()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        workoutViewModel.declineSampleSchedule()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun MyWorkoutsTheme(theme: ThemeType, content: @Composable () -> Unit) {
    val colors = when (theme) {
        ThemeType.LIGHT -> lightColors()
        ThemeType.DARK -> darkColors()
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) darkColors() else lightColors()
    }

    MaterialTheme(colors = colors) {
        content()
    }
}
