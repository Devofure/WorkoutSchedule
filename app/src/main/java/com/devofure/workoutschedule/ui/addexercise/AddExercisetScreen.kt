@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.devofure.workoutschedule.ui.addexercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.exercise.Exercise
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.Route
import com.devofure.workoutschedule.ui.WorkoutViewModel
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun AddExerciseScreen(
    subTitle: String,
    dayIndex: Int,
    workoutViewModel: WorkoutViewModel,
    navigate: Navigate,
) {
    val isLoading by workoutViewModel.isLoading.collectAsState()
    val filteredExercises by workoutViewModel.filteredExercises.collectAsState()
    val searchQuery by workoutViewModel.filterQuery.collectAsState()
    val selectedFilters by workoutViewModel.selectedFilters.collectAsState()

    val equipmentOptions by workoutViewModel.equipmentOptions.collectAsState()
    val musclesOptions by workoutViewModel.muscleOptions.collectAsState()
    val categoryOptions by workoutViewModel.categoryOptions.collectAsState()

    AddExerciseScreen(
        dayIndex = dayIndex,
        subTitle = subTitle,
        searchQuery = searchQuery,
        filteredExercises = filteredExercises,
        isLoading = isLoading,
        onSearchQueryChange = { workoutViewModel.filterQuery.value = it },
        onAddWorkouts = workoutViewModel::addWorkouts,
        navigate = navigate,
        equipmentOptions = equipmentOptions,
        musclesOptions = musclesOptions,
        categoryOptions = categoryOptions,
        selectedFilters = selectedFilters,
        onFiltersSelected = { workoutViewModel.selectedFilters.value = it }
    )
}

@Composable
fun AddExerciseScreen(
    subTitle: String,
    dayIndex: Int,
    searchQuery: String,
    filteredExercises: List<Exercise>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onAddWorkouts: (Int, List<Exercise>) -> Unit,
    navigate: Navigate,
    equipmentOptions: List<String>,
    musclesOptions: List<String>,
    categoryOptions: List<String>,
    selectedFilters: List<Pair<String, String>>,
    onFiltersSelected: (List<Pair<String, String>>) -> Unit
) {
    var selectedExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val isSearchExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Add Exercises")
                        Text(subTitle, style = MaterialTheme.typography.titleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigate::back) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onAddWorkouts(dayIndex, selectedExercises)
                        navigate.back()
                    }) {
                        Text(text = "Save")
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigate.to(Route.CreateExercise) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Exercise")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { onSearchQueryChange(it) },
                        placeholder = { Text("Search Exercises") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                }
            }

            if (selectedFilters.isNotEmpty()) {
                FlowRow(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    selectedFilters.forEach { (attribute, value) ->
                        InputChip(
                            label = { Text("$attribute: $value") },
                            onClick = {},
                            selected = false,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val newFilters = selectedFilters.toMutableList().apply {
                                            remove(Pair(attribute, value))
                                        }
                                        onFiltersSelected(newFilters)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove")
                                }
                            },
                        )
                    }
                }
            }

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
        if (showDialog) {
            FilterDialog(
                showDialog = { showDialog = it },
                selectedFilters = onFiltersSelected,
                currentFilters = selectedFilters,
                equipmentOptions = equipmentOptions,
                musclesOptions = musclesOptions,
                categoryOptions = categoryOptions,
            )
        }
    }
}

@Composable
private fun FilterDialog(
    showDialog: (Boolean) -> Unit,
    selectedFilters: (List<Pair<String, String>>) -> Unit,
    currentFilters: List<Pair<String, String>>,
    equipmentOptions: List<String>,
    musclesOptions: List<String>,
    categoryOptions: List<String>
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 600.dp), // Set a maximum height for the dialog
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Filter",
                )
                Box(modifier = Modifier.weight(1f)) {
                    FilterExerciseScreen(
                        currentFilters = currentFilters,
                        onFiltersSelected = { filters ->
                            selectedFilters(filters)
                        },
                        equipmentOptions = equipmentOptions,
                        musclesOptions = musclesOptions,
                        categoryOptions = categoryOptions,
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                TextButton(
                    onClick = { showDialog(false) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Close")
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

@Preview
@Composable
fun AddExerciseScreenPreview() {
    val navController = rememberNavController()
    val sampleExercises = listOf(
        Exercise(
            rowid = 1,
            name = "Push Up",
            force = "None",
            level = "Beginner",
            mechanic = "Compound",
            equipment = "None",
            primaryMuscles = listOf("Chest"),
            secondaryMuscles = listOf("Triceps"),
            instructions = listOf(),
            category = "Strength"
        ),
        Exercise(
            rowid = 2,
            name = "Squat",
            force = "None",
            level = "Intermediate",
            mechanic = "Compound",
            equipment = "None",
            primaryMuscles = listOf("Legs"),
            secondaryMuscles = listOf("Glutes"),
            instructions = listOf(),
            category = "Strength"
        ),
        Exercise(
            rowid = 3,
            name = "Bicep Curl",
            force = "None",
            level = "Beginner",
            mechanic = "Isolation",
            equipment = "Dumbbell",
            primaryMuscles = listOf("Biceps"),
            secondaryMuscles = listOf("Forearms"),
            instructions = listOf(),
            category = "Strength"
        )
    )

    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        AddExerciseScreen(
            subTitle = "Monday",
            dayIndex = 1,
            searchQuery = "",
            filteredExercises = sampleExercises,
            isLoading = false,
            onSearchQueryChange = {},
            onAddWorkouts = { _, _ -> },
            navigate = Navigate(navController),
            equipmentOptions = emptyList(),
            musclesOptions = emptyList(),
            categoryOptions = emptyList(),
            selectedFilters = emptyList(),
            onFiltersSelected = {}
        )
    }
}

@Preview
@Composable
fun AddExerciseScreenPreviewWithFilter() {
    val navController = rememberNavController()
    val sampleExercises = listOf(
        Exercise(
            rowid = 1,
            name = "Push Up",
            force = "None",
            level = "Beginner",
            mechanic = "Compound",
            equipment = "None",
            primaryMuscles = listOf("Chest"),
            secondaryMuscles = listOf("Triceps"),
            instructions = listOf(),
            category = "Strength"
        ),
        Exercise(
            rowid = 2,
            name = "Squat",
            force = "None",
            level = "Intermediate",
            mechanic = "Compound",
            equipment = "None",
            primaryMuscles = listOf("Legs"),
            secondaryMuscles = listOf("Glutes"),
            instructions = listOf(),
            category = "Strength"
        ),
        Exercise(
            rowid = 3,
            name = "Bicep Curl",
            force = "None",
            level = "Beginner",
            mechanic = "Isolation",
            equipment = "Dumbbell",
            primaryMuscles = listOf("Biceps"),
            secondaryMuscles = listOf("Forearms"),
            instructions = listOf(),
            category = "Strength"
        )
    )

    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        AddExerciseScreen(
            subTitle = "Monday",
            dayIndex = 1,
            searchQuery = "",
            filteredExercises = sampleExercises,
            isLoading = false,
            onSearchQueryChange = {},
            onAddWorkouts = { _, _ -> },
            navigate = Navigate(navController),
            equipmentOptions = emptyList(),
            musclesOptions = emptyList(),
            categoryOptions = emptyList(),
            selectedFilters = emptyList(),
            onFiltersSelected = {}
        )
    }
}

@Preview
@Composable
fun ExerciseItemPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        ExerciseItem(
            exercise = Exercise(
                rowid = 1,
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

@Preview
@Composable
fun ExerciseFilterPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        FilterDialog(
            showDialog = {},
            selectedFilters = {},
            equipmentOptions = emptyList(),
            musclesOptions = emptyList(),
            categoryOptions = emptyList(),
            currentFilters = emptyList()
        )
    }
}
