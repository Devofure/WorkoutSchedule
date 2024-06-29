@file:OptIn(ExperimentalMaterial3Api::class)

package com.devofure.workoutschedule.ui.editworkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.data.SetDetails
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.exercise.Exercise
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.OrientationPreviews
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun EditWorkoutScreen(
    dayIndex: Int,
    updateWorkout: (Int, Workout) -> Unit,
    workout: Workout,
    navigate: Navigate,
) {
    var sets by remember { mutableStateOf(workout.repsList?.size ?: 0) }
    var setDetailsList by remember {
        mutableStateOf(
            workout.repsList ?: List(workout.repsList?.size ?: 0) { SetDetails(reps = 0) }
        )
    }
    var duration by remember { mutableStateOf(workout.durationInSeconds?.toString() ?: "") }
    var setsError by remember { mutableStateOf<String?>(null) }
    var repsError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Edit Workout")
                        Text(workout.exercise.name, style = MaterialTheme.typography.titleSmall)
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(32.dp)
                            .clickable { navigate.back() }
                    )
                },
                actions = {
                    TextButton(
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
                                val updatedWorkout = workout.copy(
                                    repsList = setDetailsList,
                                    durationInSeconds = duration.toIntOrNull()
                                )
                                updateWorkout(dayIndex, updatedWorkout)
                                navigate.back()
                            }
                        }
                    ) {
                        Text(
                            "Save",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                DurationPickerField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = "Total Duration",
                    error = durationError,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider()
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Sets: $sets",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    OutlinedIconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
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
                    OutlinedIconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            sets += 1
                            setDetailsList =
                                setDetailsList.toMutableList().apply { add(SetDetails()) }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Set")
                    }
                }
                if (setsError != null) {
                    Text(
                        text = setsError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    setDetailsList.forEachIndexed { index, setDetails ->
                        SetDetailsRow(
                            setDetails = setDetails,
                            onSetDetailsChange = { updatedSetDetails ->
                                setDetailsList = setDetailsList.toMutableList()
                                    .apply { this[index] = updatedSetDetails }
                            },
                            setIndex = index
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (repsError != null) {
                        Text(
                            text = repsError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp)) // Add space to avoid the button covering content
            }
        }
    )
}

@Composable
fun DurationPickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
) {
    val totalSeconds = value.toIntOrNull() ?: 0
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val formattedTime = buildString {
        if (hours > 0) append("$hours h ")
        if (minutes > 0) append("$minutes m ")
        if (seconds > 0) append("$seconds s")
    }.trim()

    var showPicker by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (error == null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = formattedTime.ifEmpty { "0 s" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Pick Time",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    if (showPicker) {
        TimePickerDialog(
            initialHour = hours,
            initialMinute = minutes,
            initialSecond = seconds,
            onTimeSelected = { selectedHour: Int, selectedMinute: Int, selectedSecond: Int ->
                val totalSecondsSelected =
                    (selectedHour * 3600) + (selectedMinute * 60) + selectedSecond
                onValueChange(totalSecondsSelected.toString())
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
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
    val setTitleNumber = setIndex + 1

    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.width(width = 32.dp)
            )
            Text(
                text = "Set $setTitleNumber",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.width(width = 32.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ValidatedTextField(
                value = reps,
                onValueChange = {
                    reps = it
                    onSetDetailsChange(setDetails.copy(reps = it.toIntOrNull() ?: 1))
                },
                label = "Reps",
                error = null,
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )
            ValidatedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                    onSetDetailsChange(setDetails.copy(weight = it.toFloatOrNull()))
                },
                label = "Weight (kg)",
                error = null,
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        DurationPickerField(
            value = duration,
            onValueChange = {
                duration = it
                onSetDetailsChange(setDetails.copy(duration = it.toIntOrNull()))
            },
            label = "Duration",
            error = null,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    if (error == null)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = modifier,
            isError = false,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
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

@PreviewLightDark
@OrientationPreviews
@Composable
fun EditWorkoutScreenPreview() {
    val navController = rememberNavController()
    val navigate = Navigate(navController)
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
            rowid = 0
        ),
        repsList = listOf(SetDetails(reps = 10), SetDetails(reps = 8)),
        durationInSeconds = 30006
    )

    MyWorkoutsTheme(primaryColor = Colors.DefaultThemeColor) {
        EditWorkoutScreen(
            dayIndex = 0,
            updateWorkout = { _, _ -> },
            workout = mockWorkout,
            navigate = navigate
        )
    }
}