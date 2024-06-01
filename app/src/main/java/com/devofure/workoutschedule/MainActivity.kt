package com.devofure.workoutschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import com.devofure.workoutschedule.ui.WorkoutApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWorkoutsTheme {
                WorkoutApp()
            }
        }
    }
}
