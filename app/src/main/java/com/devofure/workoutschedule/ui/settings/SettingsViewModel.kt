// SettingsViewModel.kt
package com.devofure.workoutschedule.ui.settings

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.DayOfWeek
import com.devofure.workoutschedule.data.FirstDayOfWeek
import com.devofure.workoutschedule.data.ReminderTime
import com.devofure.workoutschedule.data.SettingsDataStoreManager
import com.devofure.workoutschedule.receiver.ReminderReceiver
import com.devofure.workoutschedule.ui.theme.Colors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ThemeType {
    LIGHT, DARK, SYSTEM
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = SettingsDataStoreManager(application.applicationContext)

    private val _theme = MutableStateFlow(ThemeType.SYSTEM)
    val theme: StateFlow<ThemeType> = _theme

    private val _primaryColor = MutableStateFlow(Colors.DefaultThemeColor)
    val primaryColor: StateFlow<Color> = _primaryColor

    private val _reminderTime = MutableStateFlow(ReminderTime(0, 0))
    val reminderTime: StateFlow<ReminderTime> = _reminderTime

    private val _firstDayOfWeek = MutableStateFlow(FirstDayOfWeek.MONDAY)
    val firstDayOfWeek: StateFlow<FirstDayOfWeek> = _firstDayOfWeek.asStateFlow()

    private val _dayNamingPreference = MutableStateFlow(DayOfWeek.DayNamingPreference.DAY_NUMBERS)
    val dayNamingPreference: StateFlow<DayOfWeek.DayNamingPreference> =
        _dayNamingPreference.asStateFlow()

    init {
        viewModelScope.launch {
            _reminderTime.value = ReminderTime(
                dataStoreManager.reminderHour.first(),
                dataStoreManager.reminderMinute.first()
            )

            _theme.value = ThemeType.valueOf(dataStoreManager.theme.first())

            _primaryColor.value = Color(dataStoreManager.primaryColor.first())

            _firstDayOfWeek.value = FirstDayOfWeek.valueOf(dataStoreManager.firstDayOfWeek.first())

            _dayNamingPreference.value = DayOfWeek
                .DayNamingPreference
                .valueOf(dataStoreManager.dayNamingPreference.first())
        }
    }

    fun setReminder(reminderTime: ReminderTime) {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.fromParts("package", context.packageName, null)
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
            PendingIntent.FLAG_IMMUTABLE
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

        viewModelScope.launch {
            dataStoreManager.setReminderHour(reminderTime.hour)
            dataStoreManager.setReminderMinute(reminderTime.minute)
            _reminderTime.update { reminderTime }
        }
    }

    fun deleteAllWorkouts() {
        // Implementation for deleting all workouts
    }

    fun setTheme(themeType: ThemeType) {
        _theme.value = themeType
        viewModelScope.launch {
            dataStoreManager.setTheme(themeType.name)
        }
    }

    fun setPrimaryColor(color: Color) {
        _primaryColor.value = color
        viewModelScope.launch {
            dataStoreManager.setPrimaryColor(color.toArgb())
        }
    }

    fun setFirstDayOfWeek(firstDay: FirstDayOfWeek) {
        _firstDayOfWeek.value = firstDay
        viewModelScope.launch {
            dataStoreManager.setFirstDayOfWeek(firstDay.name)
        }
    }

    fun setDayNamingPreference(preference: DayOfWeek.DayNamingPreference) {
        _dayNamingPreference.value = preference
        viewModelScope.launch {
            dataStoreManager.setDayNamingPreference(preference.name)
        }
    }
}
