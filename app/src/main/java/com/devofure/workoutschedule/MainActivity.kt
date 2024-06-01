package com.devofure.workoutschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.devofure.workoutschedule.ui.WorkoutApp
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModelFactory
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize WorkoutViewModel with WorkoutViewModelFactory
        val workoutViewModel: WorkoutViewModel by viewModels { WorkoutViewModelFactory(application) }

        setContent {
            MyWorkoutsTheme {
                WorkoutApp(workoutViewModel = workoutViewModel)
            }
        }
    }
}
