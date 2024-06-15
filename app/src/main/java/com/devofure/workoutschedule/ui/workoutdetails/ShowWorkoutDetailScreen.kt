package com.devofure.workoutschedule.ui.workoutdetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devofure.workoutschedule.ui.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val workoutState by sharedViewModel.selectedWorkout.collectAsState()
    workoutState?.let { workout ->
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
                        IconButton(onClick = {
                            sharedViewModel.clearSelectedWorkout()
                            navController.popBackStack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
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
                        sectionHeader(title = "Sets & Reps")
                        repsList.forEachIndexed { index, reps ->
                            detailText(text = "Set ${index + 1}: $reps reps")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    workout.duration?.let { duration ->
                        detailItem(label = "Duration", value = "$duration mins")
                    }

                    workout.exercise.equipment?.takeIf { equipment -> equipment.isNotEmpty() }
                        ?.let { equipment ->
                            detailItem(label = "Equipment", value = equipment)
                        }

                    workout.exercise.primaryMuscles.takeIf { primaryMuscles -> primaryMuscles.isNotEmpty() }
                        ?.let { primaryMuscles ->
                            detailItem(
                                label = "Primary Muscles",
                                value = primaryMuscles.joinToString(", ")
                            )
                        }

                    workout.exercise.secondaryMuscles.takeIf { secondaryMuscles -> secondaryMuscles.isNotEmpty() }
                        ?.let { secondaryMuscles ->
                            detailItem(
                                label = "Secondary Muscles",
                                value = secondaryMuscles.joinToString(", ")
                            )
                        }

                    workout.exercise.instructions.takeIf { instructions -> instructions.isNotEmpty() }
                        ?.let { instructions ->
                            sectionHeader(title = "Instructions")
                            instructions.forEachIndexed { _, instruction ->
                                instructionItem(instruction)
                            }
                        }
                }
            }
        )
    }
}

@Composable
fun sectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun detailItem(label: String, value: String) {
    Column {
        sectionHeader(title = label)
        detailText(text = value)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun detailText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
fun instructionItem(instruction: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Text(
            text = "\u2022", // Bullet point
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

@Composable
fun detailLink(text: String, url: String) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary),
            start = 0,
            end = text.length
        )
        addStringAnnotation(tag = "URL", annotation = url, start = 0, end = text.length)
    }
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                    context.startActivity(intent)
                }
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}
