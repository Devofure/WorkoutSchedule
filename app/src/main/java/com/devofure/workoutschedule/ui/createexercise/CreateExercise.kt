package com.devofure.workoutschedule.ui.createexercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.exercise.Exercise
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(
    onAddExercise: (Exercise) -> Unit,
    navigate: Navigate,
) {
    val name = remember { mutableStateOf("") }
    val force = remember { mutableStateOf("") }
    val level = remember { mutableStateOf("") }
    val mechanic = remember { mutableStateOf("") }
    val equipment = remember { mutableStateOf("") }
    val primaryMuscles = remember { mutableStateOf("") }
    val secondaryMuscles = remember { mutableStateOf("") }
    val instructions = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Exercise") },
                navigationIcon = {
                    IconButton(onClick = navigate::back) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val newExercise = Exercise(
                            rowid = 0,
                            name = name.value,
                            force = force.value,
                            level = level.value,
                            mechanic = mechanic.value,
                            equipment = equipment.value,
                            primaryMuscles = primaryMuscles.value.split(","),
                            secondaryMuscles = secondaryMuscles.value.split(","),
                            instructions = instructions.value.split(","),
                            category = category.value
                        )
                        onAddExercise(newExercise)
                        navigate.back()
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
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = force.value,
                    onValueChange = { force.value = it },
                    label = { Text("Force") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = level.value,
                    onValueChange = { level.value = it },
                    label = { Text("Level") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = mechanic.value,
                    onValueChange = { mechanic.value = it },
                    label = { Text("Mechanic") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = equipment.value,
                    onValueChange = { equipment.value = it },
                    label = { Text("Equipment") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = primaryMuscles.value,
                    onValueChange = { primaryMuscles.value = it },
                    label = { Text("Primary Muscles (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = secondaryMuscles.value,
                    onValueChange = { secondaryMuscles.value = it },
                    label = { Text("Secondary Muscles (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = instructions.value,
                    onValueChange = { instructions.value = it },
                    label = { Text("Instructions (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = category.value,
                    onValueChange = { category.value = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}

@Preview
@Composable
fun CreateExerciseScreenPreview() {
    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        CreateExerciseScreen({}, Navigate(rememberNavController()))
    }
}
