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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme
import kotlin.math.roundToInt

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

@Composable
fun ReorderableExerciseList(
    exercises: List<Workout>,
    onReorder: (List<Workout>) -> Unit
) {
    var draggedItem by remember { mutableStateOf<Workout?>(null) }
    var items by remember { mutableStateOf(exercises) }
    var currentIndex by remember { mutableStateOf(-1) }
    val density = LocalDensity.current
    val itemHeightPx = with(density) { 56.dp.toPx() }
    val dragState = remember { mutableStateOf(DragState()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemsIndexed(items) { index, item ->
            val isDragging = dragState.value.isDragging && dragState.value.draggedItemIndex == index
            val offset = if (isDragging) dragState.value.currentOffset else Offset.Zero

            DraggableExerciseCard(
                workout = item,
                isBeingDragged = isDragging,
                offsetY = offset.y,
                onDragStart = { startOffset ->
                    dragState.value = dragState.value.copy(
                        isDragging = true,
                        initialOffset = startOffset,
                        draggedItemIndex = index
                    )
                    draggedItem = item
                    currentIndex = index
                    Log.d("ReorderExerciseScreen", "Drag started at offset: $startOffset, item index: $index")
                },
                onDrag = { change, dragAmount ->
                    val newOffset = dragState.value.currentOffset + dragAmount
                    dragState.value = dragState.value.copy(
                        currentOffset = newOffset,
                        draggedItemIndex = updateDraggedItemIndex(newOffset, items, dragState.value, itemHeightPx)
                    )
                    Log.d("ReorderExerciseScreen", "Dragging... New offset: $newOffset")
                    change.consume()
                },
                onDragEnd = {
                    items = reorderItems(items, dragState.value)
                    dragState.value = dragState.value.copy(isDragging = false, currentOffset = Offset.Zero)
                    draggedItem = null
                    currentIndex = -1
                    Log.d("ReorderExerciseScreen", "Drag ended. New items order: $items")
                    onReorder(items)
                }
            )
            Log.d("ReorderExerciseScreen", "Rendering item at index $index: ${item.exercise.name}")
        }
    }
}

@Composable
fun DraggableExerciseCard(
    workout: Workout,
    isBeingDragged: Boolean,
    offsetY: Float,
    onDragStart: (Offset) -> Unit,
    onDrag: (androidx.compose.ui.input.pointer.PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(if (isBeingDragged) Color.LightGray else MaterialTheme.colorScheme.background)
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDragStart(offset)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDrag = { change, dragAmount ->
                        onDrag(change, dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
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

data class DragState(
    val isDragging: Boolean = false,
    val initialOffset: Offset = Offset.Zero,
    val currentOffset: Offset = Offset.Zero,
    val draggedItemIndex: Int = -1
)

fun getItemIndexAtOffset(offset: Offset, items: List<Workout>, itemHeightPx: Float): Int {
    return (offset.y / itemHeightPx).toInt().coerceIn(0, items.size - 1)
}

fun updateDraggedItemIndex(offset: Offset, items: List<Workout>, dragState: DragState, itemHeightPx: Float): Int {
    val absoluteOffset = dragState.initialOffset.y + offset.y
    return (absoluteOffset / itemHeightPx).toInt().coerceIn(0, items.size - 1)
}

fun reorderItems(items: List<Workout>, dragState: DragState): List<Workout> {
    val fromIndex = dragState.draggedItemIndex
    val toIndex = getItemIndexAtOffset(dragState.initialOffset + dragState.currentOffset, items, 56f) // assuming item height is 56.dp

    Log.d("ReorderExerciseScreen", "Reordering items from index $fromIndex to index $toIndex")

    if (fromIndex == toIndex || fromIndex < 0 || toIndex < 0) return items

    val mutableItems = items.toMutableList()
    val movedItem = mutableItems.removeAt(fromIndex)
    mutableItems.add(toIndex, movedItem)
    Log.d("ReorderExerciseScreen", "Items after reordering: $mutableItems")
    return mutableItems
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
