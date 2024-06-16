@file:OptIn(ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.addexercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.Exercise
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun AddExerciseScreen(
    day: String,
    searchQuery: String,
    filteredExercises: List<Exercise>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onAddWorkouts: (String, List<Exercise>) -> Unit,
    navigate: Navigate,
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { onSearchQueryChange(it) },
                                placeholder = { Text("Search Exercises") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                                    cursorColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                            )
                            IconButton(
                                onClick = {
                                    onSearchQueryChange("")
                                    isSearchExpanded = false
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    modifier = Modifier.size(32.dp),
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    } else {
                        Column {
                            Text("Add Exercises")
                            Text(day, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigate::back) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isSearchExpanded) {
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${selectedExercises.size} exercise(s) selected",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onAddWorkouts(day, selectedExercises)
                        navigate.back()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add", color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No exercises found", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn {
                    items(filteredExercises) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            isSelected = selectedExercises.contains(exercise),
                            onSelected = {
                                selectedExercises = if (selectedExercises.contains(exercise)) {
                                    selectedExercises - exercise
                                } else {
                                    selectedExercises + exercise
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val textAlpha = if (isSelected) 0.8f else 1f
    val textColor = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelected() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor.copy(
                            alpha = textAlpha
                        )
                    ),
                )
                Text(
                    text = "Equipment: ${exercise.equipment}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Primary Muscles: ${exercise.primaryMuscles.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Secondary Muscles: ${exercise.secondaryMuscles.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@PreviewLightDark
@OrientationPreviews
@Composable
fun AddExerciseScreenPreview() {
    val navController = rememberNavController()
    val sampleExercises = listOf(
        Exercise(
            "Push Up",
            "None",
            "Beginner",
            "Compound",
            "None",
            listOf("Chest"),
            listOf("Triceps"),
            listOf(),
            "Strength"
        ),
        Exercise(
            "Squat",
            "None",
            "Intermediate",
            "Compound",
            "None",
            listOf("Legs"),
            listOf("Glutes"),
            listOf(),
            "Strength"
        ),
        Exercise(
            "Bicep Curl",
            "None",
            "Beginner",
            "Isolation",
            "Dumbbell",
            listOf("Biceps"),
            listOf("Forearms"),
            listOf(),
            "Strength"
        )
    )

    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        AddExerciseScreen(
            day = "Monday",
            searchQuery = "",
            filteredExercises = sampleExercises,
            isLoading = false,
            onSearchQueryChange = {},
            onAddWorkouts = { _, _ -> },
            navigate = Navigate(navController)
        )
    }
}

@PreviewLightDark
@OrientationPreviews
@Composable
fun ExerciseItemPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        ExerciseItem(
            exercise = Exercise(
                name = "Push Up",
                force = "Push",
                level = "Beginner",
                mechanic = "Compound",
                equipment = "None",
                primaryMuscles = listOf("Chest"),
                secondaryMuscles = listOf("Triceps"),
                instructions = listOf(
                    "Keep your body straight",
                    "Lower your body until your chest touches the ground"
                ),
                category = "Strength"
            ),
            isSelected = false,
            onSelected = {}
        )
    }
}
