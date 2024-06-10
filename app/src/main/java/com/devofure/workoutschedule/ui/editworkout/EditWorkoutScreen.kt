package com.devofure.workoutschedule.ui.editworkout

import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.ui.SharedViewModel
import com.devofure.workoutschedule.ui.WorkoutViewModel

@Composable
fun EditWorkoutScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    workoutViewModel: WorkoutViewModel,
    day: String
) {
    val workout by sharedViewModel.selectedWorkout.collectAsState()

    workout?.let { workoutData ->
        var sets by remember { mutableIntStateOf(workoutData.repsList?.size ?: 0) }
        var setDetailsList by remember {
            mutableStateOf(
                workoutData.repsList ?: List(workoutData.repsList?.size ?: 0) {
                    SetDetails(reps = 0)
                }
            )
        }
        var duration by remember { mutableStateOf(workoutData.duration?.toString() ?: "") }
        var setsError by remember { mutableStateOf<String?>(null) }
        var repsError by remember { mutableStateOf<String?>(null) }
        var durationError by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Edit Workout")
                            Text(
                                workoutData.exercise.name,
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .clickable {
                                    sharedViewModel.clearSelectedWorkout()
                                    navController.popBackStack()
                                }
                        )
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        DurationPickerField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = "Total Duration (mins)",
                            error = durationError,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sets: $sets", style = MaterialTheme.typography.h6)
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(
                                onClick = {
                                    if (sets > 0) {
                                        sets -= 1
                                        setDetailsList = setDetailsList.dropLast(1).toMutableList()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove Set"
                                )
                            }
                            IconButton(
                                onClick = {
                                    sets += 1
                                    setDetailsList = setDetailsList.toMutableList().apply {
                                        add(SetDetails(reps = 0))
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Set"
                                )
                            }
                        }
                        if (setsError != null) {
                            Text(
                                text = setsError ?: "",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column {
                            setDetailsList.forEachIndexed { index, setDetails ->
                                SetDetailsRow(
                                    setDetails = setDetails,
                                    onSetDetailsChange = { updatedSetDetails ->
                                        setDetailsList = setDetailsList.toMutableList().apply {
                                            this[index] = updatedSetDetails
                                        }
                                    },
                                    setIndex = index
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (repsError != null) {
                                Text(
                                    text = repsError ?: "",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp)) // Add space to avoid the button covering content
                    }
                    Button(
                        onClick = {
                            val validationResult = validateWorkoutInput(
                                sets.toString(),
                                setDetailsList.joinToString(", ") { it.reps.toString() },
                                duration
                            )
                            setsError = validationResult.setsError
                            repsError = validationResult.repsError
                            durationError = validationResult.durationError

                            if (validationResult.isValid) {
                                val updatedWorkout = workoutData.copy(
                                    repsList = setDetailsList,
                                    duration = duration.toIntOrNull()
                                )
                                workoutViewModel.updateWorkout(day, updatedWorkout)
                                sharedViewModel.clearSelectedWorkout()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }
        )
    }
}

@Composable
fun DurationPickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    context: android.content.Context
) {
    val initialHour = remember { value.toIntOrNull()?.div(60) ?: 0 }
    val initialMinute = remember { value.toIntOrNull()?.rem(60) ?: 0 }
    var showPicker by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            isError = error != null,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Pick Time",
                    modifier = Modifier.clickable { showPicker = true }
                )
            }
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    if (showPicker) {
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                val totalMinutes = selectedHour * 60 + selectedMinute
                onValueChange(totalMinutes.toString())
                showPicker = false
            },
            initialHour,
            initialMinute,
            true
        ).apply {
            setOnDismissListener { showPicker = false }
        }.show()
    }
}

@Composable
fun SetDetailsRow(
    setDetails: SetDetails,
    onSetDetailsChange: (SetDetails) -> Unit,
    setIndex: Int
) {
    var reps by remember { mutableStateOf(setDetails.reps.toString()) }
    var weight by remember { mutableStateOf(setDetails.weight?.toString() ?: "") }
    var duration by remember { mutableStateOf(setDetails.duration?.toString() ?: "") }

    Column {
        ValidatedTextField(
            value = reps,
            onValueChange = {
                reps = it
                onSetDetailsChange(setDetails.copy(reps = it.toIntOrNull() ?: 0))
            },
            label = "Reps for set ${setIndex + 1}",
            error = null,
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(8.dp))
        ValidatedTextField(
            value = weight,
            onValueChange = {
                weight = it
                onSetDetailsChange(setDetails.copy(weight = it.toFloatOrNull()))
            },
            label = "Weight for set ${setIndex + 1} (kg)",
            error = null,
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(8.dp))
        DurationPickerField(
            value = duration,
            onValueChange = {
                duration = it
                onSetDetailsChange(setDetails.copy(duration = it.toIntOrNull()))
            },
            label = "Duration for set ${setIndex + 1} (minutes)",
            error = null,
            context = LocalContext.current
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

fun validateWorkoutInput(sets: String, repsList: String, duration: String): ValidationResult {
    var isValid = true
    var setsError: String? = null
    var repsError: String? = null
    var durationError: String? = null

    val setsInt = sets.toIntOrNull()
    val reps = repsList.split(",").map { it.trim() }

    if (setsInt == null || setsInt <= 0) {
        setsError = "Invalid number"
        isValid = false
    } else if (reps.size != setsInt) {
        repsError = "Number of reps must match the number of sets"
        isValid = false
    } else if (reps.any { it.toIntOrNull() == null }) {
        repsError = "Invalid reps format"
        isValid = false
    }

    if (duration.isNotBlank() && duration.toIntOrNull() == null) {
        durationError = "Invalid number"
        isValid = false
    }

    return ValidationResult(isValid, setsError, repsError, durationError)
}

data class ValidationResult(
    val isValid: Boolean, val setsError: String?, val repsError: String?, val durationError: String?
)
