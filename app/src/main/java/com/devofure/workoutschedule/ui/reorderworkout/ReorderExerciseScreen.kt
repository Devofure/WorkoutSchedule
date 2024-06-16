@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.devofure.workoutschedule.ui.reorderworkout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ReorderExerciseScreen(
    navigate: Navigate,
    day: String,
    workouts: List<Workout>,
    updateWorkoutOrder: (String, List<Workout>) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reorder Exercises") },
                navigationIcon = {
                    IconButton(onClick = { navigate.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        updateWorkoutOrder(day, workouts)
                        navigate.back()
                    }) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ReorderableExerciseList(
                    exercises = workouts,
                    onReorder = { updatedList ->
                        updateWorkoutOrder(day, updatedList)
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableExerciseList(
    exercises: List<Workout>,
    onReorder: (List<Workout>) -> Unit
) {
    var list by remember { mutableStateOf(exercises) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            val fromIndex = indexOfFirst { it.id == from.key }
            val toIndex = indexOfFirst { it.id == to.key }
            add(toIndex, removeAt(fromIndex))
        }
        onReorder(list)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
            ReorderableItem(reorderableLazyColumnState, item.id) {
                val interactionSource = remember { MutableInteractionSource() }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .semantics {
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = "Move Up",
                                    action = {
                                        if (index > 0) {
                                            list = list.toMutableList().apply {
                                                add(index - 1, removeAt(index))
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                                CustomAccessibilityAction(
                                    label = "Move Down",
                                    action = {
                                        if (index < list.size - 1) {
                                            list = list.toMutableList().apply {
                                                add(index + 1, removeAt(index))
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                            )
                        },
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(item.exercise.name, Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            modifier = Modifier
                                .draggableHandle(
                                    onDragStarted = {
                                        // Haptic feedback on drag start
                                    },
                                    onDragStopped = {
                                        // Haptic feedback on drag stop
                                    },
                                    interactionSource = interactionSource,
                                )
                                .clearAndSetSemantics { },
                            onClick = {},
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Reorder")
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun ReorderExerciseScreenPreview() {
    val mockWorkout = Workout(
        id = 1,
        exercise = Exercise(
            name = "Bench Press",
            force = "Push",
            level = "Intermediate",
            mechanic = "Compound",
            equipment = "Barbell",
            primaryMuscles = listOf("Chest"),
            secondaryMuscles = listOf("Triceps", "Shoulders"),
            instructions = listOf("Lift weight", "Lower weight"),
            category = "Strength",
        ),
        repsList = listOf(SetDetails(reps = 10), SetDetails(reps = 8)),
        duration = 30
    )
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        ReorderExerciseScreen(
            navigate = Navigate(rememberNavController()),
            day = "Monday",
            workouts = listOf(
                mockWorkout,
                mockWorkout,
                mockWorkout,
            ),
            updateWorkoutOrder = { _, _ -> }
        )
    }
}
