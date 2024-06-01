package com.devofure.workoutschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.devofure.workoutschedule.ui.WorkoutApp
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

class MainActivity : ComponentActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWorkoutsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    WorkoutApp(workoutViewModel)
                    val isFirstLaunch by workoutViewModel.isFirstLaunch.collectAsState()

                    if (isFirstLaunch) {
                        ShowSampleScheduleDialog(workoutViewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun ShowSampleScheduleDialog(viewModel: WorkoutViewModel) {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Generate Sample Schedule") },
                text = { Text("Do you want to generate a sample workout schedule?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.generateSampleSchedule()
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
