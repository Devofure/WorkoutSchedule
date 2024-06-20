@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.devofure.workoutschedule.ui.addexercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun FilterExerciseScreen(
    onFiltersSelected: (List<Pair<String, String>>) -> Unit,
    navigate: Navigate
) {
    val selectedAttributes = remember { mutableStateListOf<Pair<String, String>>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Exercises") },
                navigationIcon = {
                    IconButton(onClick = navigate::back) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onFiltersSelected(selectedAttributes)
                        navigate.back()
                    }) {
                        Text(text = "Filter")
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
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                FilterComponent(
                    attributeName = "Equipment",
                    selectedAttributes = selectedAttributes
                )

                FilterComponent(
                    attributeName = "Muscles (Primary and Secondary)",
                    selectedAttributes = selectedAttributes
                )

                FilterComponent(
                    attributeName = "Category",
                    selectedAttributes = selectedAttributes
                )

                if (selectedAttributes.isNotEmpty()) {
                    TextButton(onClick = { selectedAttributes.clear() }) {
                        Text(text = "Clear Filters", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    )
}

@Composable
fun FilterComponent(
    attributeName: String,
    selectedAttributes: SnapshotStateList<Pair<String, String>>
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showOptions by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = attributeName, style = MaterialTheme.typography.titleMedium)
        if (selectedAttributes.isNotEmpty()) {
            FlowRow {
                selectedAttributes.filter { it.first == attributeName }.forEach { attribute ->
                    Chip(
                        text = attribute.second,
                        onRemove = { selectedAttributes.remove(attribute) }
                    )
                }
            }
        }

        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                filteredOptions = filterOptions(attributeName, it)
                showOptions = true
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        filteredOptions = emptyList()
                        showOptions = false
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.size(8.dp))

        if (showOptions) {
            LazyColumn {
                items(filteredOptions) { option ->
                    AttributeItem(
                        attribute = option,
                        isSelected = selectedAttributes.contains(Pair(attributeName, option)),
                        onSelected = {
                            val pair = Pair(attributeName, option)
                            if (selectedAttributes.contains(pair)) {
                                selectedAttributes.remove(pair)
                            } else {
                                selectedAttributes.add(pair)
                            }
                            showOptions = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AttributeItem(
    attribute: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = attribute,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun Chip(text: String, onRemove: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

fun filterOptions(attributeName: String, query: String): List<String> {
    val allOptions = when (attributeName) {
        "Equipment" -> listOf("Dumbbell", "Barbell", "Machine", "Bodyweight")
        "Primary Muscles" -> listOf("Chest", "Back", "Legs", "Arms")
        "Secondary Muscles" -> listOf("Shoulders", "Triceps", "Biceps", "Calves")
        "Category" -> listOf("Strength", "Cardio", "Flexibility")
        else -> emptyList()
    }
    return if (query.isBlank()) {
        allOptions
    } else {
        allOptions.filter { it.contains(query, ignoreCase = true) }
    }
}

@PreviewLightDark
@Composable
fun ExerciseFilterScreenPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        FilterExerciseScreen(
            onFiltersSelected = {},
            navigate = Navigate(rememberNavController())
        )
    }
}

@PreviewLightDark
@Composable
fun AttributeItemPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        AttributeItem(
            attribute = "Primary Muscles",
            isSelected = true,
            onSelected = {}
        )
    }
}
