package com.devofure.workoutschedule.ui.addexercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.devofure.workoutschedule.ui.Navigate
import com.devofure.workoutschedule.ui.theme.Colors
import com.devofure.workoutschedule.ui.theme.MyWorkoutsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFilterScreen(
    onFiltersSelected: (List<String>) -> Unit,
    navigate: Navigate
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredAttributes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedAttributes = remember { mutableStateListOf<String>() }

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
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        filteredAttributes = filterAttributes(it)
                    },
                    placeholder = { Text("Search Attributes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(16.dp))

                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAttributes) { attribute ->
                        AttributeItem(
                            attribute = attribute,
                            isSelected = selectedAttributes.contains(attribute),
                            onSelected = {
                                if (selectedAttributes.contains(attribute)) {
                                    selectedAttributes.remove(attribute)
                                } else {
                                    selectedAttributes.add(attribute)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
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

fun filterAttributes(query: String): List<String> {
    val allAttributes = listOf(
        "Force",
        "Level",
        "Mechanic",
        "Equipment",
        "Primary Muscles",
        "Secondary Muscles",
        "Category"
    )
    return if (query.isBlank()) {
        allAttributes
    } else {
        allAttributes.filter { it.contains(query, ignoreCase = true) }
    }
}


@PreviewLightDark
@Composable
fun ExerciseFilterScreenPreview() {
    MyWorkoutsTheme(primaryColor = Colors.GreenAccent) {
        ExerciseFilterScreen(
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