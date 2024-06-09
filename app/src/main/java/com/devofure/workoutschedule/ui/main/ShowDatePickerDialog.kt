package com.devofure.workoutschedule.ui.main

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate

@Composable
fun ShowDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val today = LocalDate.now()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(
                year,
                month + 1,
                dayOfMonth
            ) // Note: Month is 0-based in DatePickerDialog
            onDateSelected(selectedDate)
        },
        today.year,
        today.monthValue - 1, // Note: Month is 0-based in DatePickerDialog
        today.dayOfMonth
    ).apply {
        setOnDismissListener { onDismissRequest() }
        show()
    }
}
