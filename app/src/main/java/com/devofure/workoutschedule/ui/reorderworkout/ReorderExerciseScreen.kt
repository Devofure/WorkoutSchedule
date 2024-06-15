@file:OptIn(ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.reorderworkout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.WorkoutViewModel
import kotlin.math.roundToInt

@Composable
fun ReorderExerciseScreen(
    navController: NavHostController,
    day: String,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val workouts by workoutViewModel.workoutsForDay(day).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reorder Exercises") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        workoutViewModel.updateWorkoutOrder(day, workouts)
                        navController.popBackStack()
                    }) {
                        Text("Save")
                    }
                }
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
                        workoutViewModel.updateWorkoutOrder(day, updatedList)
                    }
                )
            }
        }
    )
}

@Composable
fun ReorderableExerciseList(
    exercises: List<Workout>,
    onReorder: (List<Workout>) -> Unit
) {
    var draggedExercise by remember { mutableStateOf<Workout?>(null) }
    var exerciseList by remember { mutableStateOf(exercises) }
    val offsets = remember { mutableStateListOf(*Array(exercises.size) { mutableStateOf(0f) }) }
    val itemHeightPx = with(LocalDensity.current) { 50.dp.toPx() }

    LazyColumn {
        itemsIndexed(
            exerciseList,
            key = { index, item -> item.id.hashCode() * 31 + index }) { index, exercise ->
            val offset = offsets[index].value
            DraggableExerciseCard(
                workout = exercise,
                isBeingDragged = draggedExercise == exercise,
                offsetY = offset,
                itemHeightPx = itemHeightPx,
                onDragStart = {
                    draggedExercise = exercise
                    Log.d("DragDrop", "Started dragging: ${exercise.exercise.name}")
                },
                onDrag = { deltaY ->
                    val newOffset = offsets[index].value + deltaY
                    val newIndex = (index + (newOffset / itemHeightPx).roundToInt()).coerceIn(0, exerciseList.size - 1)
                    Log.d("DragDrop", "Dragging: ${exercise.exercise.name}, deltaY: $deltaY, newIndex: $newIndex, newOffset: $newOffset")

                    if (newIndex != index) {
                        exerciseList = exerciseList.toMutableList().apply {
                            add(newIndex, removeAt(index))
                        }

                        offsets.add(newIndex, offsets.removeAt(index))
                        onReorder(exerciseList)
                        Log.d("DragDrop", "Reordered list: ${exerciseList.map { it.exercise.name }}")
                    }

                    offsets[index].value = newOffset
                },
                onDragEnd = {
                    draggedExercise = null
                    offsets.forEachIndexed { i, _ -> offsets[i].value = 0f }
                    Log.d("DragDrop", "Ended dragging: ${exercise.exercise.name}")
                }
            )
        }
    }
}

@Composable
fun DraggableExerciseCard(
    workout: Workout,
    isBeingDragged: Boolean,
    offsetY: Float,
    itemHeightPx: Float,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        Modifier
            .padding(8.dp)
            .background(if (isBeingDragged) Color.LightGray else Color.White)
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y)
                        Log.d("DragDrop", "Dragging item: ${workout.exercise.name}, dragAmount: $dragAmount")
                    },
                    onDragEnd = { onDragEnd() }
                )
            }
            .shadow(if (isBeingDragged) 2.dp else 0.dp, shape = MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(workout.exercise.name, Modifier.weight(1f), fontSize = 18.sp)
            Icon(Icons.Default.Menu, contentDescription = "Drag", tint = Color.Gray)
        }
    }
}
