package com.devofure.workoutschedule.ui.settings

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.devofure.workoutschedule.receiver.ReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

enum class ThemeType {
    LIGHT, DARK, SYSTEM
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
    private val _theme = MutableStateFlow(ThemeType.SYSTEM)
    val theme: StateFlow<ThemeType> = _theme

    private val _reminderTime =
        MutableStateFlow(sharedPreferences.getString("reminderTime", "") ?: "")
    val reminderTime: StateFlow<String> = _reminderTime

    init {
        val savedTheme = sharedPreferences.getString("theme", ThemeType.SYSTEM.name)
        _theme.value = ThemeType.valueOf(savedTheme!!)
    }

    fun setReminder(reminderTime: String) {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request permission from the user to schedule exact alarms
                val intent = Intent(
                    android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    android.net.Uri.fromParts("package", context.packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE
        )

        // Split the time into components
        val timeParts = reminderTime.split(" ")
        val time = timeParts[0]
        val period = timeParts[1]

        val hourMinuteParts = time.split(":")
        val hour = hourMinuteParts[0].toInt()
        val minute = hourMinuteParts[1].toInt()

        // Adjust hour for AM/PM
        val adjustedHour = if (period.equals("PM", ignoreCase = true) && hour != 12) {
            hour + 12
        } else if (period.equals("AM", ignoreCase = true) && hour == 12) {
            0
        } else {
            hour
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, adjustedHour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        sharedPreferences.edit().putString("reminderTime", reminderTime).apply()
        _reminderTime.update { reminderTime }
    }

    fun deleteAllWorkouts() {
        sharedPreferences.edit().remove("userSchedule").apply()
    }

    fun setTheme(themeType: ThemeType) {
        _theme.value = themeType
        sharedPreferences.edit().putString("theme", themeType.name).apply()
    }
}
