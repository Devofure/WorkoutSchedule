package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.data.WEEK
import com.devofure.workoutschedule.ui.GenericItem
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun BottomSheetContent(
    dayOfWeek: DayOfWeek,
    navigate: Navigate,
    showEditDialogDayNickname: () -> Unit,
    showLogWorkoutDay: () -> Unit,
    checkAllWorkouts: (Int) -> Unit,
    hasFinishedWorkouts: Boolean,
    hasWorkouts: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        GenericItem(
            imageVector = Icons.Filled.CalendarToday,
            headline = "Log finished workouts",
            onClick = showLogWorkoutDay,
            enabled = hasFinishedWorkouts // Disable if no finished workouts
        )
        GenericItem(
            imageVector = Icons.Filled.CheckCircle,
            headline = "Mark all workouts as done",
            onClick = { checkAllWorkouts(dayOfWeek.dayIndex) },
            enabled = hasWorkouts // Disable if no workouts
        )
        GenericItem(
            imageVector = Icons.Filled.FitnessCenter,
            headline = "Add Exercise to this day",
            onClick = { navigate.to(Route.AddExercise(dayOfWeek.dayIndex)) }
        )
        GenericItem(
            imageVector = Icons.Filled.Reorder,
            headline = "Reorder Workouts",
            onClick = { navigate.to(Route.ReorderExercise(dayOfWeek.dayIndex)) },
            enabled = hasWorkouts // Disable if no workouts
        )
        GenericItem(
            imageVector = Icons.Filled.Edit,
            headline = "Edit the name of this day",
            onClick = showEditDialogDayNickname
        )
    }
}


@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun BottomSheetContentPreview() {
    val navigate = Navigate(navController = androidx.navigation.compose.rememberNavController())
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        BottomSheetContent(
            navigate = navigate,
            dayOfWeek = WEEK[0],
            showEditDialogDayNickname = {},
            showLogWorkoutDay = {},
            checkAllWorkouts = {},
            hasFinishedWorkouts = true,
            hasWorkouts = true,
        )
    }
}
