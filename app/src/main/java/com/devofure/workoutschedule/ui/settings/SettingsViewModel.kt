// SettingsViewModel.kt
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ThemeType {
    LIGHT, DARK, SYSTEM
}

data class ReminderTime(
    val hour: Int,
    val minute: Int
) {
    fun format(): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("WorkoutApp", Context.MODE_PRIVATE)
    private val _theme = MutableStateFlow(ThemeType.SYSTEM)
    val theme: StateFlow<ThemeType> = _theme

    private val _reminderTime =
        MutableStateFlow(ReminderTime(0, 0))
    val reminderTime: StateFlow<ReminderTime> = _reminderTime

    init {
        val savedHour = sharedPreferences.getInt("reminderHour", 0)
        val savedMinute = sharedPreferences.getInt("reminderMinute", 0)
        _reminderTime.value = ReminderTime(savedHour, savedMinute)

        val savedTheme = sharedPreferences.getString("theme", ThemeType.SYSTEM.name)
        _theme.value = ThemeType.valueOf(savedTheme!!)
    }

    fun setReminder(reminderTime: ReminderTime) {
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

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminderTime.hour)
            set(Calendar.MINUTE, reminderTime.minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        sharedPreferences.edit().putInt("reminderHour", reminderTime.hour)
            .putInt("reminderMinute", reminderTime.minute).apply()
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
