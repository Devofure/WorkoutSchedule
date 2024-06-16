@file:OptIn(ExperimentalFoundationApi::class)

package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
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
import com.devofure.workoutschedule.ui.GenericItem
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.getFullDayName
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun BottomSheetContent(
    daysOfWeek: List<String>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    nicknames: List<String>,
    navigate: Navigate,
    onEditNickname: () -> Unit,
    onLogDay: () -> Unit,
    checkAllWorkouts: (String) -> Unit,
) {
    val dayFullName = getFullDayName(
        daysOfWeek[pagerState.currentPage],
        nicknames[pagerState.currentPage]
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        GenericItem(
            imageVector = Icons.Filled.CalendarToday,
            headline = "Log day",
            onClick = onLogDay,
        )
        GenericItem(
            imageVector = Icons.Filled.Edit,
            headline = "Edit Nickname",
            onClick = onEditNickname
        )
        GenericItem(
            imageVector = Icons.Filled.CheckCircle,
            headline = "Mark all as done",
        ) {
            checkAllWorkouts(dayFullName)
        }
        GenericItem(
            imageVector = Icons.Filled.FitnessCenter,
            headline = "Add Exercise",
        ) {
            navigate.to(Route.AddExercise(dayFullName))
        }
        GenericItem(
            imageVector = Icons.Filled.Reorder,
            headline = "Reorder Exercises",
            ) {
            navigate.to(Route.ReorderExercise(dayFullName))
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun BottomSheetContentPreview() {
    val daysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val nicknames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val pagerState = rememberPagerState(pageCount = { daysOfWeek.size })
    val navigate = Navigate(navController = androidx.navigation.compose.rememberNavController())

    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        BottomSheetContent(
            daysOfWeek = daysOfWeek,
            pagerState = pagerState,
            nicknames = nicknames,
            navigate = navigate,
            onEditNickname = {},
            onLogDay = {},
            checkAllWorkouts = {}
        )
    }
}
