package com.devofure.workoutschedule.ui.editworkout

import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DurationPickerDialog(
    initialDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val hours = initialDuration / 3600
    val minutes = (initialDuration % 3600) / 60
    val seconds = initialDuration % 60

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val totalSeconds = (hourOfDay * 3600) + (minute * 60)
            onDurationSelected(totalSeconds)
        },
        hours,
        minutes,
        true
    ).apply {
        setOnDismissListener { onDismissRequest() }
        show()
    }
}