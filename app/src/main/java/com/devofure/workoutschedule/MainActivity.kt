package com.devofure.workoutschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devofure.workoutschedule.ui.WorkoutApp
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWorkoutsTheme {
                val workoutViewModel: WorkoutViewModel = viewModel()
                val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()

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
