@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.devofure.workoutschedule.ui.addexercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@Composable
fun FilterExerciseScreen(
    currentFilters: List<Pair<String, String>>,
    onFiltersSelected: (List<Pair<String, String>>) -> Unit,
    equipmentOptions: List<String>,
    musclesOptions: List<String>,
    categoryOptions: List<String>,
) {
    val selectedAttributes = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(currentFilters) {
        selectedAttributes.clear()
        selectedAttributes.addAll(currentFilters)
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        FilterComponent(
            attributeName = "Equipment",
            options = equipmentOptions,
            selectedAttributes = selectedAttributes,
            onFiltersSelected = onFiltersSelected,
        )

        FilterComponent(
            attributeName = "Muscles",
            options = musclesOptions,
            selectedAttributes = selectedAttributes,
            onFiltersSelected = onFiltersSelected
        )

        FilterComponent(
            attributeName = "Category",
            options = categoryOptions,
            selectedAttributes = selectedAttributes,
            onFiltersSelected = onFiltersSelected
        )

        if (selectedAttributes.isNotEmpty()) {
            TextButton(onClick = { selectedAttributes.clear() }) {
                Text(text = "Clear Filters", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun FilterComponent(
    attributeName: String,
    options: List<String>,
    selectedAttributes: SnapshotStateList<Pair<String, String>>,
    onFiltersSelected: (List<Pair<String, String>>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showOptions by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = attributeName, style = MaterialTheme.typography.titleSmall)
        if (selectedAttributes.any { it.first == attributeName }) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                selectedAttributes
                    .filter { it.first == attributeName }
                    .forEach { attribute ->
                        InputChip(
                            label = { Text(attribute.second) },
                            onClick = {},
                            selected = false,
                            trailingIcon = {
                                IconButton(
                                    modifier = Modifier.size(24.dp),
                                    onClick = {
                                        selectedAttributes.remove(attribute)
                                        onFiltersSelected(selectedAttributes)
                                    },
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove")
                                }
                            },
                        )
                    }
            }
        }

        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                filteredOptions =
                    options.filter { option -> option.contains(it, ignoreCase = true) }
                showOptions = it.isNotEmpty()
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
            Column {
                filteredOptions.take(10).forEach { option ->
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
                            onFiltersSelected(selectedAttributes)
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
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface

    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RectangleShape // Ensure the shape is not rounded
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

@PreviewLightDark
@Composable
fun ExerciseFilterScreenPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        FilterExerciseScreen(
            currentFilters = emptyList(),
            onFiltersSelected = {},
            equipmentOptions = emptyList(),
            musclesOptions = emptyList(),
            categoryOptions = emptyList(),
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
