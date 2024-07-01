@file:OptIn(ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.workoutdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun WorkoutDetailScreen(workout: Workout, navigate: Navigate) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = workout.exercise.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigate.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                workout.repsList?.let { repsList ->
                    SectionHeader(title = "Sets & Reps")
                    repsList.forEachIndexed { index, reps ->
                        DetailText(text = "Set ${index + 1}: ${reps.reps} reps")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                workout.durationInSeconds?.let { duration ->
                    val hours = duration / 3600
                    val minutes = (duration % 3600) / 60
                    val seconds = duration % 60
                    val detailedDuration = buildString {
                        if (hours > 0) append("$hours h ")
                        if (minutes > 0) append("$minutes m ")
                        if (seconds > 0) append("$seconds s")
                    }.trim()
                    DetailItem(label = "Duration", value = detailedDuration)
                }

                workout.exercise.equipment.takeIf { it?.isNotEmpty() == true }?.let { equipment ->
                    DetailItem(label = "Equipment", value = equipment)
                }

                workout.exercise.primaryMuscles.takeIf { it.isNotEmpty() }?.let { primaryMuscles ->
                    DetailItem(label = "Primary Muscles", value = primaryMuscles.joinToString(", "))
                }

                workout.exercise.secondaryMuscles.takeIf { it.isNotEmpty() }?.let { secondaryMuscles ->
                    DetailItem(label = "Secondary Muscles", value = secondaryMuscles.joinToString(", "))
                }

                workout.exercise.instructions?.takeIf { it.isNotEmpty() }?.let { instructions ->
                    SectionHeader(title = "Instructions")
                    instructions.forEach { instruction ->
                        InstructionItem(instruction)
                    }
                }
            }
        }
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        SectionHeader(title = label)
        DetailText(text = value)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun DetailText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
fun InstructionItem(instruction: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            modifier = Modifier.padding(end = 4.dp, start = 8.dp)
        )
        Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@PreviewFontScale
@OrientationPreviews
@Composable
fun PreviewWorkoutDetailScreen() {
    val workout = Workout(
        id = 1,
        exercise = Exercise(
            rowid = 1,
            name = "Push Up",
            level = "None",
            category = "Beginner",
            primaryMuscles = listOf("Chest"),
            secondaryMuscles = listOf("Triceps"),
            equipment = "None",
            instructions = listOf("Keep your back straight", "Lower your body until your chest nearly touches the floor")
        ),
        repsList = listOf(SetDetails(10), SetDetails(10), SetDetails(10)),
        durationInSeconds = 117
    )
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        WorkoutDetailScreen(workout = workout, navigate = Navigate(rememberNavController()))
    }
}