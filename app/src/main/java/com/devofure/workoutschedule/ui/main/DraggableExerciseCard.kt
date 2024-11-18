package com.devofure.workoutschedule.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.data.Workout
import kotlin.math.roundToInt

@Composable
fun DraggableExerciseCard(
    workout: Workout,
    isBeingDragged: Boolean,
    workoutListSize: Int,
    onDragStart: () -> Unit,
    onDragEnd: (Int) -> Unit
) {
    val density = LocalDensity.current
    var offsetY by remember { mutableStateOf(0f) }
    rememberDraggableState { delta ->
        offsetY += delta
    }

    Box(
        Modifier
            .padding(8.dp)
            .background(if (isBeingDragged) Color.LightGray else Color.White)
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val itemHeightPx = with(density) { 50.dp.toPx() }
                        val newIndex =
                            (offsetY / itemHeightPx)
                                .roundToInt()
                                .coerceIn(0, workoutListSize - 1)
                        onDragEnd(newIndex)
                        offsetY = 0f
                    }
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(workout.exercise.name, Modifier.weight(1f))
            Icon(Icons.Default.Menu, contentDescription = null)
        }
    }
}
