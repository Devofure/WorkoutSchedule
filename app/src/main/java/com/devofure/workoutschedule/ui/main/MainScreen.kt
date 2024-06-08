package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    sharedViewModel: SharedViewModel,
    onSettingsClick: () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val workouts by workoutViewModel.workouts.collectAsState()
    val expandedWorkoutIds = workoutViewModel.expandedWorkoutIds.collectAsState().value

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(workoutViewModel, scaffoldState, coroutineScope)
        },
        sheetPeekHeight = 0.dp,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    if (scaffoldState.bottomSheetState.isExpanded) {
                        scaffoldState.bottomSheetState.collapse()
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More Options")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TopBar(onSettingsClick = onSettingsClick)
            WorkoutList(
                workouts = workouts,
                expandedWorkoutIds = expandedWorkoutIds,
                onExpandToggle = { workoutId -> workoutViewModel.toggleExpanded(workoutId) },
                onWorkoutChecked = { workoutId, isChecked ->
                    workoutViewModel.onWorkoutChecked(
                        workoutId,
                        isChecked
                    )
                },
                onWorkoutRemove = { workout -> workoutViewModel.removeWorkout(workout) },
                onWorkoutDetail = { workout ->
                    workoutViewModel.selectWorkout(workout); navController.navigate(
                    "workout_detail"
                )
                },
                onWorkoutEdit = { workout ->
                    workoutViewModel.selectWorkout(workout); navController.navigate(
                    "edit_workout"
                )
                }
            )
        }
    }
}
