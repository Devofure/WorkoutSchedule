@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.devofure.workoutschedule.ui.main

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.Workout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun WorkoutApp(
    workoutViewModel: WorkoutViewModel = viewModel(),
    onSettingsClick: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController, workoutViewModel, onSettingsClick) }
        composable("add_exercise") { AddExerciseScreen(navController, workoutViewModel) }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    onSettingsClick: () -> Unit
) {
    val daysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val pagerState = rememberPagerState { daysOfWeek.size }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    var showEditWorkoutScreen by remember { mutableStateOf(false) }
    var showWorkoutDetailScreen by remember { mutableStateOf(false) }
    var expandedWorkoutIds by remember { mutableStateOf(setOf<Int>()) }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colors.primary,
        darkIcons = useDarkIcons
    )

    var showDatePicker by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(
                onMarkAllAsDone = {
                    val day = daysOfWeek[pagerState.currentPage]
                    workoutViewModel.onAllWorkoutsChecked(day, true)
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("All workouts completed!")
                    }
                },
                onLogDay = {
                    showDatePicker = true
                },
                onAddExercise = {
                    navController.navigate("add_exercise")
                }
            )
        },
        sheetPeekHeight = 0.dp,
        floatingActionButton = {
            val isExpanded = scaffoldState.bottomSheetState.isExpanded
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    if (isExpanded) {
                        scaffoldState.bottomSheetState.collapse()
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }) {
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 0f else 0f,
                    animationSpec = tween(durationMillis = 150),
                    label = "",
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.MoreVert,
                    contentDescription = if (isExpanded) "Close Options" else "More Options",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .animateContentSize()
        ) {
            TopAppBar(
                title = { Text("Workout Schedule") },
                backgroundColor = MaterialTheme.colors.primary,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
            PagerIndicator(
                pagerState = pagerState,
                pageCount = daysOfWeek.size,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val workouts by workoutViewModel.workoutsForDay(daysOfWeek[page])
                    .collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = daysOfWeek[page],
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    if (workouts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Rest day",
                                style = MaterialTheme.typography.h4,
                                color = Color.Gray
                            )
                        }
                    } else {
                        WorkoutProgress(workouts)
                        LazyColumn {
                            items(workouts) { workout ->
                                WorkoutItem(
                                    workout = workout,
                                    expanded = expandedWorkoutIds.contains(workout.id),
                                    onExpandToggle = {
                                        expandedWorkoutIds =
                                            if (expandedWorkoutIds.contains(workout.id)) {
                                                expandedWorkoutIds - workout.id
                                            } else {
                                                expandedWorkoutIds + workout.id
                                            }
                                    },
                                    onWorkoutChecked = { workoutId, isChecked ->
                                        workoutViewModel.onWorkoutChecked(
                                            daysOfWeek[page],
                                            workoutId,
                                            isChecked
                                        )
                                    },
                                    onWorkoutRemove = {
                                        workoutViewModel.removeWorkout(
                                            daysOfWeek[page],
                                            workout
                                        )
                                    },
                                    onWorkoutDetail = {
                                        selectedWorkout = workout
                                        showWorkoutDetailScreen = true
                                    },
                                    onWorkoutEdit = {
                                        selectedWorkout = workout
                                        showEditWorkoutScreen = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        ShowDatePickerDialog(onDateSelected = { date ->
            // Handle the selected date here, e.g., log the day
            showDatePicker = false
        }, onDismissRequest = {
            showDatePicker = false
        })
    }
}

@Composable
fun BottomSheetContent(
    onMarkAllAsDone: () -> Unit,
    onLogDay: () -> Unit,
    onAddExercise: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextButton(onClick = onMarkAllAsDone) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Mark all as done")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mark all as done")
        }
        TextButton(onClick = onLogDay) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Log day")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log day")
        }
        TextButton(onClick = onAddExercise) {
            Icon(Icons.Filled.FitnessCenter, contentDescription = "Add Exercise")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Exercise")
        }
    }
}

@Composable
fun ShowDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnDismissListener { onDismissRequest() }
        show()
    }
}

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            }
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}